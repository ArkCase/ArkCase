package com.amedia.arkcase.uitests.costsheet;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class CostsheetPage extends ArkCaseTestBase {

	
	@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a/i")
	WebElement editButton;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/a")
	public
	WebElement newButton;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/div/div[5]/div/a")
	WebElement newCostSheet;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/iframe")
	public
	WebElement firstIframe;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	public
	WebElement secondIframe;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/span/div")
	WebElement expensesTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[3]/div[1]/input[1]")
	WebElement user;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[5]/div[1]/input[1]")
	WebElement typeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[5]/div[1]/ul/li[2]/a")
	WebElement typeCase;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[5]/div[1]/ul/li[3]/a")
	WebElement typeComplaint;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[6]/div[1]/input[1]")
	WebElement codeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[6]/div[1]/ul/li[2]/a")
	WebElement firstCodeOption;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[8]/div[1]/input[1]")
	WebElement statusDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/thead/tr/th[4]/span/label")
	WebElement dateColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/thead/tr/th[5]/span/label")
	WebElement titleColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/thead/tr/th[6]/span/label")
	WebElement descriptionColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/thead/tr/th[7]/span/label")
	WebElement amountColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[5]/div/div[1]/span[2]")
	WebElement dateCalendar;
	@FindBy(how=How.XPATH,using="/html/body/iframe")
	WebElement calendarIframe;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/table/tbody/tr[6]/td[5]/input")
	WebElement dateFromCalendar;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/table/tbody/tr[6]/td[5]")
	WebElement selectDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[6]/div/div[1]/input[1]")
	WebElement titleDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[6]/div/div[1]/ul/li[2]/a")
	WebElement titleTaxi;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[6]/div/div[1]/ul/li[3]/a")
	WebElement titleHotel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[6]/div/div[1]/ul/li[4]/a")
	WebElement titleOvertime;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[6]/div/div[1]/ul/li[5]/a")
	WebElement titleFood;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[7]/div/div[1]/input")
	WebElement descriptionInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div[2]/table/tbody/tr/td[8]/div/div[1]/input")
	WebElement amountInput;
	@FindBy(how = How.XPATH, using = "//html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[10]/div[2]/table/tbody/tr/td[5]/div/div[1]/input")
	WebElement balanceNumber;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[10]/div[2]/table/tbody/tr/td[4]/div/div/span/div/b/font")
	WebElement balanceLabelName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[11]/a/span[2]")
	WebElement addFilesBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[4]/div[2]/div/div[1]/div/table/tbody/tr/td/form/input")
	WebElement browseBtn;
	@FindBy(how=How.XPATH,using="/html/body/div[4]/div[2]/div/div[2]/div")
	WebElement uploadBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[12]/div[2]/div[2]/div[1]/div[1]/div[6]")
	WebElement detailsInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[14]/div[2]/div/div[2]/table/tbody/tr/td[6]")
	WebElement selectApprover;
	// add user popup

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[1]/h4")
	WebElement addUserTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
	WebElement searchForUserInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
	WebElement goBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]")
	WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[3]")
	WebElement searchedUserType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[4]")
	WebElement searchedUserTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[5]")
	WebElement searchedUserName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
	WebElement AddBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[1]")
	WebElement cancelBtn;
	//
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[21]/div/input")
	WebElement saveBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[22]/div/input")
	WebElement sendForApprovalBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[23]/div/input")
	WebElement cancelCostSheetBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/div[1]/div[2]/a[2]")
	WebElement printBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[11]/div[1]/div/div[1]/a")
	WebElement addedFileLabel;

	
	public void waitUntilPageIsLoaded(){
	
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(editButton));
	
	}
	
	
	public void clickNewCostSheetBtn() {

		Assert.assertEquals("New cost sheet button name is wrong", "Costsheet", newCostSheet.getText());
		newCostSheet.click();

	}

	public void verifyExpensesTitle() {
		Assert.assertEquals("Expenses title is wrong", "Expenses", expensesTitle.getText());
	}

	public void clickTypeDropDown() {
		typeDropDown.click();
	}

	public void clickTypeCase() {
		Assert.assertEquals("Type case label name is wrong", "Case", typeCase.getText());
		typeCase.click();
	}

	public void clickTypeComplaint() {
		Assert.assertEquals("Type complaint lable name is wrong", "Complaint", typeComplaint.getText());
		typeComplaint.click();
	}

	public void clickCodeDropDown() {
		codeDropDown.click();
	}

	public void clickFirstOptionInCodeDropDown() {
		firstCodeOption.click();
	}

	public void verifyCostTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(dateColumnName.getText(), "Date", "Date column name is wrong");
		softAssert.assertEquals(titleColumnName.getText(), "Title", "Title column name is wrong");
		softAssert.assertEquals(descriptionColumnName.getText(), "Description", "Description column name is wrong");
		softAssert.assertEquals(amountColumnName.getText(), "Amount", "Amount column name is wrong");
		softAssert.assertAll();

	}

	public void clickDateCalendar() throws InterruptedException {

		dateCalendar.click();
		Thread.sleep(3000);

	}

	public void verifyCalnedarDate() {

		int i = driver.findElements(By.xpath("/html/body/div[5]")).size();
		Assert.assertTrue("The calendar is not displayed", i != 0);
	}

	public void selectDateFromCalendar() {

		selectDate.click();
		

	}
	public void verifyError()
	{
		int i=driver.findElements(By.xpath("/html/body/div[6]")).size();
		Assert.assertTrue("When go button is clicked for searching user Error message comunicating with server is shown", i==0);
	}


	public void clickTitleDropDown() {
		titleDropDown.click();

	}

	public void selectTitleTaxi() {
		Assert.assertEquals("Taxi lable name is wrong", "Taxi", titleTaxi.getText());
		titleTaxi.click();

	}

	public void selectTitleHotel() {

		Assert.assertEquals("Hotel lable name is wrong", "Hotel", titleHotel.getText());
		titleHotel.click();
	}

	public void selectTitleOverttime() {

		Assert.assertEquals("Overtitme label name is wrong", "Overtime", titleOvertime.getText());
		titleOvertime.click();
	}

	public void selectTitleFood() {

		Assert.assertEquals("Food lable name is wrong", "Food", titleFood.getText());
		titleFood.click();
	}

	public void detailsInput(String details) {

		detailsInput.click();
		detailsInput.sendKeys(details);

	}
	
	public void descriptionInput(String description){
	descriptionInput.click();
	descriptionInput.sendKeys(description);
		
	}

	public void amountInput(String amount) {
		amountInput.click();
		amountInput.sendKeys(amount);
	}

	public void clickAddFilesBtn() {

		addFilesBtn.click();

	}

	public void clickBrowseBtn() {

		browseBtn.click();

	}

	public void clickSaveBtn() {
		saveBtn.click();
	}
	
	public void clickUploadBtn(){
		uploadBtn.click();
	}

	public void clickSendForApproval() {
		sendForApprovalBtn.click();
	}

	public void clickCancel() {

		cancelBtn.click();
	}

	public void clickSelectForApprovel() {

		selectApprover.click();
	}

	public void verifyAddUserTitle() {
		Assert.assertEquals("Add user title is wrong", "Add User", addUserTitle.getText());

	}

	public void searchForUserInput(String user) {
		searchForUserInput.click();
		searchForUserInput.sendKeys(user);
	}

	public void clickGoBtn() {
		goBtn.click();

	}

	public void verifySearchedUser(String user, String username) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedName.getText(), user, "Searched user name is wrong");
		softAssert.assertEquals(searchedUserType.getText(), "USER", "Searched user type is wrong");
		softAssert.assertEquals(searchedUserTitle.getText(), "", "Searched user title is wtrong");
		softAssert.assertEquals(searchedUserName.getText(), username, "Searched username is wrong");
		softAssert.assertAll();

	}

	public void clickSearchedUser() {
		searchedName.click();
	}

	public void clickAddBtn() {
		AddBtn.click();
	}

	public void verifyBalanceLable() {
		Assert.assertEquals("Balance label name is wrong", "Balance", balanceLabelName.getText());
	}

	public void verifyBalanceNumber(String balance) {
		Assert.assertEquals("Balance number is not equal as ammount input", balance, balanceNumber.getText());
	}

	public void verifyUploadedFile(String file) {

		Assert.assertTrue("Uploaded file label name is wrong", addedFileLabel.getText().startsWith(file));

	}

	public void verifyAddUserPopUp() {

		int i = driver.findElements(By.xpath("/html/body/div[1]/div[2]/div/div/div/div/div[1]/h4")).size();
		Assert.assertTrue("After select for approver is clicked, add user popup is not displayed", i != 0);

	}

}
