package com.xiaoqing.flight.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.xiaoqing.flight.R;
import com.xiaoqing.flight.util.ApiServiceManager;
import com.xiaoqing.flight.util.CommonUtils;

public class LauncherActivity extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }

    @Override protected void onResume() {
        super.onResume();
        AsyncTask<Void, Void, Void> loading = new AsyncTask<Void, Void, Void>() {

            @Override protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startActivity(new Intent(LauncherActivity.this, LoginHomeActivity.class));
                finish();
            }
        };
        loading.execute();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }
}
