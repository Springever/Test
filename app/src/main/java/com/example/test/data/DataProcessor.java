package com.example.test.data;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 数据处理
 *  
 *
 */
public class DataProcessor implements IDataConstant {

	private static final String NAME_CODE = "code";
	private static final String NAME_DATA = "data";
	private static final String NAME_CONTEXT = "context";
	private static final String NAME_ENTITIES = "entities";

	@SuppressWarnings("unchecked")
	public DataCenter.Response processData(int dataId, byte[] dataBytes) throws Exception {
		if (dataBytes == null)
			return null;
		
		DataCenter.Response resp = new DataCenter.Response();
		JSONObject jsonObj = new JSONObject(new String(dataBytes));
		resp.mStatusCode = jsonObj.getInt(NAME_CODE);
		if (resp.mStatusCode != IDataConstant.DATA_RESULT_OK) {
			resp.mSuccess = false;
			return resp;
		}
		
		JSONObject dataObj = jsonObj.optJSONObject(NAME_DATA);
		if (dataObj != null) {
			JSONObject entities = dataObj.optJSONObject(NAME_ENTITIES);
			if (entities != null) {
				resp.mData = buildData(dataId, entities);
			}
			
			resp.mContext = new HashMap<String, String>();
			JSONObject contextObj = dataObj.optJSONObject(NAME_CONTEXT);
			if (contextObj != null) {
				Iterator<String> iterator = contextObj.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					String value = contextObj.getString(key);
					if (TextUtils.isEmpty(key))
						continue;
					resp.mContext.put(key, value);
				}
			}
			resp.mOtherData = buildOtherData(dataId, dataObj);
		}
		resp.mSuccess = true;
		
		return resp;
	}

	protected IDataBase buildData(int dataId, JSONObject jsonObj) throws Exception {
		switch (dataId) {
		case HOMEPAGE:
			/*
			HotPromoteTop top = new HotPromoteTop();
			top.readFromJSON(jsonObj);
			return top;
			*/
			return null;
		case HOMEPAGE_MORE:
			/*
			HotPromoteRest rest = new HotPromoteRest();
			rest.readFromJSON(jsonObj);
			return rest;
			*/
			return null;
		case MUST_INSTALL_APP:
		case MUST_INSTALL_GAME:
			/*
			BiBei bibei = new BiBei();
			bibei.readFromJSON(jsonObj);
			return bibei;
			*/
			return null;
		case APP_CATEGORY:
		case GAME_CATEGORY:
			/*
			CategoryList cl = new CategoryList();
			cl.readFromJSON(jsonObj);
			return cl;
			*/
			return null;
		case APP_RANK:
		case GAME_RANK:
		case CATEGORY_DETAIL_HOT:
			/*
			Rank rank = new Rank();
			rank.readFromJSON(jsonObj);
			return rank;
			*/
			return null;
		case APP_NEW_PROD:
		case GAME_NEW_PROD:
		case CATEGORY_DETAIL_NEWPROD:
			/*
			NewProd newProd = new NewProd();
			newProd.readFromJSON(jsonObj);
			return newProd;
			*/
			return null;
		case SEARCH_TAGS:
			/*
			Keywords tags = new Keywords();
			tags.readFromJSON(jsonObj);
			return tags;
			*/
			return null;
		case SEARCH_RESULT:
			/*
			SearchResult result = new SearchResult();
			result.readFromJSON(jsonObj);
			return result;
			*/
			return null;
		case SEARCH_ASSOCIATION:
			/*
			SearchAssociation association = new SearchAssociation();
			association.readFromJSON(jsonObj);
			return association;
			*/
			return null;
		case TOPIC_DETAIL:
			/*
			TopicDetail td = new TopicDetail();
			td.readFromJSON(jsonObj);
			return td;
			*/
			return null;
		case APP_DETAIL:
			/*
			AppDetail ad = new AppDetail();
			ad.readFromJSON(jsonObj);
			return ad;
			*/
			return null;
		case CHECK_UPDATE:
			if (jsonObj.length() <= 0)
				return null;
			
			CheckForUpdate update = new CheckForUpdate();
			update.readFromJSON(jsonObj);
			return update;
		case APP_DETAIL_COMMENT:
			/*
			CommentList commList = new CommentList();
			commList.readFromJSON(jsonObj);
			return commList;
			*/
			return null;
		default:
			return null;
		}
	}
	
	private IDataBase buildOtherData(int dataId, JSONObject jsonObj) throws Exception {
		switch (dataId) {
		case SEARCH_RESULT:
			/*
			SearchRecommend sr = new SearchRecommend();
			sr.readFromJSON(jsonObj);
			return sr;
			*/
			return null;
		default:
			return null;
		}
	}
	
}
