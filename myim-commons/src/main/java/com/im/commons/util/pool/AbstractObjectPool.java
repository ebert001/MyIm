package com.im.commons.util.pool;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class AbstractObjectPool<T extends BaseObject> implements ObjectPool<T> {

	protected Class<T> poolObjectClass = null;

	AbstractObjectPool() {
	}

	@SuppressWarnings("unchecked")
	protected void getClassType() {
		Type genType = this.getClass().getGenericInterfaces()[0];
		if (!(genType instanceof ParameterizedType)) {
			throw new IllegalStateException("Generic class type error. " + genType);
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (!(params[0] instanceof Class)) {
			throw new IllegalStateException("Generic class type error. " + params[0]);
		}
		poolObjectClass = (Class<T>) params[0];
	}

}
