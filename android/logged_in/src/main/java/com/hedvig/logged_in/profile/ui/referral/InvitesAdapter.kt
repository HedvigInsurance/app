package com.hedvig.logged_in.profile.ui.referral

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.logged_in.R

class InvitesAdapter : RecyclerView.Adapter<InvitesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.referral_invite_row, parent, false)
    )

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
