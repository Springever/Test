package com.example.test.activity;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * @authore WinterFellSo 2017/3/22
 * @purpose Rx系列库，RxJava（Android）、RxBingding
 */
public class RxActivity extends BaseActivity {

    private Button throttleFirst;

    private Button showColor;

    private CheckBox checkBoxChange;

    private TextView textView;

    private EditText editTextView;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_rx;
    }

    @Override
    protected void initGui() {
        throttleFirst = (Button) findViewById(R.id.throttleFirst);
        showColor = (Button) findViewById(R.id.showColor);
        checkBoxChange = (CheckBox) findViewById(R.id.checkBoxChange);
        textView = (TextView) findViewById(R.id.textView);
        editTextView = (EditText) findViewById(R.id.editTextView);
    }

    @Override
    protected void initAction() {
        //两秒钟之内只取一个点击事件，防抖操作
        RxView.clicks(throttleFirst)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(RxActivity.this, "点击", Toast.LENGTH_SHORT).show();
                    }
                });
        //监听长按时间
        RxView.longClicks(throttleFirst)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(RxActivity.this, "长按", Toast.LENGTH_SHORT).show();
                    }
                });
        //复选框监听
        RxCompoundButton.checkedChanges(checkBoxChange)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        showColor.setEnabled(aBoolean);
                        showColor.setBackgroundResource(aBoolean ? android.R.color.holo_red_light : android.R.color.holo_blue_bright);
                    }
                });
        //每隔两秒执行一次
        Observable.interval(2, 2, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                //TODO WHAT YOU WANT
            }
        });
        //在两秒后去执行一些操作（比如启动页跳转到主页面）
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                //TODO WHAT YOU WANT
            }
        });
        /*
        RxTextView.textChanges(editTextView)
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String value) {
                                //TODO WHAT YOU WANT
                            }
                        });
                        */
    }

    @Override
    protected void initData() {

    }
}
