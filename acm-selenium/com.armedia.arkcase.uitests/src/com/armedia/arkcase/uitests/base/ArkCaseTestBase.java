package com.armedia.arkcase.uitests.base;

import java.io.File;
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
	public static String screenshotLocatie = System.getProperty("user.home")
			+ "/.arkcase/seleniumTests/seleniumReports";
	public ExtentReports report;

	@Rule
	public ScreenshotTaker screenShootRule = new ScreenshotTaker(driver, screenshotLocatie, report);

	@BeforeClass
	public static void setUp() throws Exception {

		CheckIfFileIsDownloaded folder = new CheckIfFileIsDownloaded();
		folder.createFolder();
		FirefoxProfile fprofile = new FirefoxProfile();
		File file = new File("/.arkcase/seleniumTests/seleniumDownload/");
		fprofile.setPreference("browser.download.dir", System.getProperty("user.home") + file);
		fprofile.setPreference("browser.download.folderList", 2);
		fprofile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;" + "application/pdf;"
						+ "application/vnd.openxmlformats-officedocument.wordprocessingml.document;" + "text/plain; "
						+ "img/png" + "text/csv");
		fprofile.setPreference("browser.download.manager.showWhenStarting", false);
		fprofile.setPreference("pdfjs.disabled", true);
		driver = new FirefoxDriver(fprofile);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		baseUrl = TestsPoperties.getBaseURL();
		driver.manage().window().maximize();

	}

	@AfterClass
	public static void shutDown() throws Exception {

		CheckIfFileIsDownloaded folder = new CheckIfFileIsDownloaded();
		folder.deleteFolder();
		driver.close();
		driver.quit();
	}
}
