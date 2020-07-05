package com.s16.poetry.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.s16.poetry.Constants

object ThemeManager {
    fun getIconTintColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.iconTint, typedValue, true)
        return typedValue.data
    }

    fun setTheme(selectedOption: String?) {
        val mode = when(selectedOption) {
            Constants.PREFS_THEMES_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Constants.PREFS_THEMES_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Constants.PREFS_THEMES_BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getThemedIcon(context: Context, @DrawableRes id: Int) : Drawable? {
        val tintColor = getIconTintColor(context)
        val drawable = ContextCompat.getDrawable(context, id)
        if (drawable != null) {
            DrawableCompat.setTint(drawable, tintColor)
        }
        return drawable
    }
}