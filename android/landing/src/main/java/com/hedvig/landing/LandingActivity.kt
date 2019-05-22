package com.hedvig.landing

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.findNavController
import com.hedvig.app.BaseActivity
import com.hedvig.app.service.LoginStatusService
import org.koin.android.ext.android.inject

// This activity is registered in the app module
@SuppressLint("Registered")
class LandingActivity: BaseActivity() {

    val loggedInService: LoginStatusService by inject()
    val navController by lazy { findNavController(R.id.landingNavigationHost) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_navigation_host)

        injectFeature()
    }
}
