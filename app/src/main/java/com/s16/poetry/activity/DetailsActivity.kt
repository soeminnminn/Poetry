package com.s16.poetry.activity


import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.DetailRecord
import com.s16.poetry.data.DetailsModel
import com.s16.poetry.data.DetailsModelFactory
import com.s16.utils.formatToViewDateDefaults
import com.s16.utils.hideInputMethod
import com.s16.utils.showInputMethod
import com.s16.utils.startActivity
import java.util.*

class DetailsActivity : ThemeActivity() {

    private var openMode: Int = 0
    private var currentMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bottomBar: BottomAppBar = findViewById(R.id.bottomAppBar)
        bottomBar.inflateMenu(R.menu.menu_details_bottom)

        val id = intent.getLongExtra(Constants.ARG_PARAM_ID, -1)
        openMode = intent.getIntExtra(Constants.ARG_PARAM_ADD, 0)

        val detailContainer: ViewGroup = findViewById(R.id.detailContainer)
        ViewCompat.setTransitionName(detailContainer, getString(R.string.transition_root))

        val noteTitleContainer: View = findViewById(R.id.noteTitleContainer)
        ViewCompat.setTransitionName(noteTitleContainer, getString(R.string.transition_title))

        val noteContentContainer: View = findViewById(R.id.noteTitleContainer)
        ViewCompat.setTransitionName(noteContentContainer, getString(R.string.transition_note))

        val noteTitle: TextView = findViewById(R.id.noteTitle)
        val noteContent: TextView = findViewById(R.id.noteContent)
        val noteCategory: TextView = findViewById(R.id.noteCategory)
        val noteLastModify: TextView = findViewById(R.id.noteLastModify)
        val noteTags: ChipGroup = findViewById(R.id.noteTags)
        val editTitle: EditText = findViewById(R.id.editTitle)
        val editContent: EditText = findViewById(R.id.editContent)

        if (id != -1L) {
            val model: DetailsModel = ViewModelProviders.of(this, DetailsModelFactory(application, id))
                .get(DetailsModel::class.java)
            model.data.observe(this, Observer<DetailRecord> { record ->
                if (record != null) {
                    noteTitle.text = record.title ?: ""
                    noteContent.text = record.text ?: ""

                    editTitle.setText(record.title ?: "", TextView.BufferType.EDITABLE)
                    editContent.setText(record.text ?: "", TextView.BufferType.EDITABLE)

                    if (record.date != null) {
                        noteLastModify.text = Date(record.date!!).formatToViewDateDefaults()
                    }

                    if (record.category != null) {
                        noteCategory.text = record.category
                    } else {
                        noteCategory.visibility = View.GONE
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
                        noteTags.visibility = View.GONE
                    }
                }
            })
        }

        currentMode = openMode
        swiftMode(openMode)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (currentMode == 1)
            menuInflater.inflate(R.menu.menu_details_edit, menu)
        else
            menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_edit -> {
                currentMode = 1
                swiftMode(1)
                true
            }
            R.id.action_delete -> {
                true
            }
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (currentMode == 1) {
            save()
        }

        if (openMode == 0 && currentMode == 1) {
            currentMode = 0
            swiftMode(0)
        } else {
            super.onBackPressed()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private fun swiftMode(mode: Int) {
        val noteTitle: TextView = findViewById(R.id.noteTitle)
        val noteContent: TextView = findViewById(R.id.noteContent)
        val noteCategory: TextView = findViewById(R.id.noteCategory)
        val noteTags: ChipGroup = findViewById(R.id.noteTags)
        val editTitle: EditText = findViewById(R.id.editTitle)
        val editContent: EditText = findViewById(R.id.editContent)

        when (mode) {
            1 -> {
                invalidateOptionsMenu()

                noteTitle.visibility = View.INVISIBLE
                noteContent.visibility = View.INVISIBLE
                if (noteCategory.visibility != View.GONE) {
                    noteCategory.visibility = View.INVISIBLE
                }
                if (noteTags.visibility != View.GONE) {
                    noteTags.visibility = View.INVISIBLE
                }

                editTitle.apply {
                    visibility = View.VISIBLE
                    requestFocus()
                }
                editContent.visibility = View.VISIBLE
                showInputMethod(editTitle)
            }
            else -> {
                invalidateOptionsMenu()

                noteTitle.visibility = View.VISIBLE
                noteContent.visibility = View.VISIBLE
                if (noteCategory.visibility == View.INVISIBLE) {
                    noteCategory.visibility = View.VISIBLE
                }
                if (noteTags.visibility == View.INVISIBLE) {
                    noteTags.visibility = View.VISIBLE
                }

                editTitle.visibility = View.INVISIBLE
                editContent.visibility = View.INVISIBLE
                hideInputMethod()
            }
        }
    }

    private fun save() {

    }
}
