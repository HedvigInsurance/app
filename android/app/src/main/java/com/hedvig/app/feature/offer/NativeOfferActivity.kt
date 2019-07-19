package com.hedvig.app.feature.offer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.OfferQuery
import com.hedvig.app.R
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import kotlinx.android.synthetic.main.activity_offer.*
import kotlinx.android.synthetic.main.loading_spinner.*
import kotlinx.android.synthetic.main.offer_peril_section.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class NativeOfferActivity : AppCompatActivity() {

    private val offerViewModel: OfferViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        homeSection.paragraph.text = R.string.OFFER_APARTMENT_PROTECTION_DESCRIPTION

        offerViewModel.data.observe(lifecycleOwner = this) { data ->
            data?.let { d ->
                loadingSpinner.remove()
                container.show()
                bindHomeSection(d)
            }
        }
    }

    private fun bindHomeSection(data: OfferQuery.Data) {
        homeSection.title.text = data.insurance.address
    }
}

