package com.rockstargames.hal;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.rockstargames.hal.scroll.andHorizScrollViewImpl;
import com.rockstargames.hal.scroll.andPagerImpl;
import com.rockstargames.hal.scroll.andScrollViewImpl;

/* loaded from: classes.dex */
public class andScrollView extends andView {
    static int staticCount;
    private ScrollImpl impl;
    private boolean touchEventsDisabled = false;
    private int pageFromNative = -1;
    public boolean paging = false;

    /* loaded from: classes.dex */
    public interface ScrollImpl {
        void addSubScroll(ScrollImpl scrollImpl);

        ContainerLayout getContainer();

        ViewParent getParent();

        void removeAllSubviews();

        void setBackgroundAndImage(andImage andimage, boolean z);

        void setPage(int i);

        void setToStartingPosition();

        void setTouchEventsEnabled(boolean z);

        void touchEvent(MotionEvent motionEvent);

        void transferContainerContents(ScrollImpl scrollImpl);
    }

    @Override // com.rockstargames.hal.andView
    public native void onTouchEvent(int i, int i2, int i3, float f, float f2);

    public native void scrollViewDidScroll(int i);

    public native void scrollViewPageDidChange(int i, int i2);

    public native void scrollViewPageWillChange(int i, int i2);

    public void setContentSize(int i, int i2) {
    }

    public andScrollView(int i) {
        super(i);
        andScrollViewImpl andscrollviewimpl = new andScrollViewImpl(this);
        this.view = andscrollviewimpl;
        this.impl = andscrollviewimpl;
        this.container = andscrollviewimpl.getContainer();
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andScrollView createView(int i) {
        return new andScrollView(i);
    }

    public void setHorizontal(boolean z, boolean z2) {
        this.paging = z2;
        if (z) {
            try {
                if (!z2) {
                    changeImplTo(new andHorizScrollViewImpl(this));
                } else {
                    changeImplTo(new andPagerImpl(this));
                }
            } catch (Exception e) {
                Log.e("andScrollView", "Exception changing view to horizontal", e);
            }
        }
    }

    public void changeImplTo(ScrollImpl scrollImpl) {
        ScrollImpl scrollImpl2 = this.impl;
        if (scrollImpl2 != null) {
            scrollImpl.transferContainerContents(scrollImpl2);
            ViewParent parent = this.impl.getParent();
            if (parent != null && (parent instanceof ViewGroup)) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeView((View) this.impl);
                viewGroup.addView((View) scrollImpl);
            }
        }
        this.container = scrollImpl.getContainer();
        this.view = (View) scrollImpl;
        this.impl = scrollImpl;
    }

    public void setPage(int i) {
        ScrollImpl scrollImpl = this.impl;
        if (scrollImpl != null) {
            scrollImpl.setPage(i);
        }
    }

    public void setToStartingPosition() {
        ScrollImpl scrollImpl = this.impl;
        if (scrollImpl != null) {
            scrollImpl.setToStartingPosition();
        }
    }

    public void setTouchEventsEnabled(boolean z) {
        this.touchEventsDisabled = !z;
        this.impl.setTouchEventsEnabled(z);
    }

    @Override // com.rockstargames.hal.andView
    public View getOuterView() {
        return this.view;
    }

    @Override // com.rockstargames.hal.andView
    protected View getInnerView() {
        return this.container;
    }

    @Override // com.rockstargames.hal.andView
    public ViewGroup getContainer() {
        return this.container;
    }

    @Override // com.rockstargames.hal.andView
    public void removeFromSuperview() {
        ViewParent parent = this.view.getParent();
        if (parent == null || !(parent instanceof ViewGroup)) {
            return;
        }
        ((ViewGroup) parent).removeView(this.view);
    }

    @Override // com.rockstargames.hal.andView
    public void removeAllSubviews() {
        ScrollImpl scrollImpl = this.impl;
        if (scrollImpl != null) {
            scrollImpl.removeAllSubviews();
        }
    }

    public void setBackgroundImage(andImage andimage, boolean z) {
        this.impl.setBackgroundAndImage(andimage, z);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.touchEventsDisabled) {
            int actionIndex = motionEvent.getActionIndex();
            onTouchEvent(getHandle(), actionIndex, motionEvent.getAction(), motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
            return true;
        }
        return true;
    }
}
