package com.rockstargames.ifruit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.andFacebook;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    private static int debugStaticCheck;
    boolean windowHasFocus = true;
    boolean activityPaused = false;

    public static boolean hasHuaweiNotchInScreen(Context context) {
        try {
            try {
                try {
                    Class<?> loadClass = context.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
                    return ((Boolean) loadClass.getMethod("hasNotchInScreen", new Class[0]).invoke(loadClass, new Object[0])).booleanValue();
                } catch (Exception unused) {
                    Log.e("StartupActivity", "hasHuaweiNotchInScreen Exception");
                    return false;
                }
            } catch (ClassNotFoundException unused2) {
                Log.e("StartupActivity", "com.huawei.android.util.HwNotchSizeUtil ClassNotFoundException");
                return false;
            } catch (NoSuchMethodException unused3) {
                Log.e("StartupActivity", "com.huawei.android.util.HwNotchSizeUtil.hasNotchInScreen NoSuchMethodException");
                return false;
            }
        } catch (Throwable unused4) {
            return false;
        }
    }

    public static int[] getHuaweiNotchSize(Context context) {
        int[] iArr = {0, 0};
        try {
            try {
                try {
                    Class<?> loadClass = context.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
                    return (int[]) loadClass.getMethod("getNotchSize", new Class[0]).invoke(loadClass, new Object[0]);
                } catch (Exception unused) {
                    Log.e("StartupActivity", "getHuaweiNotchSize Exception");
                    return iArr;
                }
            } catch (ClassNotFoundException unused2) {
                Log.e("StartupActivity", "com.huawei.android.util.HwNotchSizeUtil ClassNotFoundException");
                return iArr;
            } catch (NoSuchMethodException unused3) {
                Log.e("StartupActivity", "com.huawei.android.util.HwNotchSizeUtil.getNotchSize NoSuchMethodException");
                return iArr;
            }
        } catch (Throwable unused4) {
            return iArr;
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = true;
        try {
            requestWindowFeature(1);
            getWindow().setFlags(1024, 1024);
            ActivityWrapper.setActivity(this);
            if (ActivityWrapper.getTransitioning()) {
                Log.i("StartupActivity", "*** MainActivity onCreate() transitioning (" + debugStaticCheck + ") ***)");
                ActivityWrapper.setTransitioning(false);
            } else {
                Log.i("StartupActivity", "*** MainActivity onCreate() not transitioning (" + debugStaticCheck + ") ***)");
            }
            debugStaticCheck++;
            setRequestedOrientation(7);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int i = displayMetrics.widthPixels;
            int i2 = displayMetrics.heightPixels;
            if (Build.VERSION.SDK_INT < 28 && hasHuaweiNotchInScreen(this)) {
                i2 -= getHuaweiNotchSize(this)[1];
            }
            boolean z2 = getResources().getConfiguration().orientation == 1;
            if (i >= i2) {
                z = false;
            }
            if (z2 == z) {
                ActivityWrapper.runMain(i, i2);
            } else {
                ActivityWrapper.runMain(i2, i);
            }
            andFacebook.getInstance().onCreate();
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("Exception").setMessage(e.getLocalizedMessage()).show();
        }
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        Log.i("StartupActivity", "*** MainActivity onStart() (" + debugStaticCheck + ") ***)");
        debugStaticCheck = debugStaticCheck + 1;
        ActivityWrapper.onStartCallback();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        Log.i("StartupActivity", "*** MainActivity onRestart() (" + debugStaticCheck + ") ***)");
        debugStaticCheck = debugStaticCheck + 1;
        ActivityWrapper.onRestartCallback();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        Log.i("StartupActivity", "*** MainActivity onPause() (" + debugStaticCheck + ") ***)");
        debugStaticCheck = debugStaticCheck + 1;
        this.activityPaused = true;
        ActivityWrapper.onPauseCallback();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        Log.i("StartupActivity", "*** onWindowFocusChanged - focus (" + z + "), paused (" + this.activityPaused + ")");
        if (this.activityPaused && z) {
            this.activityPaused = false;
            ActivityWrapper.onResumeCallback();
        }
        this.windowHasFocus = z;
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.i("StartupActivity", "*** MainActivity onResume() (" + debugStaticCheck + ") ***)");
        debugStaticCheck = debugStaticCheck + 1;
        if (this.windowHasFocus) {
            this.activityPaused = false;
            ActivityWrapper.onResumeCallback();
            return;
        }
        Log.i("StartupActivity", "*** MainActivity onResume() - window no focus");
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        Log.i("StartupActivity", "*** MainActivity onStop() (" + debugStaticCheck + ") ***)");
        debugStaticCheck = debugStaticCheck + 1;
        ActivityWrapper.onStopCallback();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        Log.i("StartupActivity", "*** MainActivity onDestroy() (" + debugStaticCheck + ") ***)");
        debugStaticCheck = debugStaticCheck + 1;
        ActivityWrapper.onDestroyCallback();
        super.onDestroy();
        System.exit(0);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.i("StartupActivity", "*** MainActivity onConfigurationChanged() (" + debugStaticCheck + ") ***)");
        boolean z = true;
        debugStaticCheck = debugStaticCheck + 1;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        boolean z2 = getResources().getConfiguration().orientation == 1;
        if (i >= i2) {
            z = false;
        }
        if (z2 == z) {
            ActivityWrapper.getInstance().setCurrentScreenSize(i, i2);
        } else {
            ActivityWrapper.getInstance().setCurrentScreenSize(i2, i);
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        andFacebook.getInstance().onActivityResult(i, i2, intent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        ActivityWrapper.getInstance().onBackPressed();
    }
}
