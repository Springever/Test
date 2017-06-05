package com.example.test.application;

import android.app.Application;
import android.content.Context;

/**
 * @authore WinterFellSo 2017/3/15
 * @purpose BaseApplication
 */
public class BaseApplication extends Application {

    private static BaseApplication application;

    private Context context;

    public static synchronized BaseApplication getInstance() {
        return application;
    }

    public Context getContext() {
        if (context == null) {
            context = this;
        }
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
