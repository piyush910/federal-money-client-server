package com.chandra.piyush.project3c.android.federalmoneynewserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.chandra.piyush.project3c.android.common.FederalMoneyRules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FederalMoneyService extends Service {
    private static final String TAG = "HttpGetTask";
    private static final String URL = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=";
    public static final String FEDERAL_MONEY_BROADCAST = FederalMoneyService.class.getName() + "FederalBroadcast", STATUS = "status";
    private static boolean isBound = false;
    private static boolean isStarted = false;

    public FederalMoneyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStarted = true;
        if (isBound) {
            sendBroadcastMessage("Started and Bound to one or more Clients");
        } else {
            sendBroadcastMessage("Started but not Bound to a Client");
        }
        return super.onStartCommand(intent, flags, startId);

    }

    // Implement the Stub for this Object
    private final FederalMoneyRules.Stub mBinder = new FederalMoneyRules.Stub() {

        @Override
        public synchronized List monthlyAvgCash(int aYear) {
            isBound = true;
            if (isStarted) {
                sendBroadcastMessage("Started and Bound to one or more Clients");
            } else {
                sendBroadcastMessage("Bound but not Started");
            }
            String data = "";
            String Query = "select open_mo from(select open_mo, min(day), month from t1 where date > '" + aYear
                    + "-01-01' AND date < '" + aYear + "-12-31' group by month)";
            HttpURLConnection httpUrlConnection = null;
            try {
                String fullURL = URL + URLEncoder.encode(Query, "UTF-8");
                Log.e(TAG, fullURL);
                httpUrlConnection = (HttpURLConnection) new URL(fullURL)
                        .openConnection();

                InputStream in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());

                data = readStream(in);
            } catch (MalformedURLException exception) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                Log.e(TAG, "IOException");
            } finally {
                if (null != httpUrlConnection)
                    httpUrlConnection.disconnect();
            }
            List dataList = new ArrayList();
            dataList.add(data);
            return dataList;
        }

        @Override
        public synchronized List dailyCash(int aYear, int aMonth, int aDay, int aNumber) {
            String data = "";
            isBound = true;
            if (isStarted) {
                sendBroadcastMessage("Started and Bound to one or more Clients");
            } else {
                sendBroadcastMessage("Bound but not Started");
            }
            String mon = String.format("%02d", aMonth);
            String dayOfMonth = String.format("%02d", aDay);
            String fromDate = aYear + "-" + mon + "-" + dayOfMonth;
            String toDate = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(fromDate));
            } catch (ParseException e) {
                Log.e("Parse Exception: ", e.getMessage());
            }

            c.add(Calendar.DATE, aNumber);  // number of days to add to make to date
            toDate = sdf.format(c.getTime());  // toDate is now the new date
            fromDate = fromDate.replace("'", "");
            fromDate = "'" + fromDate + "'";
            toDate = "'" + toDate + "'";
            String Query = "SELECT  \"open_today\" FROM t1 WHERE (\"date\" >= " + fromDate + " AND \"date\" <= " + toDate + " AND \"open_today\" > '1000')";
            HttpURLConnection httpUrlConnection = null;
            try {
                String fullURL = URL + URLEncoder.encode(Query, "UTF-8");
                Log.e(TAG, fullURL);
                httpUrlConnection = (HttpURLConnection) new URL(fullURL)
                        .openConnection();

                InputStream in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());

                data = readStream(in);
            } catch (MalformedURLException exception) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                Log.e(TAG, "IOException");
            } finally {
                if (null != httpUrlConnection)
                    httpUrlConnection.disconnect();
            }
            List dataList = new ArrayList();
            dataList.add(data);
            return dataList;
        }
    };

    // Return the Stub defined above
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        if (isStarted) {
            sendBroadcastMessage("Started but not Bound to a Client");
        } else {
            sendBroadcastMessage("Neither bound nor started");
        }
        return super.onUnbind(intent);
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        // StringBuffer is a thread-safe String that can also be changed
        StringBuffer data = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }

    private void sendBroadcastMessage(String statusMessage) {
        if (statusMessage != null && !statusMessage.isEmpty()) {
            Intent intent = new Intent(FEDERAL_MONEY_BROADCAST);
            intent.putExtra(STATUS, statusMessage);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;
        isBound = false;
        sendBroadcastMessage("Destroyed");
    }
}
