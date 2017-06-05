package com.example.test.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.adapter.AppUpdateAdapter;
import com.example.test.adapter.ItemBuilder;
import com.example.test.bean.AppUpdate;
import com.example.test.common.AppSettings;
import com.example.test.common.CommonInvoke;
import com.example.test.common.UpdateIgnore;
import com.example.test.common.UpdateQuery;
import com.example.test.data.DataCenter;
import com.example.test.data.IDataBase;
import com.example.test.data.IDataCallback;
import com.example.test.data.IDataConstant;
import com.example.test.data.UpdateInfo;
import com.example.test.download.DownloadService;
import com.example.test.download.DownloadTask;
import com.example.test.download.TaskList;
import com.example.test.download.TaskStatus;
import com.example.test.utils.common.Utils;
import com.example.test.utils.device.LocalApps;
import com.example.test.utils.image.ImageLoader;
import com.example.test.widget.pullToRefresh.PullToRefreshBase;
import com.example.test.widget.pullToRefresh.PullToRefreshExpandableListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * @authore WinterFellSo 2017/3/25
 * @purpose PullToRefresh
 */
public class PullToRefreshActivity extends BaseActivity implements Observer, IDataCallback, AppUpdateAdapter.Callback, View.OnClickListener {

    private PullToRefreshExpandableListView mListViewContainer;

    private ExpandableListView mListView;

    private AppUpdateAdapter mUpdateAdapter;

    private UpdateInfo mInfo;

    private Button mButtonUpdateAll;

    private boolean mRefresh = false;

    private boolean isDebug = true;

    @Override
    protected int getContentLayout() {
        return R.layout.expand_list_view;
    }

    @Override
    public void initGui() {
        mListViewContainer = (PullToRefreshExpandableListView) findViewById(R.id.ptr_listview);
        mButtonUpdateAll = (Button) findViewById(R.id.updateall_button);
    }

    @Override
    public void initAction() {
        mButtonUpdateAll.setOnClickListener(this);
        mListViewContainer.setOnRefreshListener(mOnRefreshListener);
    }

