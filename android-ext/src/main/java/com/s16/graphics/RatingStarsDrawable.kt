package com.s16.graphics

import android.R.attr
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.TypedValue


class RatingStarsDrawable(baseIntrinsicSize: Int) : Drawable() {

    enum class Orientation {
        AUTO,
        VERTICAL,
        HORIZONTAL
    }

    private var mIntrinsicHeight: Int = baseIntrinsicSize
    private var mIntrinsicWidth: Int = mIntrinsicHeight * 5

    private val mPaintHot = Paint().apply {
        style = Paint.Style.FILL
        color = DEFAULT_HOT_COLOR
    }

    private val mPaintClear = Paint().apply {
        style = Paint.Style.FILL
        color = DEFAULT_COLOR
    }

    private val mShapeArr =
        arrayOf(StarShape(), StarShape(), StarShape(), StarShape(), StarShape())

    constructor(context: Context) :
            this(getDefaultIntrinsicSize(context)) {}

    var orientation: Orientation = Orientation.AUTO
        set(value) {
            field = value
            invalidateSelf()
        }

    var hotColor: Int = 0
        set(value) {
            field = value
            mPaintHot.color = value
            invalidateSelf()
        }

    var fillColor: Int = 0
        set(value) {
            field = value
            mPaintClear.color = value
            invalidateSelf()
        }

    var radiusRatio: Float = 0.5f
        set(value) {
            field = value
            invalidateSelf()
        }

    var innerRadiusRatio: Float = 0.25f
        set(value) {
            field = value
            invalidateSelf()
        }

    var numberOfPoint: Int = 5
        set(value) {
            field = value
            invalidateSelf()
        }

    var value: Int = 0
        set(value) {
            field = value
            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        onBoundsChange(bounds)

        for (i in 0..4) {
            val shape = mShapeArr[i]
            if (i < value) {
                canvas.drawPath(shape.path, mPaintHot)
            } else {
                canvas.drawPath(shape.path, mPaintClear)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        mPaintHot.alpha = alpha
        mPaintClear.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaintHot.colorFilter = colorFilter
        mPaintClear.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun getIntrinsicWidth(): Int = when(orientation) {
        Orientation.VERTICAL -> mIntrinsicHeight
        else -> mIntrinsicWidth
    }

    override fun getIntrinsicHeight(): Int = when(orientation) {
        Orientation.VERTICAL -> mIntrinsicWidth
        else -> mIntrinsicHeight
    }

    override fun onBoundsChange(bounds: Rect) {
        measureShape(bounds.width(), bounds.height())
    }

    private fun measureShape(width: Int, height: Int) {
        if (width == 0 || height == 0) return
        var w = 0f
        var h = 0f
        if (orientation == Orientation.HORIZONTAL) {
            w = width / 5.0f
            h = height.toFloat()
        } else if (orientation == Orientation.VERTICAL) {
            w = width.toFloat()
            h = height / 5.0f
        } else {
            if (width > height) {
                w = width / 5.0f
                h = height.toFloat()
            } else {
                w = width.toFloat()
                h = height / 5.0f
            }
        }
        var left = 0f
        var top = 0f
        for (i in 0..4) {
            val x = left + w / 2.0f
            val y = top + h / 2.0f
            var radius: Float
            var innerRadius: Float
            if (w > h) {
                radius = h * radiusRatio
                innerRadius = h * innerRadiusRatio
            } else {
                radius = w * radiusRatio
                innerRadius = w * innerRadiusRatio
            }
            val shape = mShapeArr[i]
            shape.setStar(x, y, radius, innerRadius, numberOfPoint)
            val bounds = RectF(x, y, x + w, y + h)
            shape.rotate(bounds, -90.0f)
            if (orientation === Orientation.HORIZONTAL) {
                left += w
            } else if (orientation === Orientation.VERTICAL) {
                top += h
            } else {
                if (width > height) {
                    left += w
                } else {
                    top += h
                }
            }
        }
    }

    private class StarShape {
        val path = Path()

        fun setStar(x: Float, y: Float, radius: Float, innerRadius: Float, numOfPt: Int) {
            val section = 2.0 * Math.PI / numOfPt
            path.run {
                reset()
                moveTo(
                    (attr.x + attr.radius * Math.cos(0.0)).toFloat(),
                    (attr.y + attr.radius * Math.sin(0.0)).toFloat()
                )
                lineTo(
                    (attr.x + attr.innerRadius * Math.cos(0 + section / 2.0)).toFloat(),
                    (attr.y + attr.innerRadius * Math.sin(0 + section / 2.0)).toFloat()
                )

                for (i in 1 until numOfPt) {
                    lineTo(
                        (attr.x + attr.radius * Math.cos(section * i)).toFloat(),
                        (attr.y + attr.radius * Math.sin(section * i)).toFloat()
                    )
                    lineTo(
                        (attr.x + attr.innerRadius * Math.cos(section * i + section / 2.0)).toFloat(),
                        (attr.y + attr.innerRadius * Math.sin(section * i + section / 2.0)).toFloat()
                    )
                }
                close()
            }
        }

        fun rotate(bounds: RectF, angle: Float) {
            val matrix = Matrix()
            path.computeBounds(bounds, true)
            matrix.postRotate(
                attr.angle.toFloat(),
                (bounds.right + bounds.left) / 2,
                (bounds.bottom + bounds.top) / 2
            )
            path.transform(matrix)
        }
    }

    companion object {
        private const val DEFAULT_HOT_COLOR = Color.BLUE
        private const val DEFAULT_COLOR = Color.GRAY
        private const val DEFAULT_INTRINSIC_SIZE = 8f

        private fun getDefaultIntrinsicSize(context: Context) : Int {
            val size = when (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
                0 -> DEFAULT_INTRINSIC_SIZE
                1 -> (DEFAULT_INTRINSIC_SIZE + 2.0f)
                2 -> (DEFAULT_INTRINSIC_SIZE * 1.5f + 2.0f)
                3 -> (DEFAULT_INTRINSIC_SIZE * 2f)
                4 -> (DEFAULT_INTRINSIC_SIZE * 3f)
                else -> (DEFAULT_INTRINSIC_SIZE * 4f)
            }

            val dm = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, dm).toInt()
        }
    }
}