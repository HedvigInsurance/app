package com.hedvig.app.feature.offer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.hedvig.android.owldroid.fragment.IncentiveFragment
import com.hedvig.android.owldroid.fragment.PerilCategoryFragment
import com.hedvig.android.owldroid.graphql.OfferQuery
import com.hedvig.app.R
import com.hedvig.app.feature.chat.ChatActivity
import com.hedvig.app.feature.dashboard.ui.PerilBottomSheet
import com.hedvig.app.feature.dashboard.ui.PerilIcon
import com.hedvig.app.feature.dashboard.ui.PerilView
import com.hedvig.app.util.*
import com.hedvig.app.util.extensions.*
import com.hedvig.app.util.extensions.view.hide
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.extensions.view.updateMargin
import kotlinx.android.synthetic.main.activity_offer.*
import kotlinx.android.synthetic.main.feature_bubbles.*
import kotlinx.android.synthetic.main.loading_spinner.*
import kotlinx.android.synthetic.main.offer_peril_section.view.*
import kotlinx.android.synthetic.main.offer_section_terms.view.*
import kotlinx.android.synthetic.main.price_bubbles.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.min

class OfferActivity : AppCompatActivity() {

    private val offerViewModel: OfferViewModel by viewModel()

    private val doubleMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin_double) }
    private val perilTotalWidth: Int by lazy { resources.getDimensionPixelSize(R.dimen.peril_width) + (doubleMargin * 2) }
    private val rowWidth: Int by lazy {
        displayMetrics.widthPixels - (doubleMargin * 2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        offerChatButton.setHapticClickListener {
            offerViewModel.triggerOpenChat {
                startClosableChat(true)
            }
        }

        bindStaticData()

        offerViewModel.data.observe(lifecycleOwner = this) { data ->
            data?.let { d ->
                loadingSpinner.remove()
                container.show()
                offerToolbarAddress.text = d.insurance.address
                bindPriceBubbles(d)
                bindFeatureBubbles(d)
                bindDiscountButton(d)
                bindHomeSection(d)
                bindStuffSection(d)
                bindMeSection(d)
                bindTerms(d)
            }
        }
    }

    private fun bindStaticData() {
        homeSection.paragraph.text = getString(R.string.OFFER_APARTMENT_PROTECTION_DESCRIPTION)
        homeSection.hero.setImageDrawable(getDrawable(R.drawable.offer_house))

        stuffSection.hero.setImageDrawable(getDrawable(R.drawable.offer_stuff))
        stuffSection.title.text = getString(R.string.OFFER_STUFF_PROTECTION_TITLE)

        meSection.hero.setImageDrawable(getDrawable(R.drawable.offer_me))
        meSection.title.text = getString(R.string.OFFER_PERSONAL_PROTECTION_TITLE)
        meSection.paragraph.text = getString(R.string.OFFER_PERSONAL_PROTECTION_DESCRIPTION)

        termsSection.privacyPolicy.setHapticClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URL))
        }

        grossPremium.setStrikethrough(true)
    }

    private fun bindDiscountButton(data: OfferQuery.Data) {
        discountButton.text = if (hasActiveCampaign(data)) {
            getString(R.string.OFFER_REMOVE_DISCOUNT_BUTTON)
        } else {
            getString(R.string.OFFER_ADD_DISCOUNT_BUTTON)
        }

        discountButton.setHapticClickListener {
            if (hasActiveCampaign(data)) {
                showAlert(
                    R.string.OFFER_REMOVE_DISCOUNT_ALERT_TITLE,
                    R.string.OFFER_REMOVE_DISCOUNT_ALERT_DESCRIPTION,
                    R.string.OFFER_REMOVE_DISCOUNT_ALERT_REMOVE,
                    R.string.OFFER_REMOVE_DISCOUNT_ALERT_CANCEL,
                    {
                        offerViewModel.removeDiscount()
                    }
                )
            } else {
                OfferRedeemCodeDialog.newInstance().show(supportFragmentManager, OfferRedeemCodeDialog.TAG)
            }
        }
    }

    private fun bindPriceBubbles(data: OfferQuery.Data) {
        grossPremium.hide()
        discountBubble.hide()
        discountTitle.hide()

        netPremium.setTextColor(compatColor(R.color.off_black_dark))
        netPremium.text =
            data.insurance.cost?.fragments?.costFragment?.monthlyNet?.amount?.toBigDecimal()?.toInt()?.toString()
        if (data.redeemedCampaigns.size > 0) {
            when (data.redeemedCampaigns[0].fragments.incentiveFragment.incentive) {
                is IncentiveFragment.AsMonthlyCostDeduction -> {
                    grossPremium.show()
                    grossPremium.text = interpolateTextKey(
                        getString(R.string.OFFER_GROSS_PREMIUM),
                        "GROSS_PREMIUM" to data.insurance.cost?.fragments?.costFragment?.monthlyGross?.amount?.toBigDecimal()?.toInt()
                    )

                    discountBubble.show()
                    discount.text = getString(R.string.OFFER_SCREEN_INVITED_BUBBLE)
                    discount.updateMargin(top = 0)

                    netPremium.setTextColor(compatColor(R.color.pink))
                }
                is IncentiveFragment.AsFreeMonths -> {
                    discountTitle.show()
                    discount.text = interpolateTextKey(
                        getString(R.string.OFFER_SCREEN_FREE_MONTHS_BUBBLE),
                        "free_month" to (data.redeemedCampaigns[0].fragments.incentiveFragment.incentive as IncentiveFragment.AsFreeMonths).quantity
                    )
                    discount.updateMargin(top = resources.getDimensionPixelSize(R.dimen.base_margin_half))
                }
            }
        }
    }

    private fun bindFeatureBubbles(data: OfferQuery.Data) {
        amountInsured.text = interpolateTextKey(
            getString(R.string.OFFER_BUBBLES_INSURED_SUBTITLE),
            "personsInHousehold" to data.insurance.personsInHousehold
        )
        startDate.text = if (data.insurance.insuredAtOtherCompany == true) {
            getString(R.string.OFFER_BUBBLES_START_DATE_SUBTITLE_SWITCHER)
        } else {
            getString(R.string.OFFER_BUBBLES_START_DATE_SUBTITLE_NEW)
        }

        data.insurance.type?.let { t ->
            brfOrTravel.text = if (isApartmentOwner(t)) {
                getString(R.string.OFFER_BUBBLES_OWNED_ADDON_TITLE)
            } else {
                getString(R.string.OFFER_BUBBLES_TRAVEL_PROTECTION_TITLE)
            }
        }
    }

    private fun bindHomeSection(data: OfferQuery.Data) {
        homeSection.title.text = data.insurance.address
        data.insurance.perilCategories?.get(0)?.let { perils ->
            addPerils(homeSection.perilsContainer, perils.fragments.perilCategoryFragment)
        }
    }

    private fun bindStuffSection(data: OfferQuery.Data) {
        data.insurance.type?.let { insuranceType ->
            stuffSection.paragraph.text = interpolateTextKey(
                getString(R.string.OFFER_STUFF_PROTECTION_DESCRIPTION),
                "protectionAmount" to if (isStudentInsurance(insuranceType)) {
                    getString(R.string.STUFF_PROTECTION_AMOUNT_STUDENT)
                } else {
                    getString(R.string.STUFF_PROTECTION_AMOUNT)
                }
            )
        }
        data.insurance.perilCategories?.get(1)?.let { perils ->
            addPerils(stuffSection.perilsContainer, perils.fragments.perilCategoryFragment)
        }
    }

    private fun bindMeSection(data: OfferQuery.Data) {
        data.insurance.perilCategories?.get(2)?.let { perils ->
            addPerils(meSection.perilsContainer, perils.fragments.perilCategoryFragment)
        }
    }

    private fun bindTerms(data: OfferQuery.Data) {
        data.insurance.type?.let { insuranceType ->
            termsSection.maxCompensation.text = interpolateTextKey(
                getString(R.string.OFFER_TERMS_MAX_COMPENSATION),
                "MAX_COMPENSATION" to if (isStudentInsurance(insuranceType)) {
                    "200 000 kr"
                } else {
                    "1 000 000 kr"
                }
            )
            termsSection.deductible.text = interpolateTextKey(
                getString(R.string.OFFER_TERMS_DEDUCTIBLE),
                "DEDUCTIBLE" to "1 500 kr"
            )
        }
        data.insurance.presaleInformationUrl?.let { piu ->
            termsSection.presaleInformation.setHapticClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(piu)))
            }
        }
        data.insurance.policyUrl?.let { pu ->
            termsSection.terms.setHapticClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pu)))
            }
        }
    }

    private fun addPerils(container: LinearLayout, category: PerilCategoryFragment) {
        container.removeAllViews()
        category.perils?.let { perils ->
            val maxPerilsPerRow = rowWidth / perilTotalWidth
            if (perils.size < maxPerilsPerRow) {
                container.orientation = LinearLayout.HORIZONTAL
                perils.forEach { peril ->
                    container.addView(makePeril(peril, category))
                }
            } else {
                container.orientation = LinearLayout.VERTICAL
                for (row in 0 until perils.size step maxPerilsPerRow) {
                    val rowView = LinearLayout(this)
                    val rowPerils = perils.subList(row, min(row + maxPerilsPerRow, perils.size))
                    rowPerils.forEach { peril ->
                        rowView.addView(makePeril(peril, category))
                    }
                    container.addView(rowView)
                }
            }
        }
    }

    private fun makePeril(peril: PerilCategoryFragment.Peril, category: PerilCategoryFragment) = PerilView.build(
        this,
        name = peril.title,
        iconId = peril.id,
        onClick = {
            safeLet(category.title, peril.id, peril.title, peril.description) { name, id, title, description ->
                PerilBottomSheet.newInstance(name, PerilIcon.from(id), title, description)
                    .show(supportFragmentManager, PerilBottomSheet.TAG)
            }
        }
    )

    companion object {
        private val PRIVACY_POLICY_URL =
            Uri.parse("https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/Hedvig+-+integritetspolicy.pdf")

        private fun hasActiveCampaign(data: OfferQuery.Data) = data.redeemedCampaigns.size > 0
    }
}
