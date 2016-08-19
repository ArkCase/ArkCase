package com.armedia.arkcase.uitests.cases.participants;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;

public class CaseParticipantsPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[1]/div/span")
	WebElement participantsTabletitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[1]/div/div/button")
	WebElement addParticipantBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	public WebElement typeColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement nameColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[1]/div")
	WebElement typeFirstRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[1]/div")
	WebElement typeSecondRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[3]/div/div[1]/div")
	WebElement typeThirdRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[4]/div/div[1]/div")
	WebElement typeFourthRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[5]/div/div[1]/div")
	WebElement typeFifthRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[2]/div")
	WebElement nameFirstRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[2]/div")
	WebElement nameSecondRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[3]/div/div[2]/div")
	WebElement nameThirdRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[4]/div/div[2]/div")
	WebElement nameForthRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[5]/div/div[2]/div")
	WebElement nameFifthRow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/a[1]/i")
	WebElement firstRowEditBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/a[2]")
	public
	WebElement firstRowDeleteBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[3]/a[1]/i")
	public
	WebElement secondRowEditBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[3]/a[2]/i")
	public
	WebElement secondrowDeleteBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[3]/div/div[3]/a[1]/i")
	public
	WebElement thirdRowEditBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[3]/div/div[3]/a[2]")
	public
	WebElement thirdRowDeleteBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[4]/div/div[3]/a[1]")
	public
	WebElement fourthRowEditBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[4]/div/div[3]/a[2]")
	public
	WebElement fourthRowDeleteBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[5]/div/div[3]/a[1]")
	WebElement fifthRowEditBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[5]/div/div[3]/a[2]")
	public
	WebElement fifthRowDeleteBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[2]")
	WebElement noDataLabel;

	// add participants popup

	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[1]/span")
	WebElement addParticipantTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select")
	WebElement participantTypeDropDown;
	// participant type drop down
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[2]")
	WebElement assignee;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[3]")
	WebElement coOwner;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[4]")
	WebElement supervisor;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[5]")
	WebElement owningGroup;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[6]")
	WebElement approver;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[7]")
	WebElement collaborator;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[8]")
	WebElement follower;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[9]")
	WebElement reader;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/select/option[10]")
	WebElement noAccess;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/input")
	WebElement participantNameInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement addParticipantSaveBtn;
	// search user
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[1]")
	WebElement searchUserTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[1]/div/input")
	WebElement searchUserInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[1]/div/span/button")
	public
	WebElement searchBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement searhedUserType;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement searchedUserTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	public
	WebElement searchedUserName;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[3]/button[2]")
	WebElement okBtn;
	// owning grooup
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editRecordPopUpTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/input")
	WebElement participantNameGroup;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[1]")
	WebElement searchGroupPopUptitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div/div")
	public
	WebElement searchedGroupName;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[1]/div/span/button")
	WebElement searchGroupBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[2]/div/div[1]/div/input")
	WebElement searchGroupInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/div/search-modal/div[3]/button[2]")
	WebElement searchGroupOkBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement editRecordSaveBtn;

	public void verifyParticipantsTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(participantsTabletitle.getText(), "Participants", "Partitipants table name is wrong");
		softAssert.assertTrue(addParticipantBtn.isDisplayed(), "Participants add button is not displayed");
		softAssert.assertEquals(typeColumnName.getText(), "Type", "Partitipants type column name is wrong");
		softAssert.assertEquals(nameColumnName.getText(), "Name", "Partitipants name column name is wrong");
		softAssert.assertAll();
	}

	public void verifyParticipantsTypeTableData() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeFirstRow.getText(), "*", "Type * name is wrong");
		softAssert.assertEquals(typeSecondRow.getText(), "assignee", "Type Assignee name is wrong");
		softAssert.assertEquals(typeThirdRow.getText(), "follower", "Type follower name is wrong");
		softAssert.assertEquals(typeFourthRow.getText(), "owning group", "Type owning group name is wrong");
		softAssert.assertEquals(typeFifthRow.getText(), "reader", "Type reader name is wrong");
		softAssert.assertAll();

	}

	public void verifyParticipantsNameTableData(String assignee, String follower, String owningGroup, String reader) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(nameFirstRow.getText(), "*", "Name * name is wrong");
		softAssert.assertEquals(nameSecondRow.getText(), assignee, "Name Assignee name is wrong");
		softAssert.assertEquals(nameThirdRow.getText(), follower, "Name follower name is wrong");
		softAssert.assertEquals(nameForthRow.getText(), owningGroup, "Name owning group name is wrong");
		softAssert.assertEquals(nameFifthRow.getText(), reader, "Name reader name is wrong");
		softAssert.assertAll();

	}

	public void verifyParticipantsEditDeleteButtons() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(firstRowEditBtn.isDisplayed(), "Edit button in first row is not displayed");
		softAssert.assertTrue(firstRowDeleteBtn.isDisplayed(), "Delete button in first row is not displayed");
		softAssert.assertTrue(secondRowEditBtn.isDisplayed(), "Edit button in second row is not displayed");
		softAssert.assertTrue(secondrowDeleteBtn.isDisplayed(), "Delete button in second row is not displayed");
		softAssert.assertTrue(thirdRowEditBtn.isDisplayed(), "Edit button in third row is not displayed");
		softAssert.assertTrue(thirdRowDeleteBtn.isDisplayed(), "Delete button in third row is not displayed");
		softAssert.assertTrue(fourthRowEditBtn.isDisplayed(), "Edit button in fourth row is not displayed");
		softAssert.assertTrue(fourthRowDeleteBtn.isDisplayed(), "Delete button in fourth row is not displayed");
		softAssert.assertTrue(fifthRowEditBtn.isDisplayed(), "Edit button is fifth row is not displayed");
		softAssert.assertTrue(fifthRowDeleteBtn.isDisplayed(), "Delete button is fifth row is not displayed");
		softAssert.assertAll();

	}

	public void verifyParticipantsOnCreatedCaseWithOnlyFollower(String assignee, String follower, String owningGroup,
			String reader) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeSecondRow.getText(), "assignee", "Type Assignee name is wrong");
		softAssert.assertEquals(typeThirdRow.getText(), "follower", "Type follower name is wrong");
		softAssert.assertEquals(typeFourthRow.getText(), "owning group", "Type owning group name is wrong");
		softAssert.assertEquals(typeFifthRow.getText(), "reader", "Type reader name is wrong");
		softAssert.assertEquals(nameFirstRow.getText(), "*", "Name * name is wrong");
		softAssert.assertEquals(nameSecondRow.getText(), assignee, "Name Assignee name is wrong");
		softAssert.assertEquals(nameThirdRow.getText(), follower, "Name follower name is wrong");
		softAssert.assertEquals(nameForthRow.getText(), owningGroup, "Name owning group name is wrong");
		softAssert.assertEquals(nameFifthRow.getText(), reader, "Name reader name is wrong");
		softAssert.assertAll();

	}

	public void verifyParticipantsOnCreatedCaseWithOnlyOwner(String assignee, String owningGroup, String reader) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeSecondRow.getText(), "assignee", "Type Assignee name is wrong");
		softAssert.assertEquals(typeThirdRow.getText(), "owning group", "Type owning group  name is wrong");
		softAssert.assertEquals(typeFourthRow.getText(), "reader", "Type reader name is wrong");
		softAssert.assertEquals(nameSecondRow.getText(), assignee, "Name Assignee name is wrong");
		softAssert.assertEquals(nameThirdRow.getText(), owningGroup, "Name owning Group name is wrong");
		softAssert.assertEquals(nameForthRow.getText(), reader, "Name reader name is wrong");
		softAssert.assertAll();

	}

	public void clickAddParticipantsBtn() {
		addParticipantBtn.click();
	}

	public void verifyAddParticipantsPopUpTitle() {
		Assert.assertEquals("Add participants popup title is wrong", "Add Participant", addParticipantTitle.getText());
	}

	public void clickParticipantTypeDropDown() {
		participantTypeDropDown.click();
	}

	public void selectParticipantTypeCoOwner() throws InterruptedException {

		Assert.assertEquals("Participant tytpe CoOwner name is wrong", "Co-Owner", coOwner.getText());
		Select dropDown = new Select(
				driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/form/div[1]/select")));
		dropDown.selectByVisibleText("Co-Owner");

	}

	public void clickParticipantNameInput() {
		participantNameInput.click();
	}

	public void verifySearchUserTitle() {
		Assert.assertEquals("Search usert titile in popup is wrong", "Search User", searchUserTitle.getText());
	}

	public void searchUserInput(String name) {
		searchUserInput.click();
		searchUserInput.sendKeys(name);
	}

	public void verifySerarchedUser(String name, String id, String title, String userName) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedName.getText(), name, "Searched usrename is wrong");
		softAssert.assertEquals(searhedUserType.getText(), id, "Searched user id is wrong");
		softAssert.assertEquals(searchedUserTitle.getText(), title, "Searched user title is wrong");
		softAssert.assertEquals(searchedUserName.getText(), userName, "Searched username is wrong");
		softAssert.assertAll();
	}

	public void clickOkBtn() {
		okBtn.click();
	}

	public void clickAddParticipantSaveBtn() {
		addParticipantSaveBtn.click();
	}

	public void verifyAddedparticipantCoOwner(String assignee, String coowner, String owningGroup, String reader) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeSecondRow.getText(), "assignee", "Type Assignee name is wrong");
		softAssert.assertEquals(typeThirdRow.getText(), "co-owner", "Type co-owner is wrong");
		softAssert.assertEquals(typeFourthRow.getText(), "owning group", "Type owning group name is wrong");
		softAssert.assertEquals(typeFifthRow.getText(), "reader", "Type reader name is wrong");
		softAssert.assertEquals(nameSecondRow.getText(), assignee, "Name Assignee name is wrong");
		softAssert.assertEquals(nameThirdRow.getText(), coowner, "Name co-owner name is wrong");
		softAssert.assertEquals(nameForthRow.getText(), owningGroup, "Name owning group name is wrong");
		softAssert.assertEquals(nameFifthRow.getText(), reader, "Name reader name is wrong");
		softAssert.assertAll();

	}

	public void verifyDeletedParticipantCoOwner() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeThirdRow.getText(), "owning group", "Participant is not deleted");
		softAssert.assertAll();

	}

	public void selectParticipantTypeApprover() {
		Assert.assertEquals("Participant tytpe Approver name is wrong", "Approver", approver.getText());
		Select dropDown = new Select(
				driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/form/div[1]/select")));
		dropDown.selectByVisibleText("Approver");

	}

	public void verifyAddedParticipantApprover(String approver, String assignee, String owningGroup, String reader) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeSecondRow.getText(), "approver", "Type Approver name is wrong");
		softAssert.assertEquals(typeThirdRow.getText(), "assignee", "Type assignee is wrong");
		softAssert.assertEquals(typeFourthRow.getText(), "owning group", "Type owning group name is wrong");
		softAssert.assertEquals(typeFifthRow.getText(), "reader", "Type reader name is wrong");
		softAssert.assertEquals(nameSecondRow.getText(), approver, "Name Assignee name is wrong");
		softAssert.assertEquals(nameThirdRow.getText(), assignee, "Name co-owner name is wrong");
		softAssert.assertEquals(nameForthRow.getText(), owningGroup, "Name owning group name is wrong");
		softAssert.assertEquals(nameFifthRow.getText(), reader, "Name reader name is wrong");
		softAssert.assertAll();

	}

	public void verifyDeletedParticipantApprover() {

		Assert.assertEquals("Participant approver is not deleted", "assignee", typeSecondRow.getText());

	}

	public void selectParticipantTypeCollaborator() {

		Select dropDown = new Select(
				driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/form/div[1]/select")));
		dropDown.selectByVisibleText("Collaborator");

	}

	public void verifyEditParticipantsPopUpTitle() {
		Assert.assertEquals("Edit participants popup title is wrong", "Edit Record", addParticipantTitle.getText());
	}

	public void verifyApproverEditToCollaborator() {
		Assert.assertEquals("The approver is not eddited", "collaborator", typeThirdRow.getText());
	}

	public void verifyChangedAssignee(String name) {
		Assert.assertEquals("Assignee name is not changed", name, nameSecondRow.getText());
	}

	public void verifyChangedReader(String name) {
		Assert.assertEquals("Reader name is not changed", name, nameForthRow.getText());
		Assert.assertEquals("Reader for the created case is not shown ", "samuel-acm", nameFifthRow.getText());
	}

	public void selectParticipantTypeFollower() {

		Select dropDown = new Select(
				driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/form/div[1]/select")));
		dropDown.selectByVisibleText("Follower");

	}

	public void verifyAddedParticipantFollower(String name) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeThirdRow.getText(), "follower", "Participant type follower is not added");
		softAssert.assertEquals(nameThirdRow.getText(), name, "Participant name of folloer is wrong");
		softAssert.assertAll();

	}

	public void verifyDeletedFollower() {
		Assert.assertEquals("The follower is not deleted", "owning group", typeThirdRow.getText());
	}

	public void selectParticipantTypeNoAcces() {

		Select dropDown = new Select(
				driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/form/div[1]/select")));
		dropDown.selectByVisibleText("No Access");

	}

	public void VerifyNoAccesForUser() {
		Assert.assertEquals("The user still have acces to the case", "[ No data ]", noDataLabel.getText());

	}

	public void selectParticipantTypeSupervisor() {

		Select dropDown = new Select(
				driver.findElement(By.xpath("/html/body/div[5]/div/div/div[2]/form/div[1]/select")));
		dropDown.selectByVisibleText("Supervisor");

	}

	public void verifyAddedParticipantSupervisor(String name) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(typeFifthRow.getText(), "supervisor", "Participant type supervisor is not added");
		softAssert.assertEquals(nameFifthRow.getText(), name, "Participant name of supervisor is wrong");
		softAssert.assertAll();

	}

	public void verifyDeletedSupervisor() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[5]/div/div[1]/div"))
				.size();
		Assert.assertTrue("Supervisor is not deleted", i == 0);

	}

	public void verifyIfReaderCanBeDeletdDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-participants/div/div[2]/div/div[1]/div[1]/div[2]/div/div[4]/div/div[1]/div"))
				.size();

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(i > 0, "The reader should not be deleted");
		softAssert.assertEquals(typeFourthRow.getText(), "reader", "Reader type name si wrong");
		softAssert.assertAll();

	}

	public void verifyIfOwningGroupCanBeDeletd() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(nameThirdRow.getText(), "ACM_INVESTIGATOR_DEV",
				"Owning group name is wrong, owning group should not be deleted");
		softAssert.assertEquals(typeThirdRow.getText(), "owning group", "Owning group type name is wrong");
		softAssert.assertAll();

	}

	public void verifyIfassigneeCanBeDeletd() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(nameSecondRow.getText(), "ann-acm",
				"Assignee name is wrong, assigne should not be deleted");
		softAssert.assertEquals(typeSecondRow.getText(), "assignee", "Assignee type name si wrong");
		softAssert.assertAll();

	}

	public void verifyIfFirstRowWithStarCanBeDeleted() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(nameFirstRow.getText(), "*", "* name is wrong, the * should not be deleted");
		softAssert.assertEquals(typeFirstRow.getText(), "*", "* type name si wrong");
		softAssert.assertAll();
	}

	public void verifyEditOwningGroupPopUpTitle() {
		Assert.assertEquals("Edit owning group popup title is wrong", "Edit Record", editRecordPopUpTitle.getText());
	}

	public void clickParticipantGroupName() {
		participantNameGroup.click();
	}

	public void verifySearchGroupPopUpTitle() {
		Assert.assertEquals("Search group popup title is wrong", "Search Group", searchGroupPopUptitle.getText());
	}

	public void searchGroupInput(String group) {
		searchGroupInput.click();
		searchGroupInput.sendKeys(group);
	}

	public void clickSearchGroupBtn() {
		searchGroupBtn.click();
	}

	public void verifySearchedGroup(String groupName) {
		Assert.assertEquals("Searched group name is wrong", groupName, searchedGroupName.getText());
	}

	public void clickSearchGroupOkBtn() {
		searchGroupOkBtn.click();
	}

	public void clickEditRecordSaveBtn() {
		editRecordSaveBtn.click();
	}

	public void verifyChangedOwningGroup(String owningGroup) {
		Assert.assertEquals("Owning group name is not changed", owningGroup, nameThirdRow.getText());
	}

}
