package com.rockstargames.hal;

import android.content.Context;
import android.content.SharedPreferences;

/* loaded from: classes.dex */
public class andSecureData {
    private static SharedPreferences.Editor prefEditor;
    private static SharedPreferences settings;

    public static void Init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", 0);
        settings = sharedPreferences;
        prefEditor = sharedPreferences.edit();
    }

    public static String GetString(String str) {
        return settings.getString(str, "");
    }

    public static void SetString(String str, String str2) {
        prefEditor.putString(str, str2);
    }

    public static int GetInt(String str) {
        return settings.getInt(str, -1);
    }

    public static void SetInt(String str, int i) {
        prefEditor.putInt(str, i);
    }

    public static boolean GetBool(String str) {
        return settings.getBoolean(str, false);
    }

    public static void SetBool(String str, boolean z) {
        prefEditor.putBoolean(str, z);
    }

    public static float GetFloat(String str) {
        return settings.getFloat(str, -1.0f);
    }

    public static void SetFloat(String str, float f) {
        prefEditor.putFloat(str, f);
    }

    public static void Save() {
        prefEditor.commit();
    }
}
