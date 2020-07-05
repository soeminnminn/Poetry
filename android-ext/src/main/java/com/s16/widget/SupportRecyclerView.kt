package com.s16.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class SupportRecyclerView: RecyclerView {
    /**
     * View to show if there are no items to show.
     */
    private var mEmptyView: View? = null
    private val emptyObserver = object: AdapterDataObserver() {
        override fun onChanged() {
            val empty = adapter != null && adapter!!.itemCount == 0
            updateEmptyStatus(empty)
            super.onChanged()
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        emptyObserver.onChanged()
    }

    /**
     * Sets the view to show if the adapter is empty
     */
    fun setEmptyView(emptyView: View?) {
        mEmptyView = emptyView
    }

    /**
     * When the current adapter is empty, the AdapterView can display a special view
     * called the empty view. The empty view is used to provide feedback to the user
     * that no data is available in this AdapterView.
     *
     * @return The view to show if the adapter is empty.
     */
    fun getEmptyView(): View? = mEmptyView

    private fun updateEmptyStatus(empty: Boolean) {
        if (empty) {
            visibility = View.GONE
            mEmptyView?.visibility = View.VISIBLE
        } else {
            mEmptyView?.visibility = View.GONE
            visibility = View.VISIBLE
        }
    }
}