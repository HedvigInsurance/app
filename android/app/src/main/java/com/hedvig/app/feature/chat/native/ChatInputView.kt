package com.hedvig.app.feature.chat.native

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hedvig.app.R

class ChatInputView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    init {
        inflate(context, R.layout.chat_input_view, this)
    }
}
