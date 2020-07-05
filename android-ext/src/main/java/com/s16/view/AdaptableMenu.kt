package com.s16.view

import android.content.ComponentName
import android.content.Intent
import android.database.DataSetObserver
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu

class AdaptableMenu(private val menu: Menu): Menu {

    private val groupIds: MutableList<Int> = mutableListOf()
    private lateinit var mAdapter: MenuAdapter

    var adapter: MenuAdapter
        get() = mAdapter
        set(value) {
            mAdapter = value
            adapter.registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    updateMenu()
                    super.onChanged()
                }

                override fun onInvalidated() {
                    updateMenu()
                    super.onInvalidated()
                }
            })
            updateMenu()
        }

    private fun updateMenu() {
        if (::mAdapter.isInitialized) {
            menu.clear()

            for (i in 0 until adapter.getCount()) {
                val item = adapter.getMenuItem(menu, i)

                if (adapter.hasSubMenu(item)) {
                    val subMenuItemCount = adapter.getSubMenuItemCount(item)
                    for (j in 0 until subMenuItemCount) {
                        val subMenu = adapter.getSubMenu(menu, item, j)
                        if (!groupIds.contains(subMenu.item.groupId)) {
                            groupIds.add(subMenu.item.groupId)
                        }
                    }
                }

                if (!groupIds.contains(item.groupId)) {
                    groupIds.add(item.groupId)
                }
            }

            for (i in groupIds) {
                when(adapter.getGroupCheckable(i)) {
                    GROUP_CHECKABLE_YES -> menu.setGroupCheckable(i, true, false)
                    GROUP_CHECKABLE_EXCLUSIVE -> menu.setGroupCheckable(i, true, true)
                    else -> {}
                }
            }
        }
    }

    override fun clear() {
        menu.clear()
    }

    override fun removeItem(id: Int) {
        menu.removeItem(id)
    }

    override fun setGroupCheckable(group: Int, checkable: Boolean, exclusive: Boolean) {
        menu.setGroupCheckable(group, checkable, exclusive)
    }

    override fun performIdentifierAction(id: Int, flags: Int): Boolean {
        return menu.performIdentifierAction(id, flags)
    }

    override fun setGroupEnabled(group: Int, enabled: Boolean) {
        menu.setGroupEnabled(group, enabled)
    }

    override fun getItem(index: Int): MenuItem {
        return menu.getItem(index)
    }

    override fun performShortcut(keyCode: Int, event: KeyEvent?, flags: Int): Boolean {
        return menu.performShortcut(keyCode, event, flags)
    }

    override fun removeGroup(groupId: Int) {
        menu.removeGroup(groupId)
    }

    override fun setGroupVisible(group: Int, visible: Boolean) {
        menu.setGroupVisible(group, visible)
    }

    override fun add(title: CharSequence?): MenuItem {
        return menu.add(title)
    }

    override fun add(titleRes: Int): MenuItem {
        return menu.add(titleRes)
    }

    override fun add(groupId: Int, itemId: Int, order: Int, title: CharSequence?): MenuItem {
        return menu.add(groupId, itemId, order, title)
    }

    override fun add(groupId: Int, itemId: Int, order: Int, titleRes: Int): MenuItem {
        return menu.add(groupId, itemId, order, titleRes)
    }

    override fun isShortcutKey(keyCode: Int, event: KeyEvent?): Boolean {
        return menu.isShortcutKey(keyCode, event)
    }

    override fun setQwertyMode(isQwerty: Boolean) {
        menu.setQwertyMode(isQwerty)
    }

    override fun hasVisibleItems(): Boolean {
        return menu.hasVisibleItems()
    }

    override fun addSubMenu(title: CharSequence?): SubMenu {
        return menu.addSubMenu(title)
    }

    override fun addSubMenu(titleRes: Int): SubMenu {
        return menu.addSubMenu(titleRes)
    }

    override fun addSubMenu(groupId: Int, itemId: Int, order: Int, title: CharSequence?): SubMenu {
        return menu.addSubMenu(groupId, itemId, order, title)
    }

    override fun addSubMenu(groupId: Int, itemId: Int, order: Int, titleRes: Int): SubMenu {
        return menu.addSubMenu(groupId, itemId, order, titleRes)
    }

    override fun addIntentOptions(
        groupId: Int,
        itemId: Int,
        order: Int,
        caller: ComponentName?,
        specifics: Array<out Intent>?,
        intent: Intent?,
        flags: Int,
        outSpecificItems: Array<out MenuItem>?
    ): Int {
        return menu.addIntentOptions(groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems)
    }

    override fun findItem(id: Int): MenuItem {
        return menu.findItem(id)
    }

    override fun size(): Int {
        return menu.size()
    }

    override fun close() {
        menu.close()
    }

    companion object {
        const val GROUP_CHECKABLE_NO = 0
        const val GROUP_CHECKABLE_YES = 1
        const val GROUP_CHECKABLE_EXCLUSIVE = 2
    }
}
