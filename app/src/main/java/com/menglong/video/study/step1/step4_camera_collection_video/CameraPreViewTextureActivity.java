package com.menglong.video.study.step1.step4_camera_collection_video;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.constant.VideoStudyConstant;

import java.io.IOException;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class CameraPreViewTextureActivity extends BaseActivity implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    private TextureView cameraPreviewTextureview;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_camera_textureview);
        cameraPreviewTextureview = findViewById(R.id.camera_preview_textureview);
        cameraPreviewTextureview.setSurfaceTextureListener(this);

        // 打开摄像头并将展示方向旋转90度
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                Log.i(VideoStudyConstant.TAG, "onPreviewFrame: 预览回调：111");
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        camera.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
