package com.rockstargames.hal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import com.rockstargames.hal.andView;
import com.rockstargames.ifruit.C0532R;

/* loaded from: classes.dex */
public class andScreen extends andView {
    static int staticCount;
    private View loadingScreenView = null;

    /* renamed from: ph */
    private float f36ph;

    /* renamed from: pw */
    private float f37pw;

    /* renamed from: px */
    private float f38px;

    /* renamed from: py */
    private float f39py;
    private String screenName;

    public andScreen(int i) {
        super(i);
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public void createLoadingScreen(String str) {
        Log.i("andScreen", "Inflating loading screen...");
        this.screenName = str;
        LayoutInflater layoutInflater = (LayoutInflater) ActivityWrapper.getActivity().getSystemService("layout_inflater");
        if (this.screenName.equals("LSCustomsPage")) {
            this.loadingScreenView = layoutInflater.inflate(C0532R.layout.lscustomsloadingscreen, (ViewGroup) null);
        } else if (this.screenName.equals("ChopPage")) {
            this.loadingScreenView = layoutInflater.inflate(C0532R.layout.chopcustomsloadingscreen, (ViewGroup) null);
        } else {
            this.loadingScreenView = layoutInflater.inflate(C0532R.layout.loadingscreen, (ViewGroup) null);
        }
        ActivityWrapper.getLayout().addView(this.loadingScreenView, 0);
        ActivityWrapper.getLayout().setClipChildren(false);
    }

    public static andScreen createView(int i) {
        andScreen andscreen = new andScreen(i);
        andscreen.getClass();
        andscreen.setView(new andView.andViewImpl(ActivityWrapper.getActivity()));
        return andscreen;
    }

    @Override // com.rockstargames.hal.andView
    public void addSubview(andView andview) {
        super.addSubview(andview);
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = f + f3;
        super.setBounds(f7, f2, f3, f4, f5, f6);
        this.f38px = f7;
        this.f39py = f2;
        this.f37pw = f3;
        this.f36ph = f4;
        View view = this.loadingScreenView;
        if (view != null) {
            int i = (int) f3;
            view.setLayoutParams(new AbsoluteLayout.LayoutParams(i, (int) f4, ((int) f7) - i, 0));
            this.loadingScreenView.bringToFront();
        }
    }

    public void showLoadingGrid(boolean z) {
        if (z) {
            if (this.loadingScreenView != null) {
                return;
            }
            createLoadingScreen(this.screenName);
            this.loadingScreenView.setLayoutParams(new AbsoluteLayout.LayoutParams((int) this.f37pw, (int) this.f36ph, (int) this.f38px, 0));
            this.loadingScreenView.bringToFront();
            return;
        }
        cleanup();
    }

    public void cleanup() {
        Log.i("andScreen", "Cleaning up a (loading) screen...");
        View view = this.loadingScreenView;
        ViewParent viewParent = null;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.loadingScreenView);
            }
            this.loadingScreenView = null;
            this.f38px = 0.0f;
        }
        if (this.view != null) {
            viewParent = this.view.getParent();
        }
        if (viewParent != null) {
            if (viewParent instanceof ViewGroup) {
                ((ViewGroup) viewParent).removeView(this.view);
            } else {
                Log.e("andScreen", "Unable to remove screen from parent!");
            }
        }
    }

    public void Quit() {
        ActivityWrapper.getActivity().finish();
    }
}
