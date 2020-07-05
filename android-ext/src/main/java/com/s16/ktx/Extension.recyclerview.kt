package com.s16.ktx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.s16.view.RecyclerViewArrayAdapter
import com.s16.view.RecyclerViewHolder

inline fun <reified T> RecyclerView.bindArray(
    @LayoutRes itemLayout: Int,
    crossinline bindView: (holder: RecyclerView.ViewHolder, item: T) -> Unit):
        RecyclerViewArrayAdapter<RecyclerViewHolder, T> {

    val arrayAdapter = object : RecyclerViewArrayAdapter<RecyclerViewHolder, T>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(itemLayout, parent, false)
            return RecyclerViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            getItem(position)?.let {
                bindView.invoke(holder, it)
            }
        }

    }

    adapter = arrayAdapter
    return arrayAdapter
}

inline fun <reified T> RecyclerView.bindArray(
    @LayoutRes itemLayout: Int, list: List<T>,
    crossinline bindView: (holder: RecyclerView.ViewHolder, item: T) -> Unit):
        RecyclerViewArrayAdapter<RecyclerViewHolder, T> {

    val arrayAdapter = object : RecyclerViewArrayAdapter<RecyclerViewHolder, T>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(itemLayout, parent, false)
            return RecyclerViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            getItem(position)?.let {
                bindView.invoke(holder, it)
            }
        }

    }

    adapter = arrayAdapter
    arrayAdapter.submitList(list)

    return arrayAdapter
}