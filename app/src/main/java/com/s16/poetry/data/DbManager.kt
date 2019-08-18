package com.s16.poetry.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.s16.roomasset.AssetSQLiteOpenHelperFactory
import java.io.File

// PRAGMA schema_version = 1;
// PRAGMA user_version = 0;

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

    fun getDatabaseFile(context: Context): File {
        val dbName = openHelper.databaseName
        return context.getDatabasePath(dbName)
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