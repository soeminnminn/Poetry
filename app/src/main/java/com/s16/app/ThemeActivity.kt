package com.s16.app

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.s16.poetry.Constants
import com.s16.poetry.R


abstract class ThemeActivity: AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
     private var previousDarkTheme: Boolean = false

     private val systemThemeMode: Int
        get() {
            /**
             * 0 = light
             * 1 = dark
             * 2 = default
             */
            return Settings.System.getInt(contentResolver, "oem_black_mode", -1)
        }

     private val uiNightMode: Int
        get() {
            return when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> 0
                Configuration.UI_MODE_NIGHT_YES -> 1
                Configuration.UI_MODE_NIGHT_UNDEFINED -> 2
                else -> 2
            }
        }

     private val darkThemeEnabled: Boolean
        get() {
            val userSelect = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.PREFS_SELECT_THEME, "2")
            return when(userSelect) {
                "0" -> false
                "1" -> true
                else -> (systemThemeMode == 1 || uiNightMode == 1)
            }
        }

    val isLightTheme: Boolean
        get() {
            val typedValue = TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.isLightTheme, typedValue, false)
            return typedValue.data != 0
        }

    fun updateSystemUiColor() {
        val lightTheme = isLightTheme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            if (lightTheme) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                // window.statusBarColor = ContextCompat.getColor(this, R.color.background_holo_light)
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                // window.statusBarColor = ContextCompat.getColor(this, R.color.background_holo_dark)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val flags = window.decorView.systemUiVisibility
            if (lightTheme) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                window.navigationBarColor = ContextCompat.getColor(this, R.color.background_holo_light)
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                window.navigationBarColor = ContextCompat.getColor(this, R.color.background_holo_dark)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousDarkTheme = darkThemeEnabled
        setTheme(if (previousDarkTheme) R.style.AppTheme_NoActionBar else R.style.AppTheme_Light_NoActionBar)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)

        super.onDestroy()
    }

//    override fun onResume() {
//        super.onResume()
//        if (previousDarkTheme != darkThemeEnabled) recreate()
//    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        if (key == Constants.PREFS_SELECT_THEME) {
            when(sharedPreferences?.getString(key, "2") ?: "2") {
                "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            recreate()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("previousDarkTheme", previousDarkTheme)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        previousDarkTheme = savedInstanceState?.getBoolean("previousDarkTheme", false) ?: false
    }
}