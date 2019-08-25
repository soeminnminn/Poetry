package com.s16.poetry.view

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import com.s16.app.AlertDialogFragment

class EditInputDialog: AlertDialogFragment() {

    var editText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (context != null) {
            val padding = dpToPixel(context!!, 16)
            val content = FrameLayout(context!!).apply {
                setPadding(padding, padding, padding, padding)
            }
            if (editText == null) {
                editText = EditText(context!!).apply {
                    id = android.R.id.edit
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT)
                    inputType = EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES
                }
            }
            content.addView(editText)

            return content
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        fun dpToPixel(context: Context, dp: Int): Int {
            val metrics = context.resources.displayMetrics
            val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
            return px.toInt()
        }
    }
}