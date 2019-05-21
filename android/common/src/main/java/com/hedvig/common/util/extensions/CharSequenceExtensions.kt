package com.hedvig.common.util.extensions

import android.text.TextUtils

fun CharSequence.concat(vararg with: CharSequence): CharSequence = TextUtils.concat(this, *with)
