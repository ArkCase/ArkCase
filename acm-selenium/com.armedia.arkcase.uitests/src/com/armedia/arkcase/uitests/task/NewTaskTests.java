package com.armedia.arkcase.uitests.task;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.CheckIfFileIsDownloaded;

public class NewTaskTests extends ArkCaseTestBase {
	
	TaskPage task = PageFactory.initElements(driver, TaskPage.class);
	TasksPage tasks = PageFactory.initElements(driver, TasksPage.class);
	CheckIfFileIsDownloaded check=new CheckIfFileIsDownloaded();

	@Test
	public void addNewTaskCheckForLabelsAndFilds() throws IOException, InterruptedException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
	public void createNewTaskStatusActive() throws IOException, InterruptedException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("sam");
		task.typeSubject("MilanActiveMIlan");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("20");
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
		task.verifyTaskVariables("Samuel Supervisor", "MilanActiveMIlan", "20", "Low", "03/18/2016", "03/19/2016");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskSelectFromCalendarStatusClosed() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTaskTwo");
		task.selectStartDateFromCalendar();
		WebElement selectFromStartCalendar = driver.findElement(By.xpath("(//button[@type='button'])[25]"));
		selectFromStartCalendar.click();
		task.selectStatusClosed();
		task.selectDuedateFromCalendar();
		WebElement selectFromDueCalendar = driver.findElement(By.xpath("(//button[@type='button'])[25]"));
		selectFromDueCalendar.click();
		Thread.sleep(2000);
		task.typeComplete("32");
		new Select(driver.findElement(
				By.xpath("/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")))
						.selectByVisibleText("Medium");
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		task.verifyTaskVariables("Samuel Supervisor", "AutomateTestTaskTwo", "32", "Medium", "03/17/2016",
				"03/16/2016");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTasksStatusInactive() throws IOException, InterruptedException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTaskThreeMilan");
		task.typeStartDate("03/18/2016");
		task.selectStatusInActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("53");
		new Select(driver.findElement(
				By.xpath("/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")))
						.selectByVisibleText("High");
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		task.verifyTaskVariables("Samuel Supervisor", "AutomateTestTaskThreeMilan", "53", "High", "03/18/2016",
				"03/19/2016");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskStatusActiveExedite() throws IOException, InterruptedException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.taskList.click();
		Thread.sleep(2000);
		task.verifyTaskVariables("Samuel Supervisor", "AutomateTestTaskFour", "53", "Expedite", "03/18/2016",
				"03/19/2016");
		Thread.sleep(3000);
		tasks.deleteButton.click();
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskStatusActiveBadPercent() throws InterruptedException, IOException {
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("sam");
		task.typeSubject("AutomateTestTask3");
		task.typeStartDate("03/18/2016");
		task.selectStatusInActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.typeComplete("101");
		new Select(driver.findElement(
				By.xpath("/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[4]/label/select")))
						.selectByVisibleText("Expedite");
		task.insertLink("ArkCase", "http://www.arkcase.com/");
		task.verifyLinkInNotes();
		Assert.assertFalse(task.saveButton.isEnabled());
		task.taskLogOut();
		Thread.sleep(5000);
	}

