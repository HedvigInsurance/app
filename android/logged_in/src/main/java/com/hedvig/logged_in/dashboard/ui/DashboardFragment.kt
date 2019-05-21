package com.hedvig.logged_in.dashboard.ui

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hedvig.common.owldroid.DashboardQuery
import com.hedvig.logged_in.R
import com.hedvig.logged_in.dashboard.service.DashboardTracker
import com.hedvig.logged_in.loggedin.BaseTabFragment
import com.hedvig.common.util.extensions.view.animateCollapse
import com.hedvig.common.util.extensions.view.animateExpand
import com.hedvig.common.util.extensions.view.remove
import com.hedvig.common.util.extensions.view.setHapticClickListener
import com.hedvig.common.util.extensions.view.show
import com.hedvig.common.util.interpolateTextKey
import com.hedvig.common.util.isApartmentOwner
import com.hedvig.common.util.isStudentInsurance
import com.hedvig.app.viewmodel.DirectDebitViewModel
import com.hedvig.common.owldroid.type.DirectDebitStatus
import com.hedvig.common.owldroid.type.InsuranceStatus
import com.hedvig.common.owldroid.type.InsuranceType
import com.hedvig.common.util.extensions.*
import com.hedvig.logged_in.util.setupLargeTitle
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.dashboard_footnotes.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import com.hedvig.app.R as appR

class DashboardFragment : BaseTabFragment() {
    val tracker: DashboardTracker by inject()

    val dashboardViewModel: DashboardViewModel by sharedViewModel()
    val directDebitViewModel: DirectDebitViewModel by sharedViewModel()

    private val bottomNavigationHeight: Int by lazy { resources.getDimensionPixelSize(appR.dimen.bottom_navigation_height) }
    private val halfMargin: Int by lazy { resources.getDimensionPixelSize(appR.dimen.base_margin_half) }
    private val doubleMargin: Int by lazy { resources.getDimensionPixelSize(appR.dimen.base_margin_double) }
    private val tripleMargin: Int by lazy { resources.getDimensionPixelSize(appR.dimen.base_margin_triple) }
    private val perilTotalWidth: Int by lazy { resources.getDimensionPixelSize(appR.dimen.peril_width) + (doubleMargin * 2) }
    private val rowWidth: Int by lazy {
        var margin = tripleMargin * 2 // perilCategoryView margin
        margin += halfMargin * 2 // strange padding in perilCategoryView
        margin += doubleMargin * 2 // perilCategoryView ConstraintLayout padding
        requireActivity().displayMetrics.widthPixels - margin
    }

    private var isInsurancePendingExplanationExpanded = false

    private var setActivationFiguresInterval: Disposable? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onResume() {
        super.onResume()
        dashboardViewModel.data.value?.insurance()?.activeFrom()?.let { localDate ->
            setActivationFigures(localDate)
        }
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        dashboardViewModel.data.observe(this) { bindData() }
        directDebitViewModel.data.observe(this) { bindData() }
    }

    private fun bindData() {
        val dashboardData = dashboardViewModel.data.value ?: return
        val directDebitData = directDebitViewModel.data.value ?: return
        loadingSpinner?.remove()
        val title = interpolateTextKey(
            resources.getString(appR.string.DASHBOARD_TITLE),
            "NAME" to dashboardData.member().firstName()
        )
        setupLargeTitle(title, appR.font.circular_bold)
        setupInsuranceStatusStatus(dashboardData.insurance())

        perilCategoryContainer.removeAllViews()

        dashboardData.insurance().perilCategories()?.forEach { category ->
            val categoryView = makePerilCategoryRow(category)
            perilCategoryContainer.addView(categoryView)
        }

        dashboardData.insurance().status().let { insuranceStatus ->
            when (insuranceStatus) {
                InsuranceStatus.ACTIVE -> insuranceActive.show()
                else -> {
                }
            }
        }
        dashboardData.insurance().type()?.let { setupAdditionalInformationRow(it) }

        setupDirectDebitStatus(directDebitData.directDebitStatus())
    }

