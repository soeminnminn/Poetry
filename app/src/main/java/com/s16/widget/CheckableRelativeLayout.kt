package com.s16.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Checkable
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.DrawableCompat

open class CheckableRelativeLayout : RelativeLayout, Checkable {

    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param viewGroup The View whose state has changed.
         * @param isChecked The new checked state of MaterialCardView.
         */
        fun onCheckedChanged(viewGroup: CheckableRelativeLayout, isChecked: Boolean)
    }

    private val CHECKABLE_STATE_SET = intArrayOf(android.R.attr.state_checkable)
    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    private val ATTR_CHECKABLE = intArrayOf(android.R.attr.checkable)

    private var checkable: Boolean = false
    private var checked: Boolean = false
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
        val a = context.obtainStyledAttributes(attrs, ATTR_CHECKABLE)

        checkable = a.getBoolean(0, false)

        a.recycle()
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = CheckableRelativeLayout::class.java.name
        event.isChecked = checked
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = CheckableRelativeLayout::class.java.name
        info.isCheckable = true
        info.isChecked = checked
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            toggle()
        }
    }

    fun isCheckable(): Boolean = checkable

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        if (isCheckable() && isEnabled) {
            checked = !checked
            // updateForeground()
            refreshDrawableState()

            if (onCheckedChangeListener != null) {
                onCheckedChangeListener!!.onCheckedChanged(this, checked)
            }
        }
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
        background.state = drawableState
        invalidate()
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        DrawableCompat.setHotspot(background, x, y)
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        background.jumpToCurrentState()
    }

    override fun onSaveInstanceState(): Parcelable? {
        // Force our ancestor class to save its state
        val superState = super.onSaveInstanceState()

        val ss = SavedState(superState!!)

        ss.checked = isChecked
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState

        super.onRestoreInstanceState(ss.superState)
        isChecked = ss.checked
        requestLayout()
    }

    class SavedState : BaseSavedState {
        var checked: Boolean = false

        /**
         * Constructor called from [DrawerListItem.onSaveInstanceState]
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