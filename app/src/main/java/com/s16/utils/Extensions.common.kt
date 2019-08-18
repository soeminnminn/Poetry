package com.s16.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class U {
    companion object {
        @JvmStatic
        fun isEmpty(value: Any?): Boolean {
            return value == null || "$value".isEmpty()
        }

        @JvmStatic
        inline fun <reified T> IIF(condition: Boolean, truePart: T?, falsePart: T?): T? {
            return if (condition) truePart else falsePart
        }
    }
}

inline fun <reified T: Any> Any.letTo(fn: (value: T) -> Unit) = fn(this as T)


fun <T1: Any, T2: Any> let2(v1: T1?, v2: T2?, fn: (val1: T1, val2: T2) -> Unit) {
    if (v1 != null && v2 != null) {
        fn(v1, v2)
    }
}

fun <T1: Any, T2: Any, T3: Any> let3(v1: T1?, v2: T2?, v3: T3?, fn: (val1: T1, val2: T2, val3: T3) -> Unit) {
    if (v1 != null && v2 != null && v3 != null) {
        fn(v1, v2, v3)
    }
}

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