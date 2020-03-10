/*
 * FileName: CollectionUtils.java
 * Description: 集合操作工具类文件
 */

package com.platon.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 集合辅助操作类，可用于获取元素、判空等功能
 * 
 * @author devilxie
 * @version 1.0
 * 
 */
public class CollectionUtils {
	
	public static boolean isEmpty(Collection<? extends Object> c) {
		return c == null || c.size() == 0;
	}
	
	public static boolean isEmpty(Object[] objs) {
		return objs == null || objs.length == 0;
	}
	
	/**
	 * 获取集合中指定索引位置元素
	 * 
	 * @param c 集合对象
	 * @param index 索引
	 * @return
	 */
	public static <T> T get(Collection<T> c, int index) {
		if (isEmpty(c))
			return null;
		
		if (index < 0 || index >= c.size())
			return null;
		
		if (c instanceof List)
			return (T) ((List<T>) c).get(index);
		
		List<? extends T> a = new ArrayList<T>(c);
		return a.get(index);
	}
	
	/**
	 * 获取集合中第一个元素
	 * 
	 * @param c 集合对象
	 * @return
	 */
	public static <T> T first(Collection<T> c) {
		if (isEmpty(c))
			return null;
		
		if (c instanceof List)
			return (T) ((List<T>) c).get(0);
		
		Iterator<T> iter = c.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}
	
	/**
	 * 获取集合中最后一个元素
	 * 
	 * @param c 集合对象
	 * @return
	 */
	public static <T> T last(Collection<T> c) {
		if (isEmpty(c))
			return null;
		
		if (c instanceof List)
			return (T) ((List<T>) c).get(c.size() - 1);
		
		List<T> a = new ArrayList<T>(c);
		return a.get(a.size() - 1);
	}
	
	/**
	 * 求取两集合对象的差集，差集 = 集合1 - 集合2，同时集合1，集合2不变
	 * 
	 * @param l 集合对象1
	 * @param r 集合对象2
	 * @return 返回两个集合的差集
	 */
	public static <E> Collection<E> diff(Collection<E> l, Collection<E> r) {
		if (isEmpty(l) || isEmpty(r))
			return l;
		
		List<E> s = new ArrayList<E>(l);
		s.removeAll(r);
		
		return s;
	}
	
	/**
	 * 求取两集合对象的差集，差集 = 集合1 - 集合2，同时集合1不变，集合2变为剩余部分
	 * 
	 * @param l 集合对象1
	 * @param r 集合对象2，本方法对集合2有影响
	 * @return 返回两个集合的差集
	 */
	public static <E> Collection<E> diffLeft(Collection<E> l, Collection<E> r) {
		if (isEmpty(l) || isEmpty(r))
			return l;
		
		List<E> s = new ArrayList<E>(l);
		s.removeAll(r);
		r.removeAll(l);
		
		return s;
	}
	
	/**
	 * 求取两集合对象的交集，交集 = 集合1与集合2共有元素，同时集合1，集合2不变
	 * 
	 * @param l 集合对象1
	 * @param r 集合对象2
	 * @return 返回两个集合的交集
	 */
	public static <E> Collection<E> same(Collection<E> l, Collection<E> r) {
		if (isEmpty(l) || isEmpty(r))
			return null;
		
		List<E> s = new ArrayList<E>(l);
		s.removeAll(r);
		List<E> k = new ArrayList<E>(l);
		k.removeAll(s);
		return k;
	}
}
