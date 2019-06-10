package com.hedvig.app.feature.chat.native

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.app.R
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.setupLargeTitle
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.ext.android.inject

class NativeChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        setupLargeTitle(R.string.CHAT_TITLE, R.font.soray_extrabold)

        chatViewModel.messages.observe(this) { data ->
            data?.let { bindData(it) }
        }
    }

    private fun bindData(data: ChatMessagesQuery.Data) {
        messages.adapter = ChatAdapter(data.messages)
    }
}
