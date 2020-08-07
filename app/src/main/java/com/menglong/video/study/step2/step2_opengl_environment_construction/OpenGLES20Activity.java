package com.menglong.video.study.step2.step2_opengl_environment_construction;

import android.os.Bundle;

import com.menglong.video.study.base.activity.BaseActivity;

/**
 * Created by 10007358 on 2020/8/7.
 */

public class OpenGLES20Activity extends BaseActivity{

    private MyGLSurfaceView myGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(myGLSurfaceView);
    }
}
