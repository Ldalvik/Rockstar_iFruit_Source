package com.rockstargames.hal;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class andAudioSample extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static int idGen;

    /* renamed from: id */
    private int f34id;
    private boolean m_autoRemove;
    private String m_category;
    private String m_file;
    private int m_loopCount;
    private MediaPlayer m_mediaPlayer;
    private float m_volume;

    private AssetFileDescriptor getAssetFileDescriptorForFile(String str, boolean z) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        if (z) {
            return andFile.getAssetFileDescriptorForDLC("audio/" + str + ".wav");
        }
        try {
            assetFileDescriptor = andFile.getAssetFileDescriptor("audio/" + str + ".ogg", false);
        } catch (FileNotFoundException unused) {
            assetFileDescriptor = andFile.getAssetFileDescriptor("audio/" + str + ".wav", false);
        }
        if (assetFileDescriptor != null) {
            return assetFileDescriptor;
        }
        return andFile.getAssetFileDescriptor("audio/" + str + ".wav", false);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x006b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public andAudioSample(java.lang.String r8, float r9, java.lang.String r10, boolean r11, int r12, boolean r13) {
        /*
            r7 = this;
            r7.<init>()
            r0 = 0
            r7.m_mediaPlayer = r0
            r7.m_category = r0
            r1 = 1
            r7.m_autoRemove = r1
            r2 = 1065353216(0x3f800000, float:1.0)
            r7.m_volume = r2
            r2 = 0
            r7.m_loopCount = r2
            r2 = -1
            r7.f34id = r2
            r7.m_category = r10
            r7.m_autoRemove = r11
            r7.m_file = r8
            r7.m_volume = r9
            r7.m_loopCount = r12
            int r9 = com.rockstargames.hal.andAudioSample.idGen
            r7.f34id = r9
            int r9 = r9 + r1
            com.rockstargames.hal.andAudioSample.idGen = r9
            android.content.res.AssetFileDescriptor r8 = r7.getAssetFileDescriptorForFile(r8, r13)     // Catch: java.lang.Exception -> L5f
            if (r8 == 0) goto L69
            android.media.MediaPlayer r9 = new android.media.MediaPlayer     // Catch: java.lang.Exception -> L5d
            r9.<init>()     // Catch: java.lang.Exception -> L5d
            r7.m_mediaPlayer = r9     // Catch: java.lang.Exception -> L5d
            r10 = 3
            r9.setAudioStreamType(r10)     // Catch: java.lang.Exception -> L5d
            android.media.MediaPlayer r9 = r7.m_mediaPlayer     // Catch: java.lang.Exception -> L5d
            r9.setOnCompletionListener(r7)     // Catch: java.lang.Exception -> L5d
            android.media.MediaPlayer r9 = r7.m_mediaPlayer     // Catch: java.lang.Exception -> L5d
            r9.setOnErrorListener(r7)     // Catch: java.lang.Exception -> L5d
            android.media.MediaPlayer r9 = r7.m_mediaPlayer     // Catch: java.lang.Exception -> L5d
            r9.setOnPreparedListener(r7)     // Catch: java.lang.Exception -> L5d
            android.media.MediaPlayer r1 = r7.m_mediaPlayer     // Catch: java.lang.Exception -> L5d
            java.io.FileDescriptor r2 = r8.getFileDescriptor()     // Catch: java.lang.Exception -> L5d
            long r3 = r8.getStartOffset()     // Catch: java.lang.Exception -> L5d
            long r5 = r8.getLength()     // Catch: java.lang.Exception -> L5d
            r1.setDataSource(r2, r3, r5)     // Catch: java.lang.Exception -> L5d
            android.media.MediaPlayer r9 = r7.m_mediaPlayer     // Catch: java.lang.Exception -> L5d
            r9.prepareAsync()     // Catch: java.lang.Exception -> L5d
            goto L69
        L5d:
            goto L60
        L5f:
            r8 = r0
        L60:
            android.media.MediaPlayer r9 = r7.m_mediaPlayer
            if (r9 == 0) goto L69
            r9.release()
            r7.m_mediaPlayer = r0
        L69:
            if (r8 == 0) goto L6e
            r8.close()     // Catch: java.lang.Exception -> L6e
        L6e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rockstargames.hal.andAudioSample.<init>(java.lang.String, float, java.lang.String, boolean, int, boolean):void");
    }

    public int getId() {
        return this.f34id;
    }

    public void Mute(boolean z) {
        if (this.m_mediaPlayer == null || !IsPlaying()) {
            return;
        }
        if (z) {
            this.m_mediaPlayer.setVolume(0.0f, 0.0f);
            return;
        }
        MediaPlayer mediaPlayer = this.m_mediaPlayer;
        float f = this.m_volume;
        mediaPlayer.setVolume(f, f);
    }

    public String GetCategory() {
        return this.m_category;
    }

    public boolean AutoRemove() {
        return this.m_autoRemove;
    }

    public void Play() {
        MediaPlayer mediaPlayer = this.m_mediaPlayer;
        if (mediaPlayer != null) {
            if (this.m_loopCount == -1) {
                mediaPlayer.setLooping(true);
            }
            if (andAudio.m_audioMuted) {
                this.m_mediaPlayer.setVolume(0.0f, 0.0f);
            } else {
                MediaPlayer mediaPlayer2 = this.m_mediaPlayer;
                float f = this.m_volume;
                mediaPlayer2.setVolume(f, f);
            }
            this.m_mediaPlayer.start();
        }
    }

    public void Stop() {
        if (IsPlaying()) {
            this.m_mediaPlayer.stop();
        }
        MediaPlayer mediaPlayer = this.m_mediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.m_mediaPlayer = null;
        }
    }

    public boolean IsPlaying() {
        try {
            if (this.m_mediaPlayer == null) {
                return false;
            }
            return this.m_mediaPlayer.isPlaying();
        } catch (Exception unused) {
            return false;
        }
    }

    public MediaPlayer GetMediaPlayer() {
        return this.m_mediaPlayer;
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            Play();
        }
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        String str = "ERROR: Sample '" + this.m_file + "': ";
        if (i == 100) {
            String str2 = str + "Server Died, ";
            return true;
        }
        String str3 = str + "Unknown error, ";
        return true;
    }
}
