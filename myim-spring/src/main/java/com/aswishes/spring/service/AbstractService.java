package com.aswishes.spring.service;

import java.util.List;
import java.util.Map;

import com.aswishes.spring.PageResultWrapper;
import com.aswishes.spring.Restriction;
import com.aswishes.spring.dao.AbstractJdbcDao;
import com.aswishes.spring.mapper.MapperHelper;

public abstract class AbstractService {

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

	public <T> List<T> getList(Class<T> bean) {
		return dao.getList(MapperHelper.getMapper(bean));
	}

	public <T> List<T> getList(Class<T> bean, Restriction...restrictions) {
		return dao.getList(MapperHelper.getMapper(bean), restrictions);
	}

	public <T> List<T> getList(Class<T> bean, int startIndex, int pageSize) {
		return dao.getList(MapperHelper.getMapper(bean), startIndex, pageSize);
	}

	public <T> List<T> getList(Class<T> bean, int startIndex, int pageSize, Restriction...restrictions) {
		return dao.getList(MapperHelper.getMapper(bean), startIndex, pageSize, restrictions);
	}

	public PageResultWrapper<Map<String, Object>> getPage(final int pageNo, final int pageSize) {
		return dao.getPage(pageNo, pageSize);
	}

	public PageResultWrapper<Map<String, Object>> getPage(final int pageNo, final int pageSize, Restriction...restrictions) {
		return dao.getPage(pageNo, pageSize, restrictions);
	}

	public <T> PageResultWrapper<T> getPage(final Class<T> bean, final int pageNo, final int pageSize) {
		return dao.getPage(MapperHelper.getMapper(bean), pageNo, pageSize);
	}

	public <T> PageResultWrapper<T> getPage(final Class<T> bean, final int pageNo, final int pageSize, final Restriction...restrictions) {
		return dao.getPage(MapperHelper.getMapper(bean), pageNo, pageSize, restrictions);
	}
}
