package com.s16.poetry.adapters


import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import com.s16.poetry.R
import com.s16.poetry.utils.TypeFaceManager
import com.s16.poetry.data.Category
import com.s16.view.BaseMenuAdapter


class NavMenuAdapter(private val context: Context): BaseMenuAdapter() {
    var items: List<Category> = mutableListOf()

    override fun getCount(): Int = 3

    override fun getMenuItem(menu: Menu, position: Int): MenuItem = when(position) {
        0 -> menu.add(categoryGroupId, R.id.action_notes, 0, R.string.menu_notes).apply {
            setIcon(R.drawable.ic_lightbulb)
            isCheckable = true
            isChecked = true
        }
        1 -> menu.add(generalGroupId, R.id.action_settings, 0, R.string.action_settings).apply {
            setIcon(R.drawable.ic_settings)
            isCheckable = false
        }
        else -> menu.add(generalGroupId, R.id.action_about, 0, R.string.action_about).apply {
            setIcon(R.drawable.ic_about)
            isCheckable = false
        }
    }

    fun getItemById(id: Int): Category? {
        if (id == 0 || id == R.id.action_notes || id == R.id.action_category || id == R.id.action_add_category) return null
        return items.first {
            it.id == id.toLong()
        }
    }

    override fun hasSubMenu(item: MenuItem): Boolean = item.itemId == R.id.action_notes

    override fun getSubMenuItemCount(item: MenuItem): Int = if (item.itemId == R.id.action_notes) {
        items.size
    } else {
        0
    }

    override fun getSubMenu(menu: Menu, item: MenuItem, position: Int): SubMenu = if (position == 1) {
        val subMenu = menu.addSubMenu(categoryGroupId, R.id.action_category, 0, R.string.menu_categories)
        for (i in items.indices) {
            val value = items[i]
            val spannableString = TypeFaceManager.makePreferencesTypeFaceSpan(context, value.name)

            subMenu.add(categoryGroupId, value.id.toInt(), 0, spannableString).apply {
                setIcon(R.drawable.ic_category)
                isCheckable = true
            }
        }
        subMenu.add(categoryGroupId, R.id.action_add_category, 0, R.string.menu_add_category).apply {
            setIcon(R.drawable.ic_add)
            isCheckable = false
        }
        subMenu
    } else {
        super.getSubMenu(menu, item, position)
    }

    companion object {
        const val categoryGroupId = 1
        const val generalGroupId = 2
    }
}