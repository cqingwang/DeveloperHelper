//
// Created by WrBug on 2018/4/8.
//


#include "deviceutils.h"
#include "fileutils.h"

#define TAG "LSPosed.NativeDump.device_utils >"

const static long DEX_MIN_LEN = 102400L;
static int sdk_int = 0;

void init_sdk_init() {
    if (sdk_int != 0) {
        return;
    }
    char sdk[PROP_VALUE_MAX];
    __system_property_get("ro.build.version.sdk", sdk);
    sdk_int = atoi(sdk);
}

bool isArm64() {
#if defined(__aarch64__)
    return true;
#else
    return false;
#endif
}


bool isAndroid8() {
    return sdk_int == 27 || sdk_int == 26;
}

bool isAndroid9() {
    return sdk_int == 28;
}

bool isAndroid10() {
    return sdk_int == 29;
}

bool is_Android7() {
    return sdk_int == 25 || sdk_int == 24;
}

bool isAndroid6() {
    return sdk_int == 23;
}

char *get_open_function_flag() {
    init_sdk_init();
    if (isArm64()) {
        if (isAndroid9() || isAndroid10()) {
            return "_ZN3art13DexFileLoader10OpenCommonEPKhmS2_mRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPKNS_10OatDexFileEbbPS9_NS3_10unique_ptrINS_16DexFileContainerENS3_14default_deleteISH_EEEEPNS0_12VerifyResultE";
        }
        if (isAndroid8()) {
            return "_ZN3art7DexFile10OpenCommonEPKhmRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPKNS_10OatDexFileEbbPS9_PNS0_12VerifyResultE";
        }
        if (is_Android7() || isAndroid6()) {
            return "_ZN3art7DexFile10OpenMemoryEPKhmRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPKNS_10OatDexFileEPS9_";
        }
        return "";
    }

    if (isAndroid9() || isAndroid10()) {
        return "_ZN3art13DexFileLoader10OpenCommonEPKhjS2_jRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPKNS_10OatDexFileEbbPS9_NS3_10unique_ptrINS_16DexFileContainerENS3_14default_deleteISH_EEEEPNS0_12VerifyResultE";
    }
    if (isAndroid8()) {
        return "_ZN3art7DexFile10OpenCommonEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPKNS_10OatDexFileEbbPS9_PNS0_12VerifyResultE";
    }
    if (is_Android7() || isAndroid6()) {
        return "_ZN3art7DexFile10OpenMemoryEPKhjRKNSt3__112basic_stringIcNS3_11char_traitsIcEENS3_9allocatorIcEEEEjPNS_6MemMapEPKNS_10OatDexFileEPS9_";
    }

    return "";
}

//64位
/////////////////////
static void *
(*old_arm64_open_common_oreo)(uint8_t *, size_t, void *, uint32_t, void *, bool, bool, void *, void *);

static void *new_arm64_open_common_oreo(uint8_t *base, size_t size, void *location,
                                        uint32_t location_checksum, void *oat_dex_file,
                                        bool verify,
                                        bool verify_checksum,
                                        void *error_meessage, void *verify_result) {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "new_arm64_open_common_oreo");
    if (size < DEX_MIN_LEN) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "size =%u", size);
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "保存64位apk dex文件：%p", base);
        save_dex_file(base, size);
    }
    void *result = old_arm64_open_common_oreo(base, size, location, location_checksum,
                                              oat_dex_file, verify, verify_checksum,
                                              error_meessage,
                                              verify_result);
    return result;
}


static void *
(*old_arm64_open_common_pie)(uint8_t *, size_t, uint8_t *, size_t, void *, uint32_t, void *, bool, bool, void *, void *,
                             void *);

static void *new_arm64_open_common_pie(uint8_t *base, size_t size, uint8_t *data_base, size_t data_size,
                                       void *location,
                                       uint32_t location_checksum, void *oat_dex_file,
                                       bool verify,
                                       bool verify_checksum,
                                       void *error_meessage, void *container, void *verify_result) {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "new_arm64_open_common_pie");
    if (size < DEX_MIN_LEN) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "size =%u", size);
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "保存64位apk dex文件：%p", base);
        save_dex_file(base, size);
    }
    void *result = old_arm64_open_common_pie(base, size, data_base, data_size, location, location_checksum,
                                             oat_dex_file, verify, verify_checksum,
                                             error_meessage, container, verify_result);
    return result;
}
/////////////////////



/////////////////////
static void *(*old_arm64_open_memory)(uint8_t *base,
                                      size_t size, void *location,
                                      uint32_t location_checksum,
                                      void *mem_map,
                                      void *oat_dex_file, void *error_msg);

