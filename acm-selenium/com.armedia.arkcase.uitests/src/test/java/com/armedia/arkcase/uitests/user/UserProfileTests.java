package com.armedia.arkcase.uitests.user;

import java.awt.AWTException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.support.PageFactory;
import org.testng.asserts.SoftAssert;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.group.SmokeTests;

public class UserProfileTests extends ArkCaseTestBase {

	UserProfilePage user = PageFactory.initElements(driver, UserProfilePage.class);

	@Test
	@Category({ SmokeTests.class })
	public void verifyUserProfilePageLabels() throws InterruptedException {
    		
		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(user.changePicture.getText(), "Change Picture", "Change picture label name is wrong");
		softAssert.assertEquals(user.userProfileTitle.getText(), "User Profile", "User Profile label name is wrong");
		softAssert.assertEquals(user.userLogedIn.getText(), "Samuel Supervisor", "User label  name lable");
		softAssert.assertEquals(user.userEmail.getText(), "samuel-acm@armedia.com", "User email label name is wrong");
		softAssert.assertEquals(user.contactInformation.getText(), "Contact Information",
				"Contat information label name is wrong");
		softAssert.assertEquals(user.locationLabel.getText(), "Location", "Location label name is wrong");
		softAssert.assertEquals(user.ImAccountLabel.getText(), "IM Account", "Im account label name is wrong");
		softAssert.assertEquals(user.officePhoneLabel.getText(), "Office Phone", "Office phone label name is wrong ");
		softAssert.assertEquals(user.mobilePhoneLabel.getText(), "Mobile Phone", "Mobile phone label name is wrong");
		softAssert.assertEquals(user.companyDetailTitle.getText(), "Company Detail",
				"Company detail label name is wrong");
		softAssert.assertEquals(user.companyNameLabel.getText(), "Company Name", "Company name label name is wrong");
		softAssert.assertEquals(user.firstAddressLabel.getText(), "Address 1", "Address1 label name is wrong");
		softAssert.assertEquals(user.secondAddressLabel.getText(), "Address 2", "Address 2 label name is wrong");
		softAssert.assertEquals(user.cityLabel.getText(), "City", "City label name is wrong");
		softAssert.assertEquals(user.stateLabel.getText(), "State", "State label name is wrong");
		softAssert.assertEquals(user.zipLabel.getText(), "Zip", "Zip label name is wrong");
		softAssert.assertEquals(user.mainOfficePhoneLabel.getText(), "Main Office Phone",
				"Main office phone label name is wrong");
		softAssert.assertEquals(user.faxLabel.getText(), "Fax", "Fax label name is wrong");
		softAssert.assertEquals(user.websiteLabel.getText(), "Website", "Website label name is wrong");
		softAssert.assertTrue(user.changePasswordButton.isDisplayed());
		softAssert.assertTrue(user.changePasswordButton.isEnabled());
		softAssert.assertAll();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void uploadPicture() throws InterruptedException, IOException, AWTException {
	
		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.changePictureClick();
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(5000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void editProfilePicTitle() throws InterruptedException, IOException {

		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.profileTitleClick();
		Thread.sleep(2000);
		user.userTitleInput("Administrator");
		Thread.sleep(2000);
		user.usertTitleAddButtonClick();
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		Assert.assertTrue(user.userTitle.getText().equals("Administrator"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	@Category({ SmokeTests.class })
	public void editContactInformation() throws InterruptedException {

		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.locationFieldClick();
		user.locationFiledInput("Virginia");
		user.locationFieldAddButtonClick();
		Thread.sleep(2000);
		user.officePhoneFiledClick();
		user.officePhoneFiledInput("555-11-22-33");
		user.officePhoneFieldAddButtonClick();
		Thread.sleep(2000);
		user.mobilePhoneFiledClick();
		user.mobilePhoneFiledInput("334-23-456");
		user.mobilePhoneFiledAddButtonClick();
		Thread.sleep(2000);
		user.firstImAccountClick();
		user.firstImAccountFiledInput("milannjovanovski");
		user.firstImAccountAddButtonClick();
		Thread.sleep(2000);
		user.secondImaccountFieldClick();
		user.secondImAccountFieldInput("skype");
		user.secondImAccountAddButtonClick();
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(user.locationField.getText(), "Virginia", "Location field name is wrong");
		softAssert.assertEquals(user.officePhoneField.getText(), "555-11-22-33", "Office phone field name is wron");
		softAssert.assertEquals(user.mobilePhoneField.getText(), "334-23-456", "Mobile phone field name is wrong");
		softAssert.assertEquals(user.firstImAccounField.getText(), "milannjovanovski",
				"First Im Account field name is wrong");
		softAssert.assertEquals(user.secondImAccountField.getText(), "skype", "Second Im Account field name is wrong");
		softAssert.assertAll();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void editCompanyDetail() throws InterruptedException, IOException { 

		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.compnyNameFieldClick();
		user.companyNameFieldInput("Armedia");
		user.companyNameFieldAddButtonClick();
		Thread.sleep(2000);
		user.addresOneFieldClick();
		user.addressOneInput("First Street");
		user.addressOneAddButtonClik();
		Thread.sleep(2000);
		user.addresTwoFieldClick();
		user.addressTwoInput("Second Street");
		user.addressTwoAddButtonClik();
		Thread.sleep(2000);
		user.cityFieldClick();
		user.cityFieldInput("Viena");
		user.cityFieldAddButtonClick();
		Thread.sleep(2000);
		user.stateFieldClick();
		user.stateFieldInput("New York");
		user.stateFieldAddButtonClick();
		Thread.sleep(2000);
		user.zipFieldClick();
		user.zipFieldInput("2120");
		user.zipFieldAddButtonClick();
		Thread.sleep(2000);
		user.mainPhoneFieldClick();
		user.mainPhoneFieldInput("123345");
		user.mainPhoneFieldAddButtonClick();
		Thread.sleep(2000);
		user.faxFieldClick();
		user.faxFieldInput("0000");
		user.faxFieldAddButtonClick();
		Thread.sleep(2000);
		user.websiteFieldClick();
		user.websiteFieldInput("www.armedia.com");
		user.websiteFieldAddButtonClick();
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(6000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(user.companyNameFiled.getText(), "Armedia", "Company field name is wrong");
		softAssert.assertEquals(user.addressOneField.getText(), "First Street", "Address 1 field name is wrong");
		softAssert.assertEquals(user.addressTwoField.getText(), "Second Street", "Address 2 field name is wrong");
		softAssert.assertEquals(user.cityField.getText(), "Viena", "City field name is wrong");
		softAssert.assertEquals(user.stateField.getText(), "New York", "State field name is wrong ");
		softAssert.assertEquals(user.zipField.getText(), "2120", "Zip field name is wrong ");
		softAssert.assertEquals(user.mainPhoneField.getText(), "123345", "Main Phone field name is wrong");
		softAssert.assertEquals(user.faxField.getText(), "0000", "Fax field name is wrong");
		softAssert.assertEquals(user.websiteField.getText(), "www.armedia.com", "Website field name is wrong");
		softAssert.assertAll();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void editProfilePicTitleNull() throws InterruptedException {

		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.profileTitleClick();
		Thread.sleep(2000);
		user.userTitleInput("");
		Thread.sleep(2000);
		user.usertTitleAddButtonClick();
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(user.userTitle.getText(), "Unknown", "Profile Pic Title name is wrong");
		softAssert.assertAll();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void editContactInformationNull() throws InterruptedException {

		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.locationField.click();
		user.locationFiledInput("");
		user.locationFieldAddButtonClick();
		Thread.sleep(2000);
		user.officePhoneFiledClick();
		user.officePhoneFiledInput("");
		user.officePhoneFieldAddButtonClick();
		Thread.sleep(2000);
		user.mobilePhoneFiledClick();
		user.mobilePhoneFiledInput("");
		user.mobilePhoneFiledAddButtonClick();
		Thread.sleep(2000);
		user.firstImAccountClick();
		user.firstImAccountFiledInput("");
		user.firstImAccountAddButtonClick();
		Thread.sleep(2000);
		user.secondImaccountFieldClick();
		user.secondImAccountFieldInput("");
		user.secondImAccountAddButtonClick();
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(6000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(user.locationField.getText(), "Unknown", "Location field name is wrong");
		softAssert.assertEquals(user.officePhoneField.getText(), "Unknown", "Office phone filed name is wrong");
		softAssert.assertEquals(user.mobilePhoneField.getText(), "Unknown", "Mobile phone field name is wrong");
		softAssert.assertEquals(user.firstImAccounField.getText(), "Unknown", "First IM Account name is wrong");
		softAssert.assertEquals(user.secondImAccountField.getText(), "Unknown", "Second Im Account name is wrong");
		softAssert.assertAll();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	@Category({ SmokeTests.class })
	public void editCompanyDetailNull() throws InterruptedException {

		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.compnyNameFieldClick();
		user.companyNameFieldInput("");
		user.companyNameFieldAddButtonClick();
		Thread.sleep(2000);
		user.addresOneFieldClick();
		user.addressOneInput("");
		user.addressOneAddButtonClik();
		Thread.sleep(2000);
		user.addresTwoFieldClick();
		user.addressTwoInput("");
		user.addressTwoAddButtonClik();
		Thread.sleep(2000);
		user.cityFieldClick();
		user.cityFieldInput("");
		user.cityFieldAddButtonClick();
		Thread.sleep(2000);
		user.stateFieldClick();
		user.stateFieldInput("");
		user.stateFieldAddButtonClick();
		Thread.sleep(2000);
		user.zipFieldClick();
		user.zipFieldInput("");
		user.zipFieldAddButtonClick();
		Thread.sleep(2000);
		user.mainPhoneFieldClick();
		user.mainPhoneFieldInput("");
		user.mainPhoneFieldAddButtonClick();
		Thread.sleep(2000);
		user.faxFieldClick();
		user.faxFieldInput("");
		user.faxFieldAddButtonClick();
		Thread.sleep(2000);
		user.websiteFieldClick();
		user.websiteFieldInput("");
		user.websiteFieldAddButtonClick();
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(6000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(user.companyNameFiled.getText(), "Unknown", "Company field name is wrong");
		softAssert.assertEquals(user.addressOneField.getText(), "Unknown", "Address 1 field name is wrong");
		softAssert.assertEquals(user.addressTwoField.getText(), "Unknown", "Address 2 field name is wrong");
		softAssert.assertEquals(user.cityField.getText(), "Unknown", "City field name is wrong");
		softAssert.assertEquals(user.stateField.getText(), "Unknown", "State field name is wrong ");
		softAssert.assertEquals(user.zipField.getText(), "Unknown", "Zip field name is wrong ");
		softAssert.assertEquals(user.mainPhoneField.getText(), "Unknown", "Main Phone field name is wrong");
		softAssert.assertEquals(user.faxField.getText(), "Unknown", "Fax field name is wrong");
		softAssert.assertEquals(user.websiteField.getText(), "Unknown", "Website field name is wrong");
		softAssert.assertAll();
		ArkCaseAuthentication.logOut(driver);
	}

}
