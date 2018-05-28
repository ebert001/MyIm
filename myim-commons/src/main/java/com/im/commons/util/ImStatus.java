package com.im.commons.util;

import java.text.MessageFormat;

/**
 * code的定义应当尽量避开常用的错误码(比如http码)，否则后面使用的时候可能发生混乱.
 * @author lizhou
 */
public enum ImStatus {
	S_SUCCESS(1001, "OK"),
	S_USER_NOT_FOUND(1002, "User not found. {}"),
	S_USERNAME_OR_PASSWORD_ERROR(1003, "User name or password error. {}"),
	S_OLD_PASSWORD_ERROR(1004, "Old password is not right."),
	S_USER_LOCKED(1005, "User is locked. {}"),
	S_USER_DISABLED(1006, "User is disabled. {}"),
	S_USER_CANCELED(1007, "User is canceled. {}"),

	;

	private int code;
	/** 开发可见的日志消息 */
	private String logMessage;
	/** 用户可见的提示消息 */
	private String tipMessage;
	/** 用户可见提示消息是否是国际化标签 */
	private boolean i18n = false;

	private ImStatus(int code, String logMessage) {
		this.code = code;
		this.logMessage = logMessage;
	}
	private ImStatus(int code, String logMessage, String tipMessage) {
		this(code, logMessage);
		this.tipMessage = tipMessage;
		this.i18n = true;
	}
	private ImStatus(int code, String logMessage, String tipMessage, boolean i18n) {
		this(code, logMessage);
		this.tipMessage = tipMessage;
		this.i18n = i18n;
	}

	/**
	 * @return 状态码
	 */
	public int getCode() {
		return this.code;
	}
	/**
	 * @return 日志消息
	 */
	public String getLogMessage(Object...args) {
		if (CommonUtils.isEmpty(args)) {
			return this.logMessage;
		}
		return MessageFormat.format(this.logMessage, args);
	}

	/**
	 * @return 提示消息。如果tip message为 null，将返回 "日志消息"，请注意处理.
	 */
	public String getTipMessage(Object... args) {
		if (this.tipMessage == null) {
			if (CommonUtils.isNotEmpty(args)) {
				return getLogMessage(args);
			}
			return this.logMessage;
		}
		if (i18n) {
//			return ResourceHelper.getText(this.tipMessage, args);
			return null;
		} else {
			return MessageFormat.format(this.tipMessage, args);
		}
	}
}
