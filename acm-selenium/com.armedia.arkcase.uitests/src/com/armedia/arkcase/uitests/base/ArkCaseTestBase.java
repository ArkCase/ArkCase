package com.armedia.arkcase.uitests.base;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.naming.spi.InitialContextFactoryBuilder;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.relevantcodes.extentreports.ExtentReports;

public class ArkCaseTestBase {

	public static String browser;
	public static WebDriver driver;
	public static String baseUrl;
	public static String screenshotLocatie = System.getProperty("user.home")
			+ "/.arkcase/seleniumTests/seleniumReports";
	public ExtentReports report;

	@Rule
	public ScreenshotTaker screenShootRule = new ScreenshotTaker(driver, screenshotLocatie, report);

	@BeforeClass
	public static void setUp() throws Exception {

		ArkCaseUtils folder = new ArkCaseUtils();
		folder.createFolder();
		browser = TestsPoperties.getBrowser();
		switch (browser)
		{
		case "firefox":
		   {
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
			//baseUrl = TestsPoperties.getBaseUrlCore();
			driver.manage().window().maximize();
			break;
		}
		case "ie":
		{ 
			String exe = (System.getProperty("user.home") + "/.arkcase/seleniumTests/drivers/IEDriverServer.exe");
			System.setProperty("webdriver.ie.driver", exe);
			driver = new InternetExplorerDriver();
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			//baseUrl = TestsPoperties.getBaseUrlCore();
			driver.manage().window().maximize();
			break;
		}
		case "chrome":
		{
			String exePath = (System.getProperty("user.home") + "/.arkcase/seleniumTests/drivers/chromedriver.exe");
			System.setProperty("webdriver.chrome.driver", exePath);
			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			//baseUrl = TestsPoperties.getBaseUrlCore();
			driver.manage().window().maximize();
			break;
		}
		case "safari":
		{
			driver = new SafariDriver();
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			//baseUrl = TestsPoperties.getBaseUrlCore();
			driver.manage().window().maximize();
			break;
		}
		
		}
		

	}
    
	@Before
	public void logIn() {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPasswordCore(), driver, TestsPoperties.getBaseUrlCore());
	}

	@AfterClass
	public static void shutDown() throws Exception {

		ArkCaseUtils folder = new ArkCaseUtils();
		folder.deleteFolder();
		driver.close();
		driver.quit();
	}
}
