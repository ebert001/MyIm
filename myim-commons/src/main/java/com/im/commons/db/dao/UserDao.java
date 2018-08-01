package com.im.commons.db.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.im.commons.db.entity.MUser;
import com.im.commons.db.entity.MUserBehavior;
import com.im.commons.db.entity.MUserGroup;
import com.im.spring.SqlHelper;
import com.im.spring.dao.AbstractJdbcDao;
import com.im.spring.mapper.MapperHelper;

@Repository
public class UserDao extends AbstractJdbcDao {
	private static final String TABLE_USER_GROUP = "m_user_group";
	private static final String TABLE_USER_BEHAVIOR = "m_user_behavior";

	@Override
	protected void setTableName() {
		this.tableName = "m_user";
	}

	public Long register(MUser user) {
		String sql = SqlHelper.insert(tableName).columns("");
		return this.saveAndGetId(sql, user);
	}

	public MUser get(String username) {
		String sql = SqlHelper.select(tableName).columns("*").where("username = ?").toSqlString();
		return getObject(sql, MapperHelper.getMapper(MUser.class), username);
	}

	public void modifyPassword(Long userId, String password, String salt) {
		String sql = SqlHelper.update(tableName).set("password = ?, salt = ?").where("id = ?");
		update(sql, password, salt, userId);
	}

	public void addUserGroup(Long userId, String groupName) {
		String sql = SqlHelper.insert(TABLE_USER_GROUP).columns("user_id, name, create_time");
		this.saveAndGetId(sql, userId, groupName, new Date());
	}

	public void updateUserGroupName(Long id, String groupName) {
		String sql = SqlHelper.update(TABLE_USER_GROUP).set("name = ?").where("id = ?");
		this.saveAndGetId(sql, groupName, id);
	}

	public List<MUserGroup> getGroups(String userId) {
		String sql = SqlHelper.select(TABLE_USER_GROUP).columns("*").where("user_id = ?").toSqlString();
		return getList(sql, MapperHelper.getMapper(MUserGroup.class), userId);
	}

	public void deleteGroup(Long id) {
		String sql = SqlHelper.delete(TABLE_USER_GROUP).where("id = ?");
		update(sql, id);
	}

	public MUserBehavior getUserBehavior(Long userId) {
		String sql = SqlHelper.select(TABLE_USER_BEHAVIOR).columns("*").where("user_id = ?").toSqlString();
		return getObject(sql, MapperHelper.getMapper(MUserBehavior.class), userId);
	}

	public void addUserBehavior(Long userId) {
		String sql = SqlHelper.insert(TABLE_USER_BEHAVIOR).columns("user_id, presence");
		update(sql, userId, MUserBehavior.P_OFFLINE);
	}

	public void updateUserBehavior(Long userId, String token, Date expiryTime, Date lastLoginTime, String lastLoginIpv4) {
		String sql = SqlHelper.update(TABLE_USER_BEHAVIOR).set("token = ?, expiry = ?, last_login_time = ?, last_login_ip4").where("user_id = ?");
		update(sql, token, expiryTime, lastLoginTime, lastLoginIpv4, userId);
	}


}
