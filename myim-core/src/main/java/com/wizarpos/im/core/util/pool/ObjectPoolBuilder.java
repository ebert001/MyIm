package com.wizarpos.im.core.util.pool;

import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolBuilder<T extends BaseObject> {
	private static final Logger logger = LoggerFactory.getLogger(ObjectPoolBuilder.class);
	/** 钝化对象 */
	private T[] passivatedObject = null;
	/** 钝化对象索引．钝化对象的索引记录方式类似于RingBuffer的索引滚动方式 */
	private int[] passivatedIndex = null;
	/** 索引读位置 */
	private int readIndex = 0;
	/** 索引写位置 */
	private int writeIndex = 0;
	/** 对象池容量 */
	private int capacity = 16;

	private Class<T> poolObjectClass = null;

	private final ReentrantLock lock;
	private final Condition notEmpty;

	public ObjectPoolBuilder(int capacity) {
		this.capacity = capacity;

		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
	}

	@SuppressWarnings("unchecked")
	public ObjectPoolBuilder<T> build(Class<T> clazz) throws Exception {
		this.poolObjectClass = clazz;
		passivatedObject = (T[]) Array.newInstance(poolObjectClass, capacity);
		passivatedIndex = new int[capacity];

		for (int i = 0; i < capacity; i++) {
			T t = poolObjectClass.newInstance();
			t.setIndex(i);
			passivatedObject[i] = t;
			passivatedIndex[i] = i;
		}
		writeIndex = capacity - 1;
		return this;
	}

	public T borrowObject(long timeout, TimeUnit unit) {
		lock.lock();
		long nanos = unit.toNanos(timeout);
		try {
			while (!hasPassivatedObject()) {
				if (nanos <= 0L) {
					return null;
				}
				try {
					nanos = notEmpty.awaitNanos(nanos);
				} catch (InterruptedException e) {
					logger.error("Interrupted Exception", e);
					return null;
				}
			}
			return passivatedObject[getAndIncrementReadIndex()];
		} finally {
			lock.unlock();
		}
	}

	public T borrowObject(boolean await) {
		lock.lock();
		try {
			if (!hasPassivatedObject()) {
				if (await) {
					try {
						notEmpty.await();
					} catch (InterruptedException e) {
						logger.error("Interrupted Exception", e);
						return null;
					}
				} else {
					return null;
				}
			}
			return passivatedObject[getAndIncrementReadIndex()];
		} finally {
			lock.unlock();
		}
	}

	public void returnObject(T obj) {
		lock.lock();
		try {
			obj.reset();
			passivatedIndex[getAndIncrementWriteIndex()] = obj.getIndex();
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public int getNumIdle() {
		return Math.abs(writeIndex - readIndex);
	}

	public int getNumActive() {
		return capacity - getNumIdle();
	}

	private boolean hasPassivatedObject() {
		return readIndex != writeIndex;
	}

	private synchronized int getAndIncrementWriteIndex() {
		if (writeIndex == capacity) {
			writeIndex = 0;
		}
		int t = writeIndex;
		writeIndex++;
		return t;
	}

	private synchronized int getAndIncrementReadIndex() {
		if (readIndex == capacity) {
			readIndex = 0;
		}
		int t = readIndex;
		readIndex++;
		return t;
	}
}
