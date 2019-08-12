package com.s16.poetry.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.DetailRecord
import com.s16.poetry.data.DetailsModel
import com.s16.poetry.data.DetailsModelFactory
import com.s16.poetry.fragments.DetailViewFragment

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getLongExtra(Constants.ARG_PARAM_ID, -1)

        val model: DetailsModel = ViewModelProviders.of(this, DetailsModelFactory(application, id))
            .get(DetailsModel::class.java)
        model.data.observe(this, Observer<DetailRecord> {
            Log.i("DetailsActivity", it.title)
        })

        supportFragmentManager.beginTransaction()
            .replace(R.id.detailContainer, DetailViewFragment.newInstance(id))
            .commit()
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
}
