package com.s16.poetry.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.s16.poetry.Constants
import com.s16.utils.*
import kotlinx.coroutines.*
import java.io.*
import java.lang.Exception
import java.lang.Runnable
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


internal class RestoreTask(
    private val context: Context,
    private val file: File,
    private val onCanceled: (message: String) -> Unit,
    private val onComplete: () -> Unit
): Runnable {

    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun run() {
        val manager = DbManager(context.applicationContext)

        job = uiScope.launch {
            val filePath = prepareFile(file)
            if (filePath == null) {
                onCanceled("Can not read backup file.")
            } else {
                val openHelper = createOpenHelper(filePath)
                importData(manager, openHelper)
                openHelper.close()

                onComplete()
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }

    private fun createOpenHelper(name: String): SQLiteOpenHelper {
        return object: SQLiteOpenHelper(context, name, null, 1) {
            override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL("PRAGMA user_version = 0;")
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            }
        }
    }

    private suspend fun prepareFile(file: File): String? =
        withContext(Dispatchers.IO) {
            var filePath: String? = "${context.cacheDir}/${file.name}"

            if (file.extension == "zip") {
                try {
                    val fIn: InputStream = FileInputStream(file);
                    val zis = ZipInputStream(BufferedInputStream(fIn))
                    var ze: ZipEntry = zis.nextEntry
                    while (ze.name != "${Constants.BACKUP_FILE_NAME}.db") {
                        ze = zis.nextEntry
                    }

                    filePath = "${context.cacheDir}/${ze.name}"
                    val fOut = FileOutputStream(filePath)
                    val buffer = ByteArray(1024)
                    var length: Int = zis.read(buffer)
                    while (length > 0) {
                        fOut.write(buffer, 0, length)
                        length = zis.read(buffer)
                    }
                    fOut.flush()
                    fOut.close()
                    zis.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    filePath = null
                }
            }

            Log.i("RestoreTask", "filePath = $filePath")
            filePath
        }

    private suspend fun importData(manager: DbManager, openHelper: SQLiteOpenHelper): String? =
        withContext(Dispatchers.IO) {
            val provider = manager.provider()
            val db = openHelper.readableDatabase

            val categories = db.rawQuery("SELECT * FROM categories", null)
            if (categories != null) {
                val data = categories.map {
                    Category(
                        it longOf "_id",
                        it nullableStringOf "category_name",
                        it stringOf "guid"
                    )
                }

                val result = provider.deleteInsertAllCategories(data)
                categories.close()
                Log.i("RestoreTask", "restore categories ${result.size}")
            }

            val deleted = db.rawQuery("SELECT * FROM deleted", null)
            if (deleted != null) {
                val data = deleted.map {
                    Deleted(
                        type = it nullableStringOf "type",
                        value =  it nullableStringOf "value"
                    )
                }

                val result = provider.deleteInsertAllDeleted(data)
                deleted.close()
                Log.i("RestoreTask", "restore deleted ${result.size}")
            }

            val records = db.rawQuery("SELECT * FROM records", null)
            if (records != null) {
                val data = records.map {
                    Record(
                        it longOf "_id",
                        it nullableLongOf "date",
                        it nullableLongOf "color",
                        it nullableStringOf "note_title",
                        it nullableStringOf "note_text",
                        it nullableStringOf "category",
                        it longOf "upd_time",
                        it stringOf "guid"
                    )
                }

                val result = provider.deleteInsertAllRecords(data)
                records.close()
                Log.i("RestoreTask", "restore records ${result.size}")
            }

            val recordsAdd = db.rawQuery("SELECT * FROM records_add", null)
            if (recordsAdd != null) {
                val data = recordsAdd.map {
                    RecordsAdd(
                        it longOf "_id",
                        it nullableLongOf "record_id",
                        it nullableStringOf "type",
                        it nullableStringOf "value"
                    )
                }

                val result = provider.deleteInsertAllRecordsAdd(data)
                recordsAdd.close()
                Log.i("RestoreTask", "restore recordsAdd ${result.size}")
            }


            val tags = db.rawQuery("SELECT * FROM tags", null)
            if (tags != null) {
                val data = tags.map {
                    Tags(
                        it longOf "_id",
                        it nullableStringOf "tag_name",
                        it stringOf "guid"
                    )
                }

                val result = provider.deleteInsertAllTags(data)
                tags.close()
                Log.i("RestoreTask", "restore tags ${result.size}")
            }

            null
        }
}
