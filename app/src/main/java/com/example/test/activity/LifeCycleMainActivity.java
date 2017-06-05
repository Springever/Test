package com.example.test.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.test.R;

public class LifeCycleMainActivity extends Activity {

    private static String TAG = "LifeCycleMainActivity";

    private Button start_lifeCycleSubActivity = null;

    private Button start_dialog = null;

    private AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "LifeCycleMainActivity->onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lifecycle_main);
        start_lifeCycleSubActivity = (Button) findViewById(R.id.start_lifeCycleSubActivity);
        start_dialog = (Button) findViewById(R.id.start_dialog);
        start_lifeCycleSubActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LifeCycleMainActivity.this,
                        LifeCycleSubActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//标准模式
                // intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//如果存在栈顶才重复使用；否则创建新的
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//A->B->C->D;D跳转B，则新建B，且剩下A->B
                //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                //		| Intent.FLAG_ACTIVITY_CLEAR_TOP);//A->B->C->D;D跳转B，如果是SINGLE_TOP，则重复利用，否则新建B，且剩下A->B
                //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//A->B->C->D;D跳转B，如果B存在，则重复利用，则为A-C->D->B
                startActivity(intent);
            }

        });

        start_dialog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showCloseDiolg();
            }

        });
    }

    private void showCloseDiolg() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("确定退出应用？");
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });

        dialog.setNegativeButton("确定 ", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = dialog.create();
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "LifeCycleMainActivity->onStart()");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "LifeCycleMainActivity->onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "LifeCycleMainActivity->onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "LifeCycleMainActivity->onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "LifeCycleMainActivity->onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "LifeCycleMainActivity->onDestroy()");
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}