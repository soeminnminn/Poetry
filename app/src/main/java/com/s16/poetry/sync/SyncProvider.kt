package com.s16.poetry.sync

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns

/*
 * Define an implementation of ContentProvider that stubs out all methods
 */
class SyncProvider : ContentProvider() {

    private lateinit var dbHelper: DbHelper
    private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
    init {
        for (i in TABLES.indices) {
            URI_MATCHER.addURI(AUTHORITY, TABLES[i], MATCH_ALL)
            URI_MATCHER.addURI(AUTHORITY, "${TABLES[i]}/#", MATCH_ID)
        }
    }

    private val contentResolver: ContentResolver
        get() = context?.contentResolver!!

    override fun onCreate(): Boolean {
        dbHelper = DbHelper()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val matchId = URI_MATCHER.match(uri)

        return if (matchId == MATCH_ALL && matchId == MATCH_ID) {
            val table = getTableName(uri)

            val args : MutableList<String> = mutableListOf()
            selectionArgs?.let {
                args.addAll(0, it.asList())
            }

            val whereClause = if (matchId == MATCH_ID) {
                val id = ContentUris.parseId(uri)
                args.add("$id")

                if (selection == null)
                    "${BaseColumns._ID} = ?"
                else
                    "AND ${BaseColumns._ID} = ?"
            } else selection

            dbHelper.query(false, table, projection?.asList(), whereClause, args, orderBy = sortOrder)?.apply {
                setNotificationUri(contentResolver, uri)
            }
        } else null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val matchId = URI_MATCHER.match(uri)

        return if (matchId == MATCH_ALL) {
            values?.let {
                val table = getTableName(uri)
                val retId = dbHelper.insert(table, null, it)
                ContentUris.withAppendedId(CONTENT_URI, retId).apply {
                    contentResolver.notifyChange(this, null)
                }
            }
        } else null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val matchId = URI_MATCHER.match(uri)

        return if (matchId == MATCH_ALL) {
            values?.let {
                val table = getTableName(uri)
                dbHelper.update(table, it, selection, selectionArgs?.asList()).also {
                    contentResolver.notifyChange(uri, null)
                }
            } ?: 0
        } else 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val matchId = URI_MATCHER.match(uri)

        return if (matchId == MATCH_ALL) {
            val table = getTableName(uri)
            dbHelper.delete(table, selection, selectionArgs?.asList()).also {
                contentResolver.notifyChange(uri, null)
            }
        } else 0
    }

    override fun getType(uri: Uri): String? {
        val matchId = URI_MATCHER.match(uri)
        if (matchId != -1) {
            val segments = uri.pathSegments
            return if (matchId == MATCH_ID) {
                segments.removeAt(segments.size - 1)
                getContentTypeItem(segments.last())
            } else {
                getContentTypeDir(segments.last())
            }
        }
        return null
    }

    private fun getTableName(uri: Uri) : String {
        val matchId = URI_MATCHER.match(uri)
        if (matchId != -1) {
            val segments = uri.pathSegments
            return if (matchId == MATCH_ID) {
                segments.removeAt(segments.size - 1)
            } else {
                segments.last()
            }
        }
        throw Exception("Table not found.")
    }

    private fun getContentTypeDir(name: String) : String =
        "${ContentResolver.CURSOR_DIR_BASE_TYPE}/${VND_PREFIX}.${name}s"

    private fun getContentTypeItem(name: String): String =
        "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/${VND_PREFIX}.${name}"

    // TODO: Sync
    inner class DbHelper {
        fun query(distinct: Boolean = false, table: String, columns: List<String>? = null,
                  selection: String? = null, selectionArgs: List<String>? = null,
                  groupBy: String? = null, having: String? = null,
                  orderBy: String? = null, limit: String? = null) : Cursor? = null

        fun rawQuery(sql: String, selectionArgs: List<String>?) : Cursor? = null

        fun insert(table: String, nullColumnHack: String?, values: ContentValues) : Long = 0L

        fun update(table: String, values: ContentValues, whereClause: String?, whereArgs: List<String>?) : Int = 0

        fun delete(table: String, whereClause: String?, whereArgs: List<String>?) : Int = 0
    }

    companion object {
        const val AUTHORITY = "com.s16.poetry.sync.provider"
        const val SCHEME = "content://"
        const val VND_PREFIX = "vnd.table"

        const val MATCH_ANY = 0
        const val MATCH_SEARCH = 1
        const val MATCH_ALL = 2
        const val MATCH_ID = 4

        val CONTENT_URI = Uri.parse("$SCHEME$AUTHORITY")
        val TABLES = arrayOf("categories", "tags", "deleted", "records_add", "records")
    }
}
