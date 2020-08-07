package com.menglong.video.study.step2.step2_opengl_environment_construction;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 10007358 on 2020/8/7.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer{

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 在view的openGl环境被创建的时候调用
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // view每次重绘的时候都会调用
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 当view发生几何图形变化时，例如当旋转手机屏幕时
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
