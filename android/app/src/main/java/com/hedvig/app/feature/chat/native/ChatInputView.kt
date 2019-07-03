package com.hedvig.app.feature.chat.native

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hedvig.app.R
import com.hedvig.app.util.extensions.makeToast
import kotlinx.android.synthetic.main.chat_input_view.view.*

class ChatInputView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    private lateinit var sendTextMessage: ((String) -> Unit)

    init {
        inflate(context, R.layout.chat_input_view, this)
        inputText.sendClickListener = {
            sendTextMessage(inputText.currentMessage)
        }
    }

    fun initialize(sendTextMessage: (String) -> Unit) {
        this.sendTextMessage = sendTextMessage
    }

    fun clearInput() {
        inputText.text.clear()
    }

}
