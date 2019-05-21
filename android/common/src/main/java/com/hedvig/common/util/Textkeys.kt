package com.hedvig.common.util

fun interpolateTextKey(text: String, vararg replacements: Pair<String, String?>): String =
    replacements
        .toList()
        .fold(text) { acc, (key, value) ->
            acc.replace("{$key}", value ?: "")
        }
