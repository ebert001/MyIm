package com.wizarpos.im.spring.mapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RowMapper annotation writen
 * @author lizhou
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {

	/**
	 * specify column name for field mapping.
	 * @return column name for table
	 */
	String name();

	/**
	 * if ignore flag is true, the field will be not in result list.
	 * @return
	 */
	boolean ignore() default false;

	/**
	 * database data type and java data type convert interface.
	 * @return custom convert menthod
	 */
	Class<? extends TypeConverter> typeConvert() default DefaultConverter.class;
}
