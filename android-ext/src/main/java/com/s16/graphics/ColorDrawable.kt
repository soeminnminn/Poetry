package com.s16.graphics

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics

class ColorDrawable(private val context: Context, color: Int, private val sizeDp: Float) :
    Drawable() {

    enum class Shape {
        CIRCLE,
        RECTANGLE
    }

    private val colorPaint = Paint(Paint.ANTI_ALIAS_FLAG and Paint.DITHER_FLAG).also { p ->
        p.color = color
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG and Paint.DITHER_FLAG).also { p ->
        p.style = Paint.Style.FILL
        p.color = SHADOW_COLOR
    }

    var shape: Shape = Shape.RECTANGLE
        set(value) {
            field = value
            invalidateSelf()
        }

    var shadow: Boolean = true
        set(value) {
            field = value
            invalidateSelf()
        }

    var isLightTheme: Boolean = false
        set(value) {
            field = value
            invalidateSelf()
        }

    private fun dpToPixel(dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()
        val bounds = RectF()
        bounds.set(getBounds())

        if (shadow) {
            val sPaint = shadowPaint
            val sColors = if (isLightTheme) SHADOW_COLORS_LIGHT else SHADOW_COLORS

            for (i in SHADOW_PADDING.indices) {
                sPaint.color = sColors[i]
                val padding = SHADOW_PADDING[i]
                bounds.bottom -= (padding * 2)
                bounds.right -= (padding * 2)
                canvas.translate(padding.toFloat(), padding.toFloat())

                if (shape == Shape.RECTANGLE) {
                    canvas.drawRect(bounds, sPaint)
                } else {
                    canvas.drawOval(bounds, sPaint)
                }
            }

            canvas.restoreToCount(saveCount)
            bounds.set(getBounds())
            bounds.bottom -= (SHADOW_SIZE * 2)
            bounds.right -= (SHADOW_SIZE * 2)
            canvas.translate(SHADOW_SIZE, SHADOW_SIZE / 2)
        }

        if (shape == Shape.CIRCLE) {
            canvas.drawOval(bounds, colorPaint)
        } else {
            canvas.drawRect(bounds, colorPaint)
        }

        canvas.restoreToCount(saveCount)
    }

    override fun setAlpha(alpha: Int) {
        colorPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        colorPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = if (colorPaint.alpha < 255) {
        PixelFormat.TRANSLUCENT
    } else {
        PixelFormat.OPAQUE
    }

    override fun getIntrinsicWidth(): Int = dpToPixel(sizeDp).toInt()

    override fun getIntrinsicHeight(): Int = dpToPixel(sizeDp).toInt()

    override fun getPadding(padding: Rect): Boolean {
        padding.set(SHADOW_SIZE.toInt(), (SHADOW_SIZE/2).toInt(), SHADOW_SIZE.toInt(), (SHADOW_SIZE/2).toInt())
        return true
    }

    companion object {
        const val SHADOW_SIZE = 15.0f
        const val SHADOW_COLOR = 0x0

        val SHADOW_COLORS = arrayOf(
            0x05757575, 0x06757575, 0x07757575, 0x08757575,
            0x09757575, 0x10757575, 0x11757575, 0x12757575,
            0x13757575, 0x14757575
        )

        val SHADOW_COLORS_LIGHT = arrayOf(
            0x08000000, 0x09000000, 0x10000000, 0x11000000,
            0x12000000, 0x13000000, 0x14000000, 0x15000000,
            0x16000000, 0x17000000
        )

        val SHADOW_PADDING = arrayOf(3, 2, 2, 1, 1, 1, 1, 1, 1, 1)
    }
}