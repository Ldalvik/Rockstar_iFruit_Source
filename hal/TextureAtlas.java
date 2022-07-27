package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TextureAtlas {
    private static boolean FORCE_ARGB4444 = false;
    Bitmap bitmap;
    private int flags;
    private boolean isFromDLC;
    private float scale;
    private String bitmapName = null;
    private List<PackedImage> references = new ArrayList();

    public Bitmap getBitmap() {
        Bitmap bitmap = this.bitmap;
        return bitmap != null ? bitmap : ActivityWrapper.getTextureAtlasCache().get(this);
    }

    public static String getBitmapStats() {
        TextureAtlasCache textureAtlasCache = ActivityWrapper.getTextureAtlasCache();
        if (textureAtlasCache != null) {
            int size = textureAtlasCache.size();
            if (size < 1024) {
                return "bmps: " + size + "b";
            } else if (size < 1048576) {
                return "bmps: " + (size / 1024) + "k";
            } else {
                return "bmps: " + String.format("%.2fMB", Float.valueOf(size / 1048576.0f));
            }
        }
        return "bmps: ";
    }

    public void setParameters(String str, int i, float f, boolean z) {
        this.bitmapName = str;
        this.flags = i;
        this.scale = f;
        this.isFromDLC = z;
    }

    public Bitmap loadImage() {
        return loadImage(false);
    }

    public InputStream getBitmapInputStream() throws IOException {
        InputStream inputStream;
        if (this.isFromDLC) {
            return andFile.getAssetInputStreamForDLC("images/" + this.bitmapName + ".png");
        }
        try {
            inputStream = andFile.getAssetInputStream("images/" + this.bitmapName + ".png", false);
        } catch (FileNotFoundException unused) {
            inputStream = andFile.getAssetInputStream("images/" + this.bitmapName + ".jpg", false);
        }
        if (inputStream != null) {
            return inputStream;
        }
        return andFile.getAssetInputStream("images/" + this.bitmapName + ".jpg", false);
    }

    public Bitmap loadImage(boolean z) {
        Bitmap bitmap;
        Bitmap bitmap2;
        Bitmap bitmap3 = this.bitmap;
        if (bitmap3 != null) {
            return bitmap3;
        }
        Bitmap bitmap4 = null;
        try {
            if (this.flags == 0) {
                bitmap = BitmapFactory.decodeStream(getBitmapInputStream());
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getBitmapInputStream(), null, options);
                options.inJustDecodeBounds = false;
                if (this.scale <= 0.25f) {
                    options.inSampleSize = 4;
                    bitmap2 = BitmapFactory.decodeStream(getBitmapInputStream(), null, options);
                } else if (this.scale <= 0.5f) {
                    options.inSampleSize = 2;
                    bitmap2 = BitmapFactory.decodeStream(getBitmapInputStream(), null, options);
                } else {
                    bitmap2 = BitmapFactory.decodeStream(getBitmapInputStream());
                }
                Log.e("TextureAtlas", "Scale in: " + this.scale + " scale, density: " + options.inSampleSize);
                bitmap = bitmap2;
            }
            if (!FORCE_ARGB4444) {
                return bitmap;
            }
            bitmap4 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
            bitmap.recycle();
            return bitmap4;
        } catch (Exception e) {
            ActivityWrapper.handleException(e);
            return bitmap4;
        } catch (OutOfMemoryError e2) {
            Log.e("andImage", "TextureAtlas: Out of memory trying to create image: " + this.bitmapName, e2);
            ActivityWrapper.getTextureAtlasCache().evictAll();
            andViewManager.invalidateHierarchy();
            System.gc();
            try {
                Thread.sleep(50L);
            } catch (Exception unused) {
            }
            if (!z) {
                return loadImage(true);
            }
            if (this.flags == 1 && this.scale > 0.25f) {
                Log.e("andImage", "TextureAtlas: Trying again with scale = " + (this.scale / 2.0f));
                setParameters(this.bitmapName, this.flags | 1, this.scale / 2.0f, this.isFromDLC);
                return loadImage(true);
            }
            Log.e("andImage", "TextureAtlas: Unable to free enough memory to load image " + this.bitmapName);
            return bitmap4;
        }
    }

    public void LoadImageFromBitmap(String str, Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void addReference(PackedImage packedImage) {
        List<PackedImage> list = this.references;
        if (!list.contains(list)) {
            this.references.add(packedImage);
        }
    }

    public boolean removeReference(PackedImage packedImage) {
        if (this.references.contains(packedImage)) {
            this.references.remove(packedImage);
            if (this.references.size() != 0) {
                return false;
            }
            dump();
            return true;
        }
        return false;
    }

    public void dump() {
        this.references.clear();
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmap = null;
        }
    }

    public String toString() {
        return this.bitmapName + " " + super.toString();
    }
}
