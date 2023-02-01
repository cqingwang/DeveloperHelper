package com.wrbug.developerhelper.xposed.util

import com.wrbug.developerhelper.xposed.XposedInit
import de.robv.android.xposed.XposedBridge
import java.io.*

object FileUtils {
    var tag = "FileUtils"
    fun writeByteToFile(data: ByteArray, path: String) {
        try {
            val localFileOutputStream = FileOutputStream(path)
            localFileOutputStream.write(data)
            localFileOutputStream.close()
        } catch (e: Exception) {
            e.message?.let { XposedInit.log(tag, it) }
        }
    }

    fun readFile(file: File): String {
        val builder = StringBuilder()
        try {
            val fr = FileReader(file)
            var ch = fr.read()
            while (ch != -1) {
                builder.append(ch.toChar())
                ch = fr.read()
            }
        } catch (e: IOException) {
        }

        return builder.toString()
    }

    fun whiteFile(file: File, data: String) {
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val fw = FileWriter(file)
            fw.write(data)
            fw.flush()
        } catch (e: IOException) {
        }

    }
}
