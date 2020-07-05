package com.s16.poetry.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.s16.app.ProgressDialog
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.utils.ThemeManager
import com.s16.poetry.activity.ManageCategoriesActivity
import com.s16.poetry.activity.ManageTagsActivity
import com.s16.poetry.activity.SyncActivity
import com.s16.poetry.data.BackupTask
import com.s16.ktx.alertDialog
import com.s16.ktx.confirmDialog
import com.s16.ktx.startActivity

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var backupTask: BackupTask? = null

    private val versionName: String
        get() {
            val pInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            if (pInfo != null) {
                return pInfo.versionName
            }
            return ""
        }

    private fun getSelectedTheme(sharedPreferences: SharedPreferences)
         = sharedPreferences.getString(Constants.PREFS_SELECT_THEME, "system")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val selectedOption = getSelectedTheme(preferenceScreen.sharedPreferences)
        setThemeSummary(selectedOption)

        findPreference<Preference>(Constants.PREFS_MANAGE_CATEGORY)?.setOnPreferenceClickListener {
            startActivity<ManageCategoriesActivity>()
            true
        }

        findPreference<Preference>(Constants.PREFS_MANAGE_TAG)?.setOnPreferenceClickListener {
            startActivity<ManageTagsActivity>()
            true
        }

        findPreference<Preference>(Constants.PREFS_BACKUP)?.setOnPreferenceClickListener {
            requestPermission(Constants.PERMISSION_RESULT_BACKUP)
            true
        }

        findPreference<Preference>(Constants.PREFS_RESTORE)?.setOnPreferenceClickListener {
            requestPermission(Constants.PERMISSION_RESULT_RESTORE)
            true
        }

        findPreference<Preference>(Constants.PREFS_SYNC)?.setOnPreferenceClickListener {
            startActivity<SyncActivity>()
            true
        }

        val prefsAbout = findPreference<Preference>(Constants.PREFS_ABOUT)
        val versionText = context?.getText(R.string.version_text)
        if (versionText != null) {
            prefsAbout!!.summary = String.format(versionText.toString(), versionName)
        }

        prefsAbout!!.setOnPreferenceClickListener {
            showAboutDialog()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        backupTask?.cancel()
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == Constants.PREFS_SELECT_THEME) {
            val selectedOption = getSelectedTheme(sharedPreferences)
            ThemeManager.setTheme(selectedOption)
            setThemeSummary(selectedOption)
        }
    }

    private fun setThemeSummary(selected: String?) {
        findPreference<Preference>(Constants.PREFS_SELECT_THEME)?.apply {
            summary = when(selected) {
                Constants.PREFS_THEMES_LIGHT -> getString(R.string.prefs_theme_light)
                Constants.PREFS_THEMES_DARK -> getString(R.string.prefs_theme_dark)
                Constants.PREFS_THEMES_BATTERY -> getString(R.string.prefs_theme_battery)
                else -> getString(R.string.prefs_theme_system)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onPermissionsGranted(requestCode: Int) {
        when(requestCode) {
            Constants.PERMISSION_RESULT_BACKUP -> {
                doBackup()
            }
            Constants.PERMISSION_RESULT_RESTORE -> {
                showRestoreDialog()
            }
            else -> { }
        }
    }

    private fun requestPermission(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(R.string.title_request_permission)
                    setMessage(R.string.message_request_permission)
                    setNegativeButton(android.R.string.cancel, null)
                    setPositiveButton(android.R.string.ok) { dialog, _ ->
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            requestCode)
                        dialog.dismiss()
                    }
                }.show()

            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode)
            }

        } else {
            onPermissionsGranted(requestCode)
        }
    }

    private fun showAboutDialog() {
        AboutFragment.newInstance().show(childFragmentManager, "aboutDialog")
    }

    @SuppressLint("StaticFieldLeak")
    private fun doBackup() {
        val progressDialog = ProgressDialog(requireContext()).apply {
            isIndeterminate = true
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setMessage(getText(R.string.message_backup_process))
        }

        backupTask = BackupTask(
            requireContext(),
            onStartBackup = {
                progressDialog.show()
            },
            onOverride = { sender, file ->
                requireContext().confirmDialog(
                    R.string.title_backup_dialog,
                    R.string.message_backup_override) { _, _ ->
                    sender.override(file)
                }
            },
            onCanceled = { message ->
                progressDialog.hide()
                requireContext().alertDialog(R.string.title_backup_dialog, message)
            },
            onComplete = {
                progressDialog.hide()
                requireContext().alertDialog(R.string.title_backup_dialog, R.string.message_backup_complete)
            }
        )
        backupTask?.run()
    }

    private fun showRestoreDialog() {
        RestoreFragment.newInstance().show(childFragmentManager, "restoreDialog")
    }
}