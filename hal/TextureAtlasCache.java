package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.support.p000v4.util.LruCache;

/* loaded from: classes.dex */
public class TextureAtlasCache extends LruCache<TextureAtlas, Bitmap> {
    public void entryRemoved(boolean z, TextureAtlas textureAtlas, Bitmap bitmap, Bitmap bitmap2) {
    }

    public TextureAtlasCache(int i) {
        super(i);
    }

    public Bitmap create(TextureAtlas textureAtlas) {
        return textureAtlas.loadImage();
    }

    public int sizeOf(TextureAtlas textureAtlas, Bitmap bitmap) {
        return bitmap.getByteCount();
    }
}
