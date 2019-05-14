package com.hedvig.app.feature.chat

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.hedvig.app.util.LiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class ChatViewModel(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val isUploading = LiveEvent<Boolean>()
    val fileUploadKey = LiveEvent<String>()

    fun logout(callback: () -> Unit) {
        disposables += userRepository
            .logout()
            .subscribe({
                callback()
            }, { error ->
                Timber.e(error, "Failed to log out")
            })
    }

    fun uploadFile(uri: Uri) {
        isUploading.value = true
        disposables += chatRepository
            .uploadFile(uri)
            .subscribe({ data ->
                data.uploadFile.key?.let { fileUploadKey.postValue(it) }
            }, { Timber.e(it) })
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
