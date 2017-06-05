package com.example.test.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.utils.webview.NativePlugin;
import com.example.test.utils.webview.WebViewManager;

import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author WiterFellA 2017-03-01
 * @purpose
 */
public class WebViewActivity extends Activity implements View.OnClickListener, WebViewManager.WebViewManageListener {

    private static String TAG = "WebviewActivity";

    private WebView webView;

    private Button cutGreenBt = null;

    private Button captureWebView = null;

    private EditText address = null;

    private WebViewManager webViewManager;

    private NativePlugin nativePlugin;

    private final String nativePluginName = "WebCallNative";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);////设置窗口风格为进度条
        setContentView(R.layout.webview);
        initGui();
        initAction();
        initData();
    }

    public void initGui() {
        webView = (WebView) findViewById(R.id.webview);
        cutGreenBt = (Button) findViewById(R.id.cutGreenBt);
        captureWebView = (Button) findViewById(R.id.captureWebView);
        address = (EditText) findViewById(R.id.address);
    }

    public void initAction() {
        cutGreenBt.setOnClickListener(this);
        captureWebView.setOnClickListener(this);
        captureWebView.setEnabled(false);
    }

    public void initData() {
        nativePlugin = new NativePlugin(this, webView);
        webViewManager = new WebViewManager(this, webView);
        webViewManager.setListener(this);
        webViewManager.addJavascriptInterface(nativePlugin, nativePluginName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cutGreenBt:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!StringUtils.isEmpty(address.getText().toString())) {
                            webViewManager.load(address.getText().toString().trim());
                        } else {
                            Toast.makeText(WebViewActivity.this, "请输入地址", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //webViewManager.load("file:///android_asset/www/log.txt");
                captureWebView.setEnabled(true);
                break;
            case R.id.captureWebView:
                runOnUiThread(new Runnable() {
                    ByteArrayOutputStream bos = null;
                    FileOutputStream fos = null;

                    @Override
                    public void run() {
                        try {
                            fos = new FileOutputStream(new File("/mnt/sdcard/capture.png"));
                            Bitmap bitmapSource = webViewManager.captureWebView(webView);
                            Bitmap bitmap = webViewManager.getScaleBitmap(bitmapSource, 0.5f);
                            bos = webViewManager.compress(bitmap, 200, 100);
                            fos.write(bos.toByteArray());
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {

                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {

                                }
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClickErrorConfirm(int errorCode, String description) {
        finish();
    }
}