package com.hedvig.app.feature.chat.native

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.app.R
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import kotlinx.android.synthetic.main.chat_message_hedvig.view.*
import kotlinx.android.synthetic.main.chat_message_user.view.*

class ChatAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            FROM_ME -> UserMessage(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.chat_message_user,
                    parent,
                    false
                )
            )
            else -> TODO("Handle the invalid invariant")
        }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int) = if (messages[position].header.isFromMyself) {
        FROM_ME
    } else {
        FROM_HEDVIG
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            FROM_HEDVIG -> {
                (viewHolder as? HedvigMessage)?.apply {
                    message.show()
                    val data = messages[position]
                    message.text = data.body.text
                    if (data.body.text == "") {
                        message.remove()
                    }
                }
            }
            FROM_ME -> {
                (viewHolder as? UserMessage)?.apply {
                    val data = messages[position]
                    message.show()
                    message.text = data.body.text
                }
            }
        }
    }

    inner class HedvigMessage(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.hedvigMessage
    }

    inner class UserMessage(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.userMessage
    }

    companion object {
        private const val FROM_HEDVIG = 0
        private const val FROM_ME = 1

        private fun isLast(position: Int, items: List<*>) = items.size - 1 == position
    }
}


