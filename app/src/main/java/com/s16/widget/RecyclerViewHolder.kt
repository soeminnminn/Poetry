package com.s16.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
class RecyclerViewHolder(itemView: View, vararg ids: Int) : RecyclerView.ViewHolder(itemView) {
    private val mViewHolder: MutableMap<Int, View> = mutableMapOf()

    init {
        ids.forEach {
            mViewHolder[it] = itemView.findViewById(it)
        }
    }

    fun <T: View> findViewById(id: Int): T {
        return if (mViewHolder.containsKey(id)) {
            mViewHolder[id]!! as T
        } else {
            mViewHolder[id] = itemView.findViewById<T>(id)
            mViewHolder[id]!! as T
        }
    }

    operator fun <T: View> get(id: Int): T = mViewHolder[id]!! as T
}