package com.menglong.video.study.step1.step4_camera_collection_video;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.constant.VideoStudyConstant;

import java.io.IOException;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class CameraPreViewSurfaceViewActivity extends BaseActivity implements SurfaceHolder.Callback {

    private SurfaceView cameraPreviewSurfaceview;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_camera_surfaceview);
        cameraPreviewSurfaceview = findViewById(R.id.camera_preview_surfaceview);
        cameraPreviewSurfaceview.getHolder().addCallback(this);

        // 打开摄像头并将展示方向旋转90度
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                Log.i(VideoStudyConstant.TAG, "onPreviewFrame: 预览回调：222");
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.release();
    }
}
