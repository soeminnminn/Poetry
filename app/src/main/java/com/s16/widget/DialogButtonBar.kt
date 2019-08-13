package com.s16.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils

class DialogButtonBar : LinearLayout {

    private val colorAccent: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true)
            return typedValue.data
        }

    private val colorControlHighlight: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorControlHighlight, typedValue, true)
            return typedValue.data
        }

    private val selectableBackground: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.selectableItemBackground, typedValue, true)
            return typedValue.resourceId
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
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
        setPadding(dpToPixel(context, 12), dpToPixel(context, 10), dpToPixel(context, 12), dpToPixel(context, 10))

        val space = Space(context).apply {
            visibility = View.INVISIBLE
        }
        val spaceParams = LinearLayout.LayoutParams(0, 0).apply {
            weight = 1f
        }
        addView(space, spaceParams)

        addNegativeButton()
        addPositiveButton()
    }

    private fun addNegativeButton() {
        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        buttonParams.rightMargin = dpToPixel(context, 4)

        val button = DialogButton(context).apply {
            setText(android.R.string.cancel)
            setBackgroundResource(selectableBackground)
            rippleColor = colorControlHighlight
            layoutParams = buttonParams
        }

        addView(button)
    }

    private fun addPositiveButton() {
        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        val button = DialogButton(context).apply {
            setText(android.R.string.ok)
            setTextColor(colorAccent)
            setBackgroundResource(selectableBackground)
            rippleColor = ColorUtils.setAlphaComponent(colorAccent, 60)
            layoutParams = buttonParams
        }

        addView(button)
    }

    class DialogButton: AppCompatTextView {

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
            gravity = Gravity.CENTER
            isClickable = true
            minEms = 1
            maxLines = 1
            minWidth = dpToPixel(context,52)
            isAllCaps = true
            setTypeface(typeface, Typeface.BOLD)
            setPadding(dpToPixel(context,12), dpToPixel(context,8), dpToPixel(context,12), dpToPixel(context,8))
        }

        var rippleColor: Int = 0
            set(color) {
                field = color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val sd = ShapeDrawable()
                    val radii = FloatArray(8)
                    radii.fill(12f)
                    sd.shape = RoundRectShape(radii, null, null)
                    background = RippleDrawable(ColorStateList.valueOf(color), null, sd)
                }
            }
    }

    companion object {
        fun dpToPixel(context: Context, dp: Int): Int {
            val metrics = context.resources.displayMetrics
            val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            return px.toInt()
        }
    }
}