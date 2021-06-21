package com.s16.poetry.data

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.s16.poetry.Constants
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal class BackupTask(
    private val context: Context,
    private val onStartBackup: (file: File) -> Unit,
    private val onOverride: (sender: BackupTask, file: File) -> Unit,
    private val onCanceled: (message: String) -> Unit,
    private val onComplete: () -> Unit
): Runnable {

    private var uiScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    @Suppress("DEPRECATION")
    override fun run() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                && Environment.isExternalStorageEmulated()) {
            var extDir = Environment.getExternalStorageDirectory()

            if (!extDir.exists() || !extDir.canWrite()) {
                val externalStorageVolumes: Array<out File> =
                    ContextCompat.getExternalFilesDirs(context.applicationContext, null)

                extDir = externalStorageVolumes.first()
            }

            if (!extDir.exists() || !extDir.canWrite()) {
                extDir = context.getExternalFilesDir(null)
            }

            if (!extDir.exists() || !extDir.canWrite()) {
                onCanceled("Can not write backup file.")

            } else {
                val files = extDir.absoluteFile.listFiles { _, name -> name == "${Constants.BACKUP_FILE_NAME}.zip" }

                if (files != null && files.isNotEmpty()) {
                    onOverride(this, files[0])
                } else {
                    val file = File(extDir, "${Constants.BACKUP_FILE_NAME}.zip")
                    onStartBackup(file)
                    runTask(file)
                }
            }

        } else {
            onCanceled("Can not write backup file.")
        }
    }

    fun override(file: File) {
        if (file.delete()) {
            onStartBackup(file)
            runTask(file)
        } else {
            onCanceled("Can not delete backup file.")
        }
    }

    fun cancel() {
        job?.cancel()

    }

    private fun runTask(file: File) {
        val dbManager = DbManager(context)
        dbManager.close()

        job = uiScope.launch {
            val result = createZip(dbManager.getDatabaseFile(context), file)
            if (result != null) {
                Log.i("BackupTask", result)
                onCanceled(result)
            } else {
                onComplete()
            }
        }
    }

    private suspend fun createZip(srcFile: File, destFile: File): String? =
        withContext(Dispatchers.IO) {
            try {
                val files = listOf(srcFile.name, "${srcFile.name}-shm", "${srcFile.name}-wal")

                val buffer = ByteArray(1024)
                val zipOut = ZipOutputStream(FileOutputStream(destFile))

                files.forEach {
                    val currentFile = File(srcFile.parentFile, it)
                    if (currentFile.exists()) {
                        val zipName = it.replace(srcFile.name, "${Constants.BACKUP_FILE_NAME}.db")

                        // Add ZIP entry to output stream.
                        zipOut.putNextEntry(ZipEntry(zipName))

                        // Transfer bytes from the file to the ZIP file
                        val fileIn = FileInputStream(currentFile)
                        var length: Int = fileIn.read(buffer)
                        while (length > 0) {
                            zipOut.write(buffer, 0, length)
                            length = fileIn.read(buffer)
                        }

                        // Complete the entry
                        zipOut.closeEntry()
                        fileIn.close()
                    }
                }

                // Complete the ZIP file
                zipOut.close()
                null

            } catch (e: Exception) {
                e.printStackTrace()
                e.localizedMessage
            }
        }
}