package com.menglong.video.study.step1.step5_mediaextractor_mediamuxer;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.util.MainUIHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class MediaExtractorAnalysisVideoActivity extends BaseActivity {

    private String SDCARD_PATH = "";
    private Button separateVideoBut, separateAudioBut, synthesisVideoBut;
    private MediaMuxer mediaMuxer;
    private MediaExtractor mediaExtractor;

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
        int videoIndex = -1;
        try {
            mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource(SDCARD_PATH + "/test.mp4"); //数据源
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String string = trackFormat.getString(MediaFormat.KEY_MIME);
                if (string.startsWith("video/")) {
                    videoIndex = i;//得到具体轨道
                    break;
                }
            }
            mediaExtractor.selectTrack(videoIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoIndex);
            mediaMuxer = new MediaMuxer(SDCARD_PATH + "/output_video.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int i = mediaMuxer.addTrack(trackFormat);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1000);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();

            long videoSampleTime = 70000;
            //获取每一帧的时间
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    mediaExtractor.advance();
                }
                mediaExtractor.readSampleData(byteBuffer, 0);
                mediaExtractor.advance();
                long sampleTime = mediaExtractor.getSampleTime();

                mediaExtractor.readSampleData(byteBuffer, 0);
                mediaExtractor.advance();
                long sampleTime1 = mediaExtractor.getSampleTime();
                videoSampleTime = Math.abs(sampleTime - sampleTime1);
            }
            //重新选择 否则会丢掉上面的三帧
            mediaExtractor.unselectTrack(videoIndex);
            mediaExtractor.selectTrack(videoIndex);

            while (true) {
                int data = mediaExtractor.readSampleData(byteBuffer, 0);
                if (data < 0) {
                    break;
                }

                bufferInfo.size = data;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += videoSampleTime;

                mediaMuxer.writeSampleData(i, byteBuffer, bufferInfo);

                mediaExtractor.advance();
            }
            MainUIHandler.handler().postWaiting(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MediaExtractorAnalysisVideoActivity.this, "ok", Toast.LENGTH_SHORT).show();
                    separateVideoBut.setText("分离出纯视频");
                    separateVideoBut.setEnabled(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaExtractor.release();
            mediaMuxer.stop();
            mediaMuxer.release();
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
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                super.run();
//                combineVideo();
                combineTwoVideos(SDCARD_PATH + "/k.mp4",0L,SDCARD_PATH + "/test.mp4",new File(SDCARD_PATH + "/output.mp4"));


            }
        }.start();

        synthesisVideoBut.setText("正在合成...");
        synthesisVideoBut.setEnabled(false);
    }



    /**
     * 合成视频1的音频和视频2的图像
     *
     * @param audioVideoPath  提供音频的视频
     * @param audioStartTime  音频的开始时间
     * @param frameVideoPath  提供图像的视频
     * @param combinedVideoOutFile  合成后的文件
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void combineTwoVideos(String audioVideoPath,
                                        long audioStartTime,
                                        String frameVideoPath,
                                        File combinedVideoOutFile) {
        MediaExtractor audioVideoExtractor = new MediaExtractor();
        int mainAudioExtractorTrackIndex = -1; //提供音频的视频的音频轨（有点拗口）
        int mainAudioMuxerTrackIndex = -1; //合成后的视频的音频轨
        int mainAudioMaxInputSize = 0; //能获取的音频的最大值

        MediaExtractor frameVideoExtractor = new MediaExtractor();
        int frameExtractorTrackIndex = -1; //视频轨
        int frameMuxerTrackIndex = -1; //合成后的视频的视频轨
        int frameMaxInputSize = 0; //能获取的视频的最大值
        int frameRate = 0; //视频的帧率
        long frameDuration = 0;

        MediaMuxer muxer = null; //用于合成音频与视频

        try {
            muxer = new MediaMuxer(combinedVideoOutFile.getPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            audioVideoExtractor.setDataSource(audioVideoPath); //设置视频源
            //音轨信息
            int audioTrackCount = audioVideoExtractor.getTrackCount(); //获取数据源的轨道数
            //在此循环轨道数，目的是找到我们想要的音频轨
            for (int i = 0; i < audioTrackCount; i++) {
                MediaFormat format = audioVideoExtractor.getTrackFormat(i); //得到指定索引的记录格式
                String mimeType = format.getString(MediaFormat.KEY_MIME); //主要描述mime类型的媒体格式
                if (mimeType.startsWith("audio/")) { //找到音轨
                    mainAudioExtractorTrackIndex = i;
                    mainAudioMuxerTrackIndex = muxer.addTrack(format); //将音轨添加到MediaMuxer，并返回新的轨道
                    mainAudioMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE); //得到能获取的有关音频的最大值
//                    mainAudioDuration = format.getLong(MediaFormat.KEY_DURATION);
                }
            }

            //图像信息
            frameVideoExtractor.setDataSource(frameVideoPath); //设置视频源
            int trackCount = frameVideoExtractor.getTrackCount(); //获取数据源的轨道数
            //在此循环轨道数，目的是找到我们想要的视频轨
            for (int i = 0; i < trackCount; i++) {
                MediaFormat format = frameVideoExtractor.getTrackFormat(i); //得到指定索引的媒体格式
                String mimeType = format.getString(MediaFormat.KEY_MIME); //主要描述mime类型的媒体格式
                if (mimeType.startsWith("video/")) { //找到视频轨
                    frameExtractorTrackIndex = i;
                    frameMuxerTrackIndex = muxer.addTrack(format); //将视频轨添加到MediaMuxer，并返回新的轨道
                    frameMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE); //得到能获取的有关视频的最大值
                    frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE); //获取视频的帧率
                    frameDuration = format.getLong(MediaFormat.KEY_DURATION); //获取视频时长
                }
            }

            muxer.start(); //开始合成

            audioVideoExtractor.selectTrack(mainAudioExtractorTrackIndex); //将提供音频的视频选择到音轨上
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer audioByteBuffer = ByteBuffer.allocate(mainAudioMaxInputSize);
            while (true) {
                int readSampleSize = audioVideoExtractor.readSampleData(audioByteBuffer, 0); //检索当前编码的样本并将其存储在字节缓冲区中
                if (readSampleSize < 0) { //如果没有可获取的样本则退出循环
                    audioVideoExtractor.unselectTrack(mainAudioExtractorTrackIndex);
                    break;
                }

                long sampleTime = audioVideoExtractor.getSampleTime(); //获取当前展示样本的时间（单位毫秒）

                if (sampleTime < audioStartTime) { //如果样本时间小于我们想要的开始时间就快进
                    audioVideoExtractor.advance(); //推进到下一个样本，类似快进
                    continue;
                }

                if (sampleTime > audioStartTime + frameDuration) { //如果样本时间大于开始时间+视频时长，就退出循环
                    break;
                }
                //设置样本编码信息
                audioBufferInfo.size = readSampleSize;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioVideoExtractor.getSampleFlags();
                audioBufferInfo.presentationTimeUs = sampleTime - audioStartTime;

                muxer.writeSampleData(mainAudioMuxerTrackIndex, audioByteBuffer, audioBufferInfo); //将样本写入
                audioVideoExtractor.advance(); //推进到下一个样本，类似快进
            }

            frameVideoExtractor.selectTrack(frameExtractorTrackIndex); //将提供视频图像的视频选择到视频轨上
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer videoByteBuffer = ByteBuffer.allocate(frameMaxInputSize);
            while (true) {
                int readSampleSize = frameVideoExtractor.readSampleData(videoByteBuffer, 0); //检索当前编码的样本并将其存储在字节缓冲区中
                if (readSampleSize < 0) { //如果没有可获取的样本则退出循环
                    frameVideoExtractor.unselectTrack(frameExtractorTrackIndex);
                    break;
                }
                //设置样本编码信息
                videoBufferInfo.size = readSampleSize;
                videoBufferInfo.offset = 0;
                videoBufferInfo.flags = frameVideoExtractor.getSampleFlags();
                videoBufferInfo.presentationTimeUs += 1000 * 1000 / frameRate;

                muxer.writeSampleData(frameMuxerTrackIndex, videoByteBuffer, videoBufferInfo); //将样本写入
                frameVideoExtractor.advance(); //推进到下一个样本，类似快进
            }
        } catch (IOException e) {
            Log.e("smllllll", "combineTwoVideos: ", e);
        } finally {
            MainUIHandler.handler().postWaiting(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MediaExtractorAnalysisVideoActivity.this, "合成视频成功", Toast.LENGTH_SHORT).show();
                    synthesisVideoBut.setText("合成视频");
                    synthesisVideoBut.setEnabled(true);
                }
            });
            //释放资源
            audioVideoExtractor.release();
            frameVideoExtractor.release();
            if (muxer != null) {
                muxer.release();
            }
        }
    }
}
