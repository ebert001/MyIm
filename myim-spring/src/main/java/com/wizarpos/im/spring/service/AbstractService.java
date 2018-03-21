package com.wizarpos.im.spring.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.wizarpos.im.spring.PageResultWrapper;
import com.wizarpos.im.spring.Restriction;
import com.wizarpos.im.spring.dao.AbstractJdbcDao;

public abstract class AbstractService<T extends RowMapper<T>> {

	protected AbstractJdbcDao dao;

	public abstract void setDao();

	public void delete(Long id) {
		dao.delete(id);
	}

	public int getCount() {
		return dao.getCount();
	}

	public int getCount(Restriction...restrictions) {
		return dao.getCount(restrictions);
	}

	public List<Map<String, Object>> getList() {
		return dao.getList();
	}

	public List<T> getList(T bean) {
		return dao.getList(bean);
	}

	public List<T> getList(T bean, Restriction...restrictions) {
		return dao.getList(bean, restrictions);
	}

	public List<T> getList(T bean, int startIndex, int perNo) {
		return dao.getList(bean, startIndex, perNo);
	}

	public List<T> getList(T bean, int startIndex, int perNo, Restriction...restrictions) {
		return dao.getList(bean, startIndex, perNo, restrictions);
	}

	public PageResultWrapper<Map<String, Object>> getPage(final int pageNo, final int perNo) {
		return dao.getPage(pageNo, perNo);
	}

	public PageResultWrapper<Map<String, Object>> getPage(final int pageNo, final int perNo, Restriction...restrictions) {
		return dao.getPage(pageNo, perNo, restrictions);
	}

	public PageResultWrapper<T> getPage(final T bean, final int pageNo, final int perNo) {
		return dao.getPage(bean, pageNo, perNo);
	}

	public PageResultWrapper<T> getPage(final T bean, final int pageNo, final int perNo, final Restriction...restrictions) {
		return dao.getPage(bean, pageNo, perNo, restrictions);
	}
}
