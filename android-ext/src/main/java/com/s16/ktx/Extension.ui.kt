package com.s16.ktx

import android.app.Activity
import android.view.MenuItem
import android.view.View

/*
 * MenuItem
 */
fun MenuItem.backPressed(activity: Activity): Boolean {
    if (itemId == android.R.id.home) {
        activity.onBackPressed()
        return true
    }
    return false
}

/*
 * View
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

inline fun <reified T> View.tagAs(): T? {
    return if (tag == null) {
        null
    } else {
        try {
            tag as T
        } catch(e: Exception) {
            null
        }
    }
}