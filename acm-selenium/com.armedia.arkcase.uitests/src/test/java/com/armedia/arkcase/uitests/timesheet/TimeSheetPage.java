package com.armedia.arkcase.uitests.timesheet;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class TimeSheetPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/iframe")
	public WebElement firstIframe;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	public WebElement secondIframe;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[4]/div/div[1]/input[1]")
	WebElement typeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul/li[2]/a")
	WebElement typeCase;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul/li[3]/a")
	WebElement typeComplaint;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul/li[4]/a")
	WebElement typeOther;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[5]/div/div[1]/input[1]")
	WebElement chargeCodeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[5]/div/div[1]/ul/li[2]/a")
	WebElement firstChargeCode;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[8]/div/div[1]/input")
	WebElement firstDayInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[10]/div/div[1]/input")
	WebElement secondDayInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[12]/div/div[1]/input")
	WebElement thirdDatInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[14]/div/div[1]/input")
	WebElement forthDayInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[16]/div/div[1]/input")
	WebElement fifthDayInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[18]/div/div[1]/input")
	WebElement sixDayInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[20]/div/div[1]/input")
	WebElement sevenDayInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[12]/div[2]/div[2]/div[1]/div[1]/div[6]")
	WebElement detailsTextArea;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[14]/div[2]/div/div[2]/table/tbody/tr/td[6]")
	WebElement selectApprover;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[1]/h4")
	WebElement addUserPopUpTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
	WebElement goBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
	WebElement seachForUserInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]/a")
	public WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[3]")
	WebElement searchedType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[5]")
	WebElement searchedUsername;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
	WebElement addButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/span/div/b")
	WebElement timeTrackingTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[19]/div/input")
	WebElement saveButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[20]/div/input")
	WebElement sendForApprovalBtn;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/div/div[4]/div/a")
	WebElement newTimesheetBtn;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/a")
	WebElement newButton;
	@FindBy(how = How.XPATH, using = ".//*[@value='Next Week']")
	WebElement nextWeekButton;
	@FindBy(how = How.XPATH, using =".//*[@cname='timeTable']/div[2]/div/div/span")
	WebElement typeGridEmptyAlert;

	public TimeSheetPage verifyTimeTrackinTitle() {
		Assert.assertEquals("Time tracking title is wrong", "Time Tracking", timeTrackingTitle.getText());
		return this;
	}

	public TimeSheetPage clickNewButton() {
		newButton.click();
		return this;
	}

	public TimeSheetPage clickNewTimeSheetBtn() {
		Assert.assertEquals("New timesheet button name is wrong", "Timesheet", newTimesheetBtn.getText());
		newTimesheetBtn.click();
		return this;
	}

	public TimeSheetPage noDataAvialible() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td"))
				.size();
		Assert.assertTrue("No user is displayed after Go button is clicked", i != 0);
		return this;

	}

	public TimeSheetPage verifyAddUserForm() {
		int i = driver.findElements(By.xpath("/html/body/div[1]/div[2]/div/div/div/div")).size();
		Assert.assertTrue("After search approver is clicked , add user popup form is not displayed", i != 0);
		return this;
	}

	public TimeSheetPage verifySearchedUser(String name, String type, String username) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedName.getText(), name, "Searched name is wrong");
		softAssert.assertEquals(searchedType.getText(), type, "Searched type is wrong");
		softAssert.assertEquals(searchedUsername.getText(), username, "Searched username is wrong");
		softAssert.assertAll();
		return this;
	}

	public TimeSheetPage verifyTypeDropDown() {
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul"))
				.size();
		Assert.assertTrue("Type drop down menu is not displayed", i != 0);
		return this;

	}

	public TimeSheetPage verifyError() {
		int i = driver.findElements(By.xpath("/html/body/div[6]")).size();
		Assert.assertTrue(
				"When go button is clicked for searching user Error message comunicating with server is shown", i == 0);
		return this;
	}

	public TimeSheetPage verifyChargeCodeDropDown() {
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[5]/div/div[1]/ul"))
				.size();
		Assert.assertTrue("Charge code drop down menu is not displayed", i != 0);
		return this;
	}

	public TimeSheetPage clickSaveButton() {
		saveButton.click();
		return this;
	}

	public TimeSheetPage clickSendForApprovalBtn() {
		sendForApprovalBtn.click();
		return this;
	}

	public TimeSheetPage clickType() {
		typeDropDown.click();
		return this;
	}

	public TimeSheetPage selectTypeCase() {
		Assert.assertEquals("Case type name is wrong", "Case", typeCase.getText());
		typeCase.click();
		return this;
	}

	public TimeSheetPage selectTypeComplaint() {
		Assert.assertEquals("Complaint type name is wrong", "Complaint", typeComplaint.getText());
		typeComplaint.click();
		return this;
	}

	public TimeSheetPage selectTypeOther() {
		Assert.assertEquals("Other type name is wrong", "Other", typeOther.getText());
		typeOther.click();
		return this;
	}

	public TimeSheetPage selectFirstChargeCode() {
		firstChargeCode.click();
		return this;
	}

	public TimeSheetPage clickChargeCode() {
		chargeCodeDropDown.click();
		return this;
	}

	public TimeSheetPage setDetailsText(String text) {
		detailsTextArea.click();
		detailsTextArea.sendKeys(text);
		return this;
	}

	public TimeSheetPage clickSelectForApprover() {
		selectApprover.click();
		return this;
	}

	public TimeSheetPage setFirstDay(String hours) throws InterruptedException {

		firstDayInput.click();
		Thread.sleep(2000);
		firstDayInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage setSecondtDay(String hours) throws InterruptedException {

		secondDayInput.click();
		Thread.sleep(2000);
		secondDayInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage setThirdDay(String hours) throws InterruptedException {

		thirdDatInput.click();
		Thread.sleep(2000);
		thirdDatInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage setForthDay(String hours) throws InterruptedException {

		forthDayInput.click();
		Thread.sleep(2000);
		forthDayInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage setFifthDay(String hours) throws InterruptedException {

		fifthDayInput.click();
		Thread.sleep(2000);
		fifthDayInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage setSixthDay(String hours) throws InterruptedException {

		sixDayInput.click();
		Thread.sleep(2000);
		sixDayInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage setSeventhDay(String hours) throws InterruptedException {

		sevenDayInput.click();
		Thread.sleep(2000);
		sevenDayInput.sendKeys(hours);
		return this;

	}

	public TimeSheetPage searchForUserInput(String name) {
		seachForUserInput.sendKeys(name);
		return this;
	}

	public TimeSheetPage clickGoButton() {
		goBtn.click();
		return this;
	}

	public TimeSheetPage clickAddButton() {
		addButton.click();
		return this;
	}
	public TimeSheetPage clickNextWeekButton() {
		nextWeekButton.click();
		return this;
	}
	public boolean typeGridIsEmpty(){
	   if (typeGridEmptyAlert.isDisplayed())
		   return true;
	   else 
		   return false;
	}
	public TimeSheetPage clickNextWeekUntilEmptySheetisAvailable()
	{
	while (!typeGridIsEmpty())
	{
		clickNextWeekButton();
	}
	return this;
	}

}