    @Override
    public void initData() {
        DataCenter.getInstance().ensureInit(this);
        DataCenter.getInstance().addObserver(this);
        ImageLoader.getInstance().initLoader(this);
        Utils.setupPTRLoadingProxy(getResources(), mListViewContainer.getLoadingLayoutProxy());
        mListView = mListViewContainer.getRefreshableView();
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mUpdateAdapter = new AppUpdateAdapter(this);
        mUpdateAdapter.registerCallback(this);
        mListView.setAdapter(mUpdateAdapter);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                mListView.expandGroup(groupPosition);
            }
        });
        mListView.expandGroup(AppUpdateAdapter.GROUP_UPDATE);
        mListView.expandGroup(AppUpdateAdapter.GROUP_IGNORE);
        requestData(true);
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonUpdateAll) {
            int nUpdateCount = mUpdateAdapter.getChildrenCount(AppUpdateAdapter.GROUP_UPDATE);
            for (int i = 0; i < nUpdateCount; i++) {
                AppUpdate appUpdate = (AppUpdate) mUpdateAdapter.getChild(AppUpdateAdapter.GROUP_UPDATE, i);
                DownloadTask task = getDownloadTaskByPkgname(appUpdate.mPackageName);
                if (task != null) {
                    if (task.mStatus == TaskStatus.STATUS_PAUSE || task.mStatus == TaskStatus.STATUS_FAILED) {
                        Intent service = new Intent(this, DownloadService.class);
                        service.setAction(DownloadService.ACTION_RESUME_TASK);
                        service.putExtra(DownloadService.EXTRA_TASK, task);
                        startService(service);
                    } else if (task.mStatus == TaskStatus.STATUS_INSTALLED) {
                        Intent service = new Intent(this, DownloadService.class);
                        service.setAction(DownloadService.ACTION_EXIST_UPDATE);
                        service.putExtra(DownloadService.EXTRA_TASK, task);
                        startService(service);
                    }
                } else {
                    task = new DownloadTask();
                    task = DownloadTask.buildNewTask(appUpdate.mPackageName, DownloadTask.SOURCE_UPDATE_LIST, appUpdate.mChannelId);
                    task.mTitle = appUpdate.mLabel;
                    task.mVersionCode = appUpdate.mVersionCode;
                    task.mVersionName = appUpdate.mVersion;
                    task.mTaskUrl = appUpdate.mDownloadPath;
                    task.mLocalPath = CommonInvoke.generateDownloadPath(this, appUpdate.mPackageName, appUpdate.mVersionCode, appUpdate.mVersion, task.mIsPatch);

                    Intent service = new Intent(this, DownloadService.class);
                    service.setAction(DownloadService.ACTION_START_TASK);
                    service.putExtra(DownloadService.EXTRA_TASK, task);

                    File file = new File(task.mLocalPath);
                    if (!CommonInvoke.ensureStorageSpaceEnough(this, file.getParent(), appUpdate.mFileSize))
                        break;
                    startService(service);
                }
            }
        }
    }

    private DownloadTask getDownloadTaskByPkgname(String pkgName) {
        DownloadTask downloadTask = null;
        TaskList taskList = DataCenter.getInstance().getTaskList();
        List<DownloadTask> downloadList = taskList.getTaskList();
        for (DownloadTask task : downloadList) {
            if (task.mPackageName.equalsIgnoreCase(pkgName)) {
                downloadTask = task;
                break;
            }
        }
        return downloadTask;
    }

    protected void onTaskProgress(DownloadTask task) {
        if (task == null || TextUtils.isEmpty(task.mPackageName))
            return;
        Utils.handleButtonProgress(this.getWindow().getDecorView(), R.id.update_button, task);
    }

    private void requestData(boolean allowCache) {
        if (!mRefresh)
            Utils.startLoadingAnimation(findViewById(R.id.loading_layout));
        if (!isDebug) {
            UpdateQuery.startQuery(this, this, allowCache);
        } else {
            AppSettings.setUpdateQueryTime(this, System.currentTimeMillis());
            DataCenter.getInstance().requestAllLocalPackage();
            onDataObtain(IDataConstant.UPDATE_INFO,getResponseDemo());
        }
    }

    @Override
    public void onDataObtain(int dataId, DataCenter.Response resp) {
        if (dataId != IDataConstant.UPDATE_INFO || !resp.mSuccess || resp.mData == null || !(resp.mData instanceof UpdateInfo)) {
            Utils.stopLoadingAnimation(findViewById(R.id.loading_layout));
            if (!mRefresh)
                showLoadingFailedLayout();
            else
                Toast.makeText(this, getResources().getString(R.string.update_refresh_error), Toast.LENGTH_SHORT).show();
            return;
        }
        UpdateQuery.onQuerySuccessful(this);
        findViewById(R.id.updateall_button).setVisibility(View.VISIBLE);
        findViewById(R.id.ptr_listview).setVisibility(View.VISIBLE);
        mInfo = (UpdateInfo) resp.mData;
        {
            Utils.stopLoadingAnimation(findViewById(R.id.loading_layout));
            mListViewContainer.onRefreshComplete();
            showData();

        }
    }

    protected void onNetworkStateChanged() {
        if (findViewById(R.id.loading_failed_layout).getVisibility() == View.VISIBLE)
            showLoadingFailedLayout();
    }

    private void showData() {
        if (mInfo == null)
            return;

        DataCenter dc = DataCenter.getInstance();
        Set<String> ignoreSet = UpdateIgnore.getIgnorePackageSet(this);
        List<AppUpdate> update = new ArrayList<AppUpdate>();
        List<AppUpdate> ignore = new ArrayList<AppUpdate>();
        if (mInfo.mUpdates != null) {
            for (AppUpdate au : mInfo.mUpdates) {
                int status = dc.getPackageInstallStatus(au.mPackageName, au.mVersionCode, au.mVersion);
                if (status != LocalApps.STATUS_INSTALLED_OLD_VERSION)
                    continue;
                if (ignoreSet.contains(au.mPackageName)) {
                    ignore.add(au);
                } else {
                    update.add(au);
                }

                DownloadTask task = dc.getTask(au.mPackageName);
                au.setInstStatus(dc.getPackageInstallStatus(au.mPackageName, au.mVersionCode, au.mVersion));
                au.setTaskStatus(task == null ? TaskStatus.STATUS_UNKNOWN : task.mStatus);
            }
        }
        mUpdateAdapter.setData(update, ignore);
        showAllUpdateText(update);
        checkAllUpdateVisible(update);

        int count = update.size();
        AppSettings.setUpdateCount(this, count);
        dc.reportUpdateCountChanged();
    }

    private long getUpdateAppSize(AppUpdate updateApp) {
        long updateAppSize = updateApp.mFileSize;
        int instStatus = updateApp.getInstStatus();
        int taskStatus = updateApp.getTaskStatus();
        if (instStatus == LocalApps.STATUS_INSTALLED) {
            updateAppSize = 0;
        }
        if (taskStatus != TaskStatus.STATUS_UNKNOWN && taskStatus != TaskStatus.STATUS_INSTALLED) {
            updateAppSize = 0;
        }

        return updateAppSize;
    }

    private long getUpdateAppSaveSize(AppUpdate updateApp) {
        long saveSize = 0;
        if (updateApp.mHasPatch && updateApp.mPatchSize > 0) {
            saveSize = (updateApp.mFileSize - updateApp.mPatchSize);
            int instStatus = updateApp.getInstStatus();
            int taskStatus = updateApp.getTaskStatus();
            if (instStatus == LocalApps.STATUS_INSTALLED) {
                saveSize = 0;
            }
            if (taskStatus != TaskStatus.STATUS_UNKNOWN && taskStatus != TaskStatus.STATUS_INSTALLED) {
                saveSize = 0;
            }
        }
        return saveSize;
    }

    private void showAllUpdateText(List<AppUpdate> updatelist) {
        long size = 0;
        long patchDiffSize = 0;
        for (AppUpdate au : updatelist) {
            size += getUpdateAppSize(au);
            if (au.mHasPatch && au.mPatchSize > 0)
                patchDiffSize += getUpdateAppSaveSize(au);
        }
        String text = Utils.getTotalUpdateSizeString(size - patchDiffSize) + (patchDiffSize > 0 ? "(共省" + Utils.getSizeString(patchDiffSize) + ")" : "");
        mButtonUpdateAll.setText(text);
    }

    private void checkAllUpdateVisible(List<AppUpdate> updatelist) {
        boolean bVisible = true;
        if (updatelist.size() == 0)
            bVisible = false;
        bVisible = true;
        for (AppUpdate appUpdate : updatelist) {
            boolean bFind = getUpdateAppSize(appUpdate) > 0 ? true : false;
            if (bFind) {
                bVisible = true;
                break;
            } else
                bVisible = false;
        }
        if (bVisible)
            mButtonUpdateAll.setVisibility(View.VISIBLE);
        else
            mButtonUpdateAll.setVisibility(View.GONE);
    }

    private void showLoadingFailedLayout() {
        View failedLayout = findViewById(R.id.loading_failed_layout);
        TextView resultText = (TextView) failedLayout.findViewById(R.id.failed_result);
        TextView tipText = (TextView) failedLayout.findViewById(R.id.failed_tip);
        Button failedButton = (Button) failedLayout.findViewById(R.id.failed_tip_button);
        failedLayout.setVisibility(View.VISIBLE);
        boolean hasNetwork = Utils.isNetworkAvailable(this);
        if (hasNetwork) {
            resultText.setText(R.string.network_not_good);
            tipText.setText(R.string.click_button_refresh_later);
            failedButton.setText(R.string.click_to_refresh);
            failedButton.setOnClickListener(mOnRefreshButtonClicked);
        } else {
            resultText.setText(R.string.network_not_connected);
            tipText.setText(R.string.click_button_setting_network);
            failedButton.setText(R.string.network_setting);
            failedButton.setOnClickListener(mOnSettingNetworkClicked);
        }
    }

    @Override
    public void onUpdate(ItemBuilder.UpdateInfoHolder updateInfo) {
        CommonInvoke.processUpdateBtn(this, updateInfo.mIcon, updateInfo.mItem);
    }

    @Override
    public void onIgnore(AppUpdate item) {
        UpdateIgnore.onIgnore(this, item);
        showData();
    }

    @Override
    public void onRemoveIgnore(AppUpdate item) {
        UpdateIgnore.onRemoveIgnore(this, item);
        showData();
    }

    protected void refreshAppData() {
        showData();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data == null || !(data instanceof Message))
            return;

        Message msg = (Message) data;
        switch (msg.what) {
            case DataCenter.MSG_LOCAL_APP_CHANGED:
                refreshAppData();
                break;
            case DataCenter.MSG_DOWN_EVENT_PROGRESS:
                if (msg.obj != null && msg.obj instanceof DownloadTask) {
                    onTaskProgress((DownloadTask) msg.obj);
                }
                break;
            case DataCenter.MSG_DOWN_EVENT_STATUS_CHANGED:
            case DataCenter.MSG_DOWN_EVENT_TASK_LIST_CHANGED:
                refreshAppData();
                break;
            case DataCenter.MSG_NET_STATE_CHANGED:
                onNetworkStateChanged();
                break;
        }
    }

    private View.OnClickListener mOnRefreshButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View failedLayout = findViewById(R.id.loading_failed_layout);
            failedLayout.setVisibility(View.GONE);
            requestData(true);
        }
    };

    private View.OnClickListener mOnSettingNetworkClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.jumpNetworkSetting(PullToRefreshActivity.this);
        }
    };

    private PullToRefreshBase.OnRefreshListener<ExpandableListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener<ExpandableListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
            mRefresh = true;
            requestData(false);
        }
    };


    private static final String NAME_CONTEXT = "context";
    private static final String NAME_ENTITIES = "entities";

    public DataCenter.Response getResponseDemo() {
        int dataId = IDataConstant.UPDATE_INFO;
        DataCenter.Response resp = null;
        JSONObject jsonObj = null;
        try {
            resp = new DataCenter.Response();
            byte[] bytes = Utils.readFromAsset(this, "preload/update.json");
            if (bytes != null) {
                jsonObj = new JSONObject(new String(bytes));
            } else {
                return null;
            }
            JSONObject jsonContext = jsonObj.optJSONObject(NAME_CONTEXT);
            if (jsonContext != null) {
                resp.mContext = new HashMap<String, String>();
                Iterator<String> iterator = jsonContext.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    resp.mContext.put(key, jsonContext.getString(key));
                }
            }
            JSONObject entities = jsonObj.optJSONObject(NAME_ENTITIES);
            if (entities != null) {
                resp.mData = convertEntitiesToData(dataId, entities);
            }
            resp.mSuccess = true;
        } catch (Exception e) {
        }
        return resp;
    }

    private IDataBase convertEntitiesToData(int dataId, JSONObject entities) throws JSONException {
        switch (dataId) {
            case IDataConstant.UPDATE_INFO:
                UpdateInfo info = new UpdateInfo();
                info.readFromJSON(entities);
                return info;
            default:
                return null;
        }
    }

    @Override
    public void onDestroy() {
        DataCenter.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAppData();
    }
}
