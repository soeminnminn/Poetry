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

    @Query("SELECT * FROM categories")
    fun listCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM tags")
    fun listTags(): LiveData<List<Tags>>

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun listPagedRecords(): DataSource.Factory<Int, Record>

    @Query("SELECT * FROM records WHERE category IS :category ORDER BY date DESC")
    fun listPagedRecordsByCategory(category: String): DataSource.Factory<Int, Record>

    @Query("""SELECT records.*, records_add.type, records_add.value FROM records
        LEFT JOIN records_add ON records._id = records_add.record_id
        WHERE records._id IS :id""")
    fun getDetailRecord(id: Long): LiveData<DetailRecord>
}