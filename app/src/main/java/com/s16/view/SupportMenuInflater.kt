package com.s16.view

import android.content.Context
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import org.xmlpull.v1.XmlPullParserException
import android.util.Xml
import android.view.*
import androidx.annotation.LayoutRes
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import android.graphics.PorterDuff
import android.content.res.ColorStateList
import androidx.core.internal.view.SupportMenu
import com.s16.poetry.R
import android.os.Build
import android.annotation.SuppressLint


// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-appcompat-release/appcompat/src/main/java/androidx/appcompat/view/SupportMenuInflater.java
class SupportMenuInflater(private val context: Context): MenuInflater(context) {

    override fun inflate(@LayoutRes menuRes: Int, menu: Menu?) {
        if (menu != null) {
            super.inflate(menuRes, menu)
        }

        var parser: XmlResourceParser? = null
        try {
            parser = context.resources.getLayout(menuRes)
            val attrs = Xml.asAttributeSet(parser)
            parseMenu(parser, attrs, menu)
        } catch (e: XmlPullParserException) {
            throw InflateException("Error inflating menu XML", e)
        } catch (e: IOException) {
            throw InflateException("Error inflating menu XML", e)
        } finally {
            parser?.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMenu(parser: XmlPullParser, attrs: AttributeSet, menu: Menu?) {
        val menuState = MenuState(context, menu!!)
        var eventType = parser.eventType
        var tagName: String
        var lookingForEndOfUnknownTag = false
        var unknownTagName: String? = null
        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.name
                if (tagName == XML_MENU) {
                    // Go to next tag
                    eventType = parser.next()
                    break
                }
                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)
        var reachedEndOfMenu = false
        loop@ while (!reachedEndOfMenu) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (lookingForEndOfUnknownTag) {
                        break@loop
                    }
                    tagName = parser.name
                    if (tagName == XML_GROUP) {
                        menuState.readGroup(attrs)
                    } else if (tagName == XML_ITEM) {
                        menuState.readItem(attrs)
                    } else if (tagName == XML_MENU) {
                        // A menu start tag denotes a submenu for an item
                        val subMenu = menuState.addSubMenuItem()
                        // Parse the submenu into returned SubMenu
                        parseMenu(parser, attrs, subMenu)
                    } else {
                        lookingForEndOfUnknownTag = true
                        unknownTagName = tagName
                    }
                }
                XmlPullParser.END_TAG -> {
                    tagName = parser.name
                    if (lookingForEndOfUnknownTag && tagName == unknownTagName) {
                        lookingForEndOfUnknownTag = false
                        unknownTagName = null
                    } else if (tagName == XML_GROUP) {
                        menuState.resetGroup()
                    } else if (tagName == XML_ITEM) {
                        // Add the item if it hasn't been added (if the item was
                        // a submenu, it would have been added already)
                        if (!menuState.hasAddedItem()) {
                            if (menuState.itemActionProvider != null && menuState.itemActionProvider!!.hasSubMenu()) {
                                menuState.addSubMenuItem()
                            } else {
                                menuState.addItem()
                            }
                        }
                    } else if (tagName == XML_MENU) {
                        reachedEndOfMenu = true
                    }
                }
                XmlPullParser.END_DOCUMENT -> throw RuntimeException("Unexpected end of document")
            }
            eventType = parser.next()
        }
    }

    private class MenuState(private val context: Context, private val menu: Menu) {

        /*
         * Group state is set on items as they are added, allowing an item to
         * override its group state. (As opposed to set on items at the group end tag.)
         */
        private var groupId: Int = 0
        private var groupCategory: Int = 0
        private var groupOrder: Int = 0
        private var groupCheckable: Int = 0
        private var groupVisible: Boolean = false
        private var groupEnabled: Boolean = false
        private var itemAdded: Boolean = false
        private var itemId: Int = 0
        private var itemCategoryOrder: Int = 0
        private var itemTitle: CharSequence? = null
        private var itemTitleCondensed: CharSequence? = null
        private var itemIconResId: Int = 0
        private var itemAlphabeticShortcut: Char = ' '
        private var itemAlphabeticModifiers: Int = 0
        private var itemNumericShortcut: Char = ' '
        private var itemNumericModifiers: Int = 0
        /**
         * Sync to attrs.xml enum:
         * - 0: none
         * - 1: all
         * - 2: exclusive
         */
        private var itemCheckable: Int = 0
        private var itemChecked: Boolean = false
        private var itemVisible: Boolean = false
        private var itemEnabled: Boolean = false
        /**
         * Sync to attrs.xml enum, values in MenuItem:
         * - 0: never
         * - 1: ifRoom
         * - 2: always
         * - -1: Safe sentinel for "no value".
         */
        private var itemShowAsAction: Int = 0
        private var itemActionViewLayout: Int = 0
        private var itemActionViewClassName: String? = null
        private var itemActionProviderClassName: String? = null
        private var itemListenerMethodName: String? = null

        var itemActionProvider: ActionProvider? = null

        private var itemContentDescription: CharSequence? = null
        private var itemTooltipText: CharSequence? = null
        private var itemIconTintList: ColorStateList? = null
        private var itemIconTintMode: PorterDuff.Mode? = null

        init {
            resetGroup()
        }

        fun resetGroup() {
            groupId = defaultGroupId
            groupCategory = defaultItemCategory
            groupOrder = defaultItemOrder
            groupCheckable = defaultItemCheckable
            groupVisible = defaultItemVisible
            groupEnabled = defaultItemEnabled
        }

        /**
         * Called when the parser is pointing to a group tag.
         */
        fun readGroup(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MenuGroup)
            groupId = a.getResourceId(R.styleable.MenuGroup_android_id, defaultGroupId)
            groupCategory = a.getInt(
                R.styleable.MenuGroup_android_menuCategory, defaultItemCategory
            )
            groupOrder = a.getInt(R.styleable.MenuGroup_android_orderInCategory, defaultItemOrder)
            groupCheckable = a.getInt(
                R.styleable.MenuGroup_android_checkableBehavior, defaultItemCheckable
            )
            groupVisible = a.getBoolean(R.styleable.MenuGroup_android_visible, defaultItemVisible)
            groupEnabled = a.getBoolean(R.styleable.MenuGroup_android_enabled, defaultItemEnabled)
            a.recycle()
        }

        /**
         * Called when the parser is pointing to an item tag.
         */
        fun readItem(attrs: AttributeSet) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MenuItem)

            // Inherit attributes from the group as default value
            itemId = a.getResourceId(R.styleable.MenuItem_android_id, defaultItemId)
            val category = a.getInt(R.styleable.MenuItem_android_menuCategory, groupCategory)
            val order = a.getInt(R.styleable.MenuItem_android_orderInCategory, groupOrder)
            itemCategoryOrder =
                category and SupportMenu.CATEGORY_MASK or (order and SupportMenu.USER_MASK)
            itemTitle = a.getText(R.styleable.MenuItem_android_title)
            itemTitleCondensed = a.getText(R.styleable.MenuItem_android_titleCondensed)
            itemIconResId = a.getResourceId(R.styleable.MenuItem_android_icon, 0)
            itemAlphabeticShortcut =
                getShortcut(a.getString(R.styleable.MenuItem_android_alphabeticShortcut))
            itemAlphabeticModifiers =
                a.getInt(R.styleable.MenuItem_alphabeticModifiers, KeyEvent.META_CTRL_ON)
            itemNumericShortcut =
                getShortcut(a.getString(R.styleable.MenuItem_android_numericShortcut))
            itemNumericModifiers =
                a.getInt(R.styleable.MenuItem_numericModifiers, KeyEvent.META_CTRL_ON)
            itemCheckable = if (a.hasValue(R.styleable.MenuItem_android_checkable)) {
                // Item has attribute checkable, use it
                if (a.getBoolean(R.styleable.MenuItem_android_checkable, false)) 1 else 0
            } else {
                // Item does not have attribute, use the group's (group can have one more state
                // for checkable that represents the exclusive checkable)
                groupCheckable
            }
            itemChecked = a.getBoolean(R.styleable.MenuItem_android_checked, defaultItemChecked)
            itemVisible = a.getBoolean(R.styleable.MenuItem_android_visible, groupVisible)
            itemEnabled = a.getBoolean(R.styleable.MenuItem_android_enabled, groupEnabled)
            itemShowAsAction = a.getInt(R.styleable.MenuItem_showAsAction, -1)
            itemListenerMethodName = a.getString(R.styleable.MenuItem_android_onClick)
            itemActionViewLayout = a.getResourceId(R.styleable.MenuItem_actionLayout, 0)
            itemActionViewClassName = a.getString(R.styleable.MenuItem_actionViewClass)
            itemActionProviderClassName = a.getString(R.styleable.MenuItem_actionProviderClass)

