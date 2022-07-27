package com.rockstargames.hal;

import android.content.Intent;
import android.net.Uri;

/* loaded from: classes.dex */
public class andLinkAccounts {
    public static void LinkAccount(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(str));
        ActivityWrapper.getActivity().startActivity(intent);
    }
}
