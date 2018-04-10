package com.im.commons.util.pool;

public abstract class BaseObject {

	private int index;

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int index) {
		this.index = index;
	}

	protected abstract void reset();
}
