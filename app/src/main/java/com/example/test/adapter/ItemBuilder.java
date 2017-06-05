package com.example.test.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.application.BaseApplication;
import com.example.test.bean.AppUpdate;
import com.example.test.utils.common.Utils;
import com.example.test.utils.device.LocalApps;
import com.example.test.utils.image.ImageLoader;
import com.example.test.widget.button.DownStatusButton;
import com.example.test.widget.expandableTextView.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 此Builder用于构造通用列表项
 *  
 *
 */
public class ItemBuilder {

	private static final String DATA_PATTERN = "yyyy-MM-dd";

	/*
	 * 本地软件项
	 */
	public static View buildLocalAppView(LayoutInflater inflater, int position, LocalApps.LocalAppInfo item,
										 View convertView, ViewGroup parent) {
		LocalAppViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_local_app, null);
			holder = new LocalAppViewHolder();
			holder.mIcon = (ImageView) convertView.findViewById(R.id.icon);
			holder.mUninstallButton = (Button) convertView.findViewById(R.id.uninstall_button);
			holder.mLabel = (TextView) convertView.findViewById(R.id.label);
			holder.mDescription = (TextView) convertView.findViewById(R.id.desciprtion);
			holder.mInstDate = (TextView) convertView.findViewById(R.id.install_time);
			convertView.setTag(holder);
		} else {
			holder = (LocalAppViewHolder) convertView.getTag();
		}

		Context context = parent.getContext();
		Locale locale = Locale.getDefault();
		ImageLoader.getInstance().loadImage(ImageLoader.wrapPackageUriString(item.mPackageName), holder.mIcon);
		SimpleDateFormat dataFormat = new SimpleDateFormat(DATA_PATTERN, locale);
		String sizeFormatter = context.getString(R.string.soft_used_space);
		String apkSize = Utils.getSizeString(item.mAppSize);
		String instDataFormatter = context.getString(R.string.install_time);
		String instData = dataFormat.format(item.mLastUpdateTime);
		
		holder.mUninstallButton.setTag(item);
		holder.mLabel.setText(item.mAppLabel);
		holder.mDescription.setText(String.format(locale, sizeFormatter, apkSize));
		holder.mInstDate.setText(String.format(locale, instDataFormatter, instData));
		
		return convertView;
	}

	public static View buildUpdateGroupView(LayoutInflater inflater, int position, String title,
											boolean isExpanded, View convertView, ViewGroup parent) {
		UpdateGroupViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_update_group, null);
			holder = new UpdateGroupViewHolder();
			holder.mContentLayout = convertView.findViewById(R.id.content_layout);
			holder.mEmptyLayout = convertView.findViewById(R.id.empty_view);
			holder.mTitle = (TextView) convertView.findViewById(R.id.group_title);
			convertView.setTag(holder);
		} else {
			holder = (UpdateGroupViewHolder) convertView.getTag();
		}

		holder.mTitle.setText(title);
		return convertView;
	}

	/**
	 * 可更新列表项
	 */
	public static View buildUpdateItemView(LayoutInflater inflater, int position, AppUpdate item,
										   boolean isExpand, View convertView, ViewGroup parent) {
		UpdateItemViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_update, null);
			holder = new UpdateItemViewHolder();
			holder.mUpdateInfo = new UpdateInfoHolder();
			holder.mUpdateInfo.mIcon = (ImageView) convertView.findViewById(R.id.icon);
			holder.mUpdateInfo.mDownButton = (DownStatusButton) convertView.findViewById(R.id.update_button);
			holder.mLabel = (TextView) convertView.findViewById(R.id.label);
			holder.mAppSize = (TextView) convertView.findViewById(R.id.text_app_size);
			holder.mNewVersion = (TextView) convertView.findViewById(R.id.new_version);
			holder.mIgnoreButton = (TextView) convertView.findViewById(R.id.ignore_button);
			holder.mUpdateView = (ExpandableTextView) convertView.findViewById(R.id.update_info_view);

			holder.mPatchSize = (TextView)convertView.findViewById(R.id.text_patch_size);
			holder.mPatchLine = (ImageView)convertView.findViewById(R.id.image_patch_line);
			convertView.setTag(holder);
		} else {
			holder = (UpdateItemViewHolder) convertView.getTag();
		}

		Resources res = parent.getResources();
		Locale locale = Locale.getDefault();
		String localFormat = res.getString(R.string.local_version);
		String newFormat = res.getString(R.string.new_version);
		holder.mUpdateInfo.mItem = item;
		ImageLoader.getInstance().loadImage(ImageLoader.wrapPackageUriString(item.mPackageName), holder.mUpdateInfo.mIcon);
		holder.mPackageName = item.mPackageName;
		holder.mLabel.setText(item.mLabel);
		holder.mAppSize.setText(Utils.getSizeString(item.mFileSize));
		if(item.mHasPatch) {
			holder.mPatchSize.setText(Utils.getSizeString(item.mPatchSize));
			holder.mPatchSize.setVisibility(View.VISIBLE);
			holder.mPatchLine.setVisibility(View.VISIBLE);
		} else {
			holder.mPatchSize.setVisibility(View.GONE);
			holder.mPatchLine.setVisibility(View.GONE);
		}
		String oldVersion = String.format(locale, localFormat, item.mLocalVersion);
		String newVersion = String.format(locale, newFormat, item.mVersion);
		if(!oldVersion.equalsIgnoreCase(newVersion))
			holder.mNewVersion.setText(oldVersion + " → " + newVersion);
		else {
			PackageManager pm = BaseApplication.getInstance().getContext().getPackageManager();
			try {
				PackageInfo info = pm.getPackageInfo(item.mPackageName, 0);
				int versionCode = info.versionCode;
				holder.mNewVersion.setText(oldVersion+"_"+ versionCode + " → " + newVersion+"_"+item.mVersionCode);
			} catch (PackageManager.NameNotFoundException e) {
				holder.mNewVersion.setText(oldVersion + " → " + newVersion+"_"+item.mVersionCode);
			}
		}
		String strUpdateInfo = item.mUpdateInfo;
		if(TextUtils.isEmpty(strUpdateInfo)) {
			strUpdateInfo = res.getString(R.string.update_description_empty);
		}
		holder.mUpdateView.setChangeLevel(item.isImportantUpdate());
		holder.mUpdateView.setUpdateTime(item.mUpdateTime);
		holder.mIgnoreButton.setTag(item);
		Utils.scaleClickRect(holder.mIgnoreButton);
		holder.mUpdateView.setViewButton(holder.mIgnoreButton);

		if (TextUtils.isEmpty(strUpdateInfo)) {
			holder.mUpdateView.setVisibility(View.GONE);
		} else {
			holder.mUpdateView.setText(strUpdateInfo);
			holder.mUpdateView.setExpand(isExpand);
			holder.mUpdateView.setVisibility(View.VISIBLE);
		}

		bindUpdateButton(holder.mUpdateInfo);
		return convertView;
	}

	/**
	 * 已忽略软件项
	 */
	public static View buildIgnoreItemView(LayoutInflater inflater, int position, AppUpdate item,
										   boolean isExpand,View convertView, ViewGroup parent) {
		IgnoreItemViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_ignore, null);
			holder = new IgnoreItemViewHolder();
			holder.mIgnoreInfo = new UpdateInfoHolder();
			holder.mIgnoreInfo.mItem = item;
			holder.mIgnoreInfo.mIcon = (ImageView) convertView.findViewById(R.id.icon);
			holder.mIgnoreInfo.mDownButton = (DownStatusButton) convertView.findViewById(R.id.update_button);
			holder.mLabel = (TextView) convertView.findViewById(R.id.label);
			holder.mAppSize = (TextView) convertView.findViewById(R.id.text_app_size);
			holder.mNewVersion = (TextView) convertView.findViewById(R.id.new_version);
			holder.mIgnoreCancelButton = (TextView) convertView.findViewById(R.id.cancel_ignore_button);
			holder.mIgnoreView = (ExpandableTextView) convertView.findViewById(R.id.ignore_info_view);
			convertView.setTag(holder);
		} else {
			holder = (IgnoreItemViewHolder) convertView.getTag();
		}

		Resources res = parent.getResources();
		Locale locale = Locale.getDefault();
		String localFormat = res.getString(R.string.local_version);
		String newFormat = res.getString(R.string.new_version);
		String descFormat = res.getString(R.string.update_description);
		if(TextUtils.isEmpty(descFormat)) {
			descFormat = res.getString(R.string.update_description_empty);
		}
		ImageLoader.getInstance().loadImage(ImageLoader.wrapPackageUriString(item.mPackageName), holder.mIgnoreInfo.mIcon);
		holder.mPackageName = item.mPackageName;
		holder.mLabel.setText(item.mLabel);
		holder.mAppSize.setText(Utils.getSizeString(item.mFileSize));
		holder.mNewVersion.setText(String.format(locale, localFormat, item.mLocalVersion) + " → " +
				String.format(locale, newFormat, item.mVersion));
		String strUpdateInfo = item.mUpdateInfo;
		if(TextUtils.isEmpty(strUpdateInfo)) {
			strUpdateInfo = res.getString(R.string.update_description_empty);
		}
		holder.mIgnoreView.setChangeLevel(item.isImportantUpdate());
		holder.mIgnoreView.setUpdateTime(item.mUpdateTime);
		holder.mIgnoreCancelButton.setTag(item);
		holder.mIgnoreInfo.mDownButton.setTag(item);
		if (TextUtils.isEmpty(strUpdateInfo)) {
			holder.mIgnoreView.setVisibility(View.GONE);
		} else {
			holder.mIgnoreView.setText(strUpdateInfo);
			holder.mIgnoreView.setExpand(isExpand);
			holder.mIgnoreView.setVisibility(View.VISIBLE);
		}
