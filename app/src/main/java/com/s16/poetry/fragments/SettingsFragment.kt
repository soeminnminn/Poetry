package com.s16.poetry.fragments

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
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
    }

    private val versionName: String
        get() {
            val pInfo = context?.packageManager?.getPackageInfo(context?.packageName, 0)
            if (pInfo != null) {
                return pInfo.versionName
            }
            return ""
        }

    private fun showAboutDialog() {
        var aboutText = context!!.getText(R.string.about_text)
        val dialogBuilder = AlertDialog.Builder(context!!).apply {
            setTitle(R.string.prefs_about)
            setIcon(android.R.drawable.ic_dialog_info)
            setMessage(Html.fromHtml(aboutText.toString()))
            setPositiveButton(null, null)
            setNegativeButton(android.R.string.ok) { _, _ ->
            }
        }
        dialogBuilder.create().show()
    }
}