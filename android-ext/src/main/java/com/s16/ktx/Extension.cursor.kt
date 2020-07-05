package com.s16.ktx

import android.database.Cursor


inline fun Cursor.forEach(action: (Cursor) -> Unit) {
    if (moveToFirst()) {
        while (moveToNext()) action(this)
    }
}

inline fun <reified T> Cursor.map(action: (Cursor) -> T): List<T> {
    val arr: MutableList<T> = mutableListOf()
    if (moveToFirst()) {
        while (moveToNext()) arr.add(action(this))
    }
    return arr
}

operator fun Cursor.get(position: Int): Cursor? {
    if (moveToPosition(position)) return this
    return null
}

inline fun <reified T> Cursor.get(columnName: String): T? {
    val idx = getColumnIndex(columnName)

    if (idx > -1) {
        if (isNull(idx)) return null

        val klass = T::class.java
        return when {
            klass.isAssignableFrom(ByteArray::class.java) -> getBlob(idx) as T
            klass.isAssignableFrom(String::class.java) -> getString(idx) as T
            klass.isAssignableFrom(Short::class.java) -> getShort(idx) as T
            klass.isAssignableFrom(Int::class.java) -> getInt(idx) as T
            klass.isAssignableFrom(Long::class.java) -> getLong(idx) as T
            klass.isAssignableFrom(Float::class.java) -> getFloat(idx) as T
            klass.isAssignableFrom(Double::class.java) -> getDouble(idx) as T
            else -> null
        }
    }

    return null
}

infix fun Cursor.blobOf(columnName: String): ByteArray {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return ByteArray(0)
    return getBlob(idx) ?: ByteArray(0)
}

infix fun Cursor.stringOf(columnName: String): String {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return ""
    return getString(idx) ?: ""
}

infix fun Cursor.shortOf(columnName: String): Short {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return -1
    return getShort(idx)
}

infix fun Cursor.intOf(columnName: String): Int {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return -1
    return getInt(idx)
}

infix fun Cursor.longOf(columnName: String): Long {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return -1
    return getLong(idx)
}

infix fun Cursor.floatOf(columnName: String): Float {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return -1F
    return getFloat(idx)
}

infix fun Cursor.doubleOf(columnName: String): Double {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return (-1).toDouble()
    return getDouble(idx)
}

infix fun Cursor.nullableBlobOf(columnName: String): ByteArray? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getBlob(idx)
}

infix fun Cursor.nullableStringOf(columnName: String): String? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getString(idx)
}

infix fun Cursor.nullableShortOf(columnName: String): Short? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getShort(idx)
}

infix fun Cursor.nullableIntOf(columnName: String): Int? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getInt(idx)
}

infix fun Cursor.nullableLongOf(columnName: String): Long? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getLong(idx)
}

infix fun Cursor.nullableFloatOf(columnName: String): Float? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getFloat(idx)
}

infix fun Cursor.nullableDoubleOf(columnName: String): Double? {
    val idx = getColumnIndex(columnName)
    if (idx == -1) return null
    return getDouble(idx)
}