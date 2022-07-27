package com.rockstargames.hal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class andView implements View.OnTouchListener {
    static int staticCount;
    protected ContainerLayout container;
    public String debugString;
    protected int handle;
    protected float height;
    protected float originX;
    protected float originY;
    protected float paddingB;
    protected float paddingR;
    public andView parent;
    protected View view;
    protected float width;
    protected boolean positionOverridden = false;
    public ArrayList<andView> children = new ArrayList<>();
    private float alpha = 1.0f;
    private int layerType = 0;
    protected boolean antiFlicker = false;
    protected boolean userInteractionEnabled = true;

    public native void onAttachedToWindow(int i);

    public native void onTouchEvent(int i, int i2, int i3, float f, float f2);

    native void setPlatformSize(int i, int i2, int i3);

    public andView(int i) {
        this.handle = i;
        staticCount++;
    }

    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andView createView(int i) {
        andView andview = new andView(i);
        andview.getClass();
        andview.setView(new andViewImpl(ActivityWrapper.getActivity()));
        andview.setBounds(0.0f, 0.0f, 200.0f, 200.0f, 0.0f, 0.0f);
        return andview;
    }

    public int getHandle() {
        return this.handle;
    }

    public void setView(View view) {
        this.view = view;
        if (this.container == null) {
            ContainerLayout containerLayout = new ContainerLayout(view.getContext());
            this.container = containerLayout;
            containerLayout.setId(andViewManager.genID());
            this.container.setPadding(0, 0, 0, 0);
        }
        view.setLayoutParams(new AbsoluteLayout.LayoutParams(100, 100, 0, 0));
        this.container.addView(view);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.userInteractionEnabled) {
            return false;
        }
        int actionIndex = motionEvent.getActionIndex();
        onTouchEvent(getHandle(), actionIndex, motionEvent.getAction(), motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
        return true;
    }

    public void clipChildren(boolean z) {
        getContainer().setClipChildren(z);
    }

    public void sendViewToBack(andView andview) {
        View outerView = andview.getOuterView();
        getContainer().removeView(outerView);
        getContainer().addView(outerView, 0);
    }

    public View getView() {
        return this.view;
    }

    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        if (f3 <= 0.0f) {
            f3 = 600.0f;
        }
        if (f4 <= 0.0f) {
            f4 = 600.0f;
        }
        if (!this.positionOverridden) {
            this.originX = f;
            this.originY = f2;
        }
        this.width = f3;
        this.height = f4;
        this.paddingR = f5;
        this.paddingB = f6;
        recalcLayout();
    }

    protected void recalcLayout() {
        AbsoluteLayout.LayoutParams layoutParams;
        float scale = ActivityWrapper.getScale();
        int i = (int) (this.originX * scale);
        int i2 = (int) (this.originY * scale);
        int i3 = (int) (this.width * scale);
        int i4 = (int) (this.height * scale);
        View outerView = getOuterView();
        ViewGroup.LayoutParams layoutParams2 = outerView.getLayoutParams();
        AbsoluteLayout.LayoutParams layoutParams3 = null;
        if (outerView instanceof ContainerLayout) {
            ((ContainerLayout) outerView).setBounds(i, i2, i3, i4);
        } else {
            try {
                layoutParams = (AbsoluteLayout.LayoutParams) layoutParams2;
            } catch (Exception unused) {
                Log.e("andView", "LayoutParams failed: " + layoutParams2 + " + " + outerView);
                layoutParams = null;
            }
            if (layoutParams == null) {
                layoutParams = new AbsoluteLayout.LayoutParams(i3, i4, i, i2);
            } else {
                layoutParams.x = i;
                layoutParams.y = i2;
                layoutParams.width = i3;
                layoutParams.height = i4;
            }
            outerView.setLayoutParams(layoutParams);
        }
        View innerView = getInnerView();
        if (innerView == outerView) {
            return;
        }
        if (innerView instanceof ContainerLayout) {
            ((ContainerLayout) innerView).setBounds(0, 0, i3, i4);
            return;
        }
        ViewGroup.LayoutParams layoutParams4 = innerView.getLayoutParams();
        if (layoutParams4 != null) {
            try {
                layoutParams3 = (AbsoluteLayout.LayoutParams) layoutParams4;
            } catch (Exception unused2) {
                if (layoutParams4 != null && (layoutParams4 instanceof ViewGroup.LayoutParams)) {
                    return;
                }
                Log.e("andView", "LayoutParams failed: " + layoutParams4 + " + " + outerView);
            }
        }
        if (layoutParams3 == null) {
            layoutParams3 = new AbsoluteLayout.LayoutParams(i3, i4, 0, 0);
        } else {
            layoutParams3.x = 0;
            layoutParams3.y = 0;
            layoutParams3.width = i3;
            layoutParams3.height = i4;
        }
        innerView.setLayoutParams(layoutParams3);
    }

    public void setBackgroundColour(int i, int i2, int i3, int i4) {
        this.view.setBackgroundColor(Color.argb(i, i2, i3, i4));
    }

    public ViewGroup getContainer() {
        return this.container;
    }

    public View getOuterView() {
        return getContainer();
    }

    protected View getInnerView() {
        return getView();
    }

    public void addSubview(andView andview) {
        try {
            boolean z = andview instanceof andLabel;
            andview.parent = this;
            this.container.addView(andview.getOuterView());
            this.children.add(andview);
        } catch (Exception e) {
            Log.e("andView", "Exception adding view: " + andview.getClass().getSimpleName(), e);
        }
    }

    public void removeFromSuperview() {
        andView andview = this.parent;
        if (andview != null) {
            andview.getContainer().removeView(getOuterView());
            this.parent.children.remove(this);
            return;
        }
        Log.e("andView", "Attempting to remove view with no parent!");
    }

    public void removeAllSubviews() {
        while (!this.children.isEmpty()) {
            andView andview = this.children.get(0);
            andview.removeFromSuperview();
            this.children.remove(andview);
        }
    }

    public andImage toImage(String str) {
        Bitmap createBitmap = Bitmap.createBitmap((int) this.width, (int) this.height, Bitmap.Config.ARGB_8888);
        getOuterView().draw(new Canvas(createBitmap));
        float min = Math.min(600.0f / this.width, 600.0f / this.height);
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setScale(min, min);
        Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap, 0, 0, (int) this.width, (int) this.height, matrix, true);
        Bitmap createBitmap3 = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap3).drawBitmap(createBitmap2, (600.0f - (this.width * min)) * 0.5f, (600.0f - (this.height * min)) * 0.5f, (Paint) null);
        PackedImageAttributes packedImageAttributes = new PackedImageAttributes();
        packedImageAttributes.f32x = 0;
        packedImageAttributes.f33y = 0;
        packedImageAttributes.width = 600;
        packedImageAttributes.height = 600;
        packedImageAttributes.packedWidth = 600;
        packedImageAttributes.packedHeight = 600;
        packedImageAttributes.offsetX = 0;
        packedImageAttributes.offsetY = 0;
        packedImageAttributes.textureAtlas = new TextureAtlas();
        packedImageAttributes.textureAtlas.bitmap = createBitmap3;
        return new andImage(str, new PackedImage(packedImageAttributes));
    }

    /* loaded from: classes.dex */
    protected class andViewImpl extends ScrollView {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andViewImpl(Context context) {
            super(context);
            andView.this = r1;
            setId(andViewManager.genID());
            setOnTouchListener(r1);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            andView andview = andView.this;
            andview.onAttachedToWindow(andview.getHandle());
        }
    }

    public void printHierarchy(int i) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append("    ");
        }
        stringBuffer.append(getDebugId());
        stringBuffer.append(": ");
        stringBuffer.append(getClass().getSimpleName());
        stringBuffer.append("(");
        stringBuffer.append(this.originX);
        stringBuffer.append(", ");
        stringBuffer.append(this.originY);
        stringBuffer.append(") ");
        stringBuffer.append(this.width);
        stringBuffer.append(" x ");
        stringBuffer.append(this.height);
        ViewGroup container = getContainer();
        stringBuffer.append(" / (");
        stringBuffer.append(container.getLeft());
        stringBuffer.append(", ");
        stringBuffer.append(container.getTop());
        stringBuffer.append(") ");
        stringBuffer.append(container.getWidth());
        stringBuffer.append(" x ");
        stringBuffer.append(container.getHeight());
        Log.d("andView", stringBuffer.toString());
        Iterator<andView> it = this.children.iterator();
        while (it.hasNext()) {
            it.next().printHierarchy(i + 1);
        }
    }

    public void invalidateHierarchy() {
        View view = this.view;
        if (view != null) {
            view.invalidate();
        }
        Iterator<andView> it = this.children.iterator();
        while (it.hasNext()) {
            it.next().invalidateHierarchy();
        }
    }

    public String getDebugId() {
        String str = this.debugString;
        if (str != null) {
            return str;
        }
        return "Handle " + this.handle;
    }

    public void setAlpha(float f) {
        this.alpha = f;
        getOuterView().setAlpha(Math.min(Math.max(f, 0.0f), 1.0f));
    }

    public void setLayerType(boolean z) {
        this.layerType = z ? 2 : 0;
        getOuterView().setLayerType(this.layerType, null);
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setHidden(boolean z) {
        getOuterView().setVisibility(z ? 4 : 0);
    }

    public boolean getHidden() {
        return getOuterView().getVisibility() == 4;
    }

    public void setTransform(float f, float f2, float f3, float f4, float f5, float f6) {
        this.container.setTransform(f, f2, f3, f4, f5, f6);
    }

    public void setDebugString(String str) {
        this.debugString = str + " (handle " + this.handle + ")";
    }

    public void setAntiFlicker() {
        this.antiFlicker = true;
        Iterator<andView> it = this.children.iterator();
        while (it.hasNext()) {
            it.next().setAntiFlicker();
        }
    }

    public void setUserInteractionEnabled(boolean z) {
        this.userInteractionEnabled = z;
    }
}
