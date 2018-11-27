package com.aswishes.im.commons.mvc.entity;

import java.util.Date;

/**
 * 用户签名消息．签名消息一旦生成，不能修改，不能删除
 * @author lizhou
 */
public class MUserShortMsg extends BaseId {
	/** 用户Id */
	private Long userId;
	/** 签名信息 */
	private String shortMsg;
	/** 签名时间 */
	private Date createTime;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getShortMsg() {
		return shortMsg;
	}

	public void setShortMsg(String shortMsg) {
		this.shortMsg = shortMsg;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
