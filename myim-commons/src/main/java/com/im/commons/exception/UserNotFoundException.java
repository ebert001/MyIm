package com.im.commons.exception;

import com.im.commons.util.ImStatus;

public class UserNotFoundException extends ImException {
	private static final long serialVersionUID = -7797854252948382169L;

	public UserNotFoundException(ImStatus status) {
		super(status.getCode(), status.getLogMessage());
		this.status = status;
	}

	public UserNotFoundException(ImStatus status, Throwable e) {
		super(status.getCode(), status.getLogMessage(), e);
		this.status = status;
	}


}
