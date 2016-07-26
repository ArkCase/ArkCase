package com.armedia.arkcase.uitests.task;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

public class TaskPage {
	
	public @FindBy(how = How.ID, using = "saveButton")
    WebElement saveButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[1]/h3/span")
	WebElement taskTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[1]/label")
	WebElement asignToLable;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[1]/input")
	WebElement asignToInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[3]/label")
	WebElement associeteWithLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[3]/input")
	WebElement associeteWithInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[3]/div/label")
	WebElement subjectLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[3]/div/input")
	WebElement subjectInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[1]/label")
	WebElement startDateLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[1]/div/div/input")
	WebElement startDateInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[1]/div/div/span/button")
	WebElement startDateCalendar;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[2]/label/select")
	@CacheLookup
	WebElement statusDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[2]/label/select/option[1]")
	@CacheLookup
	WebElement statusActive;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[2]/label/select/option[2]")
	@CacheLookup
	WebElement statusInActive;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[2]/label/select/option[3]")
	@CacheLookup
	WebElement statusClosed;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[3]/label")
	@CacheLookup
	WebElement dueDateLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[3]/div/div/input")
	@CacheLookup
	WebElement dueDateInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[3]/div/div/span/button")
	@CacheLookup
	WebElement dueDateCalendar;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")
	@CacheLookup
	WebElement priorityDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select/option[1]")
	@CacheLookup
	WebElement priorityLow;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select/option[2]")
	@CacheLookup
	WebElement priorityMedium;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select/option[3]")
	@CacheLookup
	WebElement priorityHigh;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select/option[4]")
	@CacheLookup
	WebElement priorityExpedite;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[5]/div/label")
	@CacheLookup
	WebElement completeLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[5]/div/input")
	@CacheLookup
	WebElement completeInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/label")
	@CacheLookup
	WebElement notesLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/div/div[1]/div/span/a/i")
	WebElement editButton;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/a")
	@CacheLookup
	WebElement newButton;
	@FindBy(how = How.XPATH, using = "/html/body/header/div/nav/ul/li/div/div[3]/div/a/i")
	@CacheLookup
	WebElement mainTaskButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[1]/div/button")
	WebElement notesStyle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[2]/button[1]")
	WebElement notesBold;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[2]/button[2]")
	WebElement notesItalic;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[2]/button[3]")
	WebElement notesUnderline;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[2]/button[4]")
	WebElement notesRemoveFontStle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[3]/div/button")
	WebElement notesFontFamily;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[4]/div/button")
	WebElement notesFontSize;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[5]/button")
	WebElement notesRecentColor;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[5]/div/button")
	WebElement notesMoreColor;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[6]/button[1]")
	WebElement notesUnorderList;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[6]/button[2]")
	WebElement notesOrderList;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[6]/div/button")
	WebElement notesParagraph;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[7]/div/button")
	WebElement notesLineHeight;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[8]/div/button")
	WebElement notesTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[9]/button[3]")
	WebElement notesHorizontalLine;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[10]/button[1]")
	WebElement notesFullScreen;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[10]/button[2]")
	WebElement notesCodeView;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[11]/button")
	WebElement notesHelp;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[9]/button[1]")
	WebElement notesInsertLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[2]/div[1]/input")
	WebElement notesLinkTextToDisplay;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[2]/div[2]/input")
	WebElement notesLinkUrl;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[3]/button")
	WebElement notesLinkInsertButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[9]/button[2]")
	WebElement notesPicture;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[2]/div[1]/input")
	WebElement pictureBrowse;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[3]/button")
	WebElement insertImageButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/search-modal/div[2]/div/div[1]/div/input")
	@CacheLookup
	WebElement userSearchBox;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/search-modal/div[2]/div/div[1]/div/span/button")
	@CacheLookup
	WebElement userSearchButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/search-modal/div[3]/button[2]")
	@CacheLookup
	WebElement userSearchConfirm;
	@FindBy(how = How.XPATH, using = "/html/body/div[6]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	@CacheLookup
	WebElement userNameFound;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[1]/nav/div[1]/div/div/div[2]/a/strong/span[1]")
	WebElement taskLogoutf;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[1]/nav/div[1]/div/div/div[2]/ul/li[4]/a")
	WebElement taskLogouts;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[3]/div[4]/p/a")
	WebElement linkNotes;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/a")
	WebElement secondPageUser;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/h4/a")
	WebElement secondSubject;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[1]/div/a")
	WebElement secondComplete;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[3]/div/a")
	WebElement secondPriority;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[4]/div/a")
	WebElement secondStartDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[5]/div/a")
	WebElement secondDueDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[1]/nav/ul/li[4]/a/strong")
	WebElement moduleTask;
	// task associate with case
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[2]/label/select")
	WebElement parentTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[2]/label/select/option[2]")
	WebElement associateWithCAase;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[1]/div[1]/h4/a")
	WebElement casename;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[7]/a")
	WebElement casestaskLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a")
	WebElement taskInTaskTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[2]/div[1]/div[1]/button[3]")
	WebElement tableForward;
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[4]") WebElement deleteTask;

	public void newTask() throws InterruptedException {

		editButton.isEnabled();

		Thread.sleep(2000);
		newButton.isDisplayed();
		newButton.isEnabled();
		newButton.click();
		mainTaskButton.isDisplayed();
		mainTaskButton.isEnabled();
		mainTaskButton.click();
		Thread.sleep(2000);
	}

	public void newButtonClick() throws InterruptedException {

		newButton.click();
		Thread.sleep(3000);
	}

	public void newTaskButtonClick() throws InterruptedException {

		mainTaskButton.click();
		Thread.sleep(5000);

	}

	public void verifySubjectTitleInput() {

		Assert.assertEquals("Subject", subjectLabel.getText());
		Assert.assertTrue(subjectInput.isDisplayed());
		Assert.assertTrue(subjectInput.isEnabled());

	}

	public void verifyTaskTitle() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(taskTitle.getText(), "New Task", "Task title is wrong");
		softAssert.assertAll();

	}

