package com.im.core.db.entity;

import java.util.Date;

/**
 * 用户群组(聊天室)
 * @author lizhou
 */
public class MMuc extends BaseId {
	/** 群组创建者id */
	private Long creatorId;
	/** 群组名称 */
	private String name;
	/** 群组logo */
	private String logo;
	/** 群组人数限制．默认为100人 */
	private int capacity = 100;
	/** 群组创建时间 */
	private Date createTime;

	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
