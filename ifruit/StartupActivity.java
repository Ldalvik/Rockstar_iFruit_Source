package com.rockstargames.ifruit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.support.p000v4.app.ActivityCompat;
import android.support.p000v4.content.ContextCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.andFile;
import java.util.List;

/* loaded from: classes.dex */
public class StartupActivity extends Activity implements IDownloaderClient {
    static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 479;
    private static boolean debugOnConfigurationChangedStaticCheck = false;
    private static boolean debugOnCreateStaticCheck = false;
    private ProgressBar downloadProgressBar;
    private TextView downloadText;
    private IStub downloaderClientStub = null;
    private boolean failed = false;
    private IDownloaderService remoteService;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else if (!obbFilesExist()) {
            startDownloadUI();
        } else {
            startApp();
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (iArr.length > 0 && iArr[0] == 0) {
                if (!obbFilesExist()) {
                    startDownloadUI();
                    return;
                } else {
                    startApp();
                    return;
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Write Permissions").setMessage("The application requires to download and store external recources, but currently lacks permission access to write to the external storage.").setCancelable(false).setPositiveButton("Application settings", new DialogInterface.OnClickListener() { // from class: com.rockstargames.ifruit.StartupActivity.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(268435456);
                    intent.addFlags(1073741824);
                    intent.addFlags(8388608);
                    this.startActivity(intent);
                    StartupActivity.this.finish();
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() { // from class: com.rockstargames.ifruit.StartupActivity.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    StartupActivity.this.finish();
                }
            });
            builder.create().show();
        }
    }

    private void startDownloadUI() {
        Log.i("StartupActivity", "Checking for update.");
        Intent intent = new Intent(this, StartupActivity.class);
        intent.setFlags(335544320);
        int i = 0;
        try {
            i = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, PendingIntent.getActivity(this, 0, intent, 134217728), ObbDownloaderService.class);
        } catch (PackageManager.NameNotFoundException e) {
            ActivityWrapper.handleException(e);
        }
        Log.i("StartupActivity", "Downloader returned: " + i);
        if (i != 0) {
            this.downloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ObbDownloaderService.class);
            showDownloadUI();
        } else if (andFile.checkObbFiles()) {
        } else {
            showDownloadUI();
            onDownloadStateChanged(19);
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        IStub iStub = this.downloaderClientStub;
        if (iStub != null) {
            iStub.connect(this);
        }
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onStop() {
        IStub iStub = this.downloaderClientStub;
        if (iStub != null) {
            iStub.disconnect(this);
        }
        super.onStop();
    }

    private boolean obbFilesExist() {
        return andFile.checkObbFiles();
    }

    private void startApp() {
        Uri data = getIntent().getData();
        if (data != null) {
            ActivityWrapper.SetLinkedAccountResult(data.getQueryParameter("linkStatus"));
        }
        if (needStartApp()) {
            startActivity(new Intent(this, MainActivity.class));
            Log.i("StartupActivity", "*** StartupActivity onCreate() needs to start app (" + debugOnCreateStaticCheck + ") ***)");
        } else {
            Log.i("StartupActivity", "*** StartupActivity onCreate() doesn't need to start app (" + debugOnCreateStaticCheck + ") ***)");
        }
        debugOnCreateStaticCheck = true;
        finish();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(null);
        Log.i("StartupActivity", "*** StartupActivity onConfigurationChanged() (" + debugOnConfigurationChangedStaticCheck + ") ***)");
        debugOnConfigurationChangedStaticCheck = true;
    }

    private boolean needStartApp() {
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) getSystemService("activity")).getRunningTasks(1024);
            if (!runningTasks.isEmpty()) {
                String packageName = getPackageName();
                int size = runningTasks.size();
                for (int i = 0; i < size; i++) {
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(i);
                    if (packageName.equals(runningTaskInfo.baseActivity.getPackageName())) {
                        return runningTaskInfo.numActivities == 1;
                    }
                }
            }
        } catch (Exception unused) {
        }
        return true;
    }

    private static String getLocalisedString(String str, String str2) {
        String localisedString = ActivityWrapper.getInstance().getLocalisedString(str);
        return !localisedString.equals(str) ? localisedString : str2;
    }

    private void showDownloadUI() {
        setContentView(C0532R.layout.download_screen);
        this.downloadText = (TextView) findViewById(C0532R.C0534id.download_text);
        this.downloadProgressBar = (ProgressBar) findViewById(C0532R.C0534id.download_progress);
        this.downloadText.setText(getLocalisedString("ObbDownloadingKey", "Downloading application data..."));
        this.downloadProgressBar.setProgress(0);
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onServiceConnected(Messenger messenger) {
        IDownloaderService CreateProxy = DownloaderServiceMarshaller.CreateProxy(messenger);
        this.remoteService = CreateProxy;
        CreateProxy.onClientUpdated(this.downloaderClientStub.getMessenger());
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onDownloadStateChanged(int i) {
        Log.i("StartupActivity", "onDownloadStateChanged: " + i);
        Log.i("StartupActivity", "onDownloadStateChanged: " + Helpers.getDownloaderStringResourceIDFromState(i));
        switch (i) {
            case 5:
                if (andFile.checkObbFiles()) {
                    startApp();
                    return;
                } else {
                    onDownloadStateChanged(19);
                    return;
                }
            case 6:
            case 10:
            case 11:
            case 13:
            case 17:
            default:
                return;
            case 7:
            case 8:
            case 9:
            case 12:
            case 14:
            case 15:
            case 16:
            case 18:
            case 19:
                this.downloadText.setText(getLocalisedString("ObbDownloadFailedKey", "Downloading application data failed."));
                this.failed = true;
                this.downloadProgressBar.setVisibility(4);
                return;
        }
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onDownloadProgress(DownloadProgressInfo downloadProgressInfo) {
        Log.i("StartupActivity", "onDownloadProgress: " + downloadProgressInfo.mOverallProgress + " / " + downloadProgressInfo.mOverallTotal);
        if (!this.failed) {
            int i = (int) ((downloadProgressInfo.mOverallProgress * 100) / downloadProgressInfo.mOverallTotal);
            if (i < 0) {
                i = 0;
            }
            if (i >= 99) {
                i = 100;
            }
            this.downloadProgressBar.setProgress(i);
        }
    }
}