	public void verifyAssighnToLabelInput() {
		Assert.assertEquals("Assign To", asignToLable.getText());
		Assert.assertTrue(asignToInput.isDisplayed());
		Assert.assertTrue(asignToInput.isEnabled());

	}

	public void verifyAssociateWith() {
		Assert.assertEquals("Associate with Complaint or Case", associeteWithLabel.getText());
		Assert.assertTrue("Associate with input is not displayed", associeteWithInput.isDisplayed());
		Assert.assertTrue("Associate with input is enabled", associeteWithInput.isEnabled());
	}

	public void VerifyAssociateWithEmptyTask() {

		Assert.assertEquals("Associate with Complaint or Case", associeteWithLabel.getText());
		Assert.assertTrue("Associate with input is not displayed", associeteWithInput.isDisplayed());
		Assert.assertFalse("Associate with input is enabled", associeteWithInput.isEnabled());

	}

	public void verifyStartDate() {

		Assert.assertEquals("Start Date", startDateLabel.getText());
		Assert.assertTrue(startDateInput.isDisplayed());
		Assert.assertTrue(startDateInput.isEnabled());
		Assert.assertTrue(startDateCalendar.isDisplayed());
		Assert.assertTrue(startDateCalendar.isEnabled());
	}

	public void verifyStatus() {

		Assert.assertTrue(statusDropDown.isDisplayed());
		Assert.assertTrue(statusDropDown.isEnabled());
		Assert.assertTrue(statusActive.isDisplayed());
		Assert.assertTrue(statusActive.isEnabled());
		Assert.assertTrue(statusInActive.isDisplayed());
		Assert.assertTrue(statusInActive.isEnabled());
		Assert.assertTrue(statusClosed.isDisplayed());
		Assert.assertTrue(statusClosed.isEnabled());
	}

	public void verifyDueDate() {

		Assert.assertEquals("Due Date", dueDateLabel.getText());
		Assert.assertTrue(dueDateInput.isDisplayed());
		Assert.assertTrue(dueDateInput.isEnabled());
		Assert.assertTrue(dueDateCalendar.isDisplayed());
		Assert.assertTrue(dueDateCalendar.isEnabled());

	}

