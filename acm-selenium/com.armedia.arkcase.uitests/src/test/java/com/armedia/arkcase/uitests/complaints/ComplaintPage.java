package com.armedia.arkcase.uitests.complaints;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class ComplaintPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/div/div[1]/div/a/span")
	WebElement newComplaintBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div")
	WebElement complaintPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[1]/span[4]/label")
	WebElement initiatorTab;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	public WebElement secondIframe;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/iframe")
	public WebElement firstIfarme;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/div[2]/div[3]/div[1]/input")
	WebElement firstNameInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[3]/div[2]/div[4]/div[2]/div[1]/div[2]/div[4]/div[1]/input")
	WebElement lastNameInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[2]/span[4]/label")
	WebElement incidendTab;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[4]/div[1]/input")
	WebElement nextButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div[4]/div[1]/input[1]")
	WebElement incidentCategoryDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div[4]/div[1]/ul/li[2]/a")
	WebElement agricultural;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[2]/div[6]/div[1]/input")
	WebElement complaintTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[1]/div[5]/span[4]")
	WebElement peopleTab;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[5]/div[2]/table/tbody/tr/td[4]/div/div[1]/input[1]")
	WebElement selectParticipantTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[5]/div[2]/table/tbody/tr/td[4]/div/div[1]/ul/li[2]/a")
	WebElement participantOwner;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[5]/div[5]/div[2]/table/tbody/tr/td[7]/div")
	WebElement selectParticipant;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div")
	WebElement addUserPopUp;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
	WebElement searchForUserInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
	WebElement goBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]")
	WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[3]")
	WebElement searchedUserType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[5]")
	WebElement searchedUsername;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
	WebElement addBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[9]/div/input")
	WebElement submitBtn;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/a")
	WebElement newButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td")
	WebElement noDataAviable;

	public ComplaintPage clickNewButton() {

		newButton.click();
		return this;
	}

	public ComplaintPage clickNewComplain() {
		Assert.assertEquals("Complaint new button name is wrong", "Complaint", newComplaintBtn.getText());
		newComplaintBtn.click();
		return this;

	}

	public ComplaintPage verifyNewComplaintPage() {
		int i = driver
				.findElements(By.xpath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div"))
				.size();
		Assert.assertTrue("New complaint page is not displayed", i != 0);
		Assert.assertFalse("New complaint form is not displayed", complaintPage.getText().isEmpty());
		return this;

	}

	public ComplaintPage clickInitiatorFirstName() {
		firstNameInput.click();
		return this;
	}

	public ComplaintPage setInitiatorFirstName(String name) {
		firstNameInput.sendKeys(name);
		return this;
	}

	public ComplaintPage clickInitiatorLastName() {
		lastNameInput.click();
		return this;
	}

	public ComplaintPage setInitiatorLastName(String lastName) {
		lastNameInput.sendKeys(lastName);
		return this;
	}

	public ComplaintPage clickNextButton() {
		nextButton.click();
		return this;
	}

	public ComplaintPage clickIncidentCategory() {
		incidentCategoryDropDown.click();
		return this;
	}

	public ComplaintPage selectAgricultural() {
		agricultural.click();
		return this;
	}

	public ComplaintPage clickIncidentTab() {
		incidendTab.click();
		return this;
	}

	public ComplaintPage clickComplaintTitle() {
		complaintTitleInput.click();
		return this;
	}

	public ComplaintPage setComplaintTitle(String title) {
		complaintTitleInput.sendKeys(title);
		return this;
	}

	public ComplaintPage clickPeopleTab() {
		peopleTab.click();
		return this;
	}

	public ComplaintPage clickSelectparticipantType() {
		selectParticipantTypeDropDown.click();
		return this;
	}

	public ComplaintPage selectOwner() {
		participantOwner.click();
		return this;
	}

	public ComplaintPage clickSelectParticipant() {
		selectParticipant.click();
		return this;
	}

	public ComplaintPage verifyAddpersonPopUp() {
		int i = driver.findElements(By.xpath("/html/body/div[1]/div[2]/div/div/div/div")).size();
		Assert.assertTrue("Add person popup is not displayed", i != 0);
		return this;
	}

	public ComplaintPage setUserSearch(String name) {
		searchForUserInput.sendKeys(name);
		return this;
	}

	public ComplaintPage clickGoButton() {
		goBtn.click();
		return this;
	}

	public ComplaintPage verifySearchedUser(String name) {
		Assert.assertEquals("Searched username is wrong", name, searchedName.getText());
		return this;
	}

	public ComplaintPage clickSearchedUser() {
		searchedName.click();
		return this;
	}

	public ComplaintPage clickAddButton() {
		addBtn.click();
		return this;
	}

	public ComplaintPage clickSubmitButton() {
		submitBtn.click();
		return this;
	}

	public ComplaintPage verifyNoDataAviliable() {
		Assert.assertTrue("Searched user is not shown", noDataAviable.getText().equals("No data available!"));
		return this;
	}

	public ComplaintPage verifyError() {
		int i = driver.findElements(By.xpath("/html/body/div[6]")).size();

		Assert.assertTrue(
				"When go button is clicked for searching user Error message comunicating with server is shown", i != 0);
		return this;
	}

}
