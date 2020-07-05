package com.s16.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
open class RecyclerViewHolder(itemView: View, vararg ids: Int) : RecyclerView.ViewHolder(itemView) {
    private val mViewHolder: MutableMap<Int, View?> = mutableMapOf()

    init {
        ids.forEach {
            mViewHolder[it] = itemView.findViewById(it)
        }
    }

    val context: Context = itemView.context

    open fun <T: View> findViewById(id: Int): T {
        return if (mViewHolder.containsKey(id)) {
            mViewHolder[id]!! as T
        } else {
            mViewHolder[id] = itemView.findViewById<T>(id)
            mViewHolder[id]!! as T
        }
    }

    operator fun <T: View> get(id: Int): T = findViewById(id)

    operator fun plus(id: Int) {
        mViewHolder[id] = itemView.findViewById(id)
    }

    companion object {
        fun inflate(layoutRes: Int, parent: ViewGroup, attachToRoot: Boolean = false)
                :RecyclerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, attachToRoot)
            return RecyclerViewHolder(view)
        }
    }
}

// MARK: Extensions
inline fun RecyclerViewHolder.setOnClickListener(crossinline listener: (view: View?) -> Unit) {
    val wrapper = View.OnClickListener { v -> listener(v) }
    itemView.setOnClickListener(wrapper)
}