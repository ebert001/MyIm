package com.im.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql语句的条件约束
 * @author lizhou
 *
 */
public class Restriction {
	/** 等于 */
	public static final String EQ = "=";
	/** 不等于 */
	public static final String NOT_EQ = "!=";
	/** 小于 */
	public static final String LT = "<";
	/** 大于 */
	public static final String GT = ">";
	/** 小于等于 */
	public static final String LE = "<=";
	/** 大于等于 */
	public static final String GE = ">=";
	/** 模糊like */
	public static final String LIKE = "like";
	/** 模糊like 取反 */
	public static final String NOT_LIKE = "not like";
	/** 之间 between */
	public static final String BETWEEN = "between";
	/** in */
	public static final String IN = "in";
	/** not in */
	public static final String NOT_IN = "not in";

	/** 空 null */
	public static final String IS_NULL = "is null";
	/** 不为空 not null */
	public static final String IS_NOT_NULL = "is not null";
	/** 升序 */
	public static final String ORDER_ASC = "asc";
	/** 降序 */
	public static final String ORDER_DESC = "desc";

	private String fieldName;
	/** order 会使用到 */
	private List<String> fieldNames = new ArrayList<String>();

	private Object value;
	/** between, in, not in会使用到 */
	private List<Object> values = new ArrayList<Object>();

	private String type;

	private String boundSymbol = "and";

	public Restriction() {
	}

	public Restriction(String fieldName, String type) {
		this.fieldName = fieldName;
		this.type = type.toLowerCase();
	}

	public Restriction(String fieldName, String type, Object value) {
		this(fieldName, type);
		this.value = value;
	}

	// between 和 in 使用
	public Restriction(String fieldName, String type, List<? extends Object> values) {
		this(fieldName, type);
		this.values.addAll(values);
	}

	public Restriction(String fieldName, String type, Object... values) {
		this(fieldName, type);
		this.values.addAll(Arrays.asList(values));
	}

	// order 使用
	public Restriction(String type, String... fieldNames) {
		this.fieldNames.addAll(Arrays.asList(fieldNames));
		this.type = type.toLowerCase();
	}

	public static Restriction le(String fieldName, Object value) {
		return new Restriction(fieldName, LE, value);
	}

	public static Restriction ge(String fieldName, Object value) {
		return new Restriction(fieldName, GE, value);
	}

	public static Restriction eq(String fieldName, Object value) {
		return new Restriction(fieldName, EQ, value);
	}

	public static Restriction notEq(String fieldName, Object value) {
		return new Restriction(fieldName, NOT_EQ, value);
	}

	public static Restriction lt(String fieldName, Object value) {
		return new Restriction(fieldName, LT, value);
	}

	public static Restriction gt(String fieldName, Object value) {
		return new Restriction(fieldName, GT, value);
	}

	public static Restriction likeBefore(String fieldName, Object value) {
		if (value == null || "".equals(String.valueOf(value))) {
			return null;
		}
		return like(fieldName, "%" + value);
	}

	public static Restriction likeAfter(String fieldName, Object value) {
		if (value == null || "".equals(String.valueOf(value))) {
			return null;
		}
		return new Restriction(fieldName, LIKE, value + "%");
	}

	public static Restriction like(String fieldName, Object value) {
		if (value == null || "".equals(String.valueOf(value))) {
			return null;
		}
		return new Restriction(fieldName, LIKE, "%" + value + "%");
	}

	public static Restriction notLike(String fieldName, Object value) {
		return new Restriction(fieldName, NOT_LIKE, value);
	}

	public static Restriction between(String fieldName, Object value1, Object value2) {
		return new Restriction(fieldName, BETWEEN, Arrays.asList(value1, value2));
	}

	public static Restriction in(String fieldName, Object... values) {
		return new Restriction(fieldName, IN, Arrays.asList(values));
	}

	public static Restriction in(String fieldName, List<? extends Object> values) {
		return new Restriction(fieldName, IN, values);
	}

