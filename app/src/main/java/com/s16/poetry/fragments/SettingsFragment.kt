package com.s16.poetry.fragments

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import com.s16.app.ProgressDialog
import com.s16.poetry.Constants
import com.s16.poetry.R

class SettingsFragment : PreferenceFragmentCompat() {

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
            doBackup()
            true
        }

        findPreference(Constants.PREFS_RESTORE)?.setOnPreferenceClickListener {
            showRestoreDialog()
            true
        }
    }

    private val versionName: String
        get() {
            val pInfo = context?.packageManager?.getPackageInfo(context?.packageName, 0)
            if (pInfo != null) {
                return pInfo.versionName
            }
            return ""
        }

    private fun dpToPixel(dp: Int): Int {
        val metrics = context!!.resources.displayMetrics
        val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return px.toInt()
    }

    private fun showAboutDialog() {
        var aboutText = context!!.getText(R.string.about_text)
        val paddingX = dpToPixel(20)
        val paddingY = dpToPixel(16)
        val message = TextView(context).apply {
            setPadding(paddingX, paddingY, paddingX, paddingY)
            movementMethod = LinkMovementMethod.getInstance()
            text = Html.fromHtml(aboutText.toString())
        }

        val dialogBuilder = AlertDialog.Builder(context!!).apply {
            setTitle(R.string.prefs_about)
            setIcon(android.R.drawable.ic_dialog_info)
            setView(message)
            setPositiveButton(null, null)
            setNegativeButton(android.R.string.ok) { _, _ ->
            }
        }
        dialogBuilder.create().show()
    }

    private fun doBackup() {
        val progressDialog = ProgressDialog(context!!).apply {
            isIndeterminate = true
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setMessage(getText(R.string.message_backup_process))
        }
        progressDialog.show()

    }

    private fun showRestoreDialog() {
        RestoreFragment.newInstance().show(childFragmentManager, "restoreDialog")
    }
}