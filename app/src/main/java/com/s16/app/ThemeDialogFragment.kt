package com.s16.app

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.DialogFragment
import com.s16.poetry.R

abstract class ThemeDialogFragment: DialogFragment() {

    private val isLightTheme: Boolean
        get() {
            val typedValue = TypedValue()
            context!!.theme.resolveAttribute(com.google.android.material.R.attr.isLightTheme, typedValue, false)
            return typedValue.data != 0
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLightTheme) {
            setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_Light_NoTitle)
        } else {
            setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog_NoTitle)
        }
    }
}