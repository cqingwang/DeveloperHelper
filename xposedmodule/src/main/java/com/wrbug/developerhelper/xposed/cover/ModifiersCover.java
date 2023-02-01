/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/2/1
 * Created by chan on 2023/2/1
 */


package com.wrbug.developerhelper.xposed.cover;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class ModifiersCover {
    private int modify;

    public void cover() {
        // 定义全局变量 modify
//        XposedHelpers.findAndHookMethod(Method.class, "getModifiers", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                Method method = (Method) param.thisObject;
//                Cover.logXposed("method:"+method);
//                String[] array = new String[]{"getDeviceId"};
//                String method_name = method.getName();
//                if (Arrays.asList(array).contains(method_name)) {
//                    modify = 0;
//                } else {
//                    modify = (int) param.getResult();
//                }
//                super.afterHookedMethod(param);
//            }
//        });
//
//        XposedHelpers.findAndHookMethod(Modifier.class, "isNative", int.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                param.args[0] = modify;
//                super.beforeHookedMethod(param);
//            }
//        });


    }

}
