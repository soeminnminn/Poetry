package com.s16.poetry.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import com.s16.poetry.R
import com.s16.poetry.data.Category


class CategoriesListAdapter(context: Context):
    ArrayAdapter<Category>(context, R.layout.list_item_simple_selectable) {

    private val checkMarkDrawable: Drawable
        get() {
            val attrs = intArrayOf(android.R.attr.listChoiceIndicatorMultiple)
            var drawable: Drawable? = null
            val ta = context.theme.obtainStyledAttributes(attrs)
            drawable = ta.getDrawable(0)
            ta.recycle()
            return drawable
        }

    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?:
        layoutInflater.inflate(R.layout.list_item_simple_selectable, parent, false)

        val textView: CheckedTextView = view as CheckedTextView
        getItem(position)?.let {
            textView.text = it.name
            textView.checkMarkDrawable = checkMarkDrawable
        }
//        val textView: TextView = view as TextView
//        getItem(position)?.let {
//            textView.text = it.name
//        }
        return view
    }
}