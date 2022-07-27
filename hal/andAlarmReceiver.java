package com.rockstargames.hal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.p000v4.app.NotificationCompat;
import com.rockstargames.ifruit.C0532R;
import com.rockstargames.ifruit.StartupActivity;

/* loaded from: classes.dex */
public class andAlarmReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("notificationTitle");
        String stringExtra2 = intent.getStringExtra("notificationMsg");
        int intExtra = intent.getIntExtra("notificationID", 0);
        intent.putExtra("title", stringExtra);
        intent.putExtra("text", stringExtra2);
        PendingIntent.getActivity(context, 0, intent, 134217728);
        ((NotificationManager) context.getSystemService("notification")).notify(intExtra, new NotificationCompat.Builder(context).setSmallIcon(C0532R.C0533drawable.boot_ifruit).setTicker(stringExtra2).setContentTitle(stringExtra).setContentText(stringExtra2).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, StartupActivity.class), 0)).setAutoCancel(true).build());
    }
}
