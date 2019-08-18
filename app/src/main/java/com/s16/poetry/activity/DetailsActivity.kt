package com.s16.poetry.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.fragments.DetailEditFragment
import com.s16.poetry.fragments.DetailViewFragment
import com.s16.utils.startActivity

class DetailsActivity : ThemeActivity() {

    private var openMode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getLongExtra(Constants.ARG_PARAM_ID, -1)
        openMode = intent.getIntExtra(Constants.ARG_PARAM_ADD, 0)

        when (openMode) {
            1 ->
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailContainer, DetailEditFragment.newInstance(id))
                    .commit()
            else ->
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detailContainer, DetailViewFragment.newInstance(id))
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (openMode == 0)
            menuInflater.inflate(R.menu.menu_details, menu)
        else
            menuInflater.inflate(R.menu.menu_details_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
