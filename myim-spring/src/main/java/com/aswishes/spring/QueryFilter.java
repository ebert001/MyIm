package com.aswishes.spring;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;

/**
 * Format: 
 * filter-SLIKE-username
 * filter-LIN-id
 * 
 * @author lizhou
 */
public class QueryFilter {
	
	private String propertyName = null;
	private Object propertyValue = null;
	private Class<?> propertyClass = null;
	private MatchType matchType = null;

	/** 属性比较类型. */
	public enum MatchType {
		EQ("="), LIKE("like"), IN("in"), NI("not in"), LT("<"), GT(">"), LE("<="), GE(">=");
		private String name;
		private MatchType(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}

	/** 属性数据类型. */
	public enum PropertyType {
		S(String.class), I(Integer.class), L(Long.class), N(Double.class), D(Date.class), B(Boolean.class), C(Character.class);

		private Class<?> clazz;
		private PropertyType(Class<?> clazz) {
			this.clazz = clazz;
		}
		public Class<?> getValue() {
			return clazz;
		}
	}

	public QueryFilter(String filterName, Object value) {
		String[] ss = filterName.split("-", 2);
		String type = ss[0];
		
		propertyClass = Enum.valueOf(PropertyType.class, type.substring(0, 1)).getValue(); // Long
		matchType = Enum.valueOf(MatchType.class, type.substring(1)); // IN
		
		propertyName = ss[1];
		if (matchType == MatchType.IN) {
			propertyValue = ConvertUtils.convert(value, propertyClass);
		} else {
			propertyValue = ConvertUtils.convert(value, propertyClass);
		}
	}
	
	public Restriction toRestriction() {
		return new Restriction(propertyName, matchType.getName(), propertyValue);
	}

	public static List<QueryFilter> buildFromMap(final Map<String, Object> param, final String prefix) {
		List<QueryFilter> list = new ArrayList<QueryFilter>();
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			String name = entry.getKey();
			if (!name.startsWith(prefix)) {
				continue;
			}
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			String pvalue = String.valueOf(value);
			if (pvalue.isEmpty()) {
				continue;
			}
			String pname = name.substring(name.indexOf("-") + 1);
			list.add(new QueryFilter(pname, value));
		}
		return list;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}
	
	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	public MatchType getMatchType() {
		return matchType;
	}
}
