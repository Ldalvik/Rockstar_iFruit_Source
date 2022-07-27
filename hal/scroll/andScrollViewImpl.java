package com.rockstargames.hal.scroll;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.ScrollView;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.ContainerLayout;
import com.rockstargames.hal.PackedImage;
import com.rockstargames.hal.andImage;
import com.rockstargames.hal.andScrollView;
import com.rockstargames.hal.andViewManager;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class andScrollViewImpl extends ScrollView implements andScrollView.ScrollImpl {
    private final andScrollView andScrollView;
    PackedImage backgroundPackedImage;
    private Paint backgroundPaint;
    private Rect backgroundRect;
    private final ContainerLayout container;
    private SoftReference<Bitmap> lastBackgroundBitmap;
    private BitmapDrawable lastBackgroundBitmapTiled;
    private boolean touchEventsDisabled = false;
    private boolean tileBackground = false;
    Point touchStart = new Point();
    private ArrayList<andScrollView.ScrollImpl> subScrolls = new ArrayList<>();

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setPage(int i) {
    }

    public andScrollViewImpl(andScrollView andscrollview) {
        super(ActivityWrapper.getActivity());
        this.andScrollView = andscrollview;
        setLayoutParams(new AbsoluteLayout.LayoutParams(200, 200, 0, 0));
        setId(andViewManager.genID());
        ContainerLayout containerLayout = new ContainerLayout(ActivityWrapper.getActivity());
        this.container = containerLayout;
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        addView(this.container);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setBackgroundAndImage(andImage andimage, boolean z) {
        this.backgroundPackedImage = andimage.getPackedImage();
        this.backgroundRect = new Rect();
        this.backgroundPaint = new Paint();
        this.tileBackground = z;
        Bitmap bitmap = this.backgroundPackedImage.getBitmap();
        if (!this.tileBackground) {
            this.lastBackgroundBitmap = new SoftReference<>(bitmap);
        } else {
            this.lastBackgroundBitmapTiled = new BitmapDrawable(bitmap);
        }
        this.backgroundPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        PackedImage packedImage = this.backgroundPackedImage;
        if (packedImage != null) {
            Bitmap bitmap = packedImage.getBitmap();
            if (!this.tileBackground) {
                SoftReference<Bitmap> softReference = this.lastBackgroundBitmap;
                if (softReference == null || bitmap != softReference.get()) {
                    this.lastBackgroundBitmap = new SoftReference<>(bitmap);
                    this.backgroundPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT));
                }
                this.backgroundRect.set(0, 0, canvas.getWidth(), canvas.getHeight() * 2);
                canvas.drawBitmap(bitmap, (Rect) null, this.backgroundRect, this.backgroundPaint);
            } else {
                this.lastBackgroundBitmapTiled.setBounds(0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
                this.lastBackgroundBitmapTiled.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                this.lastBackgroundBitmapTiled.draw(canvas);
            }
        }
        super.onDraw(canvas);
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

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.touchStart.x = (int) motionEvent.getX();
            this.touchStart.y = (int) motionEvent.getY();
        } else if (action == 2 && Math.abs(this.touchStart.x - motionEvent.getX()) > Math.abs(this.touchStart.y - motionEvent.getY())) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (!this.touchEventsDisabled) {
            this.andScrollView.onTouchEvent(motionEvent);
        }
        return onTouchEvent;
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
    public void setToStartingPosition() {
        post(new Runnable() { // from class: com.rockstargames.hal.scroll.andScrollViewImpl.1
            @Override // java.lang.Runnable
            public void run() {
                andScrollViewImpl.this.fullScroll(33);
            }
        });
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