	public static Restriction notIn(String fieldName, Object... values) {
		return new Restriction(fieldName, NOT_IN, Arrays.asList(values));
	}

	public static Restriction notIn(String fieldName, List<Object> values) {
		return new Restriction(fieldName, NOT_IN, values);
	}

	public static Restriction isNull(String fieldName) {
		return new Restriction(fieldName, IS_NULL);
	}

	public static Restriction isNotNull(String fieldName) {
		return new Restriction(fieldName, IS_NOT_NULL);
	}

	public static Restriction orderByAsc(String... fieldNames) {
		return new Restriction(ORDER_ASC, fieldNames);
	}

	public static Restriction orderByDesc(String... fieldNames) {
		return new Restriction(ORDER_DESC, fieldNames);
	}

	public static Restriction or(Restriction restriction) {
		Restriction r = new Restriction();
		r.boundSymbol = "or";
		r.fieldName = restriction.fieldName;
		r.fieldNames = restriction.fieldNames;
		r.type = restriction.type;
		r.value = restriction.value;
		r.values = restriction.values;
		return r;
	}

	public String toSqlString() {
		if (EQ.equalsIgnoreCase(type) || NOT_EQ.equalsIgnoreCase(type) || LT.equalsIgnoreCase(type)
				|| GT.equalsIgnoreCase(type) || LE.equalsIgnoreCase(type) || GE.equalsIgnoreCase(type)
				|| LIKE.equalsIgnoreCase(type) || NOT_LIKE.equalsIgnoreCase(type)) {
			return buildString();
		}
		if (IS_NULL.equalsIgnoreCase(type) || IS_NOT_NULL.equalsIgnoreCase(type)) {
			return buildStringOfNull();
		}
		if (BETWEEN.equalsIgnoreCase(type)) {
			return buildStringOfBetween();
		}
		if (IN.equalsIgnoreCase(type) || NOT_IN.equalsIgnoreCase(type)) {
			return buildStringOfIn();
		}
		if (ORDER_ASC.equalsIgnoreCase(type) || ORDER_DESC.equalsIgnoreCase(type)) {
			return buildStringOfOrder();
		}
		return getBoundSymbol(boundSymbol) + fieldName + " = ? ";
	}

	/** 获取连接符(and, or)。如果是第一个块，不需要使用and,or */
	private String getBoundSymbol(String boundSymbol) {
		return boundSymbol + " ";
	}
	private String buildString() {
		return new StringBuilder(getBoundSymbol(boundSymbol))
			.append(fieldName).append(" ").append(type).append(" ? ")
			.toString();
	}
	private String buildStringOfOrder() {
		StringBuilder sb = new StringBuilder("order by ");
		sb.append(join(fieldNames, ", ")).append(" ").append(type).append(" ");
		return sb.toString();
	}
	private String buildStringOfNull() {
		return new StringBuilder(getBoundSymbol(boundSymbol))
			.append(fieldName).append(" ").append(type).append(" ")
			.toString();
	}
	private String buildStringOfBetween() {
		if (values.size() != 2) {
			throw new IllegalArgumentException("between must have two values.");
		}
		return new StringBuilder(getBoundSymbol(boundSymbol))
			.append(fieldName).append(" ").append(type).append(" ? and ? ")
			.toString();
	}
	private String buildStringOfIn() {
		StringBuilder sb = new StringBuilder(getBoundSymbol(boundSymbol));
		sb.append(fieldName).append(" ").append(type).append("(");
		sb.append(repeat("?", values.size(), ","));
		sb.append(") ");
		return sb.toString();
	}

