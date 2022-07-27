package com.rockstargames.hal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class andDrawingView extends andView {
    static int staticCount;
    public ArrayList<Shape> shapes = new ArrayList<>();

    public andDrawingView(int i) {
        super(i);
        setView(new andDrawingViewImpl());
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andDrawingView createView(int i) {
        return new andDrawingView(i);
    }

    public void invalidate() {
        ((andDrawingViewImpl) getView()).invalidate();
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = this.width;
        float f8 = this.height;
        super.setBounds(f, f2, f3, f4, f5, f6);
        float f9 = this.width / f7;
        float f10 = this.height / f8;
        for (int i = 0; i < this.shapes.size(); i++) {
            Shape shape = this.shapes.get(i);
            if (!shape.filled) {
                int size = shape.points.size() * 2;
                float[] fArr = new float[size];
                for (int i2 = 0; i2 < shape.points.size(); i2++) {
                    int i3 = i2 * 2;
                    fArr[i3] = shape.points.get(i2).x * f9;
                    fArr[i3 + 1] = shape.points.get(i2).y * f10;
                }
                shape.path = new Path();
                shape.points.clear();
                for (int i4 = 0; i4 < size - 1; i4 += 2) {
                    shape.addPointInternal(fArr[i4], fArr[i4 + 1]);
                }
                if (shape.pathClosed) {
                    shape.path.close();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Shape {
        public Paint fillPaint;
        public boolean filled;
        public Path path = new Path();
        public boolean pathClosed = false;
        public Paint strokePaint = new Paint();
        public ArrayList<PointF> points = new ArrayList<>();

        public Shape(boolean z) {
            this.filled = z;
            this.strokePaint.setStyle(Paint.Style.STROKE);
            if (this.filled) {
                Paint paint = new Paint();
                this.fillPaint = paint;
                paint.setStyle(Paint.Style.FILL);
            }
        }

        public void addPoint(float f, float f2) {
            if (this.filled) {
                throw new RuntimeException("Attempting to add a point to a filled shape!");
            }
            addPointInternal(f, f2);
        }

        public void addPointInternal(float f, float f2) {
            if (this.points.size() == 0) {
                this.path.moveTo(f, f2);
            } else {
                this.path.lineTo(f, f2);
            }
            this.points.add(new PointF(f, f2));
        }

        public void removePoint(int i) {
            if (this.filled) {
                throw new RuntimeException("Attempting to remove a point from a filled shape!");
            }
            this.points.remove(i);
            this.path.reset();
            boolean z = true;
            Iterator<PointF> it = this.points.iterator();
            while (it.hasNext()) {
                PointF next = it.next();
                if (z) {
                    this.path.moveTo(next.x, next.y);
                    z = false;
                } else {
                    this.path.lineTo(next.x, next.y);
                }
            }
        }

        public void clear() {
            if (this.filled) {
                throw new RuntimeException("Attempting to clear a filled shape!");
            }
            this.points.clear();
            this.path.reset();
            this.pathClosed = false;
        }

        public void setPoints(float[] fArr) {
            if (!this.filled) {
                throw new RuntimeException("Attempting set all points to a line!");
            }
            this.path = new Path();
            this.points.clear();
            for (int i = 0; i < fArr.length - 1; i += 2) {
                addPointInternal(fArr[i], fArr[i + 1]);
            }
            this.path.close();
            this.pathClosed = true;
        }

        public void draw(Canvas canvas) {
            if (this.points.size() > 0) {
                Paint paint = this.fillPaint;
                if (paint != null) {
                    canvas.drawPath(this.path, paint);
                }
                canvas.drawPath(this.path, this.strokePaint);
            }
        }
    }

    /* loaded from: classes.dex */
    public class andDrawingViewImpl extends View {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andDrawingViewImpl() {
            super(ActivityWrapper.getActivity());
            andDrawingView.this = r1;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Iterator<Shape> it = andDrawingView.this.shapes.iterator();
            while (it.hasNext()) {
                it.next().draw(canvas);
            }
        }
    }

    public int createShape(boolean z) {
        this.shapes.add(new Shape(z));
        return this.shapes.size() - 1;
    }

    public void setFillColour(long j, int i, int i2, int i3, int i4) {
        this.shapes.get((int) j).fillPaint.setColor(Color.argb(i, i2, i3, i4));
        invalidate();
    }

    public void setStrokeColour(long j, int i, int i2, int i3, int i4) {
        this.shapes.get((int) j).strokePaint.setColor(Color.argb(i, i2, i3, i4));
        invalidate();
    }

    public void setStrokeThickness(long j, float f) {
        this.shapes.get((int) j).strokePaint.setStrokeWidth(f);
        invalidate();
    }

    public void addPoint(long j, float f, float f2) {
        this.shapes.get((int) j).addPoint(f, f2);
        invalidate();
    }

    public void removePoint(long j, int i) {
        this.shapes.get((int) j).removePoint(i);
        invalidate();
    }

    public void clear(long j) {
        this.shapes.get((int) j).clear();
        invalidate();
    }

    public void setPoints(long j, float[] fArr) {
        this.shapes.get((int) j).setPoints(fArr);
        invalidate();
    }
}
