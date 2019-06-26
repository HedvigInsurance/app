package com.hedvig.app.feature.loggedin.ui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.app.R

abstract class BaseTabFragment : Fragment() {
    val navController by lazy { requireActivity().findNavController(R.id.loggedNavigationHost) }

    @get:LayoutRes
    abstract val layout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layout, container, false)

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            view?.let { v ->
                (v as? NestedScrollView)?.scrollTo(0, 0)
            }
        }
    }
}
