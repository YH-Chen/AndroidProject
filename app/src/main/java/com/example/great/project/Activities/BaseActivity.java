package com.example.great.project.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Danboard on 18-1-7.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private static final String EXIT_ACTION = "action.exit";

    private ExitReceiver exitReceiver = new ExitReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXIT_ACTION);
        registerReceiver(exitReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(exitReceiver);
    }

    class ExitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BaseActivity.this.finish();
        }
    }

}