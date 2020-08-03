package com.menglong.video.study.step1.step2_collection_audio;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.constant.VideoStudyConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by aml on 2020/8/3.
 */

public class AudioRecordCollectionAudio extends BaseActivity {

    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
    private AudioRecord audioRecord = null;  // 声明 AudioRecord 对象
    private boolean isRecording = true;
    private FileOutputStream os;
    private byte[] data;
    private Button startRecordBut;
    private TextView saveFileLocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_audio);
        startRecordBut = findViewById(R.id.start_record_but);
        saveFileLocalPath = findViewById(R.id.save_file_local_path);
    }

    /**
     * 开始录音 - 通过AndioRecord方式
     */
    public void collection_audio(View view) {

        startRecordBut.setEnabled(false);

        // step1 构造一个AudioRecord对象，其中需要的最小的录音缓存buufer可以通过getMinBufferSize()得到，如果buffer容量过小，会导致AudioRecord对象构建失败
        // 获取创建AudioRecord对象需要的最小缓冲区
        recordBufSize = AudioRecord.getMinBufferSize(VideoStudyConstant.SAMPLE_RATE_INHZ, VideoStudyConstant.CHANNEL_CONFIG, VideoStudyConstant.AUDIO_FORMAT);
        // 创建AudioRecord对象
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, VideoStudyConstant.SAMPLE_RATE_INHZ, VideoStudyConstant.CHANNEL_CONFIG, VideoStudyConstant.AUDIO_FORMAT, recordBufSize);

        // step2 创建一个buffer
        data = new byte[recordBufSize];

        // step3 开始录音
        audioRecord.startRecording();
        isRecording = true;


        startRecordBut.setText("正在录音...");

        // step4 创建一个输入流，一边将AudioRecord中录音数据读取到创建好的buffer，一边将buffer中的数据写入到文件当中
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
                    if (!file.mkdir()) {
                        Log.e(VideoStudyConstant.TAG, "Directory not created");
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                    os = new FileOutputStream(file);
                    if (null != os) {
                        while (isRecording) {
                            int read = audioRecord.read(data, 0, recordBufSize);
                            // 如果读取音频没有错误，就将写入到文件
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                try {
                                    os.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 结束录音
     *
     * @param view
     */
    public void end_audio(View view) {
        // 停止录音
        isRecording = false;
        // 释放资源
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        saveFileLocalPath.setVisibility(View.VISIBLE);

        startRecordBut.setEnabled(true);
        startRecordBut.setText("开始录音");
    }

    /**
     * 将pcm源文件转换为可播放的wav文件
     *
     * @param view
     */
    public void pcmToWav(View view) {
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(VideoStudyConstant.SAMPLE_RATE_INHZ, VideoStudyConstant.CHANNEL_CONFIG, VideoStudyConstant.AUDIO_FORMAT);
        File pcmFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav");
        if (!wavFile.mkdirs()) {
            Log.e(VideoStudyConstant.TAG, "wavFile Directory not created");
        }
        if (wavFile.exists()) {
            wavFile.delete();
        }
        pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());
    }
}
