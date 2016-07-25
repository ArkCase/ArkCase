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
	WebElement firstIframe;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	WebElement secondIframe;
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
	WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[3]")
	WebElement searchedType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[5]")
	WebElement searchedUsername;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div")
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

	public void verifyTimeTrackinTitle() {
		Assert.assertEquals("Time tracking title is wrong", "Time Tracking", timeTrackingTitle.getText());
	}

	public void clickNewButton() {
		newButton.click();
	}

	public void clickNewTimeSheetBtn() {
		Assert.assertEquals("New timesheet button name is wrong", "Timesheet", newTimesheetBtn.getText());
		newTimesheetBtn.click();
	}

	public void noDataAvialible() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td"))
				.size();
		Assert.assertTrue("No user is displayed after Go button is clicked", i == 0);

	}

	public void verifyAddUserForm() {
		int i = driver.findElements(By.xpath("/html/body/div[1]/div[2]/div/div/div/div")).size();
		Assert.assertTrue("After search approver is clicked , add user popup form is not displayed", i != 0);
	}

	public void verifySearchedUser(String name, String type, String username) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedName.getText(), name, "Searched name is wrong");
		softAssert.assertEquals(searchedType.getText(), type, "Searched type is wrong");
		softAssert.assertEquals(searchedUsername.getText(), username, "Searched username is wrong");
		softAssert.assertAll();
	}

	public void verifyTypeDropDown() {
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul"))
				.size();
		Assert.assertTrue("Type drop down menu is not displayed", i != 0);

	}

	public void verifyError() {
		int i = driver.findElements(By.xpath("/html/body/div[6]")).size();
		Assert.assertTrue(
				"When go button is clicked for searching user Error message comunicating with server is shown", i == 0);
	}

	public void verifyChargeCodeDropDown() {
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[5]/div/div[1]/ul"))
				.size();
		Assert.assertTrue("Charge code drop down menu is not displayed", i != 0);
	}

	public void clickSaveButton() {
		saveButton.click();
	}

	public void clickSendForApprovalBtn() {
		sendForApprovalBtn.click();
	}

	public void clickType() {
		typeDropDown.click();
	}

	public void selectTypeCase() {
		Assert.assertEquals("Case type name is wrong", "Case", typeCase.getText());
		typeCase.click();
	}

	public void selectTypeComplaint() {
		Assert.assertEquals("Complaint type name is wrong", "Complaint", typeComplaint.getText());
		typeComplaint.click();
	}

	public void selectTypeOther() {
		Assert.assertEquals("Other type name is wrong", "Other", typeOther.getText());
		typeOther.click();
	}

	public void selectFirstChargeCode() {
		firstChargeCode.click();
	}
	
	public void clickChargeCode(){
		chargeCodeDropDown.click();
	}

	public void setDetailsText(String text) {
		detailsTextArea.click();
		detailsTextArea.sendKeys(text);
	}
	public void clickSelectForApprover(){
		selectApprover.click();
	}

	public void setFirstDay(String hours) throws InterruptedException {

		firstDayInput.click();
		Thread.sleep(2000);
		firstDayInput.sendKeys(hours);

	}

	public void setSecondtDay(String hours) throws InterruptedException {

		secondDayInput.click();
		Thread.sleep(2000);
		secondDayInput.sendKeys(hours);

	}

	public void setThirdDay(String hours) throws InterruptedException {

		thirdDatInput.click();
		Thread.sleep(2000);
		thirdDatInput.sendKeys(hours);

	}

	public void setForthDay(String hours) throws InterruptedException {

		forthDayInput.click();
		Thread.sleep(2000);
		forthDayInput.sendKeys(hours);

	}

	public void setFifthDay(String hours) throws InterruptedException {

		fifthDayInput.click();
		Thread.sleep(2000);
		fifthDayInput.sendKeys(hours);

	}

	public void setSixthDay(String hours) throws InterruptedException {

		sixDayInput.click();
		Thread.sleep(2000);
		sixDayInput.sendKeys(hours);

	}

	public void setSeventhDay(String hours) throws InterruptedException {

		sevenDayInput.click();
		Thread.sleep(2000);
		sevenDayInput.sendKeys(hours);

	}

	public void searchForUserInput(String name){
		seachForUserInput.sendKeys(name);
	}

	
	public void clickGoButton(){
		goBtn.click();
	}
	
	public void clickAddButton(){
		addButton.click();
	}
	
	
	
	
}
