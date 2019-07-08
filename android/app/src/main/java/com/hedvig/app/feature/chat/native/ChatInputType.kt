package com.hedvig.app.feature.chat.native

import com.hedvig.android.owldroid.fragment.ChatMessageFragmet
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.type.KeyboardType
import timber.log.Timber

sealed class ChatInputType {
    companion object {
        fun from(message: ChatMessagesQuery.Message) = when (val body = message.fragments.chatMessageFragmet.body) {
            is ChatMessageFragmet.AsMessageBodyFile -> TextInput()
            is ChatMessageFragmet.AsMessageBodyText -> TextInput(body.keyboard, body.placeholder, true)
            is ChatMessageFragmet.AsMessageBodyNumber -> TextInput(body.keyboard, body.placeholder, false)
            else -> {
                Timber.e("Implement support for ${message::class.java.simpleName}")
                NullInput
            }
        }
    }
}

data class TextInput(
    val keyboardType: KeyboardType? = null,
    val hint: String? = null,
    val richTextSupport: Boolean = false
) : ChatInputType()

data class SingleSelect(val options: List<*>) : ChatInputType()
object Audio : ChatInputType()
object NullInput : ChatInputType()
