package com.s16.poetry.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.s16.poetry.Constants

import com.s16.poetry.R

class DetailEditFragment : Fragment() {
    private var id: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getLong(Constants.ARG_PARAM_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_edit, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(id: Long) =
            DetailEditFragment().apply {
                arguments = Bundle().apply {
                    putLong(Constants.ARG_PARAM_ID, id)
                }
            }
    }
}
