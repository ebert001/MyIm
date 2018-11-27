package com.aswishes.im.commons.util.cache;

import redis.clients.jedis.Jedis;

public class RedisCache extends ImCache {
	private Jedis jedis;

	@Override
	public void init() {
		jedis = new Jedis("");
	}

	@Override
	public String get(String key) {
		return jedis.get(key);
	}

	@Override
	public void set(String key, String value) {
		jedis.set(key, value);
	}
}
