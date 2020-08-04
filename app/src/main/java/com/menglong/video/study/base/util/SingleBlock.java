package com.menglong.video.study.base.util;

import android.util.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 10007358 on 2020/8/4.
 */
public class SingleBlock {
    static final String TAG = SingleBlock.class.getName();
    private final Lock lock = new ReentrantLock();
    private final Condition waitingConditon;
    private final Condition signalConditon;
    private boolean signal;

    public SingleBlock() {
        this.waitingConditon = this.lock.newCondition();
        this.signalConditon = this.lock.newCondition();
        this.signal = false;
    }

    public void waiting() {
        this.lock.lock();

        try {
            while(!this.signal) {
                this.waitingConditon.await();
            }

            this.signal = false;
            this.signalConditon.signal();
        } catch (InterruptedException var5) {
            Log.e(TAG, "waiting", var5);
        } finally {
            this.lock.unlock();
        }

    }

    public void signal() {
        this.lock.lock();

        try {
            while(this.signal) {
                this.signalConditon.await();
            }

            this.signal = true;
            this.waitingConditon.signal();
        } catch (InterruptedException var5) {
            Log.e(TAG, "signal", var5);
        } finally {
            this.lock.unlock();
        }

    }
}