package com.hedvig.app

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlin.reflect.KClass

abstract class BaseActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
