package com.chandra.piyush.project3c.android.federalmoneynewclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chandra.piyush.project3c.android.common.FederalMoneyRules;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.OnCompleteListener {
    private static Integer numOfDays;
    private static EditText numOfDaysEditText;
    private static EditText yearForMnthlyAvgEditText;
    private static int yearForMnthlyAvg;
    private static boolean isAvgBtnClicked = false;
    protected static final String TAG = "MainActivity Client";
    private boolean mIsBound = false;
    private FederalMoneyRules federalMoneyRules;
    private List averageMonthlyList;
    private List totalDataList;
    private static final int RECEIVED_MONTHLY_DATA = 0;
    private static final int RECEIVED_TOTAL_DATA = 1;
    private static int day;
    private static int month;
    private static int year;

    // A common handler to handle service when user clicks on to find total data and monthly data
    Handler clientHandler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case RECEIVED_MONTHLY_DATA:
                    Intent mnthlyAvgIntent = new Intent(MainActivity.this, DisplayActivity.class);

                    mnthlyAvgIntent.putExtra("data", msg.obj.toString());
                    mnthlyAvgIntent.putExtra("isMnthlyAvg", true);
                    MainActivity.this.startActivity(mnthlyAvgIntent);
                    break;

                case RECEIVED_TOTAL_DATA:
                    Intent totalDataIntent = new Intent(MainActivity.this, DisplayActivity.class);

                    totalDataIntent.putExtra("data", msg.obj.toString());
                    totalDataIntent.putExtra("isMnthlyAvg", false);
                    MainActivity.this.startActivity(totalDataIntent);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button findMonthlyAvgBtn = (Button) findViewById(R.id.findMonthlyAvgBtn);
        numOfDaysEditText = (EditText) findViewById(R.id.year);
        findMonthlyAvgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // average button listener
                isAvgBtnClicked = true;
                yearForMnthlyAvgEditText = findViewById(R.id.year);
                yearForMnthlyAvg = Integer.parseInt(yearForMnthlyAvgEditText.getText().toString());

                if (mIsBound) {
                    Thread t1 = new Thread(new ReadPageRunnable()); // start thread
                    t1.start();

                } else {
                    Log.i(TAG, "Average Monthly Client is not bound");
                }

            }

        });

        final Button findDataButton = (Button) findViewById(R.id.findDataButton);
        findDataButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // daily cash button listener
                isAvgBtnClicked = false;
                numOfDaysEditText = findViewById(R.id.numWorkingDays);
                numOfDays = Integer.parseInt(numOfDaysEditText.getText().toString());
                if(numOfDays>=5&& numOfDays<=25) { // check num of days entered
                    if (mIsBound) {
                        Thread t2 = new Thread(new ReadPageRunnable());
                        t2.start();

                    } else {
                        Log.i(TAG, "Total Data Client is not bound");
                    }
                }
            }
        });

        final Button unbindButton = findViewById(R.id.unbindButton);
        unbindButton.setOnClickListener(new View.OnClickListener() { // unbind button listener
            @Override
            public void onClick(View v) {
                unbindService(mConnection);
                mIsBound = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if not bind then bind to the service
        if (!mIsBound) {
            boolean b = false;
            Intent i = new Intent(FederalMoneyRules.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, 0);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if (b) {
                Log.i(TAG, "BindService() succeeded!");
            } else {
                Log.i(TAG, "BindService() failed!");
            }

        }
    }


    private final ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder iservice) {

            federalMoneyRules = FederalMoneyRules.Stub.asInterface(iservice);

            mIsBound = true;

        }

        public void onServiceDisconnected(ComponentName className) {

            federalMoneyRules = null;

            mIsBound = false;

        }
    };

    /**
     * Create separate thread for running service
     */
    public class ReadPageRunnable implements Runnable {

        public void run() {
            if (isAvgBtnClicked) {
                String avgMnthlyString = "";
                try {
                    averageMonthlyList = federalMoneyRules.monthlyAvgCash(yearForMnthlyAvg);
                    avgMnthlyString = averageMonthlyList + averageMonthlyList.get(0).toString();
                } catch (RemoteException e) {
                    Log.e(TAG, "Remote Exception");
                }
                for (Object x : averageMonthlyList) {
                    Log.i(TAG, x.toString());
                }
                Message msg = clientHandler.obtainMessage(MainActivity.RECEIVED_MONTHLY_DATA);
                msg.obj = avgMnthlyString;
                clientHandler.sendMessage(msg);
            } else {
                String totalDataString = "";
                try {
                    totalDataList = federalMoneyRules.dailyCash(year, month, day, numOfDays);
                    totalDataString = totalDataString + totalDataList.get(0).toString();
                } catch (RemoteException e) {
                    Log.e(TAG, "Remote Exception");
                }
                for (Object x : totalDataList) {
                    Log.i(TAG, x.toString());
                }
                Message msg = clientHandler.obtainMessage(MainActivity.RECEIVED_TOTAL_DATA);
                msg.obj = totalDataString;
                clientHandler.sendMessage(msg);
            }

        }
    }

    public void onButtonClicked(View v) { // Date Picker Button
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "Date Picker");
    }

    public void onComplete(List<Integer> dateList) { // date picker fragment interface implementation
        day = dateList.get(0);
        month = dateList.get(1);
        year = dateList.get(2);
    }
}
