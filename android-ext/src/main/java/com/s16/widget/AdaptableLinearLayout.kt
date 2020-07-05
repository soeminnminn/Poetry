package com.s16.widget

import android.content.Context
import android.widget.BaseAdapter
import androidx.appcompat.widget.LinearLayoutCompat
import android.util.TypedValue
import android.database.DataSetObserver
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

typealias ItemClickFun = (parent: ViewGroup, view: View, position: Int, id: Long) -> Unit

class AdaptableLinearLayout : LinearLayoutCompat {

    interface OnItemClickListener {
        fun onItemClick(parent: ViewGroup, view: View, position: Int, id: Long)
    }

    private lateinit var mAdapter: BaseAdapter
    private var mItemClickListener: OnItemClickListener? = null
    private var mItemClickFun: ItemClickFun? = null
    private var mItemBackgroundResId: Int = 0

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    init {
        orientation = LinearLayoutCompat.VERTICAL
        mItemBackgroundResId = getItemBackgroundResId(context);
    }

    private fun getItemBackgroundResId(context: Context): Int {
        var backgroundResId = 0
        if (11 < Build.VERSION.SDK_INT) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
            backgroundResId = typedValue.resourceId
        }

        return backgroundResId
    }

    var adapter: BaseAdapter
        get() = mAdapter
        set(value) {
            mAdapter = value
            adapter.registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    updateLayout()
                    super.onChanged()
                }

                override fun onInvalidated() {
                    updateLayout()
                    super.onInvalidated()
                }
            })
            updateLayout()
        }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener
        if (::mAdapter.isInitialized) {
            for (i in 0 until mAdapter.count) {
                val childLayout = getChildAt(i) as FrameLayout
                childLayout.setOnClickListener {
                    mItemClickListener!!.onItemClick(this, childLayout.rootView, i, mAdapter.getItemId(i))
                }
            }
        }
    }

    fun setOnItemClickListener(listener: ItemClickFun) {
        mItemClickFun = listener
        if (::mAdapter.isInitialized) {
            for (i in 0 until mAdapter.count) {
                val childLayout = getChildAt(i) as FrameLayout
                childLayout.setOnClickListener {
                    if (mItemClickFun != null) {
                        mItemClickFun!!(this, childLayout.rootView, i, mAdapter.getItemId(i))
                    }
                }
            }
        }
    }


    private fun updateLayout() {
        if (::mAdapter.isInitialized) {
            val itemCount = mAdapter.count
            removeAllViews()

            for (i in 0 until itemCount) {
                val child = mAdapter.getView(i, null, this)
                setUpChild(child, i)
            }

            requestLayout()
            invalidate()
        }
    }

    private fun setUpChild(child: View, position: Int) {
        val childLayout = FrameLayout(context)
        childLayout.setBackgroundResource(mItemBackgroundResId)
        childLayout.layoutParams =
                LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
        childLayout.addView(child)
        addView(childLayout)
        childLayout.setOnClickListener {
            var id = 0L
            if (::mAdapter.isInitialized) {
                id = mAdapter.getItemId(position)
            }
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(this, childLayout.rootView, position, id)
            } else if (mItemClickFun != null) {
                mItemClickFun!!(this, childLayout.rootView, position, id)
            }
        }
    }

}