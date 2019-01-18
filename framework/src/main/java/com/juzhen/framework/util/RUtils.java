/*
 * FileName: R.java
 * Description: 资源查找反射类文件
 */
package com.juzhen.framework.util;

import java.lang.reflect.Field;

import android.content.Context;

/**
 * 资源查询反射类，用于android应用层逻辑，根据资源的字符串名索引到相关的资源
 * 
 * @author devilxie
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public final class RUtils {
	public static Context	mContext;
	public static Class		R_id		= null;
	public static Class		R_drawable	= null;
	public static Class		R_layout	= null;
	public static Class		R_anim		= null;
	public static Class		R_style		= null;
	public static Class		R_xml		= null;
	public static Class		R_string	= null;
	public static Class		R_array		= null;
	
	public static void init(Context context) {
		mContext = context;
		init();
	}
	
	static void init() {
		try {
			R_drawable = Class.forName(mContext.getPackageName() + ".R$drawable");
			
		} catch (ClassNotFoundException e) {
		}
		
		try {
			R_layout = Class.forName(mContext.getPackageName() + ".R$layout");
		} catch (ClassNotFoundException e) {
		}
		try {
			R_id = Class.forName(mContext.getPackageName() + ".R$id");
		} catch (ClassNotFoundException e) {
		}
		try {
			R_anim = Class.forName(mContext.getPackageName() + ".R$anim");
		} catch (ClassNotFoundException e) {
		}
		try {
			R_style = Class.forName(mContext.getPackageName() + ".R$style");
		} catch (ClassNotFoundException e) {
		}
		try {
			R_string = Class.forName(mContext.getPackageName() + ".R$string");
		} catch (ClassNotFoundException e) {
		}
		try {
			R_xml = Class.forName(mContext.getPackageName() + ".R$xml");
		} catch (ClassNotFoundException e) {
		}
		try {
			R_array = Class.forName(mContext.getPackageName() + ".R$array");
		} catch (ClassNotFoundException e) {
		}
	}
	
	public static int string(String field) {
		try {
			Field idField = R_string.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int anim(String field) {
		try {
			Field idField = R_anim.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int id(String field) {
		try {
			Field idField = R_id.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int drawable(String field) {
		try {
			Field idField = R_drawable.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int layout(String field) {
		try {
			Field idField = R_layout.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int style(String field) {
		try {
			Field idField = R_style.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int xml(String field) {
		try {
			Field idField = R_xml.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static int array(String field) {
		try {
			Field idField = R_array.getField(field);
			int id = idField.getInt(field);
			
			return id;
		} catch (Exception e) {
		}
		return -1;
	}
}
