package com.im.commons.db.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.im.commons.db.entity.MCorporation;
import com.im.spring.SqlHelper;
import com.im.spring.dao.AbstractJdbcDao;
import com.im.spring.mapper.MapperHelper;

@Repository
public class CorporationDao extends AbstractJdbcDao {

	@Override
	protected void setTableName() {
		this.tableName = "m_corporation";
	}

	public MCorporation get(Long id) {
		String sql = SqlHelper.select(tableName).columns("*").where("id = ?").toSqlString();
		return getObject(sql, MapperHelper.getMapper(MCorporation.class), id);
	}

	public MCorporation get(String name) {
		String sql = SqlHelper.select(tableName).columns("*").where("name = ?").toSqlString();
		return getObject(sql, MapperHelper.getMapper(MCorporation.class), name);
	}

	public void save(String name) {
		String sql = SqlHelper.insert(tableName).columns("name, createTime");
		saveAndGetId(sql, name, new Date());
	}

	public void update(Long id, String name) {
		String sql = SqlHelper.update(tableName).columns("name = ?").where("id = ?");
		update(sql, name, id);
	}

}
