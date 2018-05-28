package com.im.spring.service;

import java.util.List;
import java.util.Map;

import com.im.spring.PageResultWrapper;
import com.im.spring.Restriction;
import com.im.spring.dao.AbstractJdbcDao;
import com.im.spring.mapper.MapperHelper;

public abstract class AbstractService<T> {

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

	public List<T> getList(Class<T> bean) {
		return dao.getList(MapperHelper.getMapper(bean));
	}

	public List<T> getList(Class<T> bean, Restriction...restrictions) {
		return dao.getList(MapperHelper.getMapper(bean), restrictions);
	}

	public List<T> getList(Class<T> bean, int startIndex, int perNo) {
		return dao.getList(MapperHelper.getMapper(bean), startIndex, perNo);
	}

	public List<T> getList(Class<T> bean, int startIndex, int perNo, Restriction...restrictions) {
		return dao.getList(MapperHelper.getMapper(bean), startIndex, perNo, restrictions);
	}

	public PageResultWrapper<Map<String, Object>> getPage(final int pageNo, final int perNo) {
		return dao.getPage(pageNo, perNo);
	}

	public PageResultWrapper<Map<String, Object>> getPage(final int pageNo, final int perNo, Restriction...restrictions) {
		return dao.getPage(pageNo, perNo, restrictions);
	}

	public PageResultWrapper<T> getPage(final Class<T> bean, final int pageNo, final int perNo) {
		return dao.getPage(MapperHelper.getMapper(bean), pageNo, perNo);
	}

	public PageResultWrapper<T> getPage(final Class<T> bean, final int pageNo, final int perNo, final Restriction...restrictions) {
		return dao.getPage(MapperHelper.getMapper(bean), pageNo, perNo, restrictions);
	}
}
