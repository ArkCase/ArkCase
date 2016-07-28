package SmokeTests;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.PageFactory;
import org.testng.asserts.SoftAssert;

import com.amedia.arkcase.uitests.costsheet.CostTrackingPage;
import com.amedia.arkcase.uitests.costsheet.CostsheetPage;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.cases.CasePage;
import com.armedia.arkcase.uitests.cases.CasesPage;
import com.armedia.arkcase.uitests.cases.documents.CaseDocumentsPage;
import com.armedia.arkcase.uitests.complaints.ComplaintPage;
import com.armedia.arkcase.uitests.complaints.ComplaintsPage;
import com.armedia.arkcase.uitests.dashboard.DashboardPage;
import com.armedia.arkcase.uitests.task.TaskPage;
import com.armedia.arkcase.uitests.task.TasksPage;
import com.armedia.arkcase.uitests.timesheet.TimeSheetPage;
import com.armedia.arkcase.uitests.user.UserProfilePage;

public class SmokeTests extends ArkCaseTestBase {

	ComplaintPage complaint = PageFactory.initElements(driver, ComplaintPage.class);
	ComplaintsPage complaints = PageFactory.initElements(driver, ComplaintsPage.class);
	TaskPage task = PageFactory.initElements(driver, TaskPage.class);
	UserProfilePage user = PageFactory.initElements(driver, UserProfilePage.class);
	DashboardPage dash = PageFactory.initElements(driver, DashboardPage.class);
	TimeSheetPage timesheet = PageFactory.initElements(driver, TimeSheetPage.class);
	TasksPage tasks = PageFactory.initElements(driver, TasksPage.class);
	CasePage casePom = PageFactory.initElements(driver, CasePage.class);
	CasesPage casesPom = PageFactory.initElements(driver, CasesPage.class);
	CaseDocumentsPage documentsPage = PageFactory.initElements(driver, CaseDocumentsPage.class);
	CostsheetPage costsheet = PageFactory.initElements(driver, CostsheetPage.class);
	CostTrackingPage costTracking = PageFactory.initElements(driver, CostTrackingPage.class);

