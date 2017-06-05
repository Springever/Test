package com.example.test.utils.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.http.SslError;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.cookie.Cookie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * @author WiterFellA 2017-03-06
 * @purpose WebView设置
 */
public class WebViewManager {

    private Context context;

    private WebView webView;

    private WebSettings webSettings;

    private static final String APP_CACAHE_DIRNAME = "/webcache";

    private List<Cookie> cookies = null;

    private WebViewManageListener webViewManageListener = null;

    private AlertDialog.Builder mBuilder;

    private AlertDialog mAlertDialog;

    public WebViewManager(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        initWebView();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initWebView() {
        webSettings = webView.getSettings();
        //webSettings.setSupportZoom(true);// 支持缩放
        //webSettings.setBuiltInZoomControls(true);// 出现缩放工具
        //webSettings.setUseWideViewPort(true);// 扩大比例的缩放
        webSettings.setLoadWithOverviewMode(true);// 设置页面自适应屏幕
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小
         * 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
         */
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 设置布局方式-自适应屏幕
        //webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);//不使用网络，只读取本地缓存数据。
        //webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式，根据cache-control决定是否从网络上取数据。
        //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不使用webview缓存
        //webSettings.setPluginState(WebSettings.PluginState.ON);// 支持flash，API18以后不用了
        //webSettings.setAppCacheEnabled(true);//h5的应用缓存（可选择性缓存html、css、js等所有）
        webSettings.setDomStorageEnabled(true);// 使用h5的localStorage（缓存生命周期无限制）、sessionStorage（生命周期为session存在时间，即浏览器窗口关闭，即删除）
        webSettings.setDatabaseEnabled(true);// 开启数据库存储功能
        webSettings.setNeedInitialFocus(false); // 阻止内部节点获取焦点
        webSettings.setJavaScriptEnabled(true);// 支持读取文件后，可以调用javascript
        // 可以使用File协议，这样才能加载本地文件包括h5页面（比如loadUrl或者file:///）
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        // 由于类似进行跨域请求读取其他文件；比如在读取一个html（里面有js）后，在可以通过//此js读取其他本地文件；是否开启file协议同源检查
        webSettings.setAllowFileAccessFromFileURLs(true);
        // 真正的跨域请求，可以通过https、http等访问其他服务器资源
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        //webSettings.setBlockNetworkLoads(false);// 是否加载网络资源
        //webSettings.setBlockNetworkImage(true);// 是否加载网络图像
        //webSettings.setDefaultFontSize(16);//设置默认字体的大小
        //webSettings.setFixedFontFamily("monospace");//设置默认使用的字体
        //webSettings.setLightTouchEnabled(true);//设置用鼠标激活被选项
        //webSettings.setDefaultTextEncodingName("uft-8");//设置编码格式，比如gbk
        //webSettings.setSavePassword(true);//用户与密码明文保存在h5应用缓存中
        /*
        if (Build.VERSION.SDK_INT >= 19) {//不要自动加载图片，等页面finish后再发起图片加载。
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        */
        // SDK3.0以上开启硬件加速，部分机器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //webView.setDrawingCacheEnabled(true);// 开启图片cache
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条
        // 支持获取手势焦点
        webView.requestFocusFromTouch();
        webView.setFocusable(true);
        webView.setLongClickable(true);// 支持长单击事件
        webView.setOnLongClickListener(new WebView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;// WebView开启复制粘贴功能，如果要关闭，改为true
            }
        });
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        webView.setWebViewClient(webViewClient);
    }

    //加载
    public void load(String url) {
        webView.loadUrl(url);
        //webView.loadUrl("javascript:"+functionName+"('"+returnResult+"')");
        webView.loadUrl("javascript:test('hello,world')");
        //webView.loadUrl("file:///android_asset/www/index.html");
        webView.requestFocus();
    }

    //截全屏
    public Bitmap captureWebView(WebView webView) {
        float scale = webView.getScale();//由于webView可能放大或缩小
        int h = (int) (webView.getContentHeight() * scale);
        Bitmap bmp = Bitmap.createBitmap(webView.getWidth(), h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        webView.draw(canvas);
        return bmp;
    }

    //图片缩放
    public Bitmap getScaleBitmap(Bitmap bitmap, float scaleXY) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int w = outMetrics.widthPixels;
        float scale = (float) ((w * scaleXY) / bitmap.getWidth());
        int bW = bitmap.getWidth();
        int bH = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, bW, bH, matrix, true);
        return newBmp;
    }

    //压缩，图片大小不能超过maxSize(KB),压缩质量quality(100表示不压缩)
    public ByteArrayOutputStream compress(Bitmap bitmap, double maxSize, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 99;
        if(baos.toByteArray().length / 1024 <= maxSize){
            if (quality >= 0 && quality < 100) {
                bitmap.compress(Bitmap.CompressFormat.PNG,
                        quality, baos);
            } else if (quality <= 0) {
                bitmap.compress(Bitmap.CompressFormat.PNG,
                        0, baos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            }
        }else{
            while (baos.toByteArray().length / 1024 > maxSize) {
                options -= 3;
                if (options < 0) {
                    break;
                }
                baos.reset();
                if (quality >= 0 && quality < 100) {
                    bitmap.compress(Bitmap.CompressFormat.PNG,
                            quality, baos);
                } else if (quality <= 0) {
                    bitmap.compress(Bitmap.CompressFormat.PNG,
                            0, baos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                }
            }
        }
        return baos;
    }

    private void clearWebView() {
        webView.pauseTimers();
        webView.stopLoading();
        webView.setDrawingCacheEnabled(false);
        webView.removeAllViews();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFocus();
        webView.destroy();
    }


    // 清除WebView缓存
    public void clearWebViewCache() {
        /*Webview缓存
        /data/data/package_name/cache/webview/
        /data/data/package_name/cache/webviewCache/
        /data/data/package_name/databases/webview.db 缓存对应的索引
        /data/data/package_name/databases/webviewCache.db 缓存对应的索引
         */
        // 清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // WebView 缓存文件 data/data/<application package>/files
        File appCacheDir = new File(context.getFilesDir().getAbsolutePath()
                + APP_CACAHE_DIRNAME);
        ///data/data/<application package>/cache
        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()
                + "/webviewCache");
        // 删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        // 删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    // 递归删除 文件/文件夹
    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {

        }
    }

    // 添加javascript插件,页面与android互动
    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object plugin, String name) {
        webView.addJavascriptInterface(plugin, name);
    }

    public void setListener(WebViewManageListener listener) {
        this.webViewManageListener = listener;
    }

    // 添加需要同步的Cookie，并且做一次同步
    public void setCookies(List<Cookie> cookies) {
        removeCookie();
        this.cookies = cookies;
        syncCookies(this.cookies);
    }

    // 清除Cookie
    private void removeCookie() {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    // Cookie 同步
    private void syncCookies(List<Cookie> cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        if (cookies != null) {
            for (int i = 0; i < cookies.size(); i++) {
                String sessionString = cookies.get(i).getName() + "="
                        + cookies.get(i).getValue() + ";domain="
                        + cookies.get(i).getDomain();
                // String sessionString = cookies.get(i).getName()
                // + "=" + cookies.get(i).getValue()
                // + ";path=" + cookies.get(i).getPath();
                cookieManager.setCookie("http://192.168.1.200:8080/webview",
                        sessionString);
            }
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            CookieSyncManager.getInstance().sync();
        }
    }

    // 是否支持JS alert 显示
    public void setSupportJsAlert(boolean isSupport) {
        if (!isSupport) {
            webView.setWebChromeClient(null);
        } else {
            webView.setWebChromeClient(new WebChromeClient() {
                @SuppressLint("NewApi")
                @Override
                public boolean onJsAlert(WebView view, String url,
                                         String message, final JsResult result) {
                    AlertDialog.Builder b2 = new AlertDialog.Builder(
                            context,
                            AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("测试WebView")
                            .setMessage(message)
                            .setPositiveButton("确定",
                                    new AlertDialog.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            result.confirm();
                                        }
                                    });
                    b2.setCancelable(false);
                    b2.create();
                    b2.show();
                    return true;
                }

                @Override
                public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                    new AlertDialog.Builder(context)
                            .setTitle("confirm")
                            .setMessage(message)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "hello world", Toast.LENGTH_SHORT);
                                    result.confirm();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //处理结果为取消状态 同时唤醒WebCore线程
                                    result.cancel();
                                }
                            }).create().show();
                    return true;
                }

                //当WebView进度改变时更新窗口进度
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //Activity的进度范围在0到10000之间,所以这里要乘以100
                    ((Activity) context).setProgress(newProgress * 100);
                }
            });
        }
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.requestFocus();
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//开始加载网页时，发生的的动作
            super.onPageStarted(view, url, favicon);
        }

        // /**
        // * 此方法从api 21开始引入
        // */
        // @SuppressLint("NewApi")
        // public WebResourceResponse shouldInterceptRequest(WebView view,
        // android.webkit.WebResourceRequest request) {
        // String url = request.getUrl().getPath();
        // WebResourceResponse response = super.shouldInterceptRequest(view,
        // request);
        // return getResourceResponse(url,response);
        // };
        //
        // /**
        // * 此方法从api 11开始引入，api 21弃用
        // */
        // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        // @Override
        // public WebResourceResponse shouldInterceptRequest(WebView view,
        // String url) {
        // WebResourceResponse response = super.shouldInterceptRequest(view,
        // url);
        // return getResourceResponse(url,response);
        // }

        @Override
        public void onPageFinished(WebView view, String url) {//加载网页完成后，发生的的动作
            super.onPageFinished(view, url);
            /*
            if (!webSettings.getLoadsImagesAutomatically()) {
                webSettings.setLoadsImagesAutomatically(true);
            }
            */
        }

        @SuppressLint("NewApi")
        @Override
        public void onReceivedError(WebView view, final int errorCode,
                                    String description, String failingUrl) {//网页加载失败后，发生的动作
            String errorFlagString = "";
            switch (errorCode) {
                // User authentication failed on server
                case ERROR_AUTHENTICATION:
                    errorFlagString = "用户认证失败";
                    break;
                // Failed to connect to the server
                case ERROR_CONNECT:
                    errorFlagString = "连接服务器失败";
                    break;
                // Connection timed out
                case ERROR_TIMEOUT:
                    errorFlagString = "网络连接超时";
                    break;
                case ERROR_PROXY_AUTHENTICATION:
                    errorFlagString = "用户代理验证失败";
                    break;
                case ERROR_HOST_LOOKUP:
                    // errorFlagString = "服务器绑定或代理失败";
                    errorFlagString = "网络连接已断开，请稍后再试";
                    break;
                case ERROR_BAD_URL:
                    errorFlagString = "URL 格式错误";
                    break;
                default:
                    errorFlagString = "未知错误";
                    break;
            }

            final int err = errorCode;
            final String msg = description;
            if (mBuilder == null) {
                mBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                mBuilder.setTitle("网页错误提示");
                mBuilder.setMessage(errorFlagString);
                mBuilder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (webViewManageListener != null) {
                                    webViewManageListener.onClickErrorConfirm(err,
                                            msg);
                                }
                                mBuilder = null;
                                dialog.dismiss();
                            }
                        });

                mAlertDialog = mBuilder.create();
                mAlertDialog.setCancelable(false);
                mAlertDialog.show();
            }
            if (view.canGoBack()) {
                view.goBack();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {//对https处理
            handler.proceed();  // 接受信任所有网站的证书
            // handler.cancel();   // 默认操作 不处理
            // handler.handleMessage(null);  // 可做其他处理
        }
    };

    public interface WebViewManageListener {
        // 联网错误弹出确认框，点击确认框监听
        public void onClickErrorConfirm(int errorCode, String description);
    }
}
