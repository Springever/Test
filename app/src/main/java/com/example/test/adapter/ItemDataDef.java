package com.example.test.adapter;

/**
 * 软件数据结构定义
 *  
 *
 */
public class ItemDataDef {

	/** 用于列表中需要显示多种数据类型 */
	public static class ItemDataWrapper {
		public ItemDataWrapper(Object data, int type) {
			mData = data;
			mItemType = type;
		}
		
		/** 数据 */
		public Object mData;
		
		/** 数据类型，由各Adapter定义维护 */
		public int mItemType;
	}
}
