package com.s16.poetry.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.s16.app.ThemeActivity
import com.s16.poetry.R
import com.s16.poetry.adapters.TagsAdapter
import com.s16.poetry.data.*
import com.s16.poetry.view.EditInputDialog
import com.s16.poetry.view.SwipeToDeleteCallback
import com.s16.widget.SupportRecyclerView
import kotlinx.coroutines.*

class ManageTagsActivity : ThemeActivity() {

    private var backgroundScope = CoroutineScope(Dispatchers.IO)
    private var saveJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tags)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView: SupportRecyclerView = findViewById(R.id.recyclerView)
        val emptyView: ViewGroup = findViewById(R.id.emptyView)
        recyclerView.setEmptyView(emptyView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = TagsAdapter()
        recyclerView.adapter = adapter
        enableSwipeToDeleteAndUndo(recyclerView)

        val tagsModel by lazy {
            ViewModelProviders.of(this).get(TagsModel::class.java)
        }
        tagsModel.tags.observe(this, Observer<List<Tags>> {
            adapter.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_manage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_add -> {
                showAddNew()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        val recyclerView: SupportRecyclerView = findViewById(R.id.recyclerView)
        val adapter = recyclerView.adapter as TagsAdapter
        save(adapter.getItems())

        super.onPause()
    }

    override fun onDestroy() {
        runBlocking {
            saveJob?.join()
        }
        super.onDestroy()
    }

    private fun enableSwipeToDeleteAndUndo(recyclerView: RecyclerView) {
        val rootLayout: CoordinatorLayout = findViewById(R.id.rootLayout)

        val swipeToDeleteCallback = object: SwipeToDeleteCallback(this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val adapter = recyclerView.adapter as TagsAdapter
                val item = adapter.getItem(position)!!

                adapter.removeAt(position)
                val snackBar = Snackbar.make(rootLayout, "Tag ${item.name} was deleted",
                    Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.action_undo) {
                        adapter.insert(item, position)
                        recyclerView.scrollToPosition(position)
                    }
                }
                snackBar.show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showAddNew() {
        val recyclerView: SupportRecyclerView = findViewById(R.id.recyclerView)
        val adapter = recyclerView.adapter as TagsAdapter

        val fragment = EditInputDialog()
        fragment.setTitle(R.string.title_add_tag)
        fragment.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })
        fragment.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, _ ->
            val newText = fragment.editText?.text
            if (newText != null) {
                adapter.add("$newText")
            }
            dialog.dismiss()
        })
        fragment.show(supportFragmentManager, "addNewDialog")
    }

    private fun save(items: List<Tags>) {
        val manager = DbManager(applicationContext)
        saveJob = backgroundScope.launch {
            manager.provider().deleteInsertAllTags(items)
        }
    }
}
