package com.hedvig.app.authenticate

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.app.R
import com.hedvig.app.feature.chat.UserViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AuthenticateDialog: DialogFragment() {

    private val userViewModel: UserViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_authenticate, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            view.elevation = 2f

        userViewModel.fetchBankIdStartToken()
    }
}
