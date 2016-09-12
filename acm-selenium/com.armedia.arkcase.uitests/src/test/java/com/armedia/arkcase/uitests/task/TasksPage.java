package com.armedia.arkcase.uitests.task;

import java.awt.AWTException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.base.WaitHelper;

public class TasksPage extends ArkCaseTestBase {

	// TaskPage taskf = PageFactory.initElements(driver, TaskPage.class);
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/input") WebElement serachInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/span/button")
	WebElement serachButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/span/span[3]")
	public
	WebElement taskList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/span/span[1]")
	WebElement expandTask;
	// Details Section
	@FindBy(how = How.XPATH, using = "//li/ul/li/span/span[3]]")
	WebElement detailsTaskList;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/span")
	WebElement taskDetailsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/div/button")
	WebElement detailsSaveButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[3]/div[4]")
	WebElement taskdetailsPanel;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[1]/div/button")
	WebElement detailsStyle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[2]/button[1]")
	WebElement detailsBold;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[2]/button[2]")
	WebElement detailsItalic;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[2]/button[3]")
	WebElement detailsUnderline;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[2]/button[4]")
	WebElement detailsRemoveFontStle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[3]/div/button")
	WebElement detailsFontFamily;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[4]/div/button")
	WebElement detailsFontSize;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[5]/button")
	WebElement detailsRecentColor;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[5]/div/button")
	WebElement detailsMoreColor;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[6]/button[1]")
	WebElement detailsUnorderList;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[6]/button[2]")
	WebElement detailsOrderList;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[6]/div/button")
	WebElement detailsParagraph;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[7]/div/button")
	WebElement detailsLineHeight;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[8]/div/button")
	WebElement detailsTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[9]/button[3]")
	WebElement detailsHorizontalLine;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[10]/button[1]")
	WebElement detailsFullScreen;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[10]/button[2]")
	WebElement detailsCodeView;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[11]/button")
	WebElement detailsHelp;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[9]/button[1]")
	WebElement detailsInsertLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[2]/div[1]/input")
	WebElement detailsLinkTextToDisplay;

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[2]/div[2]/input")
	WebElement detailsLinkUrl;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[3]/button")
	WebElement detailsLinkInsertButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[2]/div[9]/button[2]")
	WebElement detailsPicture;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[2]/div[1]/input")
	WebElement detailsPictureBrowse;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[3]/button")
	WebElement detailsInsertImageButton;

	// Reject Comment Section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/ul/li[2]/span/span[2]")
	WebElement rejectCommentsList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[1]/div/span")
	WebElement rejectCommentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div")
	WebElement rejectTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement commentColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement rejectCreatedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement rejectedAuthorColumn;
	// Attachment Section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/ul/li[3]/span/span[3]")
	WebElement attachmentsList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/span")
	WebElement attachmentsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/div/button")
	WebElement attachmentsRefreshButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table")
	WebElement attachmentTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[1]/input")
	WebElement attachmentCheckBox;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[3]")
	WebElement attachmentTitleColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[4]")
	WebElement attachmentTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[5]")
	WebElement attachmentCreatedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[6]")
	WebElement attachmentModifiedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[7]")
	WebElement attachmentAuthorColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[8]")
	WebElement attachmentVersionColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[9]")
	WebElement attachmentStatusColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody")
	WebElement attachmentUploadRow;
	// Notes Section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/ul/li[4]/span/span[3]")
	WebElement notesList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[1]/div/span")
	WebElement notesHeaderTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]")
	WebElement noterow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement noteColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement notesCreatedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement notesAuthorColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[1]/div/div/button")
	public WebElement addNewNoteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[1]/h3/span")
	WebElement tasksTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[1]/span")
	WebElement notesPopUpTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/textarea")
	WebElement newNoteInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[1]")
	WebElement newNoteCancelButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement newNoteSubmit;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement addedNoteNote;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement addedNoteCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement addedNoteAuthor;
	@FindBy(how = How.XPATH, using = " /html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/a[1]/i")
	WebElement editNoteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editNoteTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/textarea")
	WebElement editNoteInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement editNoteSaveButton;
	@FindBy(how = How.XPATH, using = "  /html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/a[2]")
	WebElement deleteAddedNote;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]")
	WebElement verifyDeletedNote;
	// Workflow section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[5]/span/span[3]")
	WebElement workOverviewList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/span")
	WebElement workTitleHeader;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]")
	WebElement workOverviewTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement workParticipantColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement workRoleColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]")
	WebElement workStatusColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement workStartColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[5]/div[2]/div[1]/span[1]")
	WebElement workEndColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement workParticipantData;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement workStatusData;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement workStartData;
	// History section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[6]/span/span[3]")
	WebElement historyList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/span")
	WebElement historyTitleHeader;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]")
	WebElement historyTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement histortEventName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]")
	WebElement historyDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement historyUser;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]")
	WebElement historyTableData;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[1]/div")
	WebElement historyEventNameValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[2]/div")
	WebElement historyDateValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/div")
	WebElement historyUserValue;
	// eSignature Section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/ul/li[7]/span/span[3]")
	WebElement eSignaturesList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/span")
	WebElement eSignatureHeaderTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]")
	WebElement eSignatureTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement eSignatureDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement eSignatureSignBy;
	// Tags
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/ul/li[8]/span/span[3]")
	WebElement tagsList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/span")
	WebElement tagsHeaderTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]")
	WebElement tagsTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement tagsNameColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement tagsCreatedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement tagsCreatedColumnBy;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[1]/div/div/button")
	WebElement addNewTagButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3/span")
	WebElement tagPopUpTitle;
	@FindBy(how = How.XPATH, using = "(//input[@type='text'])[6]")
	WebElement newTagInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement addTagButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/tags-input/div/auto-complete/div/ul/li[1]/ti-autocomplete-match/ng-include/span")
	WebElement searchedTag;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement addedTagName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement addedTagDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement addedTagUser;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/a")
	WebElement deleteAddedTag;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]")
	WebElement verifyDeletedRow;
	// Component links
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[1]/a")
	WebElement overviewLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[2]/a")
	WebElement detailsLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[3]/a")
	WebElement rejectCommentLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[4]/a")
	WebElement attachmentsLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[5]/a")
	WebElement notesLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[6]/a")
	WebElement workflowLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[7]/a")
	WebElement historyLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[8]/a")
	WebElement esignatureLink;
	@FindBy(how = How.XPATH, using = "//div[5]/ul/li[9]/a")
	WebElement tagsLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[5]")
	WebElement colapseLinks;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[5]/a/i")
	WebElement expandLinks;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[3]/div[4]/p/a")
	WebElement insertedLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[3]/div[2]/div[1]/div[2]/div/button[2]")
	WebElement unlink;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[3]/div[4]/p/img")
	WebElement insertedImage;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[3]/div[2]/div[2]/div[2]/div[4]/button")
	WebElement removeInsertedImage;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div[2]/div[3]/div[2]/div[1]/div[2]/div/button[1]")
	WebElement editInsertedLInk;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[1]")
	WebElement signButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[2]")
	WebElement subscribeButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[4]")
	public
	WebElement deleteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[5]")
	public WebElement completeButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[7]")
	public WebElement refreshButton;
	// subject
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/h4/a")
	WebElement subject;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/h4/form/div/input")
	WebElement subjectInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/h4/form/div/span/button[1]")
	WebElement subjectConfirm;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/h4/form/div/span/button[2]")
	WebElement subjectCancel;
	// percent of completion
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[1]/div/a")
	WebElement percentOfCompletition;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[1]/div/form/div/input")
	WebElement percentInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[1]/div/form/div/span/button[1]")
	WebElement percentConfirm;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[1]/div/form/div/span/button[2]")
	WebElement percentCancel;
	// Start Date
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[4]/div/a")
	WebElement startDate;
	@FindBy(how = How.XPATH, using = " /html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[4]/div/form/div/div[1]/input")
	WebElement startDateInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[4]/div/form/div/span/button[2]")
	WebElement startDateCancel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[4]/div/form/div/span/button[1]")
	WebElement startDateConfirm;
	// DueDate
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[5]/div/a")
	WebElement dueDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[5]/div/form/div/div[1]/input")
	WebElement dueDateInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[5]/div/form/div/span/button[2]")
	WebElement dueDateCancel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[5]/div/form/div/span/button[1]")
	WebElement dueDateConfirm;
	// Assignee
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/a")
	public WebElement assignee;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/form/div/span/button[2]")
	WebElement assigneeCancel;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/form/div/span/button[1]")
	public WebElement assigneeConfirm;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/form/div/select")
	WebElement selectAssignee;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/form/div/select/option[4]")
	WebElement selectBillThomas;
	// Priority
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[3]/div/a")
	public WebElement priority;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[3]/div/form/div/span/button[2]")
	WebElement priorityCancelButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[3]/div/form/div/span/button[1]")
	public WebElement priorityConfirmButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[3]")
	WebElement unsucribeButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[6]/div")
	public WebElement stateTask;
	// Attachment new Document
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr/td[3]/span/span[3]")
	WebElement root;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement newDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[7]")
	WebElement otherDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]")
	WebElement addedNewOther;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[4]")
	WebElement typeDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[5]")
	WebElement createdDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]")
	WebElement authorDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[7]/span/select")
	WebElement versiondocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]")
	WebElement statusDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[13]")
	WebElement renameDocument;
	@FindBy(how = How.XPATH, using = "  /html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[1]/td[3]/span/span[1]")
	WebElement expander;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span/span[3]/input")
	WebElement renameD;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[17]")
	WebElement replaceDocment;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]/span")
	WebElement secondVersion;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[18]")
	WebElement declareAsRecord;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]")
	WebElement email;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3")
	WebElement emailTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[1]/div/span/button")
	WebElement searchEmail;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[1]/div/input")
	WebElement emailInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement selectRecipient;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement searchedEmailUser;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement searchedEmail;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/textarea")
	WebElement emailRecipients;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement sendEmail;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[14]")
	WebElement deleteDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[1]")
	WebElement newFolder;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]/input")
	WebElement newFolderName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]")
	WebElement newFolderCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]/input")
	WebElement secondNewFolder;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]")
	WebElement secondFolderCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[2]")
	WebElement addedDocumentToFirstFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[10]")
	WebElement copyDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span/span[1]")
	WebElement expanderFirstFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[7]")
	WebElement pasteDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[2]/span/span[1]")
	WebElement expanderSecondFolder;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[2]/span")
	WebElement secondFolderDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[13]")
	WebElement renamedDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]")
	WebElement cutDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[6]")
	WebElement witnessInterview;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[5]")
	WebElement noticeOfInvestigation;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[4]")
	WebElement sfSignature;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[3]")
	WebElement eDelivery;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[2]")
	WebElement generalRelease;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[1]")
	WebElement medicalRelease;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[16]")
	WebElement downloadDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[4]")
	WebElement deleteTask;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement workflowStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement worFlowParticipant;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement workFLowStart;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement workFlowEnd;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[8]")
	WebElement renameFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]")
	WebElement deleteFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[10]")
	WebElement deleteDocInFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[10]")
	WebElement deleteDocInFirstFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]")
	WebElement deleteLastFile;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[2]/label/select")
	WebElement parentTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[2]/div[2]/label/select/option[2]")
	WebElement associateWithCAase;
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[2]/button") WebElement refreshButtonInTaskList;
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]") WebElement emptyTaskTable;

	// new attachment
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]")
	WebElement firstRowDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[4]")
	WebElement firstRowType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[5]")
	WebElement firstRowCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]")
	WebElement firstRowModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[7]")
	WebElement firstRowAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]")
	WebElement firstRowVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[9]")
	WebElement firstRowStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span")
	WebElement secondRowDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[4]")
	WebElement secondRowType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[5]")
	WebElement secondRowCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[6]")
	WebElement secondRowModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[7]")
	WebElement secondRowAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[8]")
	WebElement secondRowVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[9]")
	WebElement secondRowStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[3]/span/span[3]")
	WebElement thirdRowDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[4]")
	WebElement thirdRowType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[5]")
	WebElement thirdRowCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[6]")
	WebElement thirdRowModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[7]")
	WebElement thirdRowAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[8]")
	WebElement thirdRowVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[9]")
	WebElement thirdRowStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[3]/span/span[3]")
	WebElement forthRowDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[4]")
	WebElement forthRowType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[5]")
	WebElement forthRowCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[6]")
	WebElement forthRowModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[7]")
	WebElement forthRowAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[8]")
	WebElement forthRowVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[5]/td[9]")
	WebElement forthRowStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]/input")
	WebElement firtsRowInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]/input")
	WebElement secondRowInput;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[5]")
	WebElement checkOutDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span")
	WebElement lockedIcon;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[7]")
	WebElement cancelEditing;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement editWithWord;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]/span/select/option[2]")
	WebElement firstRowVersion2;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[6]")
	WebElement checkInDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[6]")
	WebElement recordDownload;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement recordEmail;

	public TasksPage taskSearchBox(String name) {

		Assert.assertTrue(serachInput.isDisplayed());
		Assert.assertTrue(serachInput.isEnabled());
		serachInput.click();
		serachInput.sendKeys(name);
		return this;
	}

	public TasksPage expandTask() {
		Assert.assertTrue(tasksTitle.getText().equals("Tasks"));
		taskList.click();
		expandTask.click();
		return this;
	}

	public TasksPage detailTasksList() {
		detailsTaskList.click();
		return this;
	}

	public TasksPage verifyDetailsTasksSection() {

		Assert.assertTrue(taskDetailsTitle.getText().equals("Task Details"));
		Assert.assertTrue(detailsSaveButton.isDisplayed());
		Assert.assertTrue(detailsSaveButton.isEnabled());

		Assert.assertTrue(detailsStyle.isDisplayed());
		Assert.assertTrue(detailsStyle.isEnabled());
		Assert.assertTrue(detailsStyle.isDisplayed());
		Assert.assertTrue(detailsBold.isEnabled());
		Assert.assertTrue(detailsBold.isDisplayed());
		Assert.assertTrue(detailsItalic.isEnabled());
		Assert.assertTrue(detailsItalic.isDisplayed());
		Assert.assertTrue(detailsUnderline.isEnabled());
		Assert.assertTrue(detailsUnderline.isDisplayed());
		Assert.assertTrue(detailsRemoveFontStle.isEnabled());
		Assert.assertTrue(detailsRemoveFontStle.isDisplayed());
		Assert.assertTrue(detailsFontFamily.isEnabled());
		Assert.assertTrue(detailsFontFamily.isDisplayed());
		Assert.assertTrue(detailsFontSize.isEnabled());
		Assert.assertTrue(detailsFontSize.isDisplayed());
		Assert.assertTrue(detailsRecentColor.isEnabled());
		Assert.assertTrue(detailsRecentColor.isDisplayed());
		Assert.assertTrue(detailsMoreColor.isEnabled());
		Assert.assertTrue(detailsMoreColor.isDisplayed());
		Assert.assertTrue(detailsUnorderList.isEnabled());
		Assert.assertTrue(detailsUnorderList.isDisplayed());
		Assert.assertTrue(detailsOrderList.isEnabled());
		Assert.assertTrue(detailsOrderList.isDisplayed());
		Assert.assertTrue(detailsParagraph.isEnabled());
		Assert.assertTrue(detailsParagraph.isDisplayed());
		Assert.assertTrue(detailsLineHeight.isEnabled());
		Assert.assertTrue(detailsLineHeight.isDisplayed());
		Assert.assertTrue(detailsTable.isEnabled());
		Assert.assertTrue(detailsTable.isDisplayed());
		Assert.assertTrue(detailsInsertLink.isEnabled());
		Assert.assertTrue(detailsInsertLink.isDisplayed());
		Assert.assertTrue(detailsPicture.isEnabled());
		Assert.assertTrue(detailsPicture.isDisplayed());
		Assert.assertTrue(detailsHorizontalLine.isEnabled());
		Assert.assertTrue(detailsHorizontalLine.isDisplayed());
		Assert.assertTrue(detailsFullScreen.isEnabled());
		Assert.assertTrue(detailsFullScreen.isDisplayed());
		Assert.assertTrue(detailsCodeView.isEnabled());
		Assert.assertTrue(detailsCodeView.isDisplayed());
		Assert.assertTrue(detailsHelp.isEnabled());
		Assert.assertTrue(detailsHelp.isDisplayed());
		return this;

	}

	public TasksPage rejectComentList() {

		rejectCommentsList.click();
		return this;

	}

	/*
	 * public void verifyRejectCommnetsTable() {
	 * 
	 * Assert.assertTrue(rejectCommentTitle.getText().equals("Reject Comments"
	 * )); Assert.assertTrue(rejectTable.isEnabled());
	 * Assert.assertTrue(rejectTable.isDisplayed());
	 * Assert.assertTrue(commentColumn.getText().equals("Comment"));
	 * Assert.assertTrue(rejectCreatedColumn.getText().equals("Created"));
	 * Assert.assertTrue(rejectedAuthorColumn.getText().equals("Author"));
	 * 
	 * }
	 */

	public TasksPage attachmentsList() {

		attachmentsList.click();
		return this;
	}

	public TasksPage verifyAttachmentTable() {

		Assert.assertTrue(attachmentsTitle.getText().equals("Attachments"));
		Assert.assertTrue(attachmentsRefreshButton.isDisplayed());
		Assert.assertTrue(attachmentsRefreshButton.isEnabled());
		attachmentsRefreshButton.click();
		Assert.assertTrue(attachmentTable.isDisplayed());
		Assert.assertTrue(attachmentTable.isEnabled());
		Assert.assertTrue(attachmentCheckBox.isDisplayed());
		Assert.assertTrue(attachmentCheckBox.isEnabled());
		attachmentCheckBox.click();
		Assert.assertTrue(attachmentTitleColumn.getText().equals("Title"));
		Assert.assertTrue(attachmentTypeColumn.getText().equals("Type"));
		Assert.assertTrue(attachmentCreatedColumn.getText().equals("Created"));
		Assert.assertEquals("Author column name is wrong", "Author", attachmentAuthorColumn.getText());
		Assert.assertEquals("Modified column name is wrong", "Modified", attachmentModifiedColumn.getText());
		Assert.assertTrue(attachmentAuthorColumn.getText().equals("Author"));
		Assert.assertTrue(attachmentVersionColumn.getText().equals("Version"));
		Assert.assertTrue(attachmentStatusColumn.getText().equals("Status"));
		Assert.assertTrue(attachmentUploadRow.isDisplayed());
		Assert.assertTrue(attachmentUploadRow.isEnabled());
		attachmentCheckBox.click();
		return this;
	}

	public TasksPage noteListClick() {
		notesList.click();
		return this;
	}

	public TasksPage verifyNotesTable() throws InterruptedException, IOException {

		Assert.assertTrue(notesHeaderTitle.getText().equals("Notes"));
		Assert.assertTrue(noteColumn.getText().equals("Note"));
		Assert.assertTrue(notesCreatedColumn.getText().equals("Created"));
		Assert.assertTrue(notesAuthorColumn.getText().equals("Author"));
		Assert.assertTrue(addNewNoteButton.isDisplayed());
		Assert.assertTrue(addNewNoteButton.isEnabled());
		return this;

	}

	public TasksPage addNewNote(String note) throws InterruptedException {
		addNewNoteButton.click();
		Thread.sleep(2000);

		Assert.assertTrue(notesPopUpTitle.getText().equals("Add Note"));
		Assert.assertTrue(newNoteInput.isDisplayed());
		Assert.assertTrue(newNoteInput.isEnabled());
		Assert.assertTrue(newNoteSubmit.isDisplayed());
		Assert.assertFalse(newNoteSubmit.isEnabled());
		newNoteInput.click();
		newNoteInput.sendKeys(note);
		Assert.assertTrue(newNoteCancelButton.isDisplayed());
		Assert.assertTrue(newNoteCancelButton.isEnabled());
		newNoteSubmit.click();
		return this;

	}

	public TasksPage verifyAddedNote(String note, String Author) {

		Assert.assertTrue(addedNoteNote.getText().equals(note));
		Assert.assertTrue(addedNoteCreated.isDisplayed());
		Assert.assertFalse(addedNoteCreated.getText().isEmpty());
		Assert.assertTrue(addedNoteAuthor.isDisplayed());
		Assert.assertTrue(addedNoteAuthor.getText().equals(Author));
		return this;
	}

	public TasksPage editNote(String editnote) {

		Assert.assertTrue(editNoteButton.isDisplayed());
		Assert.assertTrue(editNoteButton.isEnabled());
		editNoteButton.click();
		Assert.assertTrue(editNoteTitle.getText().equals("Edit Record"));
		Assert.assertTrue(editNoteInput.isDisplayed());
		Assert.assertTrue(editNoteInput.isEnabled());
		editNoteInput.click();
		editNoteInput.clear();
		editNoteInput.sendKeys(editnote);
		Assert.assertTrue(editNoteSaveButton.isDisplayed());
		Assert.assertTrue(editNoteSaveButton.isEnabled());
		editNoteSaveButton.click();
		return this;
	}

	public TasksPage deleteNote() {

		Assert.assertTrue(deleteAddedNote.isDisplayed());
		Assert.assertTrue(deleteAddedNote.isEnabled());
		deleteAddedNote.click();
		return this;

	}

	public TasksPage workFlowListClick() {
		workOverviewList.click();
		return this;
	}

	public TasksPage verifyWorkOverView(String participant) {

		Assert.assertTrue(workTitleHeader.getText().equals("Workflow"));
		Assert.assertTrue(workOverviewTable.isDisplayed());
		Assert.assertTrue(workOverviewTable.isEnabled());
		Assert.assertTrue(workParticipantColumn.getText().equals("Participant"));
		Assert.assertTrue(workRoleColumn.getText().equals("Role"));
		Assert.assertTrue(workStatusColumn.getText().equals("Status"));
		Assert.assertTrue(workStartColumn.getText().equals("Start"));
		Assert.assertTrue(workEndColumn.getText().equals("End"));
		Assert.assertTrue(workParticipantData.getText().equals(participant));
		Assert.assertTrue(workStatusData.getText().equals("ACTIVE"));
		Assert.assertFalse(workStartData.getText().isEmpty());
		return this;
	}

	public TasksPage historyListClick() {
		historyList.click();
		return this;
	}

	public TasksPage verifyHistorySection() {

		Assert.assertTrue(historyTitleHeader.getText().equals("History"));
		Assert.assertTrue(historyTable.isEnabled());
		Assert.assertTrue(historyTable.isDisplayed());
		Assert.assertTrue(histortEventName.getText().equals("Event Name"));
		Assert.assertTrue(historyDate.getText().equals("Date"));
		Assert.assertTrue(historyUser.getText().equals("User"));
		Assert.assertFalse(historyTableData.getText().isEmpty());
		return this;
	}

	public TasksPage verifyHistoryTableData(String eventName, String user) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(historyEventNameValue.getText(), eventName, "History event name is wrong");
		softAssert.assertEquals(historyDateValue.getText(), createdDate, "History date is wrong");
		softAssert.assertEquals(historyUserValue.getText(), user, "History user is wrong");
		softAssert.assertAll();
		return this;

	}

	public TasksPage eSignaturesListClick() {
		eSignaturesList.click();
		return this;
	}

	public TasksPage verifyEsignature() {

		Assert.assertTrue(eSignatureHeaderTitle.getText().equals("eSignatures"));
		Assert.assertTrue(eSignatureTable.isDisplayed());
		Assert.assertTrue(eSignatureTable.isEnabled());
		Assert.assertTrue(eSignatureDate.getText().equals("Date"));
		Assert.assertTrue(eSignatureSignBy.getText().equals("Signed By"));
		return this;
	}

	public TasksPage tagsListClick() {
		tagsList.click();
		return this;
	}

	public TasksPage verifyTagsTable() {

		Assert.assertTrue(tagsHeaderTitle.getText().equals("Tags"));
		Assert.assertTrue(tagsTable.isDisplayed());
		Assert.assertTrue(tagsTable.isEnabled());
		Assert.assertTrue(tagsNameColumn.getText().equals("Tag"));
		Assert.assertTrue(tagsCreatedColumn.getText().equals("Created"));
		Assert.assertTrue(tagsCreatedColumnBy.getText().equals("Created By"));
		return this;
	}

	public TasksPage addNewTag(String tag) throws InterruptedException {

		Assert.assertTrue(addNewTagButton.isDisplayed());
		Assert.assertTrue(addNewTagButton.isEnabled());
		addNewTagButton.click();
		Assert.assertTrue(tagPopUpTitle.getText().equals("Tag"));
		newTagInput.click();
		Thread.sleep(2000);
		newTagInput.sendKeys(tag);

		tagPopUpTitle.click();
		Thread.sleep(2000);
		addTagButton.click();
		return this;
	}

	public TasksPage addSearchedNewTag(String name) throws InterruptedException {

		Assert.assertTrue(addNewTagButton.isDisplayed());
		Assert.assertTrue(addNewTagButton.isEnabled());
		addNewTagButton.click();
		Assert.assertTrue(tagPopUpTitle.getText().equals("Tag"));
		newTagInput.click();
		newTagInput.sendKeys(name);
		Thread.sleep(2000);
		searchedTag.click();
		Thread.sleep(2000);
		addTagButton.click();
		return this;
	}

	public TasksPage verifyAddedTag(String tag, String user) {

		Assert.assertEquals("Added tag name is wrong", tag, addedTagName.getText());
		Assert.assertFalse(addedTagDate.getText().isEmpty());
		Assert.assertTrue(addedTagUser.getText().equals(user));
		return this;

	}

	public TasksPage deleteAddedTag() {
		deleteAddedTag.click();
        return this;
	}

	public TasksPage overviewLinkClick() {
		Assert.assertTrue(overviewLink.isDisplayed());
		Assert.assertTrue(overviewLink.isEnabled());
		overviewLink.click();
		return this;
	}

	public TasksPage detailsLinkClick() {
		Assert.assertTrue(detailsLink.isDisplayed());
		Assert.assertTrue(detailsLink.isEnabled());
		detailsLink.click();
		return this;
	}

	public TasksPage rejectCommentLink() {
		Assert.assertTrue(rejectCommentLink.isDisplayed());
		Assert.assertTrue(rejectCommentLink.isEnabled());
		rejectCommentLink.click();
		return this;
	}

	public TasksPage attachmentLinkClick() {
		Assert.assertTrue(attachmentsLink.isDisplayed());
		Assert.assertTrue(attachmentsLink.isEnabled());
		attachmentsLink.click();
		return this;
	}

	public TasksPage notestLinkClick() {

		Assert.assertTrue(notesLink.isDisplayed());
		Assert.assertTrue(notesLink.isEnabled());
		notesLink.click();
		return this;
	}

	public TasksPage workflowLinkClick() {

		Assert.assertTrue(workflowLink.isDisplayed());
		Assert.assertTrue(workflowLink.isEnabled());
		workflowLink.click();
		return this;
	}

	public TasksPage historyLinkClick() {

		Assert.assertTrue(historyLink.isDisplayed());
		Assert.assertTrue(historyLink.isEnabled());
		historyLink.click();
		return this;

	}

	public TasksPage esignatureLinkClick() {

		Assert.assertTrue(esignatureLink.isDisplayed());
		Assert.assertTrue(esignatureLink.isEnabled());
		esignatureLink.click();
		return this;
	}

	public TasksPage tagsLinkClick() {

		Assert.assertTrue(tagsLink.isDisplayed());
		Assert.assertTrue(tagsLink.isEnabled());
		tagsLink.click();
		return this;
	}

	public TasksPage clearInsertedLInk() throws InterruptedException {

		insertedLink.click();
		Thread.sleep(2000);
		unlink.click();
		Thread.sleep(2000);
		taskdetailsPanel.clear();
		Thread.sleep(2000);
		detailsSaveButton.click();
		return this;
	}

	public TasksPage insertLink(String link, String url) {

		detailsInsertLink.click();
		detailsLinkTextToDisplay.click();
		detailsLinkTextToDisplay.clear();
		detailsLinkTextToDisplay.sendKeys(link);
		detailsLinkUrl.click();
		detailsLinkUrl.clear();
		detailsLinkUrl.sendKeys(url);
		detailsLinkInsertButton.click();
		return this;
	}

	public TasksPage editInsertedLInk() throws InterruptedException {

		insertedLink.click();
		Thread.sleep(2000);
		editInsertedLInk.click();
		Thread.sleep(2000);
		detailsLinkTextToDisplay.click();
		detailsLinkTextToDisplay.clear();
		detailsLinkTextToDisplay.sendKeys("ArkCase1");
		detailsLinkInsertButton.click();
		return this;
	}

	public TasksPage detailsInsertPicture() throws InterruptedException, IOException, AWTException {

		detailsPicture.click();
		Thread.sleep(2000);
		detailsPictureBrowse.click();
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(2000);
		return this;
	}

	public TasksPage detailsRemovePicture() throws InterruptedException {

		insertedImage.click();
		Thread.sleep(2000);
		removeInsertedImage.click();
		detailsSaveButton.click();
		return this;
	}

	public TasksPage editSubjectClickCncel() throws InterruptedException {

		String verify = subject.getText();
		subject.click();
		subjectInput.click();
		subjectCancel.click();
		Thread.sleep(2000);
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(subject.getText().equals(verify));
		return this;
	}

	public TasksPage editSubjectClickConfirm(String subjectText) {

		subject.click();
		subjectInput.click();
		subjectInput.clear();
		subjectInput.sendKeys(subjectText);
		subjectConfirm.click();
		refreshButton.click();
		Assert.assertTrue(subjectText.equals(subject.getText()));
		return this;
	}

	public TasksPage editPercentClickCancel() {

		String percentText = percentOfCompletition.getText();
		percentOfCompletition.click();
		percentInput.click();
		percentCancel.click();
		refreshButton.click();
		Assert.assertTrue(percentText.equals(percentOfCompletition.getText()));
		return this;
	}

	public TasksPage editPercentClickConfirm(String percent) throws InterruptedException {

		percentOfCompletition.click();
		percentInput.click();
		percentInput.clear();
		percentInput.sendKeys(percent);
		percentConfirm.click();
		Thread.sleep(2000);
		refreshButton.click();
		Thread.sleep(3000);
		Assert.assertTrue(percentOfCompletition.getText().equals(percent));
		return this;
	}

	public TasksPage editStartDateClickCancel() throws InterruptedException {

		startDate.click();
		startDateInput.click();
		startDateCancel.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertEquals("Start date is wrong", "03/17/2016", startDate.getText());
		return this;

	}

	public TasksPage editStartDateClickConfirm(String startDateText) throws InterruptedException {

		startDate.click();
		startDateInput.click();
		startDateInput.clear();
		startDateInput.sendKeys(startDateText);
		startDateConfirm.click();
		refreshButton.click();
		Thread.sleep(3000);
		Assert.assertEquals("Start date is wrong", startDateText, startDate.getText());
        return this;
	}

	public TasksPage editDueDateClickCancel() throws InterruptedException {

		dueDate.click();
		dueDateInput.click();
		dueDateCancel.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(dueDate.getText().equals("03/19/2016"));
		return this;
	}

	public TasksPage editDueDateClickConfirm(String dueDateText) throws InterruptedException {

		dueDate.click();
		dueDateInput.click();
		dueDateInput.clear();
		dueDateInput.sendKeys(dueDateText);
		dueDateConfirm.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(dueDate.getText().equals(dueDateText));
		return this;
	}

	public TasksPage editAssigneeCancelButton() throws InterruptedException {

		String assigneText = assignee.getText();
		assignee.click();
		assigneeCancel.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(assignee.getText().equals(assigneText));
		return this;
	}

	public TasksPage editAssigneeCheckButtonsIfDisabled() throws InterruptedException {

		Assert.assertTrue(refreshButton.isDisplayed());
		Assert.assertTrue(refreshButton.isEnabled());
		Assert.assertFalse(completeButton.isDisplayed());
		Assert.assertFalse(deleteButton.isDisplayed());
		Assert.assertTrue(subscribeButton.isDisplayed());
		return this;
	}

	public TasksPage editAssigneeCheckButtonsIfEnabled() throws InterruptedException {

		Assert.assertTrue(refreshButton.isDisplayed());
		Assert.assertTrue(refreshButton.isEnabled());
		Assert.assertTrue(completeButton.isDisplayed());
		Assert.assertTrue(deleteButton.isDisplayed());
		Assert.assertTrue(subscribeButton.isDisplayed());
		return this;
	}

	public TasksPage EditPriorityCancelButton() {

		String priorityText = priority.getText();
		priority.click();
		priorityCancelButton.click();
		Assert.assertTrue(priorityText.equals(priority.getText()));
		return this;
	}

	public TasksPage SubscribeButton() throws InterruptedException {

		refreshButton.click();
		Thread.sleep(2000);
		subscribeButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(unsucribeButton.getText().equals("Unsubscribe"));
		refreshButton.click();
		Thread.sleep(2000);
		unsucribeButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(subscribeButton.getText().equals("Subscribe"));
		return this;
	}

	public TasksPage completeButtonClick() throws InterruptedException {

		refreshButton.click();
		Assert.assertEquals("Task State should be ACTIVE", "ACTIVE", stateTask.getText());
		completeButton.click();
		Thread.sleep(4000);
		Assert.assertEquals("Task State should be CLOSED", "CLOSED", stateTask.getText());
		return this;
	}

	public void workFlowData(String participant) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(worFlowParticipant.getText(), participant,
				"Workflow participan name is wrong or does not exist");
		softAssert.assertEquals(workflowStatus.getText(), "ACTIVE", "Worklfow status is wrong");
		softAssert.assertEquals(workFLowStart.getText(), createdDate, "Workflow start date is wrong");
		softAssert.assertEquals(workFlowEnd.getText(), "", "Workflow end date is wrong");
		softAssert.assertAll();		
	}

	public void verifyWorkflowTableStatusClosed(String participant) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(worFlowParticipant.getText(), participant,
				"Workflow participan name is wrong or does not exist");
		softAssert.assertEquals(workflowStatus.getText(), "CLOSED", "Worklfow status is wrong");
		softAssert.assertEquals(workFLowStart.getText(), createdDate, "Workflow start date is wrong");
		softAssert.assertEquals(workFlowEnd.getText(), createdDate, "Workflow end date is wrong");
		softAssert.assertAll();
   	}

	public void verifyAddDocumentAsignOtherUser() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Other user is sigh in, can not be added document", i == 0);
        
	}

	public TasksPage verifyAddingDocumentIfTaskIsClosed() throws InterruptedException {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(root).build();
		action.perform();
		Thread.sleep(2000);
		newDocument.click();
		Assert.assertFalse("Adding new document should not be enabled, task state is CLOSED",
				otherDocument.isDisplayed());
        return this;
	}

	public void verifyAddingNoteIfDifrentUserAsignTo() {

		Assert.assertTrue("Note is added, it shouldnt", noterow.getText().isEmpty());        
	}

	public TasksPage detailsSaveButtonClick() {
		detailsSaveButton.click();
		return this;
	}

	public void verifyIfTagIsDeleted() {

		Assert.assertTrue("The tag is not deleted", verifyDeletedRow.getText().isEmpty());

	}

	public void verifyInsertedLinkDetails(String link) {

		Assert.assertEquals("The inserted link is wrong or not exesting", link, insertedLink.getText());

	}

	public void verifyDetailsPanelIsEmpty() {

		Assert.assertTrue("Details panel is not empty", taskdetailsPanel.getText().isEmpty());

	}

	public void verifyIfNoteIsDeleted() {

		Assert.assertTrue("Added note is not deleted", verifyDeletedNote.getText().isEmpty());

	}

	public void createFolder(String name) throws InterruptedException {

		Thread.sleep(2000);
	}

	public TasksPage performRightClickOnRoot() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(root).build();
		action.perform();
		return this;

	}

	public void verifyIfRightClickWorksOnRoot() {

		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on root is not workin", i != 0);

	}

	public TasksPage performRightClickOnFirstRow() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(firstRowDocumentTitle).build();
		action.perform();
		return this;

	}

	public void verifyIfRightClickWorksOnFirstRow() {

		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on first row is not working", i != 0);

	}

	public TasksPage performRightClickOnSecondRow() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(secondRowDocumentTitle).build();
		action.perform();
		return this;

	}

	public void verifyIfRightClickWorksOnSecondRow() {

		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on second row is not working", i != 0);

	}

	public TasksPage performRightClickOnThirdRow() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(thirdRowDocumentTitle).build();
		action.perform();
		return this;
	}

	public void verifyIfRightClickWorksOnThirdRow() {

		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on third row is not working", i != 0);
	}

	public TasksPage performRightClickOnForthRow() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(forthRowDocumentTitle).build();
		action.perform();
		return this;
	}

	public void verifyIfRightClickWorksOnForthRow() {

		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on forth row is not working", i != 0);

	}

	public TasksPage newFolderClick() {

		Assert.assertEquals("New folder label name is wrong", "New Folder", newFolder.getText());
		newFolder.click();
		return this;

	}

	public TasksPage nameTheFirstFolder(String name) {

		firtsRowInput.sendKeys(name);
        return this;
	}

	public void verifyFirstFolderName(String name) {

		Assert.assertEquals("First folder name is wrong", name, firstRowDocumentTitle.getText());
	}

	public TasksPage nameTheSecondFolder(String name) {

		secondRowInput.sendKeys(name);
		return this;
	}

	public void verifySecondFolderName(String name) {

		Assert.assertEquals("Second folder name is wrong", name, secondRowDocumentTitle.getText());
	}

	public void verifyNewDocumentMenu() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[2]/ul")).size();
		Assert.assertTrue("New document menu option is not displayed", i != 0);

	}

	public TasksPage clickNewDocument() {

		Assert.assertEquals("New Document label name is wrong", "New Document", newDocument.getText());
		newDocument.click();
		return this;

	}

	public TasksPage clickOtherDocument() {

		Assert.assertEquals("Other document name is wrong", "Other", otherDocument.getText());
		otherDocument.click();
		return this;
	}

	public void verifySecondRowDocument(String title, String type, String author, String version, String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(secondRowDocumentTitle.getText(), title, "Second row document title is wrong");
		softAssert.assertEquals(secondRowType.getText(), type, "Second row type name is wrong");
		softAssert.assertEquals(secondRowCreated.getText(), createdDate, "Second row created date is wrong");
		softAssert.assertEquals(secondRowModified.getText(), createdDate, "Second row modified date is wrong");
		softAssert.assertEquals(secondRowAuthor.getText(), author, "Second row author name is wrong");
		softAssert.assertEquals(secondRowVersion.getText(), version, "Second row version is wrong");
		softAssert.assertEquals(secondRowStatus.getText(), status, "Second row status is wrong");
		softAssert.assertAll();

	}

	public void verifyForthRowDocument(String title, String type, String author, String version, String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(forthRowDocumentTitle.getText(), title, "Forth row document title is wrong");
		softAssert.assertEquals(forthRowType.getText(), type, "Forth row type name is wrong");
		softAssert.assertEquals(forthRowCreated.getText(), createdDate, "Forth row created date is wrong");
		softAssert.assertEquals(forthRowModified.getText(), createdDate, "Forth row modified date is wrong");
		softAssert.assertEquals(forthRowAuthor.getText(), author, "Forth row author name is wrong");
		softAssert.assertEquals(forthRowVersion.getText(), version, "Forth row version is wrong");
		softAssert.assertEquals(forthRowStatus.getText(), status, "Forth row status is wrong");
		softAssert.assertAll();

	}

	public void verifyFirstRowDocument(String title, String type, String author, String version, String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(firstRowDocumentTitle.getText(), title, "First row document title is wrong");
		softAssert.assertEquals(firstRowType.getText(), type, "First row type name is wrong");
		softAssert.assertEquals(firstRowCreated.getText(), createdDate, "First row created date is wrong");
		softAssert.assertEquals(firstRowModified.getText(), createdDate, "First row modified date is wrong");
		softAssert.assertEquals(firstRowAuthor.getText(), author, "First row author name is wrong");
		softAssert.assertEquals(firstRowVersion.getText(), version, "First row version is wrong");
		softAssert.assertEquals(firstRowStatus.getText(), status, "First row status is wrong");
		softAssert.assertAll();

	}

	public TasksPage cutDocument() {

		Assert.assertEquals("Cut label name is wrong", "Cut", cutDocument.getText());
		cutDocument.click();
		return this;

	}

	public TasksPage pasteDocument() {

		Assert.assertTrue("Paste is not enabled", pasteDocument.isEnabled());
		Assert.assertEquals("Paste label name is wrong", "Paste", pasteDocument.getText());
		pasteDocument.click();
		return this;
	}

	public void verifyIfCutDocumentDisapierd() {

		Assert.assertEquals("Cut document has not dissapierd", "document2", secondRowDocumentTitle.getText());

	}

	public TasksPage copyDocument() {
		Assert.assertEquals("Copy label name is wrong", "Copy", copyDocument.getText());
		copyDocument.click();
		return this;
	}

	public TasksPage deleteDocument() {

		Assert.assertEquals("Delete document label name is wrong", "Delete", deleteDocument.getText());
		deleteDocument.click();
		return this;
	}

	public void verifyIfDocumentIsDeletedStateDelete() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("The document should not be deleted, state task is DELETE", i != 0);
	}

	public void verifyIfDocumentIfAddTaskStateIsClosed() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("The document should not be added, state task is CLOSED", i == 0);
	}

	public TasksPage renameDocument() {

		Assert.assertEquals("Rename document label name is wrong", "Rename", renameDocument.getText());
		renameDocument.click();
		return this;
	}

	public TasksPage renameFolder() {

		Assert.assertEquals("Rename folder  label name is wrong", "Rename", renameFolder.getText());
		renameFolder.click();
		return this;
	}

	public TasksPage deleteFolder() {
		Assert.assertEquals("Delete folder label name is wrong", "Delete", deleteFolder.getText());
		deleteFolder.click();
		return this;
	}

	public void verifyIfFolderIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("The folder is not deleted", i == 0);

	}

	public TasksPage clickeDelivery() {

		Assert.assertEquals("Edelivery label name is wrong", "eDelivery", eDelivery.getText());
		eDelivery.click();
		return this;
	}

	public void verifyAddedDocumentAfterRefresh() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("After refresh document is not there", i != 0);

	}

	public TasksPage clickSfSignature() {

		Assert.assertEquals("Sfsignature label name is wrong", "SF86 Signature", sfSignature.getText());
		sfSignature.click();
		return this;
	}

	public TasksPage clickNoticeOfInvestigation() {

		Assert.assertEquals("Notice of Investigation label name is wrong", "Notice of Investigation",
				noticeOfInvestigation.getText());
		noticeOfInvestigation.click();
		return this;
	}

	public TasksPage clickMedicalRelease() {

		Assert.assertEquals("Medical Release label name is wrong", "Medical Release", medicalRelease.getText());
		medicalRelease.click();
		return this;
	}

	public TasksPage clickGeneralRelease() {

		Assert.assertEquals("General Release label name is wrong", "General Release", generalRelease.getText());
		generalRelease.click();
		return this;
	}

	public TasksPage clickDeclareAsRecord() {

		Assert.assertEquals("Declare as recoerd label name is wrong", "Declare as Record(s)",
				declareAsRecord.getText());
		declareAsRecord.click();
		return this;
	}

	public TasksPage nameTheFirstDocument(String name) {

		firtsRowInput.sendKeys(name);
		return this;
	}

	public void verifyFirstRowDocumentAfterRefresh(String title, String type, String author, String version,
			String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(firstRowDocumentTitle.getText(), title,
				" After refresh the page,First row document title is wrong");
		softAssert.assertEquals(firstRowType.getText(), type, "After refresh the page,First row type name is wrong");
		softAssert.assertEquals(firstRowCreated.getText(), createdDate,
				"After refresh the page,First row created date is wrong");
		softAssert.assertEquals(firstRowModified.getText(), createdDate,
				"After refresh the page,First row modified date is wrong");
		softAssert.assertEquals(firstRowAuthor.getText(), author,
				"After refresh the page,First row author name is wrong");
		softAssert.assertEquals(firstRowVersion.getText(), version,
				"After refresh the page,First row version is wrong");
		softAssert.assertEquals(firstRowStatus.getText(), status, "After refresh the page,First row status is wrong");
		softAssert.assertAll();

	}

	public TasksPage replaceDocument() {

		Assert.assertEquals("Replace document label name is wrong", "Replace", replaceDocment.getText());
		replaceDocment.click();
		return this;
	}

	public TasksPage emailDocument() {

		Assert.assertEquals("Email label name is wrong", "Email", email.getText());
		email.click();
		return this;

	}

	public void verifyEmailPopUpTitle() {
		Assert.assertEquals("Email popup title is wrong", "Email", emailTitle.getText());
	}

	public TasksPage searchUserEmailInput(String user) {

		emailInput.click();
		emailInput.sendKeys(user);
		return this;
	}

	public TasksPage clickSearchEmailBtn() {
		searchEmail.click();
		return this;
	}

	public void verifySearchedUserEmail(String userName, String email) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedEmailUser.getText(), userName, "Searched user for email is wrong");
		softAssert.assertEquals(searchedEmail.getText(), email, "Searched email is wrong");
		softAssert.assertAll();

	}

	public TasksPage clickSendEmailBtn() {
		sendEmail.click();
		return this;
	}

	public TasksPage downlaodDocument() {

		Assert.assertEquals("Download document label name is wrong ", "Download", downloadDocument.getText());
		downloadDocument.click();
		return this;

	}

	public TasksPage checkOutDocument() {

		Assert.assertEquals("Checkout label name is wrong", "Checkout", checkOutDocument.getText());
		checkOutDocument.click();
		return this;
	}

	public void verifyLockedIcon() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();
		Assert.assertFalse("Locked icon is not displayed", i == 0);

	}

	public TasksPage cancelEditingDocument() {

		Assert.assertEquals("Cancel Editing label name is wrong", "Cancel Editing", cancelEditing.getText());
		cancelEditing.click();
		return this;
	}

	public void verifyIfLockedIconIsDissapierd() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();
		Assert.assertTrue("Locked icon should not be  displayed", i == 0);
	}

	public TasksPage editWithWord() {

		Assert.assertEquals("Edit with word label name is wrong", "Edit With Word", editWithWord.getText());
		editWithWord.click();
		return this;
	}

	public void verifyFirstRowDocumentModified(String title, String type, String author, String version,
			String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(firstRowDocumentTitle.getText(), title, "First row document title is wrong");
		softAssert.assertEquals(firstRowType.getText(), type, "First row type name is wrong");
		softAssert.assertEquals(firstRowCreated.getText(), createdDate, "First row created date is wrong");
		softAssert.assertEquals(firstRowModified.getText(), createdDate, "First row modified date is wrong");
		softAssert.assertEquals(firstRowAuthor.getText(), author, "First row author name is wrong");
		softAssert.assertEquals(firstRowVersion2.getText(), version, "First row version is wrong");
		softAssert.assertEquals(firstRowStatus.getText(), status, "First row status is wrong");
		softAssert.assertAll();

	}

	public void verifyIfDocumentIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("The document is not deleted, delete button is not working", i == 0);

	}

	public TasksPage downloadRecord() {

		Assert.assertEquals("Record download label name is wrong", "Download", recordDownload.getText());
		recordDownload.click();
		return this;
	}

	public void verifyIfRecordRightClickMenuIsDisplayed() {

		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Record right clik menu is not displayed", i != 0);

	}

	public TasksPage emailRecord() {
		Assert.assertEquals("Email record label name is wrong", "Email", recordEmail.getText());
		recordEmail.click();
		return this;
	}
	
	public TasksPage deleteTask() {
		WaitHelper.waitForElement(deleteButton, driver);
		deleteButton.click();
		return this;
	}

}
