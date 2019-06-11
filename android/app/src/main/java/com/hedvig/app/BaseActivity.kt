package com.hedvig.app

import android.support.v7.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import io.reactivex.disposables.CompositeDisposable
import android.content.Context
import android.os.Bundle

abstract class BaseActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

}
