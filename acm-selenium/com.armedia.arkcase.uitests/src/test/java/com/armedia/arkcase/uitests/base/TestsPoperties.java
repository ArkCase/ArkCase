package com.armedia.arkcase.uitests.base;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class TestsPoperties {

	private static String BASE_URL_CORE;
	private static String BASE_URL;
	private static String SUPERVISOR_USER_USERNAME;
	private static String SUPERVISOR_USER_PASSWORD;
	private static String SUPERVISOR_USER_PASSWORD_CORE;
	private static String BROWSER;
	private static String REMOTE;
	private static String HUBIP;
	private static String HUBPORT;

	static {
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(
					new File(System.getProperty("user.home") + "/.arkcase/seleniumTests/tests.properties"));
			Properties properties = new Properties();
			properties.load(fileInput);

			BASE_URL_CORE=properties.getProperty("baseUrlCore");
			BASE_URL = properties.getProperty("baseUrl");
			SUPERVISOR_USER_USERNAME = properties.getProperty("user.supervisor.username");
			SUPERVISOR_USER_PASSWORD = properties.getProperty("user.supervisor.password");
			SUPERVISOR_USER_PASSWORD_CORE=properties.getProperty("user.core.supervisor.password");
			BROWSER=properties.getProperty("browser");
			REMOTE=properties.getProperty("remote");
			HUBIP=properties.getProperty("hubIP");
			HUBPORT=properties.getProperty("hubPort");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getBaseURL() {
		return BASE_URL;
	}

	public static String getSupervisorUserUsername() {
		return SUPERVISOR_USER_USERNAME;
	}

	public static String getSupervisorUserPassword() {
		return SUPERVISOR_USER_PASSWORD;
	}
	
	public static String getBaseUrlCore(){
		return BASE_URL_CORE;
	}
	
	public static String getSupervisorUserPasswordCore(){
		return SUPERVISOR_USER_PASSWORD_CORE;
	}
	
	public static String getBrowser() {
		return BROWSER;
	}
	
	public static String getRemoteInfo() {
		return REMOTE;
	}
	
	public static String getHubIP(){
		return HUBIP;
	}
	
	public static String getHubPort(){
		return HUBPORT;
	}
}
