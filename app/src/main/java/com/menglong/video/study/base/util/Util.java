package com.menglong.video.study.base.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by 10007358 on 2020/8/3.
 */

public class Util {

    /**
     * 设置状态栏透明的风格
     */
    public static void setStatusTransparentStyle(Window window, boolean isTextBlack) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //android 6.0及以上支持改变状态栏字体颜色
            if (isTextBlack) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            //这两句加上后 在我的Dialog里面 布局延伸到了状态栏此时要加一定的marginTop值来调整
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //android 5.0及以上支持改变状态栏为透明状态
            //顶部状态栏透明 这个一定要设置才生效 此处放在6.0以上才执行是因为
            //如果5.0执行了 则透明了 如果背景又是白色 就啥都看不见了 所以5.0要想实现效果需要UI的配合
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public static void intentActivity(Context mContext,Class intentClass) {
        Intent intent = new Intent(mContext, intentClass);
        mContext.startActivity(intent);
    }
}
