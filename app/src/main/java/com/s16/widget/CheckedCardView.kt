package com.s16.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.annotation.Nullable
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import com.s16.poetry.R
import androidx.appcompat.content.res.AppCompatResources

// https://github.com/material-components/material-components-android/blob/master/lib/java/com/google/android/material/card/MaterialCardView.java
open class CheckedCardView: CardView, Checkable {

    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param card The Material Card View whose state has changed.
         * @param isChecked The new checked state of MaterialCardView.
         */
        fun onCheckedChanged(card: CheckedCardView, isChecked: Boolean)
    }

    private val CHECKABLE_STATE_SET = intArrayOf(android.R.attr.state_checkable)
    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    private val DEFAULT_STROKE_VALUE: Int = -1

    private var strokeColor: ColorStateList? = null
    private var strokeWidth: Int = 0
    private var checkable: Boolean = false
    private var checked: Boolean = false
    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context) : super(context) {
        initialize(context, null, R.attr.checkedCardViewStyle)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs, R.attr.checkedCardViewStyle)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }

    private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CheckedCardView, defStyleAttr, 0)

        radius = a.getDimension(R.styleable.CheckedCardView_cardCornerRadius, 0f)
        cardElevation = a.getDimension(R.styleable.CheckedCardView_cardElevation, 0f)
        maxCardElevation = a.getDimension(R.styleable.CheckedCardView_cardMaxElevation, 0f)

        if (a.hasValue(R.styleable.CheckedCardView_cardBackgroundColor)) {
            val bkgStateList = getColorStateList(context, a, R.styleable.CheckedCardView_cardBackgroundColor)
            if (bkgStateList != null) {
                setCardBackgroundColor(bkgStateList)
            } else {
                val bkgColor = a.getColor(R.styleable.CheckedCardView_cardBackgroundColor, -1)
                setCardBackgroundColor(bkgColor)
            }
        }

        useCompatPadding = a.getBoolean(R.styleable.CheckedCardView_cardUseCompatPadding, false)
        preventCornerOverlap = a.getBoolean(R.styleable.CheckedCardView_cardPreventCornerOverlap, true)

        val defaultPadding = a.getDimensionPixelSize(R.styleable.CheckedCardView_contentPadding, 0)
        val paddingLeft = a.getDimensionPixelSize(R.styleable.CheckedCardView_contentPaddingLeft, defaultPadding)
        val paddingTop = a.getDimensionPixelSize(R.styleable.CheckedCardView_contentPaddingTop, defaultPadding)
        val paddingRight = a.getDimensionPixelSize(R.styleable.CheckedCardView_contentPaddingRight, defaultPadding)
        val paddingBottom = a.getDimensionPixelSize(R.styleable.CheckedCardView_contentPaddingBottom, defaultPadding)
        setContentPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        minimumHeight = a.getDimensionPixelSize(R.styleable.CheckedCardView_android_minHeight, 0)
        minimumWidth = a.getDimensionPixelSize(R.styleable.CheckedCardView_android_minWidth, 0)

        strokeColor = getColorStateList(context, a, R.styleable.CheckedCardView_strokeColor)
        strokeWidth = a.getDimensionPixelSize(R.styleable.CheckedCardView_strokeWidth, 0)
        checkable = a.getBoolean(R.styleable.CheckedCardView_android_checkable, false)

        a.recycle()
    }

    private fun getColorStateList(context: Context, attributes: TypedArray, index: Int): ColorStateList? {
        if (attributes.hasValue(index)) {
            val resourceId = attributes.getResourceId(index, 0)
            if (resourceId != 0) {
                val value = AppCompatResources.getColorStateList(context, resourceId)
                if (value != null) {
                    return value
                }
            }
        }

        // Reading a single color with getColorStateList() on API 15 and below doesn't always correctly
        // read the value. Instead we'll first try to read the color directly here.
//        if (VERSION.SDK_INT <= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//            val color = attributes.getColor(index, -1)
//            if (color != -1) {
//                return ColorStateList.valueOf(color)
//            }
//        }

        return attributes.getColorStateList(index)
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = CheckedCardView::class.java.name
        event.isChecked = checked
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = CheckedCardView::class.java.name
        info.isCheckable = true
        info.isChecked = checked
    }

    fun setStrokeColor(@ColorInt strokeColor: Int) {
        this.strokeColor = ColorStateList.valueOf(strokeColor)
        updateForeground()
    }

    fun setStrokeColor(strokeColor: ColorStateList) {
        if (this.strokeColor != strokeColor) {
            this.strokeColor = strokeColor
            updateForeground()
        }
    }

    fun getStrokeColor(): Int = strokeColor?.defaultColor ?: DEFAULT_STROKE_VALUE

    fun getStrokeColorStateList(): ColorStateList? = strokeColor

    fun setStrokeWidth(strokeWidth: Int) {
        if (this.strokeWidth != strokeWidth) {
            this.strokeWidth = strokeWidth
            updateForeground()
        }
    }

    fun setCheckable(checkable: Boolean) {
        this.checkable = checkable
    }

    fun isCheckable(): Boolean = checkable

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        if (isCheckable() && isEnabled) {
            checked = !checked
            updateForeground()
            refreshDrawableState()

            if (onCheckedChangeListener != null) {
                onCheckedChangeListener!!.onCheckedChanged(this, checked)
            }
        }
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            toggle()
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this Card changes.
     *
     * @param listener the callback to call on checked state change
     */
    fun setOnCheckedChangeListener(@Nullable listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 3)
        if (checkable) {
            View.mergeDrawableStates(drawableState, CHECKABLE_STATE_SET)
        }

        if (checked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }

        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

    private fun updateForeground() {
        this.foreground = this.createForegroundDrawable()
    }

    private fun createForegroundDrawable(): Drawable {
        val fgDrawable = GradientDrawable()
        fgDrawable.cornerRadius = this.radius
        if (this.strokeColor != null) {
            var color = this.strokeColor!!.defaultColor
            if (checked) {
                color = this.strokeColor!!.getColorForState(CHECKED_STATE_SET, color)
            }
            fgDrawable.setStroke(this.strokeWidth, color)
        }

        return fgDrawable
    }

    public override fun onSaveInstanceState(): Parcelable? {
        // Force our ancestor class to save its state
        val superState = super.onSaveInstanceState()

        val ss = SavedState(superState!!)

        ss.checked = isChecked
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState

        super.onRestoreInstanceState(ss.superState)
        isChecked = ss.checked
        requestLayout()
    }

    class SavedState : View.BaseSavedState {
        var checked: Boolean = false

        /**
         * Constructor called from [CheckedCardView.onSaveInstanceState]
         */
        constructor(superState: Parcelable) : super(superState)

        /**
         * Constructor called from [.CREATOR]
         */
        @SuppressLint("ParcelClassLoader")
        private constructor(`in`: Parcel) : super(`in`) {
            checked = `in`.readValue(null) as Boolean
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(checked)
        }

        override fun toString(): String {
            return ("CheckedCardView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}")
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}