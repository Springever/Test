package com.example.test.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

import com.example.test.R;
import com.example.test.data.DataCenter;
import com.example.test.download.DownloadService;
import com.example.test.download.DownloadTask;
import com.example.test.widget.dialog.CommonDialog;

import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver {
      
	public static boolean gReportWifi2MobileFlag = false;
	
    @Override  
    public void onReceive(Context context, Intent intent) {	
    	State wifiState = null;  
        State mobileState = null;  
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        Log.e("demo", "wifiState = "+wifiState);
        Log.e("demo", "mobileState = "+mobileState);
        boolean bConnect = false;
        if (wifiState != null && mobileState != null  
                && State.CONNECTED != wifiState  
                && State.CONNECTED == mobileState) {  
        	Log.d("demo", "mobile ok");
        	bConnect = true;
        	if(mRunningDownloadTaskList != null && mRunningDownloadTaskList.size() >0 && !gReportWifi2MobileFlag) {
        		gReportWifi2MobileFlag = true;
        		Log.d("demo", "report wifi_to_mobile");
        		DataCenter.getInstance().reportDownloadEvent(DataCenter.MSG_WIFI_TO_MOBILE_CHANGED_EVENT, null);
        	} 		
        } else if (wifiState != null && mobileState != null  
                && State.CONNECTED != wifiState  
                && State.CONNECTED != mobileState) {  
        	NetworkStateReceiver.captureRunningTaskList(context);
        	Log.d("demo", "all disconnect");
        } else if (wifiState != null && State.CONNECTED == wifiState) {  
        	Log.d("demo", "wifi ok");
        	bConnect = true;
        	if(mRunningDownloadTaskList != null && mRunningDownloadTaskList.size() >0) {
        		if(gDialog != null && gDialog.isShowing()) {
        			gReportWifi2MobileFlag = false;
        			gDialog.dismiss();
        		}		
        		resumeNetworkTaskList(context);
        	} 	
        }
        if(bConnect) {
			DataCenter dc = DataCenter.getInstance();
			dc.ensureInit(context);
			dc.reportNetStateChanged();
        }
    }
    
    public static void captureRunningTaskList(Context context) {
    	try {
        	if(mRunningDownloadTaskList == null) { 	
        		int runningCount = DataCenter.getInstance().getTaskList().getRunningAndWaitingTaskCount();
        		if (runningCount > 0) {
        			mRunningDownloadTaskList = DataCenter.getInstance().getTaskList().getRunningTaskList();
    				Intent service = new Intent(context, DownloadService.class);
    				service.setAction(DownloadService.ACTION_STOP_ALL_TASK);
    				context.startService(service);
        		}
        	}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static boolean hasCapture() {
    	return (mRunningDownloadTaskList!=null);
    }
    
    private static List<DownloadTask> mRunningDownloadTaskList;
    public static void resumeNetworkTaskList(Context context) {
    	List<DownloadTask> taskList = DataCenter.getInstance().getTaskList().getTaskList();
    	if(taskList == null) return;
		for(DownloadTask task : mRunningDownloadTaskList) {
			if(taskList.contains(task))
			{
				Intent service = new Intent(context, DownloadService.class);
				service.setAction(DownloadService.ACTION_RESUME_TASK);
				service.putExtra(DownloadService.EXTRA_TASK, task);
				context.startService(service);
			}
		}
		mRunningDownloadTaskList = null;
    }
      
    private static CommonDialog gDialog = null;
	public static void showNetworkChangeQueryDialog(final Context context) {
		if(mRunningDownloadTaskList != null) 
		{
			String message = "当前处于2G/3G网络，软件下载自动暂停，是否继续下载?";
			String title = "下载已暂停";
			
			final CommonDialog queryDialog = new CommonDialog(context);
			gDialog = queryDialog;
			queryDialog.setMessage(message);
			queryDialog.setTitle(title);
			queryDialog.setCheckBoxVisible(false);
			queryDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					gReportWifi2MobileFlag = false;
					mRunningDownloadTaskList = null;
				}
			});
			queryDialog.setPositiveButton(R.string.dialog_continue, false, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					gReportWifi2MobileFlag = false;
					resumeNetworkTaskList(context);
				}
			});
			queryDialog.show();
		}
	}
  
}
