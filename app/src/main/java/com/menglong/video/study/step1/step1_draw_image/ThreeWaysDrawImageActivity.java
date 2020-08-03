package com.menglong.video.study.step1.step1_draw_image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.menglong.video.study.R;
import com.menglong.video.study.base.activity.BaseActivity;

/**
 * Created by sml on 2020/8/3.
 * 三种方式绘制图片
 */

public class ThreeWaysDrawImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);

        // 通过ImageView绘制图片
        ((ImageView) findViewById(R.id.draw_image_way1)).setImageResource(R.mipmap.demo);

        // 通过SurfaceView绘制图片
        SurfaceView drawImageWay2 = findViewById(R.id.draw_image_way2);
        drawImageWay2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (surfaceHolder == null) {
                    return;
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.demo);
                Canvas canvas = surfaceHolder.lockCanvas(); // 锁定surfaceView 的 画布
                canvas.drawBitmap(bitmap, 0, 0, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }
}
