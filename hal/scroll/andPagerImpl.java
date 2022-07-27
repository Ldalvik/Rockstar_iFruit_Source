package com.rockstargames.hal.scroll;

import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.ContainerLayout;
import com.rockstargames.hal.andImage;
import com.rockstargames.hal.andScrollView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class andPagerImpl extends ViewPager implements andScrollView.ScrollImpl {
    private final andScrollView andScrollView;
    private boolean touchEventsDisabled = false;
    private int pageFromNative = -1;
    private ArrayList<View> items = new ArrayList<>();
    int currentPage = 0;
    private int lastUpdatePage = -1;
    public FakeContainer fakeContainer = new FakeContainer();
    andPagerAdapter adapter = new andPagerAdapter();

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void addSubScroll(andScrollView.ScrollImpl scrollImpl) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setBackgroundAndImage(andImage andimage, boolean z) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void touchEvent(MotionEvent motionEvent) {
    }

    /* loaded from: classes.dex */
    public class FakeContainer extends ContainerLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FakeContainer() {
            super(ActivityWrapper.getActivity());
            andPagerImpl.this = r1;
        }

        @Override // android.view.ViewGroup
        public void addView(View view) {
            andPagerImpl.this.items.add(view);
            andPagerImpl.this.lastUpdatePage = -1;
            if (andPagerImpl.this.getAdapter() != andPagerImpl.this.adapter) {
                andPagerImpl andpagerimpl = andPagerImpl.this;
                andpagerimpl.setAdapter(andpagerimpl.adapter);
                return;
            }
            andPagerImpl.this.adapter.notifyDataSetChanged();
        }

        @Override // android.view.ViewGroup, android.view.ViewManager
        public void removeView(View view) {
            andPagerImpl.this.items.remove(view);
            andPagerImpl.this.lastUpdatePage = -1;
            if (andPagerImpl.this.getAdapter() != andPagerImpl.this.adapter) {
                andPagerImpl andpagerimpl = andPagerImpl.this;
                andpagerimpl.setAdapter(andpagerimpl.adapter);
                return;
            }
            andPagerImpl.this.adapter.notifyDataSetChanged();
        }

        @Override // android.view.ViewGroup
        public void removeAllViews() {
            andPagerImpl.this.items.clear();
        }
    }

    /* loaded from: classes.dex */
    public class andPagerAdapter extends PagerAdapter {
        private ArrayList<View> addedViews;

        @Override // android.support.p000v4.view.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        }

        @Override // android.support.p000v4.view.PagerAdapter
        public int getItemPosition(Object obj) {
            return -2;
        }

        private andPagerAdapter() {
            andPagerImpl.this = r1;
            this.addedViews = new ArrayList<>();
        }

        @Override // android.support.p000v4.view.PagerAdapter
        public int getCount() {
            return andPagerImpl.this.items.size();
        }

        @Override // android.support.p000v4.view.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            int intValue = ((Integer) obj).intValue();
            return intValue < andPagerImpl.this.items.size() && view == andPagerImpl.this.items.get(intValue);
        }

        @Override // android.support.p000v4.view.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            return Integer.valueOf(i);
        }

        @Override // android.support.p000v4.view.PagerAdapter
        public void finishUpdate(ViewGroup viewGroup) {
            if (andPagerImpl.this.lastUpdatePage != andPagerImpl.this.currentPage) {
                viewGroup.removeAllViews();
                for (int i = 0; i < andPagerImpl.this.items.size(); i++) {
                    viewGroup.addView((View) andPagerImpl.this.items.get(i));
                }
                andPagerImpl andpagerimpl = andPagerImpl.this;
                andpagerimpl.lastUpdatePage = andpagerimpl.currentPage;
            }
            super.finishUpdate(viewGroup);
        }
    }

    /* loaded from: classes.dex */
    public class Listener implements ViewPager.OnPageChangeListener {
        @Override // android.support.p000v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // android.support.p000v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        private Listener() {
            andPagerImpl.this = r1;
        }

        @Override // android.support.p000v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            if (!andPagerImpl.this.touchEventsDisabled) {
                andPagerImpl.this.andScrollView.scrollViewPageWillChange(andPagerImpl.this.andScrollView.getHandle(), i);
                if (andPagerImpl.this.currentPage == i) {
                    return;
                }
                andPagerImpl.this.currentPage = i;
                andPagerImpl.this.andScrollView.scrollViewPageDidChange(andPagerImpl.this.andScrollView.getHandle(), andPagerImpl.this.currentPage);
                andPagerImpl.this.pageFromNative = i;
            }
        }
    }

    public andPagerImpl(andScrollView andscrollview) {
        super(ActivityWrapper.getActivity());
        this.andScrollView = andscrollview;
        setLayoutParams(new AbsoluteLayout.LayoutParams(100, 100, 0, 0));
        setOnPageChangeListener(new Listener());
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setTouchEventsEnabled(boolean z) {
        this.touchEventsDisabled = !z;
    }

    @Override // android.support.p000v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.touchEventsDisabled) {
            this.andScrollView.onTouch(null, motionEvent);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.support.p000v4.view.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.touchEventsDisabled) {
            this.andScrollView.onTouch(null, motionEvent);
            View view = (View) getParent();
            if (view != null) {
                view.invalidate();
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.support.p000v4.view.ViewPager, android.view.View
    public void onMeasure(int i, int i2) {
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            if (!(childAt.getLayoutParams() instanceof ViewPager.LayoutParams)) {
                Log.e("andPager", "Bad view: " + childAt.toString());
                ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
                layoutParams.width = childAt.getLayoutParams().width;
                layoutParams.height = childAt.getLayoutParams().height;
                childAt.setLayoutParams(layoutParams);
            }
        }
        Log.e("andPager", "This view's layoutparams: " + getLayoutParams());
        try {
            super.onMeasure(i, i2);
        } catch (Exception e) {
            Log.e("andPager", "Error measuring", e);
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view) {
        if (!(view.getLayoutParams() instanceof ViewPager.LayoutParams)) {
            view.setLayoutParams(new ViewPager.LayoutParams());
        }
        super.addView(view);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setPage(int i) {
        setCurrentItem(i);
        andScrollView andscrollview = this.andScrollView;
        andscrollview.scrollViewPageWillChange(andscrollview.getHandle(), i);
        if (this.currentPage != i) {
            this.currentPage = i;
            andScrollView andscrollview2 = this.andScrollView;
            andscrollview2.scrollViewPageDidChange(andscrollview2.getHandle(), this.currentPage);
            this.pageFromNative = i;
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setToStartingPosition() {
        setCurrentItem(0);
        andScrollView andscrollview = this.andScrollView;
        andscrollview.scrollViewPageWillChange(andscrollview.getHandle(), 0);
        if (this.currentPage != 0) {
            this.currentPage = 0;
            andScrollView andscrollview2 = this.andScrollView;
            andscrollview2.scrollViewPageDidChange(andscrollview2.getHandle(), this.currentPage);
            this.pageFromNative = 0;
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public ContainerLayout getContainer() {
        return this.fakeContainer;
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
        this.fakeContainer.removeAllViews();
        try {
            setAdapter(null);
        } catch (NullPointerException e) {
            Log.e("andPager", "Unable to remove adapter", e);
        }
    }
}