//            val hasActionProvider = itemActionProviderClassName != null
//            if (hasActionProvider && itemActionViewLayout === 0 && itemActionViewClassName == null) {
//                itemActionProvider = newInstance(
//                    itemActionProviderClassName,
//                    ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE,
//                    mActionProviderConstructorArguments
//                )
//            } else {
//                if (hasActionProvider) {
//                    Log.w(
//                        LOG_TAG,
//                        "Ignoring attribute 'actionProviderClass'." + " Action view already specified."
//                    )
//                }
//                itemActionProvider = null
//            }
            if (itemActionProviderClassName != null) {
                itemActionProvider = null
            }

            itemContentDescription = a.getText(R.styleable.MenuItem_contentDescription)
            itemTooltipText = a.getText(R.styleable.MenuItem_tooltipText)
            if (a.hasValue(R.styleable.MenuItem_iconTintMode)) {
                itemIconTintMode = DrawableUtils.parseTintMode(
                    a.getInt(
                        R.styleable.MenuItem_iconTintMode, -1
                    ),
                    itemIconTintMode
                )
            } else {
                // Reset to null so that it's not carried over to the next item
                itemIconTintMode = null
            }
            if (a.hasValue(R.styleable.MenuItem_iconTint)) {
                itemIconTintList = a.getColorStateList(R.styleable.MenuItem_iconTint)
            } else {
                // Reset to null so that it's not carried over to the next item
                itemIconTintList = null
            }
            a.recycle()

            itemAdded = false
        }

        fun addItem() {
            itemAdded = true;
            setItem(menu.add(groupId, itemId, itemCategoryOrder, itemTitle))
        }

        fun addSubMenuItem(): SubMenu {
            itemAdded = true;
            val subMenu = menu.addSubMenu(groupId, itemId, itemCategoryOrder, itemTitle)
            setItem(subMenu.item)
            return subMenu
        }

        fun hasAddedItem(): Boolean = itemAdded

        private fun setItem(item: MenuItem) {
            // TODO: Add Item
        }

        private fun getShortcut(shortcutString: String?): Char {
            return if (shortcutString == null) {
                0.toChar()
            } else {
                shortcutString[0]
            }
        }
    }

    companion object {
        private const val LOG_TAG = "SupportMenuInflater"

        /** Menu tag name in XML. */
        private const val XML_MENU = "menu"

        /** Group tag name in XML. */
        private const val XML_GROUP = "group"

        /** Item tag name in XML. */
        private const val XML_ITEM = "item"

        private const val NO_ID = 0

        private const val defaultGroupId = NO_ID
        private const val defaultItemId = NO_ID
        private const val defaultItemCategory = 0
        private const val defaultItemOrder = 0
        private const val defaultItemCheckable = 0
        private const val defaultItemChecked = false
        private const val defaultItemVisible = true
        private const val defaultItemEnabled = true
    }
}

object DrawableUtils {

    @SuppressLint("ObsoleteSdkInt")
    fun parseTintMode(value: Int, defaultMode: PorterDuff.Mode?): PorterDuff.Mode? {
        return when (value) {
            3 -> PorterDuff.Mode.SRC_OVER
            5 -> PorterDuff.Mode.SRC_IN
            9 -> PorterDuff.Mode.SRC_ATOP
            14 -> PorterDuff.Mode.MULTIPLY
            15 -> PorterDuff.Mode.SCREEN
            16 -> if (Build.VERSION.SDK_INT >= 11)
                PorterDuff.Mode.valueOf("ADD")
            else
                defaultMode
            else -> defaultMode
        }
    }
}