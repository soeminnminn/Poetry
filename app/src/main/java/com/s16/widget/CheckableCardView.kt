package com.s16.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import androidx.annotation.Nullable
import com.google.android.material.card.MaterialCardView
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.annotation.ColorInt
import com.s16.poetry.R


// https://github.com/material-components/material-components-android/blob/master/lib/java/com/google/android/material/card/MaterialCardView.java
class CheckableCardView: MaterialCardView, Checkable {

    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param card The Material Card View whose state has changed.
         * @param isChecked The new checked state of MaterialCardView.
         */
        fun onCheckedChanged(card: CheckableCardView, isChecked: Boolean)
    }

    private val CHECKABLE_STATE_SET = intArrayOf(android.R.attr.state_checkable)
    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    private val CHECKED_ATTRS = intArrayOf(android.R.attr.checked)
    private val STROKE_COLOR_ATTRS = intArrayOf(R.attr.strokeColorChecked)

    private var mChecked: Boolean = false
    private var mStrokeColor: Int = -1
    private var mStrokeColorChecked: Int = -1
    private var onCheckedChangeListener: OnCheckedChangeListener? = null

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
        val ta = context.obtainStyledAttributes(attrs, CHECKED_ATTRS)
        mChecked = ta.getBoolean(0, false)
        ta.recycle()

        val a = context.obtainStyledAttributes(attrs, R.styleable.CheckableCardView)
        mStrokeColorChecked = a.getColor(R.styleable.CheckableCardView_strokeColorChecked, -1)
        a.recycle()

        if (mStrokeColorChecked == -1) {
            val ta1 = context.obtainStyledAttributes(attrs, STROKE_COLOR_ATTRS)
            mStrokeColorChecked = ta1.getColor(0, -1)
            ta1.recycle()
        }
        Log.i("StrokeColorChecked", "$mStrokeColorChecked")
    }

    override fun performLongClick(): Boolean {
        toggle()
        return super.performLongClick()
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = CheckableCardView::class.java.name
        event.isChecked = mChecked
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = CheckableCardView::class.java.name
        info.isCheckable = true
        info.isChecked = mChecked
    }

    override fun setStrokeColor(strokeColor: Int) {
        mStrokeColor = strokeColor
        if (!mChecked) {
            super.setStrokeColor(strokeColor)
        }
    }

    override fun getStrokeColor(): Int {
        return mStrokeColor
    }

    fun setStrokeColorChecked(@ColorInt strokeColor: Int) {
        mStrokeColorChecked = strokeColor
        invalidate()
    }

    @ColorInt
    fun getStrokeColorChecked(): Int {
        return mStrokeColorChecked
    }

    fun isCheckable(): Boolean = true

    override fun isChecked(): Boolean = mChecked

    override fun toggle() {
        if (isCheckable() && isEnabled) {
            mChecked = !mChecked
            updateForeground()
            refreshDrawableState()

            if (onCheckedChangeListener != null) {
                onCheckedChangeListener!!.onCheckedChanged(this, mChecked)
            }
        }
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
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
        if (isCheckable()) {
            View.mergeDrawableStates(drawableState, CHECKABLE_STATE_SET)
        }

        if (isChecked) {
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
        if (mChecked && mStrokeColorChecked != -1) {
            fgDrawable.setStroke(this.strokeWidth, this.mStrokeColorChecked)
        } else if (this.strokeColor != -1) {
            fgDrawable.setStroke(this.strokeWidth, this.strokeColor)
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
         * Constructor called from [CheckableCardView.onSaveInstanceState]
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
            return ("CheckableCardView.SavedState{"
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