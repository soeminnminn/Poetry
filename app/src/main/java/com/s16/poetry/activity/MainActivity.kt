package com.s16.poetry.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.adapters.NavMenuAdapter
import com.s16.poetry.adapters.RecordsPagedAdapter
import com.s16.poetry.adapters.setItemClickListener
import com.s16.poetry.data.Category
import com.s16.poetry.data.CategoryModel
import com.s16.poetry.data.Record
import com.s16.poetry.data.RecordPagedModel
import com.s16.utils.startActivity
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navAdapter: NavMenuAdapter
    private lateinit var recordsModel: RecordPagedModel

    private lateinit var layoutManager: StaggeredGridLayoutManager
    private var menuItemViewMode: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val recordsAdapter = RecordsPagedAdapter()
        recordsAdapter.setItemClickListener { _, id, _ ->
            startActivity<DetailsActivity>(Pair(Constants.ARG_PARAM_ID, id))
        }

        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recordsAdapter

        recordsModel = ViewModelProviders.of(this).get(RecordPagedModel::class.java)
        recordsModel.filterData.observe(this, Observer<PagedList<Record>> {
            recordsAdapter.submitList(it)
        })

        navAdapter = NavMenuAdapter(navView.menu, this)
        val categoryModel by lazy {
            ViewModelProviders.of(this).get(CategoryModel::class.java)
        }
        categoryModel.categories.observe(this, Observer<List<Category>> {
            navAdapter.items = it
            navAdapter.notifyDataSetChanged()
        })
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuItemViewMode = menu?.findItem(R.id.action_view_mode)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.action_view_mode -> {
                toggleViewMode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        filterRecords(item.itemId)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun filterRecords(categoryId: Int) {
        val category = navAdapter.getItemById(categoryId)
        if (category == null) {
            recordsModel.filter(null)
        } else {
            recordsModel.filter(category.name)
        }
    }

    private fun toggleViewMode() {
        menuItemViewMode?.run {
            if (isChecked) {
                layoutManager.spanCount = 2
                setIcon(R.drawable.ic_view_grid_white)
            } else {
                layoutManager.spanCount = 1
                setIcon(R.drawable.ic_view_list_white)
            }
            isChecked = !isChecked
        }
    }
}
