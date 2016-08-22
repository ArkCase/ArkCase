package com.armedia.arkcase.uitests.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

public class UserProfilePage {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a/i")
	WebElement editDashoboard;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[1]/nav/div[1]/div/div/div[2]/a/strong/span[1]")
	WebElement arrowDown;
	@FindBy(how = How.XPATH, using = ".//a[@title='Profile']")
	WebElement profileLink;
	@FindBy(how = How.ID, using = "lnkChangePicture")
	public
	WebElement changePicture;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[1]/h3/span")
	public
	WebElement userProfileTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[1]")
	public
	WebElement userLogedIn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[3]")
	public
	WebElement userEmail;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/h4/span")
	public
	WebElement contactInformation;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/small[1]/span")
	public
	WebElement locationLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/small[2]/span")
	public
	WebElement ImAccountLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/small[1]/span")
	public
	WebElement officePhoneLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/small[2]/span")
	public
	WebElement mobilePhoneLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/h4")
	public
	WebElement companyDetailTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/small[1]/span")
	public
	WebElement companyNameLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/small[2]/span")
	public
	WebElement firstAddressLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/small[3]/span")
	public
	WebElement secondAddressLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/small[4]/span")
	public
	WebElement cityLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/small[5]/span")
	public
	WebElement stateLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/small[6]/span")
	public
	WebElement zipLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/small[1]/span")
	public
	WebElement mainOfficePhoneLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/small[2]/span")
	public
	WebElement faxLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/small[3]/span")
	public
	WebElement websiteLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[1]/div/div/button")
	public
	WebElement changePasswordButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[2]/a")
	public
	WebElement userTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[2]/form/div/input")
	WebElement userTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[1]/section/div/div/div[2]/h4[2]/form/div/span/button[1]")
	WebElement userTitleAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[1]/a")
	public
	WebElement locationField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[1]/form/div/input")
	WebElement locationFiledInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[1]/form/div/span/button[1]")
	WebElement locationFiledAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[1]/a")
	public
	WebElement officePhoneField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[1]/form/div/input")
	WebElement officePhoneFiledInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[1]/form/div/span/button[1]")
	WebElement officePhoneFiledAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[2]/a")
	public
	WebElement mobilePhoneField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[2]/form/div/input")
	WebElement mobilePhoneFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[2]/h4[2]/form/div/span/button[1]")
	WebElement mobilePhoneFiledAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/a[1]")
	public
	WebElement firstImAccounField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/input")
	WebElement firstImAccountInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]")
	WebElement firstImAccountFieldAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/a[2]")
	public
	WebElement secondImAccountField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/input")
	WebElement secondImAccountInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[1]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]")
	WebElement secondImAccountFiledAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[1]/a")
	public
	WebElement companyNameFiled;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[1]/form/div/input")
	WebElement companyNameFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[1]/form/div/span/button[1]")
	WebElement companyNameFieldAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[2]/a")
	public
	WebElement addressOneField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[2]/form/div/input")
	WebElement addressOneInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[2]/form/div/span/button[1]")
	WebElement addressOneAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[3]/a")
	public
	WebElement addressTwoField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[3]/form/div/input")
	WebElement addressTwoInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[3]/form/div/span/button[1]")
	WebElement addressTwoAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[4]/a")
	public
	WebElement cityField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[4]/form/div/input")
	WebElement cityFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[4]/form/div/span/button[1]")
	WebElement cityFieldAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[5]/a")
	public
	WebElement stateField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[5]/form/div/input")
	WebElement stateFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[5]/form/div/span/button[1]")
	WebElement stateFieldAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[6]/a")
	public
	WebElement zipField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[6]/form/div/input")
	WebElement zipFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[1]/h4[6]/form/div/span/button[1]")
	WebElement zipFieldAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[1]/a")
	public
	WebElement mainPhoneField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[1]/form/div/input")
	WebElement mainPhoneFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[1]/form/div/span/button[1]")
	WebElement mainPhoneFieldAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[2]/a")
	public
	WebElement faxField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[2]/form/div/input")
	WebElement faxFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[2]/form/div/span/button[1]")
	WebElement faxFieldAddButton;

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[3]/a")
	public
	WebElement websiteField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[3]/form/div/input")
	WebElement websiteFieldInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[2]/div[2]/div[2]/section/ul/li/div/div[2]/h4[3]/form/div/span/button[1]")
	WebElement websiteFieldAddButton;

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[3]/section/div/div[1]/div/span")
	WebElement subscriptionsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[3]/section/div/div[2]/div[1]/div[2]/div[2]/div/div/div/div[1]/div")
	WebElement subsciptionTitleFiled;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[3]/section/div/div[2]/div[1]/div[2]/div[2]/div/div/div/div[2]/div")
	WebElement subscriptionDateFiled;
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[3]/section/div/div[2]/div[1]/div[2]/div[2]/div/div/div/div[3]/div/button") WebElement deleteSubscription;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/section/div/div[1]/div[2]/div[3]/section/div/div[2]/div[1]/div[2]/div[2]")
	WebElement subscriptionEmptyTable;

	public UserProfilePage verifyIfEditDashboardButtonIsEnabled() {

		Assert.assertTrue("Edit Dashboard button is not enabled", editDashoboard.isEnabled());
		return this;

	}

	public UserProfilePage arrowDownClick() {

		arrowDown.click();
		return this;
	}

	public UserProfilePage profileLinkClick() {

		//Assert.assertTrue(profileLink.getText().equals("Profile"));
		profileLink.click();
		return this;

	}

	public UserProfilePage changePictureClick() {

		Assert.assertTrue(changePicture.getText().equals("Change Picture"));
		changePicture.click();
		return this;

	}

	public UserProfilePage profileTitleClick() {

		userTitle.click();
		return this;

	}

	public UserProfilePage userTitleInput(String title) throws InterruptedException {

		userTitleInput.click();
		userTitleInput.clear();
		userTitleInput.sendKeys(title);
		return this;

	}

	public UserProfilePage usertTitleAddButtonClick() {

		userTitleAddButton.click();
		return this;

	}

	public UserProfilePage locationFieldClick() {

		locationField.click();
		return this;

	}

	public UserProfilePage locationFiledInput(String location) {

		locationFiledInput.click();
		locationFiledInput.clear();
		locationFiledInput.sendKeys(location);
		return this;

	}

	public UserProfilePage locationFieldAddButtonClick() {

		locationFiledAddButton.click();
		return this;
	}

	public UserProfilePage officePhoneFiledClick() {

		officePhoneField.click();
		return this;

	}

	public UserProfilePage officePhoneFiledInput(String officePhone) {

		officePhoneFiledInput.click();
		officePhoneFiledInput.clear();
		officePhoneFiledInput.sendKeys(officePhone);
		return this;

	}

	public UserProfilePage officePhoneFieldAddButtonClick() {

		officePhoneFiledAddButton.click();
		return this;

	}

	public UserProfilePage mobilePhoneFiledClick() {

		mobilePhoneField.click();
		return this;

	}

	public UserProfilePage mobilePhoneFiledInput(String mobilePhone) {

		mobilePhoneFieldInput.click();
		mobilePhoneFieldInput.clear();
		mobilePhoneFieldInput.sendKeys(mobilePhone);
		return this;

	}

	public UserProfilePage mobilePhoneFiledAddButtonClick() {

		mobilePhoneFiledAddButton.click();
		return this;
	}

	public UserProfilePage firstImAccountClick() {

		firstImAccounField.click();
		return this;

	}

	public UserProfilePage firstImAccountFiledInput(String ImAccount) {

		firstImAccountInput.click();
		firstImAccountInput.clear();
		firstImAccountInput.sendKeys(ImAccount);
		return this;

	}

	public UserProfilePage firstImAccountAddButtonClick() {

		firstImAccountFieldAddButton.click();
		return this;

	}

	public UserProfilePage secondImaccountFieldClick() {

		secondImAccountField.click();
		return this;

	}

	public UserProfilePage secondImAccountFieldInput(String IM) {

		secondImAccountInput.click();
		secondImAccountInput.clear();
		secondImAccountInput.sendKeys(IM);
		return this;

	}

	public UserProfilePage secondImAccountAddButtonClick() {

		secondImAccountFiledAddButton.click();
		return this;
	}

	public UserProfilePage compnyNameFieldClick() {

		companyNameFiled.click();
		return this;
	}

	public UserProfilePage companyNameFieldInput(String companyName) {

		companyNameFieldInput.click();
		companyNameFieldInput.clear();
		companyNameFieldInput.sendKeys(companyName);
		return this;

	}

	public UserProfilePage companyNameFieldAddButtonClick() {

		companyNameFieldAddButton.click();
		return this;
	}

	public UserProfilePage addresOneFieldClick() {

		addressOneField.click();
		return this;

	}

	public UserProfilePage addressOneInput(String addressOne) {

		addressOneInput.click();
		addressOneInput.clear();
		addressOneInput.sendKeys(addressOne);
		return this;

	}

	public UserProfilePage addressOneAddButtonClik() {

		addressOneAddButton.click();
		return this;

	}

	public UserProfilePage addresTwoFieldClick() {

		addressTwoField.click();
		return this;

	}

	public UserProfilePage addressTwoInput(String addressTwo) {

		addressTwoInput.click();
		addressTwoInput.clear();
		addressTwoInput.sendKeys(addressTwo);
		return this;

	}

	public UserProfilePage addressTwoAddButtonClik() {

		addressTwoAddButton.click();
		return this;

	}

	public UserProfilePage cityFieldClick() {

		cityField.click();
		return this;
	}

	public UserProfilePage cityFieldInput(String city) {

		cityFieldInput.click();
		cityFieldInput.clear();
		cityFieldInput.sendKeys(city);
		return this;

	}

	public UserProfilePage cityFieldAddButtonClick() {

		cityFieldAddButton.click();
		return this;

	}

	public UserProfilePage stateFieldClick() {

		stateField.click();
		return this;
	}

	public UserProfilePage stateFieldInput(String state) {

		stateFieldInput.click();
		stateFieldInput.clear();
		stateFieldInput.sendKeys(state);
		return this;

	}

	public UserProfilePage stateFieldAddButtonClick() {

		stateFieldAddButton.click();
		return this;

	}

	public UserProfilePage zipFieldClick() {

		zipField.click();
		return this;
	}

	public UserProfilePage zipFieldInput(String zip) {

		zipFieldInput.click();
		zipFieldInput.clear();
		zipFieldInput.sendKeys(zip);
		return this;

	}

	public UserProfilePage zipFieldAddButtonClick() {

		zipFieldAddButton.click();
		return this;

	}

	public UserProfilePage mainPhoneFieldClick() {

		mainPhoneField.click();
		return this;
	}

	public UserProfilePage mainPhoneFieldInput(String mainPhone) {

		mainPhoneFieldInput.click();
		mainPhoneFieldInput.clear();
		mainPhoneFieldInput.sendKeys(mainPhone);
		return this;

	}

	public UserProfilePage mainPhoneFieldAddButtonClick() {

		mainPhoneFieldAddButton.click();
		return this;

	}

	public UserProfilePage faxFieldClick() {

		faxField.click();
		return this;
	}

	public UserProfilePage faxFieldInput(String fax) {

		faxFieldInput.click();
		faxFieldInput.clear();
		faxFieldInput.sendKeys(fax);
		return this;

	}

	public UserProfilePage faxFieldAddButtonClick() {

		faxFieldAddButton.click();
		return this;

	}

	public UserProfilePage websiteFieldClick() {

		websiteField.click();
		return this;
	}

	public UserProfilePage websiteFieldInput(String website) {

		websiteFieldInput.click();
		websiteFieldInput.clear();
		websiteFieldInput.sendKeys(website);
		return this;

	}

	public UserProfilePage websiteFieldAddButtonClick() {

		websiteFieldAddButton.click();
		return this;

	}

	public UserProfilePage verifySubscriptionsTable(String name) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(subscriptionsTitle.getText(), "Subscriptions", "Subscriptions table title is wrong");
		softAssert.assertEquals(subsciptionTitleFiled.getText(), name, "Subscription name is wrong");
		softAssert.assertEquals(subscriptionDateFiled.getText(), createdDate, "Subscriptions credated date is wrong");
		softAssert.assertTrue(deleteSubscription.isDisplayed(), "Delete subscription button is not displayed");
		softAssert.assertAll();
		return this;

	}

	public UserProfilePage verifyEmptySubscriptionTable() {

		Assert.assertTrue("Subscription should not be there", subscriptionEmptyTable.isDisplayed());
		return this;

	}

}
