package com.hedvig.app.feature.chat.native

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.app.R
import com.hedvig.app.util.extensions.observe
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.viewmodel.ext.android.viewModel

class NativeChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        input.initialize(
            sendTextMessage = { message -> chatViewModel.respondToLasMessage(message) }
        )

        messages.adapter = ChatAdapter()
        chatViewModel.messages.observe(this) { data ->
            data?.let { bindData(it) }
        }
        chatViewModel.sendMessageResponse.observe(this) {response ->
            if (response == true) {
                input.clearInput()
            }
        }
    }

    private fun bindData(data: ChatMessagesQuery.Data) {
        (messages.adapter as? ChatAdapter)?.messages = data.messages
    }
}