//		bindUpdateButton(holder.mIgnoreInfo);
		return convertView;
	}


	@Deprecated
	private static void bindUpdateButton(UpdateInfoHolder updateInfo) {
		/*
		Button button = updateInfo.mDownButton;
		AppUpdate au = updateInfo.mItem;

		if(updateInfo.mIcon != null) {
			DownloadTask task = DataCenter.getInstance().getTask(au.mPackageName);
			if(task != null) {
				updateInfo.mIcon.setTag(task.mPackageName);
				if(updateInfo.mDownButton != null)
					Utils.handleButtonProgress(updateInfo.mDownButton, task);
			} else {
				updateInfo.mIcon.setTag("");
			}
		}
		button.setTag(updateInfo);
		int instStatus = au.getInstStatus();
		int taskStatus = au.getTaskStatus();

		button.setEnabled(taskStatus != TaskStatus.STATUS_INSTALLING && taskStatus != TaskStatus.STATUS_MERGING);

		if (instStatus == LocalApps.STATUS_INSTALLED) {
			button.setText(R.string.open);
			button.setTextColor(gray_bg_text_color);
			button.setBackgroundResource(R.drawable.btn_gray_bg);
			return;
		}

		switch (taskStatus) {
			case TaskStatus.STATUS_DOWNLOAD:
				button.setText(R.string.install);
				button.setTextColor(green_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_green_bg);
				break;
			case TaskStatus.STATUS_DOWNLOADING:
				String  buttonText = button.getText().toString();
				if(buttonText != null && buttonText.contains("%")) {
					//如果已经显示进度，则不做处理
				} else {
					button.setText(R.string.pause);
					button.setTextColor(green_bg_text_color);
					button.setBackgroundResource(R.drawable.btn_green_bg);
				}
				break;
			case TaskStatus.STATUS_FAILED:
				button.setText(R.string.retry);
				button.setTextColor(green_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_green_bg);
				break;
			case TaskStatus.STATUS_INSTALLING:
				button.setText(R.string.installing);
				button.setTextColor(green_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_green_bg);
				break;
			case TaskStatus.STATUS_MERGING:
				button.setText(R.string.merge);
				button.setTextColor(green_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_green_bg);
				break;
			case TaskStatus.STATUS_PAUSE:
				button.setText(R.string.continue_down);
				button.setTextColor(gray_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_gray_bg);
				break;
			case TaskStatus.STATUS_WAIT:
				button.setText(R.string.pause);
				button.setTextColor(green_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_green_bg);
				break;
			default:
				Utils.clearButtonProgress(updateInfo.mDownButton);
				button.setText(R.string.update);
				button.setTextColor(green_bg_text_color);
				button.setBackgroundResource(R.drawable.btn_green_bg);
				break;
		}
		*/
	}

	public static class LocalAppViewHolder {
		public ImageView mIcon;
		public Button mUninstallButton;
		public TextView mLabel;
		public TextView mDescription;
		public TextView mInstDate;
	}

	public static class UpdateInfoHolder {
		public AppUpdate mItem;
		public ImageView mIcon;
		public DownStatusButton mDownButton;
	}

	public static class UpdateGroupViewHolder {
		public View mContentLayout;
		public View mEmptyLayout;
		public TextView mTitle;
	}

	public static class UpdateItemViewHolder {
		public String mPackageName;
		public TextView mLabel;
		public TextView mAppSize;
		public TextView mNewVersion;
		public UpdateInfoHolder mUpdateInfo;
		public TextView mIgnoreButton;
		public ExpandableTextView mUpdateView;

		public TextView mPatchSize;
		public ImageView mPatchLine;
	}

	public static class IgnoreItemViewHolder {
		public String mPackageName;
		public TextView mLabel;
		public TextView mAppSize;
		public TextView mNewVersion;
		public UpdateInfoHolder mIgnoreInfo;
		public TextView mIgnoreCancelButton;
		public ExpandableTextView mIgnoreView;
	}
}