static void *
(new_arm64_open_memory)(uint8_t *base, size_t size, void *location,
                        uint32_t location_checksum, void *mem_map,
                        void *oat_dex_file, void *error_msg) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "new_arm64_open_memory");

    if (size < DEX_MIN_LEN) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "size < DEX_MIN_LEN");
    } else {
        save_dex_file(base, size);
    }
    return (*old_arm64_open_memory)(base, size, location, location_checksum, mem_map,
                                    oat_dex_file, error_msg);
}
/////////////////////

//32位


/////////////////////
static void *(*old_nougat_open_memory)(void *DexFile_thiz, uint8_t *base,
                                       size_t size, void *location, uint32_t location_checksum,
                                       void *mem_map,
                                       void *oat_dex_file, void *error_msg);

static void *
(new_nougat_open_memory)(void *DexFile_thiz, uint8_t *base, size_t size, void *location,
                         uint32_t location_checksum, void *mem_map,
                         void *oat_dex_file, void *error_msg) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "new_nougat_open_memory");

    if (size < DEX_MIN_LEN) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "size < DEX_MIN_LEN");
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "保存64位nougat apk dex文件：%p", base);
        save_dex_file(base, size);
    }
    return (*old_nougat_open_memory)(DexFile_thiz, base, size, location, location_checksum, mem_map,
                                     oat_dex_file, error_msg);
}
/////////////////////

/////////////////////
static void *(*old_opencommon_oreo)(void *DexFile_thiz, uint8_t *base, size_t size, void *location,
                                    uint32_t location_checksum, void *oat_dex_file, bool verify,
                                    bool verify_checksum,
                                    void *error_meessage, void *verify_result);


static void *new_opencommon_oreo(void *DexFile_thiz, uint8_t *base, size_t size, void *location,
                                 uint32_t location_checksum, void *oat_dex_file, bool verify,
                                 bool verify_checksum,
                                 void *error_meessage, void *verify_result) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "new_opencommon");
    if (size < DEX_MIN_LEN) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "size < DEX_MIN_LEN");
    } else {
        save_dex_file(base, size);
    }
    return (*old_opencommon_oreo)(DexFile_thiz, base, size, location, location_checksum,
                                  oat_dex_file, verify, verify_checksum, error_meessage,
                                  verify_result);
}

static void *
(*old_opencommon_pie)(void *DexFile_thiz, uint8_t *, size_t, uint8_t *, size_t, void *, uint32_t, void *, bool, bool,
                      void *, void *,
                      void *);


static void *new_opencommon_pie(void *DexFile_thiz, uint8_t *base, size_t size, uint8_t *data_base, size_t data_size,
                                void *location,
                                uint32_t location_checksum, void *oat_dex_file,
                                bool verify,
                                bool verify_checksum,
                                void *error_meessage, void *container, void *verify_result) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "new_opencommon_pie");
    if (size < DEX_MIN_LEN) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "size < DEX_MIN_LEN");
    } else {
        save_dex_file(base, size);
    }
    return (*old_opencommon_pie)(DexFile_thiz, base, size, data_base, data_size, location, location_checksum,
                                 oat_dex_file, verify, verify_checksum,
                                 error_meessage, container, verify_result);
}
/////////////////////

void **get_old_open_function_addr() {
    if (isArm64()) {
        if (isAndroid9()) {
            return reinterpret_cast<void **>(&old_arm64_open_common_pie);
        } else if (isAndroid8()) {
            return reinterpret_cast<void **>(&old_arm64_open_common_oreo);
        } else {
            return reinterpret_cast<void **>(&old_arm64_open_memory);

        }
    } else {
        if (isAndroid9()) {
            return reinterpret_cast<void **>(&old_opencommon_pie);
        } else if (isAndroid8()) {
            return reinterpret_cast<void **>(&old_opencommon_oreo);
        } else {
            return reinterpret_cast<void **>(&old_nougat_open_memory);
        }
    }
}

void *get_new_open_function_addr() {
    if (isArm64()) {
        if (isAndroid9()) {
            return reinterpret_cast<void *>(new_arm64_open_common_pie);
        } else if (isAndroid8()) {
            return reinterpret_cast<void *>(new_arm64_open_common_oreo);
        } else {
            return reinterpret_cast<void *>(new_arm64_open_memory);

        }
    } else {
        if (isAndroid9()) {
            return reinterpret_cast<void *>(new_opencommon_pie);
        } else if (isAndroid8()) {
            return reinterpret_cast<void *>(new_opencommon_oreo);
        } else {
            return reinterpret_cast<void *>(new_nougat_open_memory);
        }
    }
}