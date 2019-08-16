package com.s16.poetry.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.s16.app.ThemeDialogFragment

import com.s16.poetry.R
import com.s16.widget.setPositiveButton
import kotlinx.android.synthetic.main.fragment_restore.*

class RestoreFragment : ThemeDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogButtons.setPositiveButton(android.R.string.ok) { _, _ ->
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RestoreFragment().apply {
                arguments = Bundle()
            }
    }
}
