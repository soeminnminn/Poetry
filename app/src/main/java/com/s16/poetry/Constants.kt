package com.s16.poetry

import android.content.Context
import android.graphics.Typeface

object Constants {
    const val ARG_PARAM_ADD = "add"
    const val ARG_PARAM_ID = "id"
    const val ARG_PARAM_UUID = "uuid"
    const val ARG_PARAM_NAME = "name"
    const val ARG_PARAM_CATEGORY = "category"
    const val ARG_PARAM_TAGS = "tags"

    const val RESULT_SELECT_CATEGORY = 0x0021
    const val RESULT_SELECT_TAG = 0x0022

    const val RESULT_NONE = 0
    const val RESULT_OK = 1

    const val DISPLAY_DATE_FORMAT = "dd/MM/yyyy"

    const val PREFS_SELECT_THEME = "select_theme"
    const val PREFS_THEMES_LIGHT = "light"
    const val PREFS_THEMES_DARK = "dark"
    const val PREFS_THEMES_BATTERY = "battery"
    const val PREFS_THEMES_SYSTEM = "system"

    const val PREFS_AUTHOR_NAME = "author_name"
    const val PREFS_MANAGE_CATEGORY = "manage_category"
    const val PREFS_MANAGE_TAG = "manage_tags"
    const val PREFS_MMFONT = "prefs_mmfont"
    const val PREFS_BACKUP = "backup"
    const val PREFS_RESTORE = "restore"
    const val PREFS_SYNC = "sync"
    const val PREFS_ABOUT = "about"

    const val BACKUP_FILE_NAME = "PoetryBackup"

    const val PERMISSION_RESULT_RESTORE = 0x1211
    const val PERMISSION_RESULT_BACKUP = 0x1221
}