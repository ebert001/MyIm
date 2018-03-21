package com.wizarpos.im.core.db.entity;

import java.util.Date;

/**
 * 已送达消息表．正常情况下，已送达消息不需要记录．
 * @author lizhou
 */
public class MArrivedMessage extends BaseId {
	/** 用户id */
	private Long userId;
	/** 群组id */
	private Long mucId;
	/** 好友id (目标用户) */
	private Long friendId;
	/** 消息．不超过4KB */
	private String msg;
	/** 消息发送时间 */
	private Date sendTime;
	/** 消息记录入库时间 */
	private Date createTime;

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getMucId() {
		return mucId;
	}
	public void setMucId(Long mucId) {
		this.mucId = mucId;
	}
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
