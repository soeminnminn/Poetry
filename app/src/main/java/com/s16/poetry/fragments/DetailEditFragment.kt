package com.s16.poetry.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.DetailRecord
import com.s16.poetry.data.DetailsModel
import com.s16.poetry.data.DetailsModelFactory

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTitle: EditText = view.findViewById(R.id.editTitle)
        val editNote: EditText = view.findViewById(R.id.editNote)

        if (id != -1L) {
            val model: DetailsModel = ViewModelProviders.of(activity!!, DetailsModelFactory(activity!!.application, id))
                .get(DetailsModel::class.java)
            model.data.observe(this, Observer<DetailRecord> { record ->
                if (record != null) {
                    editTitle.setText(record.title ?: "", TextView.BufferType.EDITABLE)
                    editNote.setText(record.text ?: "", TextView.BufferType.EDITABLE)
                }
            })
        }
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
