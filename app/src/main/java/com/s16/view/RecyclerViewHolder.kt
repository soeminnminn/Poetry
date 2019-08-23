package com.s16.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
open class RecyclerViewHolder(itemView: View, vararg ids: Int) : RecyclerView.ViewHolder(itemView) {
    private val mViewHolder: MutableMap<Int, View?> = mutableMapOf()

    init {
        ids.forEach {
            mViewHolder[it] = itemView.findViewById(it)
        }
    }

    open fun <T: View> findViewById(id: Int): T {
        return if (mViewHolder.containsKey(id)) {
            mViewHolder[id]!! as T
        } else {
            mViewHolder[id] = itemView.findViewById<T>(id)
            mViewHolder[id]!! as T
        }
    }

    operator fun <T: View> get(id: Int): T = findViewById(id)
}

// MARK: Extensions

inline fun RecyclerViewHolder.setOnClickListener(crossinline listener: (view: View?) -> Unit) {
    val wrapper = View.OnClickListener { v -> listener(v) }
    itemView.setOnClickListener(wrapper)
}