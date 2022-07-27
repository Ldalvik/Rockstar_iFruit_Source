package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.p000v4.view.ViewCompat;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/* loaded from: classes.dex */
public class andImage {
    private String name;
    private PackedImage packedImage;
    private static HashMap<String, TextureAtlas> hashMap = new HashMap<>();
    private static Bitmap caulk = null;

    /* loaded from: classes.dex */
    public interface andImageReferencer {
        boolean hasReferenceTo(PackedImage packedImage);

        void removeReferencesTo(PackedImage packedImage);
    }

    public static Bitmap getCaulk() {
        if (caulk == null) {
            int[] iArr = new int[16384];
            for (int i = 0; i < 16384; i++) {
                boolean z = true;
                boolean z2 = i % 2 == 0;
                if ((i / 128) % 2 != 0) {
                    z = false;
                }
                iArr[i] = z2 == z ? ViewCompat.MEASURED_STATE_MASK : -65281;
            }
            caulk = Bitmap.createBitmap(iArr, 128, 128, Bitmap.Config.RGB_565);
        }
        return caulk;
    }

    private static TextureAtlas getTextureAtlas(String str, int i, float f, boolean z) {
        TextureAtlas textureAtlas = hashMap.get(str);
        if (textureAtlas == null) {
            textureAtlas = new TextureAtlas();
            hashMap.put(str, textureAtlas);
        }
        textureAtlas.setParameters(str, i, f, z);
        textureAtlas.getBitmap();
        return textureAtlas;
    }

    private static TextureAtlas LoadImageFromBitmap(String str, Bitmap bitmap) {
        TextureAtlas textureAtlas = hashMap.get(str);
        if (textureAtlas == null) {
            textureAtlas = new TextureAtlas();
            hashMap.put(str, textureAtlas);
        }
        textureAtlas.LoadImageFromBitmap(str, bitmap);
        return textureAtlas;
    }

    private static void unloadPackedImage(PackedImage packedImage) {
        TextureAtlas unload = packedImage.unload();
        if (unload != null) {
            hashMap.remove(unload);
        }
    }

    public static void dumpAllImages() {
        for (TextureAtlas textureAtlas : hashMap.values()) {
            textureAtlas.dump();
        }
        hashMap.clear();
    }

    public static andImage createImage(String str) {
        return new andImage(str);
    }

    public static andImage unpackImage(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, boolean z) {
        PackedImage packedImage;
        try {
            PackedImageAttributes packedImageAttributes = new PackedImageAttributes();
            packedImageAttributes.f32x = i;
            packedImageAttributes.f33y = i2;
            packedImageAttributes.packedWidth = i3;
            packedImageAttributes.packedHeight = i4;
            packedImageAttributes.width = i7;
            packedImageAttributes.height = i8;
            packedImageAttributes.textureAtlas = getTextureAtlas(str2, i9, Math.max(ActivityWrapper.getWindowWidth() / i7, ActivityWrapper.getWindowHeight() / i8), z);
            packedImageAttributes.offsetX = i5;
            packedImageAttributes.offsetY = i6;
            packedImage = new PackedImage(packedImageAttributes);
        } catch (Exception e) {
            Log.e("andImage", "Exception thrown when creating image:", e);
            packedImage = null;
        }
        return new andImage(str, packedImage);
    }

    public static andImage loadImageFromBytes(String str, byte[] bArr, int i) {
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, i);
        if (decodeByteArray != null) {
            PackedImageAttributes packedImageAttributes = new PackedImageAttributes();
            packedImageAttributes.f32x = 0;
            packedImageAttributes.f33y = 0;
            packedImageAttributes.packedWidth = decodeByteArray.getWidth();
            packedImageAttributes.packedHeight = decodeByteArray.getHeight();
            packedImageAttributes.width = decodeByteArray.getWidth();
            packedImageAttributes.height = decodeByteArray.getHeight();
            packedImageAttributes.textureAtlas = LoadImageFromBitmap(str, decodeByteArray);
            packedImageAttributes.offsetX = 0;
            packedImageAttributes.offsetY = 0;
            return new andImage(str, new PackedImage(packedImageAttributes));
        }
        return null;
    }

    public andImage(String str) {
        this.name = str;
    }

    public andImage(String str, PackedImage packedImage) {
        this.name = str;
        this.packedImage = packedImage;
    }

    public int getWidth() {
        PackedImage packedImage = this.packedImage;
        if (packedImage != null) {
            return packedImage.getWidth();
        }
        ActivityWrapper.handleException(new NullPointerException());
        return -1;
    }

    public int getHeight() {
        PackedImage packedImage = this.packedImage;
        if (packedImage != null) {
            return packedImage.getHeight();
        }
        ActivityWrapper.handleException(new NullPointerException());
        return -1;
    }

    public PackedImage getPackedImage() {
        return this.packedImage;
    }

    public String getName() {
        return this.name;
    }

    public void unload() {
        PackedImage packedImage = this.packedImage;
        if (packedImage == null) {
            Log.e("andImage", "Trying to recycle null bitmap!");
            return;
        }
        unloadPackedImage(packedImage);
        this.packedImage = null;
    }

    public byte[] getByteData() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.packedImage.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void turnFilteringOff() {
        PackedImage packedImage = this.packedImage;
        if (packedImage != null) {
            packedImage.turnOffFiltering();
        } else {
            Log.e("andImage", "Trying to make NULL packed image unfiltered");
        }
    }
}
