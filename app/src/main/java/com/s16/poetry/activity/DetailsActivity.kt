package com.s16.poetry.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.s16.app.ThemeActivity
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.DetailRecord
import com.s16.poetry.data.DetailsModel
import com.s16.poetry.data.DetailsModelFactory
import com.s16.poetry.fragments.DetailViewFragment
import com.s16.utils.startActivity

class DetailsActivity : ThemeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getLongExtra(Constants.ARG_PARAM_ID, -1)

        val model: DetailsModel = ViewModelProviders.of(this, DetailsModelFactory(application, id))
            .get(DetailsModel::class.java)
        model.data.observe(this, Observer<DetailRecord> {
            Log.i("DetailsActivity", it.title)
        })

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.detailContainer, DetailViewFragment.newInstance(id))
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
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
