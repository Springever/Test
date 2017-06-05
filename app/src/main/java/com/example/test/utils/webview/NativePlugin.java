package com.example.test.utils.webview;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * @author WinterFellA
 * @purpose js与android交互插件
 */
public class NativePlugin {

    private Activity activity;

    private WebView webView;

    public NativePlugin(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    @JavascriptInterface
    public void showToast(final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,msg,Toast.LENGTH_SHORT);
            }
        });
    }
}
