package com.s16.utils

import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.os.bundleOf
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

val Activity.context: Context
    get() = this

val Activity.decorView: View
    get() {
        return window.decorView
    }

val Activity.contentView: ViewGroup
    get() {
        val decorView = window.decorView as ViewGroup
        return decorView.findViewById(android.R.id.content)
    }

val Activity.rootView: ViewGroup
    get() {
        val decorView = window.decorView as ViewGroup
        val view = decorView.findViewById<ViewGroup>(android.R.id.content)
        return view.getChildAt(0) as ViewGroup
    }

val Context.screenOrientation: Int
    get() {
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = manager.defaultDisplay.rotation
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            }
        }
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            }
        } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

val Context.isTablet: Boolean
    get() {
        val xlarge =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
        val large =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

val Context.isPortrait: Boolean
    get() {
        return screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
                screenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
    }

inline fun <reified T: Activity> Context.startActivity(extras: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    if (extras != null) {
        intent.putExtras(extras)
    }
    startActivity(intent)
}

inline fun <reified T: Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    startActivity(intent)
}

inline fun <reified T: Activity> Fragment.startActivity(extras: Bundle? = null) {
    val intent = Intent(activity, T::class.java)
    if (extras != null) {
        intent.putExtras(extras)
    }
    startActivity(intent)
}

inline fun <reified T: Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(activity, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    startActivity(intent)
}

inline fun <reified T: Activity> Activity.startActivityForResult(requestCode: Int, extras: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    if (extras != null) {
        intent.putExtras(extras)
    }
    startActivityForResult(intent, requestCode)
}

inline fun <reified T: Activity> Activity.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    startActivityForResult(intent, requestCode)
}

inline fun <reified T: Activity> Fragment.startActivityForResult(requestCode: Int, extras: Bundle? = null) {
    val intent = Intent(activity!!, T::class.java)
    if (extras != null) {
        intent.putExtras(extras)
    }
    startActivityForResult(intent, requestCode)
}

inline fun <reified T: Activity> Fragment.startActivityForResult(requestCode: Int, vararg params: Pair<String, Any?>) {
    val intent = Intent(activity!!, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    startActivityForResult(intent, requestCode)
}

inline fun <reified T: Service> Context.startService(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    startService(intent)
}

inline fun <reified T : Service> Context.stopService(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    stopService(intent)
}

inline fun <reified T: Service> Fragment.startService(vararg params: Pair<String, Any?>) {
    val intent = Intent(activity!!, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    activity!!.startService(intent)
}

inline fun <reified T : Service> Fragment.stopService(vararg params: Pair<String, Any?>) {
    val intent = Intent(activity!!, T::class.java)
    if (params.isNotEmpty()) {
        intent.putExtras(bundleOf(*params))
    }
    activity!!.stopService(intent)
}

inline fun <reified T: Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent
        = Intent(this, T::class.java).also {
    if (params.isNotEmpty()) {
        it.putExtras(bundleOf(*params))
    }
}

inline fun <reified T: Any> Fragment.intentFor(vararg params: Pair<String, Any?>): Intent
        = Intent(this.activity, T::class.java).also {
    if (params.isNotEmpty()) {
        it.putExtras(bundleOf(*params))
    }
}

fun Context.share(text: String, subject: String = "", title: String? = null): Boolean {
    return try {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, title))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.getColorCompat(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

fun Context.dpToPixel(dp: Int): Int {
    val metrics = resources.displayMetrics
    val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return px.toInt()
}

fun Context.dpToPixel(dp: Float): Float {
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Activity.showInputMethod(v: EditText) {
    v.requestFocus()
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED)
}

fun Activity.hideInputMethod() = window.peekDecorView()?.let {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(window.peekDecorView().windowToken, 0)
}

fun Activity.translucentStatusBar(value: Boolean) = window.let {
    if (Build.VERSION.SDK_INT >= 19) {
        if (value) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}

fun Activity.fullScreenWindow(value: Boolean) = window.let {
    if (Build.VERSION.SDK_INT >= 19) {
        if (value) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}

fun Activity.translucentNavigationBar(value: Boolean) = window.let {
    if (Build.VERSION.SDK_INT >= 19) {
        if (value) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }
}

/**
 * Convenient method to avoid the Navigation Bar to blink during transitions on some devices
 * @see https://stackoverflow.com/questions/26600263/how-do-i-prevent-the-status-bar-and-navigation-bar-from-animating-during-an-acti
 */
fun Activity.makeSceneTransitionAnimation(vararg pairs: Pair<View, String>): ActivityOptionsCompat {
    val updatedPairs = pairs.toMutableList()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val navBar = findViewById<View>(android.R.id.navigationBarBackground)
        if (navBar != null) {
            updatedPairs.add(Pair(navBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
        }
    }
    val newPairs = updatedPairs.map {
        androidx.core.util.Pair.create(it.first, it.second)
    }
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, *newPairs.toTypedArray())
}

fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }

fun Intent.clearTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }

fun Intent.excludeFromRecents(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }

fun Intent.multipleTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK) }

fun Intent.newTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

fun Intent.noAnimation(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) }

fun Intent.noHistory(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }

fun Intent.singleTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }

fun MenuItem.backPressed(activity: Activity): Boolean {
    if (itemId == android.R.id.home) {
        activity.onBackPressed()
        return true
    }
    return false
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}