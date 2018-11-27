package com.aswishes.im.commons.mvc.entity;

import java.util.Date;

/**
 * 用户好友分组
 * @author lizhou
 */
public class MUserGroup extends BaseId {
	/** 用户id */
	private Long userId;
	/** 分组名称 */
	private String name;
	/** 分组创建时间 */
	private Date createTime;

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
