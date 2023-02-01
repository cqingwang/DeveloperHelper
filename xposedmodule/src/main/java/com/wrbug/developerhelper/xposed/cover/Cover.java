/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/2/1
 * Created by chan on 2023/2/1
 */


package com.wrbug.developerhelper.xposed.cover;

import com.wrbug.developerhelper.xposed.XposedInit;

import java.io.BufferedReader;
import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Cover {
    public static void logXposed(String info) {
        XposedInit.Companion.log("cover", "cover: " + info);
    }

    public static String[] coverKeys = new String[]{
            "Xposed",
            "xposed",
            "lsposed",
            "XposedBridge",
            "XposedHelpers",
            "disableHooks",
            "findAndHookMethod",
            "methodCache",
            "constructorCache",
            "fieldCache",
            "XposedHelper",
            "XposedBridge.jar",
            "xposed.installer",
            "com.android.internal.os.ZygoteInit",
            "de.robv.android",
            "de.robv.android.xposed.installer",
            "de.robv.android.xposed.XposedBridge"
    };

    public static void cover(XC_LoadPackage.LoadPackageParam lparams) {
        try {
            keep(lparams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean includes(String target, String[] keys) {
        for (String key : keys) {
            if (target.indexOf(key) > -1) return true;
        }
        return false;
    }

    private static void keep(XC_LoadPackage.LoadPackageParam lparams) throws Exception {
        new ClassCover().cover();
        new ModifiersCover().cover();
        new FileCover().cover();
    }


}
