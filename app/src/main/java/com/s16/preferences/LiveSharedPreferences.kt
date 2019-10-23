package com.s16.preferences

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class LiveSharedPreferences<T> constructor(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) {
            value = getValueFromPreferences(key, defValue)
        }
    }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()

        value = getValueFromPreferences(key, defValue)
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class IntLiveSharedPreference(private val sharedPrefs: SharedPreferences, key: String, defValue: Int) :
    LiveSharedPreferences<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int = sharedPrefs.getInt(key, defValue)
}

class StringLiveSharedPreference(private val sharedPrefs: SharedPreferences, key: String, defValue: String) :
    LiveSharedPreferences<String>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String): String = sharedPrefs.getString(key, defValue) ?: defValue
}

class BooleanLiveSharedPreference(private val sharedPrefs: SharedPreferences, key: String, defValue: Boolean) :
    LiveSharedPreferences<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defValue)
}

class FloatLiveSharedPreference(private val sharedPrefs: SharedPreferences, key: String, defValue: Float) :
    LiveSharedPreferences<Float>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Float): Float = sharedPrefs.getFloat(key, defValue)
}

class LongLiveSharedPreference(private val sharedPrefs: SharedPreferences, key: String, defValue: Long) :
    LiveSharedPreferences<Long>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Long): Long = sharedPrefs.getLong(key, defValue)
}

class StringSetLiveSharedPreference(private val sharedPrefs: SharedPreferences, key: String, defValue: Set<String>) :
    LiveSharedPreferences<Set<String>>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Set<String>): Set<String> = sharedPrefs.getStringSet(key, defValue) ?: defValue
}
