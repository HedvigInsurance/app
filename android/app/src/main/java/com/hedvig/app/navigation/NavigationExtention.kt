package com.hedvig.app.navigation

import android.content.Intent
import com.hedvig.app.BuildConfig

fun intentTo(className: String): Intent =
    Intent().setClassName(BuildConfig.APPLICATION_ID, className)
