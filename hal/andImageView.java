package com.rockstargames.hal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class andImageView extends andView {
    private static Paint caulkPaint = new Paint();
    static int staticCount;
    private int overlayColour;
    private ShapeDrawable overlay = null;
    private float overlayScale = 1.0f;
    private ImageView overlayImageView = null;

    public andImageView(int i) {
        super(i);
        setView(new andImageViewImp());
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andImageView createView(int i) {
        return new andImageView(i);
    }

    private andImageViewImp getImpl() {
        return (andImageViewImp) getView();
    }

    public void SetShapeOverlayScale(float f) {
        if (this.overlay != null) {
            this.overlayScale = f;
            updateOverlayScale();
        }
    }

    public void SetShapeOverlay(int i, int i2, int i3, int i4) {
        this.overlayColour = Color.argb(i, i2, i3, i4);
        if (this.overlay == null) {
            this.overlayImageView = new OverlayImageView();
            updateOverlayScale();
            this.overlayImageView.setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 0, 0));
            this.container.addView(this.overlayImageView);
            return;
        }
        updateOverlayScale();
    }

    /* loaded from: classes.dex */
    private static class OverlayImageView extends ImageView {
        public OverlayImageView() {
            super(ActivityWrapper.getActivity());
        }
    }

    void updateOverlayScale() {
        if (this.overlayImageView != null) {
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            this.overlay = shapeDrawable;
            shapeDrawable.getPaint().setColor(this.overlayColour);
            this.overlay.getPaint().setAntiAlias(true);
            this.overlay.setIntrinsicWidth((int) (this.width * this.overlayScale));
            this.overlay.setIntrinsicHeight((int) (this.height * this.overlayScale));
            this.overlayImageView.setImageDrawable(this.overlay);
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) this.overlayImageView.getLayoutParams();
            AbsoluteLayout.LayoutParams layoutParams2 = (AbsoluteLayout.LayoutParams) this.container.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);
            }
            layoutParams.width = this.overlay.getIntrinsicWidth();
            layoutParams.height = this.overlay.getIntrinsicHeight();
            layoutParams.x = (layoutParams2.width - layoutParams.width) / 2;
            layoutParams.y = (layoutParams2.height - layoutParams.height) / 2;
        }
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        super.setBounds(f, f2, f3, f4, f5, f6);
        updateOverlayScale();
    }

    public void setImage(andImage andimage) {
        if (andimage == null) {
            getImpl().setPackedImage(null);
            return;
        }
        andimage.getName().startsWith("viewrender");
        getImpl().setPackedImage(andimage.getPackedImage());
        getImpl().setPadding(0, 0, 0, 0);
    }

    public void setTiled(boolean z) {
        getImpl().setTiled(z);
    }

    /* loaded from: classes.dex */
    public class andImageViewImp extends View {
        boolean attached = false;
        PackedImage packedImage;
        boolean tiled;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andImageViewImp() {
            super(ActivityWrapper.getActivity());
            andImageView.this = r2;
            setId(andViewManager.genID());
            setOnTouchListener(r2);
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
        }

        public boolean isAttached() {
            return this.attached;
        }

        public void setPackedImage(PackedImage packedImage) {
            this.packedImage = packedImage;
            invalidate();
        }

        public void setTiled(boolean z) {
            this.tiled = z;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            PackedImage packedImage = this.packedImage;
            if (packedImage != null) {
                packedImage.Draw(canvas, getWidth(), getHeight(), this.tiled);
                if (!andImageView.this.antiFlicker) {
                    return;
                }
                invalidate();
            }
        }
    }
}
