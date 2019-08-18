package com.s16.poetry.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DataProvider {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategories(data: List<Category>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTags(data: List<Tags>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRecords(data: List<Record>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRecordAdd(data: List<RecordsAdd>): List<Long>

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