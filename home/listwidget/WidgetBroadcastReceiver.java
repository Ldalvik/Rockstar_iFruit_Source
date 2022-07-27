package com.rockstargames.home.listwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.amazon.device.home.HeroWidgetIntent;
import com.facebook.internal.NativeProtocol;
import com.rockstargames.hal.ActivityWrapper;

/* loaded from: classes.dex */
public class WidgetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WidgetBroadcastReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra(HeroWidgetIntent.EXTRA_HERO_WIDGET_DATA);
        Log.d(TAG, "Click received! List Item: " + stringExtra);
        if (stringExtra == null) {
            return;
        }
        String[] split = stringExtra.split("\\|");
        if (split.length < 2) {
            return;
        }
        String str = split[0];
        if (str.equalsIgnoreCase(NativeProtocol.IMAGE_URL_KEY)) {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setData(Uri.parse(split[1]));
            intent2.addFlags(268435456);
            context.startActivity(intent2);
        } else if (!str.equalsIgnoreCase("app") || split.length < 3) {
        } else {
            String str2 = split[2];
            String str3 = split[1];
            Intent launchIntentForPackage = ActivityWrapper.getActivity().getPackageManager().getLaunchIntentForPackage(str2);
            if (launchIntentForPackage == null) {
                launchIntentForPackage = new Intent("android.intent.action.VIEW");
                launchIntentForPackage.setData(Uri.parse("amzn://apps/android?asin=" + str3));
                launchIntentForPackage.addFlags(268435456);
            }
            context.startActivity(launchIntentForPackage);
        }
    }
}
