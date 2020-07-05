package com.s16.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.s16.android.R

class AspectFrameLayout : FrameLayout {
    private var mAspectRatioX = 4.0f
    private var mAspectRatioY = 3.0f
    private var mAspectRatio = 0.0f

    fun setAspectRatioX(ratioX: Float) {
        mAspectRatioX = ratioX
        val ratio = mAspectRatioX / mAspectRatioY
        if (mAspectRatio != ratio) {
            mAspectRatio = ratio
            requestLayout()
        }
    }

    fun setAspectRatioY(ratioY: Float) {
        mAspectRatioY = ratioY
        val ratio = mAspectRatioX / mAspectRatioY
        if (mAspectRatio != ratio) {
            mAspectRatio = ratio
            requestLayout()
        }
    }

    fun setAspectRatio(aspectRatio: Float) {
        if (mAspectRatio != aspectRatio) {
            mAspectRatio = aspectRatio
            requestLayout()
        }
    }

    constructor(context: Context)
            : super(context) {
        initialize(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs) {
        initialize(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {
        initialize(context, attrs, defStyle)
    }

    private fun initialize(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AspectFrameLayout)
        mAspectRatioX = a.getFloat(R.styleable.AspectFrameLayout_aflAspectRatioX, 4.0f)
        mAspectRatioY = a.getFloat(R.styleable.AspectFrameLayout_aflAspectRatioY, 3.0f)
        a.recycle()
        mAspectRatio = mAspectRatioX / mAspectRatioY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec

        if (isInEditMode) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // Target aspect ratio will be < 0 if it hasn't been set yet.  In that case,
        // we just use whatever we've been handed.
        if (mAspectRatio > 0) {
            var initialWidth = MeasureSpec.getSize(widthMeasureSpec)
            var initialHeight = MeasureSpec.getSize(heightMeasureSpec)

            // factor the padding out
            val horizPadding = paddingLeft + paddingRight
            val vertPadding = paddingTop + paddingBottom
            initialWidth -= horizPadding
            initialHeight -= vertPadding

            if (initialWidth <= 0) {
                initialWidth = (initialHeight * mAspectRatio).toInt()
            } else if (initialHeight <= 0) {
                initialHeight = (initialWidth / mAspectRatio).toInt()
            } else {
                val viewAspectRatio = (initialWidth / initialHeight).toDouble()
                val aspectDiff = mAspectRatio / viewAspectRatio - 1
                if (Math.abs(aspectDiff) >= 0.01) {
                    if (aspectDiff > 0) { // limited by narrow width; restrict height
                        initialHeight = (initialWidth / mAspectRatio).toInt()
                    } else { // limited by short height; restrict width
                        initialWidth = (initialHeight * mAspectRatio).toInt()
                    }
                }
            }

            initialWidth += horizPadding
            initialHeight += vertPadding
            widthSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY)
            heightSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthSpec, heightSpec)
    }
}