	public void verifyPriority() {

		Assert.assertTrue(priorityDropDown.isDisplayed());
		Assert.assertTrue(priorityDropDown.isEnabled());
		Assert.assertTrue(priorityLow.isDisplayed());
		Assert.assertTrue(priorityLow.isEnabled());
		Assert.assertTrue(priorityMedium.isDisplayed());
		Assert.assertTrue(priorityMedium.isEnabled());
		Assert.assertTrue(priorityHigh.isDisplayed());
		Assert.assertTrue(priorityHigh.isEnabled());
		Assert.assertTrue(priorityExpedite.isDisplayed());
		Assert.assertTrue(priorityExpedite.isEnabled());

	}

	public void verifyNotes() {

		Assert.assertEquals("Notes", notesLabel.getText());
		Assert.assertTrue(notesStyle.isDisplayed());
		Assert.assertTrue(notesStyle.isEnabled());
		Assert.assertTrue(notesStyle.isDisplayed());
		Assert.assertTrue(notesBold.isEnabled());
		Assert.assertTrue(notesBold.isDisplayed());
		Assert.assertTrue(notesItalic.isEnabled());
		Assert.assertTrue(notesItalic.isDisplayed());
		Assert.assertTrue(notesUnderline.isEnabled());
		Assert.assertTrue(notesUnderline.isDisplayed());
		Assert.assertTrue(notesRemoveFontStle.isEnabled());
		Assert.assertTrue(notesRemoveFontStle.isDisplayed());
		Assert.assertTrue(notesFontFamily.isEnabled());
		Assert.assertTrue(notesFontFamily.isDisplayed());
		Assert.assertTrue(notesFontSize.isEnabled());
		Assert.assertTrue(notesFontSize.isDisplayed());
		Assert.assertTrue(notesRecentColor.isEnabled());
		Assert.assertTrue(notesRecentColor.isDisplayed());
		Assert.assertTrue(notesMoreColor.isEnabled());
		Assert.assertTrue(notesMoreColor.isDisplayed());
		Assert.assertTrue(notesUnorderList.isEnabled());
		Assert.assertTrue(notesUnorderList.isDisplayed());
		Assert.assertTrue(notesOrderList.isEnabled());
		Assert.assertTrue(notesOrderList.isDisplayed());
		Assert.assertTrue(notesParagraph.isEnabled());
		Assert.assertTrue(notesParagraph.isDisplayed());
		Assert.assertTrue(notesLineHeight.isEnabled());
		Assert.assertTrue(notesLineHeight.isDisplayed());
		Assert.assertTrue(notesTable.isEnabled());
		Assert.assertTrue(notesTable.isDisplayed());
		Assert.assertTrue(notesInsertLink.isEnabled());
		Assert.assertTrue(notesInsertLink.isDisplayed());
		Assert.assertTrue(notesPicture.isEnabled());
		Assert.assertTrue(notesPicture.isDisplayed());
		Assert.assertTrue(notesHorizontalLine.isEnabled());
		Assert.assertTrue(notesHorizontalLine.isDisplayed());
		Assert.assertTrue(notesFullScreen.isEnabled());
		Assert.assertTrue(notesFullScreen.isDisplayed());
		Assert.assertTrue(notesCodeView.isEnabled());
		Assert.assertTrue(notesCodeView.isDisplayed());
		Assert.assertTrue(notesHelp.isEnabled());
		Assert.assertTrue(notesHelp.isDisplayed());

	}

	public void verifySaveButton() {
		Assert.assertTrue(saveButton.isDisplayed());
		Assert.assertFalse(saveButton.isEnabled());
	}

	public void saveButtonClick() {
		saveButton.click();
	}

	public void assignTo(String user) throws InterruptedException {

		asignToInput.click();
		Assert.assertFalse(userSearchConfirm.isEnabled());
		Assert.assertFalse(userSearchButton.isEnabled());
		userSearchBox.click();
		userSearchBox.sendKeys(user);
		userSearchButton.click();
		Thread.sleep(3000);
		userNameFound.click();
		Thread.sleep(2000);
		userSearchConfirm.click();
		Thread.sleep(3000);
	}

