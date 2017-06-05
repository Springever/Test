package com.example.test.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ExpandableListView;

import com.example.test.R;
import com.example.test.adapter.ExpandableListAdapter;
import com.example.test.bean.AppUpdate;
import com.example.test.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


//import java.util.Observable;

public class ExpandListViewActivity extends BaseActivity implements ExpandableListAdapter.Callback {

    private final static String TAG = "ExpandListViewActivity";

    private ExpandableListView mListView;

    private ExpandableListAdapter mUpdateAdapter;

    private static final String JSON_UPAPPITEMS = "upappitems";

    private static final String JSON_IGNOREAPPITEMS = "ignoreappitems";

    private static final String NAME_ENTITIES = "entities";

    public List<AppUpdate> mUpdates = new ArrayList<AppUpdate>();

    public List<AppUpdate> mIgnores = new ArrayList<AppUpdate>();

    private static final int VALUE_IGNORE = 1;

    private static String PREF_IGNORE = "ignore";

    public static final int STATUS_NOT_INSTALLED = 0;

    public static final int STATUS_INSTALLED = 1;

    public static final int STATUS_INSTALLED_OLD_VERSION = 2;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_expand;
    }

    @Override
    public void initGui() {
        mListView = (ExpandableListView) this.findViewById(R.id.ecpandable);
    }

    @Override
    public void initAction() {
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mUpdateAdapter = new ExpandableListAdapter(this);
        mUpdateAdapter.registerCallback(this);//注册回调函数
        mListView.setAdapter(mUpdateAdapter);
        mListView.setCacheColorHint(Color.TRANSPARENT);//点击时候不会变黑
        mListView.setGroupIndicator(null);//去掉左边图标
        mListView.expandGroup(ExpandableListAdapter.GROUP_UPDATE);//触发展开
        mListView.expandGroup(ExpandableListAdapter.GROUP_IGNORE);//触发展开
        /*
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {//折叠
            @Override
            public void onGroupCollapse(int groupPosition) {
                mListView.expandGroup(groupPosition);
            }
        });
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {//展开
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < mUpdateAdapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        mListView.collapseGroup(i);
                    }
                }
            }
        });
        */
    }

    @Override
    public void initData() {
        showData();
    }

    public void showData() {
        final Thread t = Thread.currentThread();
        Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(Subscriber<? super JSONObject> subscriber) {
                byte[] bytes = readFromAsset(ExpandListViewActivity.this, "preload/update.json");
                JSONObject jsonObj = null;
                if (bytes != null) {
                    try {
                        jsonObj = new JSONObject(new String(bytes));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onNext(jsonObj);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObj) {
                        JSONObject entities = jsonObj.optJSONObject(NAME_ENTITIES);
                        if (entities != null) {
                            try {
                                readFromJSON(entities);
                                //上次忽略更新的应用
                                SharedPreferences pref = getSharedPreferences(PREF_IGNORE, 0);
                                Set<String> ignoreSet = pref.getAll().keySet();
                                List<AppUpdate> update = new ArrayList<AppUpdate>();
                                List<AppUpdate> ignore = new ArrayList<AppUpdate>();
                                if (mUpdates != null) {
                                    for (AppUpdate au : mUpdates) {
                                        //比较本地应用
                                        //int status = getXXX(au.mPackageName, au.mVersionCode, au.mVersion);
                                        //if (status != STATUS_INSTALLED_OLD_VERSION)
                                        //    continue;
                                        if (ignoreSet.contains(au.mPackageName)) {
                                            ignore.add(au);
                                        } else {
                                            update.add(au);
                                        }
                                    }
                                }
                                mUpdateAdapter.setData(update, ignore);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
        /*
        JSONObject jsonObj = null;
        try {
            byte[] bytes = readFromAsset(this, "preload/update.json");
            if (bytes != null) {
                jsonObj = new JSONObject(new String(bytes));
            } else {

            }
            JSONObject entities = jsonObj.optJSONObject(NAME_ENTITIES);
            if (entities != null) {
                readFromJSON(entities);
                //上次忽略更新的应用
                SharedPreferences pref = getSharedPreferences(PREF_IGNORE, 0);
                Set<String> ignoreSet = pref.getAll().keySet();
                List<AppUpdate> update = new ArrayList<AppUpdate>();
                List<AppUpdate> ignore = new ArrayList<AppUpdate>();
                if (mUpdates != null) {
                    for (AppUpdate au : mUpdates) {
                        //比较本地应用
                        //int status = getXXX(au.mPackageName, au.mVersionCode, au.mVersion);
                        //if (status != STATUS_INSTALLED_OLD_VERSION)
                        //    continue;
                        if (ignoreSet.contains(au.mPackageName)) {
                            ignore.add(au);
                        } else {
                            update.add(au);
                        }
                    }
                }
                mUpdateAdapter.setData(update, ignore);
            }
        } catch (Exception e) {
        }
        */
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
            } catch (IOException e) {
            }
        }
        return ret;
    }

    @Override
    public void onUpdate(ExpandableListAdapter.UpdateInfoHolder updateInfo) {//更新
        Log.d(TAG, "onUpdate");
    }

    @Override
    public void onIgnore(AppUpdate item) {//忽略
        Log.d(TAG, "onIgnore");
        if (item == null || TextUtils.isEmpty(item.mPackageName))
            return;
        SharedPreferences pref = getSharedPreferences(PREF_IGNORE, 0);
        pref.edit().putInt(item.mPackageName, VALUE_IGNORE).commit();
        showData();
    }

    @Override
    public void onRemoveIgnore(AppUpdate item) {//取消忽略
        Log.d(TAG, "onRemoveIgnore");
        if (item == null || TextUtils.isEmpty(item.mPackageName))
            return;

        SharedPreferences pref = getSharedPreferences(Constants.PREF_IGNORE, 0);
        pref.edit().remove(item.mPackageName).commit();
        showData();
    }

    public void readFromJSON(JSONObject jsonObj) throws JSONException {
        mUpdates.clear();
        Object upAppItemObj = jsonObj.opt(JSON_UPAPPITEMS);
        if (upAppItemObj != null) {
            // 兼容两种更新接口数据
            if (upAppItemObj instanceof JSONArray) {
                parseUpdateArrayData(mUpdates, (JSONArray) upAppItemObj);
            } else if (upAppItemObj instanceof JSONObject) {
                int objCount = ((JSONObject) upAppItemObj).length();
                parseUpdateObjData(mUpdates, (JSONObject) upAppItemObj, objCount);
            } else {
                // Can't resolve upappitems, do nothing.
            }
        }

        mIgnores.clear();
        Object ignoreAppItemObj = jsonObj.opt(JSON_IGNOREAPPITEMS);
        if (ignoreAppItemObj != null) {
            if (ignoreAppItemObj instanceof JSONArray) {
                parseUpdateArrayData(mIgnores, (JSONArray) ignoreAppItemObj);
            } else if (ignoreAppItemObj instanceof JSONObject) {
                int objCount = ((JSONObject) ignoreAppItemObj).length();
                parseUpdateObjData(mIgnores, (JSONObject) ignoreAppItemObj, objCount);
            } else {
                // Can't resolve ignoreappitems, do nothing.
            }
        }
    }

    private void parseUpdateObjData(List<AppUpdate> outList, JSONObject jsonObj, int objCount) {
        int length = objCount;
        if (jsonObj == null || objCount <= 0)
            return;
        for (int pos = 0; pos < length; ++pos) {
            JSONObject updateObj = jsonObj.optJSONObject(String.valueOf(pos));
            if (updateObj == null)
                continue;
            try {
                AppUpdate update = new AppUpdate();
                update.readFromJSON(updateObj);
                outList.add(update);
            } catch (JSONException e) {
                continue;
            }
        }
    }

    private void parseUpdateArrayData(List<AppUpdate> outList, JSONArray jsonObj) {
        JSONArray updateArray = jsonObj;
        int length = 0;
        if (updateArray != null && (length = updateArray.length()) > 0) {
            for (int pos = 0; pos < length; ++pos) {
                JSONObject updateObj = updateArray.optJSONObject(pos);
                if (updateObj == null)
                    continue;
                try {
                    AppUpdate update = new AppUpdate();
                    update.readFromJSON(updateObj);
                    outList.add(update);
                } catch (JSONException e) {
                    continue;
                }
            }
        }
    }

    public JSONObject generateJSONObject() throws JSONException {
        JSONObject ret = new JSONObject();
        JSONArray array = new JSONArray();
        for (AppUpdate update : mUpdates) {
            if (update == null)
                continue;
            JSONObject updateObj = update.generateJSONObject();
            array.put(updateObj);
        }
        ret.put(JSON_UPAPPITEMS, array);
        array = new JSONArray();
        for (AppUpdate update : mIgnores) {
            if (update == null)
                continue;
            JSONObject updateObj = update.generateJSONObject();
            array.put(updateObj);
        }
        ret.put(JSON_IGNOREAPPITEMS, array);
        return ret;
    }
}
