package com.s16.utils

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

fun Context.alertDialog(@StringRes title: Int, @StringRes message: Int) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(null, null)
        setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}

fun Context.alertDialog(@StringRes title: Int, message: String) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(null, null)
        setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}

fun Context.alertDialog(title: String, @StringRes message: Int) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(null, null)
        setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}

fun Context.alertDialog(title: String, message: String) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(null, null)
        setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
    }.show()
}

inline fun Context.confirmDialog(
    @StringRes title: Int, @StringRes message: Int,
    crossinline listener: (dialog: DialogInterface, which: Int) -> Unit) {

    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(android.R.string.cancel) { _, _ ->
        }
        setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            listener(dialog, which)
        }
    }.show()
}

inline fun Context.confirmDialog(
    @StringRes title: Int, message: String,
    crossinline listener: (dialog: DialogInterface, which: Int) -> Unit) {

    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(android.R.string.cancel) { _, _ ->
        }
        setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            listener(dialog, which)
        }
    }.show()
}

inline fun Context.confirmDialog(
    title: String, @StringRes message: Int,
    crossinline listener: (dialog: DialogInterface, which: Int) -> Unit) {

    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(android.R.string.cancel) { _, _ ->
        }
        setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            listener(dialog, which)
        }
    }.show()
}

inline fun Context.confirmDialog(
    title: String, message: String,
    crossinline listener: (dialog: DialogInterface, which: Int) -> Unit) {

    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setNegativeButton(android.R.string.cancel) { _, _ ->
        }
        setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            listener(dialog, which)
        }
    }.show()
}