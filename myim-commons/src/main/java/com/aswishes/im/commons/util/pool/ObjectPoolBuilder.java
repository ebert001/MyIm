package com.aswishes.im.commons.util.pool;

import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolBuilder<T extends BaseObject> extends AbstractObjectPool<T> {
	private static final Logger logger = LoggerFactory.getLogger(ObjectPoolBuilder.class);
	/** 钝化对象 */
	private T[] passivatedObject = null;
	/** 钝化对象索引．钝化对象的索引记录方式类似于RingBuffer的索引滚动方式 */
	private int[] passivatedIndex = null;
	/** 索引读位置 */
	private int readIndex = 0;
	/** 索引写位置 */
	private int writeIndex = 0;
	/** 最小容量 */
	private int minCapacity = 8;
	/** 对象池容量 */
	private int capacity = 16;
	/** 当前容量 */
	private int curCapacity = 0;

	private Class<T> poolObjectClass = null;

	private final ReentrantLock lock;
	private final Condition notEmpty;

	private PoolObjectCreator<T> creator;
	private int returnCount = 0;

	public ObjectPoolBuilder(int minCapacity, int capacity) {
		super();
		this.minCapacity = minCapacity;
		this.capacity = capacity;

		lock = new ReentrantLock();
		notEmpty = lock.newCondition();
	}

	@SuppressWarnings("unchecked")
	public ObjectPoolBuilder<T> build(PoolObjectCreator<T> creator) throws Exception {
		this.creator = creator;
		passivatedObject = (T[]) Array.newInstance(poolObjectClass, capacity);
		passivatedIndex = new int[capacity];

		initCapacity();
		return this;
	}

	private synchronized void initCapacity() {
		for (int i = 0; i < this.minCapacity; i++) {
			T t = this.creator.create();
			if (t == null) {
				this.curCapacity = i + 1;
				break;
			}
			t.setIndex(i);
			passivatedObject[i] = t;
			passivatedIndex[i] = i;
		}
		if (this.curCapacity == 0) {
			this.curCapacity = this.minCapacity;
		}
		writeIndex = this.curCapacity - 1;
	}

	@Override
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

	@Override
	public T borrowObject() {
		lock.lock();
		try {
			if (!hasPassivatedObject()) {
				try {
					notEmpty.await();
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

	@Override
	public void returnObject(T obj) {
		returnCount++;
		lock.lock();
		try {
			obj.reset();
			if (obj.getIndex() == -1) {
				return;
			}
			passivatedIndex[getAndIncrementWriteIndex()] = obj.getIndex();
			notEmpty.signal();
		} finally {
			lock.unlock();
			// 调整容量
			changeCapacity();
		}
	}

	@Override
	public int getNumIdle() {
		return Math.abs(writeIndex - readIndex);
	}

	@Override
	public int getNumActive() {
		return capacity - getNumIdle();
	}

	private boolean isAdd() {
		return (((float) getNumIdle()) / ((float) this.curCapacity) < 0.1) && (returnCount % 1000 == 0);
	}

	private boolean isReduce() {
		return (((float) getNumIdle()) / ((float) this.curCapacity) > 0.5) && (returnCount % 1000 == 0);
	}

	private boolean hasPassivatedObject() {
		return readIndex != writeIndex;
	}

	private synchronized int getAndIncrementWriteIndex() {
		if (writeIndex == this.curCapacity) {
			writeIndex = 0;
		}
		int t = writeIndex;
		writeIndex++;
		return t;
	}

	private synchronized int getAndIncrementReadIndex() {
		if (readIndex == this.curCapacity) {
			readIndex = 0;
		}
		int t = readIndex;
		readIndex++;
		return t;
	}

	private int maxReturnCount = 1 << 30 - 1;
	private void changeCapacity() {
		addCapacity();
		reduceCapacity();
		if (returnCount > maxReturnCount) {
			returnCount = 0;
		}
	}

	private synchronized void addCapacity() {
		if (!isAdd() && curCapacity >= capacity) {
			return;
		}
		int addNum = (int) (curCapacity * 0.25);
		if (curCapacity + addNum > capacity) {
			addNum = capacity - curCapacity;
		}
		int newCurCapacity = curCapacity + addNum;
		for (int i = curCapacity - 1; i < newCurCapacity; i++) {
			T t = this.creator.create();
			if (t == null) {
				newCurCapacity = i + 1;
				break;
			}
			t.setIndex(i);
			passivatedObject[i] = t;
			passivatedIndex[i] = i;
		}
		if (newCurCapacity != curCapacity + addNum) {
			curCapacity = newCurCapacity;
		}
		writeIndex = this.curCapacity - 1;
	}

	private synchronized void reduceCapacity() {
		if (!isReduce() || curCapacity <= minCapacity) {
			return;
		}
		int reduceNum = (int) (curCapacity * 0.25);
		if (curCapacity - reduceNum < minCapacity) {
			reduceNum = curCapacity - minCapacity;
		}
		int newCurCapacity = curCapacity - reduceNum;
		for (int i = curCapacity - 1; i > newCurCapacity; i--) {
			T t = passivatedObject[i];
			t.setIndex(-1);
			passivatedObject[i] = null;
		}
		curCapacity = newCurCapacity;
		writeIndex = this.curCapacity - 1;
	}

}
