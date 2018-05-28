package com.im.commons.util.pool;

import java.util.concurrent.TimeUnit;

public interface ObjectPool<T extends BaseObject> {

	public T borrowObject(long timeout, TimeUnit unit);
	public T borrowObject();
	public void returnObject(T obj);
	public int getNumIdle();
	public int getNumActive();
}
