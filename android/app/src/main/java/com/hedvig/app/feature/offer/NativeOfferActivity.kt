package com.hedvig.app.feature.offer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.OfferQuery
import com.hedvig.app.R
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.interpolateTextKey
import com.hedvig.app.util.isStudentInsurance
import kotlinx.android.synthetic.main.activity_offer.*
import kotlinx.android.synthetic.main.loading_spinner.*
import kotlinx.android.synthetic.main.offer_peril_section.view.*
import kotlinx.android.synthetic.main.offer_section_terms.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class NativeOfferActivity : AppCompatActivity() {

    private val offerViewModel: OfferViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        homeSection.paragraph.text = getString(R.string.OFFER_APARTMENT_PROTECTION_DESCRIPTION)
        stuffSection.title.text = getString(R.string.OFFER_STUFF_PROTECTION_TITLE)

        meSection.title.text = getString(R.string.OFFER_PERSONAL_PROTECTION_TITLE)
        meSection.paragraph.text = getString(R.string.OFFER_PERSONAL_PROTECTION_DESCRIPTION)

        offerViewModel.data.observe(lifecycleOwner = this) { data ->
            data?.let { d ->
                loadingSpinner.remove()
                container.show()
                bindHomeSection(d)
                bindStuffSection(d)
                bindTerms(d)
            }
        }
    }

    private fun bindHomeSection(data: OfferQuery.Data) {
        homeSection.title.text = data.insurance.address
    }

    private fun bindStuffSection(data: OfferQuery.Data) {
        data.insurance.type?.let { insuranceType ->
            stuffSection.paragraph.text = interpolateTextKey(
                getString(R.string.OFFER_STUFF_PROTECTION_DESCRIPTION),
                "protectionAmount" to if (isStudentInsurance(insuranceType)) {
                    "25 000 kr"
                } else {
                    "50 000 kr"
                }
            )
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
    }
}
