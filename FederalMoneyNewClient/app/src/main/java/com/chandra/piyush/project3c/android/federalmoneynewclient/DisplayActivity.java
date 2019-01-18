package com.chandra.piyush.project3c.android.federalmoneynewclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayActivity extends AppCompatActivity {
    private static boolean isMnthlyAvg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent(); // get json string from intent
        String value = intent.getStringExtra("data");
        isMnthlyAvg = intent.getBooleanExtra("isMnthlyAvg", false);
        if (isMnthlyAvg) {
            value = value.substring(1, value.length() - 1);
        }
        jsonParser(value);
    }

    /**
     * This method will parse the json object and will display it iteratively in table
     *
     * @param jsonString
     */
    public void jsonParser(String jsonString) {
        try {
            final JSONArray jsonArray = new JSONArray(jsonString);
            final int n = jsonArray.length();
            TableLayout ll = (TableLayout) findViewById(R.id.score_table);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView tv1 = new TextView(this);
            tv1.setText("S.No");
            TextView tv2 = new TextView(this);
            tv2.setText("Open Today");
            tv1.setGravity(Gravity.CENTER_HORIZONTAL);
            tv2.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(tv1);
            row.addView(tv2);
            row.setBackgroundResource(R.drawable.row_border);
            row.setGravity(Gravity.CENTER);
            ll.addView(row, 0);
            for (int i = 0; i < n; ++i) {
                final JSONObject treasuryData = jsonArray.getJSONObject(i);
                row = new TableRow(this);
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                tv1 = new TextView(this);
                tv1.setText("" + (i + 1));
                tv2 = new TextView(this);
                if (isMnthlyAvg) {
                    tv2.setText(treasuryData.getString("open_mo"));
                } else {
                    tv2.setText(treasuryData.getString("open_today"));
                }
                tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                row.addView(tv1);
                row.addView(tv2);

                row.setBackgroundResource(R.drawable.row_border);
                ll.addView(row, i + 1);
            }
        } catch (JSONException e) {
            Log.e("JSON Exception", e.getMessage());
        }
    }
}
