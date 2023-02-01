//
// Created by WrBug on 2018/3/23.
//
#include "native.h"
#include "inlineHook.h"

#define TAG "LSPosed.NativeDump.dump>"

extern "C" JNIEXPORT void JNICALL Java_com_wrbug_developerhelper_xposed_dumpdex_NativeDump_dump
        (JNIEnv *env, jobject obj, jstring packageName) {
    static bool is_hook = false;
    char *pkgName = (char *) env->GetStringUTFChars(packageName, 0);
    __android_log_print(ANDROID_LOG_ERROR, TAG, "hook start:%s", pkgName);
    if (is_hook) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "hooked ignore");
        return;
    }

    init_package_name(pkgName);
    env->ReleaseStringChars(packageName, (const jchar *) pkgName);
    ndk_init(env);
    void *handle = ndk_dlopen("libart.so", RTLD_NOW);
    if (handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error: unable to find the SO : libart.so");
        return;
    }
    char* dlOpenSyml=get_open_function_flag();
    __android_log_print(ANDROID_LOG_INFO, TAG, "dlopen_syml:%s",dlOpenSyml);
    void *open_common_addr = ndk_dlsym(handle, dlOpenSyml);
    if (open_common_addr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error: unable to find the Symbol : ");
        return;
    }
#if defined(__aarch64__)
    A64HookFunction(open_common_addr, get_new_open_function_addr(), get_old_open_function_addr());
    __android_log_print(ANDROID_LOG_DEFAULT, TAG, "loaded so: libart.so");
#elif defined(__arm__)
    if (registerInlineHook((uint32_t) open_common_addr, (uint32_t) get_new_open_function_addr(),
                           (uint32_t **) get_old_open_function_addr()) != ELE7EN_OK) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "register1 hook failed!");
        return;
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "register1 hook success!");
    }
    if (inlineHook((uint32_t) open_common_addr) != ELE7EN_OK) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "register2 hook failed!");
        return;
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "register2 hook success!");
    }
    __android_log_print(ANDROID_LOG_DEFAULT, TAG, "loaded so: libart.so");
#endif

    __android_log_print(ANDROID_LOG_ERROR, TAG, "hook end");
    is_hook = true;
}
