package com.kingbird.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: SharedPreferencesUtils
 * @Description: java类作用描述
 * @Author: Pan
 * @CreateDate: 2019/11/26 11:19
 */
public class SharedPreferencesUtils {

    private static final String SP_NAME = "config";
    private static SharedPreferences sp;

    /**
     * 保存字符串
     */
    public static void writeString(Context context, String key, String value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }
        sp.edit().putString(key, value).apply();
    }

    /**
     * 返回字符串
     */
    public static String readString(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }
        return sp.getString(key, "");
    }

    /**
     * 保存布尔
     */
    public static void writeBoolean(Context context, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 返回布尔
     */
    public static boolean readBoolean(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }

        return sp.getBoolean(key, false);
    }

    /**
     * 保存int
     */
    public static void writeInt(Context context, String key, int value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }

        sp.edit().putInt(key, value).apply();
    }

    /**
     * 返回int
     */
    public static int readInt(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }

        return sp.getInt(key, 0);
    }

    /**
     * 保存float
     */
    public static void writeFloat(Context context, String key, float value) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }

        sp.edit().putFloat(key, value).apply();
    }

    /**
     * 返回float
     */
    public static float getFloat(Context context, String key, float defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }

        return sp.getFloat(key, defValue);
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, 0);
        }
        return sp.contains(key);
    }

}

