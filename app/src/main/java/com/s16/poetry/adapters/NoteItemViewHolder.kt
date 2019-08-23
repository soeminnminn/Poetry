package com.s16.poetry.adapters

import android.view.View
import android.widget.TextView
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.Record
import com.s16.utils.format
import com.s16.widget.CheckedCardView
import com.s16.view.RecyclerViewHolder
import java.util.*
import kotlin.math.max

interface LongClickSelectable {
    fun onItemClick(view: View, id: Long, position: Int)

    fun setSelectMode(mode: Boolean)
    fun isSelectedMode(): Boolean
    fun onSelectStart()
    fun onSelectionChange(position: Int, checked: Boolean)
}

class NoteItemViewHolder(view: View, private val adapter: LongClickSelectable)
    : RecyclerViewHolder(view), View.OnClickListener, View.OnLongClickListener, CheckedCardView.OnCheckedChangeListener {

    private val label: TextView = findViewById(R.id.noteTitle)
    private val content: TextView = findViewById(R.id.noteContent)
    private val lastEdit: TextView = findViewById(R.id.noteLastModify)
    private val cardView: CheckedCardView = findViewById(R.id.cardView)

    private var recordId: Long = -1

    init {
        itemView.isClickable = true
        itemView.setOnClickListener(this)

        itemView.isLongClickable = true
        itemView.setOnLongClickListener(this)

        cardView.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View?) {
        if (adapter.isSelectedMode()) {
            cardView.toggle()
        } else {
            adapter.onItemClick(itemView, recordId, adapterPosition)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        if (!adapter.isSelectedMode()) {
            adapter.onSelectStart()
            adapter.setSelectMode(true)
            cardView.toggle()
            return true
        }
        return false
    }

    override fun onCheckedChanged(card: CheckedCardView, isChecked: Boolean) {
        adapter.onSelectionChange(adapterPosition, isChecked)
    }

    fun dataBind(record: Record, isChecked: Boolean) {
        recordId = record.id

        label.text = record.title

        val linesCount = record.text?.split(Regex("\\n"))?.size ?: 0
        content.maxLines = max(linesCount / 2, 3)

        content.text = record.text

        lastEdit.text = if (record.date != null) {
            Date(record.date!!).format(Constants.DISPLAY_DATE_FORMAT)
        } else {
            ""
        }

        cardView.isChecked = isChecked
    }
}