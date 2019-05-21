package com.hedvig.common.util.extensions

inline fun <reified E: Enum<E>> byOrdinal(index: Int) = enumValues<E>()[index]
