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
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[1]/nav/div[1]/div/div/div[2]/ul/li[1]/a")
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

	public void verifyIfEditDashboardButtonIsEnabled() {

		Assert.assertTrue("Edit Dashboard button is not enabled", editDashoboard.isEnabled());

	}

	public void arrowDownClick() {

		arrowDown.click();
	}

	public void profileLinkClick() {

		Assert.assertTrue(profileLink.getText().equals("Profile"));
		profileLink.click();

	}

	public void changePictureClick() {

		Assert.assertTrue(changePicture.getText().equals("Change Picture"));
		changePicture.click();

	}

	public void profileTitleClick() {

		userTitle.click();

	}

	public void userTitleInput(String title) throws InterruptedException {

		userTitleInput.click();
		userTitleInput.clear();
		userTitleInput.sendKeys(title);

	}

	public void usertTitleAddButtonClick() {

		userTitleAddButton.click();

	}

	public void locationFieldClick() {

		locationField.click();

	}

	public void locationFiledInput(String location) {

		locationFiledInput.click();
		locationFiledInput.clear();
		locationFiledInput.sendKeys(location);

	}

	public void locationFieldAddButtonClick() {

		locationFiledAddButton.click();
	}

	public void officePhoneFiledClick() {

		officePhoneField.click();

	}

	public void officePhoneFiledInput(String officePhone) {

		officePhoneFiledInput.click();
		officePhoneFiledInput.clear();
		officePhoneFiledInput.sendKeys(officePhone);

	}

	public void officePhoneFieldAddButtonClick() {

		officePhoneFiledAddButton.click();

	}

	public void mobilePhoneFiledClick() {

		mobilePhoneField.click();

	}

	public void mobilePhoneFiledInput(String mobilePhone) {

		mobilePhoneFieldInput.click();
		mobilePhoneFieldInput.clear();
		mobilePhoneFieldInput.sendKeys(mobilePhone);

	}

	public void mobilePhoneFiledAddButtonClick() {

		mobilePhoneFiledAddButton.click();
	}

	public void firstImAccountClick() {

		firstImAccounField.click();

	}

	public void firstImAccountFiledInput(String ImAccount) {

		firstImAccountInput.click();
		firstImAccountInput.clear();
		firstImAccountInput.sendKeys(ImAccount);

	}

	public void firstImAccountAddButtonClick() {

		firstImAccountFieldAddButton.click();

	}

	public void secondImaccountFieldClick() {

		secondImAccountField.click();

	}

	public void secondImAccountFieldInput(String IM) {

		secondImAccountInput.click();
		secondImAccountInput.clear();
		secondImAccountInput.sendKeys(IM);

	}

	public void secondImAccountAddButtonClick() {

		secondImAccountFiledAddButton.click();

	}

	public void compnyNameFieldClick() {

		companyNameFiled.click();
	}

	public void companyNameFieldInput(String companyName) {

		companyNameFieldInput.click();
		companyNameFieldInput.clear();
		companyNameFieldInput.sendKeys(companyName);

	}

	public void companyNameFieldAddButtonClick() {

		companyNameFieldAddButton.click();

	}

	public void addresOneFieldClick() {

		addressOneField.click();

	}

	public void addressOneInput(String addressOne) {

		addressOneInput.click();
		addressOneInput.clear();
		addressOneInput.sendKeys(addressOne);

	}

	public void addressOneAddButtonClik() {

		addressOneAddButton.click();

	}

	public void addresTwoFieldClick() {

		addressTwoField.click();

	}

	public void addressTwoInput(String addressTwo) {

		addressTwoInput.click();
		addressTwoInput.clear();
		addressTwoInput.sendKeys(addressTwo);

	}

	public void addressTwoAddButtonClik() {

		addressTwoAddButton.click();

	}

	public void cityFieldClick() {

		cityField.click();
	}

	public void cityFieldInput(String city) {

		cityFieldInput.click();
		cityFieldInput.clear();
		cityFieldInput.sendKeys(city);

	}

	public void cityFieldAddButtonClick() {

		cityFieldAddButton.click();

	}

	public void stateFieldClick() {

		stateField.click();
	}

	public void stateFieldInput(String state) {

		stateFieldInput.click();
		stateFieldInput.clear();
		stateFieldInput.sendKeys(state);

	}

	public void stateFieldAddButtonClick() {

		stateFieldAddButton.click();

	}

	public void zipFieldClick() {

		zipField.click();
	}

	public void zipFieldInput(String zip) {

		zipFieldInput.click();
		zipFieldInput.clear();
		zipFieldInput.sendKeys(zip);

	}

	public void zipFieldAddButtonClick() {

		zipFieldAddButton.click();

	}

	public void mainPhoneFieldClick() {

		mainPhoneField.click();
	}

	public void mainPhoneFieldInput(String mainPhone) {

		mainPhoneFieldInput.click();
		mainPhoneFieldInput.clear();
		mainPhoneFieldInput.sendKeys(mainPhone);

	}

	public void mainPhoneFieldAddButtonClick() {

		mainPhoneFieldAddButton.click();

	}

	public void faxFieldClick() {

		faxField.click();
	}

	public void faxFieldInput(String fax) {

		faxFieldInput.click();
		faxFieldInput.clear();
		faxFieldInput.sendKeys(fax);

	}

	public void faxFieldAddButtonClick() {

		faxFieldAddButton.click();

	}

	public void websiteFieldClick() {

		websiteField.click();
	}

	public void websiteFieldInput(String website) {

		websiteFieldInput.click();
		websiteFieldInput.clear();
		websiteFieldInput.sendKeys(website);

	}

	public void websiteFieldAddButtonClick() {

		websiteFieldAddButton.click();

	}

	public void verifySubscriptionsTable(String name) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(subscriptionsTitle.getText(), "Subscriptions", "Subscriptions table title is wrong");
		softAssert.assertEquals(subsciptionTitleFiled.getText(), name, "Subscription name is wrong");
		softAssert.assertEquals(subscriptionDateFiled.getText(), createdDate, "Subscriptions credated date is wrong");
		softAssert.assertTrue(deleteSubscription.isDisplayed(), "Delete subscription button is not displayed");
		softAssert.assertAll();

	}

	public void verifyEmptySubscriptionTable() {

		Assert.assertTrue("Subscription should not be there", subscriptionEmptyTable.isDisplayed());

	}

}
