package com.s16.poetry.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.s16.app.ThemeActivity
import com.s16.poetry.R
import com.s16.poetry.adapters.CategoriesListAdapter
import com.s16.poetry.data.Category
import com.s16.poetry.data.CategoryModel

class SelectCategoryActivity : ThemeActivity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView: ListView = findViewById(R.id.listView)
        val emptyView: ViewGroup = findViewById(R.id.emptyView)
        listView.emptyView = emptyView
        listView.isSmoothScrollbarEnabled = true
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.onItemClickListener = this

        val listAdapter = CategoriesListAdapter(this)
        listView.adapter = listAdapter

        val categoryModel by lazy {
            ViewModelProviders.of(this).get(CategoryModel::class.java)
        }
        categoryModel.categories.observe(this, Observer<List<Category>> {
            listAdapter.clear()
            listAdapter.addAll(it)
        })
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }
}
