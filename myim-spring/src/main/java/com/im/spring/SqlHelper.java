package com.im.spring;

import java.util.Arrays;

public class SqlHelper {

	public static Insert insert(String tableName) {
		return Insert.table(tableName);
	}

	public static Delete delete(String tableName) {
		return Delete.table(tableName);
	}

	public static Select select(String tableName) {
		return Select.table(tableName);
	}

	public static Update update(String tableName) {
		return Update.table(tableName);
	}

	public static class Insert {
		private StringBuilder sql = new StringBuilder("insert into ");
		public static Insert table(String tableName) {
			return new Insert().tableName(tableName);
		}
		private Insert tableName(String tableName) {
			sql.append(tableName);
			return this;
		}
		public String columns(String... columns) {
			sql.append("(");
			String names = "";
			String values = "";
			for (String column : columns) {
				names += column.trim() + ", ";
				values += "?, ";
			}
			if (names.length() > 1) {
				names = names.substring(0, names.length() - 2);
				values = values.substring(0, values.length() - 2);
			}
			sql.append(names);
			sql.append(") values (");
			sql.append(values);
			sql.append(")");
			return sql.toString();
		}

		public String columns(String columns) {
			String[] ss = columns.split(",");
			return columns(ss);
		}
	}

	public static class Delete {
		private StringBuilder sql = new StringBuilder("delete from ");
		public static Delete table(String tableName) {
			return new Delete().tableName(tableName);
		}
		private Delete tableName(String tableName) {
			sql.append(tableName).append(" ");
			return this;
		}
		public String where(Restriction...restrictions) {
			if (restrictions == null || restrictions.length < 1) {
				return sql.toString();
			}
			sql.append(Restriction.whereSql(restrictions));
			return sql.toString();
		}
		public String where(String where) {
			if (where == null || "".equals(where.trim())) {
				return sql.toString();
			}
			sql.append("where ").append(where);
			return sql.toString();
		}
	}

	public static class Select {
		private StringBuilder sql = new StringBuilder();
		private String tableName;
		private String countColumns = "*";
		private String columns = "*";
		private boolean useCount = false;
		public static Select table(String tableName) {
			Select select = new Select();
			select.tableName = tableName;
			return select;
		}
		public Select count(String columns) {
			useCount = true;
			this.countColumns = columns;
			return this;
		}
		public Select columns(String columns) {
			this.columns = columns;
			return this;
		}
		public Select columns(String... columns) {
			this.columns = Restriction.join(Arrays.asList(columns), ",");
			return this;
		}
		public Select leftJoin(String tableName) {
			sql.append("left join ").append(tableName).append(" ");
			return this;
		}
		public Select rightJoin(String tableName) {
			sql.append("right join ").append(tableName).append(" ");
			return this;
		}
		public Select innerJoin(String tableName) {
			sql.append("inner join ").append(tableName).append(" ");
			return this;
		}
		public Select on(String matchColumn) {
			sql.append("on ").append(matchColumn).append(" ");
			return this;
		}
		public Select where(Restriction...restrictions) {
			if (restrictions == null || restrictions.length < 1) {
				return this;
			}
			sql.append(Restriction.whereSql(restrictions));
			return this;
		}
		public Select where(String where) {
			if (where == null || "".equals(where.trim())) {
				return this;
			}
			sql.append("where ").append(where);
			return this;
		}
		public Select groupBy(String columns) {
			sql.append("group by ").append(columns).append(" ");
			return this;
		}
		public Select having(String express) {
			sql.append("having ").append(express).append(" ");
			return this;
		}
		public String toCountString() {
			StringBuilder r = new StringBuilder();
			r.append("select count(").append(countColumns).append(") from ").append(tableName).append(" ");
			r.append(sql).append(" ");
			return r.toString();
		}
		public String toSqlString() {
			StringBuilder r = new StringBuilder();
			r.append("select ").append(columns).append(" from ").append(tableName).append(" ");
			r.append(sql).append(" ");
			return r.toString();
		}
		@Override
		public String toString() {
			if (useCount == true) {
				return toCountString();
			}
			return toSqlString();
		}
	}

	public static class Update {
		private StringBuilder sql = new StringBuilder("update ");
		public static Update table(String tableName) {
			Update update = new Update();
			update.sql.append(tableName).append(" ").append("set ");
			return update;
		}
		public Update columns(String columns) {
			sql.append(columns).append(" ");
			return this;
		}
		public String where(Restriction...restrictions) {
			if (restrictions == null || restrictions.length < 1) {
				return sql.toString();
			}
			sql.append(Restriction.whereSql(restrictions)).append(" ");
			return sql.toString();
		}
		public String where(String where) {
			if (where == null || "".equals(where.trim())) {
				return sql.append(" ").toString();
			}
			sql.append("where ").append(where).append(" ");
			return sql.toString();
		}
	}
}