	public static String restrictionSql(Restriction...restrictions) {
		StringBuilder sb = new StringBuilder("");
		if (restrictions == null || restrictions.length < 1) {
			return sb.toString();
		}
		List<Restriction> orderRestrictions = new ArrayList<Restriction>();
		int num = 0;
		for (int i = 0; i < restrictions.length; i++) {
			Restriction restriction = restrictions[i];
			if (restriction == null) {
				continue;
			}
			String type = restriction.getType();
			if (Restriction.IN.equals(type) || Restriction.NOT_IN.equals(type) || Restriction.BETWEEN.equals(type)) { // 多值
				if (restriction.getValues().size() < 1) { // 除去空值，这部分将不构成sql语句
					continue;
				}
			} else if (Restriction.IS_NULL.equals(type) || Restriction.IS_NOT_NULL.equals(type)) { // 无值
				// 不存在空值，但是是sql语句的一部分
			} else if (Restriction.ORDER_ASC.equals(type) || Restriction.ORDER_DESC.equals(type)) { // 无值
				// 考虑到多个order by选项的情况，order by 语句单独处理
				orderRestrictions.add(restriction);
				continue;
			} else { // 一个值
				if (restriction.getValue() == null) { // 除去空值，这部分将不构成sql语句
					continue;
				}
			}
			if (num == 0) {
				restriction.boundSymbol = "";
			}
			sb.append(restriction.toSqlString());
			num++;
		}
		for (int i = 0; i < orderRestrictions.size(); i++) {
			Restriction restriction = orderRestrictions.get(i);
			if (i == 0) {
				sb.append("order by ");
				sb.append(join(restriction.fieldNames, ", ")).append(" ").append(restriction.type).append(" ");
			} else {
				sb.append(", ").append(join(restriction.fieldNames, ", ")).append(" ").append(restriction.type).append(" ");
			}
		}
		sb.append(" ");
		return sb.toString();
	}

	public static String whereSql(Restriction...restrictions) {
		String restrictionSql = restrictionSql(restrictions);
		if ("".equals(restrictionSql.trim())) {
			return restrictionSql;
		}
		if (restrictionSql.startsWith("order")) { // 不需要前缀 where
			return restrictionSql;
		}
		return "where " + restrictionSql;
	}

	public static Object[] whereValues(Restriction...restrictions) {
		List<Object> list = new ArrayList<Object>();
		if (restrictions == null || restrictions.length < 1) {
			return list.toArray();
		}
		for (Restriction restriction : restrictions) {
			if (restriction == null) {
				continue;
			}
			String type = restriction.getType();
			if (Restriction.IN.equals(type) || Restriction.NOT_IN.equals(type) || Restriction.BETWEEN.equals(type)) { // 多值
				if (restriction.getValues().size() < 1) {
					continue;
				}
				list.addAll(restriction.getValues());

			} else if (Restriction.IS_NULL.equals(type) || Restriction.IS_NOT_NULL.equals(type)
					|| Restriction.ORDER_ASC.equals(type) || Restriction.ORDER_DESC.equals(type)) { // 无值
				continue;

			} else { // 一个值
				if (restriction.getValue() == null) {
					continue;
				}
				list.add(restriction.getValue());
			}
		}
		return list.toArray();
	}

	public static Map<String, Object> valueMap(Restriction...restrictions) {
		Map<String, Object> values = new HashMap<String, Object>();
		if (restrictions == null || restrictions.length < 1) {
			return values;
		}
		for (Restriction restriction : restrictions) {
			if (restriction == null) {
				continue;
			}
			String type = restriction.getType();

			if (Restriction.IN.equals(type) || Restriction.NOT_IN.equals(type) || Restriction.BETWEEN.equals(type)) { // 多值
				values.put(restriction.getFieldName(), restriction.getValues());

			} else if (Restriction.IS_NULL.equals(type) || Restriction.IS_NOT_NULL.equals(type)
					|| Restriction.ORDER_ASC.equals(type) || Restriction.ORDER_DESC.equals(type)) { // 无值
				continue;

			} else { // 一个值
				values.put(restriction.getFieldName(), restriction.getValue());
			}
		}
		return values;
	}

	public static String repeat(String s, int num, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			sb.append(s);
			if (i < num -1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public static String join(List<String> values, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			sb.append(values.get(i));
			if (i < values.size() -1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public String getType() {
		return type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public List<Object> getValues() {
		return values;
	}

	public Object getValue() {
		return value;
	}

}
