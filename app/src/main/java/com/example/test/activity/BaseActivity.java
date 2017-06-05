package com.example.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.example.test.R;
import com.example.test.application.BaseApplication;
import com.example.test.data.ActivityTack;
import com.example.test.data.DataCenter;

/**
 * @authore WiActivitynterFellSo 2017/3/22
 * @purpose 基础
 */
public abstract class BaseActivity extends Activity{

    protected Activity ctx;

    protected BaseApplication app;

    private long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        // 取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        // window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 高亮
        // window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 添加到activity stack管理集合
        ActivityTack.getInstanse().addActivity(this);
        ctx = this;
        app = (BaseApplication) getApplication();
        app.setContext(ctx);
        if (getContentLayout() != 0) {
            setContentView(getContentLayout());
        }
        initGui();
        initAction();
        initData();
    }

    /**
     *
     * @Title: isFastDoubleClick
     * @Description: 判断事件出发时间间隔是否超过预定值
     * @param @return
     * @return boolean
     * @throws
     */
    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public void startActivity(Intent intent) {
        // 防止连续点击
        if (isFastDoubleClick()) {
            return;
        }
        super.startActivity(intent);
    }

    protected abstract int getContentLayout();

    protected abstract void initGui();

    protected abstract void initAction();

    protected abstract void initData();

    //完全退出
    public void exit(){
        ActivityTack.getInstanse().exit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ActivityTack.getInstanse().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
