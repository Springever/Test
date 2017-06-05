package com.example.test.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.application.BaseApplication;
import com.example.test.bean.AppUpdate;
import com.example.test.utils.common.Utils;
import com.example.test.widget.button.DownStatusButton;
import com.example.test.widget.expandableTextView.ExpandableTextView;

import java.util.List;
import java.util.Locale;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final static String TAG="ExpandableListAdapter";

    public static final int GROUP_UPDATE = 0;//更新列表

    public static final int GROUP_IGNORE = 1;//忽略列表

    private static final int GROUP_COUNT = 2;

    private static final int CHILD_UPDATE = 0;

    private static final int CHILD_IGNORE = 1;

    private static final int CHILD_TYPE_COUNT = 2;

    private List<AppUpdate> mUpdates;

    private List<AppUpdate> mIgnores;

    private LayoutInflater mInflater;

    private Callback mCallback;

    private static Context context;

    private String mShowControlPackage;//本应用

    public interface Callback {

        public void onUpdate(UpdateInfoHolder updateInfo);

        public void onIgnore(AppUpdate item);

        public void onRemoveIgnore(AppUpdate item);
    }

    public ExpandableListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void registerCallback(Callback callback) {
        mCallback = callback;
    }

    public void setData(List<AppUpdate> updatelist, List<AppUpdate> ignorelist) {
        mUpdates = updatelist;
        mIgnores = ignorelist;
        checkmShowControlPackageExist(updatelist, ignorelist);
        notifyDataSetChanged();
    }

    private void checkmShowControlPackageExist(List<AppUpdate> updatelist, List<AppUpdate> ignorelist) {
        if(mShowControlPackage != null) {
            boolean bFind = false;
            for(AppUpdate update : updatelist) {
                if(update.mPackageName.equalsIgnoreCase(mShowControlPackage)) {
                    bFind = true;
                    break;
                }
            }
            for(AppUpdate ignore : ignorelist) {
                if(ignore.mPackageName.equalsIgnoreCase(mShowControlPackage)) {
                    bFind = true;
                    break;
                }
            }
            if(!bFind)
                mShowControlPackage = null;
        }
    }

    public List<AppUpdate> getData(int groupPosition) {
        List<AppUpdate> list = null;
        if (groupPosition == GROUP_UPDATE) {
            list = mUpdates;
        } else if (groupPosition == GROUP_IGNORE) {
            list = mIgnores;
        }
        return list;
    }

    @Override
    public int getGroupCount() {
        return GROUP_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return getChildrenCount(GROUP_UPDATE) + getChildrenCount(GROUP_IGNORE) <= 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == GROUP_UPDATE) {
            return mUpdates == null ? 0 : mUpdates.size();
        } else if (groupPosition == GROUP_IGNORE) {
            return mIgnores == null ? 0 : mIgnores.size();
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if (groupPosition == GROUP_UPDATE) {
            return CHILD_UPDATE;
        } else if (groupPosition == GROUP_IGNORE) {
            return CHILD_IGNORE;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int getChildTypeCount() {
        return CHILD_TYPE_COUNT;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == GROUP_UPDATE) {
            return mUpdates.get(childPosition);
        } else if (groupPosition == GROUP_IGNORE) {
            return mIgnores.get(childPosition);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 0x10000000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Resources res = parent.getResources();
        String formatString = null;
        if (groupPosition == GROUP_UPDATE) {
            formatString = res.getString(R.string.can_be_update);
        } else if (groupPosition == GROUP_IGNORE) {
            formatString = res.getString(R.string.already_ignored);
        } else {
            throw new RuntimeException();
        }

        int count = getChildrenCount(groupPosition);
        String title = String.format(Locale.getDefault(), formatString, count);
        convertView = buildUpdateGroupView(mInflater, groupPosition, title, isExpanded,
                convertView, parent);

        View emptyView = convertView.findViewById(R.id.empty_view);
        View contentView = convertView.findViewById(R.id.content_layout);

        if (groupPosition == GROUP_IGNORE) {
            emptyView.setVisibility(count <= 0 ? View.VISIBLE : View.GONE);
            contentView.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (groupPosition == GROUP_UPDATE) {
            return getUpdateView(groupPosition, childPosition, isLastChild, convertView, parent);
        } else if (groupPosition == GROUP_IGNORE) {
            return getIgnoreView(groupPosition, childPosition, isLastChild, convertView, parent);
        } else {
            throw new RuntimeException();
        }
    }

    private View getUpdateView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AppUpdate item = (AppUpdate) getChild(groupPosition, childPosition);
        convertView = buildUpdateItemView(mInflater, childPosition, item,
                item.mPackageName.equals(mShowControlPackage), convertView, parent);
        if (isLastChild) {
            convertView.findViewById(R.id.update_item_divider).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.update_item_divider).setVisibility(View.VISIBLE);
        }

        convertView.setOnClickListener(mOnItemClicked);
        UpdateItemViewHolder holder = (UpdateItemViewHolder) convertView.getTag();
        holder.mIgnoreButton.setOnClickListener(mOnIgnoreClicked);
        holder.mUpdateInfo.mDownButton.setOnClickListener(mOnUpdateClicked);
        return convertView;
    }

    private View getIgnoreView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final AppUpdate item = (AppUpdate) getChild(groupPosition, childPosition);
        convertView = buildIgnoreItemView(mInflater, groupPosition, item, item.mPackageName.equals(mShowControlPackage), convertView, parent);
        if (isLastChild) {
            convertView.findViewById(R.id.ignore_item_divider).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.ignore_item_divider).setVisibility(View.VISIBLE);
        }
        convertView.setOnClickListener(mOnItemClicked);
        IgnoreItemViewHolder holder = (IgnoreItemViewHolder) convertView.getTag();
        holder.mIgnoreInfo.mDownButton.setOnClickListener(mOnCancelIgnoreClicked);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private View.OnClickListener mOnItemClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "mOnItemClicked");
            Object tag = v.getTag();
            if (tag == null)
                return;

            if(tag instanceof UpdateItemViewHolder) {
                String packageName = ((UpdateItemViewHolder) tag).mPackageName;
                if (packageName.equals(mShowControlPackage)) {
                    mShowControlPackage = null;
                } else {
                    mShowControlPackage = packageName;
                }
                notifyDataSetChanged();
            } else if(tag instanceof IgnoreItemViewHolder) {
                String packageName = ((IgnoreItemViewHolder) tag).mPackageName;
                if (packageName.equals(mShowControlPackage)) {
                    mShowControlPackage = null;
                } else {
                    mShowControlPackage = packageName;
                }
                notifyDataSetChanged();
            }
        }
    };

    private View.OnClickListener mOnIgnoreClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                AppUpdate item = ((AppUpdate) v.getTag());
                mCallback.onIgnore(item);
            }
        }
    };

    private View.OnClickListener mOnUpdateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                UpdateInfoHolder item = ((UpdateInfoHolder) v.getTag());
                mCallback.onUpdate(item);
            }
        }
    };

    private View.OnClickListener mOnCancelIgnoreClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                Log.d(TAG,"mOnCancelIgnoreClicked");
                AppUpdate item = ((AppUpdate) v.getTag());
                mCallback.onRemoveIgnore(item);
            }
        }
    };

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

    public View buildUpdateItemView(LayoutInflater inflater, int position, AppUpdate item,
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
            holder.mPatchSize = (TextView) convertView.findViewById(R.id.text_patch_size);
            holder.mPatchLine = (ImageView) convertView.findViewById(R.id.image_patch_line);
            convertView.setTag(holder);
        } else {
            holder = (UpdateItemViewHolder) convertView.getTag();
        }
        Resources res = parent.getResources();
        Locale locale = Locale.getDefault();
        String localFormat = res.getString(R.string.local_version);
        String newFormat = res.getString(R.string.new_version);
        holder.mUpdateInfo.mItem = item;
        Uri uri = Uri.parse("package:" + item.mPackageName);
        holder.mUpdateInfo.mIcon.setScaleType(ImageView.ScaleType.CENTER);
        holder.mUpdateInfo.mIcon.setWillNotCacheDrawing(false);
        String scheme = uri.getScheme();
        PackageManager mPackageManager = context.getPackageManager();
        if (TextUtils.isEmpty(scheme))
            return null;
        if ("package".equals(scheme)) {
            try {
                int index = uri.toString().indexOf(":") + 1;
                String packageName = uri.toString().substring(index);
                Drawable iconDrawable = mPackageManager.getApplicationIcon(packageName);
                holder.mUpdateInfo.mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                holder.mUpdateInfo.mIcon.setImageDrawable(iconDrawable);
            } catch (Exception e) {
            }
        }
        holder.mPackageName = item.mPackageName;
        holder.mLabel.setText(item.mLabel);
        holder.mAppSize.setText(Utils.getSizeString(item.mFileSize));
        if (item.mHasPatch) {
            holder.mPatchSize.setText(Utils.getSizeString(item.mPatchSize));
            holder.mPatchSize.setVisibility(View.VISIBLE);
            holder.mPatchLine.setVisibility(View.VISIBLE);
        } else {
            holder.mPatchSize.setVisibility(View.GONE);
            holder.mPatchLine.setVisibility(View.GONE);
        }
        String oldVersion = String.format(locale, localFormat, item.mLocalVersion);
        String newVersion = String.format(locale, newFormat, item.mVersion);
        if (!oldVersion.equalsIgnoreCase(newVersion))
            holder.mNewVersion.setText(oldVersion + " → " + newVersion);
        else {
            PackageManager pm = BaseApplication.getInstance().getContext().getPackageManager();
            try {
                PackageInfo info = pm.getPackageInfo(item.mPackageName, 0);
                int versionCode = info.versionCode;
                holder.mNewVersion.setText(oldVersion + "_" + versionCode + " → " + newVersion + "_" + item.mVersionCode);
            } catch (PackageManager.NameNotFoundException e) {
                holder.mNewVersion.setText(oldVersion + " → " + newVersion + "_" + item.mVersionCode);
            }
        }
        String strUpdateInfo = item.mUpdateInfo;
        if (TextUtils.isEmpty(strUpdateInfo)) {
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
        return convertView;
    }

    public static View buildIgnoreItemView(LayoutInflater inflater, int position, AppUpdate item,
                                           boolean isExpand, View convertView, ViewGroup parent) {
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
        if (TextUtils.isEmpty(descFormat)) {
            descFormat = res.getString(R.string.update_description_empty);
        }
        Uri uri = Uri.parse("package:" + item.mPackageName);
        holder.mIgnoreInfo.mIcon.setScaleType(ImageView.ScaleType.CENTER);
        holder.mIgnoreInfo.mIcon.setWillNotCacheDrawing(false);
        String scheme = uri.getScheme();
        PackageManager mPackageManager = context.getPackageManager();
        if (TextUtils.isEmpty(scheme))
            return null;
        if ("package".equals(scheme)) {
            try {
                int index = uri.toString().indexOf(":") + 1;
                String packageName = uri.toString().substring(index);
                Drawable iconDrawable = mPackageManager.getApplicationIcon(packageName);
                holder.mIgnoreInfo.mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                holder.mIgnoreInfo.mIcon.setImageDrawable(iconDrawable);
            } catch (Exception e) {
            }
        }
        holder.mPackageName = item.mPackageName;
        holder.mLabel.setText(item.mLabel);
        holder.mAppSize.setText(Utils.getSizeString(item.mFileSize));
        holder.mNewVersion.setText(String.format(locale, localFormat, item.mLocalVersion) + " → " +
                String.format(locale, newFormat, item.mVersion));
        String strUpdateInfo = item.mUpdateInfo;
        if (TextUtils.isEmpty(strUpdateInfo)) {
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
        return convertView;
    }

    public static class UpdateGroupViewHolder {
        public View mContentLayout;
        public View mEmptyLayout;
        public TextView mTitle;
    }

    public static class UpdateInfoHolder {
        public AppUpdate mItem;
        public ImageView mIcon;
        public DownStatusButton mDownButton;
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
