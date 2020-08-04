package com.menglong.video.study.step1.step5_mediaextractor_mediamuxer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.util.MainUIHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class MediaExtractorAnalysisVideoActivity extends BaseActivity {

    private String SDCARD_PATH = "";
    private Button separateVideoBut, separateAudioBut,synthesisVideoBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_video);

        SDCARD_PATH = getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();

        separateVideoBut = findViewById(R.id.separate_video_but);
        separateAudioBut = findViewById(R.id.separate_audio_but);
        synthesisVideoBut = findViewById(R.id.synthesis_video_but);
    }

    /**
     * 分解出视频
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void muxerMedia() {
        //1. 创建MediaExtractor对象
        MediaExtractor mediaExtractor = new MediaExtractor();
        //2. 定位视频信道
        int videoIndex = -1;
        try {
            mediaExtractor.setDataSource(SDCARD_PATH + "/test.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mimeType = trackFormat.getString(MediaFormat.KEY_MIME);
                // 取出视频的信号
                if (mimeType.startsWith("video/")) {
                    videoIndex = i;
                }
            }

            //3 切换道视频信号的信道
            mediaExtractor.selectTrack(videoIndex);

            //4 获取指定的通道格式
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoIndex);

            // 创建MediaMuxer对象
            MediaMuxer mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output_video.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            // 添加通道
            int trackIndex = mediaMuxer.addTrack(trackFormat);
            // 开始合成文件
            mediaMuxer.start();


            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 500);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            long videoSampleTime;
            //获取每帧的之间的时间
            {
                // 把指定通道内的数据按偏移量读取到byteBuffer中
                mediaExtractor.readSampleData(byteBuffer, 0);
                // 跳过第一帧
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC)
                    mediaExtractor.advance(); // 读取下一帧数据

                mediaExtractor.readSampleData(byteBuffer, 0);
                long firstVideoPTS = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long SecondVideoPTS = mediaExtractor.getSampleTime();

                videoSampleTime = Math.abs(SecondVideoPTS - firstVideoPTS);
                Log.d("fuck", "videoSampleTime is " + videoSampleTime);
            }
            //重新切换此信道，不然上面跳过了3帧,造成前面的帧数模糊
            mediaExtractor.unselectTrack(videoIndex);
            mediaExtractor.selectTrack(videoIndex);
            while (true) {
                //读取帧之间的数据
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                mediaExtractor.advance();
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += videoSampleTime;
                //写入帧的数据
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
            }
            //release
            mediaMuxer.stop();
            mediaExtractor.release();
            mediaMuxer.release();

            MainUIHandler.handler().postWaiting(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MediaExtractorAnalysisVideoActivity.this, "分离成功", Toast.LENGTH_SHORT).show();
                    separateVideoBut.setText("分离出纯视频");
                    separateVideoBut.setEnabled(true);
                }
            });

            Log.e("TAG", "finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分解出音频
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void muxerAudio() {
        MediaExtractor mediaExtractor = new MediaExtractor();
        int audioIndex = -1;
        try {
            mediaExtractor.setDataSource(SDCARD_PATH + "/test.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                    audioIndex = i;
                }
            }
            mediaExtractor.selectTrack(audioIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex);
            MediaMuxer mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output_audios.wav", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeAudioIndex = mediaMuxer.addTrack(trackFormat);
            mediaMuxer.start();
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            long stampTime = 0;
            //获取帧之间的间隔时间
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    mediaExtractor.advance();
                }
                mediaExtractor.readSampleData(byteBuffer, 0);
                long secondTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long thirdTime = mediaExtractor.getSampleTime();
                stampTime = Math.abs(thirdTime - secondTime);
                Log.e("fuck", stampTime + "");
            }

            mediaExtractor.unselectTrack(audioIndex);
            mediaExtractor.selectTrack(audioIndex);
            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                mediaExtractor.advance();

                bufferInfo.size = readSampleSize;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.offset = 0;
                bufferInfo.presentationTimeUs += stampTime;

                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();
            Log.e("fuck", "finish");

            MainUIHandler.handler().postWaiting(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MediaExtractorAnalysisVideoActivity.this, "分离音频成功", Toast.LENGTH_SHORT).show();
                    separateAudioBut.setText("分离出纯音频");
                    separateAudioBut.setEnabled(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 合成视频
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void combineVideo() {
        try {
            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(SDCARD_PATH + "/output_video.mp4");
            MediaFormat videoFormat = null;
            int videoTrackIndex = -1;
            int videoTrackCount = videoExtractor.getTrackCount();
            for (int i = 0; i < videoTrackCount; i++) {
                videoFormat = videoExtractor.getTrackFormat(i);
                String mimeType = videoFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("video/")) {
                    videoTrackIndex = i;
                    break;
                }
            }

            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(SDCARD_PATH + "/output_audios.wav");
            MediaFormat audioFormat = null;
            int audioTrackIndex = -1;
            int audioTrackCount = audioExtractor.getTrackCount();
            for (int i = 0; i < audioTrackCount; i++) {
                audioFormat = audioExtractor.getTrackFormat(i);
                String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i;
                    break;
                }
            }

            videoExtractor.selectTrack(videoTrackIndex);
            audioExtractor.selectTrack(audioTrackIndex);

            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

            MediaMuxer mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            int writeAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
            mediaMuxer.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            long sampleTime = 0;
            {
                videoExtractor.readSampleData(byteBuffer, 0);
                if (videoExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtractor.advance();
                }
                videoExtractor.readSampleData(byteBuffer, 0);
                long secondTime = videoExtractor.getSampleTime();
                videoExtractor.advance();
                long thirdTime = videoExtractor.getSampleTime();
                sampleTime = Math.abs(thirdTime - secondTime);
            }
            videoExtractor.unselectTrack(videoTrackIndex);
            videoExtractor.selectTrack(videoTrackIndex);

            while (true) {
                int readVideoSampleSize = videoExtractor.readSampleData(byteBuffer, 0);
                if (readVideoSampleSize < 0) {
                    break;
                }
                videoBufferInfo.size = readVideoSampleSize;
                videoBufferInfo.presentationTimeUs += sampleTime;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeVideoTrackIndex, byteBuffer, videoBufferInfo);
                videoExtractor.advance();
            }

            while (true) {
                int readAudioSampleSize = audioExtractor.readSampleData(byteBuffer, 0);
                if (readAudioSampleSize < 0) {
                    break;
                }

                audioBufferInfo.size = readAudioSampleSize;
                audioBufferInfo.presentationTimeUs += sampleTime;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = videoExtractor.getSampleFlags();
                mediaMuxer.writeSampleData(writeAudioTrackIndex, byteBuffer, audioBufferInfo);
                audioExtractor.advance();
            }

            mediaMuxer.stop();
            mediaMuxer.release();
            videoExtractor.release();
            audioExtractor.release();

            MainUIHandler.handler().postWaiting(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MediaExtractorAnalysisVideoActivity.this, "合成视频成功", Toast.LENGTH_SHORT).show();
                    synthesisVideoBut.setText("合成视频");
                    synthesisVideoBut.setEnabled(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void separate_video(View view) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                muxerMedia();
            }
        }.start();

        separateVideoBut.setText("正在分离...");
        separateVideoBut.setEnabled(false);
    }

    public void separate_audio(View view) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                muxerAudio();
            }
        }.start();

        separateAudioBut.setText("正在分离...");
        separateAudioBut.setEnabled(false);
    }

    public void synthesis_video(View view) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                combineVideo();
            }
        }.start();

        synthesisVideoBut.setText("正在合成...");
        synthesisVideoBut.setEnabled(false);
    }
}
