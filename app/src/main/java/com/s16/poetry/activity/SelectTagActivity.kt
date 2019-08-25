package com.s16.poetry.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.s16.app.ThemeActivity
import com.s16.poetry.R

class SelectTagActivity : ThemeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_tag)
        updateSystemUiColor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
