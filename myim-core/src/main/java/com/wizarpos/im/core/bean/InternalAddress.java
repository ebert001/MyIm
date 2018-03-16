package com.wizarpos.im.core.bean;

/**
 * 需加入到缓存中
 * @author lizhou
 */
public class InternalAddress {

	private Long id;
	
	/** 端点服务器名称．消息服务器可通过该 */
	private String pointServerName;
	/** 端点服务器公网IP */
	private String internetIp;
	/** 端点服务器公网端口 */
	private int internetPort;
	
	/** 端点服务器内网IP */
	private String internalIp;
	/** 端点服务器内网端口 */
	private int internalPort;
	
	/** 消息服务器名称. */
	private String msgServerName;
	/** 消息服务器公网IP */
	private String msgServerInternetIp;
	/** 消息服务器公网端口 */
	private int msgServerInternetPort;
	
	/** 消息服务器内网IP */
	private String msgServerInternalIp;
	/** 消息服务器内网端口 */
	private int msgServerInternalPort;
}
