package com.rockstargames.ifruit;

import android.support.p003v7.app.AppCompatDelegate;
import com.google.android.vending.expansion.downloader.impl.DownloaderService;
import org.apache.http.HttpStatus;

/* loaded from: classes.dex */
public class ObbDownloaderService extends DownloaderService {
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhqnXbur+efFjeyiiDyrOEyaQaR59fcXmgziMm/0mAS/VmbGRWBVaT2OT9mXFdg+FBiFrey4Eb6jrI2RsRSVyk7r420Lp+qRTsYNTETfzZzM3xdbr9wVg3ovJEM/F41amvrXEJO0upu9I/ae/kUasDFzOnVujO9uMBLWiKri7thH/fEBmu0iRdVFqHmsrVdDo8QBfXwOKbitoAHmQtTlrJePvIdBKOGsjQmyeyNVKzLgup8IOjslvVdlJMdrbHzdKdxF9ljOC/LOp2UeC0XwV42APjZjj3PFdcCCSnOXA+/4EjoryF0JCC0//2p5MEz5fjvA3FozBLWZmkMn+YwK3dQIDAQAB";
    public static final byte[] SALT = new byte[600];

    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    static {
        int[] iArr = {200, 69, 180, 111, 63, 126, 121, 169, 227, 65, 21, 123, 76, 237, 62, 136, 116, 139, 76, 143, 61, 69, 11, 8, 19, 83, 72, 52, 9, 169, 9, 231, 31, 164, 148, 124, 18, 164, HttpStatus.SC_PARTIAL_CONTENT, 32, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY, 149, 31, 158, 127, 20, 211, 178, 8, 125, 189, 26, 120, 99, 141, 29, 223, 123, 122, 26, 231, 51, 198, 113, 56, 221, 211, 17, 95, 218, 183, 24, 176, 49, 92, 43, 139, 198, 253, 111, 14, DownloaderService.STATUS_WAITING_FOR_NETWORK, 1, 153, 26, 173, 92, 237, 187, 146, 120, 121, 189, 38, 214, 99, 38, 99, DownloaderService.STATUS_PAUSED_BY_APP, 236, 67, 224, 216, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY, 112, 125, 66, 44, 133, 40, 214, 155, 149, 191, 89, 37, 39, DownloaderService.STATUS_PAUSED_BY_APP, 226, 38, 8, 17, 134, 79, 246, 21, 126, 39, 250, 81, 234, 149, 79, DownloaderService.STATUS_QUEUED_FOR_WIFI_OR_CELLULAR_PERMISSION, 53, 176, 59, 35, 114, 180, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY, 210, 200, 7, 212, HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, 114, 214, HttpStatus.SC_PARTIAL_CONTENT, 173, 236, 191, 164, DownloaderService.STATUS_WAITING_FOR_NETWORK, DownloaderService.STATUS_QUEUED_FOR_WIFI_OR_CELLULAR_PERMISSION, 188, 180, 82, 72, 183, 89, 221, DownloaderService.STATUS_WAITING_TO_RETRY, 152, 22, 163, 147, 179, 121, 88, 145, 75, 233, 187, 93, 78, 200, 9, 76, 123, 184, 70, 174, 215, 106, 121, 16, 3, 153, 174, 222, 64, 100, DownloaderService.STATUS_PAUSED_BY_APP, 66, 124, 250, 159, 241, 199, 96, 111, 181, 6, 74, 98, 227, 16, 99, 129, 245, 33, 16, DownloaderService.STATUS_QUEUED_FOR_WIFI_OR_CELLULAR_PERMISSION, 73, 60, 189, 188, 214, 163, 99, 136, 0, 251, DownloaderService.STATUS_WAITING_TO_RETRY, 65, 129, 73, 249, 136, 43, 123, 107, 135, 26, 237, 48, 170, 121, 38, 38, 39, 208, 63, 43, 234, 86, 68, 93, 135, 160, 10, 25, 41, 211, 65, 174, 123, 52, 229, 228, 88, 78, 226, 87, 121, 181, 168, DownloaderService.STATUS_WAITING_TO_RETRY, 86, 93, 141, 183, 147, 237, 138, 47, 157, HttpStatus.SC_MULTI_STATUS, 82, 213, 178, 115, HttpStatus.SC_PARTIAL_CONTENT, 107, 63, 210, 46, 22, HttpStatus.SC_RESET_CONTENT, 84, 232, 14, 1, HttpStatus.SC_PROCESSING, 61, 77, 24, 104, 134, 48, 54, 89, DownloaderService.STATUS_PENDING, 44, 99, HttpStatus.SC_CREATED, 188, 120, 198, 89, 253, 255, 103, 133, HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, 29, 241, 120, 122, 136, 129, 94, 189, 43, 32, 40, DownloaderService.STATUS_PAUSED_BY_APP, 250, 156, 177, 131, 208, 46, 209, 130, 5, 171, 141, 229, 47, 140, 200, 1, 29, 105, 37, 23, 36, 14, 50, 139, 97, 18, 212, 69, 103, 55, 164, 90, 14, 136, 19, 160, 158, 34, 56, 178, 216, 13, 67, 89, 22, 48, DownloaderService.STATUS_WAITING_FOR_NETWORK, 10, 234, 64, 65, 0, 154, 4, 13, 191, 11, 130, 162, HttpStatus.SC_RESET_CONTENT, 236, DownloaderService.STATUS_WAITING_FOR_NETWORK, 159, 78, 233, 88, 242, 157, 64, HttpStatus.SC_SWITCHING_PROTOCOLS, 53, 240, 123, 20, 46, 148, 232, 141, 144, 84, 74, 133, HttpStatus.SC_ACCEPTED, 119, 81, 42, 148, 231, 175, 25, 57, 234, HttpStatus.SC_MULTI_STATUS, 20, 14, 176, 8, 89, 117, 216, 42, 66, 81, 29, 97, 174, 164, 22, 252, 163, DownloaderService.STATUS_PENDING, 11, 249, 188, HttpStatus.SC_SWITCHING_PROTOCOLS, 9, 239, 166, 191, 187, 188, 83, 81, 83, 87, 183, 149, 96, 0, 231, 51, 48, 103, 180, 236, 87, 191, 25, 160, 85, 22, 186, 34, 181, 62, 226, 17, 240, 191, 159, 100, 6, 11, 32, 113, 123, 198, 42, 245, 176, 255, 126, 237, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR, 250, DownloaderService.STATUS_WAITING_FOR_NETWORK, 138, 119, 161, 72, 35, BuildConfig.VERSION_CODE, 18, 233, HttpStatus.SC_ACCEPTED, 115, 142, 59, 187, 3, 140, 73, 72, 191, 55, 12, 54, HttpStatus.SC_SWITCHING_PROTOCOLS, 165, 115, 253, 44, 122, 74, 131, 106, 113, 170, DownloaderService.STATUS_QUEUED_FOR_WIFI_OR_CELLULAR_PERMISSION, 73, 235, HttpStatus.SC_SWITCHING_PROTOCOLS, 28, 169, 214, 114, 25, 85, 158, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY, 182, 94, 132, 238, 243, 23, 99, 26, 135, 76, 76, 209, 199, 208, 30, 1, 19, 248, 85, 211, HttpStatus.SC_SWITCHING_PROTOCOLS, 107, 138, 234, DownloaderService.STATUS_RUNNING, 145, 8, 215, 160, 100, 116, 142, 16, 165, 70, 157, 236, 147, 245, 61, 86, 200, 98, 138, 249, 112, 171, 25, 1, 105, 31, 236, 248, 47, 50, 45, 145, 126, AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY, 237, DownloaderService.STATUS_PAUSED_BY_APP, 19};
        for (int i = 0; i < 600; i++) {
            SALT[i] = (byte) (iArr[i] - 128);
        }
    }

    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public byte[] getSALT() {
        return SALT;
    }

    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public String getAlarmReceiverClassName() {
        return ObbAlarmReceiver.class.getName();
    }
}
