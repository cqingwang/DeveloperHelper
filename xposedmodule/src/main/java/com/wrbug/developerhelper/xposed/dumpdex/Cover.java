/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/2/1
 * Created by chan on 2023/2/1
 */


package com.wrbug.developerhelper.xposed.dumpdex;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Cover {
    public static void cover(XC_LoadPackage.LoadPackageParam lp) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    lp.classLoader = ((Context) param.args[0]).getClassLoader();

                    XposedHelpers.findAndHookMethod(
                            "com.tencent.bugly.beta.tinker.TinkerUncaughtExceptionHandler", lp.classLoader,
                            "uncaughtException", Thread.class, Throwable.class, new XC_MethodReplacement() {
                                @Override
                                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                    return null;
                                }
                            });

                }
            });



    }
}
