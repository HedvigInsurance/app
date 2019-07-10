package com.hedvig.app.feature.chat.native

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.app.R
import com.hedvig.app.feature.profile.ui.charity.CharityExplanationBottomSheet
import com.hedvig.app.util.extensions.*
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.showRestartDialog
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.viewmodel.ext.android.viewModel

class NativeChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        input.initialize(
            sendTextMessage = { message -> chatViewModel.respondToLastMessage(message) },
            sendSingleSelect = { value -> chatViewModel.respondWithSingleSelect(value) },
            sendSingleSelectLink = { value -> handleSingleSelectLink(value) },
            paragraphPullMessages = { chatViewModel.load() },
            openUpload = { }
        )

        messages.adapter = ChatAdapter()
        chatViewModel.messages.observe(this) { data ->
            data?.let { bindData(it) }
        }
        chatViewModel.sendMessageResponse.observe(this) { response ->
            if (response == true) {
                input.clearInput()
                chatViewModel.load()
            }
        }
        chatViewModel.sendSingelSelectResponse.observe(this) { response ->
            if (response == true) {
                chatViewModel.load()
            }
        }

        resetChatButton.setOnClickListener {
            showRestartDialog {
                setAuthenticationToken(null)
                userViewModel.logout { triggerRestartActivity(NativeChatActivity::class.java) }
            }
        }

        reload.setHapticClickListener {
            chatViewModel.load()
        }

        getAuthenticationToken()?.let {
            chatViewModel.loadAndSubscribe()
        } ?: run {
            userViewModel.newSessionInformation.observe(this@NativeChatActivity) { data ->
                data?.createSessionV2?.token?.let {
                    setAuthenticationToken(it)

                    chatViewModel.loadAndSubscribe()
                }
            }
            userViewModel.newSession()
        }
    }

    private fun bindData(data: ChatMessagesQuery.Data) {
        (messages.adapter as? ChatAdapter)?.messages = data.messages
        input.message = data.messages.firstOrNull()?.let { ChatInputType.from(it) }
    }
}
