package com.hedvig.app.feature.referrals.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.app.R
import com.hedvig.app.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.fragment_referral_completed.*

class NotificationCompletedFragment : Fragment() {

    val navController by lazy { requireActivity().findNavController(R.id.rootNavigationHost) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_referral_completed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inviteMore.setHapticClickListener {
            navController
        }

        close.setHapticClickListener {
            navController.popBackStack()
        }
    }
}
