package com.hedvig.common.util.extensions

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

inline fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, crossinline action: (t: T?) -> Unit) =
    observe(lifecycleOwner, Observer { action(it) })
