package com.s16.ktx

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Throws(IOException::class)
fun File.copyTo(outFile: File) {
    val fis = FileInputStream(this)
    val output = FileOutputStream(outFile)

    val buffer = ByteArray(1024)
    var length: Int = fis.read(buffer)
    while (length > 0) {
        output.write(buffer, 0, length)
        length = fis.read(buffer)
    }
    output.flush()
    output.close()
    fis.close()
}

fun Context.getExternalDataDir() : File? {
    val dataFolder = "/Android/data/${packageName}/"
    return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val path = "${Environment.getExternalStorageDirectory().absolutePath}${dataFolder}"
        val folder = File(path)
        if (!folder.exists() && !folder.mkdirs()) {
            null
        } else {
            folder
        }
    } else {
        null
    }
}

fun Context.getDownloadsDir(): File? {
    return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val folder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (folder != null && folder.exists()) {
            folder
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

    } else {
        null
    }
}