package com.example.test.utils.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.test.R;
import com.example.test.application.BaseApplication;
import com.example.test.common.AppSettings;
import com.example.test.common.Constants;
import com.example.test.common.Encoder;
import com.example.test.common.ShellUtils;
import com.example.test.data.DataCenter;
import com.example.test.download.DownloadControl;
import com.example.test.download.DownloadService;
import com.example.test.download.DownloadTask;
import com.example.test.download.TaskStatus;
import com.example.test.utils.device.LocalApps;
import com.example.test.widget.button.DownStatusButton;
import com.example.test.widget.pullToRefresh.ILoadingLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.uc.appmall.apk.merge.ZipMerge;

/**
 * @authore WinterFellSo 2017/3/15
 * @purpose 常用的一些方法
 */
public class Utils {

    private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    private static String gUid;

    private static final String APPINFO_DIR_NAME = ".appInfo";

    private static String APPINFO_FILENAME = "appinfo.dat";

    private static String APPINFO_UID = "uid";

    private static String APPINFO_CHANNELID = "channelid";

    private static String gChannelId;

    private static final String PREF_APPINFO = "pref_appinfo";

    private static final String KEY_BUSI = "X-BUSI";

    private static final String KEY_VNAME = "X-VNAME";

    private static final String KEY_VCODE = "X-VCODE";

    private static final String KEY_DEVICE = "X-DEVICE";

    private static final String KEY_TIME = "X-TIME";

    private static final String KEY_IMSI = "X-IMSI";

    private static final String KEY_IMEI = "X-IMEI";

    private static final String KEY_SID = "X-SID";

    private static final String KEY_SIGN = "X-SIGN";

    private static final String KEY_OSVER = "X-OSVER";

    private static final String KEY_SCR = "X-SCR";

    private static final String KEY_IAP = "X-IAP";

    private static final String KEY_NET = "X-NET";

    private static final String KEY_APP = "X-APP";

    private static final String WIFI = "wifi";

    static final String LOG_TAG = "PullToRefresh";

    public static byte[] getMd5(byte[] data) {
        MessageDigest digester = null;
        byte[] md5 = null;
        try {
            digester = MessageDigest.getInstance("MD5");
            digester.update(data, 0, data.length);
            md5 = digester.digest();
        } catch (NoSuchAlgorithmException e) { }
        return md5;
    }

    public static String toHexString(byte[] array)
    {
        int length = array.length;
        char[] buf = new char[length * 2];

        int bufIndex = 0;
        for (int i = 0 ; i < length; i++)
        {
            byte b = array[i];
            buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
            buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
        }

        return new String(buf);
    }

