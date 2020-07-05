package com.s16.data

import android.database.AbstractCursor
import android.database.Cursor
import android.os.Bundle
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.NullPointerException

class JSONArrayCursor(private val jsonArray: JSONArray) : AbstractCursor() {

    private val columnNames: MutableList<String> = mutableListOf()
    private var current: JSONObject? = null
    private var dataKey = DATA_KEY

    constructor(jsonObject: JSONObject, key: String = DATA_KEY)
            : this(getJSONArray(jsonObject, key)) {
        dataKey = key
    }

    init {
        if (jsonArray.length() > 0) {
            try {
                jsonArray.getJSONObject(0)
            } catch (e: JSONException) {
                null
            }?.let { obj ->
                for (i in obj.keys()) {
                    columnNames.add(i)
                }
            }
        }
    }

    override fun getCount(): Int = jsonArray.length()

    override fun getColumnNames(): Array<String> = columnNames.toTypedArray()

    @Throws(NullPointerException::class, JSONException::class)
    override fun getString(column: Int): String = current!!.let {
        val name = getColumnName(column)
        it.getString(name)
    }

    @Throws(NullPointerException::class, JSONException::class)
    override fun getLong(column: Int): Long = current!!.let {
        val name = getColumnName(column)
        it.getLong(name)
    }

    @Throws(NullPointerException::class, JSONException::class)
    override fun getShort(column: Int): Short = current!!.let {
        val name = getColumnName(column)
        it.getInt(name).toShort()
    }

    @Throws(NullPointerException::class, JSONException::class)
    override fun getInt(column: Int): Int = current!!.let {
        val name = getColumnName(column)
        it.getInt(name)
    }

    @Throws(NullPointerException::class, JSONException::class)
    override fun getFloat(column: Int): Float = current!!.let {
        val name = getColumnName(column)
        it.getDouble(name).toFloat()
    }

    @Throws(NullPointerException::class, JSONException::class)
    override fun getDouble(column: Int): Double = current!!.let {
        val name = getColumnName(column)
        it.getDouble(name)
    }

    override fun isNull(column: Int): Boolean = current?.let {
        val name = getColumnName(column)
        it.isNull(name)
    } ?: true

    override fun onMove(oldPosition: Int, newPosition: Int): Boolean {
        if (oldPosition != newPosition || current == null) {
            try {
                current = jsonArray.getJSONObject(newPosition)
            } catch (e: JSONException) {
                e.printStackTrace()
                current = null
            }
        }
        return current != null
    }

    override fun respond(extras: Bundle?): Bundle {
        val result = Bundle()
        result.putString(DATA_KEY, toJSONString())
        result.putInt("count", count)
        result.putStringArray("columns", columnNames.toTypedArray())
        result.putInt("position", position)
        result.putString("current", current?.toString())
        return result
    }

    override fun toString(): String = toJSONString()

    fun toJSONString() : String {
        val result = JSONObject()
        try {
            result.put(dataKey, jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return result.toString()
    }

    companion object {
        const val DATA_KEY = "d"

        private fun getJSONArray(jsonObject: JSONObject, key: String) : JSONArray {
            var array: JSONArray? = null
            if (jsonObject.has(key)) {
                try {
                    array = jsonObject.getJSONArray(key)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            if (array == null) {
                val tempObj = try {
                    jsonObject.getJSONObject(key)
                } catch (e: JSONException) {
                    null
                }

                if (tempObj != null) {
                    array = JSONArray()
                    array.put(tempObj)
                }
            }
            return array ?: JSONArray()
        }
    }
}

/**
 * Extensions
 */
fun JSONObject.toCursor() : Cursor = JSONArrayCursor(this)

fun JSONObject.toCursor(key: String) : Cursor = JSONArrayCursor(this, key)

fun JSONArray.toCursor() : Cursor = JSONArrayCursor(this)