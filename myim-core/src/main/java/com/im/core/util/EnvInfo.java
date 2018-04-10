package com.im.core.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 准备当前项目环境
 * @author lizhou
 */
public class EnvInfo {
	private static final Logger logger = LoggerFactory.getLogger(EnvInfo.class);
	private static EnvInfo instance = new EnvInfo();
	private String contextPath = null;
	private String projectName = null;
	private String projectPath = null;
	/** 系统启动时间 */
	private long startTime;
	/** 系统的进程id */
	private int processId;

	private EnvInfo() {
		startTime = System.currentTimeMillis();

		String name = ManagementFactory.getRuntimeMXBean().getName();
		int index = name.indexOf("@");
		processId = Integer.parseInt(name.substring(0, index));
	}

	public int getProcessId() {
		return processId;
	}

	public long getStartTime() {
		return startTime;
	}

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
		return new File(instance.projectPath);
	}

	/**
	 * @return class path with '/'
	 */
	public static String getClassPath() {
		URL url = EnvInfo.class.getResource("/");
		return url.getPath();
	}

}
