package com.hedvig.common.util

import android.support.annotation.StringRes
import com.hedvig.common.R

object Regexes {
    val emailRegex = Regex("^\\S+@\\S+\$")
    val phoneNumberRegex = Regex("([+]*[0-9]+[+. -]*)")
}

fun validateEmail(email: CharSequence): ValidationResult =
    if (!Regexes.emailRegex.matches(email)) {
        ValidationResult(false, R.string.PROFILE_MY_INFO_INVALID_EMAIL)
    } else {
        ValidationResult(true, null)
    }

fun validatePhoneNumber(phoneNumber: CharSequence): ValidationResult =
    if (!Regexes.phoneNumberRegex.matches(phoneNumber)) {
        ValidationResult(false, R.string.PROFILE_MY_INFO_INVALID_PHONE_NUMBER)
    } else {
        ValidationResult(true, null)
    }

data class ValidationResult(val isSuccessful: Boolean, @StringRes val errorTextKey: Int?)