	public void typeAssociateWithComplaintOrCase(String comOrCase) {
		associeteWithInput.click();
		associeteWithInput.sendKeys(comOrCase);
	}

	public void typeSubject(String subject) {
		subjectInput.click();
		subjectInput.sendKeys(subject);
	}

	public void typeStartDate(String startdate) {

		startDateInput.click();
		startDateInput.clear();
		startDateInput.sendKeys(startdate);
	}

	public void selectStatusClosed() {

		statusDropDown.click();
		statusDropDown.sendKeys("Closed");
	}

	public void selectStatusActive() {

		statusDropDown.click();
		statusDropDown.sendKeys("Active");
	}

	public void selectStatusInActive() {

		statusDropDown.click();
		statusDropDown.sendKeys("InActive");
	}

	public void typeDuedate(String duedate) {
		dueDateInput.click();
		dueDateInput.clear();
		dueDateInput.sendKeys(duedate);

	}

	public void selectPriorityLow() {

		priorityDropDown.click();
		priorityDropDown.sendKeys("Low");

	}

	public void selectPriorityMedium() {

		priorityDropDown.click();
		priorityDropDown.sendKeys("Medium");
	}

	public void selectPriorityHigh() {

		priorityDropDown.click();
		priorityDropDown.sendKeys("High");
	}

	public void selectPriorityExpedite() {

		priorityDropDown.click();
		priorityDropDown.sendKeys("Expedite");
	}

	public void typeComplete(String percent) {

		completeInput.click();
		completeInput.clear();
		completeInput.sendKeys(percent);

	}

	public void selectStartDateFromCalendar() {
		startDateCalendar.click();

	}

	public void selectDuedateFromCalendar() {
		dueDateCalendar.click();
	}

	public void taskLogOut() {
		taskLogoutf.click();
		taskLogouts.click();
	}

	public void insertLink(String link, String url) {

		notesInsertLink.click();
		notesLinkTextToDisplay.click();
		notesLinkTextToDisplay.clear();
		notesLinkTextToDisplay.sendKeys(link);
		notesLinkUrl.click();
		notesLinkUrl.clear();
		notesLinkUrl.sendKeys(url);
		notesLinkInsertButton.click();
	}

	public void verifyLinkInNotes() {

		Assert.assertTrue(linkNotes.isDisplayed());
		Assert.assertTrue(linkNotes.isEnabled());
	}

	public void verifyTaskVariables(String user, String subject, String percent, String priority, String startDate,
			String dueDate) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondPageUser.getText(), user, "User is wrong");
		softAssert.assertEquals(secondSubject.getText(), subject, "Subject name is wrong");
		softAssert.assertEquals(secondComplete.getText(), percent, "Percent complete value is wrong");
		softAssert.assertEquals(secondPriority.getText(), priority, "Priority value is wrong");
		softAssert.assertEquals(secondStartDate.getText(), startDate, "Start date values is wrong");
		softAssert.assertEquals(secondDueDate.getText(), dueDate, "Due date value is wrong");
		softAssert.assertAll();
	}

	public void moduleTask() throws InterruptedException {

		editButton.isEnabled();
		Thread.sleep(2000);
		Assert.assertTrue(moduleTask.getText().equals("Tasks"));
		moduleTask.click();
		Thread.sleep(3000);
	}

	public void asscociateWithCase() {

		Assert.assertEquals("Associate with Case file name is wrong", "Case File", associateWithCAase.getText());
		associateWithCAase.click();
	}

	public void associateInput(String associate) {

		associeteWithInput.click();
		associeteWithInput.sendKeys(associate);

	}

	public void verifyTaskInCasesTaskTable() {

		Assert.assertEquals("Task in cases task table is wrong", "TestMilanassociate", taskInTaskTable.getText());

	}

	public void parentTypeDropDownClick() {

		parentTypeDropDown.click();
	}

	public void caseNameClick() {

		casename.click();
	}

	public void casesTaskLinkClick() {

		casestaskLink.click();
	}

}
