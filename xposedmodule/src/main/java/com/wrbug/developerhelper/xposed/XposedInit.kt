package com.wrbug.developerhelper.xposed

import com.wrbug.developerhelper.xposed.developerhelper.DeveloperHelper
import com.wrbug.developerhelper.xposed.dumpdex.Dump
import com.wrbug.developerhelper.xposed.processshare.GlobalConfigProcessData
import com.wrbug.developerhelper.xposed.processshare.ProcessDataManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * XposedInit
 *
 * @author wrbug
 * @since 2018/3/20
 */
class XposedInit : IXposedHookLoadPackage {
    var tag = "XposedInit"
    var configData: GlobalConfigProcessData? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        log(tag, "handleLoadPackage${lpparam.packageName}")
        if (lpparam.packageName == SELF_PACKAGE_NAME) {
            log(tag, "call DeveloperHelper.init")
            DeveloperHelper.init(lpparam)
            return
        }
        if (configData == null) {
            configData = ProcessDataManager.get(GlobalConfigProcessData::class.java)
        }
        if (configData == null) {
            return
        }
        if (configData?.isXposedOpen() == false) {
            log(tag, "xposed已关闭")
            return
        }
        Dump.init(lpparam)
    }

    companion object {
        const val SELF_PACKAGE_NAME = "com.wrbug.developerhelper"

        fun log(tag: String, msg: String) {
            XposedBridge.log("developerhelper:$tag:$msg")
        }
    }
}
