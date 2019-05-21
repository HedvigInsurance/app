package com.hedvig.onboarding.util

import android.app.Dialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import com.hedvig.onboarding.R
import com.hedvig.app.R as appR


fun Context.showRestartDialog(restart: () ->  Unit){
    val dialog = Dialog(this)
    val resetDialogView = LayoutInflater.from(this).inflate(R.layout.reset_onboarding_dialog, null)
    dialog.window!!.setBackgroundDrawable(
        ContextCompat.getDrawable(
            this,
            appR.drawable.dialog_background
        )
    )
    dialog.setContentView(resetDialogView)

    resetDialogView.findViewById<View>(R.id.chatResetDialogNegativeButton)
        .setOnClickListener { dialog.dismiss() }
    resetDialogView.findViewById<View>(R.id.chatResetDialogPositiveButton).setOnClickListener {
        dialog.dismiss()
        restart()
    }

    dialog.show()
}
