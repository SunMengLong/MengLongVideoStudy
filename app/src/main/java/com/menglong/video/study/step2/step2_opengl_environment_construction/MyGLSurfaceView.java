package com.menglong.video.study.step2.step2_opengl_environment_construction;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by 10007358 on 2020/8/7.
 */

public class MyGLSurfaceView extends GLSurfaceView{

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // 指定 OpenGL ES 的版本 2.0
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // 设置在SurfaceView上绘制的渲染器
        setRenderer(mRenderer);
    }
}
