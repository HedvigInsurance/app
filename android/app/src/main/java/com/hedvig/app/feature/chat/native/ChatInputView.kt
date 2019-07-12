package com.hedvig.app.feature.chat.native

import android.content.Context
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.hedvig.android.owldroid.fragment.ChatMessageFragment
import com.hedvig.android.owldroid.type.KeyboardType
import com.hedvig.app.R
import com.hedvig.app.util.extensions.children
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import kotlinx.android.synthetic.main.chat_input_view.view.*

class ChatInputView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    private lateinit var sendTextMessage: ((String) -> Unit)
    private lateinit var sendSingleSelect: ((String) -> Unit)
    private lateinit var singleSelectLink: ((String) -> Unit)
    private lateinit var paragraphPullMessages: (() -> Unit)
    private lateinit var openAttachFile: (() -> Unit)

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    var message: ChatInputType? = null
        set(value) {
            field = value
            hideInputContainers()
            when (value) {
                is TextInput -> bindTextInput(value)
                is SingleSelect -> bindSingleSelect(value)
                is ParagraphInput -> bindParagraphInput()
                is NullInput -> bindNullInput()
            }
        }

    init {
        inflate(context, R.layout.chat_input_view, this)
        inputText.sendClickListener = {
            sendTextMessage(inputText.currentMessage)
        }
        uploadFile.setHapticClickListener {
            inputText.clearFocus()
            openAttachFile()
        }
    }

    fun initialize(sendTextMessage: (String) -> Unit,
                   sendSingleSelect: (String) -> Unit,
                   sendSingleSelectLink: (String) -> Unit,
                   paragraphPullMessages: () -> Unit,
                   openAttachFile: () -> Unit) {
        this.sendTextMessage = sendTextMessage
        this.sendSingleSelect = sendSingleSelect
        this.singleSelectLink = sendSingleSelectLink
        this.paragraphPullMessages = paragraphPullMessages
        this.openAttachFile = openAttachFile
    }

    fun clearInput() {
        inputText.text.clear()
    }

    private fun hideInputContainers() {
        textInputContainer.remove()
        singleSelectContainer.remove()
        paragraphView.remove()
        paragraphView.cancelAnimation()
        nullView.remove()
    }

    private fun bindTextInput(input: TextInput) {
        textInputContainer.show()
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
        singleSelectContainer.show()
        singleSelectContainer.removeAllViews()
        input.options.forEach {
            when (it) {
                is ChatMessageFragment.AsMessageBodyChoicesSelection ->
                    inflateSingleSelectButton(it.text, it.value, SingleSelectChoiceType.SELECTION)
                is ChatMessageFragment.AsMessageBodyChoicesLink ->
                    inflateSingleSelectButton(it.text, it.value, SingleSelectChoiceType.LINK)
                is ChatMessageFragment.AsMessageBodyChoicesUndefined ->
                    inflateSingleSelectButton(it.text, it.value, SingleSelectChoiceType.UNDEFINED)
            }
        }
    }

    private fun inflateSingleSelectButton(label: String, value: String, type: SingleSelectChoiceType) {
        val singleSelectButton = layoutInflater.inflate(R.layout.chat_single_select_button, singleSelectContainer, false) as TextView
        singleSelectButton.text = label
        singleSelectButton.setHapticClickListener {
            singleSelectButton.isSelected = true
            singleSelectButton.setTextColor(context.compatColor(R.color.white))
            disableSingleButtons()
            when (type) {
                SingleSelectChoiceType.UNDEFINED, // TODO: Let's talk about this one
                SingleSelectChoiceType.SELECTION -> sendSingleSelect(value)
                SingleSelectChoiceType.LINK -> singleSelectLink(value)
            }

        }

        singleSelectContainer.addView(singleSelectButton)
    }

    private fun disableSingleButtons() {
        singleSelectContainer.children.forEach { it.isEnabled = false }
    }

    private fun bindParagraphInput() {
        paragraphView.show()
        paragraphView.playAnimation()
        paragraphPullMessages()
    }

    private fun bindNullInput() {
        nullView.show()
    }

    fun rotateFileUploadIcon(isOpening: Boolean) {
        SpringAnimation(uploadFile, DynamicAnimation.ROTATION).animateToFinalPosition(if (isOpening) 135f else 0f)
    }

}

enum class SingleSelectChoiceType {
    LINK,
    SELECTION,
    UNDEFINED
}
