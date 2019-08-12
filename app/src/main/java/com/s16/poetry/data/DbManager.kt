package com.s16.poetry.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.s16.roomasset.AssetSQLiteOpenHelperFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

// PRAGMA schema_version = 1;
// PRAGMA user_version = 0;

// https://stackoverflow.com/questions/50987119/backup-room-database

@Database(entities = [
    Category::class,
    Deleted::class,
    Record::class,
    RecordsAdd::class,
    Tags::class],
    version = 1,
    exportSchema = false)
abstract class DbManager: RoomDatabase() {
    abstract fun provider(): DataProvider

    @Throws(IOException::class)
    private fun copyFile(inFile: File, outFile: File) {
        val fis = FileInputStream(inFile)
        val output = FileOutputStream(outFile)

        val buffer = ByteArray(1024)
        var length: Int = fis.read(buffer)
        while (length > 0) {
            output.write(buffer, 0, length)
            length = fis.read(buffer)
        }
        output.flush()
        output.close()
        fis.close()
    }

    fun backup(context: Context, outFile: File) {
        val dbName = openHelper.databaseName
        close()
        val db = context.getDatabasePath(dbName)
//        val dbShm = File(db.parent, "$dbName-shm")
//        val dbWal = File(db.parent, "$dbName-wal")
        try {
            copyFile(db, outFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun restore(context: Context, inFile: File) {
        val dbName = openHelper.databaseName
        close()
        val db = context.getDatabasePath(dbName)
        try {
            copyFile(inFile, db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile private var instance: DbManager? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            DbManager::class.java,
            "diary.db")
            .fallbackToDestructiveMigration()
            .openHelperFactory(AssetSQLiteOpenHelperFactory("database"))
            .build()
    }
}