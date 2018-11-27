package com.aswishes.im.commons.util.pool;

public interface PoolObjectCreator<T extends BaseObject> {

	T create();
}
