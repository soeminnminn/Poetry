package com.s16.poetry.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DataProvider {

    @RawQuery
    suspend fun execRawQuery(query: SupportSQLiteQuery): Int

    @Transaction
    suspend fun truncateTable(tableName: String) {
        execRawQuery(SimpleSQLiteQuery("DELETE FROM $tableName"))
        execRawQuery(SimpleSQLiteQuery("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='$tableName'"))
    }

    // Insert All
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(data: List<Category>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeleted(data: List<Deleted>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecords(data: List<Record>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecordAdd(data: List<RecordsAdd>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTags(data: List<Tags>): List<Long>

    // Delete Insert
    @Transaction
    suspend fun deleteInsertAllCategories(data: List<Category>): List<Long> {
        execRawQuery(SimpleSQLiteQuery("DELETE FROM categories"))
        execRawQuery(SimpleSQLiteQuery("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='categories'"))
        return insertAllCategories(data)
    }

    @Transaction
    suspend fun deleteInsertAllDeleted(data: List<Deleted>): List<Long> {
        execRawQuery(SimpleSQLiteQuery("DELETE FROM deleted"))
        execRawQuery(SimpleSQLiteQuery("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='deleted'"))
        return insertAllDeleted(data)
    }

    @Transaction
    suspend fun deleteInsertAllRecords(data: List<Record>): List<Long> {
        execRawQuery(SimpleSQLiteQuery("DELETE FROM records"))
        execRawQuery(SimpleSQLiteQuery("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='records'"))
        return insertAllRecords(data)
    }

    @Transaction
    suspend fun deleteInsertAllRecordsAdd(data: List<RecordsAdd>): List<Long> {
        execRawQuery(SimpleSQLiteQuery("DELETE FROM records_add"))
        execRawQuery(SimpleSQLiteQuery("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='records_add'"))
        return insertAllRecordAdd(data)
    }

    @Transaction
    suspend fun deleteInsertAllTags(data: List<Tags>): List<Long> {
        execRawQuery(SimpleSQLiteQuery("DELETE FROM tags"))
        execRawQuery(SimpleSQLiteQuery("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='tags'"))
        return insertAllTags(data)
    }

    // Insert
    @Insert
    suspend fun insertCategory(data: Category): Long

    @Insert
    suspend fun insertDeleted(data: Deleted): Long

    @Insert
    suspend fun insertRecord(data: Record): Long

    @Insert
    suspend fun insertRecordAdd(data: RecordsAdd): Long

    @Insert
    suspend fun insertTag(data: Tags): Long

    // Update
    @Update
    suspend fun updateCategory(data: Category)

    @Update
    suspend fun updateDeleted(data: Deleted)

    @Update
    suspend fun updateRecord(data: Record)

    @Update
    suspend fun updateRecordAdd(data: RecordsAdd)

    @Update
    suspend fun updateTag(data: Tags)

    // delete
    @Query("DELETE FROM records WHERE _id IS :id")
    suspend fun deleteRecord(id: Long)

    @Query("DELETE FROM records_add WHERE record_id IS :recordId")
    suspend fun deleteRecordAdd(recordId: Long)

    @Transaction
    suspend fun deleteRecordAll(vararg recordId: Long) {
        recordId.forEach { id ->
            deleteRecord(id)
            deleteRecordAdd(id)
        }
    }

    // save
    @Transaction
    suspend fun saveRecord(record: Record, recordAdd: List<RecordsAdd>): Long {
        val id: Long = if (record.id != 0L) {
            updateRecord(record)
            record.id
        } else {
            insertRecord(record)
        }

        deleteRecordAdd(id)
        if (recordAdd.isNotEmpty()) {
            recordAdd.forEach {
                it.recordId = id
                Log.i("saveRecord", "${it.value}")
                insertRecordAdd(it)
            }
        }

        return id
    }

    // Select
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM tags")
    suspend fun getAllTags(): List<Tags>

    @Query("SELECT * FROM deleted")
    suspend fun getAllDeleted(): List<Deleted>

    @Query("SELECT * FROM records_add")
    suspend fun getAllRecordAdd(): List<RecordsAdd>

    @Query("SELECT * FROM records")
    suspend fun getAllRecords(): List<Record>

    @Query("""SELECT categories.* FROM categories
        LEFT JOIN deleted ON categories.category_name = deleted.value AND deleted.type = 'Category'
        WHERE deleted.value IS NULL""")
    fun listCategories(): LiveData<List<Category>>

    @Query("""SELECT tags.* FROM tags
        LEFT JOIN deleted ON tags.tag_name = deleted.value AND deleted.type = 'Tag'
        WHERE deleted.value IS NULL""")
    fun listTags(): LiveData<List<Tags>>

    @Query("""SELECT records.* FROM records
        LEFT JOIN deleted ON records.note_title = deleted.value AND deleted.type = 'Record'
        WHERE deleted.value IS NULL
        ORDER BY records.date DESC""")
    fun listPagedRecords(): DataSource.Factory<Int, Record>

    @Query("""SELECT records.* FROM records
        LEFT JOIN deleted ON records.note_title = deleted.value AND deleted.type = 'Record'
        WHERE deleted.value IS NULL AND category IS :category
        ORDER BY date DESC""")
    fun listPagedRecordsByCategory(category: String): DataSource.Factory<Int, Record>

    @Query("""SELECT records.*, records_add.type, records_add.value FROM records
        LEFT JOIN records_add ON records._id = records_add.record_id
        WHERE records._id IS :id""")
    fun getDetailRecord(id: Long): LiveData<DetailRecord>
}