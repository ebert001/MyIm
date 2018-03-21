package com.wizarpos.im.core.db.entity;

import java.util.Date;

/**
 * 用户分组中拥有的好友
 * @author lizhou
 */
public class RGroupUser extends BaseId {
	/** 用户分组id */
	private Long groupId;
	/** 好友id */
	private Long friendId;
	/** 好友别名 */
	private String friendAlias;
	/** 好友名称 */
	private String friendName;
	/** 添加到分组时间 */
	private Date createTime;

	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
	public String getFriendAlias() {
		return friendAlias;
	}
	public void setFriendAlias(String friendAlias) {
		this.friendAlias = friendAlias;
	}
	public String getFriendName() {
		return friendName;
	}
	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
