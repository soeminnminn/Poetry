package com.s16.poetry.adapters


import android.content.Context
import android.view.Menu
import android.view.MenuItem
import com.s16.poetry.R
import com.s16.poetry.TypeFaceUtil
import com.s16.poetry.data.Category
import com.s16.view.AdaptableMenu
import com.s16.view.BaseMenuAdapter


class NavMenuAdapter(private val context: Context): BaseMenuAdapter() {
    var items: List<Category> = mutableListOf()

    override fun getCount(): Int = items.size + 3

    override fun getMenuItem(menu: Menu, position: Int): MenuItem {
        val maxSize = items.size + 1
        return when {
            position == 0 -> menu.add(categoryGroupId, 0, 0, R.string.menu_all).apply {
                setIcon(R.drawable.ic_category_gray)
                isChecked = true
            }
            position < maxSize -> {
                val item = items[position - 1]
                val spannableString = TypeFaceUtil.makePreferencesTypeFaceSpan(context, item.name)

                menu.add(categoryGroupId, item.id.toInt(), 0, spannableString).apply {
                    setIcon(R.drawable.ic_category_gray)
                }
            }
            else -> when(position - maxSize) {
                0 -> menu.add(generalGroupId, R.id.action_settings, 0, R.string.action_settings).apply {
                    setIcon(R.drawable.ic_settings_gray)
                }
                else -> menu.add(generalGroupId, R.id.action_about, 0, R.string.action_about).apply {
                    setIcon(R.drawable.ic_about_gray)
                }
            }
        }
    }

    override fun getGroupCheckable(groupId: Int): Int {
        return if (groupId == categoryGroupId) {
            AdaptableMenu.GROUP_CHECKABLE_YES
        } else {
            AdaptableMenu.GROUP_CHECKABLE_NO
        }
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