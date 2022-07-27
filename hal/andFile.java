package com.rockstargames.hal;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Environment;
import android.util.Log;
import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.google.android.vending.expansion.downloader.Constants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class andFile {
    private static boolean checkedForObbVersion = false;
    private static ArrayList<String> downlodableContent = new ArrayList<>();
    private static int obbMainVersion = 145;
    private static int obbPatchVersion = 145;
    private static ZipResourceFile zipFile;

    private static String getExpansionAPKDirectory(Context context) {
        return Environment.getExternalStorageDirectory().toString() + Constants.EXP_PATH + context.getPackageName();
    }

    private static int getExpansionFileVersion(File file) {
        int i;
        int indexOf;
        String lowerCase = file.getName().toLowerCase();
        int indexOf2 = lowerCase.indexOf(46);
        if (indexOf2 == -1 || (indexOf = lowerCase.indexOf(46, (i = indexOf2 + 1))) == -1) {
            return 0;
        }
        return Integer.parseInt(lowerCase.substring(i, indexOf));
    }

    private static File[] getApplicationExpansionFiles(final Context context) {
        File file = new File(getExpansionAPKDirectory(ActivityWrapper.getApplicationContext()));
        return file.exists() ? file.listFiles(new FileFilter() { // from class: com.rockstargames.hal.andFile.1
            @Override // java.io.FileFilter
            public boolean accept(File file2) {
                String lowerCase = file2.getName().toLowerCase();
                String quote = Pattern.quote(context.getPackageName());
                return lowerCase.matches("(main|patch)\\.\\d+\\." + quote + ".obb");
            }
        }) : new File[0];
    }

    public static boolean checkObbFiles() {
        File[] applicationExpansionFiles;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (File file : getApplicationExpansionFiles(ActivityWrapper.getApplicationContext())) {
            int expansionFileVersion = getExpansionFileVersion(file);
            long length = file.length();
            Log.i("StartupActivity", "OBB " + expansionFileVersion + ": " + length + " bytes");
            if (!z2 && file.getName().startsWith("main")) {
                if (expansionFileVersion == 145 && length == 131418876) {
                    obbMainVersion = expansionFileVersion;
                    z2 = true;
                } else {
                    file.delete();
                }
            }
            if (!z3 && file.getName().startsWith("patch")) {
                if (expansionFileVersion == 145 && length == 170282) {
                    obbPatchVersion = expansionFileVersion;
                    z3 = true;
                } else {
                    file.delete();
                }
            }
        }
        if (!z2 || !z3) {
            if (!z2 && !z3) {
                Log.e("andFile", "No OBB file found!");
            }
            if (!z2) {
                Log.e("andFile", "Main OBB file NOT found!");
            }
            if (!z3) {
                Log.e("andFile", "Patch OBB file NOT found!");
            }
        } else {
            Log.i("andFile", "Using Main OBB version: " + obbMainVersion);
            Log.i("andFile", "Using Patch OBB version: " + obbPatchVersion);
            z = true;
        }
        checkedForObbVersion = true;
        return z;
    }

    private static ZipResourceFile getZipFile() throws IOException {
        if (!checkedForObbVersion) {
            checkObbFiles();
        }
        ZipResourceFile zipResourceFile = zipFile;
        if (zipResourceFile == null) {
            ZipResourceFile aPKExpansionZipFile = APKExpansionSupport.getAPKExpansionZipFile(ActivityWrapper.getActivity().getApplicationContext(), obbMainVersion, obbPatchVersion);
            zipFile = aPKExpansionZipFile;
            addAllDownloadableContent();
            return aPKExpansionZipFile;
        }
        return zipResourceFile;
    }

    private static void addAllDownloadableContent() {
        Iterator<String> it = downlodableContent.iterator();
        while (it.hasNext()) {
            addZipFile(it.next());
        }
    }

    public static String getExternalFilesDir() {
        return ActivityWrapper.getActivity().getExternalFilesDir(null).getAbsolutePath();
    }

    public static boolean addZipFile(String str) {
        ZipResourceFile zipResourceFile = zipFile;
        try {
            if (zipResourceFile == null) {
                getZipFile();
            } else {
                zipResourceFile.addPatchFile(str);
            }
            if (downlodableContent.contains(str)) {
                return true;
            }
            downlodableContent.add(str);
            return true;
        } catch (IOException e) {
            Log.e("andFile", "Failed to load zip file from path: " + str);
            Log.e("andFile", "Exception: " + e.toString());
            return false;
        } catch (Exception e2) {
            Log.e("andFile", "Exception: " + e2.toString());
            return false;
        }
    }

    public static InputStream getAssetInputStreamForDLC(String str) throws IOException {
        ZipResourceFile zipFile2 = getZipFile();
        InputStream inputStream = zipFile2.getInputStream("assets/" + str);
        if (inputStream == null) {
            Log.e("andFile", "Failed to load file at " + str + ", retrying after inspecting downloadable content...");
            addAllDownloadableContent();
            ZipResourceFile zipFile3 = getZipFile();
            return zipFile3.getInputStream("assets/" + str);
        }
        return inputStream;
    }

    public static AssetFileDescriptor getAssetFileDescriptorForDLC(String str) throws IOException {
        ZipResourceFile zipFile2 = getZipFile();
        return zipFile2.getAssetFileDescriptor("assets/" + str);
    }

    public static InputStream getAssetInputStream(String str, boolean z) throws IOException {
        if (!z) {
            ZipResourceFile zipFile2 = getZipFile();
            return zipFile2.getInputStream("assets/" + str);
        }
        return ActivityWrapper.getActivity().getAssets().open(str);
    }

    public static AssetFileDescriptor getAssetFileDescriptor(String str, boolean z) throws IOException {
        if (!z) {
            ZipResourceFile zipFile2 = getZipFile();
            return zipFile2.getAssetFileDescriptor("assets/" + str);
        }
        return ActivityWrapper.getActivity().getAssets().openFd(str);
    }

    public static byte[] getFile(String str, String str2, String str3) {
        boolean z;
        String str4 = "xml";
        try {
            String str5 = "";
            if (str3.equalsIgnoreCase(str4)) {
                z = true;
            } else {
                if (str3.equalsIgnoreCase("json")) {
                    str4 = "json";
                } else {
                    str4 = str3.equalsIgnoreCase("png") ? "images" : str5;
                }
                z = false;
            }
            StringBuilder sb = new StringBuilder();
            if (str4.length() != 0) {
                str5 = str4 + "/";
            }
            sb.append(str5);
            sb.append(str2);
            sb.append(".");
            sb.append(str3);
            InputStream assetInputStream = getAssetInputStream(sb.toString(), z);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[131072];
            while (true) {
                int read = assetInputStream.read(bArr);
                if (read != -1) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    assetInputStream.close();
                    return byteArray;
                }
            }
        } catch (IOException e) {
            Log.i("andFile", "Problem loading " + str2 + "." + str3, e);
            return null;
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:12:0x0027 -> B:21:0x002a). Please submit an issue!!! */
    public static void writeUserFile(String str, String str2) {
        FileOutputStream fileOutputStream = null;
        try {
            try {
                try {
                    fileOutputStream = ActivityWrapper.getActivity().openFileOutput(str, 0);
                    fileOutputStream.write(str2.getBytes());
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    ActivityWrapper.handleException(e);
                    if (fileOutputStream == null) {
                        return;
                    }
                    fileOutputStream.close();
                }
            } catch (IOException e2) {
                ActivityWrapper.handleException(e2);
            }
        } catch (Throwable th) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e3) {
                    ActivityWrapper.handleException(e3);
                }
            }
            throw th;
        }
    }

    public static String readUserFile(String str) {
        FileInputStream fileInputStream;
        Throwable th;
        try {
            fileInputStream = ActivityWrapper.getActivity().openFileInput(str);
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32768);
                byte[] bArr = new byte[32768];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
                String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused) {
                    }
                }
                return byteArrayOutputStream2;
            } catch (Exception unused2) {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused3) {
                    }
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused4) {
                    }
                }
                throw th;
            }
        } catch (Exception unused5) {
            fileInputStream = null;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
    }
}
