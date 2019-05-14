package com.hedvig.app.service

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.hedvig.android.owldroid.graphql.TextKeysQuery
import com.ice.restring.Restring
import com.ice.restring.RestringUtil
import timber.log.Timber
import javax.inject.Inject

class TextKeys @Inject constructor(val apolloClient: ApolloClient) {
    fun refreshTextKeys() {
        val textKeysQuery = TextKeysQuery
            .builder()
            .build()

        apolloClient
            .query(textKeysQuery)
            .enqueue(object : ApolloCall.Callback<TextKeysQuery.Data>() {
                override fun onStatusEvent(event: ApolloCall.StatusEvent) {
                    Timber.d("StatusEvent: %s", event.toString())
                }

                override fun onFailure(e: ApolloException) {
                    Timber.e(e, "Failed to load text keys")
                }

                override fun onResponse(response: Response<TextKeysQuery.Data>) {
                    val data = response.data()?.languages()

                    data
                        ?.filter { !EXCLUDED_LANGUAGES.contains(it.code()) }
                        ?.forEach { language ->
                            language.translations()
                                ?.filter { it.key()?.value() != null }
                                ?.map { translation ->
                                    translation.key()?.value() as String to translation.text().replace(
                                        "\\n",
                                        "\n"
                                    ) as String?
                                }
                                ?.toMap()
                                ?.toMutableMap()
                                ?.let { textKeys ->
                                    Restring.setStrings(formatLanguageCode(language.code()), textKeys)
                                    if (language.code() == DEFAULT_LANGUAGE) {
                                        Restring.setStrings(RestringUtil.DEFAULT_LANGUAGE, textKeys)
                                    }
                                }
                        }
                }
            })
    }

    companion object {
        private val EXCLUDED_LANGUAGES = listOf("en_SE")
        private const val DEFAULT_LANGUAGE = "sv_SE"

        fun formatLanguageCode(languageCode: String): String = languageCode.replace("_", "-r")
    }
}
