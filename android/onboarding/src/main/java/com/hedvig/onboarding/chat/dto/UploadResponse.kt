package com.hedvig.onboarding.chat.dto

data class UploadResponse(
    val data: UploadData?,
    val errors: List<UploadError>?
)
