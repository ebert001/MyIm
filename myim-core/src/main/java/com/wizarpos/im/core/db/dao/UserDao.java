package com.wizarpos.im.core.db.dao;

import org.springframework.stereotype.Repository;

import com.wizarpos.im.spring.dao.AbstractJdbcDao;

@Repository
public class UserDao extends AbstractJdbcDao {

	@Override
	protected void setTableName() {
		this.tableName = "m_user";
	}

	public void register() {

	}


}