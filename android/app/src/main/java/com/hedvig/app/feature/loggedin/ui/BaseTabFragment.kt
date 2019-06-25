package com.hedvig.app.feature.loggedin.ui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.app.R
import com.hedvig.app.feature.claims.ui.ClaimsViewModel
import com.hedvig.app.util.extensions.startClosableChat
import com.hedvig.app.util.extensions.view.updatePadding
import kotlinx.android.synthetic.main.app_bar.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseTabFragment : Fragment() {

    private val baseTabViewModel: ClaimsViewModel by sharedViewModel()

    val navController by lazy { requireActivity().findNavController(R.id.loggedNavigationHost) }

    @get:LayoutRes
    abstract val layout: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.updatePadding(end = resources.getDimensionPixelSize(R.dimen.base_margin_double))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.base_tab_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        baseTabViewModel.triggerFreeTextChat {
            requireActivity().startClosableChat()
        }
        return true
    }
}
