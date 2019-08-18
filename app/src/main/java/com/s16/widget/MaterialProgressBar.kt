package com.s16.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.widget.ProgressBar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

class MaterialProgressBar: ProgressBar {

    private val ATTRS = intArrayOf(
        android.R.attr.minWidth,
        android.R.attr.minHeight,
        android.R.attr.maxWidth,
        android.R.attr.maxHeight)

    private val colorPrimary: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
            return typedValue.data
        }

    constructor(context: Context) : super(context) {
        initialize(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }

    private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, ATTRS)
        val baseSize = a.getDimension(0, 0f)
        a.recycle()

        val largeSize = dpToPixel(context, CIRCLE_DIAMETER_LARGE)
        // val mediumSize = dpToPixel(context, CIRCLE_DIAMETER)
        val smallSize = dpToPixel(context, CIRCLE_DIAMETER_SMALL)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val progressDrawable = CircularProgressDrawable(context)
            progressDrawable.setColorSchemeColors(colorPrimary)
            when(baseSize) {
                largeSize -> {
                    progressDrawable.centerRadius = dpToPixel(context, CENTER_RADIUS_LARGE)
                    progressDrawable.strokeWidth = dpToPixel(context, STROKE_WIDTH_LARGE)
                }
                smallSize -> {
                    progressDrawable.centerRadius = dpToPixel(context, CENTER_RADIUS_SMALL)
                    progressDrawable.strokeWidth = dpToPixel(context, STROKE_WIDTH_SMALL)
                }
                else -> {
                    progressDrawable.centerRadius = dpToPixel(context, CENTER_RADIUS)
                    progressDrawable.strokeWidth = dpToPixel(context, STROKE_WIDTH)
                }
            }

            indeterminateDrawable = progressDrawable
        }
    }

    companion object {
        // Maps to ProgressBar.Small style
        private const val CIRCLE_DIAMETER_SMALL = 16
        private const val CENTER_RADIUS_SMALL = 5
        private const val STROKE_WIDTH_SMALL = 2

        // Maps to ProgressBar default style
        // private const val CIRCLE_DIAMETER = 48
        private const val CENTER_RADIUS = 17
        private const val STROKE_WIDTH = 4

        // Maps to ProgressBar.Large style
        private const val CIRCLE_DIAMETER_LARGE = 76
        private const val CENTER_RADIUS_LARGE = 28
        private const val STROKE_WIDTH_LARGE = 6

        private fun dpToPixel(context: Context, dp: Int): Float {
            val metrics = context.resources.displayMetrics
            return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}