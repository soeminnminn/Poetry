package com.s16.poetry.fragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import com.s16.app.ThemeDialogFragment
import com.s16.poetry.Constants
import com.s16.poetry.R
import com.s16.poetry.data.RestoreTask
import com.s16.widget.DialogButtonBar
import com.s16.widget.setNegativeButton
import com.s16.widget.setPositiveButton
import java.io.File


class RestoreFragment : ThemeDialogFragment() {

    private var restoreTask: RestoreTask? = null
    private lateinit var message: TextView
    private lateinit var loading: ViewGroup
    private lateinit var dialogButtons: DialogButtonBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restore, container, false)
        message = view.findViewById(android.R.id.message)
        message.setText(R.string.message_restore_warning)

        loading = view.findViewById(R.id.loading)
        dialogButtons = view.findViewById(R.id.dialogButtons)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        dialogButtons.setNegativeButton(android.R.string.cancel) { _, _ ->
            dialog!!.cancel()
        }
        dialogButtons.setPositiveButton(android.R.string.ok) { _, _ ->
            doRestore()
        }
    }

    override fun onDestroy() {
        restoreTask?.cancel()
        super.onDestroy()
    }

    private fun doRestore() {
        message.visibility = View.GONE
        loading.visibility = View.VISIBLE
        dialogButtons.visibility = View.GONE

        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val extDir = Environment.getExternalStorageDirectory()
            Log.i("doRestore", "extDir = ${extDir.absolutePath}")

            val files = extDir.listFiles { _, name -> name == "${Constants.BACKUP_FILE_NAME}.zip" }
            if (files.isEmpty()) {
                showMessage(R.string.message_backup_file_not_found)
            } else {
                runRestoreTask(files.first())
            }
        } else {
            showMessage(R.string.message_backup_file_not_found)
        }
    }

    private fun runRestoreTask(file: File) {
        restoreTask = RestoreTask(
            context!!, file,
            onCanceled = { message ->
                showMessage(message)
            },
            onComplete = {
                showMessage(R.string.message_restore_complete)
            }
        )
        restoreTask?.run()
    }

    private fun showMessage(@StringRes msg: Int) {
        message.setText(msg)
        message.visibility = View.VISIBLE
        message.visibility = View.VISIBLE
        loading.visibility = View.GONE

        dialogButtons.setNegativeButton(null, null)
        dialogButtons.setPositiveButton(android.R.string.ok) { _, _ ->
            dismiss()
        }
        dialogButtons.visibility = View.VISIBLE
    }

    private fun showMessage(msg: String) {
        message.text = msg
        message.visibility = View.VISIBLE
        message.visibility = View.VISIBLE
        loading.visibility = View.GONE

        dialogButtons.setNegativeButton(null, null)
        dialogButtons.setPositiveButton(android.R.string.ok) { _, _ ->
            dismiss()
        }
        dialogButtons.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RestoreFragment().apply {
                arguments = Bundle()
            }
    }
}
