package com.hedvig.app.feature.chat.native

import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.type.KeyboardType

sealed class ChatInputType {
    companion object {
        fun from(message: ChatMessagesQuery.Message): ChatInputType {
            val body = message.body
            if (body is ChatMessagesQuery.AsMessageBodyFile) {
                return TextInput()
            }
            if (body is ChatMessagesQuery.AsMessageBodyText) {
                return TextInput(body.keyboard, body.placeholder, true)
            }
            if (body is ChatMessagesQuery.AsMessageBodyNumber) {
                return TextInput(body.keyboard, body.placeholder, false)
            }
            TODO("Will implement once we have started building other input types.")
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
