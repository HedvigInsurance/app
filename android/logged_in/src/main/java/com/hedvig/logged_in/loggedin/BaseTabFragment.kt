package com.hedvig.logged_in.loggedin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import com.hedvig.app.ui.view.BaseFragment
import com.hedvig.logged_in.R
import com.hedvig.logged_in.claims.ui.ClaimsViewModel
import com.hedvig.common.constants.FragmentArgs
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.common.util.extensions.view.updatePadding
import com.hedvig.navigation.features.OnboardingNavigation
import kotlinx.android.synthetic.main.app_bar.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import com.hedvig.app.R as appR

abstract class BaseTabFragment : BaseFragment() {

    val baseTabViewModel: ClaimsViewModel by sharedViewModel()

    val navController by lazy { requireActivity().findNavController(R.id.loggedInNavigationHost) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.updatePadding(end = resources.getDimensionPixelSize(appR.dimen.base_margin_double))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(appR.menu.base_tab_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        baseTabViewModel.triggerFreeTextChat {
            startActivity(OnboardingNavigation.getIntent(requireContext()))
        }
        return true
    }
}
