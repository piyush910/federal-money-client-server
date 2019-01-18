package com.chandra.piyush.project3c.android.federalmoneynewserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.textView2);
        Intent i = new Intent(MainActivity.this, FederalMoneyService.class);
        startService(i);
        // register local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String status = intent.getStringExtra(FederalMoneyService.STATUS);
                        tv.setText(status);
                    }
                }, new IntentFilter(FederalMoneyService.FEDERAL_MONEY_BROADCAST)
        );
    }
}
