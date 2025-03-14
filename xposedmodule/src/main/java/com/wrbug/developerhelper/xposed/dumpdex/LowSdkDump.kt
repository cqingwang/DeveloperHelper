package com.wrbug.developerhelper.xposed.dumpdex

import android.app.Application
import android.content.Context
import com.wrbug.developerhelper.xposed.XposedInit

import com.wrbug.developerhelper.xposed.util.FileUtils

import java.io.File

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.ArrayList

object LowSdkDump {
    var tag = "LowSdkDump";


    fun init(lpparam: XC_LoadPackage.LoadPackageParam, type: PackerInfo.Type) {
        XposedInit.log(tag, "start hook Instrumentation#newApplication")
        if (Dump.isNativeHook()) {
            NativeDump.dump(lpparam.packageName)
        }
        XposedHelpers.findAndHookMethod(
            "android.app.Instrumentation",
            lpparam.classLoader,
            "newApplication",
            ClassLoader::class.java,
            String::class.java,
            Context::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    param?.apply {
                        XposedInit.log(tag, "Application=$result")
                        dump(
                            lpparam.packageName, result.javaClass
                        )
                        attachBaseContextHook(lpparam, result as Application)
                    }
                }
            })

        XposedHelpers.findClassIfExists(type.shellClassName, lpparam.classLoader)?.apply {
            var list = ArrayList<String>()
            for (method in methods) {
                list.add(method.name)
            }
            for (method in declaredMethods) {
                list.add(method.name)
            }
            XposedInit.log(tag, "开始hook application ${list.size} 方法")
            list.forEach {
                XposedBridge.hookAllMethods(this, it, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        param?.args?.apply {
                            if (this.isEmpty().not()) {
                                this.forEach { arg ->
                                    if (arg is Context) {
                                        XposedInit.log(tag, "hook $arg")
                                        dump(lpparam.packageName, arg.javaClass)
                                        attachBaseContextHook(lpparam, arg)
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    private fun dump(packageName: String, aClass: Class<*>?) {
        val dexCache = XposedHelpers.getObjectField(aClass, "dexCache")
        XposedInit.log(tag, "decCache=$dexCache")
        val o = XposedHelpers.callMethod(dexCache, "getDex")
        val bytes = XposedHelpers.callMethod(o, "getBytes") as ByteArray
        val path = "/data/data/$packageName/dump"
        val file = File(path, "source-" + bytes.size + ".dex")
        if (file.exists()) {
            XposedInit.log(tag, file.name + " exists")
            return
        }
        FileUtils.writeByteToFile(bytes, file.absolutePath)
    }


    private fun attachBaseContextHook(lpparam: XC_LoadPackage.LoadPackageParam, context: Context) {
        val classLoader = context.classLoader
        XposedHelpers.findAndHookMethod(
            ClassLoader::class.java,
            "loadClass",
            String::class.java,
            Boolean::class.javaPrimitiveType,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    XposedInit.log(tag, "loadClass->" + param!!.args[0])
                    val result = param.result as Class<*>
                    dump(lpparam.packageName, result)
                }
            })
        XposedHelpers.findAndHookMethod(
            "java.lang.ClassLoader",
            classLoader,
            "loadClass",
            String::class.java,
            Boolean::class.javaPrimitiveType,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                    XposedInit.log(tag, "loadClassWithclassLoader->" + param!!.args[0])
                    val result = param.result as Class<*>
                    dump(lpparam.packageName, result)
                }
            })
    }
}
