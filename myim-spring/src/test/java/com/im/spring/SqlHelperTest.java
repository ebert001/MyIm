package com.im.spring;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.im.spring.SqlHelper.Select;

public class SqlHelperTest {

	@Test
	public void testInsert() {
		String actual = SqlHelper.insert("m_user").columns("name, key_type, key_size, public_exponent, modulus, `desc`");
		String expected = "insert into m_user(name, key_type, key_size, public_exponent, modulus, `desc`) values (?, ?, ?, ?, ?, ?)";
		assertEquals(expected, actual);
	}

	@Test
	public void testSelect() {
		assertEquals("select * from m_user",
				SqlHelper.select("m_user").columns("*").where());

		assertEquals("select count(*) from m_user",
				SqlHelper.select("m_user").count("*").where());

		assertEquals("select count(*) from m_user where  id = ?",
				SqlHelper.select("m_user").count("*").where(Restriction.eq("id", 12L)));

		assertEquals("select count(*) from m_user where id = ?",
				SqlHelper.select("m_user").count("*").where("id = ?"));

		assertEquals("select * from m_user a left join r_user_role b on a.id = b.user_id where a.id = ? ",
				SqlHelper.select("m_user a").columns("*").leftJoin("r_user_role b").on("a.id = b.user_id").where("a.id = ?"));


		Select select = SqlHelper.select("m_user").columns("*").where(Restriction.eq("id", 12L), Restriction.eq("name", null), Restriction.or(Restriction.in("ids", 10L, 20L)));
		assertEquals("select count(*) from m_user where  id = ? or ids in(?,?)",
				select.toCountString());
		assertEquals("select * from m_user where  id = ? or ids in(?,?)",
				select.toCountString());
	}

	@Test
	public void testDelete() {
		assertEquals("delete from m_user where  id = ? and name = ? or ids in(?,?)",
				SqlHelper.delete("m_user").where(Restriction.eq("id", 12L), Restriction.eq("name", "zhangsan"), Restriction.or(Restriction.in("ids", 10L, 20L))));

		assertEquals("delete from m_user where  id = ?",
				SqlHelper.delete("m_user").where(Restriction.or(Restriction.eq("id", 12L))));

		assertEquals("delete from m_user where id = ?",
				SqlHelper.delete("m_user").where("id = ?"));

		assertEquals("delete from m_user",
				SqlHelper.delete("m_user").where());

	}

	@Test
	public void testUpdate() {
		assertEquals("update m_user set name = ?, `desc` = ? ",
				SqlHelper.update("m_user").columns("name = ?, `desc` = ?").where());

		assertEquals("update m_user set name = ?, `desc` = ? where  id = ?",
				SqlHelper.update("m_user").columns("name = ?, `desc` = ?").where(Restriction.eq("id", 12L)));

		assertEquals("update m_user set name = ?, `desc` = ? where id = ?",
				SqlHelper.update("m_user").columns("name = ?, `desc` = ?").where("id = ?"));
	}
}
