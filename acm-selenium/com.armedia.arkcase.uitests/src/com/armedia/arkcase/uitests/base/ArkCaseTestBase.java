package com.armedia.arkcase.uitests.base;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.relevantcodes.extentreports.ExtentReports;



public class ArkCaseTestBase {

	public static WebDriver driver;
	public static String baseUrl;
	public static String screenshotLocatie = "C:\\Users\\milan.jovanovski\\SleniumTests\\SeleniumReports";
	public ExtentReports report;

	@Rule
	public ScreenshotTaker screenShootRule = new ScreenshotTaker(driver, screenshotLocatie, report);

	
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		FirefoxProfile fprofile = new FirefoxProfile();
		fprofile.setPreference("browser.download.dir", "C:\\Users\\milan.jovanovski\\SleniumTests\\SeleniumDownload"); 
		fprofile.setPreference("browser.download.folderList", 2);
		fprofile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;"+ "application/pdf;"  + "application/vnd.openxmlformats-officedocument.wordprocessingml.document;"  + "text/plain;" + "text/csv");                       
		fprofile.setPreference( "browser.download.manager.showWhenStarting", false );
		fprofile.setPreference( "pdfjs.disabled", true );
		driver = new FirefoxDriver(fprofile);
		driver.manage().timeouts().implicitlyWait(13, TimeUnit.SECONDS);
		baseUrl = "http://cloud.arkcase.com/";
		driver.manage().window().maximize();
		
	
		
	}

	@AfterClass
	public static void shutDown() throws Exception {
		
	   
		driver.close();
		driver.quit();
	}
}
