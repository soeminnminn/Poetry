package com.s16.poetry.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.s16.poetry.R
import com.s16.poetry.utils.TypeFaceManager
import com.s16.poetry.data.Category
import com.s16.poetry.utils.ThemeManager
import com.s16.ktx.dpToPixel
import com.s16.view.RecyclerViewArrayAdapter
import com.s16.view.RecyclerViewHolder
import java.util.*

class CategoriesAdapter: RecyclerViewArrayAdapter<RecyclerViewHolder, Category>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simple, parent, false)

        (view as TextView).apply {
            val icon = ThemeManager.getThemedIcon(parent.context, R.drawable.ic_category)
            setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
            compoundDrawablePadding = parent.context.dpToPixel(8)
        }
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getItem(position)?.let { category ->
            (holder.itemView as TextView).apply {
                typeface = TypeFaceManager.getPreferencesTypeFace(holder.context)
                text = category.name
            }
        }
    }

    fun add(category: String) {
        val item = Category(0, category, UUID.randomUUID().toString())
        add(item)
    }

}