    public static byte[] readFromAsset(Context context, String fileName) {
        byte[] ret = null;
        InputStream instream = null;
        try {
            instream = context.getAssets().open(fileName);
            byte[] buffer = new byte[8192];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = -1;
            while ((len = instream.read(buffer)) >= 0)
                baos.write(buffer, 0, len);
            baos.flush();
            ret = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
        } finally {
            try {
                if (instream != null)
                    instream.close();
            } catch (IOException e) { }
        }

        return ret;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void AsyncTaskExecute(AsyncTask<Object, Object, Object> task, Object... params) {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            } else {
                task.execute(params);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void warnDeprecation(String depreacted, String replacement) {
        Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
    }
    public static void setupPTRLoadingProxy(Resources res, ILoadingLayout loadingLayoutProxy) {
        loadingLayoutProxy.setPullLabel(res.getString(R.string.ptr_pull_to_refresh));
        loadingLayoutProxy.setRefreshingLabel(res.getString(R.string.ptr_refreshing));
        loadingLayoutProxy.setReleaseLabel(res.getString(R.string.ptr_release_to_refresh));
        loadingLayoutProxy.setProgressDrawable(res.getDrawable(R.drawable.ptr_refreshing_drawable));
    }

    public static String getSizeString(long size) {
        if(size <= 0)
            return "0B";

        if (size < 100 * 1024) {
            return "0.1M";
        } else {
            return new DecimalFormat("#,##0.#").format(size / 1024f / 1024) + "M";
        }
    }

    public static void scaleClickRect(final View buttonView) {
        if(buttonView == null) return;
        if(View.class.isInstance(buttonView.getParent())) {
            final View parent = (View) buttonView.getParent();
            if(parent != null) {
                parent.post(new Runnable() {

                    @Override
                    public void run() {
                        Rect rect = new Rect();
                        buttonView.getHitRect(rect);
                        rect.top -=60;
                        rect.bottom +=60;
                        rect.right +=60;
                        parent.setTouchDelegate(new TouchDelegate(rect, buttonView));
                    }
                });
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void reqSystemInstall(Context context, String packagePath) {
        if (TextUtils.isEmpty(packagePath))
            return;

        File targetFile = new File(packagePath);
        if (!targetFile.exists() || targetFile.isDirectory())
            return;

        try {
            Uri packageURI = Uri.fromFile(targetFile);
            Intent intent = null;
            if (Build.VERSION.SDK_INT >= 14) {
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.setData(packageURI);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(packageURI, "application/vnd.android.package-archive");
            }
            if(context instanceof Activity)
                ((Activity) context).startActivityForResult(intent, 0);
            else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException anfe) { }
    }

    public static void reqSystemOpen(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        try {
            Intent intent = pm.getLaunchIntentForPackage(pkgName);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) { }
    }

    public static void reqSystemUninstall(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return;

        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        if (!(context instanceof Activity))
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    public static void doTaskInstall(Context context, DownloadTask task) {
        if(task == null) return;
        LocalApps.LocalAppInfo appInfo = DataCenter.getInstance().getLocalApps().getLocalPackage(task.mPackageName);
        if(task.mIsSignDiff && appInfo != null) {
            DataCenter.getInstance().reportDownloadEvent(DataCenter.MSG_INSTALL_SIGNATURE_NOTIFY_EVENT, task.mPackageName);
            return;
        }
        if (AppSettings.isRootInstall(context) && Utils.isPhoneRoot()) {
            task.mStatus = TaskStatus.STATUS_INSTALLING;
            if(DownloadService.getDownloadControl() != null)
                DownloadService.getDownloadControl().onTaskStatusChanged(task);
            Utils.AsyncTaskExecute(new DownloadControl.SilenceInstall(context), task);
        } else {
            reqSystemInstall(context, task.mLocalPath);
        }
    }

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    public static boolean isPhoneRoot() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/" };
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    @SuppressWarnings("deprecation")
    public static HashMap<String, String> generateXHeaders(Context context, String url, byte[] postData) {
        if (url == null)
            throw new NullPointerException();

        String versionName = null;
        String versionCode = null;
        String device = null;
        String imsiEncrypt = null;
        String imeiEncrypt = null;
        String sid = null;
        String sign = null;
        String osver = null;
        String scr = null;
        String iap = null;
        String net = null;

        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
            versionCode = String.valueOf(info.versionCode);
            device = Build.MODEL;

            TelephonyManager telManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            String imsi = telManager.getSubscriberId();
            if (imsi == null)
                imsi = "";
            imsiEncrypt = Base64.encodeToString(Encoder.encode(imsi.getBytes()), Base64.NO_WRAP);
            String imei = telManager.getDeviceId();
            if (imei == null)
                imei = "";
            imeiEncrypt = Base64.encodeToString(Encoder.encode(imei.getBytes()), Base64.NO_WRAP);

            sid = Base64.encodeToString(Encoder.encode(getUid(context).getBytes()), Base64.NO_WRAP);

            Uri uri = Uri.parse(url);
            int portValue = uri.getPort();
            String host = uri.getHost();
            String port = portValue == -1 ? null : String.valueOf(portValue);
            int index = port == null ? url.indexOf(host) + host.length() : url.indexOf(port) + port.length();
            String pathWithQuery = url.substring(index);
            if (postData == null) {
                sign = Utils.toHexString(Utils.getMd5((imeiEncrypt + Constants.VALUE_APP_KEY + pathWithQuery).getBytes()));
            } else {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                byteOutput.write((imeiEncrypt + Constants.VALUE_APP_KEY + pathWithQuery).getBytes());
                byteOutput.write(postData);
                sign = Utils.toHexString(Utils.getMd5(byteOutput.toByteArray()));
            }

            osver = Utils.getOSVer(context);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            scr = display.getWidth() + "x" + display.getHeight();

            NetworkInfo cni = Utils.getCurrNetworkInfo(context);
            if (cni != null) {
                net = cni.getTypeName();
                iap = cni.getExtraInfo();
            }
        } catch (Exception e) {
        }

        HashMap<String, String> ret = new HashMap<String, String>();
        ret.put(KEY_BUSI, Constants.VALUE_BUSI);
        ret.put(KEY_TIME, String.valueOf(System.currentTimeMillis() / 1000));
        ret.put(KEY_APP, Utils.getChannelId(context));
        if (!TextUtils.isEmpty(versionName))
            ret.put(KEY_VNAME, versionName);
        if (!TextUtils.isEmpty(versionCode))
            ret.put(KEY_VCODE, versionCode);
        if (!TextUtils.isEmpty(device))
            ret.put(KEY_DEVICE, device);
        if (!TextUtils.isEmpty(imsiEncrypt))
            ret.put(KEY_IMSI, imsiEncrypt);
        if (!TextUtils.isEmpty(imeiEncrypt))
            ret.put(KEY_IMEI, imeiEncrypt);
        if (!TextUtils.isEmpty(sid))
            ret.put(KEY_SID, sid);
        if (!TextUtils.isEmpty(sign))
            ret.put(KEY_SIGN, sign);
        if (!TextUtils.isEmpty(osver))
            ret.put(KEY_OSVER, osver);
        if (!TextUtils.isEmpty(scr))
            ret.put(KEY_SCR, scr);
        if (!TextUtils.isEmpty(net))
            ret.put(KEY_NET, net);
        if (!TextUtils.isEmpty(iap))
            ret.put(KEY_IAP, iap);

        return ret;
    }

    public static String getUid(Context context) {
        if(TextUtils.isEmpty(gUid)) {
            String uid = loadUidFromCacheFile(context);
            if(uid == null) {
                uid = getUidFromPref(context);
                if(uid == null) {
                    gUid = createUid(context);
                    setUidToPref(context, gUid);
                    saveAppInfoToCacheFile(context, gUid, gChannelId);
                }else {
                    gUid = uid;
                }
            } else {
                gUid = uid;
            }
        }
        return gUid;
    }

    public static String loadUidFromCacheFile(Context context) {
        String uid = null;
        String filePath = getAppInfoPath(context);
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            byte b[]=new byte[(int)file.length()];
            fis.read(b);
            JSONObject json = new JSONObject(new String(b));
            uid = json.getString(APPINFO_UID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e2) {
            }
        }
        return uid;
    }

    private static String getAppInfoPath(Context context) {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = new File(Environment.getExternalStorageDirectory(), APPINFO_DIR_NAME);
            if (!dir.exists())
                dir.mkdirs();
        } else {
            dir = context.getCacheDir();
        }

        File file = new File(dir, APPINFO_FILENAME);
        return file.getAbsolutePath();
    }

    public static String getOSVer(Context context) {
        String ret = Build.VERSION.RELEASE;
        if (ret == null)
            ret = "";
        return ret;
    }

    public static String getChannelIdFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_APPINFO, Context.MODE_PRIVATE);
        return pref.getString(APPINFO_CHANNELID, null);
    }

    public static String getUidFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_APPINFO, Context.MODE_PRIVATE);
        return pref.getString(APPINFO_UID, null);
    }

    private static String createUid(Context context) {
        String uid = getImei(context)+ System.currentTimeMillis();
        return uid;
    }

    public static String getImei(Context context) {
        String imei = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        if (imei == null) {
            imei = "";
        }
        return imei;
    }

    public static void setUidToPref(Context context, String uid) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_APPINFO, Context.MODE_PRIVATE);
        pref.edit().putString(APPINFO_UID, uid).commit();
    }

    public static void saveAppInfoToCacheFile(final Context context, final String uid, final String channelId) {
        Thread thread = new Thread(
                new Runnable(){
                    @Override
                    public void run() {
                        String filePath = getAppInfoPath(context);
                        FileOutputStream fis = null;
                        try {
                            File file = new File(filePath);
                            fis = new FileOutputStream(file);
                            JSONObject json = new JSONObject();
                            json.put(APPINFO_UID, uid);
                            json.put(APPINFO_CHANNELID, channelId);
                            fis.write(json.toString().getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally{
                            try {
                                if(fis != null ){
                                    fis.close();
                                }
                            } catch (Exception e2) {
                            }
                        }
                    }
                });
        thread.start();
    }

    public static NetworkInfo getCurrNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    public static String getChannelId(Context context) {
        if(TextUtils.isEmpty(gChannelId)) {
            String channelId = loadChannelIdFromCacheFile(context);
            if(channelId == null) {
                channelId = getChannelIdFromPref(context);
                if(channelId == null) {
                    gChannelId = createChannelId(context);
                    setChannelIdToPref(context, gChannelId);
                    saveAppInfoToCacheFile(context, gUid, gChannelId);
                }else {
                    gChannelId = channelId;
                }
            }
            else {
                gChannelId = channelId;
            }
        }
        return gChannelId;
    }

    public static String loadChannelIdFromCacheFile(Context context) {
        String channelId = null;
        String filePath = getAppInfoPath(context);
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            byte b[]=new byte[(int)file.length()];
            fis.read(b);
            JSONObject json = new JSONObject(new String(b));
            channelId = json.getString(APPINFO_CHANNELID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e2) {
            }
        }
        return channelId;
    }

    private static String createChannelId(Context context){
        String Result="";
        try {
            InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open("bid.txt") );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            while((line = bufReader.readLine()) != null)
                Result += line;

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(Result) || Result.equalsIgnoreCase("$bid"))
            Result = "1";
        return Result;
    }

    public static void setChannelIdToPref(Context context, String channelId) {
        SharedPreferences pref = context.getSharedPreferences(
                PREF_APPINFO, Context.MODE_PRIVATE);
        pref.edit().putString(APPINFO_CHANNELID, channelId).commit();
    }

    public static boolean reqSilenceInstall(Context context, String filepath) {
        if (TextUtils.isEmpty(filepath))
            return false;
        File file = new File(filepath);
        ShellUtils.CommandResult result = ShellUtils.execSuperUserCommand("pm install -r " + file.getAbsolutePath());
        return result.result == 0 && "Success".equalsIgnoreCase(result.successMsg);
    }

    public static void mergeApk(Context context, final DownloadTask task, final String oldApkFilePath,final String newApkFilePath
            , final String patchFilePath, final String tempNewFilePath) {

        if(task != null) {
            File oldFile = new File(oldApkFilePath);
            File patchFile = new File(patchFilePath);
            if(oldFile.exists() && patchFile.exists())
                Utils.AsyncTaskExecute(new ApkMergeAsycTask(context), task, oldApkFilePath, newApkFilePath, patchFilePath, tempNewFilePath);
        }

    }

    public static class ApkMergeAsycTask extends AsyncTask<Object, Object, Object> {

        private Context mContext;
        public ApkMergeAsycTask(Context context) {
            mContext = context;
        }

        @Override
        protected Object doInBackground(Object... params) {
            DownloadTask task = null;
            try {
                task = (DownloadTask) params[0];
                String oldApkFilePath = (String)params[1];
                String newApkFilePath = (String)params[2];
                String patchFilePath = (String)params[3];
                String tempNewFilePath = (String)params[4];
                while(isMerging())
                    Thread.sleep(100);
                setMergeFlag(true);
                Log.d("demo", "mergeApk start");
                Log.d("demo", "mergeApk oldApkFilePath = "+oldApkFilePath);
                Log.d("demo", "mergeApk newApkFilePath = "+newApkFilePath);
                Log.d("demo", "mergeApk patchFilePath = "+patchFilePath);
                Log.d("demo", "mergeApk tempNewFilePath = "+tempNewFilePath);

                //test
                long time1 = System.currentTimeMillis();
                ZipMerge zipMerge = new ZipMerge();
                File newFile = new File(newApkFilePath);
                if(newFile.exists())
                    newFile.delete();
                zipMerge.mergePatchByBuffer(oldApkFilePath, patchFilePath,newApkFilePath);
                long time2 = System.currentTimeMillis() - time1;
                Log.d("demo", "mergeApk end meregetime = "+ String.valueOf(time2));
                {

                    File patchFile = new File(patchFilePath);
                    if(patchFile.exists())
                        patchFile.delete();
                    task.mTotal = newFile.length();
                    task.mTransfered = task.mTotal;
                    task.mLocalPath = newApkFilePath;
                    task.mStatus = TaskStatus.STATUS_DOWNLOAD;
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return task;
        }

        @Override
        protected void onPostExecute(Object result) {
            setMergeFlag(false);
            if (result == null)
                return;

            DownloadTask task = (DownloadTask) result;
            if(task.mStatus == TaskStatus.STATUS_DOWNLOAD) {
                if (AppSettings.isRootInstall(mContext) && Utils.isPhoneRoot()) {
                    task.mStatus = TaskStatus.STATUS_INSTALLING;
                    Utils.AsyncTaskExecute(new DownloadControl.SilenceInstall(mContext), task);
                } else if(AppSettings.isAutoInstall(mContext)) {
                    task.mStatus = TaskStatus.STATUS_DOWNLOAD;
                    Utils.reqSystemInstall(mContext, task.mLocalPath);
                }
            }
            if(DownloadService.getDownloadControl() != null)
                DownloadService.getDownloadControl().onTaskStatusChanged(task);
        }
    }

    private static boolean gIsMerging = false;
    private static synchronized boolean isMerging() {
        return gIsMerging;
    }
    private static synchronized void setMergeFlag(boolean flag) {
        gIsMerging = flag;
    }

    @Deprecated
    public static void handleButtonProgress(DownStatusButton downButton, DownloadTask task) {
        if(downButton != null) {
            Resources res = BaseApplication.getInstance().getContext().getResources();
            int status = task == null ? TaskStatus.STATUS_UNKNOWN : task.mStatus;
            String statusText = null;
            int statusTextColor = 0;
            Drawable background = null;
            Drawable progressDrawable = null;
            int progress = (int)(task.mTransfered * 100f / task.mTotal);
            switch (status) {
                case TaskStatus.STATUS_DOWNLOAD:
                    progressDrawable = null;
                    statusText = res.getString(R.string.install);
                    statusTextColor = res.getColor(R.color.green_bg_text_color);
                    background = res.getDrawable(R.drawable.btn_green_bg);
                    break;
                case TaskStatus.STATUS_INSTALLING:
                    statusText = res.getString(R.string.installing);
                    statusTextColor = res.getColor(R.color.green_bg_text_color);
                    background = res.getDrawable(R.drawable.btn_green_bg);
                    break;
                case TaskStatus.STATUS_DOWNLOADING:
                case TaskStatus.STATUS_WAIT:
                    statusText = progress + "%";
                    statusTextColor = res.getColor(R.color.green_bg_text_color);
                    background = res.getDrawable(R.drawable.btn_green_bg);
                    progressDrawable = res.getDrawable(R.drawable.btn_green_progress_bg);
                    break;
                case TaskStatus.STATUS_FAILED:
                case TaskStatus.STATUS_PAUSE:
                    statusText = res.getString(R.string.pausing);
                    statusTextColor = res.getColor(R.color.gray_bg_text_color);
                    background = res.getDrawable(R.drawable.btn_gray_bg);
                    progressDrawable = res.getDrawable(R.drawable.btn_gray_progress_bg);
                    break;
                case TaskStatus.STATUS_UNKNOWN:
                default:
                    progressDrawable = null;
                    break;
            }

            downButton.setText(statusText);
            downButton.setTextColor(statusTextColor);
            downButton.setProgress(progress);
            downButton.setProgressVisible(progressDrawable != null);
            downButton.setBackgroundDrawable(background);
            downButton.setProgressDrawable(progressDrawable);
        }
    }

    public static void openPackage(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null)
            return;
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void jumpSystemAppManageActivity(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) { }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            String typeName = info.getTypeName();
            if (!TextUtils.isEmpty(typeName)) {
                return typeName.toLowerCase(Locale.getDefault()).equals(WIFI) && info.isAvailable();
            }
        }

        return false;
    }

    public static void jumpNetworkSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) { }
    }

    public static void startLoadingAnimation(View layoutLoading) {
        layoutLoading.setVisibility(View.VISIBLE);
        ImageView imageLoading = (ImageView)layoutLoading.findViewById(R.id.progress_bar);
        AnimationDrawable animationDrawable = (AnimationDrawable)imageLoading.getDrawable();
        animationDrawable.start();
    }

    public static void stopLoadingAnimation(View layoutLoading) {
        layoutLoading.setVisibility(View.GONE);
        ImageView imageLoading = (ImageView)layoutLoading.findViewById(R.id.progress_bar);
        AnimationDrawable animationDrawable = (AnimationDrawable)imageLoading.getDrawable();
        animationDrawable.stop();
    }

    public static String getTotalUpdateSizeString(long size) {
        String text = "全部更新";
        if(size <= 0)
            return text+"0B";

        if (size < 1024 * 1024) {
            return text+new DecimalFormat("#,##0.#").format(size / 1024f) + "K";
        } else if(size < 1024 * 1024 *1024){
            return text+new DecimalFormat("#,##0.##").format(size / 1024f / 1024) + "M";
        } else {
            return text+new DecimalFormat("#,##0.##").format(size / 1024f / 1024 /1024) + "G";
        }
    }

    public static void handleButtonProgress(View parentView, int viewid, DownloadTask task) {
        if(parentView == null) return;
        View view = parentView.findViewWithTag(task.mPackageName);
        if (view != null && (view instanceof ImageView)) {
            DownStatusButton downButton = (DownStatusButton)((View)(view.getParent())).findViewById(viewid);
            if(downButton != null) {
                handleButtonProgress(downButton, task);
            }
        }
    }

    public String getSelfApkPath(Context context) {
        List<ApplicationInfo> installList = context.getPackageManager
                ().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int i = 0; i < installList.size(); i++) {
            ApplicationInfo info=installList.get(i);
            if(info.packageName.equals(context.getPackageName())){
                Log.i("jw", "publicdir:"+info.publicSourceDir
                        +",sourcedir:"+info.sourceDir);
                return info.sourceDir;
            }
        }
        return null;
    }

    private boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已经安装程序的包信息（签名）
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pNameList中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目录程序的包名，有TRUE,没有FALSE
        return packageNames.contains(packageName);
    }
}
