package com.example.test.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;

import com.example.test.activity.BaseActivity;

public class ActivityTack {

	public List<Activity> activityList = new ArrayList<Activity>();

	public static ActivityTack tack = new ActivityTack();

	public static ActivityTack getInstanse() {
		return tack;
	}

	private ActivityTack() {

	}

	public BaseActivity getBaseActivity() {
		if (activityList != null && activityList.size() > 0) {
			return (BaseActivity) activityList.get(activityList.size() - 1);
		}
		return null;
	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void removeActivity(Activity activity) {
		if (activity != null) {
			activityList.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	public void removeAllActivity() {
		while (activityList.size() > 0) {
			popActivity(activityList.get(activityList.size() - 1));
		}
	}

	/**
	 * 完全退出
	 * 
	 * @param context
	 */
	public void exit() {
		while (activityList.size() > 0) {
			popActivity(activityList.get(activityList.size() - 1));
		}
		// 结束进程
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * 根据class name获取activity
	 * 
	 * @param name
	 * @return
	 */
	public Activity getActivityByClassName(String name) {
		for (Activity ac : activityList) {
			if (ac.getClass().getName().indexOf(name) >= 0) {
				return ac;
			}
		}
		return null;
	}

	public Activity getActivityByClass(Class cs) {
		for (Activity ac : activityList) {
			if (ac.getClass().equals(cs)) {
				return ac;
			}
		}
		return null;
	}

	/**
	 * 弹出activity
	 * 
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		removeActivity(activity);
		activity.finish();
		activity = null;
	}

	/**
	 * 弹出activity到
	 * 
	 * @param cs
	 */
	@SuppressWarnings("rawtypes")
	public void popUntilActivity(Class... cs) {
		List<Activity> list = new ArrayList<Activity>();
		for (int i = activityList.size() - 1; i >= 0; i--) {
			Activity ac = activityList.get(i);
			boolean isTop = false;
			for (int j = 0; j < cs.length; j++) {
				if (ac.getClass().equals(cs[j])) {
					isTop = true;
					break;
				}
			}
			if (!isTop) {
				list.add(ac);
			} else
				break;
		}
		for (Iterator<Activity> iterator = list.iterator(); iterator.hasNext();) {
			Activity activity = iterator.next();
			popActivity(activity);
		}
	}
}
