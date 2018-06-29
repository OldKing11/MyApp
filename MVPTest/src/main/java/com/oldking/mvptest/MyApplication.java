package com.oldking.mvptest;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import butterknife.ButterKnife;

/**
 * Created by OldKing on 2018/6/12 0012.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Utils.init(this);
        ButterKnife.setDebug(true);
    }
}
