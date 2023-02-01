package com.wrbug.developerhelper.xposed.developerhelper

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.jaredrummler.android.shell.Shell
import com.wrbug.developerhelper.xposed.XposedInit
import com.wrbug.developerhelper.xposed.dumpdex.NativeDump
import com.wrbug.developerhelper.xposed.processshare.GlobalConfigProcessData
import com.wrbug.developerhelper.xposed.processshare.ProcessDataManager
import com.wrbug.developerhelper.xposed.saveToFile
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.jetbrains.anko.doAsync
import java.io.File

object DeveloperHelper {
    var tag = "DeveloperHelper"
    fun init(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            "com.wrbug.developerhelper.ui.activity.main.MainActivity",
            lpparam.classLoader,
            "onCreate",
            Bundle::class.java,
            object :
                XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    tryReleaseSo(lpparam, param?.thisObject as Activity)
                }

                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedInit.log(tag, "Main onCreate")
                    val activity = param?.thisObject as Activity
                    val xposedSettingView = XposedHelpers.getObjectField(activity, "xposedSettingView") as View?
                    xposedSettingView?.apply {
                        visibility = View.VISIBLE
                        val configData = ProcessDataManager.get(GlobalConfigProcessData::class.java)
                        XposedHelpers.callMethod(this, "setChecked", configData.isXposedOpen())
                        XposedHelpers.callMethod(
                            this,
                            "setOnCheckedChangeListener",
                            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                                configData.setXposedOpen(isChecked)
                            })
                    }

                }
            })
        EnforceMod.start(lpparam)
    }

    private fun tryReleaseSo(lpparam: XC_LoadPackage.LoadPackageParam, activity: Activity) {
        XposedHelpers.findAndHookMethod(
            "com.wrbug.developerhelper.util.DeviceUtils",
            lpparam.classLoader,
            "isRoot",
            object :
                XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val result = param?.result as Boolean
                    XposedInit.log(tag, "isRoot->$result")
                    if (result) {
                        XposedInit.log(tag, "设备已root,开始释放so文件")
                        doAsync {
                            initProcessDataDir()
//                            saveSo(activity, "armeabi", Native.SO_FILE)
                            saveSo(activity, "armeabi-v7a", NativeDump.SO_FILE_V7a)
                            saveSo(activity, "arm64-v8a", NativeDump.SO_FILE_V8a)
                        }

                    }
                }
            })
    }

    private fun initProcessDataDir() {
        XposedInit.log(tag, "创建processdata目录")
        val dir = "/data/local/tmp/developerHelper"
        val commandResult = Shell.SU.run("mkdir -p $dir && chmod -R 777 $dir")
        if (commandResult.isSuccessful) {
            XposedInit.log(tag, "processdata目录创建成功")
        } else {
            XposedInit.log(tag, "processdata目录创建失败：${commandResult.getStderr()}")
        }
    }


    private fun saveSo(activity: Activity, libPath: String, fileName: String) {
        XposedInit.log(tag, "正在释放$fileName")
        val tmpDir = File("/data/local/tmp")
        val soFile = File(tmpDir, fileName)
        val inputStream = activity.classLoader.getResource("lib/$libPath/libnativeDump.so").openStream()
        if (inputStream == null) {
            XposedInit.log(tag, "$libPath/libnativeDump.so 不存在")
            return
        }
        XposedInit.log(tag, "已获取asset")
        val tmpFile = File(activity.cacheDir, fileName)
        inputStream.saveToFile(tmpFile)
        val commandResult =
            Shell.SU.run("cp ${tmpFile.absolutePath} ${soFile.absolutePath}", "chmod 777 ${soFile.absolutePath}")
        if (commandResult.isSuccessful) {
            XposedInit.log(tag, "$fileName 释放成功")
        } else {
            XposedInit.log(tag, "$fileName 释放失败：${commandResult.getStderr()}")
        }
        tmpFile.delete()
    }
}