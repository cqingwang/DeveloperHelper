package com.wrbug.developerhelper.xposed.dumpdex

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * PackerInfo
 *
 * @author WrBug
 * @since 2018/3/29
 *
 *
 * 加壳类型
 */
object PackerInfo {

    private val classesMap = ArrayList<String>()
    private val typesMap = HashMap<String, Type>()

    //加固应用包含的包名，如果无法脱壳，请将application的包名，加到相应数组
    private val QI_HOO = arrayOf("com.stub.StubApp") //360加固
    private val AI_JIA_MI = arrayOf("s.h.e.l.l.S") //爱加密
    private val BANG_BANG = arrayOf("com.secneo.apkwrapper.ApplicationWrapper") //梆梆加固
    private val TENCENT = arrayOf("com.tencent.StubShell.TxAppEntry") //腾讯加固
    private val BAI_DU = arrayOf("com.baidu.protect.StubApplication") //百度加固
    private val Ding_Xian = arrayOf("com.security.shell.AppStub1") //顶象加固


    init {
        classesMap.addAll(Arrays.asList(*QI_HOO))
        classesMap.addAll(Arrays.asList(*AI_JIA_MI))
        classesMap.addAll(Arrays.asList(*BANG_BANG))
        classesMap.addAll(Arrays.asList(*TENCENT))
        classesMap.addAll(Arrays.asList(*BAI_DU))
        classesMap.addAll(Arrays.asList(*Ding_Xian))


        for (s in QI_HOO) typesMap[s] = Type.QI_HOO

        for (s in AI_JIA_MI) typesMap[s] = Type.AI_JIA_MI

        for (s in BANG_BANG) typesMap[s] = Type.BANG_BANG

        for (s in TENCENT) typesMap[s] = Type.TENCENT

        for (s in BAI_DU) typesMap[s] = Type.BAI_DU

        for (s in Ding_Xian) typesMap[s] = Type.Ding_Xian

    }

    fun log(txt: String) {
        XposedBridge.log("dumpdex.PackerInfo-> $txt")
    }

    fun find(lpparam: XC_LoadPackage.LoadPackageParam): Type? {
        for (s in classesMap) {
            val clazz = XposedHelpers.findClassIfExists(s, lpparam.classLoader)
            if (clazz != null) {
                log("find class:$s")
                val type = getType(s)
                log("find packerType :" + type!!.name)
                type.application = s
                return type
            }
        }
        return null
    }


    private fun getType(packageName: String): Type? {
        return typesMap[packageName]
    }

    enum class Type private constructor(enforceName: String) {

        QI_HOO("360加固"),
        AI_JIA_MI("爱加密"),
        BANG_BANG("梆梆加固"),
        TENCENT("腾讯加固"),
        BAI_DU("百度加固"),
        Ding_Xian("顶象加固");

        var application: String = ""


    }

}
