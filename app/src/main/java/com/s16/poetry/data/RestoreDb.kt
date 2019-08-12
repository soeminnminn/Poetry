package com.s16.poetry.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.s16.utils.longOf
import com.s16.utils.map
import com.s16.utils.stringOf
import java.io.*
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class RestoreDb(val context: Context, name: String): SQLiteOpenHelper(context, name, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA user_version = 0;")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun close() {
        if (readableDatabase.isOpen)
            readableDatabase.close()
        super.close()
    }

    fun run() {
        val db = readableDatabase
        val provider = DbManager(context.applicationContext).provider()

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

    companion object {
        fun of(context: Context, file: File): RestoreDb {
            var filePath = "${context.cacheDir}/${file.name}"
            try {
                val fIn: InputStream = FileInputStream(file);
                if (file.extension == ".zip") {
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

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return RestoreDb(context, filePath)
        }
    }
}
