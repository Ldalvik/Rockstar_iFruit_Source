package com.rockstargames.hal;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.p000v4.view.ViewCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import com.amazon.device.home.HomeManager;
import com.rockstargames.home.listwidget.HomeWidgetCreator;
import java.util.Locale;

/* loaded from: classes.dex */
public class ActivityWrapper {
    private static Context applicationContext;
    private static TextureAtlasCache atlasCache;
    private static ActivityWrapper instance;
    private static ContainerLayout layout;
    private static String linkedAccountResult;
    private static Activity mainActivity;
    private static int windowHeight;
    private static int windowWidth;
    private static boolean transitioning = false;
    private static float scale = 1.0f;

    private native void main();

    private native void onPauseApp();

    private native void onResumeApp(String str);

    private native void onStartApp();

    public static void onStopCallback() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public native void runUpdateCallback();

    private native void setLanguage(String str, String str2);

    private native void setUserAgent(String str);

    private native void setVersionNumber(String str);

    public native String getLocalisedString(String str);

    public native void onBackPressed();

    public native void setCurrentScreenSize(int i, int i2);

    static {
        System.loadLibrary("ifruit");
    }

    public static Activity getActivity() {
        return mainActivity;
    }

    public static ViewGroup getLayout() {
        return layout;
    }

    public static ActivityWrapper getInstance() {
        if (instance == null) {
            instance = new ActivityWrapper();
        }
        return instance;
    }

    public static TextureAtlasCache getTextureAtlasCache() {
        return atlasCache;
    }

    public static void setApplicationContext(Context context) {
        applicationContext = context;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setActivity(Activity activity) {
        mainActivity = activity;
        applicationContext = activity.getApplicationContext();
        ContainerLayout containerLayout = new ContainerLayout(activity);
        layout = containerLayout;
        containerLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        activity.setContentView(layout);
        andSecureData.Init(activity);
        Log.e("HAL", "maxMemory():          " + Runtime.getRuntime().maxMemory());
        Log.e("HAL", "getMemoryClass():     " + ((ActivityManager) activity.getSystemService("activity")).getMemoryClass());
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        Log.e("HAL", "MemoryInfo.availMem:  " + memoryInfo.availMem);
        Log.e("HAL", "MemoryInfo.threshold: " + memoryInfo.threshold);
        if (atlasCache == null) {
            int maxMemory = (int) ((Runtime.getRuntime().maxMemory() * 50) / 100);
            atlasCache = new TextureAtlasCache(maxMemory);
            try {
                byte[] bArr = new byte[maxMemory];
            } catch (OutOfMemoryError unused) {
                Log.i("ActivityWrapper", "Running low on memory!");
            }
        }
    }

    public static void handleException(final Exception exc) {
        try {
            getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.ActivityWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Log.e("ActivityWrapper", "Exception: " + exc.getClass().getSimpleName(), exc);
                    } catch (Exception e) {
                        Log.e("ActivityWrapper", "Unable to report error as toast!", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ActivityWrapper", "Unable to report error as toast!", e);
        }
    }

    public static void showToast(final String str, final boolean z) {
        try {
            getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.ActivityWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Toast.makeText(ActivityWrapper.getActivity(), str, z ? 1 : 0).show();
                    } catch (Exception e) {
                        Log.e("ActivityWrapper", "Unable to show toast!", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ActivityWrapper", "Unable to show toast!", e);
        }
    }

    public static void logError(String str, String str2, Exception exc) {
        if (exc != null) {
            Log.e(str, str2 + "\nException ", exc);
            return;
        }
        Log.e(str, str2);
    }

    public static boolean getTransitioning() {
        return transitioning;
    }

    public static void setTransitioning(boolean z) {
        transitioning = z;
    }

    public static void runMain(int i, int i2) {
        if (i > i2) {
            i2 = i;
            i = i2;
        }
        windowWidth = i;
        windowHeight = i2;
        Log.e("HAL", "Using window size of " + windowWidth + "x" + windowHeight);
        ActivityWrapper activityWrapper = getInstance();
        setupLocale();
        activityWrapper.setUserAgent(andWebView.getUserAgent());
        activityWrapper.setVersionNumber(getVersion());
        activityWrapper.setCurrentScreenSize(i, i2);
        activityWrapper.main();
    }

    public static void setupLocale() {
        getInstance().setLanguage(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
    }

    public static String getVersion() {
        try {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            return "nope";
        }
    }

    public static long getTotalMemoryBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemoryBytes() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemoryBytes() {
        return Runtime.getRuntime().freeMemory();
    }

    public static String getManagedStaticCounts() {
        return TextureAtlas.getBitmapStats() + " " + andViewManager.getStaticCounts();
    }

    public static void onStartCallback() {
        getInstance().onStartApp();
    }

    public static void onRestartCallback() {
        getInstance().onStartApp();
    }

    public static void onResumeCallback() {
        andAudio.MuteAllAudio(false);
        andVideo.Resume();
        getInstance().onResumeApp(linkedAccountResult);
        andViewManager.invalidateHierarchy();
        andThread.setRunning(true);
    }

    public static void onPauseCallback() {
        andAudio.MuteAllAudio(true);
        andVideo.Suspend();
        getInstance().onPauseApp();
        andThread.setRunning(false);
    }

    public static void onDestroyCallback() {
        atlasCache = null;
    }

    public static void addUpdateCallback() {
        getActivity().getWindow().getDecorView().postDelayed(new Runnable() { // from class: com.rockstargames.hal.ActivityWrapper.3
            @Override // java.lang.Runnable
            public void run() {
                new ActivityWrapper().runUpdateCallback();
            }
        }, 30L);
    }

    public static void SetLinkedAccountResult(String str) {
        linkedAccountResult = str;
    }

    public static void updateHeroWidgetWithData(final String str) {
        try {
            if (!getActivity().getPackageManager().hasSystemFeature(HomeManager.FEATURE_HOME)) {
                return;
            }
            final HomeWidgetCreator homeWidgetCreator = new HomeWidgetCreator(getActivity());
            new Thread() { // from class: com.rockstargames.hal.ActivityWrapper.4
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        HomeWidgetCreator.this.updateHomeWidgetWithData(str);
                    } catch (Exception e) {
                        Log.e("AppWidget", "Failed to parse widget configuration", e);
                    }
                }
            }.start();
        } catch (Exception unused) {
        }
    }

    public static float getScale() {
        return scale;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }
}
