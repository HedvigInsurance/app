package com.hedvig.logged_in.profile.ui.referral

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.compatDrawable
import com.hedvig.logged_in.R
import kotlinx.android.synthetic.main.referral_invite_row.view.*

class InvitesAdapter(
    private val invites: List<MockReferral>
) : RecyclerView.Adapter<InvitesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.referral_invite_row, parent, false)
    )

    override fun getItemCount() = invites.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val invite = invites[position]
            name.text = invite.name

            when (invite.status) {
                MockReferralStatus.ACTIVE -> {
                    setupAvatarWithLetter(this, invite.name)

                    name.text = invite.name
                    statusText.text = "Har skaffat Hedvig"

                    statusIconContainer.setBackgroundResource(R.drawable.background_rounded_corners)
                    statusIconContainer.background.setTint(
                        statusIconContainer.context.compatColor(
                            R.color.light_gray
                        )
                    )
                    discount.text = "-10 kr" // TODO Textkey copy
                    statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_filled_checkmark))
                }
                MockReferralStatus.INITIATED -> {
                    setupAvatarWithLetter(this, invite.name)

                    name.text = invite.name
                    statusText.text = "Har påbörjat registrering"

                    statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_clock))
                }
                MockReferralStatus.NOT_INITIATED -> {
                    avatar.setImageDrawable(avatar.context.compatDrawable(R.drawable.ic_ghost))
                    name.text = "Spöke" // TODO Textkey copy
                    statusText.text = "Någon har öppnat din länk" // TODO Textkey copy
                    statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_clock))
                }
                MockReferralStatus.TERMINATED -> {
                    setupAvatarWithLetter(this, invite.name)
                    name.text = invite.name
                    statusText.text = "Har lämnat Hedvig" // TODO Textkey copy
                    statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_cross))
                }
            }
        }
    }

    private fun setupAvatarWithLetter(viewHolder: ViewHolder, name: String?) {
        viewHolder.apply {
            avatar.setImageDrawable(avatar.context.compatDrawable(R.drawable.sphere))
            avatar.drawable.setTint(avatar.context.compatColor(R.color.purple))
            name?.let { avatarLetter.text = it[0].toString().capitalize() }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.avatar
        val avatarLetter: TextView = view.avatarLetter
        val name: TextView = view.name
        val statusText: TextView = view.statusText
        val statusIconContainer: LinearLayout = view.statusIconContainer
        val discount: TextView = view.discount
        val statusIcon: ImageView = view.statusIcon
    }
}
