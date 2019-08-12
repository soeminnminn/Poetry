package com.s16.poetry.data

import androidx.room.*

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long,
    @ColumnInfo(name = "category_name")
    var name: String?,
    var guid: String = ""
)

@Entity(tableName = "deleted")
data class Deleted(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long,
    var type: String?,
    var value: String?
)

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long,
    var date: Long?,
    var hours: String?,
    var minutes: String?,
    var seconds: String?,
    var day: String?,
    var month: String?,
    var year: String?,
    @ColumnInfo(name="note_title")
    var title: String?,
    @ColumnInfo(name="note_text")
    var text: String?,
    var category: String?,
    @ColumnInfo(name="upd_time")
    var updTime: Long = 0,
    var guid: String = ""
)

@Entity(tableName = "records_add")
data class RecordsAdd(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long?,
    @ColumnInfo(name = "record_id")
    var recordId: Long?,
    var type: String?,
    var value: String?
)

@Entity(tableName = "tags")
data class Tags(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long,
    @ColumnInfo(name = "tag_name")
    var name: String?,
    var guid: String = ""
)

data class DetailRecord(
    @Embedded
    var record: Record? = null,

    @Relation(parentColumn = "_id", entityColumn = "record_id", entity = RecordsAdd::class, projection = ["type", "value"])
    var recordAdd: List<RecordsAdd> = listOf()
) {
    val id: Long?
        get() = record?.id

    val date: Long?
        get() = record?.date

    val title: String?
        get() = record?.title

    val text: String?
        get() = record?.text

    val category: String?
        get() = record?.category

    val updTime: Long?
        get() = record?.updTime

    val guid: String?
        get() = record?.guid

    val tags: List<String>
        get() {
            return recordAdd.filter {
                it.type == "Tag"
            }.map {
                it.value ?: ""
            }
        }
}