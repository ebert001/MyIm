package com.im.commons.mvc.entity;

import java.util.Date;

/**
 * 企业信息
 * @author lizhou
 */
public class MCorporation extends BaseId {

	/** Corporation name */
	private String name;

	private Date createTime;

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
