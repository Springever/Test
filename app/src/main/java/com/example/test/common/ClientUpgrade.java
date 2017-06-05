package com.example.test.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.test.R;
import com.example.test.data.CheckForUpdate;
import com.example.test.data.DataCenter;
import com.example.test.data.IDataCallback;
import com.example.test.data.IDataConstant;

public class ClientUpgrade {

	/**
	 * 
	 * @param context
	 * @param callback
	 * @return
	 */
	public static boolean startDailyQuery(Context context, IDataCallback callback) {
		long currTime = System.currentTimeMillis();
		long lastTime = AppSettings.getClientUpdateQueryTime(context);
		
		boolean satisfiedDailyUpdate = currTime < lastTime || currTime - lastTime > Constants.DAILY_MILLSECONDS;

		if (satisfiedDailyUpdate) {
			startQuery(context, callback, false);
			AppSettings.setClientUpdateQueryTime(context, System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}
	
	public static void queryCache(Context context, IDataCallback callback) {
		startQuery(context, callback, true);
	}
	
	public static boolean startWeeklyQuery(Context context, IDataCallback callback) {
		long currTime = System.currentTimeMillis();
		long lastTime = AppSettings.getBackgroundClientUpdateQueryTime(context);
		if (currTime < lastTime || currTime - lastTime > Constants.UPGRADE_MILLSECONDS) {
			startQuery(context, callback, false);
			AppSettings.setBackgroundClientUpdateQueryTime(context, System.currentTimeMillis());
			setNextUpdateAlarm(context);
			return true;
		} else {
			return false;
		}
	}

	public static void setNextUpdateAlarm(Context context) {
		long currTime = System.currentTimeMillis();
		long lastTime = AppSettings.getBackgroundClientUpdateQueryTime(context);
		long nextTime = lastTime + Constants.UPGRADE_MILLSECONDS;
		if (currTime > nextTime) {
			nextTime = 0;
		}
		Intent intent = new Intent(context, AppService.class);
		intent.setAction(AppService.ACTION_WEEKLY_CLIENT_QUERY_UPDATE);
		PendingIntent operation = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, nextTime, operation);
	}

	public static void startQuery(Context context, IDataCallback callback, boolean useCache) {
		int dataId = IDataConstant.CHECK_UPDATE;
		DataCenter.Options options = null;
		
		if (useCache) {
			options = new DataCenter.Options();
			options.mTryCache = true;
		}
		
		DataCenter.getInstance().requestDataAsync(context, dataId, callback, options);
	}

	public static void onBackgroundQueryCompleted(Context context, DataCenter.Response resp) {
		if (resp == null || !resp.mSuccess)
			return;
		if (resp.mData == null || !(resp.mData instanceof CheckForUpdate))
			return;

		CheckForUpdate checkUpdate = (CheckForUpdate)resp.mData;
		String title = context.getResources().getString(R.string.upgrade_notification_title);
		String text = checkUpdate.mChangeLog;
		NotificationMgr.showUpgradeNotification(context, title, text, checkUpdate);
	}
	
	public static void startClientQuery(Context context, IDataCallback callback) {
		int dataId = IDataConstant.CHECK_UPDATE;
		DataCenter.getInstance().requestDataAsync(context, dataId, callback, null);	
		AppSettings.setClientUpdateQueryTime(context, System.currentTimeMillis());
	}
}
