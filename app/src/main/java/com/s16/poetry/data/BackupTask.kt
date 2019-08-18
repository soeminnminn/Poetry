package com.s16.poetry.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.s16.poetry.Constants
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

abstract class BackupTask(private val context: Context): Runnable {

    abstract fun onStartBackup(file: File)

    abstract fun onOverride(sender: BackupTask, file: File)

    abstract fun onCanceled(message: String)

    abstract fun onComplete()

    override fun run() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val extDir = Environment.getExternalStorageDirectory()

            val files = extDir.listFiles { _, name -> name == "${Constants.BACKUP_FILE_NAME}.zip" }
            if (files.isNotEmpty()) {
                onOverride(this, files[0])
            } else {
                val file = File(extDir, "${Constants.BACKUP_FILE_NAME}.zip")
                onStartBackup(file)
                runTask(file)
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

    private fun runTask(file: File) {
        val dbManager = DbManager(context)
        dbManager.close()

        runBlocking {
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