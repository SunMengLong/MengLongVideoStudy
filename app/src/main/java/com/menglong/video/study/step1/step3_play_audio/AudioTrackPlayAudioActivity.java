package com.menglong.video.study.step1.step3_play_audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.constant.VideoStudyConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class AudioTrackPlayAudioActivity extends BaseActivity {

    private byte[] audioData;
    private AudioTrack audioTrack;
    private FileInputStream fileInputStream;
    private boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);
    }

    /**
     * 开始播放
     *
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void start_play(View v) {
        playAudioStatic();
    }

    /**
     * 停止播放
     *
     * @param v
     */
    public void stop_play(View v) {
        releaseAudioTrack();
    }

    /**
     * 播放，使用stream模式
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playInModeStream() {
        releaseAudioTrack();
        isPlaying = true;
        /*
        * SAMPLE_RATE_INHZ 对应pcm音频的采样率
        * channelConfig 对应pcm音频的声道
        * AUDIO_FORMAT 对应pcm音频的格式
        * */
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(VideoStudyConstant.SAMPLE_RATE_INHZ, channelConfig, VideoStudyConstant.AUDIO_FORMAT);
        audioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                new AudioFormat.Builder().setSampleRate(VideoStudyConstant.SAMPLE_RATE_INHZ)
                        .setEncoding(VideoStudyConstant.AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        try {
            fileInputStream = new FileInputStream(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] tempBuffer = new byte[minBufferSize];
                        while (fileInputStream.available() > 0 && isPlaying) {
                            int readCount = fileInputStream.read(tempBuffer);
                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                    readCount == AudioTrack.ERROR_BAD_VALUE) {
                                continue;
                            }
                            if (readCount != 0 && readCount != -1) {
                                audioTrack.write(tempBuffer, 0, readCount);
                            }
                        }

                        releaseAudioTrack();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过static方式播放音频
     */
    private void playAudioStatic() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                InputStream in = getResources().openRawResource(R.raw.test);
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream(
                            264848);
                    try {
                        for (int b; (b = in.read()) != -1; ) {
                            out.write(b);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(VideoStudyConstant.TAG, "Got the data");
                    audioData = out.toByteArray();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        audioData.length, AudioTrack.MODE_STATIC);
                audioTrack.write(audioData, 0, audioData.length);
                audioTrack.play();
            }
        }.execute();
    }

    private void releaseAudioTrack() {
        if (this.audioTrack != null) {
            isPlaying = false;
//            audioTrack.stop();
            audioTrack.release();
        }
    }

    /**
     * 播放音频 - Stream模式
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void start_play_stream(View view) {
        playInModeStream();
    }
}
