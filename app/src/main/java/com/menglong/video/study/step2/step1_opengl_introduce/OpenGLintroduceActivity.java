package com.menglong.video.study.step2.step1_opengl_introduce;

import android.os.Bundle;
import android.widget.TextView;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;

/**
 * Created by 10007358 on 2020/8/7.
 * Open GL 介绍
 */

public class OpenGLintroduceActivity extends BaseActivity {

    private TextView openglIntroduceTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl_introduce);

        openglIntroduceTv = findViewById(R.id.opengl_introduce_tv);
        openglIntroduceTv.setText("OpenGL是一个跨平台的处理2D/3D图形的标准软件接口。\n\n" +
                "OpenGL ES 是适用于嵌入式设备的OpenGL规范。" +
                "\n\nAndroid 支持OpenGL ES API版本的详细状态是：" +
                "\n* OpenGL ES 1.0 和 1.1 能够被Android 1.0及以上版本支持" +
                "\n* OpenGL ES 2.0 能够被Android 2.2及更高版本支持"+
                "\n* OpenGL ES 3.0 能够被Android 4.3及更高版本支持"+
                "\n* OpenGL ES 3.1 能够被Android 5.0及以上版本支持" +
                "\n\nAndroid接入Open GL前 必须要了解两个相关类" +
                "\nGLSerfaceView 和 GLSerfaceView.Rerderer" +
                "\nGLSerfaceView ：" +
                "\n这是一个视图类，可以使用OpenGL API来绘制和操作图形对象，这一点在功能上很类似于SurfaceView。可以通过创建一个SurfaceView的实例并添加渲染器来使用这个类。但是如果想要捕捉触摸屏的事件，则应该扩展GLSurfaceView以实现触摸监听器。" +
                "\nGLSurfaceView.Renderer ：" +
                "\n此接口定义了在GLSurfaceView中绘制图形所需的方法。必须将此接口的实现作为单独的类提供，并使用GLSurfaceView.setRenderer()将其附加到GLSurfaceView实例。"
        );
    }
}
