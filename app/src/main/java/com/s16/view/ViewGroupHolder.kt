package com.s16.view

import android.view.View
import androidx.annotation.IdRes

abstract class ViewGroupHolder {
    private val mViewHolder: MutableMap<Int, View?> = mutableMapOf()

    abstract val ids: IntArray

    abstract fun <T: View> findViewById(@IdRes id: Int): T

    fun edit() {
        ids.forEach {
            mViewHolder[it] = findViewById(it)
        }
    }

    fun commit() {
        mViewHolder.clear()
    }

    operator fun <T: View> get(@IdRes id: Int): T? {
        return if (mViewHolder.containsKey(id)) {
            mViewHolder[id]!! as T
        } else {
            null
        }
    }
}