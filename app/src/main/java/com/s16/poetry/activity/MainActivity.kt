package com.s16.poetry.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.TypeFaceUtil
import com.s16.poetry.adapters.NavMenuAdapter
import com.s16.poetry.adapters.RecordsPagedAdapter
import com.s16.poetry.adapters.setItemClickListener
import com.s16.poetry.data.*
import com.s16.poetry.fragments.AboutFragment
import com.s16.preferences.StringLiveSharedPreference
import com.s16.utils.confirmDialog
import com.s16.utils.nullableStringOf
import com.s16.utils.startActivity
import com.s16.view.AdaptableMenu
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import javax.inject.Inject


class MainActivity : ThemeActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    RecordsPagedAdapter.OnItemSelectListener {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var navAdapter: NavMenuAdapter
    private lateinit var recordsAdapter: RecordsPagedAdapter
    private lateinit var recordsModel: RecordPagedModel

    private lateinit var layoutManager: StaggeredGridLayoutManager
    private var menuItemViewMode: MenuItem? = null

    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var deleteJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toolbarEdit: Toolbar = findViewById(R.id.toolbarEdit)
        toolbarEdit.setNavigationIcon(R.drawable.ic_close_gray)
        toolbarEdit.inflateMenu(R.menu.menu_main_edit)
        toolbarEdit.setNavigationOnClickListener {
            doSelectionEnd()
        }
        toolbarEdit.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_delete) {
                doDeleteSelected()
            }
            true
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity<DetailsActivity>(Constants.ARG_PARAM_ADD to 1)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        bindNavMenu(navView)
        bindAuthorName(navView)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        bindRecords(recyclerView)
    }

    private fun bindNavMenu(navView: NavigationView) {
        navAdapter = NavMenuAdapter(this)
        val categoryModel by lazy {
            ViewModelProviders.of(this, viewModelFactory).get(CategoryModel::class.java)
        }
        categoryModel.data.observe(this, Observer<List<Category>> {
            navAdapter.items = it
            navAdapter.notifyDataSetChanged()
        })

        val menu = AdaptableMenu(navView.menu)
        menu.adapter = navAdapter
    }

    private fun bindRecords(recyclerView: RecyclerView) {
        recordsAdapter = RecordsPagedAdapter()
        recordsAdapter.setItemSelectListener(this)
        recordsAdapter.setItemClickListener { _, id, _ ->
            startActivity<DetailsActivity>(Pair(Constants.ARG_PARAM_ID, id))
        }

        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recordsAdapter

        recordsModel = ViewModelProviders.of(this, viewModelFactory).get(RecordPagedModel::class.java)
        recordsModel.data.observe(this, Observer<PagedList<Record>> {
            recordsAdapter.submitList(it)
        })
    }

    private fun bindAuthorName(navView: NavigationView) {
        val headerView = navView.getHeaderView(0)

        val textAuthorName: TextView = headerView.findViewById(R.id.navSubTitle)
        textAuthorName.typeface = TypeFaceUtil.getPreferencesTypeFace(this)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val defAuthorName = getString(R.string.author_name)
        val prefsLiveData = StringLiveSharedPreference(sharedPreferences, Constants.PREFS_AUTHOR_NAME, defAuthorName)

        prefsLiveData.observe(this, Observer {
            textAuthorName.text = it
        })
    }

    override fun onBackPressed() {
        if (recordsAdapter.isSelectedMode()) {
            doSelectionEnd()
        } else {
            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        deleteJob?.cancel()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
            }
            R.id.action_about -> {
                AboutFragment.newInstance().show(supportFragmentManager, "aboutDialog")
            }
            else -> filterRecords(item.itemId)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onItemSelectStart() {
        val appbar: ViewGroup = findViewById(R.id.appBar)
        val appbarEdit: ViewGroup = findViewById(R.id.appBarEdit)

        appbar.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(null)

        appbarEdit.apply {
            alpha = 0f
            scaleX = 0f
            scaleY = 0f
            visibility = View.VISIBLE
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(200)
                .setListener(null)
        }
    }

    override fun onItemSelectionChange(position: Int, count: Int) {
        val toolbarEdit: Toolbar = findViewById(R.id.toolbarEdit)
        toolbarEdit.title = "$count"
    }

    private fun doSelectionEnd() {
        val appbar: ViewGroup = findViewById(R.id.appBar)
        val appbarEdit: ViewGroup = findViewById(R.id.appBarEdit)

        appbar.animate()
            .alpha(1f)
            .setDuration(200)
            .setListener(null)

        appbarEdit.animate()
            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    appbarEdit.visibility = View.GONE
                }
            })
        recordsAdapter.endSelection()
    }

    private fun doDeleteSelected() {
        if (recordsAdapter.isSelectedMode()) {
            confirmDialog(R.string.title_delete_confirm,
                R.string.message_delete_confirm) { dialog, _ ->
                dialog.dismiss()
                deleteItems(recordsAdapter.getSelectedItems())
            }
        }
    }

    private fun deleteItems(items: List<Record>) {
        if (items.isNotEmpty()) {
            val manager = DbManager(applicationContext)
            deleteJob = uiScope.launch {
                withContext(Dispatchers.IO) {
                    val ids = items.map { it.id }.toLongArray()
                    manager.provider().deleteRecordAll(*ids)
                }
                doSelectionEnd()
            }
        }
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
                setIcon(R.drawable.ic_view_list_gray)
            } else {
                layoutManager.spanCount = 1
                setIcon(R.drawable.ic_view_grid_gray)
            }
            isChecked = !isChecked
        }
    }
}
