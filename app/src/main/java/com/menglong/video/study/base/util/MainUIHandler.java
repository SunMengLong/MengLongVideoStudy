package com.menglong.video.study.base.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by 10007358 on 2020/8/4.
 */

public class MainUIHandler extends Handler {
    private static MainUIHandler uiHandler;

    private MainUIHandler(Looper looper) {
        super(looper);
    }

    public static synchronized MainUIHandler handler() {
        if(uiHandler == null) {
            uiHandler = new MainUIHandler(Looper.getMainLooper());
        }

        return uiHandler;
    }

    public void postWaiting(Runnable r) {
        PermissionUtil.checkNotInMainThread();
        MainUIHandler.WaitingRunnableProxy rProxy = new MainUIHandler.WaitingRunnableProxy(r);
        this.post(rProxy);
        rProxy.waiting();
    }

    static class WaitingRunnableProxy implements Runnable {
        private Runnable innerRunnable;
        private SingleBlock block = new SingleBlock();

        public WaitingRunnableProxy(Runnable r) {
            this.innerRunnable = r;
        }

        public void run() {
            this.innerRunnable.run();
            this.block.signal();
        }

        public void waiting() {
            this.block.waiting();
        }
    }
}
