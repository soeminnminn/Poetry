package com.s16.poetry.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.s16.app.ProgressDialog
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.BackupTask
import com.s16.utils.alertDialog
import com.s16.utils.confirmDialog
import java.io.File

class SettingsFragment : PreferenceFragmentCompat() {

    private val versionName: String
        get() {
            val pInfo = context?.packageManager?.getPackageInfo(context?.packageName, 0)
            if (pInfo != null) {
                return pInfo.versionName
            }
            return ""
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val prefsAbout = findPreference(Constants.PREFS_ABOUT)
        val versionText = context?.getText(R.string.version_text)
        if (versionText != null) {
            prefsAbout.summary = String.format(versionText.toString(), versionName)
        }

        prefsAbout.setOnPreferenceClickListener {
            showAboutDialog()
            true
        }

        findPreference(Constants.PREFS_BACKUP)?.setOnPreferenceClickListener {
            requestPermission(Constants.PERMISSION_RESULT_BACKUP)
            true
        }

        findPreference(Constants.PREFS_RESTORE)?.setOnPreferenceClickListener {
            requestPermission(Constants.PERMISSION_RESULT_RESTORE)
            true
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
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(context!!).apply {
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
        val progressDialog = ProgressDialog(context!!).apply {
            isIndeterminate = true
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setMessage(getText(R.string.message_backup_process))
        }

        val task = object: BackupTask(context!!) {
            override fun onOverride(sender: BackupTask, file: File) {
                context!!.confirmDialog(
                    R.string.title_backup_dialog,
                    R.string.message_backup_override) { _, _ ->
                    sender.override(file)
                }
            }

            override fun onStartBackup(file: File) {
                progressDialog.show()
            }

            override fun onCanceled(message: String) {
                progressDialog.hide()
                context!!.alertDialog(R.string.title_backup_dialog, message)
            }

            override fun onComplete() {
                progressDialog.hide()
                context!!.alertDialog(R.string.title_backup_dialog, R.string.message_backup_complete)
            }
        }
        task.run()
    }

    private fun showRestoreDialog() {
        RestoreFragment.newInstance().show(childFragmentManager, "restoreDialog")
    }
}