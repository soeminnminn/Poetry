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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.adapters.CategoriesSelectAdapter
import com.s16.poetry.data.Category
import com.s16.poetry.data.CategoryModel
import com.s16.poetry.data.DbManager
import com.s16.poetry.view.EditInputDialog
import com.s16.widget.SupportRecyclerView
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class SelectCategoryActivity : ThemeActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var saveJob: Job? = null

    private var inserted: String? = null

    private lateinit var adapter: CategoriesSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val selectedCategory = intent.getStringExtra(Constants.ARG_PARAM_CATEGORY)

        val recyclerView: SupportRecyclerView = findViewById(R.id.recyclerView)
        val emptyView: ViewGroup = findViewById(R.id.emptyView)
        recyclerView.setEmptyView(emptyView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoriesSelectAdapter(this)
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

        val categoryModel by lazy {
            ViewModelProviders.of(this, viewModelFactory).get(CategoryModel::class.java)
        }
        categoryModel.data.observe(this, Observer<List<Category>> {
            adapter.submitList(it)

            if (inserted != null) {
                adapter.filter.filter("${textFilter.text}")
            } else {
                adapter.setSelected(selectedCategory)
            }
        })

        val addCategory: Button = findViewById(R.id.addCategory)
        addCategory.setOnClickListener {
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

            if (selected != null) {
                val resultIntent = Intent()
                resultIntent.putExtra(Constants.ARG_PARAM_ID, selected.id)
                resultIntent.putExtra(Constants.ARG_PARAM_NAME, selected.name)
                resultIntent.putExtra(Constants.ARG_PARAM_UUID, selected.guid)
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
        fragment.setTitle(R.string.title_add_category)
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
                manager.provider().insertCategory(Category(0, value, UUID.randomUUID().toString()))
            }
            inserted = value
        }
    }
}