    private fun makePerilCategoryRow(category: DashboardQuery.PerilCategory): PerilCategoryView {
        val categoryView =
            PerilCategoryView.build(requireContext())

        categoryView.categoryIconId = category.iconUrl()
        categoryView.title = category.title()
        categoryView.subtitle = category.description()
        categoryView.expandedContentContainer.measure(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        categoryView.onAnimateExpand = { handleExpandShowEntireView(categoryView) }
        category.perils()?.let { categoryView.expandedContent = makePerilCategoryExpandContent(it, category) }

        return categoryView
    }

    private fun makePerilCategoryExpandContent(
        perils: List<DashboardQuery.Peril>,
        category: DashboardQuery.PerilCategory
    ): LinearLayout {
        val expandedContent = LinearLayout(requireContext())
        val maxPerilsPerRow = rowWidth / perilTotalWidth
        if (perils.size > maxPerilsPerRow) {
            for (row in 0 until perils.size step maxPerilsPerRow) {
                expandedContent.orientation = LinearLayout.VERTICAL
                val rowView = LinearLayout(requireContext())
                val rowPerils = perils.subList(row, Math.min(row + maxPerilsPerRow, perils.size - 1))
                rowView.addViews(rowPerils.map { makePeril(it, category) })
                expandedContent.addView(rowView)
            }
        } else {
            expandedContent.addViews(perils.map { makePeril(it, category) })
        }


        return expandedContent
    }

    private fun makePeril(peril: DashboardQuery.Peril, subject: DashboardQuery.PerilCategory): PerilView {
        val perilView = PerilView.build(requireContext())

        perilView.perilName = peril.title()
        peril.id()?.let { perilView.perilIconId = it }
        perilView.setHapticClickListener {
            val subjectName = subject.title()
            val id = peril.id()
            val title = peril.title()
            val description = peril.description()

            tracker.perilClick(id)

            if (subjectName != null && id != null && title != null && description != null) {
                PerilBottomSheet.newInstance(
                    subjectName,
                    PerilIcon.from(id),
                    title,
                    description
                ).show(childFragmentManager, "perilSheet")
            }
        }

        return perilView
    }

    private fun setupAdditionalInformationRow(insuranceType: InsuranceType) {
        val additionalInformation = PerilCategoryView.build(
            requireContext(),
            bottomMargin = tripleMargin
        )

        additionalInformation.onAnimateExpand = { handleExpandShowEntireView(additionalInformation) }
        additionalInformation.categoryIcon = requireContext().compatDrawable(appR.drawable.ic_more_info)
        additionalInformation.title = resources.getString(appR.string.DASHBOARD_MORE_INFO_TITLE)
        additionalInformation.subtitle = resources.getString(appR.string.DASHBOARD_MORE_INFO_SUBTITLE)

        val content = layoutInflater.inflate(
            R.layout.dashboard_footnotes,
            additionalInformation.expandedContentContainer,
            false
        )

        content.totalCoverageFootnote.text = interpolateTextKey(
            resources.getString(appR.string.DASHBOARD_INSURANCE_AMOUNT_FOOTNOTE),
            "AMOUNT" to
                if (isStudentInsurance(insuranceType)) resources.getString(appR.string.two_hundred_thousand)
                else resources.getString(appR.string.one_million)
        )
        if (isApartmentOwner(insuranceType)) {
            content.ownerFootnote.show()
        }

        additionalInformation.expandedContent = content

        perilCategoryContainer.addView(additionalInformation)
    }

    private fun setupDirectDebitStatus(directDebitStatus: DirectDebitStatus) {
        when (directDebitStatus) {
            DirectDebitStatus.ACTIVE,
            DirectDebitStatus.PENDING,
            DirectDebitStatus.`$UNKNOWN` -> {
                directDebitNeedsSetup.remove()
            }
            DirectDebitStatus.NEEDS_SETUP -> {
                directDebitNeedsSetup.show()
                directDebitConnectButton.setHapticClickListener {
                    tracker.setupDirectDebit()
                    navController.proxyNavigate(R.id.action_dashboardFragment_to_trustlyFragment)
                }
            }
        }
    }

    private fun setupInsuranceStatusStatus(insurance: DashboardQuery.Insurance) {
        insurancePending.remove()
        insuranceActive.remove()
        when (insurance.status()) {
            InsuranceStatus.ACTIVE -> {
                insuranceActive.show()
            }
            InsuranceStatus.INACTIVE -> {
                insurancePending.show()
                insurancePendingCountDownContainer.remove()
                insurancePendingLoadingAnimation.show()

                insurancePendingLoadingAnimation.playAnimation()
                insurancePendingExplanation.text =
                    getString(appR.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_NO_START_DATE_EXPLANATION)
                setupInsurancePendingMoreInfo()
            }
            InsuranceStatus.INACTIVE_WITH_START_DATE -> {
                insurancePending.show()
                insurancePendingLoadingAnimation.remove()

                insurance.activeFrom()?.let { localDate ->
                    insurancePendingCountDownContainer.show()

                    setActivationFigures(localDate)
                    val formattedString = localDate.format(DateTimeFormatter.ofPattern("d LLLL yyyy"))
                    insurancePendingExplanation.text = interpolateTextKey(
                        getString(appR.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_HAS_START_DATE_EXPLANATION),
                        "START_DATE" to formattedString
                    )
                } ?: Timber.e("InsuranceStatus INACTIVE_WITH_START_DATE but got no start date")

                setupInsurancePendingMoreInfo()
            }
            InsuranceStatus.`$UNKNOWN`,
            InsuranceStatus.PENDING,
            InsuranceStatus.TERMINATED -> {
            }
        }
    }

    private fun setupInsurancePendingMoreInfo() {
        insurancePendingMoreInfo.setOnClickListener {
            if (isInsurancePendingExplanationExpanded) {
                tracker.expandInsurancePendingInfo()
                insurancePendingExplanation.animateCollapse()
                insurancePendingMoreInfo.text =
                    resources.getString(appR.string.DASHBOARD_DIRECT_DEBIT_STATUS_PENDING_BUTTON_LABEL)
            } else {
                tracker.collapseInsurancePendingInfo()
                insurancePendingExplanation.animateExpand()
                insurancePendingMoreInfo.text =
                    resources.getString(appR.string.DASHBOARD_INSURANCE_STATUS_PENDING_BUTTON_CLOSE)
            }
            isInsurancePendingExplanationExpanded = !isInsurancePendingExplanationExpanded
        }
        insurancePendingExplanation.animateCollapse(0, 0)
        isInsurancePendingExplanationExpanded = false
    }

    private fun setActivationFigures(startDate: LocalDate) {
        val period = LocalDate.now().until(startDate)

        insurancePendingCountdownMonths.text = period.months.toString()
        insurancePendingCountdownDays.text = period.days.toString()

        // insurances is started at mid night
        val midnight = GregorianCalendar().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val millisToMidnight = midnight.timeInMillis - System.currentTimeMillis()
        val hours = ((millisToMidnight / (1000 * 60 * 60)) % 24)
        val minutes = ((millisToMidnight / (1000 * 60)) % 60)

        insurancePendingCountdownHours.text = hours.toString()
        insurancePendingCountdownMinutes.text = minutes.toString()

        // dispose interval if one all ready exists
        setActivationFiguresInterval?.dispose()
        // start interval
        val disposable = Flowable.interval(30, TimeUnit.SECONDS, Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                { setActivationFigures(startDate) }, { Timber.e(it) }
            )
        compositeDisposable += disposable
        setActivationFiguresInterval = disposable
    }

    private fun handleExpandShowEntireView(view: View) {
        val bottomBreakPoint =
            Resources.getSystem().displayMetrics.heightPixels - (bottomNavigationHeight + doubleMargin)
        val position = intArrayOf(0, 0)
        view.getLocationOnScreen(position)
        val viewBottomPos = position[1] + view.measuredHeight

        if (viewBottomPos > bottomBreakPoint) {
            appBarLayout.setExpanded(false, true)
            val d = viewBottomPos - bottomBreakPoint
            dashboardNestedScrollView.scrollY += d
        }
    }
}
