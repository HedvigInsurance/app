package com.hedvig.app.feature.chat.native

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hedvig.app.R
import com.hedvig.app.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.attach_file_image_item.view.*
import kotlinx.android.synthetic.main.camera_and_misc_item.view.*

class AttachFileAdapter(private val imageUris: List<String>, private val pickerHeight: Int): RecyclerView.Adapter<AttachFileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (viewType == CAMERA_AND_MISC_VIEW_TYPE ){
            ViewHolder.CameraAndMiscViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.camera_and_misc_item,
                    parent,
                    false
                )
            )
        }else {
            ViewHolder.ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.attach_file_image_item,
                    parent,
                    false
                )
            )
        }

    override fun getItemCount() = imageUris.size + 1

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when (viewHolder) {
            is ViewHolder.CameraAndMiscViewHolder -> {
                viewHolder.cameraButton.setHapticClickListener {
                    //todo
                }
                viewHolder.miscButton.setHapticClickListener {
                    //todo
                }
            }
            is ViewHolder.ImageViewHolder -> {
                viewHolder.apply {
                    val uri = imageUris[position]
                    val params  = attachFileImage.layoutParams
                    val margin = attachFileImage.context.resources.getDimensionPixelSize(R.dimen.base_margin_double) * 2
                    val roundedCornersRadius = attachFileImage.context.resources.getDimensionPixelSize(R.dimen.attach_file_rounded_corners_radius)
                    params.height = pickerHeight - margin
                    params.width = pickerHeight - margin
                    attachFileImage.layoutParams = params
                    Glide
                        .with(attachFileImage.context)
                        .load(uri)
                        .transform(MultiTransformation(CenterCrop(), RoundedCorners(roundedCornersRadius)))
                        .into(attachFileImage)
                    attachFileImageContainer.setHapticClickListener {
                        //todo
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int) =
        if (position == 0) CAMERA_AND_MISC_VIEW_TYPE else IMAGE_VIEW_TYPE

    sealed class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        class CameraAndMiscViewHolder(itemView: View) : ViewHolder(itemView) {
            val cameraButton: FrameLayout = itemView.cameraButton
            val miscButton: FrameLayout = itemView.miscButton
        }

        class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
            val attachFileImage: ImageView = itemView.attachFileImage
            val attachFileImageContainer: FrameLayout = itemView.attachFileContainer
        }
    }

    companion object {
        private const val CAMERA_AND_MISC_VIEW_TYPE = 0
        private const val IMAGE_VIEW_TYPE = 1
    }
}
