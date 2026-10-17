LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := bypass400
LOCAL_SRC_FILES := Bypass400.cpp
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)
