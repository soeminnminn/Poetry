package com.s16.poetry.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.s16.poetry.R
import com.s16.poetry.data.Tags
import com.s16.utils.dpToPixel
import com.s16.view.RecyclerViewArrayAdapter
import com.s16.view.RecyclerViewHolder
import java.util.*

class TagsSelectAdapter(private val context: Context):
    RecyclerViewArrayAdapter<RecyclerViewHolder, Tags>() {

    private val mCheckedItems: MutableList<Tags> = mutableListOf()

    private val checkMarkDrawable: Drawable
        get() {
            val attrs = intArrayOf(android.R.attr.listChoiceIndicatorMultiple)
            var drawable: Drawable? = null
            val ta = context.theme.obtainStyledAttributes(attrs)
            drawable = ta.getDrawable(0)
            ta.recycle()
            return drawable
        }

    fun setSelected(tags: Array<String>) {
        if (tags.isNotEmpty()) {
            tags.forEach { tag ->
                val found = find {
                    it.name == tag
                }
                if (found != null && !mCheckedItems.contains(found)) {
                    mCheckedItems.add(found)
                }
            }
            notifyDataSetChanged()
        }
    }

    fun getSelected(): List<Tags> = mCheckedItems

    fun add(tag: String) {
        val item = Tags(0, tag, UUID.randomUUID().toString())
        add(item)
    }

    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_simple_selectable, parent, false)

        val textView: CheckedTextView = view.findViewById(android.R.id.text1)
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tag_gray, 0, 0, 0)
        textView.compoundDrawablePadding = parent.context.dpToPixel(8)
        textView.checkMarkDrawable = checkMarkDrawable

        return RecyclerViewHolder(view, android.R.id.text1)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getItem(position)?.let { tags ->
            val textView: CheckedTextView = holder[android.R.id.text1]
            textView.text = tags.name
            textView.isChecked = mCheckedItems.contains(tags)

            holder.itemView.setOnClickListener {
                val found = getItem(position)
                if (found != null) {
                    if (mCheckedItems.contains(found)) {
                        mCheckedItems.remove(found)
                    } else {
                        mCheckedItems.add(found)
                    }
                }
                notifyItemChanged(position)
            }
        }
    }

}