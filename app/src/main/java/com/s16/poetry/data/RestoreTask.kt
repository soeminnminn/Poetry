package com.s16.poetry.data

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import com.s16.utils.longOf
import com.s16.utils.map
import com.s16.utils.stringOf
import java.io.*
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class RestoreRunnable(context: Context, private val file: File)
    : ContextWrapper(context), Runnable {

    private fun createOpenHelper(name: String): SQLiteOpenHelper {
        return object: SQLiteOpenHelper(this, name, null, 1) {
            override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL("PRAGMA user_version = 0;")
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            }
        }
    }

    private fun import(db: SQLiteDatabase) {
        val provider = DbManager(applicationContext).provider()

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


        val tags = db.rawQuery("SELECT * FROM tags", null)
        if (tags != null) {
            val data = tags.map {
                Tags(
                    it longOf "_id",
                    it stringOf "tag_name",
                    it stringOf "guid"
                )
            }

            provider.insertAllTags(data)
            tags.close()
        }
    }

    override fun run() {
        var filePath = "$cacheDir/${file.name}"
        try {
            val fIn: InputStream = FileInputStream(file);
            if (file.extension == ".zip") {
                val zis = ZipInputStream(BufferedInputStream(fIn))
                val ze: ZipEntry = zis.nextEntry

                filePath = "$cacheDir/${ze.name}"
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

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val openHelper = createOpenHelper(filePath)

        import(openHelper.readableDatabase)
        openHelper.close()
    }

    companion object {
        fun start(context: Context, file: File) {
            val task = RestoreRunnable(context, file)
            AsyncTask.execute(task)
        }
    }
}
