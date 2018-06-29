package com.oldking.myapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by OldKing on 2018/5/31 0031.
 */

public class CountService extends Service {

    private static final String TAG = CountService.class.getSimpleName();
    private int count = 0;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                count++;
                Log.i(TAG, "count = " + count);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Intent it = new Intent();
        it.setAction("service.count");
        it.putExtra("count", count);
        sendBroadcast(it);
    }
}
