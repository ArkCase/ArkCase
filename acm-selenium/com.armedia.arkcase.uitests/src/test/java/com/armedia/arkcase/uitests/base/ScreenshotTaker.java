package com.armedia.arkcase.uitests.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ScreenshotTaker extends TestWatcher {

	private WebDriver browser;
	private String screenshotLocation;
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
	String createdDate = formatter.format(date);

	private String filenameOfReport = System.getProperty("user.home") + "/.arkcase/seleniumTests/SeleniumReports/SeleniumReport"
			+ createdDate + ".html";

	public ScreenshotTaker(WebDriver browser, String screenshotLocatie, ExtentReports report) {
		this.browser = browser;
		this.screenshotLocation = screenshotLocatie;

	}

	@Override
	protected void failed(Throwable e, Description description) {
		TakesScreenshot takesScreenshot = (TakesScreenshot) browser;

		File scrFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

		String destFile = getDestinationFile(description);

		File dest = new File(destFile);
		try {
			FileUtils.copyFile(scrFile, dest);
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		ExtentReports extent = createReport();
		ExtentTest test = extent.startTest(description.getDisplayName(), "Test failed");

		// step log

		test.log(LogStatus.FAIL, "Failure trace Selenium: " + e.toString());
		test.log(LogStatus.INFO, "Screenshot below" + test.addScreenCapture(destFile));

		flushReports(extent, test);

	}

	// When passed only write to the log.
	@Override
	protected void succeeded(Description description) {
		ExtentReports extent = createReport();
		ExtentTest test = extent.startTest(description.getDisplayName(), "Test ended successful ");

		// step log
		test.log(LogStatus.PASS, "-");

		flushReports(extent, test);

	}

	private ExtentReports createReport() {
		ExtentReports extent = new ExtentReports(filenameOfReport, false);

		return extent;
	}

	private void flushReports(ExtentReports extent, ExtentTest test) {
		// ending test

		if (test.getTest().getName().toString().contains("User")) {
			test.assignCategory("User Tests");
		}
		if (test.getTest().getName().toString().contains("Task")) {
			test.assignCategory("Task Tests");
		}
		if (test.getTest().getName().toString().contains("Dashboard")) {
			test.assignCategory("Dashboard Tests");
		}

		if (test.getTest().getName().toString().startsWith("createNewCase")) {
			test.assignCategory("Case Tests");
		}
		if(test.getTest().getName().toString().startsWith("createNewCostSheet")){
			test.assignCategory("Costsheet Tests");
		}

		extent.endTest(test);
		// writing everything to document
		extent.flush();

	}

	private String getDestinationFile(Description description) {
		String userDirectory = screenshotLocation;
		String date = getDateTime();

		String fileName = description.getDisplayName() + "_" + date + ".png";
		// add date of today
		String dateForDir = getDateTime();
		String absoluteFileName = userDirectory + "/" + dateForDir + "/" + fileName;

		return new String(absoluteFileName);

	}

	private String getDateTime() {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy_HH_mm_ss");
		String createdDate = formatter.format(date);

		return createdDate;

	}

}
