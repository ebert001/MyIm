package com.im.commons.util.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache extends ImCache {

	private Map<String, String> cache = new ConcurrentHashMap<String, String>();

	@Override
	public String get(String key) {
		return cache.get(key);
	}

	@Override
	public void set(String key, String value) {
		cache.put(key, value);
	}


}
