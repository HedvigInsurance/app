package com.hedvig.logged_in.claims.ui.commonclaim.bulletpoint

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestBuilder
import com.hedvig.logged_in.R
import com.hedvig.common.owldroid.CommonClaimQuery
import kotlinx.android.synthetic.main.claim_bulletpoint_row.view.*
import org.jetbrains.annotations.NotNull

class BulletPointsAdapter(
    private val bulletPoints: @NotNull MutableList<CommonClaimQuery.BulletPoint>,
    private val baseUrl: String,
    private val requestBuilder: RequestBuilder<PictureDrawable>
) : RecyclerView.Adapter<BulletPointsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.claim_bulletpoint_row,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = bulletPoints.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val bulletPoint = bulletPoints[position]
        viewHolder.apply {
            requestBuilder.load(Uri.parse(baseUrl + bulletPoint.icon().svgUrl())).into(bulletPointIcon)
            bulletPointTitle.text = bulletPoint.title()
            bulletPointDescription.text = bulletPoint.description()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bulletPointIcon: ImageView = itemView.bulletPointIcon
        val bulletPointTitle: TextView = itemView.bulletPointTitle
        val bulletPointDescription: TextView = itemView.bulletPointDescription
    }
}
