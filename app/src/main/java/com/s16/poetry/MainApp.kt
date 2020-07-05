package com.s16.poetry

import android.app.Application
import androidx.preference.PreferenceManager
import com.s16.poetry.utils.ThemeManager

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        ThemeManager.setTheme(preferences.getString(Constants.PREFS_SELECT_THEME, ""))
   }
}