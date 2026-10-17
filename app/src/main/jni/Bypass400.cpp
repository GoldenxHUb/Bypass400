#include <jni.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <sys/uio.h>
#include <cstdio>
#include <cstring>
#include <string>
#include <android/log.h>

#define LOG_TAG "Bypass400"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static ssize_t process_v_write(pid_t pid, const void* local_buf, size_t size, uintptr_t remote_addr) {
    struct iovec local[1];
    struct iovec remote[1];
    local[0].iov_base = (void*)local_buf;
    local[0].iov_len = size;
    remote[0].iov_base = (void*)remote_addr;
    remote[0].iov_len = size;
    return syscall(271, pid, local, 1, remote, 1, 0);
}

static uintptr_t get_module_base(int pid, const char *module_name) {
    FILE *fp;
    uintptr_t addr = 0;
    char *pch;
    char filename[32];
    char line[1024];
    snprintf(filename, sizeof(filename), "/proc/%d/maps", pid);
    fp = fopen(filename, "r");
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, module_name)) {
                pch = strtok(line, "-");
                addr = (uintptr_t)strtoul(pch, NULL, 16);
                break;
            }
        }
        fclose(fp);
    }
    return addr;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_goldenx_bypass400_MainActivity_applyBypass(JNIEnv *env, jobject /* this */, jint patchId, jboolean enable) {
    pid_t target_pid = -1;

    FILE *fp = popen("su -c pidof com.dts.freefireth", "r");
    if (fp) {
        char pid_str[16];
        if (fgets(pid_str, sizeof(pid_str), fp)) {
            target_pid = atoi(pid_str);
        }
        pclose(fp);
    }

    if (target_pid <= 0) {
        return env->NewStringUTF("Failed: Free Fire not running!");
    }

    uintptr_t base = get_module_base(target_pid, "libil2cpp.so");
    if (base == 0) {
        return env->NewStringUTF("Failed: libil2cpp.so not found!");
    }

    uintptr_t target_addr = base + 0xAA0D678;
    uint32_t state_code = enable ? 0x0001007B : 0x0002007C;
    uint32_t state_flag = enable ? 0x00000001 : 0x0000000E;

    if (process_v_write(target_pid, &state_code, 4, target_addr + 0x10) == 4 &&
        process_v_write(target_pid, &state_flag, 4, target_addr + 0x14) == 4) {
        return env->NewStringUTF(enable ? "Success: Bypass Activated" : "Success: Bypass Deactivated");
    }

    return env->NewStringUTF("Failed: Memory write error");
}
