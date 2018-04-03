package com.im.spring.mapper;

/**
 * Default type convert.
 * @author lizhou
 */
public class DefaultConverter implements TypeConverter {
	public Object convert(Object v) {
		return v;
	}
}
