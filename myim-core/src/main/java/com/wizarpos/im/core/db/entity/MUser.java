package com.wizarpos.im.core.db.entity;

import java.util.Date;

/**
 * 可选，加入缓存
 * @author lizhou
 */
public class MUser extends BaseId {

	/** 用户名称 */
	private String userName;
	/** 密码 */
	private String password;
	/** 盐 */
	private byte[] salt;
	/** 用户手机 */
	private String mobildNo;
	/** 注册时间 */
	private Date registerTime;
	/** 最近登录时间 */
	private Date lastLoginTime;
	
	
	/** 用户token，可作为用户离线检测的指标 */
	private byte[] token;
	/** 用户在线状态 0 离线 1 在线 2 空闲 3 忙碌 */
	private byte presence;
	
	private Date expiry;
}
