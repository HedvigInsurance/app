package com.hedvig.app.feature.chat.native

import android.net.Uri

data class FileUploadOutcome(
    val uri: Uri,
    val wasSuccessFull: Boolean
)
