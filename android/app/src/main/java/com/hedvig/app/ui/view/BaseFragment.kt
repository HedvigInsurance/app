package com.hedvig.app.ui.view

import android.support.v4.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.hedvig.app.R

abstract class BaseFragment: Fragment() {

    val loadingSpinner
        get() = view?.findViewById<LottieAnimationView>(R.id.loadingSpinner)
}
