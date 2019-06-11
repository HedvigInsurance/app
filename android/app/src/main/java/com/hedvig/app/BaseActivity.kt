package com.hedvig.app

import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
