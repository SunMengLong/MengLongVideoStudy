package com.menglong.video.study.step1.step4_camera_collection_video;

import android.os.Bundle;
import android.view.View;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;
import com.menglong.video.study.base.util.Util;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class CollectionVideoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_video);
    }

    /**
     * 预览相机 - SurfaceView方式
     *
     * @param view
     */
    public void video_preview_surfaceview(View view) {
        Util.intentActivity(this, CameraPreViewSurfaceViewActivity.class);
    }

    /**
     * 预览相机 - TextureView方式
     *
     * @param view
     */
    public void video_preview_textureview(View view) {
        Util.intentActivity(this, CameraPreViewTextureActivity.class);
    }
}
