package com.armedia.arkcase.uitests.task;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.base.ArkCaseUtils;
import com.armedia.arkcase.uitests.group.SmokeTests;

public class NewTaskTests extends ArkCaseTestBase {

	TaskPage task = PageFactory.initElements(driver, TaskPage.class);
	TasksPage tasks = PageFactory.initElements(driver, TasksPage.class);
	ArkCaseUtils check = new ArkCaseUtils();

	@Test
	public void addNewTaskCheckForLabelsAndFilds() throws IOException, InterruptedException {

		task.newTask();
		task.verifyTaskTitle();
		task.verifySubjectTitleInput();
		task.verifyAssighnToLabelInput();
		task.VerifyAssociateWithEmptyTask();
		task.verifyStartDate();
		task.verifyDueDate();
		task.verifyPriority();
		task.verifyNotes();
		task.verifySaveButton();
		Thread.sleep(2000);
		task.taskLogOut();
		Thread.sleep(2000);
	}

	@Test
	@Category({ SmokeTests.class })
	public void createNewTaskStatusActive() throws IOException, InterruptedException {

		task.newTask();
		task.assignTo("sam");
		task.typeSubject("MilanActiveMIlan");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("20");
		Thread.sleep(3000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		Assert.assertEquals("Task status is not active", "ACTIVE", tasks.stateTask.getText());
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(4000);
		Assert.assertEquals("After delete button is clicked, task state should be DELETE", "DELETE", tasks.stateTask.getText());
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskSelectFromCalendarStartDateEndDate() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTaskTwo");
		task.selectStartDateFromCalendar();
		WebElement selectFromStartCalendar = driver.findElement(By.xpath("(//button[@type='button'])[25]"));
		selectFromStartCalendar.click();
		Thread.sleep(2000);
		task.selectDuedateFromCalendar();
		WebElement selectFromDueCalendar = driver.findElement(By.xpath("(//button[@type='button'])[25]"));
		selectFromDueCalendar.click();
		Thread.sleep(2000);
		task.typeComplete("32");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		task.verifyTaskVariables("Samuel Supervisor", "AutomateTestTaskTwo", "32", "Medium", "07/14/2016",
				"07/13/2016");
		Thread.sleep(3000);
		tasks.deleteTask.click();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTasksStatusInactive() throws IOException, InterruptedException {

		task.newTask();
		task.assignTo("samuel");
		task.typeSubject("AutomateTestTaskThreeMilan");
		task.typeStartDate("03/18/2016");
		task.selectStatusInActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("53");
		Thread.sleep(3000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		Assert.assertEquals("Task status soould be INACTIVE", "INACTIVE", tasks.stateTask.getText());
		Thread.sleep(3000);
		tasks.deleteButton.click();
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTasksStatusClosed() throws IOException, InterruptedException {

		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTaskThreeMilan");
		task.typeStartDate("03/18/2016");
		task.selectStatusClosed();
		Thread.sleep(3000);
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("53");
		Thread.sleep(7000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		Assert.assertEquals("Task status soould be CLOSED", "CLOSED", tasks.stateTask.getText());
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskStatusActiveExedite() throws IOException, InterruptedException {

		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTaskFour");
		task.typeStartDate("03/18/2016");
		task.selectStatusInActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("53");
		new Select(driver.findElement(
				By.xpath("/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")))
						.selectByVisibleText("Expedite");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		Assert.assertEquals("Status of the task is worng", "Expedite", tasks.priority.getText());
		Thread.sleep(3000);
		tasks.deleteButton.click();
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskStatusActiveBadPercent() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTask3");
		task.typeStartDate("03/18/2016");
		task.selectStatusInActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("101");
		Thread.sleep(2000);
		Assert.assertFalse("Save button sould not be enabled", task.saveButton.isEnabled());
		task.taskLogOut();
		Thread.sleep(5000);
	}

	@Test
	public void createNewTaskIfSubjectEmpty() throws InterruptedException, IOException {

		task.newTask();
		task.typeSubject("");
		task.typeStartDate("03/18/2016");
		task.selectStatusClosed();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("100");
		task.selectPriorityLow();
		Assert.assertFalse("Save button should not be enabled", task.saveButton.isEnabled());
		Thread.sleep(2000);
		task.taskLogOut();
	}

	@Test
	public void creteNewTaskIfStartDateIsEmpty() throws InterruptedException, IOException {

		task.newTask();
		task.typeSubject("AutomatedTAskTest4");
		task.typeStartDate("");
		task.selectStatusClosed();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("100");
		task.selectPriorityLow();
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		Assert.assertFalse(task.saveButton.isEnabled());
		Thread.sleep(2000);
		task.taskLogOut();
	}

	@Test
	public void creteNewTaskIfDueDateIsEmpty() throws InterruptedException, IOException {

		task.newTask();
		task.typeSubject("AutomatedTAskTest5");
		task.typeStartDate("03/19/2016");
		task.selectStatusClosed();
		task.typeDuedate("");
		Thread.sleep(2000);
		task.typeComplete("100");
		task.selectPriorityLow();
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		Assert.assertFalse(task.saveButton.isEnabled());
		Thread.sleep(2000);
		task.taskLogOut();
	}

	@Test
	public void createNewTaskWithDefultUser() throws InterruptedException, IOException {

		task.newTask();
		task.typeSubject("AutomatedTestFive123");
		task.typeStartDate("03/19/2016");
		task.selectStatusActive();
		task.typeDuedate("03/20/2016");
		Thread.sleep(2000);
		task.typeComplete("100");
		new Select(driver.findElement(
				By.xpath("/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")))
						.selectByVisibleText("Medium");
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		task.saveButtonClick();
		Thread.sleep(4000);
		tasks.taskList.click();
		Thread.sleep(2000);
		Assert.assertTrue(task.secondPriority.getText().equals("Medium"));
		task.verifyTaskVariables("Samuel Supervisor", "AutomatedTestFive123", "100", "Medium", "03/19/2016",
				"03/20/2016");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckPercentComplete() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("AutomatedTaskTest7");
		task.typeStartDate("03/19/2016");
		task.selectStatusActive();
		task.typeDuedate("03/20/2016");
		Thread.sleep(2000);
		task.typeComplete("a");
		Assert.assertFalse(task.completeInput.getText().matches("[a-zA-Z]"));
		task.selectPriorityHigh();
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		task.saveButtonClick();
		Thread.sleep(4000);
		task.taskLogOut();
	}

	@Test
	public void createNewTaskFerifyLabels() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("sam");
		task.typeSubject("TestMilan");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("20");
		Thread.sleep(2000);
		new Select(driver.findElement(
				By.xpath("/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")))
						.selectByVisibleText("Low");
		Thread.sleep(2000);
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		task.verifyTaskVariables("Samuel Supervisor", "TestMilan", "20", "Low", "03/18/2016", "03/19/2016");
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskVerifyTaskList() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan1");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		tasks.expandTask();
		WebElement details = driver.findElement(By.xpath("//li/ul/li/span/span[3]"));
		Assert.assertTrue(details.getText().equals("Details"));
		details.click();
		tasks.verifyDetailsTasksSection();
		WebElement rejectComment = driver.findElement(By.xpath("//li[2]/span/span[3]"));
		Assert.assertTrue(rejectComment.getText().equals("Reject Comments"));
		rejectComment.click();
		// tasks.verifyRejectCommnetsTable();
		WebElement attachments = driver.findElement(By.xpath("//li[3]/span/span[3]"));
		Assert.assertTrue(attachments.getText().equals("Attachments"));
		attachments.click();
		Thread.sleep(3000);
		tasks.verifyAttachmentTable();
		WebElement notes = driver.findElement(By.xpath("//li[4]/span/span[3]"));
		Assert.assertTrue(notes.getText().equals("Notes"));
		notes.click();
		tasks.verifyNotesTable();
		WebElement workFlow = driver.findElement(By.xpath("//li[5]/span/span[3]"));
		Assert.assertTrue(workFlow.getText().equals("Workflow Overview"));
		workFlow.click();
		tasks.verifyWorkOverView("Samuel Supervisor");
		WebElement history = driver.findElement(By.xpath("//li[6]/span/span[3]"));
		Assert.assertTrue(history.getText().equals("History"));
		history.click();
		Thread.sleep(4000);
		tasks.verifyHistorySection();
		WebElement eSignatures = driver.findElement(By.xpath("//li[7]/span/span[3]"));
		Assert.assertTrue(eSignatures.getText().equals("eSignatures"));
		eSignatures.click();
		Thread.sleep(2000);
		tasks.verifyEsignature();
		WebElement tags = driver.findElement(By.xpath("//li[8]/span/span[3]"));
		Assert.assertTrue(tags.getText().equals("Tags"));
		tags.click();
		Thread.sleep(2000);
		tasks.verifyTagsTable();
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertEquals("Task status is wrong", "DELETE", tasks.stateTask.getText());
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskVerifyTaskLinks() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilanVerify");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.overviewLinkClick();
		tasks.detailsLinkClick();
		tasks.verifyDetailsTasksSection();
		tasks.rejectCommentLink();
		// tasks.verifyRejectCommnetsTable();
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.notestLinkClick();
		tasks.verifyNotesTable();
		tasks.workflowLinkClick();
		tasks.verifyWorkOverView("Samuel Supervisor");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//div[5]/ul/li[7]/a")).click();
		Thread.sleep(4000);
		tasks.verifyHistorySection();
		Thread.sleep(2000);
		tasks.esignatureLinkClick();
		Thread.sleep(2000);
		tasks.verifyEsignature();
		tasks.tagsLinkClick();
		Thread.sleep(2000);
		tasks.verifyTagsTable();
		Thread.sleep(3000);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskFerifyandAddTaskDetails() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Sam Supervisor");
		task.typeSubject("TestMilan3");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.overviewLinkClick();
		tasks.detailsLinkClick();
		tasks.verifyDetailsTasksSection();
		tasks.editInsertedLInk();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.insertedLink.getText().equals("ArkCase1"));
		tasks.detailsSaveButton.click();
		Thread.sleep(2000);
		tasks.clearInsertedLInk();
		Thread.sleep(3000);
		tasks.insertLink("ArkCase", "http://www.arkcase.com/");
		tasks.detailsSaveButton.click();
		Thread.sleep(3000);
		tasks.clearInsertedLInk();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.taskdetailsPanel.getText().isEmpty());
		Thread.sleep(3000);
		tasks.detailsInsertPicture();
		Thread.sleep(2000);
		tasks.detailsRemovePicture();
		Thread.sleep(3000);
		tasks.overviewLinkClick();
		Thread.sleep(2000);
		tasks.deleteTask.click();
		Thread.sleep(3000);
		tasks.overviewLinkClick();
		Thread.sleep(2000);
		Assert.assertEquals("State of task is wrong", "DELETE", tasks.stateTask.getText());
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskFerifyAddDeleteNote() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan4");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.notestLinkClick();
		tasks.verifyNotesTable();
		tasks.addNewNote("bug");
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		tasks.verifyAddedNote("bug", "samuel-acm");
		tasks.editNote("bug2");
		Thread.sleep(2000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		tasks.verifyAddedNote("bug2", "samuel-acm");
		Thread.sleep(3000);
		tasks.deleteNote();
		Thread.sleep(3000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		Assert.assertTrue("The note is not deleted", tasks.verifyDeletedNote.getText().isEmpty());
		Thread.sleep(3000);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskFerifyAddTag() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan5");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.tagsLinkClick();
		tasks.verifyTagsTable();
		tasks.addNewTag("washington7");
		Thread.sleep(3000);
		tasks.verifyAddedTag("washington7", "samuel-acm");
		driver.navigate().refresh();
		Thread.sleep(8000);
		tasks.verifyAddedTag("washington7", "Samuel Supervisor");
		tasks.deleteAddedTag();
		Thread.sleep(4000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		Assert.assertTrue("The tag is not deleted", tasks.verifyDeletedRow.getText().isEmpty());
		tasks.deleteButton.click();
		Thread.sleep(4000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddSearchedTagDelete() throws InterruptedException, IOException {
		// create new task and add searched tag
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan5");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.tagsLinkClick();
		tasks.verifyTagsTable();
		tasks.addSearchedNewTag("armedia");
		tasks.verifyAddedTag("armedia", "samuel-acm");
		Thread.sleep(2000);
		tasks.deleteAddedTag();
		Thread.sleep(4000);
		driver.navigate().refresh();
		Thread.sleep(8000);
		Assert.assertTrue("The tag is not deleted", tasks.verifyDeletedRow.getText().isEmpty());
		tasks.deleteButton.click();
		Thread.sleep(4000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewFolderAndRename() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("rename folder");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(4000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.newFolderClick();
		Thread.sleep(2000);
		tasks.nameTheFirstFolder("document1");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.verifyFirstFolderName("document1");
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.renameFolder();
		Thread.sleep(3000);
		tasks.nameTheFirstFolder("document2");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.refreshButton.click();
		Thread.sleep(4000);
		tasks.expander.click();
		Thread.sleep(3000);
		Assert.assertEquals("The folder name is not updateed, rename is not working", "document2",
				tasks.firstRowDocumentTitle.getText());
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewFolderDeleteTheFolder() throws InterruptedException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("delete folder");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(4000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.newFolderClick();
		Thread.sleep(2000);
		tasks.nameTheFirstFolder("document1");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.verifyFirstFolderName("document1");
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.deleteFolder();
		Thread.sleep(3000);
		tasks.refreshButton.click();
		Thread.sleep(4000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyIfFolderIsDeleted();
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskCreateTwoFoldersCopyPaste() throws InterruptedException, IOException, AWTException {
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Copy Paste");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.newFolderClick();
		Thread.sleep(3000);
		tasks.nameTheFirstFolder("document1");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.verifyFirstFolderName("document1");
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.newFolderClick();
		Thread.sleep(3000);
		tasks.nameTheSecondFolder("document2");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(2000);
		tasks.verifySecondFolderName("document2");
		Thread.sleep(2000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(2000);
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.verifyNewDocumentMenu();
		Thread.sleep(2000);
		tasks.clickOtherDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(4000);
		tasks.verifySecondRowDocument("imageprofile.png", "Other", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(2000);
		tasks.performRightClickOnSecondRow();
		Thread.sleep(3000);
		tasks.copyDocument();
		Thread.sleep(2000);
		tasks.performRightClickOnThirdRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnThirdRow();
		Thread.sleep(2000);
		tasks.pasteDocument();
		Thread.sleep(4000);
		tasks.verifyForthRowDocument("imageprofile.png", "Other", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(2000);
		tasks.deleteButton.click();
		Thread.sleep(3000);
		Assert.assertTrue("Task state should be deleted", tasks.stateTask.getText().equals("DELETED"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskCreateTwoFoldersCutPaste() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Cut Paste");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.newFolderClick();
		Thread.sleep(3000);
		tasks.nameTheFirstFolder("document1");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.verifyFirstFolderName("document1");
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.newFolderClick();
		Thread.sleep(3000);
		tasks.nameTheSecondFolder("document2");
		Thread.sleep(2000);
		tasks.attachmentsTitle.click();
		Thread.sleep(2000);
		tasks.verifySecondFolderName("document2");
		Thread.sleep(2000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(2000);
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.verifyNewDocumentMenu();
		Thread.sleep(2000);
		tasks.clickOtherDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(4000);
		tasks.verifySecondRowDocument("imageprofile.png", "Other", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(2000);
		tasks.performRightClickOnSecondRow();
		Thread.sleep(3000);
		tasks.cutDocument();
		Thread.sleep(2000);
		tasks.performRightClickOnThirdRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnThirdRow();
		Thread.sleep(2000);
		tasks.pasteDocument();
		Thread.sleep(4000);
		tasks.verifyForthRowDocument("imageprofile.png", "Other", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(2000);
		tasks.verifyIfCutDocumentDisapierd();
		tasks.deleteButton.click();
		Thread.sleep(3000);
		Assert.assertTrue("Task state should be deleted", tasks.stateTask.getText().equals("DELETED"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentSendEmailVerifyForm()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan9");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.emailDocument();
		Thread.sleep(5000);
		tasks.verifyEmailPopUpTitle();
		tasks.searchUserEmailInput("Samuel Supervisor");
		Thread.sleep(3000);
		tasks.clickSearchEmailBtn();
		Thread.sleep(3000);
		tasks.verifySearchedUserEmail("Samuel Supervisor", "samuel-acm@armedia.com");
		tasks.searchedEmailUser.click();
		Thread.sleep(3000);
		tasks.clickSendEmailBtn();
		Thread.sleep(5000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewDocumentWitnessRequestPdf() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Add document pdf");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPdf();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("caseSummary.pdf", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyAddedDocumentAfterRefresh();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewDocumentNoticeOfInvesstigation()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("document notice");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(3000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickNoticeOfInvestigation();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Notice Of Investigation", "Samuel Supervisor", "1.0",
				"ACTIVE");
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyAddedDocumentAfterRefresh();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentSfSignatureDocx() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("document sfsignature");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickSfSignature();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Sf86 Signature", "Samuel Supervisor", "1.0", "ACTIVE");
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyAddedDocumentAfterRefresh();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentEDelivery() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("eDelivery");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyAddedDocumentAfterRefresh();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewDocumentGeneralReleaseXlsx() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("General release");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(3000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickGeneralRelease();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "General Release", "Samuel Supervisor", "1.0", "ACTIVE");
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyAddedDocumentAfterRefresh();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentMedicalRelease() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("medical release");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(3000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickMedicalRelease();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Medical Release", "Samuel Supervisor", "1.0", "ACTIVE");
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyAddedDocumentAfterRefresh();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentOtherRename() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("rename document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.renameDocument();
		Thread.sleep(3000);
		tasks.nameTheFirstDocument("test");
		Thread.sleep(3000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyFirstRowDocumentAfterRefresh("test", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentOtherReplace() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.typeSubject("replace document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.replaceDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(5000);
		tasks.attachmentsTitle.click();
		Thread.sleep(3000);
		tasks.refreshButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyFirstRowDocumentAfterRefresh("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "2.0",
				"ACTIVE");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentDeclareAsRecord() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan18");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.clickDeclareAsRecord();
		Thread.sleep(3000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "RECORD");
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifyAndEditInformationRibbon() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan19");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.editSubjectClickCncel();
		Thread.sleep(2000);
		tasks.editSubjectClickConfirm("TestMilan2");
		tasks.editPercentClickCancel();
		Thread.sleep(2000);
		tasks.editPercentClickConfirm("45");
		Thread.sleep(2000);
		tasks.editStartDateClickCancel();
		Thread.sleep(2000);
		tasks.editStartDateClickConfirm("04/06/2016");
		tasks.editDueDateClickCancel();
		Thread.sleep(2000);
		tasks.editDueDateClickConfirm("04/07/2016");
		tasks.EditPriorityCancelButton();
		tasks.priority.click();
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("Medium");
		tasks.priorityConfirmButton.click();
		tasks.refreshButton.click();
		Thread.sleep(3000);
		Assert.assertTrue(tasks.priority.getText().equals("Medium"));
		tasks.priority.click();
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("High");
		tasks.priorityConfirmButton.click();
		tasks.refreshButton.click();
		Thread.sleep(3000);
		Assert.assertTrue(tasks.priority.getText().equals("High"));
		tasks.priority.click();
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("Expedite");
		tasks.priorityConfirmButton.click();
		tasks.refreshButton.click();
		Thread.sleep(3000);
		Assert.assertTrue(tasks.priority.getText().equals("Expedite"));
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifyChnageAssigneeAndBack() throws InterruptedException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan20");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.assignee.click();
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("Bill Thomas");
		tasks.assigneeConfirm.click();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		Assert.assertTrue(tasks.assignee.getText().equals("Bill Thomas"));
		tasks.editAssigneeCheckButtonsIfDisabled();
		Thread.sleep(2000);
		tasks.assignee.click();
		Thread.sleep(2000);
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("Samuel Supervisor");
		tasks.assigneeConfirm.click();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		Assert.assertTrue(tasks.assignee.getText().equals("Samuel Supervisor"));
		tasks.editAssigneeCheckButtonsIfEnabled();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifySubscribeUnscubscribe() throws InterruptedException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("sunscribe");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		Thread.sleep(2000);
		tasks.SubscribeButton();
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifyClickComplete() throws InterruptedException {

		task.newTask();
		task.assignTo("Samuel SUpervisor");
		task.typeSubject("TestComplete");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.completeButtonClick();
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifyIfCompleteTaskDoesNotShowInListOfOpenTasks()
			throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("newtasakclickcomplete");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		tasks.completeButtonClick();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(4000);
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.moduleTask();
		Thread.sleep(3000);
		tasks.taskSearchBox("newtasakclickcomplete");
		tasks.serachButton.click();
		Thread.sleep(3000);
		WebElement list = driver.findElement(
				By.xpath("/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div"));
		Assert.assertTrue("The completed task is in the task list, it should not be there", list.getText().isEmpty());
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifyIfDeletedIsStillShownInListOfOpenTasks() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("sunscribetestMilan");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		tasks.deleteTask.click();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(4000);
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.moduleTask();
		Thread.sleep(3000);
		tasks.taskSearchBox("sunscribetestMilan");
		tasks.serachButton.click();
		Thread.sleep(3000);
		WebElement list = driver.findElement(
				By.xpath("/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div"));
		Assert.assertTrue(list.getText().isEmpty());
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckifDocumentCanBeAddIfTaskIsAsignOtherUser()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Acces Denide add document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.assignee.click();
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("Bill Thomas");
		tasks.assigneeConfirm.click();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		Thread.sleep(3000);
		Assert.assertTrue("Assignee name is wrong", tasks.assignee.getText().equals("Bill Thomas"));
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(3000);
		tasks.clickOtherDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(4000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(2000);
		tasks.expander.click();
		tasks.verifyAddDocumentAsignOtherUser();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckAceesDenideNotes() throws InterruptedException, IOException {
		// verify if note can be added if is assigned another user

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Acces Denide add note");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.assignee.click();
		new Select(driver.findElement(By.xpath("//select"))).selectByVisibleText("Bill Thomas");
		tasks.assigneeConfirm.click();
		Thread.sleep(2000);
		tasks.refreshButton.click();
		Assert.assertTrue(tasks.assignee.getText().equals("Bill Thomas"));
		tasks.taskList.click();
		Thread.sleep(2000);
		tasks.notestLinkClick();
		tasks.verifyNotesTable();
		tasks.addNewNote("bug");
		Thread.sleep(4000);
		tasks.verifyAddingNoteIfDifrentUserAsignTo();
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckAccesDenideIftaskIsClosedAddNote() throws InterruptedException, IOException {
		// verify if note can be added is task status is closed

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Acces Denide add note");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.completeButtonClick();
		Thread.sleep(4000);
		Assert.assertTrue(tasks.stateTask.getText().equals("CLOSED"));
		tasks.notestLinkClick();
		Assert.assertFalse("Add note button is enabled", tasks.addNewNoteButton.isEnabled());
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskChecIfDocumentCanBeAddedIfTaskIsClosed()
			throws InterruptedException, IOException, AWTException {
		// verify if document can be added if the task status is closed

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("Acces Denide");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.completeButtonClick();
		Thread.sleep(3000);
		Assert.assertTrue(tasks.stateTask.getText().equals("CLOSED"));
		tasks.attachmentLinkClick();
		Thread.sleep(3000);
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(3000);
		tasks.clickOtherDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(4000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(4000);
		tasks.expander.click();
		Thread.sleep(2000);
		tasks.verifyIfDocumentIfAddTaskStateIsClosed();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskCompleteFerifyWorkFlow() throws InterruptedException, IOException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("WorkFlow");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.workflowLink.click();
		Thread.sleep(3000);
		tasks.workFlowData("Samuel Supervisor");
		tasks.completeButtonClick();
		Thread.sleep(4000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCompleteFerifyAccessDeniedDocumentDelete()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("acces denied");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(3000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickOtherDocument();
		Thread.sleep(2000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(4000);
		Assert.assertEquals("The document is not uploaded", "imageprofile.png", tasks.firstRowDocumentTitle.getText());
		tasks.deleteButton.click();
		Thread.sleep(3000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.deleteDocument();
		Thread.sleep(3000);
		tasks.verifyIfDocumentIsDeletedStateDelete();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentDownloadTheDocument()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("download document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.downlaodDocument();
		Thread.sleep(7000);
		check.checkIfFileIsDownloaded("ArkCaseTesting");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddDocumentCheckOutDocument() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("checkout document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.checkOutDocument();
		Thread.sleep(7000);
		check.checkIfFileIsDownloaded("ArkCaseTesting");
		Thread.sleep(3000);
		tasks.verifyLockedIcon();
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.cancelEditingDocument();
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewWordDocumentEditWithWord() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("checkout document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.editWithWord();
		Thread.sleep(5000);
		ArkCaseTestUtils.presEnter();
		Thread.sleep(3000);
		ArkCaseTestUtils.shiftLeftAndPressEnter();
		Thread.sleep(8000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		ArkCaseTestUtils.saveWordDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.closeWordDocument();
		Thread.sleep(10000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocumentModified("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "2.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddDocumentDeleteTheDocument() throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("delete document");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.deleteDocument();
		Thread.sleep(3000);
		tasks.refreshButton.click();
		Thread.sleep(4000);
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.verifyIfDocumentIsDeleted();
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddDocumentDeclareAsRecoerdDownloadRecord()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("record document download");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.clickDeclareAsRecord();
		Thread.sleep(3000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "RECORD");
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRecordRightClickMenuIsDisplayed();
		Thread.sleep(3000);
		tasks.downloadRecord();
		Thread.sleep(5000);
		check.checkIfFileIsDownloaded("ArkCaseTesting");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddDocumentDeclareAsRecordEmailTheDocument()
			throws InterruptedException, IOException, AWTException {

		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("record document download");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		Thread.sleep(2000);
		tasks.performRightClickOnRoot();
		Thread.sleep(2000);
		tasks.verifyIfRightClickWorksOnRoot();
		tasks.clickNewDocument();
		Thread.sleep(2000);
		tasks.clickeDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "ACTIVE");
		Thread.sleep(3000);
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRightClickWorksOnFirstRow();
		tasks.clickDeclareAsRecord();
		Thread.sleep(3000);
		tasks.verifyFirstRowDocument("ArkCaseTesting.docx", "Edelivery", "Samuel Supervisor", "1.0", "RECORD");
		tasks.performRightClickOnFirstRow();
		Thread.sleep(3000);
		tasks.verifyIfRecordRightClickMenuIsDisplayed();
		Thread.sleep(3000);
		tasks.emailRecord();
		Thread.sleep(3000);
		tasks.verifyEmailPopUpTitle();
		tasks.searchUserEmailInput("Samuel Supervisor");
		Thread.sleep(3000);
		tasks.clickSearchEmailBtn();
		Thread.sleep(3000);
		tasks.verifySearchedUserEmail("Samuel Supervisor", "samuel-acm@armedia.com");
		tasks.searchedEmailUser.click();
		Thread.sleep(3000);
		tasks.clickSendEmailBtn();
		Thread.sleep(5000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

}
