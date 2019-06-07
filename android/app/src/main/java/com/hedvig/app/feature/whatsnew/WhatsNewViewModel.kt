package com.hedvig.app.feature.whatsnew

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.hedvig.android.owldroid.graphql.WhatsNewQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class WhatsNewViewModel(
    private val whatsNewRepository: WhatsNewRepository
) : ViewModel() {

    val news = MutableLiveData<WhatsNewQuery.Data>()

    private val disposables = CompositeDisposable()

    init {
        disposables += whatsNewRepository
            .fetchWhatsNew()
            .subscribe({ response ->
                news.postValue(response.data())
            }, { Timber.e(it) })
    }

    fun hasSeenNews(version: String) = whatsNewRepository.hasSeenNews(version)

}
