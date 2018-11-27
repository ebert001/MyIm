package com.aswishes.im.commons.util.cache;

public abstract class ImCache {

	public void init() {

	}

	public abstract String get(String key);
	public abstract void set(String key, String value);
}
