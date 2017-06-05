package com.example.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.test.R;

public class LifeCycleSubActivity extends Activity {

    private static String TAG = "LifeCycleSubActivity";

    private Button start_lifeCycleMainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "LifeCycleSubActivity->onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lifecyclesub_main);
        start_lifeCycleMainActivity = (Button) findViewById(R.id.start_lifeCycleMainActivity);
        start_lifeCycleMainActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(LifeCycleSubActivity.this,
                        LifeCycleMainActivity.class));
            }

        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "LifeCycleSubActivity->onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "LifeCycleSubActivity->onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "LifeCycleSubActivity->onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "LifeCycleSubActivity->onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "LifeCycleSubActivity->onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "LifeCycleSubActivity->onDestroy()");
        super.onDestroy();
    }
}