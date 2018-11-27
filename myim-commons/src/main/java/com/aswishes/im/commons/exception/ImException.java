package com.aswishes.im.commons.exception;

import com.aswishes.im.commons.util.ImStatus;

public class ImException extends RuntimeException {
	private static final long serialVersionUID = -6634300136490098961L;

    protected int code;
    protected ImStatus status;
    protected Object[] logArgs;
    protected Object[] tipArgs;

	public ImException() {
	}

	public ImException(int code) {
		this.code = code;
	}

	public ImException(String message) {
		super(message);
	}

	public ImException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ImException(int code, Throwable e) {
		super(e);
		this.code = code;
	}

	public ImException(int code, String message, Throwable e) {
		super(message, e);
		this.code = code;
	}

	public ImException(ImStatus status) {
		this(status.getCode(), status.getLogMessage());
		this.status = status;
	}

	public ImException(ImStatus status, Throwable e) {
		this(status.getCode(), status.getLogMessage(), e);
		this.status = status;
	}



	public ImException logArgs(Object...args) {
		this.logArgs = args;
		return this;
	}

	public ImException tipArgs(Object...args) {
		this.tipArgs = args;
		return this;
	}

	public ImException logAndTipArgs(Object...args) {
		this.logArgs = args;
		this.tipArgs = args;
		return this;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ImStatus getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		if (status != null) {
			return this.status.getLogMessage(logArgs);
		}
		return super.getMessage();
	}

	public String getTipMessage() {
		return this.status.getTipMessage(tipArgs);
	}
}
