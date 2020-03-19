package com.s16.poetry.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.adapters.TagsSelectAdapter
import com.s16.poetry.data.DbManager
import com.s16.poetry.data.Tags
import com.s16.poetry.data.TagsModel
import com.s16.poetry.view.EditInputDialog
import com.s16.widget.SupportRecyclerView
import kotlinx.coroutines.*
import java.util.*

class SelectTagActivity : ThemeActivity() {

    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var saveJob: Job? = null

    private var inserted: String? = null

    private lateinit var adapter: TagsSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_tag)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val selectedTags = intent.getStringArrayExtra(Constants.ARG_PARAM_TAGS)

        val recyclerView: SupportRecyclerView = findViewById(R.id.recyclerView)
        val emptyView: ViewGroup = findViewById(R.id.emptyView)
        recyclerView.setEmptyView(emptyView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TagsSelectAdapter(this)
        recyclerView.adapter = adapter

        val textFilter: EditText = findViewById(R.id.textFilter)
        textFilter.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val search = s?.toString() ?: ""
                adapter.filter.filter(search)
            }
        })

        val tagsModel : TagsModel by viewModels()
        tagsModel.data.observe(this, Observer<List<Tags>> {
            adapter.submitList(it)

            if (inserted != null) {
                adapter.filter.filter("${textFilter.text}")
            } else {
                adapter.setSelected(selectedTags)
            }
        })

        val addTags: Button = findViewById(R.id.addTags)
        addTags.setOnClickListener {
            showAddNew()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (::adapter.isInitialized) {
            val selected = adapter.getSelected()
            if (selected.isNotEmpty()) {
                val resultIntent = Intent()
                val tags = selected.map {
                    it.name
                }
                resultIntent.putExtra(Constants.ARG_PARAM_TAGS, tags.toTypedArray())
                setResult(Constants.RESULT_OK, resultIntent)

            } else {
                setResult(Constants.RESULT_NONE)
            }
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        saveJob?.cancel()
        super.onDestroy()
    }

    private fun showAddNew() {
        val textFilter: EditText = findViewById(R.id.textFilter)

        val fragment = EditInputDialog()
        fragment.setText(textFilter.text)
        fragment.setTitle(R.string.title_add_tag)
        fragment.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })
        fragment.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, _ ->
            save("${fragment.getText()}")
            dialog.dismiss()
        })
        fragment.show(supportFragmentManager, "addNewDialog")
    }

    private fun save(value: String) {
        val manager = DbManager(applicationContext)
        saveJob = uiScope.launch {
            withContext(Dispatchers.IO) {
                manager.provider().insertTag(Tags(0, value, UUID.randomUUID().toString()))
            }
            inserted = value
        }
    }
}
