package com.hedvig.app.feature.chat.native

import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.type.KeyboardType

sealed class ChatInputType {
    companion object {
        fun from(message: ChatMessagesQuery.Message) = when (val body = message.body) {
            is ChatMessagesQuery.AsMessageBodyFile -> TextInput()
            is ChatMessagesQuery.AsMessageBodyText -> TextInput(body.keyboard, body.placeholder, true)
            is ChatMessagesQuery.AsMessageBodyNumber -> TextInput(body.keyboard, body.placeholder, false)
            else -> TODO("Will implement once we have started building other input types.")
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
