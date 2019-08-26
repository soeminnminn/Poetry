package com.s16.poetry.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.*
import com.s16.utils.*
import com.takisoft.datetimepicker.DatePickerDialog
import kotlinx.coroutines.*
import java.util.*

class DetailsActivity : ThemeActivity() {

    private lateinit var chipAdd: Chip

    private var recordId: Long = 0
        get() = intent.getLongExtra(Constants.ARG_PARAM_ID, 0)

    private var uuid: String = UUID.randomUUID().toString()

    private val openMode: Int
        get() = intent.getIntExtra(Constants.ARG_PARAM_ADD, 0)

    private var currentMode: Int = 0

    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var saveJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detailContainer: ViewGroup = findViewById(R.id.detailContainer)
        ViewCompat.setTransitionName(detailContainer, getString(R.string.transition_root))

        val noteTitleContainer: View = findViewById(R.id.noteTitleContainer)
        ViewCompat.setTransitionName(noteTitleContainer, getString(R.string.transition_title))

        val noteContentContainer: View = findViewById(R.id.noteTitleContainer)
        ViewCompat.setTransitionName(noteContentContainer, getString(R.string.transition_note))

        val noteTitle: TextView = findViewById(R.id.noteTitle)
        val noteContent: TextView = findViewById(R.id.noteContent)
        val noteCategory: TextView = findViewById(R.id.noteCategory)
        val noteDate: TextView = findViewById(R.id.noteDate)
        val noteTags: ChipGroup = findViewById(R.id.noteTags)
        val editTitle: EditText = findViewById(R.id.editTitle)
        val editContent: EditText = findViewById(R.id.editContent)

        val dateNote = Calendar.getInstance()
        noteDate.text = dateNote.format(Constants.DISPLAY_DATE_FORMAT)
        noteDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                dateNote.set(year, month, dayOfMonth)
                noteDate.text = dateNote.format(Constants.DISPLAY_DATE_FORMAT)

            }, dateNote.get(Calendar.YEAR), dateNote.get(Calendar.MONTH), dateNote.get(Calendar.DATE))
            datePickerDialog.show()
        }

        if (savedInstanceState != null) {
            uuid = savedInstanceState.getString(Constants.ARG_PARAM_UUID)
        }

        if (recordId != -1L) {
            val model: DetailsModel = ViewModelProviders.of(this, DetailsModelFactory(application, recordId))
                .get(DetailsModel::class.java)
            model.data.observe(this, Observer<DetailRecord> { record ->
                if (record != null) {
                    uuid = record.guid ?: uuid

                    noteTitle.text = record.title ?: ""
                    noteContent.text = record.text ?: ""

                    editTitle.setText(record.title ?: "", TextView.BufferType.EDITABLE)
                    editContent.setText(record.text ?: "", TextView.BufferType.EDITABLE)

                    if (record.date != null) {
                        val date = Date(record.date!!)
                        dateNote.set(date)
                        noteDate.text = dateNote.format(Constants.DISPLAY_DATE_FORMAT)
                    }

                    if (record.category != null && record.category!!.isNotBlank()) {
                        noteCategory.text = record.category
                        noteCategory.tag = record.category
                        noteCategory.visibility = View.VISIBLE
                    } else {
                        noteCategory.visibility = View.GONE
                    }

                    noteTags.removeAllViewsInLayout()
                    if (record.tags.isNotEmpty()) {
                        record.tags.forEach { tag ->
                            val chip = Chip(noteTags.context).apply {
                                text = tag
                                isClickable = false
                                isCheckable = false
                                setChipIconResource(R.drawable.ic_tag_gray)
                                isChipIconVisible = true
                            }
                            noteTags.addView(chip)
                        }
                    }

                    chipAdd = Chip(noteTags.context).apply {
                        id = R.id.action_add_tags
                        setText(R.string.action_add_tags)
                        isCheckable = false
                        setChipIconResource(R.drawable.ic_add_gray)
                        isChipIconVisible = true
                        visibility = View.GONE
                    }
                    noteTags.addView(chipAdd)
                }
            })
        } else {
            chipAdd = findViewById(R.id.action_add_tags)
        }

        noteCategory.setOnClickListener {
            val intent = Intent(this, SelectCategoryActivity::class.java)
            intent.putExtra(Constants.ARG_PARAM_CATEGORY, "${noteCategory.tag ?: ""}")
            startActivityForResult(intent, Constants.RESULT_SELECT_CATEGORY)
        }

        currentMode = openMode
        switchMode(openMode)
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
                switchMode(1)
                true
            }
            R.id.action_make_copy -> {
                true
            }
            R.id.action_save -> {
                save()
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
            switchMode(0)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        saveJob?.cancel()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RESULT_SELECT_CATEGORY && resultCode == Constants.RESULT_OK) {
            val noteCategory: TextView = findViewById(R.id.noteCategory)
            data?.let {
                val name = it.getStringExtra(Constants.ARG_PARAM_NAME)
                noteCategory.text = name
                noteCategory.tag = name
                noteCategory.visibility = View.VISIBLE
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            putString(Constants.ARG_PARAM_UUID, uuid)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            uuid = savedInstanceState.getString(Constants.ARG_PARAM_UUID)
        }
    }

    private fun switchMode(mode: Int) {
        val noteTitle: TextView = findViewById(R.id.noteTitle)
        val noteContent: TextView = findViewById(R.id.noteContent)
        val noteCategory: TextView = findViewById(R.id.noteCategory)
        val noteDate: TextView = findViewById(R.id.noteDate)
        val editTitle: EditText = findViewById(R.id.editTitle)
        val editContent: EditText = findViewById(R.id.editContent)

        when (mode) {
            1 -> {
                invalidateOptionsMenu()

                noteTitle.visibility = View.INVISIBLE
                noteContent.visibility = View.INVISIBLE
                noteCategory.visibility = View.VISIBLE
                noteCategory.isClickable = true
                noteDate.isClickable = true

                if (::chipAdd.isInitialized) {
                    chipAdd.visibility = View.VISIBLE
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
                noteCategory.isClickable = false
                if (noteCategory.tag == null) {
                    noteCategory.visibility = View.GONE
                }
                noteDate.isClickable = false

                if (::chipAdd.isInitialized) {
                    chipAdd.visibility = View.GONE
                }

                editTitle.visibility = View.INVISIBLE
                editContent.visibility = View.INVISIBLE
                hideInputMethod()
            }
        }
    }

    private fun save() {
        val editTitle: EditText = findViewById(R.id.editTitle)
        val editContent: EditText = findViewById(R.id.editContent)
        val noteCategory: TextView = findViewById(R.id.noteCategory)
        val noteDate: TextView = findViewById(R.id.noteDate)

        if ("${editTitle.text}".isBlank() && "${editContent.text}".isBlank()) {
            return
        }

        val date = Calendar.getInstance()
        date.set("${noteDate.text}", Constants.DISPLAY_DATE_FORMAT)

        val category = "${noteCategory.tag ?: ""}"

        val record = Record(
            recordId,
            date.timeInMillis,
            0,
            "${editTitle.text}",
            "${editContent.text}",
            category,
            Calendar.getInstance().timeInMillis,
            uuid)

        val manager = DbManager(applicationContext)
        saveJob = uiScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (recordId != 0L) {
                    manager.provider().updateRecord(record)
                } else {
                    manager.provider().insertRecord(record)
                }
            }

            if (result is Long) {
                recordId = result
            }
        }
    }
}
