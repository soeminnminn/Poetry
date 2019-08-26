package com.s16.poetry.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.s16.poetry.R
import com.s16.poetry.data.Category
import com.s16.utils.dpToPixel
import com.s16.view.RecyclerViewArrayAdapter
import com.s16.view.RecyclerViewHolder
import java.util.*

class CategoriesAdapter: RecyclerViewArrayAdapter<RecyclerViewHolder, Category>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simple, parent, false)

        val textView: TextView = view as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_category_gray, 0, 0, 0)
        textView.compoundDrawablePadding = parent.context.dpToPixel(8)

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getItem(position)?.let {
            val textView: TextView = holder.itemView as TextView
            textView.text = it.name
        }
    }

    fun add(category: String) {
        val item = Category(0, category, UUID.randomUUID().toString())
        add(item)
    }

}