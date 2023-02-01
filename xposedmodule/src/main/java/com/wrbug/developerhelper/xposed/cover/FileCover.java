/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/2/1
 * Created by chan on 2023/2/1
 */


package com.wrbug.developerhelper.xposed.cover;

import java.io.BufferedReader;
import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class FileCover {

    public void cover() {

//        XposedHelpers.findAndHookMethod(BufferedReader.class, "readLine", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String result = (String) param.getResult();
//                if (result != null && Cover.includes(result, Cover.coverKeys)) {
//                    param.setResult(new File("").lastModified());
//                    Cover.logXposed("readLine");
//                }
//                super.afterHookedMethod(param);
//            }
//        });

//        XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String result = (String) param.getResult();
//                if (result != null && Cover.includes(result, Cover.coverKeys)) {
//                    param.setResult(false);
//                    Cover.logXposed("readLine");
//                }
//                super.afterHookedMethod(param);
//            }
//        });
    }
}
