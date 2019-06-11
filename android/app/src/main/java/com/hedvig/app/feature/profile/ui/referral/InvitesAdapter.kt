package com.hedvig.app.feature.profile.ui.referral

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.app.R
import com.hedvig.app.util.LightClass
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.compatDrawable
import com.hedvig.app.util.getLightness
import com.hedvig.app.util.hashColor
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.referral_header.view.*
import kotlinx.android.synthetic.main.referral_invite_row.view.*
import kotlin.math.ceil
import kotlin.math.min

class InvitesAdapter(
    private val monthlyCost: Int,
    private val data: ProfileQuery.MemberReferralCampaign
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder = when (position) {
        HEADER -> {
            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.referral_header, parent, false)
            )
        }
        else -> ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.referral_invite_row, parent, false)
        )
    }

    override fun getItemViewType(position: Int) = if (position == 0) {
        HEADER
    } else {
        ITEM
    }

    override fun getItemCount(): Int {
        var count = 1 //start of with header
        count += data.receivers?.size ?: 0
        data.sender?.let { count += 1 }

        return count
    }

    private fun calculateDiscount(): Int {
        var totalDiscount = 0
        (data.sender as? ProfileQuery.AsActiveReferral?)?.let { totalDiscount += it.discount.number.intValueExact() }
        data.receivers?.filterIsInstance(ProfileQuery.AsActiveReferral1::class.java)?.forEach { receiver -> totalDiscount += receiver.discount.number.intValueExact() }
        return min(totalDiscount, monthlyCost)
    }

    private fun calculateInvitesLeftToFree(): Int {
        val amount = monthlyCost - calculateDiscount()
        return ceil(amount / data.referralInformation.incentive.number.doubleValueExact()).toInt()
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            HEADER -> (viewHolder as? HeaderViewHolder)?.apply {
                progressTankView.initialize(monthlyCost, calculateDiscount(), data.referralInformation.incentive.number.intValueExact())
                code.text = data.referralInformation.code
                subtitle.text = interpolateTextKey(
                    subtitle.resources.getString(R.string.REFERRAL_PROGRESS_HEADLINE),
                    "NUMBER_OF_FRIENDS_LEFT" to calculateInvitesLeftToFree().toString()
                )
                explainer.text = interpolateTextKey(
                    explainer.resources.getString(R.string.REFERRAL_PROGRESS_BODY),
                    "REFERRAL_VALUE" to data.referralInformation.incentive.number.intValueExact().toString()
                )
            }
            ITEM -> (viewHolder as? ItemViewHolder)?.apply {
                when (val referral = getReferralFromPosition(position)) {
                    is ProfileQuery.AsActiveReferral -> bindActiveRow(this, referral.name, referral.discount.number?.doubleValueExact().toString())
                    is ProfileQuery.AsActiveReferral1 -> bindActiveRow(this, referral.name, referral.discount.number?.doubleValueExact().toString())
                    is ProfileQuery.AsInProgressReferral -> bindInProgress(this, referral.name)
                    is ProfileQuery.AsInProgressReferral1 -> bindInProgress(this, referral.name)
                    is ProfileQuery.AsNotInitiatedReferral,
                    is ProfileQuery.AsNotInitiatedReferral1 -> bindNotInitiated(this)
                    is ProfileQuery.AsTerminatedReferral -> bindTerminated(this, referral.name)
                    is ProfileQuery.AsTerminatedReferral1 -> bindTerminated(this, referral.name)
                }
            }
        }
    }

    private fun getReferralFromPosition(position: Int): Any? =
        try {
            data.receivers?.get(position - 1)
        } catch (e: IndexOutOfBoundsException) {
            null
        } ?: data.sender

    private fun bindActiveRow(viewHolder: ItemViewHolder, nameString: String?, discountString: String?) = viewHolder.apply {
        setupAvatarWithLetter(this, nameString)

        name.text = nameString
        statusText.text = statusText.resources.getString(R.string.REFERRAL_INVITE_NEWSTATE)

        statusIconContainer.setBackgroundResource(R.drawable.background_rounded_corners)
        statusIconContainer.background.setTint(
            statusIconContainer.context.compatColor(
                R.color.light_gray
            )
        )
        discount.text = interpolateTextKey(
            discount.resources.getString(R.string.REFERRAL_INVITE_ACTIVE_VALUE),
            "REFERRAL_VALUE" to discountString
        )
        statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_filled_checkmark))
    }

    private fun bindInProgress(viewHolder: ItemViewHolder, nameString: String?) = viewHolder.apply {
        setupAvatarWithLetter(this, nameString)

        name.text = nameString
        statusText.text = statusText.resources.getString(R.string.REFERRAL_INVITE_STARTEDSTATE)

        statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_clock))
    }

    private fun bindNotInitiated(viewHolder: ItemViewHolder) = viewHolder.apply {
        avatar.setImageDrawable(avatar.context.compatDrawable(R.drawable.ic_ghost))
        avatar.scaleType = ImageView.ScaleType.CENTER
        name.text = name.resources.getString(R.string.REFERRAL_INVITE_ANON)
        statusText.text = statusText.resources.getString(R.string.REFERRAL_INVITE_OPENEDSTATE)
        statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_clock))
    }

    private fun bindTerminated(viewHolder: ItemViewHolder, nameString: String?) = viewHolder.apply {
        setupAvatarWithLetter(this, nameString)
        name.text = nameString
        statusText.text = statusText.resources.getString(R.string.REFERRAL_INVITE_QUITSTATE)
        statusIcon.setImageDrawable(statusIcon.context.compatDrawable(R.drawable.ic_cross))
    }

    private fun setupAvatarWithLetter(viewHolder: ItemViewHolder, name: String?) {
        viewHolder.apply {
            name?.let { n ->
                avatar.setImageDrawable(avatar.context.compatDrawable(R.drawable.sphere))
                val hashedColor = avatar.context.compatColor(hashColor(n))
                avatar.drawable.mutate().setTint(hashedColor)
                avatarLetter.text = n[0].toString().capitalize()
                avatarLetter.setTextColor(
                    avatarLetter.context.compatColor(
                        when (getLightness(hashedColor)) {
                            LightClass.DARK -> R.color.off_white
                            LightClass.LIGHT -> R.color.off_black_dark
                        }
                    )
                )
            }
        }
    }

    companion object {
        private const val HEADER = 0
        private const val ITEM = 1
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progressTankView: ProgressTankView = view.discountView
        val subtitle: TextView = view.subtitle
        val explainer: TextView = view.explainer
        val code: TextView = view.code
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.avatar
        val avatarLetter: TextView = view.avatarLetter
        val name: TextView = view.name
        val statusText: TextView = view.statusText
        val statusIconContainer: LinearLayout = view.statusIconContainer
        val discount: TextView = view.discount
        val statusIcon: ImageView = view.statusIcon
    }
}
