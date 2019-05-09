package com.hedvig.app.feature.chat

import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import com.hedvig.app.di.ViewModelFactory
import com.hedvig.app.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.app.util.extensions.localBroadcastManager
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.R
import com.hedvig.app.react.ActivityStarterModule
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.file_upload_dialog.*
import javax.inject.Inject

class UploadBottomSheet : RoundedBottomSheetDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater
            .from(requireContext())
            .inflate(R.layout.file_upload_dialog, null)
        dialog.setContentView(view)

        dialog.uploadImageOrVideo.setHapticClickListener {
            selectImageFromLibrary()
        }

        dialog.uploadFile.setHapticClickListener {
            selectFile()
        }

        setupSubscriptions()
    }

    private fun setupSubscriptions() {
        chatViewModel.isUploading.observe(this) { isUploading ->
            isUploading?.let { iu ->
                if (iu) {
                    dialog.header.text = resources.getString(R.string.FILE_UPLOAD_IS_UPLOADING)
                    dialog.loadingSpinner.playAnimation()
                    dialog.loadingSpinner.show()
                    dialog.uploadImageOrVideo.remove()
                    dialog.uploadFile.remove()
                    isCancelable = false
                }
            }
        }

        chatViewModel.fileUploadKey.observe(this) { fileUploadKey ->
            fileUploadKey?.let { fuk ->
                localBroadcastManager.sendBroadcast(Intent(ActivityStarterModule.FILE_UPLOAD_INTENT).apply {
                    putExtra(ActivityStarterModule.FILE_UPLOAD_RESULT, ActivityStarterModule.FILE_UPLOAD_SUCCESS)
                    putExtra(ActivityStarterModule.FILE_UPLOAD_KEY, fuk)
                })
                isCancelable = true
                dismiss()
            }
        }
    }

    private fun selectImageFromLibrary() {
        startActivityForResult(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            SELECT_IMAGE_REQUEST_CODE
        )
    }

    private fun selectFile() {
        startActivityForResult(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            },
            SELECT_FILE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            SELECT_FILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    resultData?.data?.let { uri ->
                        chatViewModel.uploadFile(uri)
                    }
                }
            }
            SELECT_IMAGE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    resultData?.data?.let { uri ->
                        chatViewModel.uploadFile(uri)
                    }
                }
            }
        }
    }

    companion object {
        private const val SELECT_FILE_REQUEST_CODE = 42
        private const val SELECT_IMAGE_REQUEST_CODE = 43
    }
}
