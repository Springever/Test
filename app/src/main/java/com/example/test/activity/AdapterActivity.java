package com.example.test.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ListView;

import com.example.test.R;
import com.example.test.adapter.ItemDataDef;
import com.example.test.adapter.LocalAppAdapter;
import com.example.test.data.DataCenter;
import com.example.test.utils.device.LocalApps;
import com.example.test.utils.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @authore WinterFellSo 2017/3/15
 * @purpose Adapter
 */
public class AdapterActivity extends Activity implements LocalAppAdapter.Callback, Observer {

    private static String TAG = "AdapterActivity";

    private ListView mListView;

    private LocalAppAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_manage_local);
        initGui();
        initAction();
        initData();
    }

    public void initGui() {
        mListView = (ListView) findViewById(R.id.list_view);
    }

    public void initAction() {

    }

    public void initData() {
        mAdapter = new LocalAppAdapter(this);
        mAdapter.registerCallback(this);
        mListView.setAdapter(mAdapter);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        DataCenter dataCenter=DataCenter.getInstance();
        dataCenter.ensureInit(this);
        ImageLoader.getInstance().initLoader(this);
        List<LocalApps.LocalAppInfo> localData = dataCenter.requestLocalData();
        buildLocalData(localData);
    }

    protected void refreshAppData() {
        List<LocalApps.LocalAppInfo> localData = DataCenter.getInstance().requestLocalData();
        buildLocalData(localData);
    }

    private void buildLocalData(List<LocalApps.LocalAppInfo> data) {
        if (data == null || data.size() <= 0)
            return;
        ArrayList<ItemDataDef.ItemDataWrapper> items = new ArrayList<ItemDataDef.ItemDataWrapper>();
        for (LocalApps.LocalAppInfo info : data) {
            items.add(new ItemDataDef.ItemDataWrapper(info, LocalAppAdapter.TYPE_LOCAL_APP));
        }
        mAdapter.setData(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUninstallClick(LocalApps.LocalAppInfo info) {
        if (info != null && !TextUtils.isEmpty(info.mPackageName)); {
            LocalApps.reqSystemUninstall(this, info.mPackageName);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        List<LocalApps.LocalAppInfo> localData = DataCenter.getInstance().requestLocalData();
        buildLocalData(localData);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DataCenter.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAppData();
    }
}
