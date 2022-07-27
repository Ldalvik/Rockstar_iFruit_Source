package com.rockstargames.hal;

import android.content.Intent;
import android.net.Uri;
import java.util.Locale;
import org.apache.http.HttpHost;

/* loaded from: classes.dex */
public class OtherAppLauncher {
    private static final InternationalVersion[] PLAY_gta3 = {new InternationalVersion("com.rockstar.gta3", null), new InternationalVersion("com.rockstar.gta3ger", "DE"), new InternationalVersion("com.rockstar.gta3jpn", "JP"), new InternationalVersion("com.rockstar.gta3aus", "AU")};
    private static final InternationalVersion[] PLAY_vc = {new InternationalVersion("com.rockstargames.gtavc", null), new InternationalVersion("com.rockstargames.gtavcger", "DE")};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Market {
        PLAY("market://details?id="),
        AMAZON("amzn://apps/android?asin=");
        
        public final String prefix;

        Market(String str) {
            this.prefix = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InternationalVersion {
        public String locale;
        public String marketIdentifier;
        public String packageIdentifer;

        public InternationalVersion(String str, String str2) {
            this.packageIdentifer = str;
            this.locale = str2;
            this.marketIdentifier = str;
        }

        public InternationalVersion(String str, String str2, String str3) {
            this.packageIdentifer = str;
            this.locale = str3;
            this.marketIdentifier = str2;
        }
    }

    private static InternationalVersion[] getVersions(Market market, String str, String str2) {
        if (market != Market.PLAY || !str.equalsIgnoreCase(PLAY_gta3[0].packageIdentifer)) {
            return (market != Market.PLAY || !str.equalsIgnoreCase(PLAY_vc[0].packageIdentifer)) ? new InternationalVersion[]{new InternationalVersion(str, str2, null)} : PLAY_vc;
        }
        return PLAY_gta3;
    }

    public static boolean openAppOrStorePage(String str, String str2, String str3) {
        Market valueOf;
        InternationalVersion[] versions = getVersions(Market.valueOf(str), str2, str3);
        Intent intent = null;
        for (InternationalVersion internationalVersion : versions) {
            intent = ActivityWrapper.getActivity().getPackageManager().getLaunchIntentForPackage(internationalVersion.packageIdentifer);
            if (intent != null) {
                break;
            }
        }
        int i = 1;
        if (intent != null) {
            ActivityWrapper.getActivity().startActivity(intent);
            return true;
        }
        String country = Locale.getDefault().getCountry();
        while (true) {
            if (i >= versions.length) {
                break;
            } else if (country.equalsIgnoreCase(versions[i].locale)) {
                str3 = versions[i].marketIdentifier;
                break;
            } else {
                i++;
            }
        }
        Intent intent2 = new Intent("android.intent.action.VIEW");
        intent2.setData(Uri.parse(valueOf.prefix + str3));
        ActivityWrapper.getActivity().startActivity(intent2);
        return false;
    }

    public static void LoadExternalWebBrowser(String str) {
        if (str.contains(HttpHost.DEFAULT_SCHEME_NAME)) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(str), "text/html");
            ActivityWrapper.getActivity().startActivity(Intent.createChooser(intent, "Launch Browser..."));
        }
    }
}
