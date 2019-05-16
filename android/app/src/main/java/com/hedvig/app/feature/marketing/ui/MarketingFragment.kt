package com.hedvig.app.feature.marketing.ui

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.app.R
import com.hedvig.app.feature.marketing.service.MarketingTracker
import com.hedvig.app.util.OnSwipeListener
import com.hedvig.app.util.SimpleOnSwipeListener
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.compatSetTint
import com.hedvig.app.util.extensions.doOnEnd
import com.hedvig.app.util.extensions.hideStatusBar
import com.hedvig.app.util.extensions.proxyNavigate
import com.hedvig.app.util.extensions.setDarkNavigationBar
import com.hedvig.app.util.extensions.setLightNavigationBar
import com.hedvig.app.util.extensions.showStatusBar
import com.hedvig.app.util.extensions.view.doOnLayout
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.percentageFade
import kotlinx.android.synthetic.main.fragment_marketing.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class MarketingFragment : Fragment() {

    enum class MarketingResult {
        ONBOARD,
        LOGIN;

        override fun toString(): String {
            return when (this) {
                ONBOARD -> "onboard"
                LOGIN -> "login"
            }
        }
    }

    val tracker: MarketingTracker by inject()

    val marketingStoriesViewModel: MarketingStoriesViewModel by sharedViewModel()

    private var buttonsAnimator: ValueAnimator? = null
    private var blurDismissAnimator: ValueAnimator? = null
    private var topHideAnimation: ValueAnimator? = null

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.rootNavigationHost)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_marketing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupSystemDecoration()
        observeMarketingStories()
    }

    override fun onStop() {
        super.onStop()
        buttonsAnimator?.removeAllListeners()
        buttonsAnimator?.cancel()
        blurDismissAnimator?.removeAllListeners()
        blurDismissAnimator?.cancel()
        topHideAnimation?.removeAllListeners()
        topHideAnimation?.cancel()
    }

    override fun onDestroyView() {
        cleanupSystemDecoration()
        super.onDestroyView()
    }

    private fun observeMarketingStories() {
        marketingStoriesViewModel
            .marketingStories
            .observe(this, Observer {
                loadingSpinner.remove()
                setupButtons()
                setupPager(it)
                setupBlurOverlay()
            })
    }

    // TODO: Refactor this function to be smaller, to be more safe (do not throw exceptions), and to
    // cancel its animations when this fragment is completed, or else it will do bad stuff
    private fun setupPager(stories: List<MarketingStoriesQuery.MarketingStory>?) {
        // FIXME Handle the zero stories case (wat do?)
        val nStories = stories?.size ?: return
        pager.adapter = StoryPagerAdapter(
            childFragmentManager,
            nStories
        )
        pager.show()
        pager.doOnLayout {
            marketingStoriesViewModel.startFirstStory()
        }
        storyProgressIndicatorContainer.show()
        val width = activity_marketing.width
        for (n in 0 until nStories) {
            val progressBar = layoutInflater.inflate(
                R.layout.marketing_progress_bar,
                storyProgressIndicatorContainer,
                false
            ) as ProgressBar
            progressBar.layoutParams = ViewGroup.LayoutParams(width / nStories, 10)
            storyProgressIndicatorContainer.addView(progressBar)
            val animator = ValueAnimator.ofFloat(0f, 100f).apply {
                duration = (stories[n].duration()?.toLong() ?: 3) * 1000
                addUpdateListener { va ->
                    progressBar.progress = (va.animatedValue as Float).toInt()
                }
            }

            marketingStoriesViewModel.page.observe(this, Observer { newPage ->
                animator.removeAllListeners()
                animator.cancel()
                newPage?.let {
                    when {
                        newPage == n -> {
                            animator.doOnEnd {
                                marketingStoriesViewModel.nextScreen()
                            }
                            animator.start()
                        }
                        newPage < n -> progressBar.progress = 0
                        newPage > n -> progressBar.progress = 100
                    }
                }
            })

            marketingStoriesViewModel.paused.observe(this, Observer { shouldPause ->
                shouldPause?.let {
                    if (shouldPause) {
                        animator.pause()
                    } else {
                        animator.resume()
                    }
                }
            })
        }
        marketingStoriesViewModel.page.observe(this, Observer { newPage ->
            if (newPage == null) {
                return@Observer
            }
            tracker.viewedStory(newPage)
            try {
                pager.currentItem = newPage
            } catch (e: IllegalStateException) {
                Timber.e(e)
            }
        })
    }

    private fun setupBlurOverlay() {
        marketingStoriesViewModel.blurred.observe(this, Observer { blurred ->
            if (blurred == null || !blurred) {
                blurOverlay.remove()
                return@Observer
            }

            blurOverlay.show()
            topHideAnimation = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = BLUR_ANIMATION_SHOW_DURATION
                addUpdateListener { opacity ->
                    marketing_hedvig_logo.alpha = opacity.animatedValue as Float
                    storyProgressIndicatorContainer.alpha = opacity.animatedValue as Float

                    val backgroundColor = percentageFade(
                        requireContext().compatColor(R.color.transparent_white),
                        requireContext().compatColor(R.color.blur_white),
                        opacity.animatedFraction
                    )
                    blurOverlay.setBackgroundColor(backgroundColor)
                }
                doOnEnd {
                    marketing_hedvig_logo.remove()
                    storyProgressIndicatorContainer.remove()
                }
                start()
            }

            val swipeListener = GestureDetector(context, SimpleOnSwipeListener { direction ->
                when (direction) {
                    OnSwipeListener.Direction.DOWN -> {
                        tracker.dismissBlurOverlay()
                        blurOverlay.setOnTouchListener(null)
                        hedvigFaceAnimation.remove()
                        sayHello.remove()
                        marketing_hedvig_logo.show()
                        storyProgressIndicatorContainer.show()

                        blurDismissAnimator = ValueAnimator.ofFloat(getHedvig.translationY, 0f).apply {
                            duration =
                                BLUR_ANIMATION_DISMISS_DURATION
                            interpolator = FastOutSlowInInterpolator()
                            addUpdateListener { translation ->
                                getHedvig.translationY = translation.animatedValue as Float
                                val elapsed = translation.animatedFraction
                                val backgroundColor = percentageFade(
                                    requireContext().compatColor(R.color.purple),
                                    requireContext().compatColor(R.color.white),
                                    elapsed
                                )
                                getHedvig.background.compatSetTint(backgroundColor)
                                val textColor = percentageFade(
                                    requireContext().compatColor(R.color.white),
                                    requireContext().compatColor(R.color.black),
                                    elapsed
                                )
                                getHedvig.setTextColor(textColor)

                                marketing_hedvig_logo.alpha = translation.animatedFraction
                                storyProgressIndicatorContainer.alpha = translation.animatedFraction

                                val blurBackgroundColor = percentageFade(
                                    requireContext().compatColor(R.color.blur_white),
                                    requireContext().compatColor(R.color.transparent_white),
                                    translation.animatedFraction
                                )
                                blurOverlay.setBackgroundColor(blurBackgroundColor)
                            }
                            doOnEnd {
                                marketingStoriesViewModel.unblur()
                            }
                            start()
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            })

            val currentTop = getHedvig.top
            val newTop = activity_marketing.height / 2 + getHedvig.height / 2
            val translation = (newTop - currentTop).toFloat()

            buttonsAnimator = ValueAnimator.ofFloat(0f, translation).apply {
                duration =
                    BUTTON_ANIMATION_DURATION
                interpolator = OvershootInterpolator()
                addUpdateListener { translation ->
                    getHedvig.translationY = translation.animatedValue as Float
                    val elapsed = translation.animatedFraction
                    val backgroundColor = percentageFade(
                        requireContext().compatColor(R.color.white),
                        requireContext().compatColor(R.color.purple),
                        elapsed
                    )
                    getHedvig.background.compatSetTint(backgroundColor)
                    val textColor = percentageFade(
                        requireContext().compatColor(R.color.black),
                        requireContext().compatColor(R.color.white),
                        elapsed
                    )
                    getHedvig.setTextColor(textColor)
                }
                doOnEnd {
                    sayHello.translationY = translation
                    sayHello.show()
                    hedvigFaceAnimation.useHardwareAcceleration(true)
                    hedvigFaceAnimation.show()
                    hedvigFaceAnimation.translationY = translation
                    hedvigFaceAnimation.playAnimation()
                    blurOverlay.setOnTouchListener { _, motionEvent ->
                        swipeListener.onTouchEvent(motionEvent)
                        true
                    }
                }
                start()
            }
        })
    }

    private fun setupButtons() {
        login.show()
        getHedvig.show()

        login.setHapticClickListener {
            tracker.loginClick(
                marketingStoriesViewModel.page.value,
                marketingStoriesViewModel.blurred.value
            )
            val args = Bundle()
            args.putString("intent", "login")
            args.putBoolean("show_restart", true)
            navController.proxyNavigate(R.id.action_marketingFragment_to_chatFragment, args)
        }

        getHedvig.setHapticClickListener {
            tracker.getHedvigClick(
                marketingStoriesViewModel.page.value,
                marketingStoriesViewModel.blurred.value
            )
            val args = Bundle()
            args.putString("intent", "onboarding")
            args.putBoolean("show_restart", true)
            navController.proxyNavigate(R.id.action_marketingFragment_to_chatFragment, args)
        }
    }

    private fun setupSystemDecoration() {
        activity?.hideStatusBar()
        activity?.setDarkNavigationBar()
    }

    private fun cleanupSystemDecoration() {
        activity?.showStatusBar()
        activity?.setLightNavigationBar()
    }

    companion object {
        const val BUTTON_ANIMATION_DURATION = 500L
        const val BLUR_ANIMATION_SHOW_DURATION = 300L
        const val BLUR_ANIMATION_DISMISS_DURATION = 200L
    }
}
