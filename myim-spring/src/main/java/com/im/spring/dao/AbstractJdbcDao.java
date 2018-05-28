package com.im.spring.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.im.spring.PageResultWrapper;
import com.im.spring.Restriction;
import com.im.spring.SqlHelper;

@Transactional
public abstract class AbstractJdbcDao {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractJdbcDao.class);
	protected JdbcTemplate jdbcTemplate;
	protected String tableName;

	public AbstractJdbcDao() {
		setTableName();
	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	protected abstract void setTableName();

	/**
	 * @param requiredType 只能是基本类型的包装类型
	 */
	public <E> E getObject(String sql, Class<E> requiredType, Object...args) {
		try {
			return jdbcTemplate.queryForObject(sql, requiredType, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <E> E getObject(String sql, Class<E> requiredType, Restriction...restrictions) {
		try {
			return jdbcTemplate.queryForObject(sql, requiredType, Restriction.whereValues(restrictions));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <E> E getObject(String sql, RowMapper<E> mapper, Object...args) {
		try {
			return jdbcTemplate.queryForObject(sql, mapper, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <E> E getObject(String sql, RowMapper<E> mapper, Restriction...restrictions) {
		try {
			return jdbcTemplate.queryForObject(sql, mapper, Restriction.whereValues(restrictions));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public <E> E getObjectBy(RowMapper<E> mapper, Restriction...restrictions) {
		String sql = SqlHelper.select(tableName).columns("*").where(restrictions).toSqlString();
		try {
			return jdbcTemplate.queryForObject(sql, mapper, Restriction.whereValues(restrictions));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Transactional
	public int getCount() {
		String sql = SqlHelper.select(tableName).count("*").where().toCountString();
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	@Transactional
	public <E> List<E> getList(RowMapper<E> mapper) {
		String sql = SqlHelper.select(tableName).columns("*").where("").toSqlString();
		return jdbcTemplate.query(sql, mapper);
	}

	@Transactional
	public List<Map<String, Object>> getList() {
		String sql = SqlHelper.select(tableName).columns("*").where("").toSqlString();
		return jdbcTemplate.queryForList(sql);
	}

	@Transactional
	public int getCount(Restriction...restrictions) {
		String sql = SqlHelper.select(tableName).count("*").where(restrictions).toCountString();
		return jdbcTemplate.queryForObject(sql, Integer.class, Restriction.whereValues(restrictions));
	}

	@Transactional
	public <E> List<E> getList(RowMapper<E> mapper, Restriction...restrictions) {
		String sql = SqlHelper.select(tableName).columns("*").where(restrictions).toSqlString();
		return jdbcTemplate.query(sql, mapper, Restriction.whereValues(restrictions));
	}

	@Transactional
	public List<Map<String, Object>> getList(Restriction...restrictions) {
		String sql = SqlHelper.select(tableName).columns("*").where(restrictions).toSqlString();
		return jdbcTemplate.queryForList(sql, Restriction.whereValues(restrictions));
	}

	@Transactional
	public <E> List<E> getList(RowMapper<E> mapper, int pageNo, int pageSize) {
		String sql = SqlHelper.select(tableName).columns("*").where().toSqlString();
		sql += getLimitSql(pageNo, pageSize);
		return jdbcTemplate.query(sql, mapper);
	}

	@Transactional
	public List<Map<String, Object>> getList(int pageNo, int pageSize) {
		String sql = SqlHelper.select(tableName).columns("*").where().toSqlString();
		sql += getLimitSql(pageNo, pageSize);
		return jdbcTemplate.queryForList(sql);
	}

	@Transactional
	public <E> List<E> getList(RowMapper<E> mapper, int pageNo, int pageSize, Restriction...restrictions) {
		String sql = SqlHelper.select(tableName).columns("*").where(restrictions).toSqlString();
		sql += getLimitSql(pageNo, pageSize);
		return jdbcTemplate.query(sql, mapper, Restriction.whereValues(restrictions));
	}

	@Transactional
	public List<Map<String, Object>> getList(int pageNo, int pageSize, Restriction...restrictions) {
		String sql = SqlHelper.select(tableName).columns("*").where(restrictions).toSqlString();
		sql += getLimitSql(pageNo, pageSize);
		return jdbcTemplate.queryForList(sql, Restriction.whereValues(restrictions));
	}

	@Transactional
	public <E> PageResultWrapper<E> getPage(final RowMapper<E> bean, int pageNo, int pageSize) {
		PageResultWrapper<E> wrapper = new PageResultWrapper<E>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount();
			}
			@Override
			public List<E> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(bean, pageNo, pageSize);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public PageResultWrapper<Map<String, Object>> getPage(int pageNo, int pageSize) {
		PageResultWrapper<Map<String, Object>> wrapper = new PageResultWrapper<Map<String, Object>>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount();
			}
			@Override
			public List<Map<String, Object>> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(pageNo, pageSize);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public <E> PageResultWrapper<E> getPage(final RowMapper<E> bean, int pageNo, int pageSize, final Restriction...restrictions) {
		PageResultWrapper<E> wrapper = new PageResultWrapper<E>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount(restrictions);
			}
			@Override
			public List<E> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(bean, pageNo, pageSize, restrictions);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public PageResultWrapper<Map<String, Object>> getPage(int pageNo, int pageSize, final Restriction...restrictions) {
		PageResultWrapper<Map<String, Object>> wrapper = new PageResultWrapper<Map<String, Object>>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount(restrictions);
			}
			@Override
			public List<Map<String, Object>> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(pageNo, pageSize, restrictions);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public int getCount(String sql, Restriction...restrictions) {
		if (restrictions == null || restrictions.length < 1) {
			return jdbcTemplate.queryForObject(sql, Integer.class);
		}
		return jdbcTemplate.queryForObject(sql, Integer.class, Restriction.whereValues(restrictions));
	}

	@Transactional
	public <E> List<E> getList(String sql, RowMapper<E> bean, Restriction...restrictions) {
		if (restrictions == null || restrictions.length < 1) {
			return jdbcTemplate.query(sql, bean);
		}
		return jdbcTemplate.query(sql, bean, Restriction.whereValues(restrictions));
	}

	@Transactional
	public List<Map<String, Object>> getList(String sql, Restriction...restrictions) {
		if (restrictions == null || restrictions.length < 1) {
			return jdbcTemplate.queryForList(sql);
		}
		return jdbcTemplate.queryForList(sql, Restriction.whereValues(restrictions));
	}

	@Transactional
	public <E> List<E> getList(String sql, RowMapper<E> bean, int pageNo, int pageSize, Restriction...restrictions) {
		sql += getLimitSql(pageNo, pageSize);
		if (restrictions == null || restrictions.length < 1) {
			return jdbcTemplate.query(sql, bean);
		}
		return jdbcTemplate.query(sql, bean, Restriction.whereValues(restrictions));
	}

	@Transactional
	public List<Map<String, Object>> getList(String sql, int pageNo, int pageSize, Restriction...restrictions) {
		sql += getLimitSql(pageNo, pageSize);
		if (restrictions == null || restrictions.length < 1) {
			return jdbcTemplate.queryForList(sql);
		}
		return jdbcTemplate.queryForList(sql, Restriction.whereValues(restrictions));
	}

	@Transactional
	public <E> PageResultWrapper<E> getPage(final String countSql, final String dataSql, final RowMapper<E> bean, int pageNo, int pageSize, final Restriction...restrictions) {
		PageResultWrapper<E> wrapper = new PageResultWrapper<E>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount(countSql, restrictions);
			}
			@Override
			public List<E> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(dataSql, bean, pageNo, pageSize, restrictions);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public PageResultWrapper<Map<String, Object>> getPage(final String countSql, final String dataSql, int pageNo, int pageSize, final Restriction...restrictions) {
		PageResultWrapper<Map<String, Object>> wrapper = new PageResultWrapper<Map<String, Object>>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount(countSql, restrictions);
			}
			@Override
			public List<Map<String, Object>> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(dataSql, pageNo, pageSize, restrictions);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public int getCount(String sql, Object...args) {
		return jdbcTemplate.queryForObject(sql, Integer.class, args);
	}

	@Transactional
	public <E> List<E> getList(String sql, RowMapper<E> bean, Object...args) {
		return jdbcTemplate.query(sql, bean, args);
	}

	@Transactional
	public List<Map<String, Object>> getList(String sql, Object...args) {
		return jdbcTemplate.queryForList(sql, args);
	}

	@Transactional
	public <E> List<E> getList(String sql, RowMapper<E> bean, int pageNo, int pageSize, Object...args) {
		sql += getLimitSql(pageNo, pageSize);
		return jdbcTemplate.query(sql, bean, args);
	}

	@Transactional
	public List<Map<String, Object>> getList(String sql, int pageNo, int pageSize, Object...args) {
		sql += getLimitSql(pageNo, pageSize);
		return jdbcTemplate.queryForList(sql, args);
	}

	@Transactional
	public <E> PageResultWrapper<E> getPage(final String countSql, final String dataSql, final RowMapper<E> bean, int pageNo, int pageSize, final Object...args) {
		PageResultWrapper<E> wrapper = new PageResultWrapper<E>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount(countSql, args);
			}
			@Override
			public List<E> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(dataSql, bean, pageNo, pageSize, args);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	@Transactional
	public PageResultWrapper<Map<String, Object>> getPage(final String countSql, final String dataSql, int pageNo, int pageSize, final Object...args) {
		PageResultWrapper<Map<String, Object>> wrapper = new PageResultWrapper<Map<String, Object>>(pageNo, pageSize) {
			@Override
			public int queryCount() throws Exception {
				return getCount(countSql, args);
			}
			@Override
			public List<Map<String, Object>> query(int pageStartIndex, int pageNo, int pageSize) throws Exception {
				return getList(dataSql, pageNo, pageSize, args);
			}
		};
		try {
			wrapper.paging();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	protected String getLimitSql(int pageNo, int pageSize) {
		return " limit " + getStartIndex(pageNo, pageSize) + "," + pageSize;
	}

	protected int getStartIndex(int pageNo, int pageSize) {
		return (pageNo - 1) * pageSize;
	}

	/**
	 * 仅适用于 数据库主键自动增长 类型的表
	 * @param id
	 */
	public void delete(Long id) {
		String sql = "delete from " + tableName + " where id = ?";
		jdbcTemplate.update(sql, id);
	}

	/**
	 * 仅适用于 数据库主键自动增长 类型的表
	 * @param sql insert语句
	 * @param values 值
	 * @return
	 */
	@Transactional
	public Long saveAndGetId(final String sql, final Object...values) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < values.length; i++) {
					ps.setObject(i + 1, values[i]);
				}
                return ps;
			}
		}, holder);
		return holder.getKey().longValue();
	}

	public void update(String sql, Object...values) {
		jdbcTemplate.update(sql, values);
	}
}
