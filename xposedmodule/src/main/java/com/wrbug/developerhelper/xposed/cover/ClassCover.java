/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/2/1
 * Created by chan on 2023/2/1
 */


package com.wrbug.developerhelper.xposed.cover;

import android.util.Log;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class ClassCover {

    public void cover() {
        XposedHelpers.findAndHookMethod(String.class, "contains", CharSequence.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args != null && param.args[0] != null) {
                    if (Cover.includes(param.args[0].toString(), Cover.coverKeys)) {
                        param.setResult(false);
                        Cover.logXposed("contains");
                    }
                }
                super.afterHookedMethod(param);
            }
        });


        XposedHelpers.findAndHookMethod(StackTraceElement.class, "getClassName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if (result != null && Cover.includes(result.toString(), Cover.coverKeys)) {
                    param.setResult("");
                    Cover.logXposed("getClassName");
                }
                super.afterHookedMethod(param);
            }
        });


        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean hasArg = param.args != null || param.args[0] != null;
                if (hasArg && param.args[0].toString().startsWith("de.robv.android.xposed.")) {
                    param.args[0] = "de.robv.android.xposed.XXABC"; // 改成一个不存在的类
                    Cover.logXposed("loadClass");
                }
                super.beforeHookedMethod(param);
            }
        });


        XposedHelpers.findAndHookMethod(Thread.class, "getStackTrace", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                StackTraceElement[] st = (StackTraceElement[]) param.getResult();
                String sts = "";
                for (StackTraceElement ste : st) {
                    sts += ste.toString() + "\n";
                }
                Log.e("StackTrace", sts);
                super.afterHookedMethod(param);
            }
        });


    }
}
