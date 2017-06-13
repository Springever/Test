package com.example.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.R;

public class MainActivity extends Activity {

    private TextView main_thread = null;

    private TextView sub_thread = null;

    private TextView sub_thread_thread = null;

    private TextView click_thread = null;

    private TextView click_subclass_thread = null;

    private TextView click_window = null;

    private TextView click_subclass_window = null;

    private Button click_thread_button = null;

    private Button click_subclass_thread_button = null;

    private Button click_button = null;

    private Button click_windowManager_button = null;

    private Button click_subclass_button = null;

    private Button start_handlerExample = null;

    private Button start_webView = null;

    private Button start_lifeCycle = null;

    private Button start_adapter = null;

    private Button start_rx = null;

    private Button start_pullToRefresh = null;

    private Button start_expandListView = null;

    private Button start_myReactActivity = null;

    private Button start_JsoupActivity = null;

    private static String TAG = "com.example.test";

    private Thread main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_thread = (TextView) findViewById(R.id.main_thread);
        sub_thread = (TextView) findViewById(R.id.sub_thread);
        sub_thread_thread = (TextView) findViewById(R.id.sub_thread_thread);
        click_thread = (TextView) findViewById(R.id.click_thread);
        click_subclass_thread = (TextView) findViewById(R.id.click_subclass_thread);
        click_window = (TextView) findViewById(R.id.click_window);
        click_subclass_window = (TextView) findViewById(R.id.click_subclass_window);
        main_thread.setText("MainActivity." + Thread.currentThread().getName()
                + "-" + Thread.currentThread().getId());
        main = Thread.currentThread();
        click_thread_button = (Button) findViewById(R.id.click_thread_button);
        click_subclass_thread_button = (Button) findViewById(R.id.click_subclass_thread_button);
        click_button = (Button) findViewById(R.id.click_button);
        click_windowManager_button = (Button) findViewById(R.id.click_windowManager_button);
        click_subclass_button = (Button) findViewById(R.id.click_subclass_button);
        start_handlerExample = (Button) findViewById(R.id.start_handlerExample);
        start_webView = (Button) findViewById(R.id.start_webView);
        start_lifeCycle = (Button) findViewById(R.id.start_lifeCycle);
        start_adapter = (Button) findViewById(R.id.start_adapter);
        start_rx = (Button) findViewById(R.id.start_rx);
        start_pullToRefresh = (Button) findViewById(R.id.start_pullToRefresh);
        start_expandListView = (Button) findViewById(R.id.start_expandListView);
        start_myReactActivity = (Button) findViewById(R.id.start_ReactNativeActivity);
        start_JsoupActivity = (Button) findViewById(R.id.start_JsoupActivity);
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (main != Thread.currentThread()) {
                    Log.d(TAG, "MainActivity.sub_thread not equals main thread");
                } else {
                    Log.d(TAG, "MainActivity.sub_thread equals main thread");
                }
                sub_thread.setText("MainActivity.sub_thread."
                        + Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId());
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (main != Thread.currentThread()) {
                            Log.d(TAG,
                                    "MainActivity.sub_thread_thread not equals main thread");
                        } else {
                            Log.d(TAG,
                                    "MainActivity.sub_thread_thread equals main thread");
                        }
                        sub_thread_thread
                                .setText("MainActivity.sub_thread_thread."
                                        + Thread.currentThread().getName()
                                        + "-" + Thread.currentThread().getId());
                    }
                }).start();
            }

        }).start();
        click_thread_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (main != Thread.currentThread()) {
                            Log.d(TAG,
                                    "MainActivity.click_thread not equals main thread");
                        } else {
                            Log.d(TAG,
                                    "MainActivity.click_thread equals main thread");
                        }
                        Log.d(TAG, "MainActivity.click_thread."
                                + Thread.currentThread().getName() + "-"
                                + Thread.currentThread().getId());
                        click_thread.setText("MainActivity.click_thread."
                                + Thread.currentThread().getName() + "-"
                                + Thread.currentThread().getId());
                    }
                }).start();
            }

        });
        click_subclass_thread_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new SubClass().start();
            }

        });
        click_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d(TAG, "MainActivity.click_window."
                        + Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId());
                click_window.setText("MainActivity.click_window."
                        + Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId());
            }

        });
        click_windowManager_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new SubWindow().start();
            }

        });
        click_subclass_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new SubClassUpdate().updateMainUI();
            }

        });
        start_handlerExample.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, HandlerExample.class));
            }

        });
        start_webView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            }
        });
        start_lifeCycle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, LifeCycleMainActivity.class));
            }
        });
        start_adapter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, AdapterActivity.class));
            }
        });
        start_rx.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, RxActivity.class));
            }
        });
        start_pullToRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, PullToRefreshActivity.class));
            }
        });
        start_expandListView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, ExpandListViewActivity.class));
            }
        });
        start_myReactActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, ReactNativeActivity.class));
            }
        });
        start_JsoupActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MainActivity.this, JsoupActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    class SubClass extends Thread {

        @Override
        public void run() {
            if (main != Thread.currentThread()) {
                Log.d(TAG,
                        "MainActivity.click_subclass_thread not equals main thread");
            } else {
                Log.d(TAG,
                        "MainActivity.click_subclass_thread equals main thread");
            }
            Log.d(TAG, "MainActivity.click_subclass_thread."
                    + Thread.currentThread().getName() + "-"
                    + Thread.currentThread().getId());
            click_subclass_thread.setText("MainActivity.click_subclass_thread."
                    + Thread.currentThread().getName() + "-"
                    + Thread.currentThread().getId());
        }
    }

    class SubWindow extends Thread {
        @Override
        public void run() {
            // Looper.prepare();
            TextView tx = new TextView(MainActivity.this);
            tx.setText("创建子窗口");
            WindowManager wm = MainActivity.this.getWindowManager();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    250, 150, 200, 200,
                    WindowManager.LayoutParams.FIRST_SUB_WINDOW,
                    WindowManager.LayoutParams.TYPE_TOAST, PixelFormat.OPAQUE);
            wm.addView(tx, params);
            // Looper.loop();
        }
    }

    class SubClassUpdate {

        public void updateMainUI() {
            Log.d(TAG, "MainActivity.click_subclass_window."
                    + Thread.currentThread().getName() + "-"
                    + Thread.currentThread().getId());
            click_subclass_window.setText("MainActivity.click_subclass_window."
                    + Thread.currentThread().getName() + "-"
                    + Thread.currentThread().getId());
            TextView tx = new TextView(MainActivity.this);
            tx.setText("创建子窗口-非线程创建");
            WindowManager wm = MainActivity.this.getWindowManager();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    250, 150, 200, 200,
                    WindowManager.LayoutParams.FIRST_SUB_WINDOW,
                    WindowManager.LayoutParams.TYPE_TOAST, PixelFormat.OPAQUE);
            //params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            //params.format = PixelFormat.RGBA_8888;
            wm.addView(tx, params);
        }
    }
}