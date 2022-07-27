package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.p000v4.view.ViewCompat;
import android.text.Layout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class andLabel extends andView {
    private static Typeface defaultFont;
    static int staticCount;
    private int cachedH;
    private int cachedW;
    private boolean hasShadow = false;
    private float sizeAdjusted;
    private float sizeFromHal;
    protected String text;

    /* loaded from: classes.dex */
    enum TextAlignment {
        TEXT_ALIGNMENT_CENTER,
        TEXT_ALIGNMENT_LEFT,
        TEXT_ALIGNMENT_RIGHT,
        TEXT_ALIGNMENT_JUSTIFY,
        TEXT_ALIGNMENT_CENTER_BOTH
    }

    public andLabel(int i) {
        super(i);
        setView(new andLabelImpl());
        staticCount++;
    }

    public andLabel(int i, boolean z) {
        super(i);
        if (z) {
            setView(new andLabelImpl());
        }
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andLabel createView(int i) {
        return new andLabel(i);
    }

    protected TextView getTextView() {
        return (TextView) this.view;
    }

    protected void setDirty() {
        if (this.view instanceof andLabelImpl) {
            ((andLabelImpl) this.view).setDirty();
        }
    }

    public void setText(String str) {
        this.text = str;
        setDirty();
        getTextView().setText(str);
    }

    public void setTextSize(float f) {
        setDirty();
        this.sizeFromHal = f;
        float f2 = f / ActivityWrapper.getActivity().getResources().getDisplayMetrics().density;
        this.sizeAdjusted = f2;
        getTextView().setTextSize(1, f2);
    }

    public void setBackgroundTransparent() {
        setDirty();
        getTextView().setBackgroundColor(0);
    }

    private void printDebugFontSizeString() {
        DisplayMetrics displayMetrics = ActivityWrapper.getActivity().getResources().getDisplayMetrics();
        Log.i("andLabel", "HAL: " + this.sizeFromHal + ", Final: " + this.sizeAdjusted + ", Text: " + this.text + " dens:" + displayMetrics.density + " dpi:" + displayMetrics.densityDpi + " sd:" + displayMetrics.scaledDensity + " height:" + displayMetrics.heightPixels + " H/h:" + (this.sizeFromHal / displayMetrics.heightPixels) + " H*h:" + (this.sizeFromHal * displayMetrics.heightPixels));
    }

    public void setTextColour(int i, int i2, int i3, int i4) {
        setDirty();
        getTextView().setTextColor(Color.argb(i, i2, i3, i4));
    }

    public int getTextColour() {
        return ((andLabelImpl) getTextView()).getCurrentTextColor();
    }

    public void setAutoResize(int i) {
        setDirty();
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        setAutoResize(z);
    }

    public void setAutoResize(boolean z) {
        setDirty();
        getTextView().setHorizontallyScrolling(true);
        if (z) {
            try {
                TextView textView = getTextView();
                TextPaint paint = textView.getPaint();
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                Paint paint2 = new Paint();
                paint2.setTextSize(Math.max(textView.getTextSize(), paint.getTextSize()));
                paint2.setTypeface(textView.getTypeface());
                StringTokenizer stringTokenizer = new StringTokenizer(textView.getText().toString(), "\r\n");
                Point point = new Point(0, 0);
                Rect rect = new Rect();
                while (stringTokenizer.hasMoreTokens()) {
                    String nextToken = stringTokenizer.nextToken();
                    if (nextToken.length() != 0) {
                        paint2.getTextBounds(nextToken, 0, nextToken.length(), rect);
                        int width = rect.width() + 2 + (rect.left > 0 ? rect.left : 0);
                        rect.height();
                        if (width > point.x) {
                            point.x = width;
                        }
                        point.y = (int) (point.y + (fontMetrics.bottom - fontMetrics.top) + fontMetrics.leading);
                    }
                }
                this.cachedW = point.x;
                this.cachedH = point.y;
            } catch (Exception e) {
                Log.e("andLabel", "Autoresize exception", e);
            }
        }
    }

    public int getCachedW() {
        return this.cachedW;
    }

    public int getCachedH() {
        return this.cachedH;
    }

    public void setFont(String str, String str2, String str3) {
        setDirty();
        if (str != null && str.length() > 0) {
            str2 = str + "/" + str2;
        }
        try {
            getTextView().setTypeface(Typeface.createFromAsset(getTextView().getResources().getAssets(), str2));
        } catch (Exception e) {
            Log.e("andLabel", "Failed to loaded typeface (" + str2 + ")");
            e.printStackTrace();
        }
    }

    public void setTextAlignment(int i) {
        setDirty();
        int i2 = C05161.$SwitchMap$com$rockstargames$hal$andLabel$TextAlignment[TextAlignment.values()[i].ordinal()];
        boolean z = true;
        if (i2 == 1) {
            getTextView().setGravity(1);
        } else if (i2 == 2) {
            getTextView().setGravity(3);
        } else if (i2 == 3) {
            getTextView().setGravity(5);
        } else if (i2 == 4) {
            getTextView().setGravity(3);
        } else if (i2 == 5) {
            getTextView().setGravity(17);
        }
        if (getTextView() instanceof andLabelImpl) {
            andLabelImpl andlabelimpl = (andLabelImpl) getTextView();
            if (TextAlignment.values()[i] == TextAlignment.TEXT_ALIGNMENT_CENTER) {
                z = false;
            }
            andlabelimpl.setCacheEnabled(z);
        }
    }

    /* renamed from: com.rockstargames.hal.andLabel$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C05161 {
        static final /* synthetic */ int[] $SwitchMap$com$rockstargames$hal$andLabel$TextAlignment;

        static {
            int[] iArr = new int[TextAlignment.values().length];
            $SwitchMap$com$rockstargames$hal$andLabel$TextAlignment = iArr;
            try {
                iArr[TextAlignment.TEXT_ALIGNMENT_CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andLabel$TextAlignment[TextAlignment.TEXT_ALIGNMENT_LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andLabel$TextAlignment[TextAlignment.TEXT_ALIGNMENT_RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andLabel$TextAlignment[TextAlignment.TEXT_ALIGNMENT_JUSTIFY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andLabel$TextAlignment[TextAlignment.TEXT_ALIGNMENT_CENTER_BOTH.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public int getWidth() {
        return getContainer().getWidth();
    }

    public int getHeight() {
        return getContainer().getHeight();
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        setDirty();
        float f7 = this.hasShadow ? 4.5f : 0.0f;
        super.setBounds(f, f2, f3 + f7, f4 + f7, f5, f6);
    }

    public void setDropShadow() {
        setDirty();
        this.hasShadow = true;
        getTextView().setShadowLayer(3.0f, 1.5f, 1.5f, ViewCompat.MEASURED_STATE_MASK);
    }

    public int getTextHeight() {
        getTextView().measure(View.MeasureSpec.makeMeasureSpec((int) this.width, 1073741824), 0);
        return getTextView().getMeasuredHeight();
    }

    public int getTextWidth() {
        getTextView().measure(View.MeasureSpec.makeMeasureSpec((int) this.width, 1073741824), 0);
        return getTextView().getMeasuredWidth();
    }

    public void setTypeFace(boolean z, boolean z2) {
        setDirty();
        if (z && z2) {
            getTextView().setTypeface(null, 3);
        } else if (z) {
            getTextView().setTypeface(null, 1);
        } else if (z2) {
            getTextView().setTypeface(null, 2);
        } else {
            getTextView().setTypeface(null, 0);
        }
    }

    public void setBlackOutlineWidth(float f) {
        setDirty();
        TextView textView = getTextView();
        if (textView instanceof andLabelImpl) {
            ((andLabelImpl) textView).setOutlineWidth(f);
        }
    }

    public static Typeface getDefaultFont() {
        if (defaultFont == null) {
            try {
                Log.i("andLabel", "Attempting to load typeface:Fonts/HELVETICANEUELTW1G-ROMAN.OTF");
                defaultFont = Typeface.createFromAsset(ActivityWrapper.getActivity().getAssets(), "Fonts/HELVETICANEUELTW1G-ROMAN.OTF");
                Log.i("andLabel", "Successfully loaded typeface (Fonts/HELVETICANEUELTW1G-ROMAN.OTF)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultFont;
    }

    /* loaded from: classes.dex */
    public class andLabelImpl extends TextView {
        private Bitmap cacheBmp;
        private Paint outlinePaint;
        private float outlineStrokeWidth = 0.0f;
        private boolean dirtyFlag = true;
        private boolean cacheError = false;
        private boolean cacheEnabled = true;
        private final Rect tempRect = new Rect();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andLabelImpl() {
            super(ActivityWrapper.getActivity());
            andLabel.this = r2;
            setText("");
            setTextSize(20.0f);
            setId(andViewManager.genID());
            setHorizontallyScrolling(false);
            setTypeface(andLabel.getDefaultFont());
        }

        public void setOutlineWidth(float f) {
            this.outlinePaint = new Paint();
            this.outlineStrokeWidth = f;
            invalidate();
        }

        @Override // android.widget.TextView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            andLabel andlabel = andLabel.this;
            andlabel.onAttachedToWindow(andlabel.getHandle());
        }

        public void setDirty() {
            this.dirtyFlag = true;
            invalidate();
        }

        public void setCacheEnabled(boolean z) {
            this.cacheEnabled = z;
        }

        @Override // android.widget.TextView, android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.outlinePaint != null && this.outlineStrokeWidth > 0.0f) {
                if (!this.cacheError && this.cacheEnabled) {
                    try {
                        canvas.getClipBounds(this.tempRect);
                        Rect rect = this.tempRect;
                        int width = canvas.getWidth();
                        int height = canvas.getHeight();
                        if (this.dirtyFlag) {
                            if (this.cacheBmp == null || this.cacheBmp.getWidth() != width || this.cacheBmp.getHeight() != height) {
                                Log.i("andLabel", "Creating canvas " + width + "x" + height + " (" + rect.left + ", " + rect.top + ") max " + canvas.getMaximumBitmapWidth() + "x" + canvas.getMaximumBitmapHeight());
                                this.cacheBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            }
                            if (this.cacheBmp == null) {
                                this.cacheError = true;
                                Log.e("andLabel", "Unable to generate cache bitmap.");
                                super.onDraw(canvas);
                            }
                            Canvas canvas2 = new Canvas(this.cacheBmp);
                            Paint paint = new Paint();
                            paint.setColor(0);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                            canvas2.drawRect(0.0f, 0.0f, canvas2.getWidth(), canvas2.getHeight(), paint);
                            super.onDraw(canvas2);
                            Layout layout = getLayout();
                            TextPaint paint2 = layout.getPaint();
                            this.outlinePaint.set(paint2);
                            paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
                            paint2.setStrokeWidth(this.outlineStrokeWidth);
                            paint2.setStyle(Paint.Style.STROKE);
                            layout.draw(canvas2);
                            paint2.set(this.outlinePaint);
                            this.dirtyFlag = false;
                        }
                        canvas.drawBitmap(this.cacheBmp, 0.0f, 0.0f, (Paint) null);
                        return;
                    } catch (Error e) {
                        this.cacheError = true;
                        Log.e("andLabel", "Error caching / drawing cache.", e);
                        super.onDraw(canvas);
                        return;
                    } catch (Exception e2) {
                        this.cacheError = true;
                        Log.e("andLabel", "Exception caching / drawing cache.", e2);
                        super.onDraw(canvas);
                        return;
                    }
                }
                super.onDraw(canvas);
                Layout layout2 = getLayout();
                TextPaint paint3 = layout2.getPaint();
                this.outlinePaint.set(paint3);
                paint3.setColor(ViewCompat.MEASURED_STATE_MASK);
                paint3.setStrokeWidth(this.outlineStrokeWidth);
                paint3.setStyle(Paint.Style.STROKE);
                layout2.draw(canvas);
                paint3.set(this.outlinePaint);
                return;
            }
            super.onDraw(canvas);
        }
    }
}
