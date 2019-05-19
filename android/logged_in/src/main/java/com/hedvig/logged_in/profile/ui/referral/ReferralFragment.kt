package com.hedvig.logged_in.profile.ui.referral

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.navigation.findNavController
import com.hedvig.logged_in.R
import com.hedvig.logged_in.profile.service.ProfileTracker
import com.hedvig.logged_in.profile.ui.ProfileViewModel
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.compatDrawable
import com.hedvig.common.util.extensions.compatSetTint
import com.hedvig.common.util.extensions.observe
import com.hedvig.common.util.extensions.view.increaseTouchableArea
import com.hedvig.common.util.extensions.view.show
import com.hedvig.common.util.interpolateTextKey
import com.hedvig.logged_in.util.setupLargeTitle
import kotlinx.android.synthetic.main.fragment_referral.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import com.hedvig.app.R as appR

class ReferralFragment : Fragment() {

    val tracker: ProfileTracker by inject()

    val profileViewModel: ProfileViewModel by sharedViewModel()

    private var buttonAnimator: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_referral, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(appR.string.PROFILE_REFERRAL_TITLE, appR.font.circular_bold, appR.drawable.ic_back) {
            requireActivity().findNavController(R.id.loggedInNavigationHost).popBackStack()
        }
        referralButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            requireContext().compatDrawable(appR.drawable.icon_share_white),
            null
        )

        profileViewModel.remoteConfigData.observe(this) { remoteConfigData ->
            remoteConfigData?.let { rcd ->
                val incentive = rcd.referralsIncentiveAmount.toString()

                youGetDescription.text = interpolateTextKey(
                    resources.getString(appR.string.PROFILE_REFERRAL_YOU_GET_DESCRIPTION),
                    "INCENTIVE" to incentive
                )
                theyGetDescription.text = interpolateTextKey(
                    resources.getString(appR.string.PROFILE_REFERRAL_THEY_GET_DESCRIPTION),
                    "INCENTIVE" to incentive
                )

                referralButton.background.compatSetTint(requireContext().compatColor(appR.color.purple))

                termsLink.increaseTouchableArea(100)
                termsLink.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hedvig.com/invite/terms")))
                }

                profileViewModel.data.observe(this, Observer { data ->
                    data?.member()?.id()?.let { memberId ->
                        profileViewModel.generateReferralLink(memberId)
                        profileViewModel.firebaseLink.observe(this, Observer { link ->
                            referralButton.show()
                            if (referralButton.translationY != 0f) {
                                buttonAnimator = ValueAnimator.ofFloat(75f, 0f).apply {
                                    duration = 500
                                    addUpdateListener { translation ->
                                        referralButton.translationY = translation.animatedValue as Float
                                    }
                                    interpolator = OvershootInterpolator()
                                    start()
                                }
                            }
                            referralButton.setOnClickListener {
                                tracker.clickReferral(profileViewModel.remoteConfigData.value?.referralsIncentiveAmount)
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        interpolateTextKey(
                                            resources.getString(appR.string.PROFILE_REFERRAL_SHARE_TEXT),
                                            "INCENTIVE" to incentive, "LINK" to link.toString()
                                        )
                                    )
                                    type = "text/plain"
                                }
                                val chooser = Intent.createChooser(
                                    shareIntent,
                                    resources.getString(appR.string.PROFILE_REFERRAL_SHARE_TITLE)
                                )
                                startActivity(chooser)
                            }
                        })
                    }
                })
            }
        }
    }

    override fun onStop() {
        super.onStop()
        buttonAnimator?.removeAllListeners()
        buttonAnimator?.cancel()
    }
}
