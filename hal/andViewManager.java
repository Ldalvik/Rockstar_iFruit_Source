package com.rockstargames.hal;

import android.util.Log;
import java.util.Iterator;

/* loaded from: classes.dex */
public class andViewManager {
    public static andView root;
    private static long spam_delay;
    private static int uniqueID;

    public static void addViewToScreen(andView andview) {
        if (andview != null) {
            try {
                root = andview;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        ActivityWrapper.getLayout().addView(andview.getOuterView());
    }

    public static void removeViewFromScreen(andView andview) {
        ActivityWrapper.getLayout().removeView(andview.getOuterView());
    }

    public static void printLayout() {
        if (root != null) {
            long currentTimeMillis = System.currentTimeMillis();
            Log.i("andViewManager", "Printing handle hierarchy...");
            root.printHierarchy(0);
            spam_delay = currentTimeMillis + 1000;
        }
    }

    public static void invalidateHierarchy() {
        Log.w("andViewManager", "Invalidating hierarchy!");
        andView andview = root;
        if (andview != null) {
            andview.invalidateHierarchy();
        }
    }

    public static int genID() {
        int i = uniqueID;
        uniqueID = i + 1;
        return i;
    }

    public static void setLandscape(boolean z) {
        ActivityWrapper.setTransitioning(true);
        ActivityWrapper.getActivity().setRequestedOrientation(z ? 6 : 7);
    }

    public static String getStaticCounts() {
        int countViewsRecursive = countViewsRecursive(root);
        return "V: " + countViewsRecursive + "/" + andView.staticCount + " S: " + andScreen.staticCount + " IV: " + andImageView.staticCount + " B: " + andButton.staticCount + " L: " + andLabel.staticCount + " SV: " + andScrollView.staticCount + " T: " + andTable.staticCount + " TI: " + andTextInput.staticCount + " WV: " + andWebView.staticCount + " CP: " + andColourPicker.staticCount + " DV: " + andDrawingView.staticCount + " SP: " + andSpinner.staticCount;
    }

    private static int countViewsRecursive(andView andview) {
        if (andview == null) {
            return 0;
        }
        int i = 1;
        if (andview.children != null) {
            Iterator<andView> it = andview.children.iterator();
            while (it.hasNext()) {
                i += countViewsRecursive(it.next());
            }
        }
        return i;
    }
}
