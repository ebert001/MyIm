package com.im.commons.db.entity;

import java.util.Date;

/**
 * 用户行为数据表．同 用户表 应当是一对一的关系．但是改动频率会比用户表要高
 * @author lizhou
 */
public class MUserBehavior extends BaseId {
	/** 用户在线状态 0 离线 */
	public static final byte P_OFFLINE = 0;
	/** 用户在线状态 1 在线 */
	public static final byte P_ONLINE = 1;
	/** 用户在线状态 2 空闲 */
	public static final byte P_IDLE = 2;
	/** 用户在线状态 3 忙碌 */
	public static final byte P_BUSY = 3;

	/** 用户id */
	private Long userId;
	/** 用户在线状态 0 离线 1 在线 2 空闲 3 忙碌 */
	private byte presence = 0;
	/** 网络类型．-1 未知 0 有线 1 WIFI 2 2G 3 3G 4 4G 5 5G  */
	private byte netType = 0;
	/** 客户端设备类型 */
	private short deviceType;

	/** 用户令牌，可作为用户离线检测的指标 */
	private String token;
	/** 用户令牌过期时间 */
	private Date expiry;
	/** 最近登录时间 */
	private Date lastLoginTime;
	/** 最近登录的IP4信息 */
	private String lastLoginIp4;

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public byte getPresence() {
		return presence;
	}
	public void setPresence(byte presence) {
		this.presence = presence;
	}
	public byte getNetType() {
		return netType;
	}
	public void setNetType(byte netType) {
		this.netType = netType;
	}
	public short getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(short deviceType) {
		this.deviceType = deviceType;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getExpiry() {
		return expiry;
	}
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLastLoginIp4() {
		return lastLoginIp4;
	}
	public void setLastLoginIp4(String lastLoginIp4) {
		this.lastLoginIp4 = lastLoginIp4;
	}

}
