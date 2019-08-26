package com.s16.poetry.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.s16.poetry.R
import com.s16.poetry.data.Category
import com.s16.utils.dpToPixel
import com.s16.view.RecyclerViewArrayAdapter
import com.s16.view.RecyclerViewHolder


class CategoriesSelectAdapter(private val context: Context):
    RecyclerViewArrayAdapter<RecyclerViewHolder, Category>() {

    private var selectedPosition: Int = -1

    private val checkMarkDrawable: Drawable
        get() {
            val attrs = intArrayOf(android.R.attr.listChoiceIndicatorSingle)
            var drawable: Drawable? = null
            val ta = context.theme.obtainStyledAttributes(attrs)
            drawable = ta.getDrawable(0)
            ta.recycle()
            return drawable
        }

    fun setSelected(category: String) {
        val position = findIndex {
            it.name == category
        }
        if (position > -1) {
            selectedPosition = position
            notifyItemChanged(selectedPosition)
        }
    }

    fun getSelected(): Category? {
        return if (selectedPosition > -1) {
            getItem(selectedPosition)
        } else {
            null
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simple_selectable, parent, false)
        return RecyclerViewHolder(view, android.R.id.text1)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getItem(position)?.let { category ->
            val textView: CheckedTextView = holder[android.R.id.text1]
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_category_gray, 0, 0, 0)
            textView.compoundDrawablePadding = context.dpToPixel(8)
            textView.text = category.name
            textView.checkMarkDrawable = checkMarkDrawable
            textView.isChecked = (position == selectedPosition)

            holder.itemView.setOnClickListener {
                if (position != selectedPosition) {
                    textView.isChecked = true
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                } else {
                    textView.isChecked = false
                    notifyItemChanged(selectedPosition)
                    selectedPosition = -1
                }
            }
        }
    }
}