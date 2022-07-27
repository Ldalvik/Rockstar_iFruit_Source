package com.rockstargames.hal;

import android.widget.ProgressBar;

/* loaded from: classes.dex */
public class andSpinner extends andView {
    static int staticCount;

    public andSpinner(int i) {
        super(i);
        setView(new andSpinnerImpl());
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andSpinner createView(int i) {
        return new andSpinner(i);
    }

    /* loaded from: classes.dex */
    public class andSpinnerImpl extends ProgressBar {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andSpinnerImpl() {
            super(ActivityWrapper.getActivity());
            andSpinner.this = r1;
            setIndeterminate(true);
        }
    }
}
