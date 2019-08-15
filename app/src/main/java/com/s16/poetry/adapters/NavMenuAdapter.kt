package com.s16.poetry.adapters

import android.content.Context
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.navigation.NavigationView
import com.s16.poetry.R
import com.s16.poetry.data.Category

class NavMenuAdapter(navView: NavigationView, private val context: Context): BaseAdapter() {
    var items: List<Category> = mutableListOf()

    private val menu: Menu = navView.menu

    override fun hasStableIds(): Boolean = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return convertView ?: View(context)
    }

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = items[position].id

    override fun getCount(): Int = items.size

    override fun notifyDataSetChanged() {
        menu.clear()
        val allMenuItem = menu.add(categoryGroupId, 0, 0, R.string.menu_all)
        allMenuItem.setIcon(R.drawable.ic_category_gray)
        allMenuItem.isChecked = true

        items.forEach {
            val menuItem = menu.add(categoryGroupId, it.id.toInt(), 0, it.name)
            menuItem.setIcon(R.drawable.ic_category_gray)
        }
        menu.setGroupCheckable(categoryGroupId, true, false)

        menu.add(generalGroupId, R.id.action_settings, 0, R.string.action_settings).apply {
            setIcon(R.drawable.ic_settings_gray)
        }
        menu.add(generalGroupId, R.id.action_about, 0, R.string.action_about).apply {
            setIcon(R.drawable.ic_about_gray)
        }

        super.notifyDataSetChanged()
    }

    fun getItemById(id: Int): Category? {
        if (id == 0) return null
        return items.first {
            it.id == id.toLong()
        }
    }

    companion object {
        const val categoryGroupId = 1
        const val generalGroupId = 2
    }
}