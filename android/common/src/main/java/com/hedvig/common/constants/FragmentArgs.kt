package com.hedvig.common.constants

object FragmentArgs {
    const val ARGS_SHOW_CLOSE = "show_close"

    enum class MarketingResult {
        ONBOARD,
        LOGIN;

        override fun toString(): String {
            return when (this) {
                ONBOARD -> "onboard"
                LOGIN -> "login"
            }
        }
    }

}
