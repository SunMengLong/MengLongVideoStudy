package com.menglong.video.study.step2;

import android.os.Bundle;
import android.view.View;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.util.Util;
import com.menglong.video.study.step2.step1_opengl_introduce.OpenGLintroduceActivity;
import com.menglong.video.study.step2.step2_opengl_environment_construction.OpenGLES20Activity;

/**
 * Created by 10007358 on 2020/8/7.
 */

public class Step2Activity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
    }

    public void study_step2_one(View view) {
        Util.intentActivity(this,OpenGLintroduceActivity.class);
    }

    public void study_step2_two(View view) {
        Util.intentActivity(this,OpenGLES20Activity.class);
    }

    public void study_step2_three(View view) {
    }

    public void study_step2_four(View view) {
    }

    public void study_step2_five(View view) {
    }

    public void study_step2_six(View view) {
    }

    public void study_step2_seven(View view) {
    }

    public void study_step2_eight(View view) {
    }

    public void study_step2_nine(View view) {
    }

    public void study_step2_ten(View view) {
    }
}
