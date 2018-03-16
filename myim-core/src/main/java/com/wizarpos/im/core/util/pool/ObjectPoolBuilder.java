package com.wizarpos.im.core.util.pool;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectPoolBuilder<T extends BaseObject> {

	/** 活动对象 */
	private BaseObject[] activatedObject = null;
	/** 钝化对象 */
	private BaseObject[] passivatedObject = null;

	private Class<T> poolObject = null;
	private int capacity;
	private AtomicInteger borrowNum = new AtomicInteger(0);
	private int lastFindIndex = 0;

	private final ReentrantLock lock;
	private final Condition notEmpty;
	private final Condition notFull;

	public ObjectPoolBuilder(int capacity) throws Exception {
		initPoolObject();
		this.capacity = capacity;
		passivatedObject = new BaseObject[capacity];
		activatedObject = new BaseObject[capacity];
		for (int i = 0; i < capacity; i++) {
			T t = poolObject.newInstance();
			t.setIndex(i);
			passivatedObject[i] = t;
		}
		lock = new ReentrantLock(true);
		notEmpty = lock.newCondition();
		notFull = lock.newCondition();
	}

	@SuppressWarnings("unchecked")
	private void initPoolObject() {
		@SuppressWarnings("rawtypes")
		Class clazz = getClass();

		while (clazz != Object.class) {
			Type t = clazz.getGenericSuperclass();
			if (!(t instanceof ParameterizedType)) {
				clazz = clazz.getSuperclass();
				continue;
			}
			Type[] args = ((ParameterizedType) t).getActualTypeArguments();
			if (args[0] instanceof Class) {
				this.poolObject = (Class<T>) args[0];
				break;
			}
		}
	}

	public T borrowObject(long borrowTimeout, TimeUnit unit) throws InterruptedException {
		lock.lock();
		long nanos = unit.toNanos(borrowTimeout);
		try {
			for (;;) {
				if (lastFindIndex == capacity) {
					lastFindIndex = 0;
				}
				@SuppressWarnings("unchecked")
				T t = (T) passivatedObject[lastFindIndex++];
				if (nanos <= 0) {
					return null;
				}
				nanos = notEmpty.awaitNanos(nanos);
				if (t == null) {
					continue;
				}
				activatedObject[t.getIndex()] = t;
				borrowNum.incrementAndGet();
				return t;
			}
		} finally {
			lock.unlock();
		}
	}

	public void returnObject(T obj) {
		synchronized (this) {
			int index = obj.getIndex();
			passivatedObject[index] = obj;
			activatedObject[index] = null;
			borrowNum.decrementAndGet();
			notFull.signal();
		}
	}

	public int getNumIdle() {
		return this.capacity - borrowNum.get();
	}

	public int getNumActive() {
		return borrowNum.get();
	}
}
