package com.rockstargames.hal;

import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import com.google.android.vending.expansion.downloader.Constants;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class andThread {
    private static boolean threadsRunning = true;
    private static boolean threadsStopping = false;
    private static Object lock = new Object();
    private static ArrayList<DelayedNativeRunnable> pausedCache = new ArrayList<>();
    private static int tid = 0;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void runNativeRunnable(int i);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DelayedNativeRunnable implements Runnable {
        private int delay;
        private int runnableId;

        public DelayedNativeRunnable(int i, int i2) {
            this.runnableId = i;
            this.delay = i2;
        }

        @Override // java.lang.Runnable
        public void run() {
            andThread.runNativeRunnable(this.runnableId);
        }

        public boolean post() {
            ViewGroup layout = ActivityWrapper.getLayout();
            if (layout != null) {
                layout.postDelayed(this, this.delay);
                return true;
            }
            return false;
        }
    }

    public static void setRunning(boolean z) {
        if (z) {
            threadsRunning = z;
            threadsStopping = false;
            synchronized (pausedCache) {
                Iterator<DelayedNativeRunnable> it = pausedCache.iterator();
                while (it.hasNext()) {
                    it.next().post();
                }
                pausedCache.clear();
            }
            return;
        }
        threadsStopping = true;
        ActivityWrapper.getLayout().postDelayed(new Runnable() { // from class: com.rockstargames.hal.andThread.1
            @Override // java.lang.Runnable
            public void run() {
                if (andThread.threadsStopping) {
                    boolean unused = andThread.threadsRunning = false;
                }
            }
        }, Constants.ACTIVE_THREAD_WATCHDOG);
    }

    public static void runOnMainThread(int i, int i2) {
        if (i2 <= 0) {
            i2 = 1;
        }
        DelayedNativeRunnable delayedNativeRunnable = new DelayedNativeRunnable(i, i2);
        if (threadsRunning) {
            delayedNativeRunnable.post();
            return;
        }
        synchronized (pausedCache) {
            pausedCache.add(delayedNativeRunnable);
        }
    }

    public static void runOnBackgroundThread(final int i, final int i2) {
        new Thread(new Runnable() { // from class: com.rockstargames.hal.andThread.2
            @Override // java.lang.Runnable
            public void run() {
                int i3 = i2;
                if (i3 > 0) {
                    try {
                        Thread.sleep(i3);
                    } catch (InterruptedException e) {
                        Log.w("HAL", "Interrupted sleep:", e);
                        return;
                    }
                }
                andThread.runNativeRunnable(i);
            }
        }, "Native background thread").start();
    }

    public static void sleep(long j) {
        try {
            Thread.sleep(j);
        } catch (Exception unused) {
        }
    }

    public static int currentThreadId() {
        int i = tid + 1;
        tid = i;
        if (i == 0) {
            ActivityWrapper.handleException(new Exception("Thread IDs overflowed!"));
            tid++;
        }
        if (ActivityWrapper.getActivity().getMainLooper() == Looper.myLooper()) {
            return 0;
        }
        return tid;
    }
}
