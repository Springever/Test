package com.example.test.activity;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.R;

public class HandlerExample extends Activity {

    private TextView handler_post_text = null;

    private TextView timer_handler_text = null;

    private TextView thread_handler_text = null;

    private TextView asyncTask_text = null;

    private Button thread_handler_button = null;

    private Button asyncTask_button = null;

    private Handler handler;

    private Timer timer;

    private AsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        //WeakReference<Handler> wf = new WeakReference<Handler>(new MyHandler());
        //handler = (wf != null) ? wf.get() : null;
        handler = new MyHandler();
        timer = new Timer();
        timer.schedule(new MyTimer(), 1, 5000);
        handler_post_text = (TextView) findViewById(R.id.handler_post_text);
        timer_handler_text = (TextView) findViewById(R.id.timer_handler_text);
        thread_handler_text = (TextView) findViewById(R.id.thread_handler_text);
        asyncTask_text = (TextView) findViewById(R.id.asyncTask_text);
        thread_handler_button = (Button) findViewById(R.id.thread_handler_button);
        asyncTask_button = (Button) findViewById(R.id.asyncTask_button);
        thread_handler_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                        // handler.sendEmptyMessage(int what);
                        // handler.sendEmptyMessageAtTime(int what, long uptimeMillis);//什么时间执行发送消息
                        // handler.sendEmptyMessageDelayed(int what, long delayMillis);//延迟多少执行发送消息
                        // handler.sendMessageAtTime(Message msg, long uptimeMillis);//什么时间执行发送消息
                        // handler.sendMessageDelayed(Message msg, long uptimeMillis);//延迟多少执行发送消息
                    }
                }).start();
            }

        });
        asyncTask_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                asyncTask = new MyAsyncTask();//需要在UI线程中进行
                asyncTask.execute("hello world");//需要在UI线程中进行
                //asyncTask.execute(Runnable runnable);
                //asyncTask.execute(Object o);
                //asyncTask.execute(Object... params);
            }
        });
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                handler_post_text.setText("MainActivity.handler_post."
                        + Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId());
            }
        }, handler_post_text, SystemClock.uptimeMillis() + 4000);// 默认Message
        // handler.post(Runnable r);
        // handler.postAtTime(Runnable r, long uptimeMillis);//什么时间执行发送消息
        // handler.postDelayed(Runnable r, long uptimeMillis);//延迟多少执行发送消息
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 设置定时器任务
    class MyTimer extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

    //onPreExecute->doInBackground->onProgressUpdate(需要doInBackground调用了publishProgress才能通过Handler触发)->onPostExecute(可无)
    //第一个参数是execute方法、doInBackground方法参数类型；
    //第二个参数是onProgressUpdate方法的参数类型；
    //第三个参数是onPostExecute方法的参数类型以及doInBackground返回参数类型
    class MyAsyncTask extends AsyncTask<Object, Integer, String> {

        @Override
        protected void onPreExecute() {//可以ui更新
            asyncTask_text.setText("MainActivity.asyncTask."
                    + Thread.currentThread().getName() + "-"
                    + Thread.currentThread().getId());
        }

        @Override
        protected String doInBackground(Object... arg0) {//可以执行耗时的任务，因而不能执行ui更新
            return "AsyncTask";
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {

        }

        @Override
        protected void onPostExecute(String result) {//更新ui，显示结果
            asyncTask_text.setText("AsyncTask");
        }

        @Override
        protected void onCancelled() {//取消执行任务时，更新ui

        }
    }

    final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 1) {
                timer_handler_text.setText("MainActivity.timer_handler."
                        + Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId());
            } else if (message.what == 2) {
                thread_handler_text.setText("MainActivity.thread_handler."
                        + Thread.currentThread().getName() + "-"
                        + Thread.currentThread().getId());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);// 释放Handler
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
