package com.hedvig.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import timber.log.Timber

class LegacyReactDatabaseSupplier private constructor(private val mContext: Context) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private var mDb: SQLiteDatabase? = null
    private var mMaximumDatabaseSize = 6L * 1024L * 1024L // 6 MB in bytes

    override fun onCreate(db: SQLiteDatabase) {
        Timber.e("This database is deprecated and should not be used. Let's not create a new one!")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Timber.e("This database is deprecated and should not be used. Let's not upgrade is!")
    }

    /**
     * Verify the database exists and is open.
     */
    /* package */ @Synchronized
    internal fun ensureDatabase(): Boolean {
        if (mDb != null && mDb!!.isOpen) {
            return true
        }
        // Sometimes retrieving the database fails. We do 2 retries: first without database deletion
        // and then with deletion.
        var lastSQLiteException: SQLiteException? = null
        for (tries in 0..1) {
            try {
                if (tries > 0) {
                    deleteDatabase()
                }
                mDb = writableDatabase
                break
            } catch (e: SQLiteException) {
                lastSQLiteException = e
            }

            // Wait before retrying.
            try {
                Thread.sleep(SLEEP_TIME_MS.toLong())
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
            }

        }
        if (mDb == null) {
            lastSQLiteException?.let {
                Timber.e(it, "mDb could not be created :(")
            } ?: Timber.e("mDb could not be created and lastSQLiteException :crying_sad_face:")
        }
        // This is a sane limit to protect the user from the app storing too much data in the database.
        // This also protects the database from filling up the disk cache and becoming malformed
        // (endTransaction() calls will throw an exception, not rollback, and leave the db malformed).
        mDb?.maximumSize = mMaximumDatabaseSize
        return true
    }

    @Synchronized
    fun get(): SQLiteDatabase? {
        ensureDatabase()
        return mDb
    }

    @Synchronized
    fun clearAndCloseDatabase() {
        try {
            clear()
            closeDatabase()
        } catch (e: Exception) {
            // Clearing the database has failed, delete it instead.
            if (deleteDatabase()) {
                return
            }
            // Everything failed, throw
            Timber.e("Clearing and deleting database $DATABASE_NAME failed")
        }

    }

    /* package */ @Synchronized
    internal fun clear() {
        get()?.delete(TABLE_CATALYST, null, null)
    }

    @Synchronized
    private fun deleteDatabase(): Boolean {
        closeDatabase()
        return mContext.deleteDatabase(DATABASE_NAME)
    }

    @Synchronized
    private fun closeDatabase() {
        if (mDb != null && mDb!!.isOpen) {
            mDb!!.close()
            mDb = null
        }
    }

    companion object {

        // VisibleForTesting
        val DATABASE_NAME = "RKStorage"

        private val DATABASE_VERSION = 1
        private val SLEEP_TIME_MS = 30

        internal val TABLE_CATALYST = "catalystLocalStorage"
        internal val KEY_COLUMN = "key"
        internal val VALUE_COLUMN = "value"

        internal val VERSION_TABLE_CREATE = "CREATE TABLE " + TABLE_CATALYST + " (" +
            KEY_COLUMN + " TEXT PRIMARY KEY, " +
            VALUE_COLUMN + " TEXT NOT NULL" +
            ")"

        fun getInstance(context: Context): LegacyReactDatabaseSupplier {
            return LegacyReactDatabaseSupplier(context.applicationContext)
        }
    }
}
