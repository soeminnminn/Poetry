package com.s16.poetry.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.s16.poetry.Constants

import com.s16.poetry.R
import com.s16.poetry.data.DetailRecord
import com.s16.poetry.data.DetailsModel
import com.s16.poetry.data.DetailsModelFactory
import com.s16.utils.formatToViewDateDefaults
import com.s16.utils.gone
import kotlinx.android.synthetic.main.fragment_detail_view.*
import java.util.*

class DetailViewFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_detail_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: DetailsModel = ViewModelProviders.of(activity!!, DetailsModelFactory(activity!!.application, id))
            .get(DetailsModel::class.java)
        model.data.observe(this, Observer<DetailRecord> { record ->
            noteTitle.text = record.title
            noteContent.text = record.text
            noteLastModify.text = Date(record.date!!).formatToViewDateDefaults()
            if (record.category != null) {
                noteCategory.text = record.category
            } else {
                noteCategory.gone()
            }
            noteTags.removeAllViewsInLayout()
            if (record.tags.isNotEmpty()) {
                record.tags.forEach { tag ->
                    val chip = Chip(noteTags.context).apply {
                        text = tag
                        isClickable = false
                        setCheckedIconResource(R.drawable.ic_tag_gray)
                        isChecked = true
                    }
                    noteTags.addView(chip)
                }
            } else {
                noteTags.gone()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Long) =
            DetailViewFragment().apply {
                arguments = Bundle().apply {
                    putLong(Constants.ARG_PARAM_ID, id)
                }
            }
    }
}
