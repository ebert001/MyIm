package com.im.core.db.entity;

public class RMucUser extends BaseId {
	/** 用户群组id */
	private Long mucId;
	/** 好友id */
	private Long friendId;
	/** 好友别名 */
	private String friendAlias;
	/** 好友名称 */
	private String friendName;
	/** 好友加入时间 */
	private String createTime;

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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
