package com.s16.poetry

class Constants {
    companion object {
        const val ARG_PARAM_ADD = "add"
        const val ARG_PARAM_ID = "id"

        const val PREFS_SELECT_THEME = "select_theme"
        const val PREFS_MANAGE_CATEGORY = "manage_category"
        const val PREFS_MANAGE_TAG = "manage_tags"
        const val PREFS_BACKUP = "backup"
        const val PREFS_RESTORE = "restore"
        const val PREFS_ABOUT = "about"

        const val BACKUP_FILE_NAME = "PrivateDiaryBackup.zip"

        const val PERMISSION_RESULT_RESTORE = 0x1121
        const val PERMISSION_RESULT_BACKUP = 0x1221
    }
}