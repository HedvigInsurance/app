package com.hedvig.app.util.react

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.facebook.react.modules.storage.ReactDatabaseSupplier
import timber.log.Timber

class AsyncStorageNativeImpl(
    private val context: Context
) : AsyncStorageNative {

    private val inMemoryCache = HashMap<String, String>()

    override fun getKey(key: String): String? {
        if (inMemoryCache.containsKey(key)) {
            val value = inMemoryCache[key]
            Timber.i("Cache hit: $key: $value")
            return value
        }
        var catalystLocalStorage: Cursor? = null
        try {
            val readableDatabase = ReactDatabaseSupplier.getInstance(context).readableDatabase
            catalystLocalStorage = readableDatabase.query(
                TABLE,
                arrayOf("key", "value"),
                "key = ?",
                arrayOf(key), null, null, null
            )

            return if (catalystLocalStorage!!.moveToFirst()) {
                val value = catalystLocalStorage.getString(catalystLocalStorage.getColumnIndex("value"))
                inMemoryCache[key] = value
                return value
            } else {
                null
            }
        } finally {
            catalystLocalStorage?.close()
        }
    }

    override fun setKey(key: String, value: String) {
        val database = ReactDatabaseSupplier.getInstance(context).writableDatabase
        database?.insert(TABLE, null, ContentValues().apply {
            put("key", key)
            put("value", value)
        })
        inMemoryCache[key] = value
    }

    override fun deleteKey(key: String) {
        if (inMemoryCache.containsKey(key)) {
            inMemoryCache.remove(key)
        }
        val database = ReactDatabaseSupplier.getInstance(context).writableDatabase
        database?.delete(TABLE, "key = ?", arrayOf(key))
    }

    override fun close() {
        ReactDatabaseSupplier.getInstance(context).close()
    }

    companion object {
        private const val TABLE = "catalystLocalStorage"
    }
}


