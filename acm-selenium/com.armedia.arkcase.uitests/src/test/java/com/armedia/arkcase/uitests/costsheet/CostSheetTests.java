package com.armedia.arkcase.uitests.costsheet;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.support.PageFactory;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.group.SmokeTests;

public class CostSheetTests extends ArkCaseTestBase {

	CostsheetPage costsheet = PageFactory.initElements(driver, CostsheetPage.class);
	CostTrackingPage costTracking = PageFactory.initElements(driver, CostTrackingPage.class);

	@Test
	public void createNewCostSheetVerifyCreatedSheetInformationRibbon()
			throws InterruptedException, IOException, AWTException {

		costsheet.waitUntilPageIsLoaded();
		costsheet.newButton.click();
		Thread.sleep(3000);
		costsheet.clickNewCostSheetBtn();
		Thread.sleep(10000);
		driver.switchTo().frame(costsheet.firstIframe);
		driver.switchTo().frame(costsheet.secondIframe);
		costsheet.verifyExpensesTitle();
		costsheet.clickTypeDropDown();
		Thread.sleep(3000);
		costsheet.clickTypeCase();
		Thread.sleep(3000);
		costsheet.clickCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickFirstOptionInCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickDateCalendar();
		Thread.sleep(5000);
		costsheet.verifyCalnedarDate();
		Thread.sleep(3000);
		costsheet.selectDateFromCalendar();
		Thread.sleep(4000);
		costsheet.clickTitleDropDown();
		Thread.sleep(3000);
		costsheet.selectTitleTaxi();
		Thread.sleep(3000);
		costsheet.descriptionInput("taxi");
		Thread.sleep(3000);
		costsheet.amountInput("10000");
		Thread.sleep(3000);
		costsheet.verifyBalanceLable();
		Thread.sleep(3000);
		costsheet.clickAddFilesBtn();
		Thread.sleep(3000);
		costsheet.clickBrowseBtn();
		Thread.sleep(2000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(3000);
		costsheet.clickUploadBtn();
		Thread.sleep(5000);
		costsheet.verifyUploadedFile("ArkCaseTesting");
		costsheet.detailsInput("Test");
		Thread.sleep(2000);
		costsheet.clickSelectForApprovel();
		Thread.sleep(3000);
		costsheet.verifyAddUserPopUp();
		costsheet.verifyAddUserTitle();
		costsheet.searchForUserInput("Samuel Supervisor");
		Thread.sleep(3000);
		costsheet.clickGoBtn();
		Thread.sleep(4000);
		costsheet.verifySearchedUser("Samuel Supervisor", "samuel-acm");
		Thread.sleep(3000);
		costsheet.clickSearchedUser();
		Thread.sleep(3000);
		costsheet.clickAddBtn();
		Thread.sleep(4000);
		costsheet.clickSendForApproval();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		Thread.sleep(3000);
		costTracking.clickSortButton();
		Thread.sleep(3000);
		costTracking.clickSortDateDesc();
		Thread.sleep(3000);
		costTracking.clickFirstCostsheet();
		Thread.sleep(4000);
		costTracking.verifyCostTrackingTitle();
		costTracking.verifyInformationRibbon("Major Issue", "20160720_107", "DRAFT", "Medium", "Samuel Supervisor",
				"Retaliation");
		costTracking.verifyButtons();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCostSheetVerifyDetails() throws InterruptedException {

		costsheet.waitUntilPageIsLoaded();
		costsheet.newButton.click();
		Thread.sleep(3000);
		costsheet.clickNewCostSheetBtn();
		Thread.sleep(10000);
		driver.switchTo().frame(costsheet.firstIframe);
		driver.switchTo().frame(costsheet.secondIframe);
		costsheet.verifyExpensesTitle();
		costsheet.clickTypeDropDown();
		Thread.sleep(3000);
		costsheet.clickTypeCase();
		Thread.sleep(3000);
		costsheet.clickCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickFirstOptionInCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickDateCalendar();
		Thread.sleep(5000);
		costsheet.verifyCalnedarDate();
		Thread.sleep(3000);
		costsheet.selectDateFromCalendar();
		Thread.sleep(4000);
		costsheet.clickTitleDropDown();
		Thread.sleep(3000);
		costsheet.selectTitleTaxi();
		Thread.sleep(3000);
		costsheet.descriptionInput("taxi");
		Thread.sleep(3000);
		costsheet.amountInput("10000");
		Thread.sleep(3000);
		costsheet.verifyBalanceLable();
		Thread.sleep(3000);
		costsheet.detailsInput("Test");
		Thread.sleep(2000);
		costsheet.clickSelectForApprovel();
		Thread.sleep(3000);
		costsheet.verifyAddUserPopUp();
		costsheet.verifyAddUserTitle();
		costsheet.searchForUserInput("Samuel Supervisor");
		Thread.sleep(3000);
		costsheet.clickGoBtn();
		Thread.sleep(4000);
		costsheet.verifySearchedUser("Samuel Supervisor", "samuel-acm");
		Thread.sleep(3000);
		costsheet.clickSearchedUser();
		Thread.sleep(3000);
		costsheet.clickAddBtn();
		Thread.sleep(4000);
		costsheet.clickSaveBtn();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		Thread.sleep(3000);
		costTracking.clickSortButton();
		Thread.sleep(3000);
		costTracking.clickSortDateDesc();
		Thread.sleep(3000);
		costTracking.clickFirstCostsheet();
		Thread.sleep(4000);
		costTracking.clickDetailsLink();
		Thread.sleep(4000);
		costTracking.verifyDetailsSection();
		costTracking.verifyDetailsTextArea("Test");
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCostsheetVerifyAddedPeople() throws InterruptedException, IOException {

		costsheet.waitUntilPageIsLoaded();
		costsheet.newButton.click();
		Thread.sleep(3000);
		costsheet.clickNewCostSheetBtn();
		Thread.sleep(10000);
		driver.switchTo().frame(costsheet.firstIframe);
		driver.switchTo().frame(costsheet.secondIframe);
		costsheet.verifyExpensesTitle();
		costsheet.clickTypeDropDown();
		Thread.sleep(3000);
		costsheet.clickTypeCase();
		Thread.sleep(3000);
		costsheet.clickCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickFirstOptionInCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickDateCalendar();
		Thread.sleep(5000);
		costsheet.verifyCalnedarDate();
		Thread.sleep(3000);
		costsheet.selectDateFromCalendar();
		Thread.sleep(4000);
		costsheet.clickTitleDropDown();
		Thread.sleep(3000);
		costsheet.selectTitleTaxi();
		Thread.sleep(3000);
		costsheet.descriptionInput("taxi");
		Thread.sleep(3000);
		costsheet.amountInput("10000");
		Thread.sleep(3000);
		costsheet.verifyBalanceLable();
		Thread.sleep(2000);
		costsheet.clickSelectForApprovel();
		Thread.sleep(3000);
		costsheet.verifyAddUserPopUp();
		costsheet.verifyAddUserTitle();
		costsheet.searchForUserInput("Samuel Supervisor");
		Thread.sleep(3000);
		costsheet.clickGoBtn();
		Thread.sleep(4000);
		costsheet.verifySearchedUser("Samuel Supervisor", "samuel-acm");
		Thread.sleep(3000);
		costsheet.clickSearchedUser();
		Thread.sleep(3000);
		costsheet.clickAddBtn();
		Thread.sleep(4000);
		costsheet.clickSaveBtn();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		Thread.sleep(4000);
		costTracking.clickSortButton();
		Thread.sleep(3000);
		costTracking.clickSortDateDesc();
		Thread.sleep(3000);
		costTracking.clickFirstCostsheet();
		Thread.sleep(4000);
		costTracking.clickPersonLink();
		Thread.sleep(4000);
		costTracking.verifyPersonTable();
		costTracking.verifyIfPersonIsShown();
		costTracking.verifyAddedPerson("Samuel Supervisor", "samuel-acm");
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void createNewCostsheetVerifyCostSummaryTableData() throws InterruptedException {

		costsheet.waitUntilPageIsLoaded();
		costsheet.newButton.click();
		Thread.sleep(3000);
		costsheet.clickNewCostSheetBtn();
		Thread.sleep(10000);
		driver.switchTo().frame(costsheet.firstIframe);
		driver.switchTo().frame(costsheet.secondIframe);
		costsheet.verifyExpensesTitle();
		costsheet.clickTypeDropDown();
		Thread.sleep(3000);
		costsheet.clickTypeCase();
		Thread.sleep(3000);
		costsheet.clickCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickFirstOptionInCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickDateCalendar();
		Thread.sleep(5000);
		costsheet.verifyCalnedarDate();
		Thread.sleep(3000);
		costsheet.selectDateFromCalendar();
		Thread.sleep(4000);
		costsheet.clickTitleDropDown();
		Thread.sleep(3000);
		costsheet.selectTitleTaxi();
		Thread.sleep(3000);
		costsheet.descriptionInput("taxi");
		Thread.sleep(3000);
		costsheet.amountInput("10000");
		Thread.sleep(3000);
		costsheet.verifyBalanceLable();
		Thread.sleep(2000);
		costsheet.clickSelectForApprovel();
		Thread.sleep(3000);
		costsheet.verifyAddUserPopUp();
		costsheet.verifyAddUserTitle();
		costsheet.searchForUserInput("Samuel Supervisor");
		Thread.sleep(3000);
		costsheet.clickGoBtn();
		Thread.sleep(4000);
		costsheet.verifySearchedUser("Samuel Supervisor", "samuel-acm");
		Thread.sleep(3000);
		costsheet.clickSearchedUser();
		Thread.sleep(3000);
		costsheet.clickAddBtn();
		Thread.sleep(4000);
		costsheet.clickSaveBtn();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		Thread.sleep(4000);
		costTracking.clickSortButton();
		Thread.sleep(3000);
		costTracking.clickSortDateDesc();
		Thread.sleep(3000);
		costTracking.clickFirstCostsheet();
		Thread.sleep(4000);
		costTracking.clickCostSummaryLink();
		Thread.sleep(4000);
		costTracking.verifyCostSummaryTable();
		costTracking.verifyCostsheetValuesInCostSummaryTable("CASE_FILE", "10000", "Taxi", "taxi");
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCostSheetEditDetailsVerifyUpdatedTextArea() throws InterruptedException {

		costsheet.waitUntilPageIsLoaded();
		costsheet.newButton.click();
		Thread.sleep(3000);
		costsheet.clickNewCostSheetBtn();
		Thread.sleep(10000);
		driver.switchTo().frame(costsheet.firstIframe);
		driver.switchTo().frame(costsheet.secondIframe);
		costsheet.verifyExpensesTitle();
		costsheet.clickTypeDropDown();
		Thread.sleep(3000);
		costsheet.clickTypeCase();
		Thread.sleep(3000);
		costsheet.clickCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickFirstOptionInCodeDropDown();
		Thread.sleep(3000);
		costsheet.clickDateCalendar();
		Thread.sleep(5000);
		costsheet.verifyCalnedarDate();
		Thread.sleep(3000);
		costsheet.selectDateFromCalendar();
		Thread.sleep(4000);
		costsheet.clickTitleDropDown();
		Thread.sleep(3000);
		costsheet.selectTitleTaxi();
		Thread.sleep(3000);
		costsheet.descriptionInput("taxi");
		Thread.sleep(3000);
		costsheet.amountInput("10000");
		Thread.sleep(3000);
		costsheet.verifyBalanceLable();
		Thread.sleep(2000);
		costsheet.clickSelectForApprovel();
		Thread.sleep(3000);
		costsheet.verifyAddUserPopUp();
		costsheet.verifyAddUserTitle();
		costsheet.searchForUserInput("Samuel Supervisor");
		Thread.sleep(3000);
		costsheet.clickGoBtn();
		Thread.sleep(4000);
		costsheet.verifySearchedUser("Samuel Supervisor", "samuel-acm");
		Thread.sleep(3000);
		costsheet.clickSearchedUser();
		Thread.sleep(3000);
		costsheet.clickAddBtn();
		Thread.sleep(4000);
		costsheet.clickSaveBtn();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		Thread.sleep(4000);
		costTracking.clickSortButton();
		Thread.sleep(3000);
		costTracking.clickSortDateDesc();
		Thread.sleep(3000);
		costTracking.clickFirstCostsheet();
		Thread.sleep(4000);
		costTracking.clickDetailsLink();
		Thread.sleep(4000);
		costTracking.detailsInput("This is test");
		Thread.sleep(2000);
		costTracking.clickDetailsSaveBtn();
		Thread.sleep(5000);
		costTracking.refreshPageBtn.click();
		Thread.sleep(4000);
		costTracking.verifyUpdatedDetailsTextArea("This is test");
		ArkCaseAuthentication.logOut(driver);

	}

}
