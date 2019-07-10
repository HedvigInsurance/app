package com.hedvig.app.feature.chat.native

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hedvig.android.owldroid.fragment.ChatMessageFragment
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.app.R
import com.hedvig.app.util.convertDpToPixel
import com.hedvig.app.util.extensions.openUri
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.chat_message_file_upload.view.*
import kotlinx.android.synthetic.main.chat_message_hedvig.view.*
import kotlinx.android.synthetic.main.chat_message_user.view.*
import kotlinx.android.synthetic.main.chat_message_user_giphy.view.*
import kotlinx.android.synthetic.main.chat_message_user_image.view.*

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messages: List<ChatMessagesQuery.Message> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            FROM_HEDVIG -> HedvigMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_hedvig,
                    parent,
                    false
                )
            )
            FROM_ME_TEXT -> UserMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_user,
                    parent,
                    false
                )
            )
            FROM_ME_GIPHY -> GiphyUserMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_user_giphy,
                    parent,
                    false
                )
            )
            FROM_ME_IMAGE -> ImageUserMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_user_image,
                    parent,
                    false
                )
            )
            FROM_ME_IMAGE_UPLOAD -> ImageUploadUserMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_user_image,
                    parent,
                    false
                )
            )
            FROM_ME_FILE_UPLOAD -> FileUploadUserMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_file_upload,
                    parent,
                    false
                )
            )
            else -> TODO("Handle the invalid invariant")
        }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int) = if (messages[position].fragments.chatMessageFragment.header.isFromMyself) {
        when {
            isImageUploadMessage(messages[position].fragments.chatMessageFragment.body) -> FROM_ME_IMAGE_UPLOAD
            isFileUploadMessage((messages[position].fragments.chatMessageFragment.body)) -> FROM_ME_FILE_UPLOAD
            isGiphyMessage(messages[position].fragments.chatMessageFragment.body?.text) -> FROM_ME_GIPHY
            isImageMessage(messages[position].fragments.chatMessageFragment.body?.text) -> FROM_ME_IMAGE
            else -> FROM_ME_TEXT
        }
    } else {
        FROM_HEDVIG
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            FROM_HEDVIG -> {
                (viewHolder as? HedvigMessage)?.apply { bind(messages[position].fragments.chatMessageFragment.body?.text) }
            }
            FROM_ME_TEXT -> {
                (viewHolder as? UserMessage)?.apply { bind(messages[position].fragments.chatMessageFragment.body?.text) }
            }
            FROM_ME_GIPHY -> {
                (viewHolder as? GiphyUserMessage)?.apply { bind(messages[position].fragments.chatMessageFragment.body?.text) }
            }
            FROM_ME_IMAGE -> {
                (viewHolder as? ImageUserMessage)?.apply { bind(messages[position].fragments.chatMessageFragment.body?.text) }
            }
            FROM_ME_IMAGE_UPLOAD -> {
                (viewHolder as? ImageUploadUserMessage)?.apply { bind(getFileUrl(messages[position].fragments.chatMessageFragment.body)) }
            }
            FROM_ME_FILE_UPLOAD -> {
                (viewHolder as? FileUploadUserMessage)?.apply { bind(getFileUrl(messages[position].fragments.chatMessageFragment.body)) }
            }
        }
    }

    inner class HedvigMessage(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.hedvigMessage

        fun reset() {
            message.remove()
        }

        fun bind(text: String?) {
            reset()
            if (text == "") {
                return
            }
            message.show()
            message.text = text
        }
    }

    inner class UserMessage(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.userMessage

        fun bind(text: String?) {
            message.text = text
        }
    }

    inner class GiphyUserMessage(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.messageImage
        val giphy: LinearLayout = view.giphy

        fun bind(url: String?) {
            Glide
                .with(image)
                .load(url)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .override(
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                )
                .into(image)
        }
    }

    inner class ImageUserMessage(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.uploadedImage

        fun bind(url: String?) {
            image.remove() // Not supported for now as our image resizing API does not work.
            //Glide
            //    .with(image)
            //    .load("${BuildConfig.PIG_URL}/unsafe/280/200/smart/${URLEncoder.encode(url, "utf-8")}")
            //    .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            //    .override(
            //        com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
            //        com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
            //    )
            //    .into(image)
        }
    }

    inner class ImageUploadUserMessage(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.uploadedImage

        fun bind(url: String?) {
            Glide
                .with(image)
                .load(url)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .override(
                    convertDpToPixel(280f),
                    convertDpToPixel(200f)
                )
                .into(image)
        }
    }

    inner class FileUploadUserMessage(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.fileUploadLabel

        fun bind(url: String?) {
            val asUri = Uri.parse(url)
            val extension = getExtension(asUri)

            label.text = interpolateTextKey(
                label.resources.getString(R.string.CHAT_FILE_UPLOADED),
                "EXTENSION" to extension
            )
            label.setHapticClickListener {
                label.context.openUri(Uri.parse(url))
            }
        }
    }

    companion object {
        private const val FROM_HEDVIG = 0
        private const val FROM_ME_TEXT = 1
        private const val FROM_ME_GIPHY = 2
        private const val FROM_ME_IMAGE = 3
        private const val FROM_ME_IMAGE_UPLOAD = 4
        private const val FROM_ME_FILE_UPLOAD = 5

        private val imageExtensions = listOf(
            "jpg", "png", "gif", "jpeg"
        )

        private fun isLast(position: Int, items: List<*>) = items.size - 1 == position
        private fun isGiphyMessage(text: String?) = text?.contains("giphy.com") ?: false
        private fun getExtension(uri: Uri) = uri.lastPathSegment?.substringAfterLast('.', "")
        private fun isImageMessage(text: String?): Boolean {
            val asUri = Uri.parse(text)

            return imageExtensions.contains(asUri.lastPathSegment?.substringAfterLast('.', ""))
        }

        private fun isImageUploadMessage(body: ChatMessageFragment.Body?): Boolean {
            val asUpload = (body as? ChatMessageFragment.AsMessageBodyFile) ?: return false

            return isImageMessage(asUpload.file.signedUrl)
        }

        private fun isFileUploadMessage(body: ChatMessageFragment.Body?): Boolean {
            val asUpload = (body as? ChatMessageFragment.AsMessageBodyFile) ?: return false

            return !isImageMessage(asUpload.file.signedUrl)
        }

        private fun getFileUrl(body: ChatMessageFragment.Body?) =
            (body as? ChatMessageFragment.AsMessageBodyFile)?.file?.signedUrl
    }
}


