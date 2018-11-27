package com.aswishes.im.commons.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class Log4jLoader {

	public static void load() {
		ConfigurationSource source = null;
		try {
			source = new ConfigurationSource(new FileInputStream(new File("log4j2.xml")));
			Configurator.initialize(null, source);
		} catch (Exception e) {
		}
	}
}
