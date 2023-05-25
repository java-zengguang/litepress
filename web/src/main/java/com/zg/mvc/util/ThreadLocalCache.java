package com.zg.mvc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal缓存类
 * 
 * @author yangqinghua-phq
 * @version 2020年11月19日
 */
public class ThreadLocalCache {
	/**
	 * 改为 每个使用 ThreadLocal 变量的线程都会在各自的线程中保存一份 独立 的副本，以便各个线程之间没有相互影响
	 */
	private static ThreadLocal<Map<String, Object>> cache = new ThreadLocal<Map<String, Object>>();
		//ThreadLocal.withInitial(()->{ return new HashMap<>(); });
	/**
	 * 从ThreadLocal里获取缓存的值
	 * @param key 要获取的数据的KEY
	 * @return 要获取的值
	 */
	public static Object getCache(String key) {
		Map<String, Object> map = cache.get();
		if (isCaheIsNull()) { return null; }
		if (map.containsKey(key)) { return map.get(key); }
		return null;
	}

	/**
	 * 向ThreadLocal缓存值
	 * @param key   要缓存的KEY
	 * @param value 要缓存的VALUE
	 */
	public static void setCache(String key, Object value) {
		if (!isCaheIsNull()) {
			cache.get().put(key, value);
		} else {
			Map<String, Object> vmap = new HashMap<String, Object>();
			vmap.put(key, value);
			cache.set(vmap);
		}
	}

	/**
	 * 根据KEY移除缓存里的数据
	 * @param key 缓存的key
	 */
	public static void removeCache(String key) {
		if (isCaheIsNull()) { return; }
		cache.get().remove(key);
	}

	/**
	 * 释放当前线程threadlocal资源
	 */
	public static void clean() {
		cache.remove();
	}

	/**
	 * 获取所有绑定线程对象
	 * @return
	 */
	public static Map<String, Object> getAllCache() {
		return cache.get();
	}

	/**
	 * 绑定线程对象赋值
	 * @return
	 */
	public static void assignCache(Map<String, Object> cacheMap) {
		ThreadLocalCache.cache.set(cacheMap);
	}

	private static boolean isCaheIsNull() {
		return cache.get() == null;
	}
}
