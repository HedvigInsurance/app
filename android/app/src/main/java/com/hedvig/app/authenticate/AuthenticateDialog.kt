package com.hedvig.app.authenticate

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import com.hedvig.android.owldroid.type.AuthState
import com.hedvig.app.R
import com.hedvig.app.feature.chat.UserViewModel
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import kotlinx.android.synthetic.main.dialog_authenticate.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class AuthenticateDialog : DialogFragment() {

    private val userViewModel: UserViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_authenticate, null)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(view)

        userViewModel.autoStartToken.observe(lifecycleOwner = this) { data ->
            data?.bankIdAuth?.autoStartToken?.let { autoStartToken ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("bankid:///?autostarttoken=$autoStartToken&redirect=null")
                    )
                )
            }
        }
        userViewModel.authStatus.observe(lifecycleOwner = this) { data ->
            Timber.i("data: $data")

            Timber.i("subscribeAuthStatus observe")
            data?.authStatus?.status?.let { bindNewStatus(it) }
        }
        userViewModel.fetchBankIdStartToken()

        return dialog
    }

    private fun bindNewStatus(state: AuthState) = when (state) {
        AuthState.INITIATED -> dialog.apply {
            authTitle.text = getString(R.string.BANK_ID_AUTH_TITLE_INITIATED)
            authDialogLoadingSpinner.show()
            authImage.remove()
        }
        AuthState.IN_PROGRESS ->  dialog.apply {
            authTitle.text = getString(R.string.BANK_ID_LOG_IN_TITLE_IN_PROGRESS)
            authDialogLoadingSpinner.show()
            authImage.remove()
        }
        AuthState.`$UNKNOWN`,
        AuthState.FAILED ->  dialog.apply {
            authTitle.text = getString(R.string.BANK_ID_LOG_IN_TITLE_FAILED)
            authDialogLoadingSpinner.remove()
            authImage.show() // todo set
        }
        AuthState.SUCCESS ->  dialog.apply {
            authTitle.text = getString(R.string.BANK_ID_LOG_IN_TITLE_SUCCESS)
            authDialogLoadingSpinner.remove()
            authImage.show() // todo set
        }
    }

    companion object {
        const val TAG = "AuthenticateDialog"
    }
}
