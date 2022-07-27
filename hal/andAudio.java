package com.rockstargames.hal;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class andAudio {
    private static List<andAudioSample> samples = new ArrayList();
    public static boolean m_audioMuted = false;

    public static void MuteAllAudio(boolean z) {
        m_audioMuted = z;
        for (int i = 0; i < samples.size(); i++) {
            andAudioSample andaudiosample = samples.get(i);
            if (andaudiosample != null) {
                andaudiosample.Mute(z);
            }
        }
    }

    public static int PlayAudioFile(String str, float f, String str2, boolean z, int i, boolean z2) {
        andAudioSample andaudiosample = new andAudioSample(str, f, str2, z, i, z2);
        if (andaudiosample.GetMediaPlayer() != null) {
            samples.add(andaudiosample);
            return andaudiosample.getId();
        }
        Log.d("andAudio", "newSample GetMediaPlayer failed : " + str);
        return -1;
    }

    public static void StopCategory(String str) {
        for (int i = 0; i < samples.size(); i++) {
            andAudioSample andaudiosample = samples.get(i);
            if (andaudiosample != null && andaudiosample.GetCategory().equalsIgnoreCase(str)) {
                andaudiosample.Stop();
            }
        }
    }

    public static void UpdateSamples() {
        for (int size = samples.size() - 1; size >= 0; size--) {
            andAudioSample andaudiosample = samples.get(size);
            if (andaudiosample != null && andaudiosample.GetMediaPlayer() == null) {
                if (andaudiosample.AutoRemove()) {
                    samples.remove(size);
                } else {
                    samples.set(size, null);
                }
            }
        }
    }

    public static int getSample(int i) {
        for (int i2 = 0; i2 < samples.size(); i2++) {
            if (samples.get(i2) != null && samples.get(i2).getId() == i) {
                return i2;
            }
        }
        return -1;
    }

    public static boolean IsPlaying(int i) {
        andAudioSample andaudiosample;
        if (getSample(i) == -1 || (andaudiosample = samples.get(getSample(i))) == null) {
            return false;
        }
        return andaudiosample.IsPlaying();
    }

    public static void ReleaseHandle(int i) {
        int sample = getSample(i);
        if (sample != -1) {
            andAudioSample andaudiosample = samples.get(sample);
            if (andaudiosample != null) {
                andaudiosample.Stop();
            }
            samples.remove(sample);
        }
    }

    public static void Stop(int i) {
        andAudioSample andaudiosample;
        if (getSample(i) == -1 || (andaudiosample = samples.get(getSample(i))) == null) {
            return;
        }
        andaudiosample.Stop();
    }
}
