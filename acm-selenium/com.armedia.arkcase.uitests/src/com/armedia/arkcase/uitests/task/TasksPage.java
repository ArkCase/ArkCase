package com.armedia.arkcase.uitests.task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;

public class TasksPage extends ArkCaseTestBase {

	// TaskPage taskf = PageFactory.initElements(driver, TaskPage.class);
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/input") WebElement serachInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/span/button")
	WebElement serachButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li/span/span[3]")
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
	WebElement attachmentAuthorColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[7]")
	WebElement attachmentVersionColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/thead/tr/th[8]")
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
	WebElement deleteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[5]")
	WebElement completeButton;
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
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]")
	WebElement renameDocument;
	@FindBy(how = How.XPATH, using = "  /html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[1]/td[3]/span/span[1]")
	WebElement expander;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span/span[3]/input")
	WebElement renameD;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[13]")
	WebElement replaceDocment;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]/span")
	WebElement secondVersion;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[14]")
	WebElement declareAsRecord;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]")
	WebElement email;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h2")
	WebElement emailTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[1]/div/span/button")
	WebElement searchEmail;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[1]/div/input")
	WebElement emailInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement selectRecipient;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/textarea")
	WebElement emailRecipients;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement sendEmail;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[10]")
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
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[5]")
	WebElement copyDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span/span[1]")
	WebElement expanderFirstFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[7]")
	WebElement pasteDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[2]/span/span[1]")
	WebElement expanderSecondFolder;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[2]/span")
	WebElement secondFolderDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span/span[3]")
	WebElement renamedDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[4]")
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
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[12]")
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
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]/kbd")
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

	public void taskSearchBox(String name) {

		Assert.assertTrue(serachInput.isDisplayed());
		Assert.assertTrue(serachInput.isEnabled());
		serachInput.click();
		serachInput.sendKeys(name);
	}

	public void expandTask() {
		Assert.assertTrue(tasksTitle.getText().equals("Tasks"));
		taskList.click();
		expandTask.click();
	}

	public void detailTasksList() {
		detailsTaskList.click();
	}

	public void verifyDetailsTasksSection() {

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

	}

	public void rejectComentList() {

		rejectCommentsList.click();

	}

	public void verifyRejectCommnetsTable() {

		Assert.assertTrue(rejectCommentTitle.getText().equals("Reject Comments"));
		Assert.assertTrue(rejectTable.isEnabled());
		Assert.assertTrue(rejectTable.isDisplayed());
		Assert.assertTrue(commentColumn.getText().equals("Comment"));
		Assert.assertTrue(rejectCreatedColumn.getText().equals("Created"));
		Assert.assertTrue(rejectedAuthorColumn.getText().equals("Author"));

	}

	public void attachmentsList() {

		attachmentsList.click();
	}

	public void verifyAttachmentTable() {

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
		Assert.assertTrue(attachmentAuthorColumn.getText().equals("Author"));
		Assert.assertTrue(attachmentVersionColumn.getText().equals("Version"));
		Assert.assertTrue(attachmentStatusColumn.getText().equals("Status"));
		Assert.assertTrue(attachmentUploadRow.isDisplayed());
		Assert.assertTrue(attachmentUploadRow.isEnabled());
		attachmentCheckBox.click();
	}

	public void noteListClick() {
		notesList.click();
	}

	public void verifyNotesTable() throws InterruptedException, IOException {

		Assert.assertTrue(notesHeaderTitle.getText().equals("Notes"));
		Assert.assertTrue(noteColumn.getText().equals("Note"));
		Assert.assertTrue(notesCreatedColumn.getText().equals("Created"));
		Assert.assertTrue(notesAuthorColumn.getText().equals("Author"));
		Assert.assertTrue(addNewNoteButton.isDisplayed());
		Assert.assertTrue(addNewNoteButton.isEnabled());

	}

	public void addNewNote(String note) throws InterruptedException {
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

	}

	public void verifyAddedNote(String note, String Author) {

		Assert.assertTrue(addedNoteNote.getText().equals(note));
		Assert.assertTrue(addedNoteCreated.isDisplayed());
		Assert.assertFalse(addedNoteCreated.getText().isEmpty());
		Assert.assertTrue(addedNoteAuthor.isDisplayed());
		Assert.assertTrue(addedNoteAuthor.getText().equals(Author));
	}

	public void editNote(String editnote) {

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
	}

	public void deleteNote() {

		Assert.assertTrue(deleteAddedNote.isDisplayed());
		Assert.assertTrue(deleteAddedNote.isEnabled());
		deleteAddedNote.click();

	}

	public void workFlowListClick() {
		workOverviewList.click();
	}

	public void verifyWorkOverView(String participant) {

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
	}

	public void historyListClick() {
		historyList.click();
	}

	public void verifyHistorySection() {

		Assert.assertTrue(historyTitleHeader.getText().equals("History"));
		Assert.assertTrue(historyTable.isEnabled());
		Assert.assertTrue(historyTable.isDisplayed());
		Assert.assertTrue(histortEventName.getText().equals("Event Name"));
		Assert.assertTrue(historyDate.getText().equals("Date"));
		Assert.assertTrue(historyUser.getText().equals("User"));
		Assert.assertFalse(historyTableData.getText().isEmpty());
	}

	public void verifyHistoryTableData(String eventName, String user) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(historyEventNameValue.getText(), eventName, "History event name is wrong");
		softAssert.assertEquals(historyDateValue.getText(), createdDate, "History date is wrong");
		softAssert.assertEquals(historyUserValue.getText(), user, "History user is wrong");
		softAssert.assertAll();

	}

	public void eSignaturesListClick() {
		eSignaturesList.click();
	}

	public void verifyEsignature() {

		Assert.assertTrue(eSignatureHeaderTitle.getText().equals("eSignatures"));
		Assert.assertTrue(eSignatureTable.isDisplayed());
		Assert.assertTrue(eSignatureTable.isEnabled());
		Assert.assertTrue(eSignatureDate.getText().equals("Date"));
		Assert.assertTrue(eSignatureSignBy.getText().equals("Signed By"));
	}

	public void tagsListClick() {
		tagsList.click();
	}

	public void verifyTagsTable() {

		Assert.assertTrue(tagsHeaderTitle.getText().equals("Tags"));
		Assert.assertTrue(tagsTable.isDisplayed());
		Assert.assertTrue(tagsTable.isEnabled());
		Assert.assertTrue(tagsNameColumn.getText().equals("Tag"));
		Assert.assertTrue(tagsCreatedColumn.getText().equals("Created"));
		Assert.assertTrue(tagsCreatedColumnBy.getText().equals("Created By"));
	}

	public void addNewTag(String tag) throws InterruptedException {

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
	}

	public void addSearchedNewTag(String name) throws InterruptedException {

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
	}

	public void verifyAddedTag(String tag, String user) {

		Assert.assertEquals("Added tag name is wrong", tag, addedTagName.getText());
		Assert.assertFalse(addedTagDate.getText().isEmpty());
		Assert.assertTrue(addedTagUser.getText().equals(user));

	}

	public void deleteAddedTag() {

		deleteAddedTag.click();

	}

	public void overviewLinkClick() {

		Assert.assertTrue(overviewLink.isDisplayed());
		Assert.assertTrue(overviewLink.isEnabled());
		overviewLink.click();
	}

	public void detailsLinkClick() {

		Assert.assertTrue(detailsLink.isDisplayed());
		Assert.assertTrue(detailsLink.isEnabled());
		detailsLink.click();
	}

	public void rejectCommentLink() {

		Assert.assertTrue(rejectCommentLink.isDisplayed());
		Assert.assertTrue(rejectCommentLink.isEnabled());
		rejectCommentLink.click();
	}

	public void attachmentLinkClick() {

		Assert.assertTrue(attachmentsLink.isDisplayed());
		Assert.assertTrue(attachmentsLink.isEnabled());
		attachmentsLink.click();
	}

	public void notestLinkClick() {

		Assert.assertTrue(notesLink.isDisplayed());
		Assert.assertTrue(notesLink.isEnabled());
		notesLink.click();
	}

	public void workflowLinkClick() {

		Assert.assertTrue(workflowLink.isDisplayed());
		Assert.assertTrue(workflowLink.isEnabled());
		workflowLink.click();
	}

	public void historyLinkClick() {

		Assert.assertTrue(historyLink.isDisplayed());
		Assert.assertTrue(historyLink.isEnabled());
		historyLink.click();

	}

	public void esignatureLinkClick() {

		Assert.assertTrue(esignatureLink.isDisplayed());
		Assert.assertTrue(esignatureLink.isEnabled());
		esignatureLink.click();
	}

	public void tagsLinkClick() {

		Assert.assertTrue(tagsLink.isDisplayed());
		Assert.assertTrue(tagsLink.isEnabled());
		tagsLink.click();
	}

	public void clearInsertedLInk() throws InterruptedException {

		insertedLink.click();
		Thread.sleep(2000);
		unlink.click();
		Thread.sleep(2000);
		taskdetailsPanel.clear();
		Thread.sleep(2000);
		detailsSaveButton.click();
	}

	public void insertLink(String link, String url) {

		detailsInsertLink.click();
		detailsLinkTextToDisplay.click();
		detailsLinkTextToDisplay.clear();
		detailsLinkTextToDisplay.sendKeys(link);
		detailsLinkUrl.click();
		detailsLinkUrl.clear();
		detailsLinkUrl.sendKeys(url);
		detailsLinkInsertButton.click();
	}

	public void editInsertedLInk() throws InterruptedException {

		insertedLink.click();
		Thread.sleep(2000);
		editInsertedLInk.click();
		Thread.sleep(2000);
		detailsLinkTextToDisplay.click();
		detailsLinkTextToDisplay.clear();
		detailsLinkTextToDisplay.sendKeys("ArkCase1");
		detailsLinkInsertButton.click();
	}

	public void detailsInsertPicture() throws InterruptedException, IOException {

		detailsPicture.click();
		Thread.sleep(2000);
		detailsPictureBrowse.click();
		ArkCaseTestUtils.uploadPicture();
		detailsInsertImageButton.click();
		Thread.sleep(2000);
	}

	public void detailsRemovePicture() throws InterruptedException {

		insertedImage.click();
		Thread.sleep(2000);
		removeInsertedImage.click();
		detailsSaveButton.click();
	}

	public void editSubjectClickCncel() throws InterruptedException {

		String verify = subject.getText();
		subject.click();
		subjectInput.click();
		subjectCancel.click();
		Thread.sleep(2000);
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(subject.getText().equals(verify));

	}

	public void editSubjectClickConfirm(String subjectText) {

		subject.click();
		subjectInput.click();
		subjectInput.clear();
		subjectInput.sendKeys(subjectText);
		subjectConfirm.click();
		refreshButton.click();
		Assert.assertTrue(subjectText.equals(subject.getText()));
	}

	public void editPercentClickCancel() {

		String percentText = percentOfCompletition.getText();
		percentOfCompletition.click();
		percentInput.click();
		percentCancel.click();
		refreshButton.click();
		Assert.assertTrue(percentText.equals(percentOfCompletition.getText()));
	}

	public void editPercentClickConfirm(String percent) throws InterruptedException {

		percentOfCompletition.click();
		percentInput.click();
		percentInput.clear();
		percentInput.sendKeys(percent);
		percentConfirm.click();
		Thread.sleep(2000);
		refreshButton.click();
		Thread.sleep(3000);
		Assert.assertTrue(percentOfCompletition.getText().equals(percent));
	}

	public void editStartDateClickCancel() throws InterruptedException {

		startDate.click();
		startDateInput.click();
		startDateCancel.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertEquals("Start date is wrong", "03/17/2016", startDate.getText());

	}

	public void editStartDateClickConfirm(String startDateText) throws InterruptedException {

		startDate.click();
		startDateInput.click();
		startDateInput.clear();
		startDateInput.sendKeys(startDateText);
		startDateConfirm.click();
		refreshButton.click();
		Thread.sleep(3000);
		Assert.assertEquals("Start date is wrong", startDateText, startDate.getText());

	}

	public void editDueDateClickCancel() throws InterruptedException {

		dueDate.click();
		dueDateInput.click();
		dueDateCancel.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(dueDate.getText().equals("03/19/2016"));
	}

	public void editDueDateClickConfirm(String dueDateText) throws InterruptedException {

		dueDate.click();
		dueDateInput.click();
		dueDateInput.clear();
		dueDateInput.sendKeys(dueDateText);
		dueDateConfirm.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(dueDate.getText().equals(dueDateText));
	}

	public void editAssigneeCancelButton() throws InterruptedException {

		String assigneText = assignee.getText();
		assignee.click();
		assigneeCancel.click();
		refreshButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(assignee.getText().equals(assigneText));
	}

	public void editAssigneeCheckButtonsIfDisabled() throws InterruptedException {

		Assert.assertTrue(refreshButton.isDisplayed());
		Assert.assertTrue(refreshButton.isEnabled());
		Assert.assertFalse(completeButton.isDisplayed());
		Assert.assertFalse(deleteButton.isDisplayed());
		Assert.assertTrue(subscribeButton.isDisplayed());
	}

	public void editAssigneeCheckButtonsIfEnabled() throws InterruptedException {

		Assert.assertTrue(refreshButton.isDisplayed());
		Assert.assertTrue(refreshButton.isEnabled());
		Assert.assertTrue(completeButton.isDisplayed());
		Assert.assertTrue(deleteButton.isDisplayed());
		Assert.assertTrue(subscribeButton.isDisplayed());
	}

	public void EditPriorityCancelButton() {

		String priorityText = priority.getText();
		priority.click();
		priorityCancelButton.click();
		Assert.assertTrue(priorityText.equals(priority.getText()));
	}

	public void SubscribeButton() throws InterruptedException {

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
	}

	public void completeButtonClick() throws InterruptedException {

		refreshButton.click();
		Assert.assertEquals("Task State should be ACTIVE", "ACTIVE", stateTask.getText());
		completeButton.click();
		Thread.sleep(2000);
		Assert.assertEquals("Task State should be CLOSED", "CLOSED", stateTask.getText());
	}

	public void addNewDcumentOther() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("New document name is wrong or right click is not working", "New Document",
				newDocument.getText());
		newDocument.click();
		Thread.sleep(2000);
		otherDocument.click();
		ArkCaseTestUtils.uploadPicture();
		addedNewOther.click();

	}

	public void verifyDocumentAddedNew(String type, String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		Assert.assertTrue(typeDocument.getText().equals(type));
		Assert.assertTrue(createdDocument.getText().equals(createdDate));
		Assert.assertTrue(authorDocument.getText().equals(assignee.getText()));
		Assert.assertTrue(versiondocument.getText().equals("1.0"));
		Assert.assertTrue(statusDocument.getText().equals(status));
	}

	public void renameAddedNewDocument() throws InterruptedException {

		Actions act = new Actions(driver);
		act.contextClick(addedNewOther).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Rename document name is wrong or right click is not working", "Rename Document",
				renameDocument.getText());
		renameDocument.click();
		Thread.sleep(2000);
		renameD.click();
		renameD.clear();
		renameD.sendKeys("image.jpg");
		Thread.sleep(2000);
		tasksTitle.click();
		Assert.assertTrue(renamedDocument.getText().equals("image.jpg"));
		tasksTitle.click();

	}

	public void replaceDocument() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(addedNewOther).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Replace document name is wrong or right click is not working", "Replace Document",
				replaceDocment.getText());
		replaceDocment.click();
		ArkCaseTestUtils.uploadPicture();
		addedNewOther.click();
	}

	public void verifyReplacedDocument(String type, String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		Assert.assertTrue(typeDocument.getText().equals(type));
		Assert.assertTrue(createdDocument.getText().equals(createdDate));
		Assert.assertTrue(authorDocument.getText().equals(assignee.getText()));
		Assert.assertTrue(secondVersion.getText().contains("2.0"));
		Assert.assertTrue(statusDocument.getText().equals(status));

	}

	public void verifyRecordDocument(String type) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		Assert.assertTrue(typeDocument.getText().equals(type));
		Assert.assertTrue(createdDocument.getText().equals(createdDate));
		Assert.assertTrue(authorDocument.getText().equals(assignee.getText()));
		Assert.assertTrue(statusDocument.getText().equals("RECORD"));

	}

	public void declareAsRecord() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(addedNewOther).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Declare as Record name is wrong or right click is not working", "Declare as Record(s)",
				declareAsRecord.getText());
		declareAsRecord.click();
	}

	public void sendEmail() throws InterruptedException {

		Actions act = new Actions(driver);
		act.contextClick(addedNewOther).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Email name is wrong or right click is not working", "Email", email.getText());
		email.click();
		Thread.sleep(3000);
		Assert.assertTrue(emailTitle.getText().equals("Email"));
		Assert.assertFalse(searchEmail.isEnabled());
		emailInput.click();
		emailInput.sendKeys("samuel supervisor");
		Thread.sleep(3000);
		searchEmail.click();
		Thread.sleep(2000);
		selectRecipient.click();
		Thread.sleep(3000);
		sendEmail.click();
	}

	public void deleteDocument() throws InterruptedException {

		Actions act = new Actions(driver);
		act.contextClick(addedNewOther).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(3000);
		Assert.assertEquals("Delete document name is ", "Delete", deleteDocument.getText());
		deleteDocument.click();

	}

	public void createNewFolder(String name) throws InterruptedException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("New Folder name is wrong ", "New Folder Ctrl+N", newFolder.getText());
		newFolder.click();
		Thread.sleep(2000);
		newFolderName.click();
		newFolderName.clear();
		newFolderName.sendKeys(name);
		attachmentsTitle.click();
		Thread.sleep(2000);
		attachmentsRefreshButton.click();
		expander.click();
		Assert.assertTrue(newFolderCreated.getText().equals(name));
	}

	public void renameFolder(String name) throws InterruptedException {

		attachmentsRefreshButton.click();
		expander.click();
		newFolderCreated.click();
		Actions act = new Actions(driver);
		act.contextClick(newFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Rename folder name is wrong or right click is not working", "Rename Folder",
				renameFolder.getText());
		renameFolder.click();
		newFolderName.sendKeys(name);
		attachmentsTitle.click();
		Thread.sleep(2000);
		attachmentsRefreshButton.click();
		expander.click();
		Assert.assertTrue(newFolderCreated.getText().equals(name));
	}

	public void deleteFolder() throws InterruptedException {

		attachmentsRefreshButton.click();
		expander.click();
		newFolderCreated.click();
		Actions act = new Actions(driver);
		act.contextClick(newFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Delete folder name is wrong or right click is not working", "Delete Folder",
				deleteFolder.getText());
		deleteFolder.click();
		Thread.sleep(2000);
		attachmentsRefreshButton.click();
		expander.click();
	}

	public void createSecondFolder(String name) throws InterruptedException {

		attachmentsRefreshButton.click();
		Thread.sleep(2000);
		expander.click();
		Thread.sleep(2000);
		root.click();
		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(4000);
		Assert.assertEquals("New Folder name is wrong ", "New Folder Ctrl+N", newFolder.getText());
		newFolder.click();
		Thread.sleep(2000);
		secondNewFolder.click();
		secondNewFolder.clear();
		secondNewFolder.sendKeys(name);
		attachmentsTitle.click();
		Thread.sleep(2000);
		attachmentsRefreshButton.click();
		expander.click();
		newFolderCreated.click();
		secondFolderCreated.click();
		Assert.assertTrue(secondFolderCreated.getText().equals(name));
	}

	public void addDocumentToFirstFolder() throws InterruptedException, IOException {

		newFolderCreated.click();
		Thread.sleep(3000);
		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(3000);
		Assert.assertEquals("New document name is wrong or right click is not working", "New Document",
				newDocument.getText());
		newDocument.click();
		Thread.sleep(2000);
		otherDocument.click();
		Thread.sleep(2000);
		ArkCaseTestUtils.uploadPicture();
		Thread.sleep(3000);

	}

	public void copyDocumentFromFirstFolder() throws InterruptedException {

		Actions act = new Actions(driver);
		addedDocumentToFirstFolder.click();
		Thread.sleep(2000);
		act.contextClick(newFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		Assert.assertEquals("Copy document name is wrong or right click is not working", "Copy Ctrl+C",
				copyDocument.getText());
		copyDocument.click();
	}

	public void pasteDocumentToSeconFolder() throws InterruptedException {

		Thread.sleep(2000);
		secondFolderCreated.click();
		Actions act = new Actions(driver);
		act.contextClick(secondFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(3000);
		Assert.assertEquals("Paste document name is wrong or right click is not working", " Paste",
				pasteDocument.getText());
		pasteDocument.click();
		Thread.sleep(4000);

	}

	public void deleteAddedFiles() throws InterruptedException {

		attachmentsRefreshButton.click();
		expander.click();
		Thread.sleep(2000);
		expanderSecondFolder.click();
		Thread.sleep(2000);
		secondFolderDocument.click();
		Thread.sleep(2000);
		Actions act = new Actions(driver);
		act.contextClick(secondFolderDocument).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		deleteLastFile.click();
		Thread.sleep(2000);
		act.contextClick(secondFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(1000);
		deleteDocInFirstFolder.click();
		Thread.sleep(2000);
		addedDocumentToFirstFolder.click();
		act.contextClick(addedDocumentToFirstFolder).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(1000);
		deleteDocInFirstFolder.click();
		Thread.sleep(2000);
		newFolderCreated.click();
		act.contextClick(newFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(1000);
		deleteFolder.click();
	}

	public void cutDocumentFromFirstFolder() throws InterruptedException {
		Actions act = new Actions(driver);
		addedDocumentToFirstFolder.click();
		Thread.sleep(2000);
		act.contextClick(newFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		cutDocument.click();
	}

	public void deleteCutedFiles() throws InterruptedException {

		attachmentsRefreshButton.click();
		expander.click();
		Thread.sleep(2000);
		expanderSecondFolder.click();
		Thread.sleep(2000);
		secondFolderCreated.click();
		expanderSecondFolder.click();
		Thread.sleep(2000);
		Actions act = new Actions(driver);
		act.contextClick(secondFolderDocument).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		deleteDocInFolder.click();
		Thread.sleep(2000);
		act.contextClick(secondFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(1000);
		deleteLastFile.click();
		Thread.sleep(2000);
		Thread.sleep(2000);
		newFolderCreated.click();
		act.contextClick(newFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(1000);
		deleteFolder.click();

	}

	public void pasteCuttedDocument() throws InterruptedException {

		secondFolderCreated.click();
		Actions act = new Actions(driver);
		act.contextClick(secondFolderCreated).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(pasteDocument.getText(), "Paste",
				"Paste document text is wrong or right click is not working");
		softAssert.assertAll();
		Thread.sleep(3000);
		pasteDocument.click();
	}

	public void addNewDocumentWitness() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Thread.sleep(2000);
		witnessInterview.click();
		ArkCaseTestUtils.uploadPdf();
		addedNewOther.click();

	}

	public void addNewDocumentNoticeOfInvestigation() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Thread.sleep(2000);
		noticeOfInvestigation.click();
		ArkCaseTestUtils.uploadPdf();
		addedNewOther.click();

	}

	public void addNewDocumentSfSignature() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Thread.sleep(2000);
		sfSignature.click();
		ArkCaseTestUtils.uploadDocx();
		addedNewOther.click();

	}

	public void addNewDocumentEDelevery() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Thread.sleep(2000);
		eDelivery.click();
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(3000);
		addedNewOther.click();
	}

	public void addNewDocumentgeneralRelease() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Thread.sleep(2000);
		generalRelease.click();
		ArkCaseTestUtils.uploadXlsx();
		Thread.sleep(3000);
	}

	public void addNewDocumentMedicalRelease() throws InterruptedException, IOException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Thread.sleep(2000);
		medicalRelease.click();
		ArkCaseTestUtils.uploadXlsx();

	}

	public void downloadDocument() throws InterruptedException {
		createdDocument.click();
		Actions act = new Actions(driver);
		act.contextClick(createdDocument).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(3000);
		Assert.assertTrue(downloadDocument.getText().equals("Download"));
		downloadDocument.click();

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
		Assert.assertTrue(i == 0);

	}

	public void verifyAddingDocumentIfTaskIsClosed() throws InterruptedException {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
		Thread.sleep(2000);
		newDocument.click();
		Assert.assertFalse("Adding new document is enabled", otherDocument.isDisplayed());

	}

	public void verifyAddingNoteIfDifrentUserAsignTo() {

		Assert.assertTrue("Note is added, it shouldnt", noterow.getText().isEmpty());

	}

	public void detailsSaveButtonClick() {
		detailsSaveButton.click();
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

}
