package com.s16.app

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.s16.poetry.R


abstract class ThemeActivity: AppCompatActivity() {

     private val isLightTheme: Boolean
        get() {
            val typedValue = TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.isLightTheme, typedValue, false)
            return typedValue.data != 0
        }

    fun updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            if (isLightTheme) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = ContextCompat.getColor(this, R.color.background_holo_light)
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = ContextCompat.getColor(this, R.color.background_holo_dark)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val flags = window.decorView.systemUiVisibility
            if (isLightTheme) {
                window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                window.navigationBarColor = ContextCompat.getColor(this, R.color.background_holo_light)
            } else {
                window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                window.navigationBarColor = ContextCompat.getColor(this, R.color.background_holo_dark)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val blackMode = Settings.System.getInt(contentResolver, "oem_black_mode", -1)
        Log.i("NightMode", "$blackMode")

        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                // Night mode is not active, we're in day time
                Log.i("NightMode", "no")
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                // Night mode is active, we're at night!
                Log.i("NightMode", "yes")
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                // We don't know what mode we're in, assume notnight
                Log.i("NightMode", "undefined")
            }
            else -> {
                Log.i("NightMode", "not found")
            }
        }
    }
}