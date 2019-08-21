package com.s16.poetry.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.fragments.DetailEditFragment
import com.s16.poetry.fragments.DetailViewFragment
import com.s16.utils.startActivity

class DetailsActivity : ThemeActivity() {

    private lateinit var toolbar: Toolbar
    private var openMode: Int = 0
    private var currentMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        updateSystemUiColor()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bottomBar: BottomAppBar = findViewById(R.id.bottomAppBar)
        bottomBar.inflateMenu(R.menu.menu_details_bottom)

        val id = intent.getLongExtra(Constants.ARG_PARAM_ID, -1)
        openMode = intent.getIntExtra(Constants.ARG_PARAM_ADD, 0)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.detailContainer, DetailEditFragment.newInstance(id), "detailsEdit")
            .add(R.id.detailContainer, DetailViewFragment.newInstance(id), "detailsView")
            .commitNow()


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

    private fun swiftMode(mode: Int) {
        when (mode) {
            1 -> {
                invalidateOptionsMenu()

                supportFragmentManager
                    .beginTransaction()
                    .hide(supportFragmentManager.findFragmentByTag("detailsView")!!)
                    .show(supportFragmentManager.findFragmentByTag("detailsEdit")!!)
                    .commit()
            }
            else -> {
                invalidateOptionsMenu()

                supportFragmentManager
                    .beginTransaction()
                    .hide(supportFragmentManager.findFragmentByTag("detailsEdit")!!)
                    .show(supportFragmentManager.findFragmentByTag("detailsView")!!)
                    .commit()
            }
        }
    }

    private fun save() {

    }
}
