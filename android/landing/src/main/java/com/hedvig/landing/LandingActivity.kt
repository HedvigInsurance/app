package com.hedvig.landing

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.app.BaseActivity

@SuppressLint("Registered")
class LandingActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_navigation_host)

        injectFeature()
    }
}
