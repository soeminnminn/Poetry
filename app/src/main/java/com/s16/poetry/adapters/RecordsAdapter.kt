package com.s16.poetry.adapters

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.s16.poetry.R
import com.s16.poetry.data.Record

class RecordsPagedAdapter:
    PagedListAdapter<Record, NoteItemViewHolder>(DIFF_CALLBACK), LongClickSelectable {

    interface OnItemSelectListener {
        fun onItemSelectStart()
        fun onItemSelectionChange(position: Int, count: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, id: Long, position: Int)
    }

    private val mInterpolator: LinearInterpolator = LinearInterpolator()
    private val mDuration: Long = 300
    private var mLastPosition: Int = -1

    private val mCheckedItems: MutableList<Int> = mutableListOf()
    private var mSelectMode = false
    private var mItemClickListener: OnItemClickListener? = null
    private var mItemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        val view: ViewGroup = LayoutInflater.from(parent.context).inflate(R.layout.list_item_note, parent, false) as ViewGroup
        return NoteItemViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        getItem(position)?.let { record ->
            holder.dataBind(record, mCheckedItems.contains(position))
        }

        animate(holder, position);
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener;
    }

    fun setItemSelectListener(listener: OnItemSelectListener) {
        mItemSelectListener = listener;
    }

    override fun setSelectMode(mode: Boolean) {
        mSelectMode = mode
    }

    override fun isSelectedMode(): Boolean = mSelectMode

    override fun onSelectStart() {
        if (!mSelectMode && mItemSelectListener != null) {
            mItemSelectListener!!.onItemSelectStart()
        }
    }

    override fun onSelectionChange(position: Int, checked: Boolean) {
        if (checked) {
            if (!mCheckedItems.contains(position)) mCheckedItems.add(position)
        } else {
            if (mCheckedItems.contains(position)) mCheckedItems.remove(position)
        }

        if (mItemSelectListener != null) {
            mItemSelectListener!!.onItemSelectionChange(position, mCheckedItems.size)
        }
    }

    override fun onItemClick(view: View, id: Long, position: Int) {
        if (mItemClickListener != null) {
            mItemClickListener!!.onItemClick(view, id, position)
        }
    }

    fun endSelection() {
        mSelectMode = false
        mCheckedItems.clear()
        notifyDataSetChanged()
    }

    private fun animate(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > mLastPosition) {
            for (anim in getAnimators(holder.itemView)) {
                anim.setDuration(mDuration).start()
                anim.interpolator = mInterpolator

            }
            mLastPosition = position
        } else {
            clearAnimation(holder.itemView)
        }
    }

    private fun getAnimators(view: View): Array<Animator> {
        if (view.measuredHeight <= 0) {
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f)
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f)
            return arrayOf<Animator>(scaleX, scaleY)
        }
        return arrayOf(
            ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f),
            ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f)
        )
    }

    private fun clearAnimation(view: View) {
        view.apply {
            alpha = 1f
            scaleY = 1f
            scaleX = 1f
            translationY = 0f
            translationX = 0f
            rotation = 0f
            rotationY = 0f
            rotationX = 0f
            pivotY = measuredHeight / 2f
            pivotX = measuredWidth / 2f
        }.animate().interpolator = null
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean = oldItem == newItem
        }
    }
}

// MARK: Extensions

inline fun RecordsPagedAdapter.setItemClickListener(
    crossinline listener: (view: View, id: Long, position: Int) -> Unit) {

    val wrapper = object: RecordsPagedAdapter.OnItemClickListener {
        override fun onItemClick(view: View, id: Long, position: Int) {
            listener.invoke(view, id, position)
        }
    }
    setItemClickListener(wrapper)
}