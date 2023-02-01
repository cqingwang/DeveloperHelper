package com.wrbug.developerhelper.xposed.dumpdex

import android.os.Build
import android.os.Process
import com.wrbug.developerhelper.xposed.XposedInit
import com.wrbug.developerhelper.xposed.processshare.DumpDexListProcessData
import com.wrbug.developerhelper.xposed.processshare.ProcessDataManager
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

object Dump {
    var tag = "Dump"
    var targetpackage = ""

    fun isNativeHook(): Boolean {
        return Build.VERSION.SDK_INT > 23
    }

    fun init(lpparam: XC_LoadPackage.LoadPackageParam) {
        val type = PackerInfo.find(lpparam) ?: return
        XposedInit.log(tag, "sheller-> ${type.name}, ${lpparam.packageName},")
        val data = ProcessDataManager.get(DumpDexListProcessData::class.java)
        val packageNames = data.getData() ?: return
        val packageName = lpparam.packageName
        if (packageNames.contains(packageName).not()) {
            XposedInit.log(tag, "未包含 $packageName ,忽略")
            return
        }
        copySoToCacheDir(packageName)
        XposedInit.log(tag, "pid:${Process.myPid()},tid:${Process.myTid()},uid:${Process.myUid()},准备脱壳：$packageName")
        if (lpparam.packageName == packageName) {
            val path = "/data/data/$packageName/dump"
            val parent = File(path)
            if (!parent.exists() || !parent.isDirectory) {
                parent.mkdirs()
            }
            XposedInit.log("Dump", "sdk version:" + Build.VERSION.SDK_INT)
            targetpackage = lpparam.packageName
            if (isNativeHook()) {
                NativeDump.dump(targetpackage)
            } else {
                LowSdkDump.init(lpparam, type)
            }

        }
    }

    private fun copySoToCacheDir(packageName: String) {
        copySoFile(packageName, NativeDump.SO_FILE_V7a)
        copySoFile(packageName, NativeDump.SO_FILE_V8a)
    }

    private fun copySoFile(packageName: String, filename: String) {
        val input = File("/data/local/tmp/$filename")
        val target = File("/data/data/$packageName/cache", filename)
        if (target.exists()) {
            target.delete()
        }
        input.copyTo(target)
    }
}
