package com.rockstargames.ifruit;

import android.app.Application;
import com.rockstargames.hal.ActivityWrapper;

/* loaded from: classes.dex */
public class MainApp extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        ActivityWrapper.setApplicationContext(getApplicationContext());
        ActivityWrapper.setupLocale();
    }
}
