package com.s16.poetry.fragments


import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.s16.app.AlertDialogFragment

import com.s16.poetry.R

class AboutFragment : AlertDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.prefs_about)
        setIcon(android.R.drawable.ic_dialog_info)
        setPositiveButton(null, null)
        setNegativeButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_about, container, false)

        var aboutText = context!!.getText(R.string.about_text)
        val message: TextView = rootView.findViewById(android.R.id.message)
        message.movementMethod = LinkMovementMethod.getInstance()
        message.text = Html.fromHtml(aboutText.toString())

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AboutFragment().apply {
                arguments = Bundle()
            }
    }
}
