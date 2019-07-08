package com.hedvig.app.feature.chat.native

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hedvig.android.owldroid.type.KeyboardType
import com.hedvig.app.R
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import kotlinx.android.synthetic.main.chat_input_view.view.*

class ChatInputView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    private lateinit var sendTextMessage: ((String) -> Unit)

    var message: ChatInputType? = null
        set(value) {
            field = value

            when (value) {
                is TextInput -> bindTextInput(value)
            }
        }

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

    private fun bindTextInput(input: TextInput) {
        if (input.richTextSupport) {
            uploadFile.show()
            sendGif.show()
        } else {
            uploadFile.remove()
            sendGif.remove()
        }
        inputText.hint = input.hint ?: ""
        inputText.inputType = when (input.keyboardType) {
            KeyboardType.DEFAULT -> InputType.TYPE_CLASS_TEXT
            KeyboardType.EMAIL -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            KeyboardType.PHONE -> InputType.TYPE_CLASS_PHONE
            KeyboardType.NUMBERPAD, KeyboardType.NUMERIC -> InputType.TYPE_CLASS_NUMBER
            KeyboardType.DECIMALPAD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

    private fun bindSingleSelect(input: SingleSelect) {
    }

    private fun bindNullInput() {
    }
}
