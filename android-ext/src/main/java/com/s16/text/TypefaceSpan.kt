package com.s16.text

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * Custom Typeface span class
 */
class TypefaceSpan constructor(inputTypeface: Typeface?) : MetricAffectingSpan() {
    private var mTypeface: Typeface? = null

    init {
        if (inputTypeface != null)
            mTypeface = inputTypeface
    }

    /**
     * Passes updateMeasureState through to the underlying [MetricAffectingSpan].
     */
    override fun updateMeasureState(textPaint: TextPaint) {
        if (mTypeface != null) {
            textPaint.typeface = mTypeface
            textPaint.flags = textPaint.flags or Paint.FILTER_BITMAP_FLAG
        }
    }

    /**
     * Passes updateDrawState through to the underlying [MetricAffectingSpan].
     */
    override fun updateDrawState(tp: TextPaint?) {
        if (tp != null && mTypeface != null) {
            tp.typeface = mTypeface
            tp.flags = tp.flags or Paint.FILTER_BITMAP_FLAG
        }
    }
}