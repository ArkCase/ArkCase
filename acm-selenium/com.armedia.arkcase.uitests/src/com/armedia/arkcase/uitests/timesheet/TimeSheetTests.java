package com.armedia.arkcase.uitests.timesheet;

import org.junit.Test;
import org.openqa.selenium.support.PageFactory;

import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class TimeSheetTests extends ArkCaseTestBase {

	TimeSheetPage timesheet = PageFactory.initElements(driver, TimeSheetPage.class);

	@Test
	public void createNewTimeSheet() throws InterruptedException {

		super.logIn();
		Thread.sleep(10000);
		timesheet.clickNewButton();
		Thread.sleep(3000);
		timesheet.clickNewTimeSheetBtn();
		Thread.sleep(15000);
		driver.switchTo().frame(timesheet.firstIframe);
		Thread.sleep(3000);
		driver.switchTo().frame(timesheet.secondIframe);
		Thread.sleep(3000);
		timesheet.verifyTimeTrackinTitle();
		timesheet.clickType();
		Thread.sleep(3000);
		timesheet.verifyTypeDropDown();
		Thread.sleep(2000);
		timesheet.selectTypeCase();
		Thread.sleep(3000);
		timesheet.clickChargeCode();
		Thread.sleep(2000);
		timesheet.verifyChargeCodeDropDown();
		Thread.sleep(2000);
		timesheet.selectFirstChargeCode();
		Thread.sleep(3000);
		timesheet.setFirstDay("8");
		Thread.sleep(2000);
		timesheet.setSecondtDay("8");
		Thread.sleep(2000);
		timesheet.setThirdDay("8");
		Thread.sleep(2000);
		timesheet.setForthDay("8");
		Thread.sleep(2000);
		timesheet.setFifthDay("8");
		Thread.sleep(2000);
		timesheet.setSixthDay("8");
		Thread.sleep(2000);
		timesheet.setSeventhDay("8");
		Thread.sleep(2000);
		timesheet.setDetailsText("Test");
		Thread.sleep(2000);
		timesheet.clickSelectForApprover();
		Thread.sleep(3000);
		timesheet.verifyAddUserForm();
		timesheet.searchForUserInput("Samuel Supervisor");
		Thread.sleep(2000);
		timesheet.clickGoButton();
		Thread.sleep(4000);
		timesheet.noDataAvialible();
		timesheet.verifySearchedUser("Samuel Supervisor", "USER", "samuel-acm");
		timesheet.searchedName.click();
		Thread.sleep(3000);
		timesheet.clickAddButton();
		Thread.sleep(3000);
		timesheet.clickSendForApprovalBtn();
		Thread.sleep(15000);
		ArkCaseAuthentication.logOut(driver);

	}

}
