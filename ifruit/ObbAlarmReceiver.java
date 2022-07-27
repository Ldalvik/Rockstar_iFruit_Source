package com.rockstargames.ifruit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.rockstargames.hal.ActivityWrapper;

/* loaded from: classes.dex */
public class ObbAlarmReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ObbDownloaderService.class);
        } catch (PackageManager.NameNotFoundException e) {
            ActivityWrapper.handleException(e);
        }
    }
}
