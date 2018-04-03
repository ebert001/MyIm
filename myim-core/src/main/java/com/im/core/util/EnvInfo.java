package com.im.core.util;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 准备当前项目环境
 * @author lizhou
 */
public class EnvInfo {
	private static final Logger logger = LoggerFactory.getLogger(EnvInfo.class);
	private static String contextPath = null;
	private static String projectName = null;
	private static String projectPath = null;
	
	public static String getUserHome() {
		return System.getProperty("user.home");
	}
	
	public static File getProjectConfigDir() {
		File confDir =  new File(getUserHome(), ".myim");
		if (!confDir.exists()) {
			confDir.mkdirs();
		}
		return confDir;
	}
	
	public static String getProjectHome() {
		return System.getProperty("myim.home");
	}
	
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}
	
	public static File getProjectDir() {
		return new File(EnvInfo.projectPath);
	}
	
	/**
	 * @return class path with '/'
	 */
	public static String getClassPath() {
		URL url = EnvInfo.class.getResource("/");
		return url.getPath();
	}
	
}