	@Test
	public void verifyUserProfilePageLabels() throws InterruptedException {

		super.logIn();
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
	public void uploadPicture() throws InterruptedException, IOException, AWTException {

		super.logIn();
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
	public void editProfilePicTitle() throws InterruptedException, IOException {

		super.logIn();
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
	public void editContactInformation() throws InterruptedException {

		super.logIn();
		user.verifyIfEditDashboardButtonIsEnabled();
		user.arrowDownClick();
		Thread.sleep(3000);
		user.profileLinkClick();
		Thread.sleep(5000);
		user.locationField.click();
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
	public void editCompanyDetail() throws InterruptedException, IOException {

		super.logIn();
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
	public void editProfilePicTitleNull() throws InterruptedException {

		super.logIn();
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
	public void editContactInformationNull() throws InterruptedException {

		super.logIn();
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
	public void editCompanyDetailNull() throws InterruptedException {

		super.logIn();
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

	@Test
	public void createNewComplaintAddNote() throws InterruptedException {

		super.logIn();
		Thread.sleep(10000);
		complaint.clickNewButton();
		Thread.sleep(5000);
		complaint.clickNewComplain();
		Thread.sleep(20000);
		driver.switchTo().frame(complaint.firstIfarme);
		Thread.sleep(3000);
		driver.switchTo().frame(complaint.secondIframe);
		Thread.sleep(3000);
		complaint.verifyNewComplaintPage();
		complaint.clickInitiatorFirstName();
		Thread.sleep(2000);
		complaint.setInitiatorFirstName("Milan");
		Thread.sleep(3000);
		complaint.clickInitiatorLastName();
		Thread.sleep(3000);
		complaint.setInitiatorLastName("Jovanovski");
		Thread.sleep(3000);
		complaint.clickIncidentTab();
		complaint.clickIncidentTab();
		Thread.sleep(3000);
		complaint.clickIncidentCategory();
		Thread.sleep(3000);
		complaint.selectAgricultural();
		Thread.sleep(3000);
		complaint.clickComplaintTitle();
		complaint.setComplaintTitle("Milan's Test Add Note");
		Thread.sleep(3000);
		complaint.clickPeopleTab();
		complaint.clickPeopleTab();
		Thread.sleep(4000);
		complaint.clickSelectparticipantType();
		Thread.sleep(3000);
		complaint.selectOwner();
		Thread.sleep(3000);
		complaint.clickSelectParticipant();
		Thread.sleep(3000);
		complaint.verifyAddpersonPopUp();
		complaint.setUserSearch("samuel");
		Thread.sleep(3000);
		complaint.clickGoButton();
		Thread.sleep(5000);
		complaint.verifyError();
		complaint.verifySearchedUser("Samuel Supervisor");
		Thread.sleep(2000);
		complaint.clickSearchedUser();
		Thread.sleep(3000);
		complaint.clickAddButton();
		Thread.sleep(4000);
		complaint.clickSubmitButton();
		Thread.sleep(20000);
		driver.switchTo().defaultContent();
		complaints.clickSortBtn();
		Thread.sleep(3000);
		complaints.sortDateDesc();
		Thread.sleep(4000);
		complaints.clickFirstComplaint();
		Thread.sleep(4000);
		complaints.verifyComplaintTitle("Milan's Test Add Note");
		complaints.clickNoteLink();
		Thread.sleep(4000);
		complaints.verifyNotesTableTitle();
		complaints.clickAddNoteButton();
		Thread.sleep(4000);
		complaints.verifyAddNotePopUp();
		complaints.setNoteTextArea("note");
		Thread.sleep(3000);
		complaints.clickSaveButton();
		Thread.sleep(4000);
		complaints.verifyAddNotePopUpDisapierd();
		complaints.verifyIfNoteIsCreated();
		complaints.verifyCreatedNote("note", "samuel-acm");
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewComplaintAddDocument() throws InterruptedException, IOException, AWTException {

		super.logIn();
		Thread.sleep(10000);
		complaint.clickNewButton();
		Thread.sleep(5000);
		complaint.clickNewComplain();
		Thread.sleep(20000);
		driver.switchTo().frame(complaint.firstIfarme);
		Thread.sleep(3000);
		driver.switchTo().frame(complaint.secondIframe);
		Thread.sleep(3000);
		complaint.verifyNewComplaintPage();
		complaint.clickInitiatorFirstName();
		Thread.sleep(2000);
		complaint.setInitiatorFirstName("Milan");
		Thread.sleep(3000);
		complaint.clickInitiatorLastName();
		Thread.sleep(3000);
		complaint.setInitiatorLastName("Jovanovski");
		Thread.sleep(3000);
		complaint.clickIncidentTab();
		complaint.clickIncidentTab();
		Thread.sleep(3000);
		complaint.clickIncidentCategory();
		Thread.sleep(3000);
		complaint.selectAgricultural();
		Thread.sleep(3000);
		complaint.clickComplaintTitle();
		complaint.setComplaintTitle("Milan's Test Add Document");
		Thread.sleep(3000);
		complaint.clickPeopleTab();
		complaint.clickPeopleTab();
		Thread.sleep(4000);
		complaint.clickSelectparticipantType();
		Thread.sleep(3000);
		complaint.selectOwner();
		Thread.sleep(3000);
		complaint.clickSelectParticipant();
		Thread.sleep(3000);
		complaint.verifyAddpersonPopUp();
		complaint.setUserSearch("samuel");
		Thread.sleep(3000);
		complaint.clickGoButton();
		Thread.sleep(5000);
		complaint.verifyError();
		complaint.verifySearchedUser("Samuel Supervisor");
		Thread.sleep(2000);
		complaint.clickSearchedUser();
		Thread.sleep(3000);
		complaint.clickAddButton();
		Thread.sleep(4000);
		complaint.clickSubmitButton();
		Thread.sleep(20000);
		driver.switchTo().defaultContent();
		complaints.clickSortBtn();
		Thread.sleep(3000);
		complaints.sortDateDesc();
		Thread.sleep(4000);
		complaints.clickFirstComplaint();
		Thread.sleep(4000);
		complaints.verifyComplaintTitle("Milan's Test Add Document");
		complaints.clickDocumentsLink();
		Thread.sleep(4000);
		complaints.refreshPageBtn.click();
		Thread.sleep(4000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		complaints.performRightClickOnRoot();
		Thread.sleep(3000);
		complaints.checkIfRightClickOnRootIsWorking();
		complaints.clickNewDocument();
		Thread.sleep(4000);
		complaints.verifyNewDocumentMenu();
		complaints.clickNewDocumentOther();
		Thread.sleep(5000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(10000);
		complaints.verifyIfDocumentIsCreated();
		complaints.verifyCreatedDocument("ArkCaseTesting.docx", "Other", "Samuel Supervisor", "1.0", "ACTIVE");
		complaints.clickNoteLink();
		Thread.sleep(5000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewComplaintVerifyDetailsAddImage() throws InterruptedException, IOException, AWTException {

		super.logIn();
		Thread.sleep(10000);
		complaint.clickNewButton();
		Thread.sleep(5000);
		complaint.clickNewComplain();
		Thread.sleep(20000);
		driver.switchTo().frame(complaint.firstIfarme);
		Thread.sleep(3000);
		driver.switchTo().frame(complaint.secondIframe);
		Thread.sleep(3000);
		complaint.verifyNewComplaintPage();
		complaint.clickInitiatorFirstName();
		Thread.sleep(2000);
		complaint.setInitiatorFirstName("Milan");
		Thread.sleep(3000);
		complaint.clickInitiatorLastName();
		Thread.sleep(3000);
		complaint.setInitiatorLastName("Jovanovski");
		Thread.sleep(3000);
		complaint.clickIncidentTab();
		complaint.clickIncidentTab();
		Thread.sleep(3000);
		complaint.clickIncidentCategory();
		Thread.sleep(3000);
		complaint.selectAgricultural();
		Thread.sleep(3000);
		complaint.clickComplaintTitle();
		complaint.setComplaintTitle("Milan's Test Add Image");
		Thread.sleep(3000);
		complaint.clickPeopleTab();
		complaint.clickPeopleTab();
		Thread.sleep(4000);
		complaint.clickSelectparticipantType();
		Thread.sleep(3000);
		complaint.selectOwner();
		Thread.sleep(3000);
		complaint.clickSelectParticipant();
		Thread.sleep(3000);
		complaint.verifyAddpersonPopUp();
		complaint.setUserSearch("samuel");
		Thread.sleep(3000);
		complaint.clickGoButton();
		Thread.sleep(5000);
		complaint.verifyError();
		complaint.verifySearchedUser("Samuel Supervisor");
		Thread.sleep(2000);
		complaint.clickSearchedUser();
		Thread.sleep(3000);
		complaint.clickAddButton();
		Thread.sleep(4000);
		complaint.clickSubmitButton();
		Thread.sleep(20000);
		driver.switchTo().defaultContent();
		complaints.clickSortBtn();
		Thread.sleep(3000);
		complaints.sortDateDesc();
		Thread.sleep(4000);
		complaints.clickFirstComplaint();
		Thread.sleep(4000);
		complaints.verifyComplaintTitle("Milan's Test Add Image");
		Thread.sleep(3000);
		complaints.clickDetailsLink();
		Thread.sleep(3000);
		complaints.verifyDetailsTitle();
		complaints.clickInsertPictureBtn();
		Thread.sleep(3000);
		complaints.verifyInsertImagePopUp();
		Thread.sleep(3000);
		complaints.clickBrowseButton();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(5000);
		complaints.verifyUploadedImage();
		complaints.detailsSaveBtn.click();
		Thread.sleep(4000);
		complaints.documentLink.click();
		Thread.sleep(5000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewComplaintVerifyAddTask() throws InterruptedException, IOException, AWTException {

		super.logIn();
		Thread.sleep(10000);
		complaint.clickNewButton();
		Thread.sleep(5000);
		complaint.clickNewComplain();
		Thread.sleep(20000);
		driver.switchTo().frame(complaint.firstIfarme);
		Thread.sleep(3000);
		driver.switchTo().frame(complaint.secondIframe);
		Thread.sleep(3000);
		complaint.verifyNewComplaintPage();
		complaint.clickInitiatorFirstName();
		Thread.sleep(2000);
		complaint.setInitiatorFirstName("Milan");
		Thread.sleep(3000);
		complaint.clickInitiatorLastName();
		Thread.sleep(3000);
		complaint.setInitiatorLastName("Jovanovski");
		Thread.sleep(3000);
		complaint.clickIncidentTab();
		complaint.clickIncidentTab();
		Thread.sleep(3000);
		complaint.clickIncidentCategory();
		Thread.sleep(3000);
		complaint.selectAgricultural();
		Thread.sleep(3000);
		complaint.clickComplaintTitle();
		complaint.setComplaintTitle("Milan's Test Add task");
		Thread.sleep(3000);
		complaint.clickPeopleTab();
		complaint.clickPeopleTab();
		Thread.sleep(4000);
		complaint.clickSelectparticipantType();
		Thread.sleep(3000);
		complaint.selectOwner();
		Thread.sleep(3000);
		complaint.clickSelectParticipant();
		Thread.sleep(3000);
		complaint.verifyAddpersonPopUp();
		complaint.setUserSearch("samuel");
		Thread.sleep(3000);
		complaint.clickGoButton();
		Thread.sleep(5000);
		complaint.verifyError();
		complaint.verifySearchedUser("Samuel Supervisor");
		Thread.sleep(2000);
		complaint.clickSearchedUser();
		Thread.sleep(3000);
		complaint.clickAddButton();
		Thread.sleep(4000);
		complaint.clickSubmitButton();
		Thread.sleep(20000);
		driver.switchTo().defaultContent();
		complaints.clickSortBtn();
		Thread.sleep(3000);
		complaints.sortDateDesc();
		Thread.sleep(4000);
		complaints.clickFirstComplaint();
		Thread.sleep(4000);
		complaints.verifyComplaintTitle("Milan's Test Add task");
		Thread.sleep(3000);
		complaints.clickTaskLink();
		Thread.sleep(4000);
		complaints.verifyTaskTableTitle();
		complaints.clickAddTaskButton();
		Thread.sleep(10000);
		task.assignTo("samuel");
		Thread.sleep(4000);
		task.typeSubject("associate with complaint");
		Thread.sleep(3000);
		task.typeDuedate("07/28/2016");
		Thread.sleep(3000);
		task.saveButtonClick();
		Thread.sleep(7000);
		complaints.verifyComplaintTitleInTasksPage("Milan's Test Add task");
		complaints.clickComplaintTitleInTasksPage();
		Thread.sleep(7000);
		complaints.clickTaskLink();
		Thread.sleep(5000);
		complaints.verifyIfTaskIsCredated();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void changeDashboardLayoutTwelve() throws InterruptedException, IOException {

		super.logIn();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonTwelve.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		dash.verifyLayoutTwelveIsDisplayed();
		dash.editButtonClick();
		Thread.sleep(2000);
		dash.editDashboardButtonClick();
		dash.radioButtonSixSix.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyLayoutSixSixIsDisplayed();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void changeDashboardLayoutSixSix() throws InterruptedException, IOException {

		super.logIn();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonSixSix.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		dash.verifyLayoutSixSixIsDisplayed();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void changeDashboardLayoutFourFourFour() throws InterruptedException, IOException {

		super.logIn();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonTripleFour.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		dash.verifySecondLayout();
		dash.verifyThirdLayout();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void changeDashboardLayoutFourEight() throws InterruptedException, IOException {

		super.logIn();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonFourEight.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyFourEightLayout();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void changeDashboardLayoutEightFour() throws InterruptedException, IOException {

		super.logIn();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonEightFour.click();
		Thread.sleep(2000);
		dash.closeEditDashboard.click();
		Thread.sleep(2000);
		dash.saveChangesButtonClick();
		Thread.sleep(2000);
		dash.verifyEightFour();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void changeDashboardUndoButton() throws InterruptedException, IOException {

		super.logIn();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(dash.editButton.isEnabled(), "Edit button is not enabled");
		softAssert.assertAll();
		dash.editButtonClick();
		Thread.sleep(4000);
		dash.editDashboardButtonClick();
		Thread.sleep(2000);
		dash.radioButtonTripleFour.click();
		Thread.sleep(2000);
		dash.editDashboadrCloseButtonClick();
		Thread.sleep(2000);
		dash.checkThirdColumnIsDisplayed();
		Thread.sleep(2000);
		dash.undoButtonClick();
		Thread.sleep(2000);
		dash.checkTripleFourStructureIsSaved();
		ArkCaseAuthentication.logOut(driver);
	}

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
		Thread.sleep(4000);
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
		Thread.sleep(2000);
		// timesheet.verifyError();
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

	@Test
	public void createNewTaskStatusActive() throws IOException, InterruptedException {

		super.logIn();
		task.newTask();
		task.assignTo("sam");
		task.typeSubject("SmokeTestTask");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("20");
		Thread.sleep(3000);
		task.saveButtonClick();
		Thread.sleep(5000);
		Assert.assertEquals("Created task title is wrong", "SmokeTestTask", task.secondSubject.getText());
		tasks.completeButton.click();
		Thread.sleep(6000);
		Assert.assertEquals("Task state after complete button is clicked is not closed", "CLOSED",
				tasks.stateTask.getText());
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewCaseAddTask() throws InterruptedException, IOException {
		// create new case add task from case page,delete the case
		super.logIn();
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(casesPom.frameOne);
		driver.switchTo().frame(casesPom.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("CaseAddTask");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		Thread.sleep(2000);
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		Thread.sleep(2000);
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		casePom.searchedName();
		Thread.sleep(4000);
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(8000);
		driver.switchTo().defaultContent();
		Thread.sleep(4000);
		casesPom.verifyCreatedCaseInfo("CaseAddTask", "Background Investigation");
		casesPom.VerifycreatedDate();
		Assert.assertEquals("Case title is wrong", "CaseAddTask (DRAFT)", casesPom.caseTitleDraft.getText());
		casesPom.caseTasks.click();
		Thread.sleep(3000);
		casesPom.verifyTaskTable();
		casesPom.taskAddButton.click();
		Thread.sleep(5000);
		task.verifyTaskTitle();
		task.verifySubjectTitleInput();
		task.verifyAssighnToLabelInput();
		task.verifyStartDate();
		task.verifyDueDate();
		task.verifyPriority();
		task.verifyNotes();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("CaseTask");
		task.typeDuedate("05/20/2016");
		task.saveButtonClick();
		Thread.sleep(5000);
		casesPom.verifyCaseWIthAddedTask("Samuel Supervisor", "Background Investigation", "ACM_INVESTIGATOR_DEV",
				"Medium");
		casesPom.verifyAddedTaskInCase("CaseTask", "0", "Samuel Supervisor", "05/20/2016", "Medium", "ACTIVE");
		casesPom.caseTitleInTasks.click();
		Thread.sleep(5000);
		casesPom.caseTasks.click();
		Thread.sleep(5000);
		casesPom.refreshPage.click();
		Thread.sleep(3000);
		casesPom.verifyTaskInTheTaskTable("CaseTask", "Samuel Supervisor", "Medium", "05/20/2016", "ACTIVE");
		casesPom.changeCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(casesPom.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(casesPom.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		casesPom.deleteCase();
		driver.switchTo().defaultContent();
		casesPom.refreshPage.click();
		Thread.sleep(3000);
		Assert.assertEquals("Case title is wrong", "CaseAddTask (IN APPROVAL)", casesPom.caseTitleDraft.getText());
		casesPom.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewCaseAddNewDocumentOther() throws InterruptedException, IOException, AWTException {

		super.logIn();
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(casesPom.frameOne);
		driver.switchTo().frame(casesPom.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("caseother");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		Thread.sleep(2000);
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		Thread.sleep(2000);
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		casesPom.caseDocuments.click();
		Thread.sleep(6000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(3000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(3000);
		documentsPage.verifyDocumentOtherName();
		Thread.sleep(5000);
		documentsPage.clickDocumentOther();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(5000);
		documentsPage.verifySecondDocument("imageprofile.png", "Other", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(casesPom.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(casesPom.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		casesPom.deleteCase();
		driver.switchTo().defaultContent();
		casesPom.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddNote() throws InterruptedException, IOException {
		// create new case add/delete note

		super.logIn();
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(casesPom.frameOne);
		driver.switchTo().frame(casesPom.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("CaseVerifyNotes");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		Thread.sleep(2000);
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		Thread.sleep(2000);
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(8000);
		driver.switchTo().defaultContent();
		Thread.sleep(4000);
		Assert.assertEquals("Case title is wrong", "CaseVerifyNotes (DRAFT)", casesPom.caseTitleDraft.getText());
		casesPom.caseNotes.click();
		Thread.sleep(4000);
		casesPom.verifyNotesTable();
		casesPom.addNewNoteButton.click();
		Thread.sleep(3000);
		casesPom.addNote("This is note");
		Thread.sleep(3000);
		casesPom.verifyAddedNote("This is note", "samuel-acm");
		driver.navigate().refresh();
		Thread.sleep(10000);
		casesPom.verifyAddedNote("This is note", "samuel-acm");
		casesPom.deleteNoteButton.click();
		Thread.sleep(4000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		casesPom.verifyIfNoteIsDeleted();
		casesPom.changeCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(casesPom.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(casesPom.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		casesPom.deleteCase();
		driver.switchTo().defaultContent();
		casesPom.refreshPage.click();
		Thread.sleep(3000);
		casesPom.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddremovePicture() throws InterruptedException, IOException, AWTException {

		super.logIn();
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(casesPom.frameOne);
		driver.switchTo().frame(casesPom.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("CaseTestMilanCongressionalResponse");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeCongressionalResponse();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		Thread.sleep(2000);
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		Thread.sleep(2000);
		casePom.selectParticipantClick();
		Thread.sleep(2000);
		casePom.searchForUsers();
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		casesPom.verifyCreatedCaseInfo("CaseTestMilanCongressionalResponse", "Congressional Response");
		casesPom.VerifycreatedDate();
		Assert.assertTrue(casesPom.caseTitleDraft.getText().equals("CaseTestMilanCongressionalResponse (DRAFT)"));
		casesPom.verifyDetailsSection();
		Thread.sleep(2000);
		casesPom.insertPicture.click();
		Thread.sleep(3000);
		casesPom.browsePictureButton.click();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(6000);
		casesPom.detailsSaveButton.click();
		casesPom.verifyInsertedImage();
		Thread.sleep(2000);
		casesPom.deleteInsertedImage();
		Thread.sleep(3000);
		casesPom.detailChangeStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(casesPom.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(casesPom.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		casesPom.deleteCase();
		driver.switchTo().defaultContent();
		casesPom.verifyCreatedCaseInfo("CaseTestMilanCongressionalResponse", "Congressional Response");
		casesPom.refreshPage.click();
		Thread.sleep(3000);
		Assert.assertTrue(casesPom.caseTitleDraft.getText().equals("CaseTestMilanCongressionalResponse (IN APPROVAL)"));
		casesPom.verifyCreatedCaseInfo("CaseTestMilanCongressionalResponse", "Congressional Response");
		casesPom.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCostsheetVerifyCostSummaryTableData() throws InterruptedException {

		super.logIn();
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

}
