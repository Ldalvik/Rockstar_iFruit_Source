package com.rockstargames.hal;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.p000v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import com.rockstargames.ifruit.C0532R;

/* loaded from: classes.dex */
public class andVideo extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, View.OnTouchListener {
    private static andVideo m_video;
    private static int m_videoPosition;
    private String m_file;
    private RelativeLayout m_relativeLayoutView;
    private VideoView m_videoView;

    public native void VideoFinishedCB();

    public static void PlayVideoFile(String str) {
        andVideo andvideo = new andVideo(str);
        m_video = andvideo;
        andvideo.Play();
    }

    public static boolean IsVideoPlaying() {
        andVideo andvideo = m_video;
        if (andvideo != null) {
            return andvideo.IsPlaying();
        }
        return false;
    }

    public static void Suspend() {
        andVideo andvideo = m_video;
        if (andvideo != null) {
            andvideo.Pause();
            m_videoPosition = m_video.GetPosition();
        }
    }

    public static void Resume() {
        andVideo andvideo = m_video;
        if (andvideo != null) {
            andvideo.SetPosition(m_videoPosition);
            m_video.Play();
        }
    }

    public static void StopVideo() {
        andVideo andvideo = m_video;
        if (andvideo != null) {
            andvideo.Stop();
        }
    }

    public andVideo(String str) {
        Uri uri;
        this.m_videoView = null;
        this.m_relativeLayoutView = null;
        this.m_file = str;
        try {
            this.m_videoView = new VideoView(ActivityWrapper.getActivity().getApplicationContext());
            if (this.m_file.equalsIgnoreCase("chop")) {
                uri = Uri.parse("android.resource://" + ActivityWrapper.getActivity().getPackageName() + "/" + C0532R.raw.chop);
            } else if (this.m_file.equalsIgnoreCase("customs")) {
                uri = Uri.parse("android.resource://" + ActivityWrapper.getActivity().getPackageName() + "/" + C0532R.raw.customs);
            } else {
                uri = null;
            }
            this.m_videoView.setVideoURI(uri);
            this.m_videoView.setOnCompletionListener(this);
            this.m_videoView.setOnErrorListener(this);
            this.m_videoView.requestFocus();
            this.m_videoView.setOnTouchListener(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
            RelativeLayout relativeLayout = new RelativeLayout(ActivityWrapper.getActivity());
            this.m_relativeLayoutView = relativeLayout;
            relativeLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            this.m_relativeLayoutView.setLayoutParams(layoutParams);
            layoutParams.addRule(14, -1);
            layoutParams.addRule(15, -1);
            this.m_videoView.setLayoutParams(layoutParams);
            this.m_relativeLayoutView.addView(this.m_videoView, layoutParams);
            ActivityWrapper.getLayout().addView(this.m_relativeLayoutView);
        } catch (Exception e) {
            Log.e("andVideo", "Unable to get video file: " + str, e);
            if (this.m_videoView == null) {
                return;
            }
            this.m_videoView = null;
        }
    }

    public boolean IsPlaying() {
        try {
            if (this.m_videoView == null) {
                return false;
            }
            return this.m_videoView.isPlaying();
        } catch (Exception e) {
            Log.e("andVideo", "Unable to query playing state for: " + this.m_file, e);
            return false;
        }
    }

    public void Play() {
        VideoView videoView = this.m_videoView;
        if (videoView != null) {
            videoView.start();
        }
    }

    public void Pause() {
        VideoView videoView = this.m_videoView;
        if (videoView != null) {
            videoView.pause();
        }
    }

    public void Stop() {
        if (this.m_videoView == null || this.m_relativeLayoutView == null) {
            return;
        }
        ActivityWrapper.getLayout().removeView(this.m_relativeLayoutView);
        this.m_videoView.stopPlayback();
        this.m_videoView = null;
        this.m_relativeLayoutView = null;
    }

    public int GetPosition() {
        VideoView videoView = this.m_videoView;
        if (videoView != null) {
            return videoView.getCurrentPosition();
        }
        return 0;
    }

    public void SetPosition(int i) {
        VideoView videoView = this.m_videoView;
        if (videoView != null) {
            videoView.seekTo(i);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        VideoComplete();
        return true;
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mediaPlayer) {
        VideoComplete();
    }

    private void VideoComplete() {
        if (this.m_videoView == null || this.m_relativeLayoutView == null) {
            return;
        }
        Stop();
        VideoFinishedCB();
    }

    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        String str;
        String str2;
        String str3 = "ERROR: Video '" + this.m_file + "': ";
        if (i == 100) {
            str = str3 + "Server Died, ";
        } else {
            str = str3 + "Unknown error, ";
        }
        if (i2 == -1010) {
            str2 = str + "Unsupported format!";
        } else if (i2 == -1007) {
            str2 = str + "Malformed video!";
        } else if (i2 == -1004) {
            str2 = str + "IO Error!";
        } else if (i2 == -110) {
            str2 = str + "Timed out!";
        } else {
            str2 = str + "Unknown error!";
        }
        Log.e("andVideo", str2);
        return true;
    }
}
