package com.menglong.video.study.step1;

import android.os.Bundle;
import android.view.View;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.util.Util;
import com.menglong.video.study.step1.step2_collection_audio.AudioRecordCollectionAudio;
import com.menglong.video.study.step1.step3_play_audio.AudioTrackPlayAudioActivity;
import com.menglong.video.study.step1.step1_draw_image.ThreeWaysDrawImageActivity;
import com.menglong.video.study.step1.step4_camera_collection_video.CollectionVideoActivity;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class Step1Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
    }

    public void study_step1_one(View view) {
        Util.intentActivity(this, ThreeWaysDrawImageActivity.class);
    }

    public void study_step1_two(View view) {
        Util.intentActivity(this, AudioRecordCollectionAudio.class);
    }

    public void study_step1_three(View view) {
        Util.intentActivity(this, AudioTrackPlayAudioActivity.class);
    }

    public void study_step1_four(View view) {
        Util.intentActivity(this, CollectionVideoActivity.class);
    }
}
