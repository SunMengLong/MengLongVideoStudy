package com.menglong.video.study.base.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.menglong.video.study.base.callback.PermissioLitener;
import com.menglong.video.study.base.util.Util;

import java.util.ArrayList;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class BaseActivity extends FragmentActivity {

    private static PermissioLitener mLitener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setStatusTransparentStyle(getWindow(), false);
    }

    public void requestRuntimePermision(String[] permission, PermissioLitener permissioLitener) {
        mLitener = permissioLitener;
        ArrayList<String> list = new ArrayList<>();
        for (String per : permission) {
            if (ContextCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED) {
                list.add(per);
            }
        }
        if (!list.isEmpty()) {
            ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), 1);
        } else {
            if (mLitener != null) {
                mLitener.onGranted();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            list.add(permission);
                        }
                    }
                    if (mLitener != null) {
                        if (!list.isEmpty()) {
                            mLitener.onDenied(list);
                        } else {
                            mLitener.onGranted();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
