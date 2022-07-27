package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/* loaded from: classes.dex */
public class PackedImage {
    PackedImageAttributes attributes;
    BitmapDrawable bitmapDrawable;
    Paint myPaint;
    private float scaleX;
    private float scaleY;
    Bitmap tiledBitmap;
    boolean filteringTurnedOff = false;
    private ColorFilter previousFilter = null;
    private int previousDrawWidth = -1;
    private int previousDrawHeight = -1;
    RectF targetRect = new RectF();
    Rect sourceRect = new Rect();

    public PackedImage(PackedImageAttributes packedImageAttributes) {
        this.attributes = packedImageAttributes;
        packedImageAttributes.textureAtlas.addReference(this);
        Paint paint = new Paint();
        this.myPaint = paint;
        paint.setAntiAlias(true);
        this.myPaint.setFilterBitmap(true);
        this.sourceRect.left = getX();
        this.sourceRect.top = getY();
        this.sourceRect.right = getX() + getPackedWidth();
        this.sourceRect.bottom = getY() + getPackedHeight();
    }

    public TextureAtlas unload() {
        if (this.attributes.textureAtlas.removeReference(this)) {
            return this.attributes.textureAtlas;
        }
        return null;
    }

    public int getX() {
        return this.attributes.f32x;
    }

    public int getY() {
        return this.attributes.f33y;
    }

    public int getWidth() {
        return this.attributes.width;
    }

    public int getHeight() {
        return this.attributes.height;
    }

    public int getPackedWidth() {
        return this.attributes.packedWidth;
    }

    public int getPackedHeight() {
        return this.attributes.packedHeight;
    }

    public int getOffsetX() {
        return this.attributes.offsetX;
    }

    public int getOffsetY() {
        return this.attributes.offsetY;
    }

    public Bitmap getBitmap() {
        return this.attributes.textureAtlas.getBitmap();
    }

    public void turnOffFiltering() {
        if (!this.filteringTurnedOff) {
            this.filteringTurnedOff = true;
            this.myPaint.setAntiAlias(false);
            this.myPaint.setFilterBitmap(false);
            this.myPaint.setDither(false);
            this.sourceRect.left++;
            this.sourceRect.top++;
            this.sourceRect.right--;
            this.sourceRect.bottom--;
        }
    }

    public void Draw(Canvas canvas, int i, int i2, boolean z) {
        Draw(canvas, i, i2, z, null);
    }

    public void Draw(Canvas canvas, int i, int i2, boolean z, ColorFilter colorFilter) {
        boolean z2;
        if (i == this.previousDrawWidth && i2 == this.previousDrawHeight) {
            z2 = false;
        } else {
            this.scaleX = i / getWidth();
            this.scaleY = i2 / getHeight();
            this.previousDrawWidth = i;
            this.previousDrawHeight = i2;
            z2 = true;
        }
        Bitmap bitmap = getBitmap();
        if (colorFilter != this.previousFilter) {
            this.myPaint.setColorFilter(colorFilter);
            this.previousFilter = colorFilter;
        }
        if (bitmap == null) {
            Log.e("PackedImage", "PackedImage attempting to draw null bitmap!");
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            return;
        }
        if (bitmap.isRecycled()) {
            Log.e("PackedImage", "PackedImage attempting to draw recycled bitmap!");
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            ActivityWrapper.getTextureAtlasCache().evictAll();
            System.gc();
            bitmap = getBitmap();
        }
        if (bitmap == null || bitmap.isRecycled()) {
            Log.e("PackedImage", "PackedImage unable to reload recycled bitmap!");
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        } else if (z) {
            if (this.tiledBitmap == null) {
                this.tiledBitmap = Bitmap.createBitmap(bitmap, this.sourceRect.left, this.sourceRect.top, getWidth(), getHeight());
                BitmapDrawable bitmapDrawable = new BitmapDrawable(ActivityWrapper.getActivity().getResources(), this.tiledBitmap);
                this.bitmapDrawable = bitmapDrawable;
                bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            }
            this.bitmapDrawable.setBounds(0, 0, (int) (getWidth() * this.scaleX), (int) (getHeight() * this.scaleY));
            this.bitmapDrawable.draw(canvas);
        } else {
            if (z2) {
                this.targetRect.left = getOffsetX() * this.scaleX;
                this.targetRect.top = getOffsetY() * this.scaleY;
                this.targetRect.right = (getOffsetX() + getPackedWidth()) * this.scaleX;
                this.targetRect.bottom = (getOffsetY() + getPackedHeight()) * this.scaleY;
            }
            canvas.drawBitmap(bitmap, this.sourceRect, this.targetRect, this.myPaint);
        }
    }
}
