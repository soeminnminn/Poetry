package com.s16.poetry.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.s16.poetry.R
import com.s16.poetry.data.Tags
import com.s16.utils.dpToPixel
import com.s16.view.RecyclerViewArrayAdapter
import com.s16.view.RecyclerViewHolder
import java.util.*

class TagsAdapter: RecyclerViewArrayAdapter<RecyclerViewHolder, Tags>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simple, parent, false)

        (view as TextView).apply {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tag_gray, 0, 0, 0)
            compoundDrawablePadding = parent.context.dpToPixel(8)
        }

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getItem(position)?.let { item ->
            (holder.itemView as TextView).apply {
                text = item.name
            }
        }
    }

    fun add(tag: String) {
        val item = Tags(0, tag, UUID.randomUUID().toString())
        add(item)
    }
}