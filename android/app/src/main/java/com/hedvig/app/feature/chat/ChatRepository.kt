package com.hedvig.app.feature.chat

import android.content.Context
import android.net.Uri
import com.google.gson.GsonBuilder
import com.hedvig.app.BuildConfig
import com.hedvig.app.feature.chat.dto.UploadData
import com.hedvig.app.feature.chat.dto.UploadResponse
import com.hedvig.app.service.FileService
import com.hedvig.app.util.extensions.into
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File

class ChatRepository(
    private val okHttpClient: OkHttpClient,
    private val fileService: FileService,
    private val context: Context
) {
    fun uploadFile(uri: Uri): Observable<UploadData> = Observable.create<UploadData> { emitter ->
        val file = File.createTempFile(TEMP_FILE_PREFIX, null) // I hate this but it seems there's no other way
        context.contentResolver.openInputStream(uri)?.into(file)
        val filename = fileService.getFileName(uri) ?: ""
        val mimeType = fileService.getMimeType(uri) ?: ""

        // See https://github.com/jaydenseric/graphql-multipart-request-spec for information.
        // TODO: Implement this in apollo-android and make a PR
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("operations", null, RequestBody.create(null, MUTATION_JSON))
            .addFormDataPart("map", null, RequestBody.create(null, VARIABLE_MAP))
            .addFormDataPart("0", filename, RequestBody.create(MediaType.parse(mimeType), file))
            .build()

        val request = Request
            .Builder()
            .url(BuildConfig.GRAPHQL_URL)
            .post(requestBody)
            .build()

        val response = okHttpClient
            .newCall(request)
            .execute()
        val parsed = GsonBuilder().create().fromJson(response.body()?.string(), UploadResponse::class.java)
        if (parsed.errors != null) {
            emitter.onError(Error("Encountered error(s) when uploading file: ${parsed.errors}"))
        }

        if (parsed.data == null) {
            emitter.onError(Error("File upload returned no data"))
        }
        parsed.data?.let { data ->
            emitter.onNext(data)
            emitter.onComplete()
        }
    }
        .subscribeOn(Schedulers.io())

    companion object {
        const val MUTATION_JSON = """
{
    "query": "mutation FileUpload(${'$'}file: Upload!) {uploadFile(file: ${'$'}file) {signedUrl,key}}",
    "variables": {
        "file": null
    }
}
        """
        const val VARIABLE_MAP = """{"0":["variables.file"]}"""

        const val TEMP_FILE_PREFIX = "hedvig_upload_temp_file"
    }
}
