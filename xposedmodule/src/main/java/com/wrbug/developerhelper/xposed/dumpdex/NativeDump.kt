package com.wrbug.developerhelper.xposed.dumpdex

import com.wrbug.developerhelper.xposed.XposedInit

/**
 * Native
 *
 * @author WrBug
 * @since 2018/3/23
 */
object NativeDump {
    var tag = "NativeDump";
    const val SO_FILE_V7a = "nativeDumpV7a.so"
    const val SO_FILE_V8a = "nativeDumpV8a.so"

    init {
        if (soload(SO_FILE_V7a) || soload(SO_FILE_V8a)) {
            XposedInit.log(tag, "动态库加载成功")
        }
    }

    private fun soload(file: String) = try {
        System.load("/data/data/${Dump.targetpackage}/cache/$file")
        XposedInit.log(tag, "loaded $file")
        true
    } catch (t: Throwable) {
        XposedInit.log(tag, "load $file failed")
        false
    }

    external fun dump(packageName: String)
}