	@Test
	public void createNewTaskIfSubjectEmpty() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.typeSubject("");
		task.typeStartDate("03/18/2016");
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
	public void creteNewTaskIfStartDateIsEmpty() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		tasks.verifyRejectCommnetsTable();
		WebElement attachments = driver.findElement(By.xpath("//li[3]/span/span[3]"));
		Assert.assertTrue(attachments.getText().equals("Attachments"));
		attachments.click();
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
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskVerifyTaskLinks() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		tasks.verifyRejectCommnetsTable();
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
	public void createNewTaskFerifyandAddTaskDetails() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		// task.taskLogOut();
		ArkCaseAuthentication.logOut(driver);
		Thread.sleep(3000);
	}

	@Test
	public void createNewTaskFerifyAddNote() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		tasks.addNewTag("washington2");
		Thread.sleep(3000);
		tasks.verifyAddedTag("washington2", "samuel-acm");
		driver.navigate().refresh();
		Thread.sleep(8000);
		tasks.verifyAddedTag("washington2", "Samuel Supervisor");
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
	public void createNewTaskAddSearchedTag() throws InterruptedException, IOException {
		// create new task and add searched tag
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan6");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		Thread.sleep(2000);
		tasks.createNewFolder("document");
		Thread.sleep(3000);
		tasks.renameFolder("document1");
		Thread.sleep(4000);
		tasks.deleteFolder();
		Thread.sleep(2000);
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]"))
				.size();
		Assert.assertFalse(i != 0);
		Thread.sleep(3000);
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskCreateTwoFoldersCopyPaste() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan7");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		Thread.sleep(2000);
		tasks.createNewFolder("document1");
		Thread.sleep(2000);
		tasks.createSecondFolder("document2");
		Thread.sleep(3000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addDocumentToFirstFolder();
		tasks.copyDocumentFromFirstFolder();
		Thread.sleep(2000);
		tasks.pasteDocumentToSeconFolder();
		Thread.sleep(3000);
		tasks.deleteAddedFiles();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]"))
				.size();
		Assert.assertFalse(i != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskCreateTwoFoldersCutPaste() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan8");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		Thread.sleep(2000);
		tasks.createNewFolder("document1");
		Thread.sleep(2000);
		tasks.createSecondFolder("document2");
		Thread.sleep(3000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addDocumentToFirstFolder();
		tasks.cutDocumentFromFirstFolder();
		Thread.sleep(2000);
		tasks.pasteCuttedDocument();
		Thread.sleep(2000);
		tasks.deleteCutedFiles();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]"))
				.size();
		Assert.assertFalse(i != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentOtherSendEmail() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		tasks.verifyAttachmentTable();
		Thread.sleep(2000);
		tasks.addNewDcumentOther();
		Thread.sleep(2000);
		tasks.verifyDocumentAddedNew("Other", "ACTIVE");
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addedNewOther.click();
		tasks.sendEmail();
		Thread.sleep(2000);
		tasks.addedNewOther.click();
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewDocumentWitnessRequestPdf() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan10");
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
		tasks.verifyAttachmentTable();
		tasks.addNewDocumentWitness();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addedNewOther.click();
		tasks.verifyDocumentAddedNew("Witness Interview Request", "ACTIVE");
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.root.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewDocumentNoticeOfInvesstigation() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan11");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.addNewDocumentNoticeOfInvestigation();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addedNewOther.click();
		tasks.verifyDocumentAddedNew("Notice Of Investigation", "ACTIVE");
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.root.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentSfSignatureDocx() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan12");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.addNewDocumentSfSignature();
		Thread.sleep(4000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		Thread.sleep(4000);
		tasks.addedNewOther.click();
		Thread.sleep(2000);
		tasks.verifyDocumentAddedNew("Sf86 Signature", "ACTIVE");
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.root.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentEDelivery() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan13");
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
		tasks.verifyAttachmentTable();
		tasks.addNewDocumentEDelevery();
		Thread.sleep(4000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(2000);
		Thread.sleep(2000);
		tasks.addedNewOther.click();
		tasks.verifyDocumentAddedNew("Edelivery", "ACTIVE");
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.root.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskAddNewDocumentGeneralReleaseXlsx() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan14");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.addNewDocumentgeneralRelease();
		Thread.sleep(3000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.addedNewOther.click();
		tasks.verifyDocumentAddedNew("General Release", "ACTIVE");
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		tasks.root.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentMedicalReleaseDownload() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan15");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.addNewDocumentMedicalRelease();
		Thread.sleep(3000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		Thread.sleep(3000);
		tasks.addedNewOther.click();
		tasks.verifyDocumentAddedNew("Medical Release", "ACTIVE");
		Thread.sleep(2000);
		tasks.downloadDocument();
		Thread.sleep(2000);
		tasks.deleteDocument();
		Thread.sleep(4000);
		check.checkIfFileIsDownloaded("Medical Release");
		tasks.attachmentsRefreshButton.click();
		tasks.root.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentOtherRename() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan16");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.addNewDcumentOther();
		Thread.sleep(4000);
		tasks.verifyDocumentAddedNew("Other", "ACTIVE");
		Thread.sleep(2000);
		tasks.renameAddedNewDocument();
		Thread.sleep(2000);
		tasks.verifyDocumentAddedNew("Other", "ACTIVE");
		Thread.sleep(2000);
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentOtherReplace() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.typeSubject("TestMilan17");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addNewDcumentOther();
		Thread.sleep(2000);
		tasks.verifyDocumentAddedNew("Other", "ACTIVE");
		Thread.sleep(2000);
		tasks.addedNewOther.click();
		tasks.replaceDocument();
		Thread.sleep(4000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.createdDocument.click();
		tasks.verifyReplacedDocument("Other", "ACTIVE");
		Thread.sleep(2000);
		tasks.deleteDocument();
		Thread.sleep(2000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(2000);
		tasks.expander.click();
		int fail = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]"))
				.size();
		Assert.assertFalse(fail != 0);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskAddNewDocumentOtherDeclareAsRecord() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		tasks.verifyAttachmentTable();
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addNewDcumentOther();
		Thread.sleep(2000);
		tasks.createdDocument.click();
		Thread.sleep(2000);
		tasks.declareAsRecord();
		Thread.sleep(3000);
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.verifyRecordDocument("Other");
		Thread.sleep(2000);
		tasks.refreshButton.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskVerifyAndEditInformationRibbon() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
	public void createNewTaskVerifyIfCompleteTaskDoesNotShowInList() throws InterruptedException, IOException {
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
	public void createNewTaskVerifyIfDeletedIsStillShownInList() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
	public void createNewTaskCheckAddDocumentAsignOtherUser() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		Assert.assertTrue(tasks.assignee.getText().equals("Bill Thomas"));
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		Thread.sleep(2000);
		tasks.addNewDcumentOther();
		Thread.sleep(5000);
		tasks.attachmentsRefreshButton.click();
		Thread.sleep(2000);
		tasks.expander.click();
		tasks.verifyAddDocumentAsignOtherUser();
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckAceesDenideNotes() throws InterruptedException, IOException {
		// verify if note can be added if is assigned another user
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		Thread.sleep(2000);
		tasks.verifyAddingNoteIfDifrentUserAsignTo();
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckAccesDenideIftaskIsClosedAddNote() throws InterruptedException, IOException {
		// verify if note can be added is task status is closed
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("CLOSED"));
		tasks.notestLinkClick();
		Assert.assertFalse("Add note button is enabled", tasks.addNewNoteButton.isEnabled());
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCheckAccessDenideIfTaskIsClosedAddDocument() throws InterruptedException, IOException {
		// verify if document can be added if the task status is closed
		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("CLOSED"));
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.verifyAddingDocumentIfTaskIsClosed();
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewTaskCompleteFerifyWorkFlow() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
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
		Assert.assertTrue(tasks.stateTask.getText().equals("CLOSED"));
		driver.navigate().refresh();
		Thread.sleep(8000);
		tasks.verifyWorkflowTableStatusClosed("Samuel Supervisor");
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewTaskCompleteFerifyAccessDeniedDocumentDelete() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn("samuel-acm", "Armedia#1", driver, baseUrl);
		task.newTask();
		task.assignTo("Samuel Supervisor");
		task.typeSubject("TestMilan1");
		task.typeStartDate("03/18/2016");
		task.selectStatusActive();
		task.typeDuedate("03/19/2016");
		Thread.sleep(2000);
		task.saveButtonClick();
		Thread.sleep(5000);
		tasks.expandTask();
		Thread.sleep(2000);
		tasks.attachmentLinkClick();
		tasks.verifyAttachmentTable();
		tasks.attachmentsRefreshButton.click();
		tasks.expander.click();
		tasks.addNewDcumentOther();
		Thread.sleep(2000);
		tasks.createdDocument.click();
		tasks.deleteButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(tasks.stateTask.getText().equals("DELETE"));
		Thread.sleep(2000);
		tasks.createdDocument.click();
		tasks.deleteDocument();
		int accessDenide = driver.findElements(By.xpath("/html/body/div[4]/div[1]")).size();
		Assert.assertFalse(accessDenide == 0);
		Thread.sleep(2000);
		ArkCaseAuthentication.logOut(driver);
	}

}
