package com.s16.poetry.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.s16.poetry.R
import com.s16.poetry.utils.TypeFaceManager
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

    private val mCheckedItems: MutableList<Record> = mutableListOf()
    private var mSelectMode = false
    private var mItemClickListener: OnItemClickListener? = null
    private var mItemSelectListener: OnItemSelectListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        val view: ViewGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_note, parent, false) as ViewGroup
        return NoteItemViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        getItem(position)?.let { record ->
            val fontFace: Typeface = TypeFaceManager.getPreferencesTypeFace(holder.context)
            holder.dataBind(record, fontFace, mCheckedItems.contains(record))
        }
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

    fun getSelectedItems() = mCheckedItems

    override fun onSelectStart() {
        if (!mSelectMode && mItemSelectListener != null) {
            mItemSelectListener!!.onItemSelectStart()
        }
    }

    override fun onSelectionChange(position: Int, checked: Boolean) {
        getItem(position)?.let { record ->
            if (checked) {
                if (!mCheckedItems.contains(record)) mCheckedItems.add(record)
            } else {
                if (mCheckedItems.contains(record)) mCheckedItems.remove(record)
            }

            if (mItemSelectListener != null) {
                mItemSelectListener!!.onItemSelectionChange(position, mCheckedItems.size)
            }
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