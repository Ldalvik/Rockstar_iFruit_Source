package com.rockstargames.hal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.p000v4.app.NotificationCompat;
import java.util.Calendar;

/* loaded from: classes.dex */
public class andNotification {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static int notificationIDCount;

    public static void AddNotification(String str, String str2, String str3, int i) {
        Intent intent = new Intent("com.rockstargames.hal.Broadcast");
        intent.setClass(ActivityWrapper.getActivity(), andAlarmReceiver.class);
        intent.putExtra("notificationTitle", str2);
        intent.putExtra("notificationMsg", str3);
        intent.putExtra("notificationID", notificationIDCount);
        PendingIntent broadcast = PendingIntent.getBroadcast(ActivityWrapper.getActivity(), notificationIDCount, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, i);
        ((AlarmManager) ActivityWrapper.getActivity().getSystemService(NotificationCompat.CATEGORY_ALARM)).set(0, calendar.getTimeInMillis(), broadcast);
        notificationIDCount++;
    }

    public static void CancelAllNotifications() {
        AlarmManager alarmManager = (AlarmManager) ActivityWrapper.getActivity().getSystemService(NotificationCompat.CATEGORY_ALARM);
        for (int i = 0; i < 16; i++) {
            Intent intent = new Intent("com.rockstargames.hal.Broadcast");
            intent.setClass(ActivityWrapper.getActivity(), andAlarmReceiver.class);
            PendingIntent broadcast = PendingIntent.getBroadcast(ActivityWrapper.getActivity(), i, intent, 536870912);
            if (broadcast != null) {
                alarmManager.cancel(broadcast);
            }
        }
        notificationIDCount = 0;
    }
}
