package com.s16.poetry

import android.content.Context
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.text.Spannable
import android.text.SpannableString
import com.s16.view.TypefaceSpan

object TypeFaceUtil {
    @Volatile
    private var mmTypeFace: Typeface? = null
    private fun getMMTypeFace(context: Context): Typeface =
        mmTypeFace ?: Typeface.createFromAsset(context.assets, "fonts/mmrtext.ttf")

    @Volatile
    private var mmTypeFaceBold: Typeface? = null
    private fun getMMTypeFaceBold(context: Context): Typeface =
        mmTypeFaceBold ?: Typeface.createFromAsset(context.assets, "fonts/mmrtextb.ttf")

    fun getPreferencesTypeFace(context: Context, bold: Boolean = false): Typeface {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var fontFace: Typeface = Typeface.DEFAULT
        if (sharedPreferences.getBoolean(Constants.PREFS_MMFONT, true)) {
            fontFace = if (bold) getMMTypeFaceBold(context) else getMMTypeFace(context)
        } else if (bold) {
            fontFace = Typeface.DEFAULT_BOLD
        }
        return fontFace
    }

    fun makePreferencesTypeFaceSpan(context: Context, text: String?, bold: Boolean = false)
            : SpannableString? = text?.let {
        val fontFace = getPreferencesTypeFace(context, bold)
        SpannableString(it).apply {
            setSpan(TypefaceSpan(fontFace), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}