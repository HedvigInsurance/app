package com.hedvig.logged_in.profile.ui.myhome

import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.logged_in.R
import com.hedvig.logged_in.profile.ui.ProfileViewModel
import com.hedvig.common.constants.FragmentArgs
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.common.util.whenApiVersion
import kotlinx.android.synthetic.main.dialog_change_home_info.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import com.hedvig.app.R as appR

class ChangeHomeInfoDialog : DialogFragment() {
    val profileViewModel: ProfileViewModel by sharedViewModel()
    val navController by lazy { requireActivity().findNavController(R.id.loggedInNavigationHost) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.dialog_change_home_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whenApiVersion(Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = 2f
        }

        dialogCancel.setOnClickListener {
            dismiss()
        }

        dialogConfirm.setOnClickListener {
            profileViewModel.triggerFreeTextChat {
                dismiss()
                navController.proxyNavigate(
                    R.id.action_myHomeFragment_to_chatFragment,
                    Bundle().apply {
                        putBoolean(FragmentArgs.ARGS_SHOW_CLOSE, true)
                    }
                )
            }
        }
    }
}
