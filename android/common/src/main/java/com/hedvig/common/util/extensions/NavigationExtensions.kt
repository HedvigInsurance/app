package com.hedvig.common.util.extensions

import android.os.Bundle
import android.support.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import timber.log.Timber


fun NavController.proxyNavigate(@IdRes id: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
    try {
        navigate(id, args, navOptions)
    } catch (exception: IllegalArgumentException) {
        Timber.e(exception, "Error when navigating from ${currentDestination?.label}")
    }
}
