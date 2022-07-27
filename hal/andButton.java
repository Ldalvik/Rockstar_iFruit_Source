package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/* loaded from: classes.dex */
public class andButton extends andLabel {
    private static StateListDrawable blankDrawable;
    static int staticCount;
    PackedImage pressedPacked;
    private float textScale = 1.0f;
    private TextView textView;
    PackedImage unpressedPacked;

    public native void onTouchCancel(int i);

    public native void onTouchDown(int i);

    public native void onTouchMove(int i);

    public native void onTouchUp(int i);

    public void setFont(String str) {
    }

    public andButton(int i) {
        super(i, false);
        setView(new andButtonImpl());
        ContainerLayout containerLayout = this.container;
        TextView textView = new TextView(ActivityWrapper.getActivity());
        this.textView = textView;
        containerLayout.addView(textView);
        this.textView.setLayoutParams(new AbsoluteLayout.LayoutParams(100, 100, 0, 0));
        this.textView.setGravity(17);
        this.textView.setLines(1);
        if (blankDrawable == null) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            blankDrawable = stateListDrawable;
            stateListDrawable.addState(new int[0], new ColorDrawable(0));
        }
        getImageButton().setBackgroundDrawable(blankDrawable);
        staticCount++;
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andButton createView(int i) {
        return new andButton(i);
    }

    protected andButtonImpl getImageButton() {
        return (andButtonImpl) this.view;
    }

    @Override // com.rockstargames.hal.andLabel
    public void setText(String str) {
        this.textView.setText(str);
    }

    @Override // com.rockstargames.hal.andLabel
    public TextView getTextView() {
        return this.textView;
    }

    @Override // com.rockstargames.hal.andLabel
    public void setTextSize(float f) {
        getTextView().setTextSize((f / getTextView().getResources().getDisplayMetrics().scaledDensity) * this.textScale);
    }

    public void setTextColour(int i) {
        this.textView.setTextColor(i);
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        super.setBounds(f, f2, f3, f4, f5, f6);
        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) getTextView().getLayoutParams();
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = (int) f3;
        layoutParams.height = (int) f4;
        getTextView().setLayoutParams(layoutParams);
        getTextView().measure(0, 0);
        float textSize = getTextView().getTextSize();
        this.textScale = 1.0f;
        while (getTextView().getMeasuredWidth() > this.width * 0.9f) {
            this.textScale *= 0.9f;
            setTextSize(textSize);
            getTextView().measure(0, 0);
        }
    }

    public void setBackgroundImages(andImage andimage, andImage andimage2) {
        if (andimage == null) {
            if (andimage2 == null) {
                return;
            }
            andimage = andimage2;
        }
        this.unpressedPacked = andimage.getPackedImage();
        getImageButton().setPackedImage(this.unpressedPacked);
    }

    /* loaded from: classes.dex */
    public class andButtonImpl extends ImageButton implements View.OnTouchListener {
        PackedImage packedImage;
        ColorFilter pressedFilter = null;
        private boolean attached = false;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andButtonImpl() {
            super(ActivityWrapper.getActivity());
            andButton.this = r2;
            setOnTouchListener(this);
            setId(andViewManager.genID());
            setAdjustViewBounds(false);
            setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
        }

        boolean isAttached() {
            return this.attached;
        }

        @Override // android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3 || motionEvent.getAction() == 4) {
                this.pressedFilter = null;
                invalidate();
            }
            if (andButton.this.userInteractionEnabled) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.pressedFilter = new LightingColorFilter(-6710887, 0);
                    andButton andbutton = andButton.this;
                    andbutton.onTouchDown(andbutton.handle);
                    invalidate();
                } else if (action == 1) {
                    andButton andbutton2 = andButton.this;
                    andbutton2.onTouchUp(andbutton2.handle);
                } else if (action == 2) {
                    andButton andbutton3 = andButton.this;
                    andbutton3.onTouchMove(andbutton3.handle);
                } else if (action == 3) {
                    andButton andbutton4 = andButton.this;
                    andbutton4.onTouchCancel(andbutton4.handle);
                }
                return super.dispatchTouchEvent(motionEvent);
            }
            return false;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (!andButton.this.userInteractionEnabled) {
                return false;
            }
            int actionIndex = motionEvent.getActionIndex();
            andButton andbutton = andButton.this;
            andbutton.onTouchEvent(andbutton.handle, actionIndex, motionEvent.getAction(), motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
            return true;
        }

        public void setPackedImage(PackedImage packedImage) {
            this.packedImage = packedImage;
            invalidate();
        }

        @Override // android.widget.ImageView, android.view.View
        public void onDraw(Canvas canvas) {
            PackedImage packedImage = this.packedImage;
            if (packedImage != null) {
                Bitmap bitmap = packedImage.getBitmap();
                if (bitmap == null || bitmap.isRecycled()) {
                    invalidate();
                }
                this.packedImage.Draw(canvas, getWidth(), getHeight(), false, this.pressedFilter);
            }
        }
    }
}
