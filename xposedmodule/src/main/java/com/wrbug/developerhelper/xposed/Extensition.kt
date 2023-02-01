package com.wrbug.developerhelper.xposed

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun InputStream.saveToFile(file: File) {
    if (file.exists().not()) {
        file.createNewFile()
    }
    val fileOutputStream = FileOutputStream(file)
    fileOutputStream.write(readBytes())
    fileOutputStream.flush()
    fileOutputStream.close()
}