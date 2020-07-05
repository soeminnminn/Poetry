package com.s16.view

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu

interface MenuAdapter {

    /**
     * Register an observer that is called when changes happen to the data used by this adapter.
     *
     * @param observer the object that gets notified when the data set changes.
     */
    fun registerDataSetObserver(observer: DataSetObserver)

    /**
     * Unregister an observer that has previously been registered with this
     * adapter via {@link #registerDataSetObserver}.
     *
     * @param observer the object to unregister.
     */
    fun unregisterDataSetObserver(observer: DataSetObserver)

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    fun getCount(): Int

    /**
     * @return true if this adapter doesn't contain any data.  This is used to determine
     * whether the empty view should be displayed.  A typical implementation will return
     * getCount() == 0 but since getCount() includes the headers and footers, specialized
     * adapters might want a different behavior.
     */
    fun isEmpty(): Boolean

    fun getGroupCheckable(groupId: Int): Int

    fun getMenuItem(menu: Menu, position: Int): MenuItem

    fun hasSubMenu(item: MenuItem): Boolean

    fun getSubMenuItemCount(item: MenuItem): Int

    fun getSubMenu(menu: Menu, item: MenuItem, position: Int): SubMenu
}

open class BaseMenuAdapter: MenuAdapter {
    private val mDataSetObservable = DataSetObservable()

    override fun registerDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    override fun getCount(): Int = 0

    override fun getSubMenuItemCount(item: MenuItem): Int = 0

    override fun isEmpty(): Boolean = getCount() == 0

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    fun notifyDataSetChanged() {
        mDataSetObservable.notifyChanged()
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    fun notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated()
    }

    override fun getGroupCheckable(groupId: Int): Int = AdaptableMenu.GROUP_CHECKABLE_NO

    override fun getMenuItem(menu: Menu, position: Int): MenuItem {
        return menu.getItem(position)
    }

    override fun hasSubMenu(item: MenuItem): Boolean = false

    override fun getSubMenu(menu: Menu, item: MenuItem, position: Int): SubMenu {
        return menu.addSubMenu("")
    }
}