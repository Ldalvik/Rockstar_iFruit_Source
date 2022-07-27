package com.rockstargames.hal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

/* loaded from: classes.dex */
public class ContainerLayout extends AbsoluteLayout {
    protected Matrix matrix;
    protected float[] matrixValues;
    protected Matrix offsetMatrix;
    protected float[] offsetMatrixValues;
    protected RectF boundsRect = new RectF();
    protected RectF transformedRect = new RectF();
    protected PointF transformOffset = new PointF();
    boolean canInvalidate = true;
    boolean clippingChildren = true;

    public ContainerLayout(Context context) {
        super(context);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.offsetMatrix != null) {
            canvas.save();
            canvas.concat(this.offsetMatrix);
        }
        try {
            super.dispatchDraw(canvas);
        } catch (Throwable th) {
            Log.e("ContainerLayout", "Error attempting to dispatch draw:", th);
        }
        if (this.offsetMatrix != null) {
            canvas.restore();
        }
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        float f = i3 + i;
        float f2 = i4 + i2;
        float f3 = i;
        if (this.boundsRect.left == f3 && this.boundsRect.top == i2 && this.boundsRect.right == f && this.boundsRect.bottom == f2) {
            return;
        }
        this.boundsRect.left = f3;
        this.boundsRect.top = i2;
        this.boundsRect.right = f;
        this.boundsRect.bottom = f2;
        updateLayoutParams();
    }

    public void setTransform(float f, float f2, float f3, float f4, float f5, float f6) {
        if (this.matrix == null) {
            this.matrix = new Matrix();
            this.matrixValues = new float[16];
            this.offsetMatrix = new Matrix();
            this.offsetMatrixValues = new float[16];
        }
        this.transformOffset.x = f5;
        this.transformOffset.y = f6;
        float[] fArr = this.matrixValues;
        fArr[0] = f;
        fArr[1] = f3;
        fArr[2] = f5;
        fArr[3] = f2;
        fArr[4] = f4;
        fArr[5] = f6;
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
        fArr[8] = 1.0f;
        this.matrix.setValues(fArr);
        updateLayoutParams();
        if (this.canInvalidate) {
            invalidate();
            this.canInvalidate = false;
        }
    }

    protected void updateLayoutParams() {
        if (this.matrix == null) {
            this.transformedRect.set(this.boundsRect);
        } else {
            this.transformedRect.set(0.0f, 0.0f, this.boundsRect.width(), this.boundsRect.height());
            System.arraycopy(this.matrixValues, 0, this.offsetMatrixValues, 0, 9);
            float[] fArr = this.offsetMatrixValues;
            fArr[2] = fArr[2] + this.boundsRect.left;
            float[] fArr2 = this.offsetMatrixValues;
            fArr2[5] = fArr2[5] + this.boundsRect.top;
            this.offsetMatrix.setValues(this.offsetMatrixValues);
            this.offsetMatrix.mapRect(this.transformedRect);
            float[] fArr3 = this.offsetMatrixValues;
            fArr3[2] = fArr3[2] - this.transformedRect.left;
            float[] fArr4 = this.offsetMatrixValues;
            fArr4[5] = fArr4[5] - this.transformedRect.top;
            this.offsetMatrix.setValues(this.offsetMatrixValues);
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            setLayoutParams(new AbsoluteLayout.LayoutParams((int) this.transformedRect.width(), (int) this.transformedRect.height(), (int) this.transformedRect.left, (int) this.transformedRect.top));
        } else if (layoutParams instanceof AbsoluteLayout.LayoutParams) {
            AbsoluteLayout.LayoutParams layoutParams2 = (AbsoluteLayout.LayoutParams) layoutParams;
            int width = (int) this.transformedRect.width();
            int height = (int) this.transformedRect.height();
            int i = (int) this.transformedRect.left;
            int i2 = (int) this.transformedRect.top;
            if (layoutParams2.width == width && layoutParams2.height == height && layoutParams2.x == i && layoutParams2.y == i2) {
                return;
            }
            layoutParams.width = width;
            layoutParams.height = height;
            layoutParams2.x = i;
            layoutParams2.y = (int) this.transformedRect.top;
            setLayoutParams(layoutParams);
        } else {
            layoutParams.width = (int) this.boundsRect.width();
            layoutParams.height = (int) this.boundsRect.height();
            setLayoutParams(layoutParams);
        }
    }

    @Override // android.view.ViewGroup
    public void setClipChildren(boolean z) {
        super.setClipChildren(z);
        this.clippingChildren = z;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.matrix != null) {
            canvas.save();
            canvas.concat(this.matrix);
        }
        super.onDraw(canvas);
        if (this.matrix != null) {
            canvas.restore();
        }
        this.canInvalidate = true;
        if (this.clippingChildren) {
            invalidate();
        }
    }
}
