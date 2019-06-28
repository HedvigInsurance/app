package com.hedvig.app.feature.loggedin.ui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
                (v as? RecyclerView)?.let { recyclerView ->
                        recyclerView.scrollToPosition(0)
                        (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
                }
                (v as? CoordinatorLayout)?.let { coordinatorLayout ->
                    (coordinatorLayout.getChildAt(0) as? NestedScrollView)?.let {
                        it.scrollTo(0, 0)
                    }
                    (coordinatorLayout.getChildAt(0) as? RecyclerView)?.let { recyclerView ->
                        recyclerView.scrollToPosition(0)
                        (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
                    }
                }
            }
        }
    }
}
