package com.s16.poetry.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.s16.utils.longOf
import com.s16.utils.map
import com.s16.utils.stringOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


abstract class RestoreTask(private val context: Context, private val file: File)
    : Runnable {

    abstract fun onCanceled(message: String)

    abstract fun onComplete()

    override fun run() {
        val manager = DbManager(context.applicationContext)

        runBlocking {
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

            if (file.extension == ".zip") {
                try {
                    val fIn: InputStream = FileInputStream(file);
                    val zis = ZipInputStream(BufferedInputStream(fIn))
                    val ze: ZipEntry = zis.nextEntry

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
                        it stringOf "category_name",
                        it stringOf "guid"
                    )
                }

                provider.insertAllCategories(data)
                categories.close()
            }


//            val tags = db.rawQuery("SELECT * FROM tags", null)
//            if (tags != null) {
//                val data = tags.map {
//                    Tags(
//                        it longOf "_id",
//                        it stringOf "tag_name",
//                        it stringOf "guid"
//                    )
//                }
//
//                provider.insertAllTags(data)
//                tags.close()
//            }

            null
        }
}
