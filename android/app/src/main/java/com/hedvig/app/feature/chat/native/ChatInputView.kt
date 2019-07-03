package com.hedvig.app.feature.chat.native

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import com.hedvig.app.R
import com.hedvig.app.util.extensions.makeToast
import kotlinx.android.synthetic.main.chat_input_view.view.*

class ChatInputView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    init {
        inflate(context, R.layout.chat_input_view, this)
        inputText.sendClickListener = {
            context.makeToast("Send text ${inputText.currentMessage}")
        }
    }

}
