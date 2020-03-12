package com.platon.framework.app.config;

import android.content.Context;

import java.util.HashMap;

public abstract class SysContext {
	HashMap<String, Object>	objsMap	= new HashMap<String, Object>();
	
	private Context			mContext;
	
	public void resetContext() {
		mContext = null;
	}
	
	public SysContext(Context context) {
		this.mContext = context.getApplicationContext();
	}
	
	public Context getApplicationContext() {
		return mContext;
	}
	
	public void registerSystemObject(String name, Object obj) {
		objsMap.put(name, obj);
	}
	
	public Object getSystemObject(String name) {
		return objsMap.get(name);
	}
	
}
