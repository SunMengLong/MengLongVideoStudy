package com.menglong.video.study.base.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.menglong.video.study.R;
import com.menglong.video.study.base.callback.PermissioLitener;
import com.menglong.video.study.base.util.Util;
import com.menglong.video.study.step1.Step1Activity;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        requestRuntimePermision(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA}, new PermissioLitener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(ArrayList<String> list) {
                MainActivity.this.finish();
            }
        });
    }

    /**
     * 点击事件
     * @param view
     */
    public void study_step1_but(View view) {
        Util.intentActivity(this, Step1Activity.class);
    }

    public void study_step2_but(View view) {
    }

    public void study_step3_but(View view) {
    }

    public void study_step4_but(View view) {
    }
}
