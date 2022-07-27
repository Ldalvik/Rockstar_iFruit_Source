package com.rockstargames.hal.scroll;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.ContainerLayout;
import com.rockstargames.hal.andImage;
import com.rockstargames.hal.andScrollView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class andHorizScrollViewImpl extends HorizontalScrollView implements andScrollView.ScrollImpl {
    private final andScrollView andScrollView;
    private final ContainerLayout container;
    private boolean touchEventsDisabled = false;
    private ArrayList<andScrollView.ScrollImpl> subScrolls = new ArrayList<>();

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setBackgroundAndImage(andImage andimage, boolean z) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setPage(int i) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setToStartingPosition() {
    }

    public andHorizScrollViewImpl(andScrollView andscrollview) {
        super(ActivityWrapper.getActivity());
        this.andScrollView = andscrollview;
        setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 100, 100));
        setHorizontalScrollBarEnabled(true);
        ContainerLayout containerLayout = new ContainerLayout(ActivityWrapper.getActivity());
        this.container = containerLayout;
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        addView(this.container);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setTouchEventsEnabled(boolean z) {
        this.touchEventsDisabled = !z;
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (!this.touchEventsDisabled) {
            andScrollView andscrollview = this.andScrollView;
            andscrollview.scrollViewDidScroll(andscrollview.getHandle());
        }
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action;
        super.onTouchEvent(motionEvent);
        if (!this.touchEventsDisabled && ((action = motionEvent.getAction()) == 1 || action == 3)) {
            int scrollX = getScrollX();
            int width = getWidth();
            int i = scrollX % width;
            if (scrollX < width / 2) {
                scrollTo(scrollX - i, getScrollY());
            } else {
                scrollTo(scrollX + (width - i), getScrollY());
            }
        }
        return true;
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void touchEvent(MotionEvent motionEvent) {
        if (!this.touchEventsDisabled) {
            onTouchEvent(motionEvent);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof andScrollView.ScrollImpl) {
                ((andScrollView.ScrollImpl) parent).addSubScroll(this);
            }
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void addSubScroll(andScrollView.ScrollImpl scrollImpl) {
        this.subScrolls.add(scrollImpl);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public ContainerLayout getContainer() {
        return this.container;
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void transferContainerContents(andScrollView.ScrollImpl scrollImpl) {
        ContainerLayout container = scrollImpl.getContainer();
        while (container.getChildCount() > 0) {
            View childAt = container.getChildAt(0);
            ViewParent parent = childAt.getParent();
            if (parent != null && (parent instanceof ViewGroup)) {
                ((ViewGroup) parent).removeView(childAt);
            }
            container.addView(childAt);
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void removeAllSubviews() {
        this.container.removeAllViews();
    }
}
