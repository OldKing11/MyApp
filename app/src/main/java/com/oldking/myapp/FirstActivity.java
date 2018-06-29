package com.oldking.myapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by OldKing on 2018/5/31 0031.
 */

public class FirstActivity extends Activity {

    private Button start;
    private TextView stop;

    private Intent serviceIntent;
    private MyReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        serviceIntent = new Intent(FirstActivity.this, CountService.class);
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(serviceIntent);
            }
        });
        stop = (TextView) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(serviceIntent);
            }
        });
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("service.count");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("service.count".equals(intent.getAction())) {
                int count = intent.getIntExtra("count", 0);
                Toast.makeText(FirstActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
