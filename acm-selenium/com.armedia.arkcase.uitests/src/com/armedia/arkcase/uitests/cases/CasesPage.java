package com.armedia.arkcase.uitests.cases;

import java.awt.AWTException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.asserts.SoftAssert;

import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.base.HttpResponseCode;

public class CasesPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[1]/nav/ul/li[3]/a")
	WebElement casesModule;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[2]/button")
	WebElement casesListRefresh;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/span/span[3]")
	public WebElement firstCaseInCaseList;
	// frames
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/iframe")
	public WebElement frameOne;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	public WebElement frameTwo;
	// Information ribbon
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[1]/h3/span")
	WebElement casesListTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[1]/div[1]/h4/a")
	WebElement createdCaseTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/div/div/div/ul/li[1]/span/span[3]")
	WebElement createdCaseTitleList;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[1]/div/a")
	WebElement createdCaseType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[1]/nav/div[1]/div/div/div[2]/a/strong/span[1]")
	WebElement userLogedIn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[1]/div/a")
	public WebElement assignedTo;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[2]/div")
	public WebElement createdDateCase;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[2]/div/a")
	public WebElement owningGroup;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[3]/div/a")
	WebElement priority;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[1]/div[2]/h4")
	WebElement caseId;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[3]/div/a")
	WebElement dueDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[3]/div/form/div/select")
	WebElement priorityDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[3]/div/form/div/select/option[1]")
	WebElement priorityLow;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[3]/div/form/div/span/button[1]")
	WebElement priorityConfirmBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[3]/div/form/div/select/option[3]")
	WebElement priorityHigh;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[3]/div/form/div/select/option[4]")
	WebElement priorityExpedite;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[1]/div/form/div/select")
	WebElement assignedToDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[1]/div/form/div/select/option[5]")
	WebElement assignedToAnn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[1]/div/form/div/span/button[1]")
	WebElement assignedToConfirmBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[1]/div[1]/h4/form/div/input")
	WebElement caseTitleInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[1]/div[1]/h4/form/div/span/button[1]")
	WebElement caseTitleConfirmBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[1]/div/form/div/select")
	WebElement caseTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[1]/div/form/div/select/option[10]")
	WebElement caseTypeDrugTrafficking;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[2]/div[1]/div/form/div/span/button[1]")
	WebElement editCaseTypeConfirmBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[2]/div/form/div/select")
	WebElement caseOwningGroupDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[2]/div/form/div/select/option[5]")
	WebElement ownigGroupACM_SUPERVISOR_DEV;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[3]/div[2]/div/form/div/span/button[1]")
	WebElement owningGroupConfirmBtn;

	// case buttons
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[1]")
	WebElement newCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[2]")
	WebElement editCaseButton;
	public @FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[3]") WebElement changeCaseStatusButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[4]")
	WebElement reinvestigateCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[2]")
	WebElement subscribeCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[1]")
	WebElement unsucribeCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[3]")
	WebElement mergeCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[4]")
	WebElement splitCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[1]/div/label/span")
	WebElement restrictCaseButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[5]")
	public WebElement refreshPage;
	// case links
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[1]/a")
	WebElement caseOverview;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[2]/a")
	WebElement caseDetails;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[3]/a")
	WebElement casePeople;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[4]/a")
	public WebElement caseDocuments;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[5]/a")
	public WebElement caseParticipants;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[6]/a")
	WebElement caseNotes;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[7]/a")
	public WebElement caseTasks;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[8]/a")
	WebElement caseReferences;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[9]/a")
	WebElement caseHistory;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[10]/a")
	WebElement caseCorrespondence;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[10]/a")
	WebElement caseTime;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[11]/a/i")
	WebElement caseCost;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[12]/a")
	WebElement caseTags;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[4]/ul/li[13]/a")
	WebElement caseCalendar;
	// change case status
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[4]/div[1]/input[1]")
	WebElement changeCaseStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[4]/div[1]/ul/li[5]/a")
	WebElement caseStatusDelete;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[4]/div[1]/ul/li[2]/a")
	WebElement caseStatusActive;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[4]/div[1]/ul/li[3]/a")
	WebElement caseStatusInactive;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[4]/div[1]/ul/li[4]/a")
	WebElement caseStatusClosed;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[5]/fieldset/div[1]/input")
	WebElement closedStatusDenied;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[5]/fieldset/div[2]/input")
	WebElement closedStatusFull;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[2]/div[2]/div[5]/fieldset/div[3]/input")
	WebElement closedStatusPartial;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[8]/div/input")
	WebElement changeCaseStatusSubmit;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div/div[1]/div[1]/h4")
	WebElement caseTitleDraft;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/iframe")
	public WebElement chnageCaseStausFrameOne;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	public WebElement chnageCaseStatusFrameTwo;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[4]/div[2]/div/div[2]/table/tbody/tr")
	WebElement caseSelectAprover;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
	WebElement searchForUsersInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
	WebElement GoSearch;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]/a")
	WebElement SearchedUser;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
	WebElement addUser;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[6]/a")
	WebElement chnageCaseStatusAddFilesBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[4]/div[2]/div/div[1]/div/table/tbody/tr/td/form/input")
	WebElement changeCaseStatusBrowseBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[4]/div[2]/div/div[2]/div/div/a")
	WebElement changeCaseStatusUploadBtn;
	// Cases tasks
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	public WebElement ReviewREquestToChange;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[7]")
	public WebElement approveDocumenButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[5]")
	public WebElement refreshbuttons;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[6]/div")
	public WebElement reviewRequestState;
	// case details
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[3]/div[4]")
	WebElement detailsText;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement detailsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement detailsSaveButton;
	@FindBy(how = How.XPATH, using = " /html/body/div[5]/div[1]")
	WebElement caseDetailsSavedPopup;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[3]")
	public WebElement detailChangeStatusButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[2]/div[9]/button[1]")
	WebElement detailsInsertLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[2]/div[1]/input")
	WebElement detailsLinkTextToDisplay;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[2]/div[2]/input")
	WebElement detailsLinkUrl;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div[3]/button")
	WebElement detailsLinkInsertButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[3]/div[4]/p/a")
	WebElement insertedLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[3]/div[2]/div[1]/div[2]/div/button[1]")
	WebElement editInsertedLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[3]/div[2]/div[1]/div[2]/div/button[2]")
	WebElement unlink;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[2]/div[9]/button[2]")
	WebElement insertPicture;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[1]/h4")
	WebElement picturePopUpTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[2]/div[1]/input")
	WebElement browsePictureButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[1]/div/div/div[3]/button")
	WebElement insertImageButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[3]/div[4]/p/img")
	WebElement insertedImage;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div[3]/div[2]/div[2]/div[2]/div[4]/button")
	WebElement deleteImageIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[2]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[9]")
	WebElement refreshDetails;

	// case people
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[1]/div/span")
	WebElement peopleTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement peopleTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement peopleFirstNameColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement peopleLastNameColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[1]/div/div/button")
	WebElement peopleAddNewButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/a[1]")
	WebElement contactMethodIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/a[2]")
	WebElement organizationIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/a[3]")
	WebElement addressIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/a[4]")
	WebElement aliasesIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[2]/div")
	WebElement typeInitiator;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[3]/div")
	WebElement initiatorFirstName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[4]/div")
	WebElement initiatorLastName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[5]/a[1]")
	WebElement peopleEditRecord;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div/div[5]/a[2]")
	WebElement peopleDeleteIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[1]/a[1]")
	WebElement contactMethodIconC;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[1]/a[2]")
	WebElement organizationsIconC;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[1]/a[3]")
	WebElement addressIconC;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[1]/a[4]")
	WebElement aliasesIconC;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[2]/div")
	WebElement typeComplaintant;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[3]/div")
	WebElement complaintantFirstName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[4]/div")
	WebElement complaintantLastName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[5]/a[1]")
	WebElement peopleEditRecordC;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div/div[5]/a[2]")
	WebElement peopleDeleteIconC;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[3]")
	WebElement peopleChangeCaseStatusButton;

	// people initiator contact Methods
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[1]/div/span")
	WebElement contactMethodsTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement typeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement valueColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement dateAddedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement addedByColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement typeContactMethods;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement valueContactMethods;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement dateAddedContactMethods;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement addedByContactMethods;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/span/i")
	WebElement editContactMethods;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement deleteContactMethods;
	// people organizations

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[1]/div/span")
	WebElement organizationTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement organizationTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement organizationValueColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement organizatiosDateAddedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement organizationAddedByColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement organizationTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement organizationValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement organizationDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement organizationAddedBytext;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/span/i")
	WebElement editOrganization;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement deleteOrganization;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[1]/div/div/button")
	WebElement addOrganizationButton;
	// people address table title
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[1]/div/span")
	WebElement addressTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement addressTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement addresseAddress;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]")
	WebElement addressCity;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement addressState;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[5]/div[2]/div[1]/span[1]")
	WebElement addressZip;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[6]/div[2]/div[1]/span[1]")
	WebElement addressCountry;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[7]/div[2]/div[1]/span[1]")
	WebElement addressDateAdded;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[8]/div[2]/div[1]/span[1]")
	WebElement addressAddedBy;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement addressTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement addressAddressText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement addressCityText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement addressZipText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/div")
	WebElement addressCountryText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[7]/div")
	WebElement addressDateAdddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[8]/div")
	WebElement addressAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[9]/span/i")
	WebElement editAddress;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[10]/span/i")
	WebElement deleteAddress;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[1]/div/div/button")
	WebElement addAddressButton;
	// people asliases
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[1]/div/span")
	WebElement aliasesTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement aliasesTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement aliasesValueColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement aliasesDateAddedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement aliasesAddedByColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement aliasesTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement aliasesValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement aliasesDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement aliasesAddedBytext;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/span/i")
	WebElement editAlias;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement deleteAlias;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[1]/div/div/button")
	WebElement addAliasButton;
	// complaintant(second row people) contact methods
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[1]/div/span")
	WebElement secondContactMethodsTabTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement secondContactMethodsType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement secondContactMethodsValue;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement secondContactMethodsDateAdded;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement secondContactMethodsAddedBy;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement seconndContactMethodsTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement secondContactMethodsValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement secondContactMethodsDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement secondContactMethodsAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/span/i")
	WebElement secondContactMethodsEditButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement secondContactMethodsDeleteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[1]/div/div/button")
	WebElement secondContactMethodsAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[1]/div[1]/a[1]/i")
	WebElement secondContactMethodIcon;
	// second row organizations
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[1]/div/span")
	WebElement secondOrganizationTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement secondOrganizationTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement secondOrganizationValueColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement secondOrganizatiosDateAddedColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement secondOrganizationAddedByColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement secondOrganizationTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement secondOrganizationValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement secondOrganizationDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement secondOrganizationAddedBytext;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/span")
	WebElement secondeditOrganization;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement secondDeleteOrganization;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[1]/div/div/button")
	WebElement secondAddOrganizationButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[1]/div[1]/a[2]")
	WebElement secondOrganizationIcon;
	// second row address
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[1]/div/span")
	WebElement secondAddressTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement secondAddressTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement secondAddresseAddress;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement secondAddressCity;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement secondAddressState;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[5]/div[2]/div[1]/span[1]")
	WebElement secondAddressZip;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[6]/div[2]/div[1]/span[1]")
	WebElement secondAddressCountry;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[7]/div[2]/div[1]/span[1]")
	WebElement secondAddressDateAdded;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[8]/div[2]/div[1]/span[1]")
	WebElement secondAddressAddedBy;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement secondAddressTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement secondAddressAddressText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement secondAddressCityText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement secondAddressStateText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]")
	WebElement secondAddressCountryText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement secondAddressZipText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[7]/div")
	WebElement secondAddressDateAdddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[8]/div")
	WebElement secondAddressAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[9]/span/i")
	WebElement secondEditAddress;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[10]/span/i")
	WebElement secondDeleteAddress;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[3]/div[1]/div/div/button")
	WebElement secondAddAddressButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[1]/div[1]/a[3]")
	WebElement secondAddresessIcon;
	// sort type
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[2]/div[2]/div[2]/i")
	WebElement sortType;
	// second row aliases

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[1]/div[1]/a[4]/i")
	WebElement secondAliasesIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[1]/div/span")
	WebElement secondAliasesTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement secondAliasesTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement secondAliasesValueColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement secondAliaseDateAddColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement secondAliasesAddedByColumn;
	// add new alias second rows
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[1]/div/div/button")
	WebElement addNewAliasButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[1]/span")
	WebElement addAliasTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/label")
	WebElement aliasTypesLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select")
	WebElement aliasTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[2]")
	WebElement asliasTypeFKA;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[2]")
	WebElement aliasTypeMaried;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/label")
	WebElement aliasValueLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input")
	WebElement aliasValueInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement saveAliasButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[1]/div")
	WebElement secondAliasTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[2]/div")
	WebElement secondAliasValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[3]/div")
	WebElement secondAliasDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div[1]/div/div[4]/div")
	WebElement secondAliasAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/span/i")
	WebElement editAliasButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement deleteAddedAlias;
	// edit alias popup
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editAliasPopUpTitle;
	// edit first contact methods
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editContactMethodsTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[1]/label")
	WebElement editContactMethodsTypes;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select")
	WebElement editContactMethodsDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[3]")
	WebElement editContactMethodsMobile;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div[2]/label")
	WebElement editContactMethodsValueLabel;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input")
	WebElement editContactMethodsValueInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement saveEditContactButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[5]")
	WebElement editContactMethodsFacebook;

	// edit organizations
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editOrganizationTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select")
	WebElement organizationTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[2]")
	WebElement organizationTypeGovernment;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input")
	WebElement editOrganizationValueInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement editOrganizationSaveButton;
	// edit address
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editAddressTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select")
	WebElement editAddressTypeDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[2]")
	WebElement addressTypeHome;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[1]")
	WebElement addressStreetInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[2]")
	WebElement addressCityInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[3]")
	WebElement addressStateInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[4]")
	WebElement addressZipInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[5]")
	WebElement addressCountryInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement editAddressSaveButton;
	// edit second people type
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select")
	WebElement personTypesDropDown;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[2]")
	WebElement personTypesDefendant;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[3]")
	WebElement personTypesWitness;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[4]")
	WebElement personTypesWASOfficer;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[5]")
	WebElement personTypesVictim;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[6]")
	WebElement personTypesDefenceCounsel;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[7]")
	WebElement personTypesForensicScientist;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[1]")
	WebElement personfirstNameInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/input[2]")
	WebElement personlastNameInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement editRecordSaveButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[2]/i")
	WebElement peopleSortType;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[1]/span")
	WebElement addPersonTitle;
	// Third Row people section
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[2]")
	WebElement thirdTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[3]")
	WebElement thirdFirstNameText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[4]")
	WebElement thirdLastNameText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[5]/a[1]")
	WebElement thirdEditPeopleButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[5]/a[2]")
	WebElement thirdDeletePeopleButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[1]/a[1]")
	WebElement thirdContactMethodsIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[1]/a[2]")
	WebElement thirdOrganizationsIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[1]/a[3]")
	WebElement thirdAddressIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[1]/a[4]")
	WebElement thirdAliasesIcon;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[6]")
	WebElement personTypeDefenceCounsel;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/select/option[4]")
	WebElement personTypeVictim;
	// third contact methods
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[1]/div/div/button")
	WebElement thirdContactMethodAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement thirdContactMethodsTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement thirdContactMethodsValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]")
	WebElement thirdContactMethodDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]")
	WebElement thirdContactMethodAddedBy;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement thirdContactMethodDeleteButton;
	// third organization
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[1]/div/div/button")
	WebElement thirdOrganizationAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[1]/span")
	WebElement addOrganizationTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement thirdOrganizationTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]")
	WebElement thirdOrganizationValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]")
	WebElement thirdOrganizationDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]")
	WebElement thirdOrganizationAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement thirdOrganizationDeleteButton;
	// third address
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[1]/div/div/button")
	WebElement thirdAddressAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement thirdAddressTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]")
	WebElement thirdAddressText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]")
	WebElement thirdAddressCityText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]")
	WebElement thirdAddressStateText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]")
	WebElement thirdAddressZipText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]")
	WebElement thirdAddressCountryText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[7]")
	WebElement thirdAddedDateText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[8]")
	WebElement thirdAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[10]/span/i")
	WebElement thirdAddedAddressDeleteButton;
	// third alias
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[1]/div/div/button")
	WebElement thirdAliasAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement thirdAliasTypeText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]")
	WebElement thirdAliasValueText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]")
	WebElement thirdAliasDateAddedText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]")
	WebElement thirdAliasAddedByText;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/span/i")
	WebElement thirdAliasDeleteButton;
	// filters people table
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[2]/div[2]/div[3]/div/div/input")
	WebElement peopleTypeFilter;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[3]/div[2]/div[3]/div/div/input")
	WebElement peopleFirstNameFilter;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[1]/div/div/div/div/div/div[4]/div[2]/div[3]/div/div/input")
	WebElement peopleLastNameFilter;
	/// cases notes
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[1]/div/span")
	WebElement notesTitleTable;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement notesNoteColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement notesCreatedColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement notesAuthorColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[1]/div/div/button")
	WebElement addNewNoteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[3]/div/div/input")
	WebElement noteFilter;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[3]/div/div/input")
	WebElement createdFilter;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[3]/div/div/input")
	WebElement authorFilter;
	// add note popup
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[1]/span")
	WebElement addNoteTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/form/div/textarea")
	WebElement noteInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement addNoteButton;
	// added note
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement notesNote;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement notesCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement notesAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/a[1]/i")
	WebElement editNoteButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/a[2]/i")
	WebElement deleteNoteButton;
	// edit note
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3[2]/span")
	WebElement editNoteTitle;
	// cases task table
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement taskTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement taskAddButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement taskTitleColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement assigneeCoumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement createdColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement priorityColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[5]/div[2]/div[1]/span[1]")
	WebElement dueColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[6]/div[2]/div[1]/span[1]")
	WebElement statusColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[7]/div[2]/div[1]/span[1]")
	WebElement actionColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement titleField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement assigneeField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement createdField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement priorityFiled;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement dueField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement statusField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[7]/div")
	WebElement actionField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a")
	WebElement tableTaskTitle;

	// task page associate with case

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[1]/div[1]/h4/a")
	public WebElement caseTitleInTasks;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[1]/div[2]/h4/span")
	WebElement caseIdInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[1]")
	WebElement signButtonInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[2]")
	WebElement subscribeButtonInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[4]")
	WebElement deleteButtonInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[5]")
	WebElement completeButtonInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[2]/div[1]/div")
	WebElement caseTypeInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[7]")
	WebElement refreshButtonInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[3]/div[1]/div")
	WebElement assignToInTheTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[2]/div[2]/div")
	WebElement incidentDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[3]/div[2]/div")
	WebElement owningGroupInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[1]/div/div/div[1]/div[2]/div[3]/div")
	WebElement priorityInTaskPage;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/h4/a")
	WebElement taskTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[1]/div/a")
	WebElement percentCompletition;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[4]/div/a")
	WebElement startDate;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[2]/div/a")
	WebElement assignee;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[5]/div/a")
	WebElement dueDateTask;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[3]/div/a")
	WebElement priorityTask;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div[6]/div")
	WebElement stateTask;
	// References
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement referencesTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement addReferenceButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[1]/div[1]/div[1]/span[1]")
	WebElement referenceNumberColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[2]/div[2]/div[1]/span[1]")
	WebElement referenceTitleColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[3]/div[2]/div[1]/span[1]")
	WebElement referenceModifiedColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[4]/div[2]/div[1]/span[1]")
	WebElement referenceTypeColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[1]/div/div/div/div/div/div[5]/div[2]/div[1]/span[1]")
	WebElement referenceStatusColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[1]")
	WebElement addReferencePopUpTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[1]/div/input")
	WebElement searchReferenceInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[1]/div/span/button")
	WebElement searchReferenceButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement searchedReferenceTypeColumn;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[3]/button[2]")
	WebElement AddSearchedReferenceButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement searchedReferenceName;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement searchedreferenceType;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement searchedReferenceTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[3]/button[1]")
	WebElement addreferenceCancelButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[3]/button[2]")
	WebElement addSearchedReferenceButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]")
	WebElement referenceNumberField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]")
	WebElement referenceTitleField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement referenceModifiedField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement referenceTypeField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement referenceStatusField;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/input")
	WebElement casesSearchInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/div/div[2]/div/object-tree/section/header/div[1]/div/span/button")
	WebElement casesGoButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div/div/div[1]/div[3]/div")
	WebElement searchedRefNoResult;
	// second row in references table
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[1]")
	WebElement secondRowReferenceNumber;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[2]")
	WebElement secondRowReferenceTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[3]/div")
	WebElement secondRowReferenceModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[4]/div")
	WebElement secondRowReferenceType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div[2]/div/div[5]/div")
	WebElement secondRowReferenceStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a")
	WebElement referenceNumberLink;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/a")
	WebElement referencetitleLink;
	// merge popup
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[1]")
	WebElement mergePopUpTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[1]/div/input")
	WebElement mergeSearchForCaseInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[1]/div/span/button")
	WebElement mergeSearchBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement searchedCaseName;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement searchedCaseType;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[3]/div")
	WebElement searchedCaseTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[4]/div")
	WebElement searchedCaseParent;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[5]/div")
	WebElement searchedCaseAssignee;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[6]/div")
	WebElement searchedCaseMdofied;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[3]/button[1]")
	WebElement mergeCancelBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/search-modal/div[3]/button[2]")
	WebElement mergeBtn;

	public void casesModuleClick() {

		casesModule.click();
	}

	public void verifyCasesTitle() {

		Assert.assertEquals("Case title in the cases page is wrong", "Cases", casesListTitle.getText());
	}

	public void createdCaseInListClick() {
		createdCaseTitleList.click();
	}

	public void VerifycreatedDate() {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		Assert.assertTrue(createdDateCase.getText().equals(createdDate));

	}

	public void verifyCreatedCaseInfo(String caseName, String caseType) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(createdCaseTitle.getText(), caseName, "Case title is wrong");
		createdCaseInListClick();
		softAssert.assertEquals(createdCaseTitleList.getText(), caseName, "Case title in the cases list is wrong");
		softAssert.assertEquals(createdCaseType.getText(), caseType, "Case type name is wrong");
		// softAssert.assertEquals(userLogedIn.getText(), assignedTo.getText(),
		// "User name is wrong");
		softAssert.assertEquals(owningGroup.getText(), "ACM_INVESTIGATOR_DEV", "Owning group is wrong");
		softAssert.assertEquals(priority.getText(), "Medium", "Priority type is wrong");
		softAssert.assertFalse(caseId.getText().isEmpty(), "case id is empty");
		softAssert.assertFalse(dueDate.getText().isEmpty(), "due date is empty");
		softAssert.assertFalse(dueDate.getText().isEmpty(), "due date is empty");
		softAssert.assertTrue(restrictCaseButton.isDisplayed(), "restrict button is not diplayed");
		softAssert.assertTrue(restrictCaseButton.isEnabled(), "restrict button is not enabled");
		softAssert.assertEquals(restrictCaseButton.getText(), "Restrict?", "Restrict text is wrong");
		softAssert.assertTrue(newCaseButton.isDisplayed(), "New case button is not displayed");
		softAssert.assertTrue(newCaseButton.isEnabled(), "New case button is not enabled");
		softAssert.assertTrue(newCaseButton.getText().equals("New"), "New case button text is not New");
		softAssert.assertTrue(editCaseButton.isDisplayed(), "Edit case button is not displayed");
		softAssert.assertTrue(editCaseButton.isEnabled(), "Edit case button is not enabled");
		softAssert.assertTrue(editCaseButton.getText().equals("Edit"), "Edit case button text is not new");
		softAssert.assertTrue(changeCaseStatusButton.isDisplayed(), "Change Case Status case button is not displayed");
		softAssert.assertTrue(changeCaseStatusButton.isEnabled(), "Change Case Status button is not enabled");
		softAssert.assertEquals(changeCaseStatusButton.getText(), "Change Case Status",
				"Change Case status button text is not change case status");

		softAssert.assertTrue(reinvestigateCaseButton.isDisplayed(),
				"Reinvestigate Case Status case button is not displayed");
		softAssert.assertTrue(reinvestigateCaseButton.isEnabled(), "Reinvestigate Case Status button is not enabled");
		softAssert.assertTrue(reinvestigateCaseButton.getText().equals("Reinvestigate"),
				"Reinvestigate Case   button text is not Reinvestigate");
		softAssert.assertTrue(subscribeCaseButton.isDisplayed(), "SubscribeCase  case button is not displayed");
		softAssert.assertTrue(subscribeCaseButton.isEnabled(), "Subscribe Case  button is not enabled");
		softAssert.assertTrue(subscribeCaseButton.getText().equals("Subscribe"),
				"Subscribe Case   button text is not Subscribe");
		softAssert.assertTrue(mergeCaseButton.isDisplayed(), "MergeCaseButton case button is not displayed");
		softAssert.assertTrue(mergeCaseButton.isEnabled(), "MergeCaseButton  button is not enabled");
		softAssert.assertTrue(mergeCaseButton.getText().equals("Merge"), "Merge button text is not Merge");
		softAssert.assertTrue(splitCaseButton.isDisplayed(), "splitCaseButton case button is not displayed");
		softAssert.assertTrue(splitCaseButton.isEnabled(), "SplitCaseButton  button is not enabled");
		softAssert.assertTrue(splitCaseButton.getText().equals("Split"), "Split button text is not Merge");
		softAssert.assertTrue(refreshPage.isDisplayed(), "Refresh button is not diplayed");
		softAssert.assertTrue(refreshPage.isEnabled(), "Refresh button is not enabled");
		softAssert.assertTrue(caseOverview.isDisplayed(), "Overview link is not displayed");
		softAssert.assertTrue(caseDetails.isDisplayed(), "Details link is not displayed");
		softAssert.assertTrue(casePeople.isDisplayed(), "People link is not displayed");
		softAssert.assertTrue(caseDocuments.isDisplayed(), "Documents link is not displayed");
		softAssert.assertTrue(caseParticipants.isDisplayed(), "PArticipants link is not displayed");
		softAssert.assertTrue(caseNotes.isDisplayed(), "Notes link is not displayed");
		softAssert.assertTrue(caseTasks.isDisplayed(), "Tasks link is not displayed");
		softAssert.assertTrue(caseReferences.isDisplayed(), "References link is not displayed");
		softAssert.assertTrue(caseHistory.isDisplayed(), "History link is not displayed");
		softAssert.assertTrue(caseCorrespondence.isDisplayed(), "Correspondence  link is not displayed");
		softAssert.assertTrue(caseTime.isDisplayed(), "Time link is not displayed");
		softAssert.assertTrue(caseCost.isDisplayed(), "Cost link is not displayed");
		softAssert.assertTrue(caseTags.isDisplayed(), "Tags link is not displayed");
		softAssert.assertTrue(caseCalendar.isDisplayed(), "Calendar link is not displayed");
		softAssert.assertAll();
	}

	public void deleteCase() throws InterruptedException {
		changeCaseStatus.click();
		Thread.sleep(4000);
		caseStatusDelete.click();
		Thread.sleep(4000);
		caseSelectAprover.click();
		Thread.sleep(2000);
		searchForUsersInput.click();
		searchForUsersInput.sendKeys("Samuel Supervisor");
		GoSearch.click();
		Thread.sleep(2000);
		SearchedUser.click();
		Thread.sleep(1000);
		addUser.click();
		Thread.sleep(4000);
		changeCaseStatusSubmit.click();
		Thread.sleep(6000);
	}

	public void changeCaseStatusActive() throws InterruptedException {

		changeCaseStatus.click();
		Thread.sleep(4000);
		Assert.assertEquals("Case status Active name is wrong", "Active", caseStatusActive.getText());
		caseStatusActive.click();
		Thread.sleep(4000);
	}

	public void selectApproverForChangeCaseStatus(String approver) throws InterruptedException {

		caseSelectAprover.click();
		Thread.sleep(2000);
		searchForUsersInput.click();
		searchForUsersInput.sendKeys(approver);
		GoSearch.click();
		Thread.sleep(2000);
		SearchedUser.click();
		Thread.sleep(1000);
		addUser.click();
		Thread.sleep(4000);
		changeCaseStatusSubmit.click();
		Thread.sleep(6000);

	}

	public void changeCaseStatusInactive() throws InterruptedException {

		changeCaseStatus.click();
		Thread.sleep(4000);
		Assert.assertEquals("Case status Inactive name is wrong", "Inactive", caseStatusInactive.getText());
		caseStatusInactive.click();
		Thread.sleep(4000);
	}

	public void changeCaseStatusClosed() throws InterruptedException {

		changeCaseStatus.click();
		Thread.sleep(4000);
		Assert.assertEquals("Case status closed name is wrong", "Closed", caseStatusClosed.getText());
		caseStatusClosed.click();
		Thread.sleep(4000);
	}

	public void changeCaseStatusAproved() throws InterruptedException, IOException {
		HttpResponseCode responseCode = new HttpResponseCode();
		// TasksPage tasks = PageFactory.initElements(driver, TasksPage.class);
		caseTasks.click();
		Thread.sleep(6000);
		refreshPage.click();
		Thread.sleep(10000);
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]"))
				.size();
		Assert.assertTrue("Created task for deleting the case is not shown", i > 0);
		taskTitleColumn.click();
		Thread.sleep(5000);
		taskTitleColumn.click();
		Thread.sleep(5000);
		Assert.assertEquals(
				"Review request to change task name is wrong or automated task for deleting case is not created",
				"Review Request to Change Case Status" + " '" + caseId.getText() + "'",
				ReviewREquestToChange.getText());
		ReviewREquestToChange.click();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]");
		Thread.sleep(10000);
		approveDocumenButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(reviewRequestState.getText().equals("CLOSED"));

	}

	public void verifyDetailsSection() throws InterruptedException {

		caseDetails.click();
		Thread.sleep(3000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(detailsTitle.getText(), "Details", "Details title is wrong");
		softAssert.assertTrue(detailsSaveButton.isDisplayed(), "Details save button is not shown");
		softAssert.assertAll();
	}

	public void deleteDetailsText() {

		detailsText.click();
		detailsText.clear();
		detailsSaveButton.click();
		Assert.assertTrue(detailsText.getText().isEmpty());
		Assert.assertEquals("Case details alert text is wrong", "Case details saved", caseDetailsSavedPopup.getText());

	}

	public void verifyPeopleSectionInitiator(String firstName, String lastName) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(peopleTableTitle.getText(), "People", "People table ttle is not correct");
		softAssert.assertEquals(peopleTypeColumn.getText(), "Type", "People type column text is not correct");
		softAssert.assertEquals(peopleFirstNameColumn.getText(), "FirstName",
				"People first name column is not correct ");
		softAssert.assertEquals(peopleLastNameColumn.getText(), "LastName", "People last name column is not correct");
		softAssert.assertTrue(peopleAddNewButton.isDisplayed(), "Add new people button is not displayed");
		softAssert.assertTrue(contactMethodIcon.isDisplayed(), "Contact method icon is not displayed");
		softAssert.assertTrue(organizationIcon.isDisplayed(), "Ogranization icon is not diplayed");
		softAssert.assertTrue(addressIcon.isDisplayed(), "Address icon is not displayed");
		softAssert.assertTrue(aliasesIcon.isDisplayed(), "Aliases icon is not displayed");
		softAssert.assertTrue(typeInitiator.getText().equals("Initiator"), "Initiator text is not correct");
		softAssert.assertEquals(initiatorFirstName.getText(), firstName, "Initiator first name is not correct");
		softAssert.assertEquals(initiatorLastName.getText(), lastName, "Initiator last name is not correct");
		softAssert.assertTrue(peopleEditRecord.isDisplayed(), "Edit record icon is not displayed");
		softAssert.assertTrue(peopleDeleteIcon.isDisplayed(), "People delete icon is not displayed");
		softAssert.assertAll();
	}

	public void verifyPeopleType(String type, String firstName, String lastName) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(contactMethodIconC.isDisplayed(), "Second row Contact method icon is not displayed");
		softAssert.assertTrue(organizationsIconC.isDisplayed(),
				"Second row Second row Ogranization icon is not diplayed");
		softAssert.assertTrue(addressIconC.isDisplayed(), " Second row Address icon is not displayed");
		softAssert.assertTrue(aliasesIconC.isDisplayed(), "Second row Aliases icon is not displayed");
		softAssert.assertEquals(typeComplaintant.getText(), type, "People type name is wrong");
		softAssert.assertEquals(complaintantFirstName.getText(), firstName, "People first name is wrong");
		softAssert.assertEquals(complaintantLastName.getText(), lastName, "People last name is wrong");
		softAssert.assertTrue(peopleEditRecordC.isDisplayed(), "Second row Edit record icon is not displayed");
		softAssert.assertTrue(peopleDeleteIconC.isDisplayed(), "Second row People delete icon is not displayed");
		softAssert.assertAll();
	}

	public void verifyPersonTypes() throws InterruptedException {

		personTypesDropDown.click();
		Thread.sleep(2000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(personTypesDefendant.getText(), "Defendant", "Defendant person type name is wrong");
		softAssert.assertEquals(personTypesWitness.getText(), "Witness", "Witness person type name is wrong");
		softAssert.assertEquals(personTypesWASOfficer.getText(), "WAS Officer",
				"WAS Officer person type name is wrong");
		softAssert.assertEquals(personTypesVictim.getText(), "Victim", "Victim person type name is wrong");
		softAssert.assertEquals(personTypesDefenceCounsel.getText(), "Defence Counsel",
				"Defence Counsel perosn type name is wrong");
		softAssert.assertEquals(personTypesForensicScientist.getText(), "Forensic Scientist",
				"Forensic Scientist person type name is wrong");
		softAssert.assertAll();
	}

	public void addPersonType(String firstName, String lastName) throws InterruptedException {

		personTypesDropDown.click();
		Thread.sleep(2000);
		personTypesForensicScientist.click();
		Thread.sleep(2000);
		personfirstNameInput.click();
		personfirstNameInput.clear();
		personfirstNameInput.sendKeys(firstName);
		personlastNameInput.click();
		personlastNameInput.clear();
		personlastNameInput.sendKeys(lastName);
		Thread.sleep(2000);
		editRecordSaveButton.click();
		Thread.sleep(3000);

	}

	public void priorityTypePeople() throws InterruptedException {

		peopleTypeColumn.click();
		Thread.sleep(2000);
		peopleSortType.click();
		Thread.sleep(2000);

	}

	public void verifyContactMethods(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(contactMethodsTableTitle.getText().equals("Contact Methods"),
				"Contact methods table title is wrong");
		softAssert.assertTrue(typeColumn.getText().equals("Type"), "Type column name is wrong");
		softAssert.assertTrue(valueColumn.getText().equals("Value"), "Value name column");
		softAssert.assertTrue(dateAddedColumn.getText().equals("Date Added"), "Date added column name is wrong");
		softAssert.assertTrue(addedByColumn.getText().equals("Added By"), "Added by column name is wrong");
		softAssert.assertEquals(typeContactMethods.getText(), type, "Contact methods added type text is wrong");
		softAssert.assertEquals(valueContactMethods.getText(), value, "Contact methods added value text is wrong");
		softAssert.assertEquals(dateAddedContactMethods.getText(), createdDate,
				"Contact methods created date is wrong");
		softAssert.assertEquals(addedByContactMethods.getText(), addedBy, "Contact methods added by user is wrong");
		softAssert.assertTrue(editContactMethods.isDisplayed(), "Edit contact methods icon is not displayed");
		softAssert.assertTrue(deleteContactMethods.isDisplayed(), "Delete contact methods icon is not diplsyed");
		softAssert.assertAll();

	}

	public void verifyOrganizations(String type, String value, String user) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(organizationTableTitle.getText().equals("Organizations"),
				"Organization table title is wrong");
		softAssert.assertTrue(organizationTypeColumn.getText().equals("Type"),
				"Organization type column name is wrong");
		softAssert.assertTrue(organizationValueColumn.getText().equals("Value"),
				"Organization value column name is wrong");
		softAssert.assertTrue(organizatiosDateAddedColumn.getText().equals("Date Added"),
				"Organization DateAdded column name is wrong");
		softAssert.assertTrue(organizationAddedByColumn.getText().equals("Added By"),
				"Organization added by column name is wrong");
		softAssert.assertEquals(organizationTypeText.getText(), type, "Organization type name is wrong");
		softAssert.assertEquals(organizationValueText.getText(), value, "Organization value name is wrong");
		softAssert.assertEquals(organizationDateAddedText.getText(), createdDate, "Date added text is wrong");
		softAssert.assertEquals(organizationAddedBytext.getText(), user, "Organization added by user is wrong");
		softAssert.assertTrue(editOrganization.isDisplayed(), "Edit organization icon is not displayed");
		softAssert.assertTrue(deleteOrganization.isDisplayed(), "Delete organization is not displayed");
		softAssert.assertTrue(addOrganizationButton.isDisplayed(), "Add organization button is not displayed");
		softAssert.assertAll();
	}

	public void verifyAddressesTable(String type, String address, String city, String state, String zip, String country,
			String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(addressTableTitle.getText().equals("Addresses"), "Address table title is wrong");
		softAssert.assertTrue(addressTypeColumn.getText().equals("Type"), "Address type column name  is wrong");
		softAssert.assertTrue(addresseAddress.getText().equals("Address"), "Address address column name is wrong");
		softAssert.assertTrue(addressCity.getText().equals("City"), "Address city column name is wrong");
		softAssert.assertTrue(addressState.getText().equals("State"));
		softAssert.assertTrue(addressZip.getText().equals("Zip"), "Addres zip column name is wrong");
		softAssert.assertTrue(addressCountry.getText().equals("Country"), "Address country column name is wrong");
		softAssert.assertTrue(addressDateAdded.getText().equals("Date Added"),
				"Address Date added column name is wrong");
		softAssert.assertTrue(addressAddedBy.getText().equals("Added By"), "Address Added by column name is wrong");
		softAssert.assertEquals(addressTypeText.getText(), type, "Address type text  is wrong");
		softAssert.assertEquals(addressAddressText.getText(), address, "Address address text is wrong");
		softAssert.assertEquals(addressCityText.getText(), city, "Address city text is wrong");
		softAssert.assertEquals(addressZipText.getText(), zip, "Address zip text is wrong");
		softAssert.assertEquals(addressDateAdddedText.getText(), createdDate, "Address added date is wrong");
		softAssert.assertEquals(addressAddedByText.getText(), addedBy, "Address added by text is wrong");
		softAssert.assertTrue(editAddress.isDisplayed(), "Edit Address icon is not displayed");
		softAssert.assertTrue(deleteAddress.isDisplayed(), "Delete address icon is not displayed");
		softAssert.assertTrue(addAddressButton.isDisplayed(), "Add address button is not displayed");
		softAssert.assertAll();

	}

	public void verifyAddressDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("The address is not deleted", i == 0);

	}

	public void verifyAliasesTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(aliasesTableTitle.getText().equals("Aliases"), "Aliases table title is wrong");
		softAssert.assertTrue(aliasesTypeColumn.getText().equals("Type"), "Aliases type column name is wrong");
		softAssert.assertTrue(aliasesValueColumn.getText().equals("Value"), "Aliases value column name is wrong");
		softAssert.assertTrue(aliasesDateAddedColumn.getText().equals("Date Added"),
				"Aliases DateAdded column name is wrong");
		softAssert.assertTrue(aliasesAddedByColumn.getText().equals("Added By"),
				"Aliases added by column name is wrong");
		softAssert.assertAll();

	}

	public void verifySecondContactMethods(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondContactMethodsTabTitle.getText(), "Contact Methods",
				"Second  conact methods table title is wrong");
		softAssert.assertEquals(secondContactMethodsType.getText(), "Type",
				"Second contact methods type column name is wrong");

		softAssert.assertEquals(secondContactMethodsValue.getText(), "Value",
				"Second contact methods value column name is wrong");
		softAssert.assertEquals(secondContactMethodsDateAdded.getText(), "Date Added",
				"Second contact methods date added column name is wrong");
		softAssert.assertEquals(secondContactMethodsAddedBy.getText(), "Added By",
				"Second contact methods added by column name is wrong");
		softAssert.assertEquals(seconndContactMethodsTypeText.getText(), type,
				"Second contact methods type value is wrong");

		softAssert.assertEquals(secondContactMethodsValueText.getText(), value,
				"Second contact methods Value value is wrong ");

		softAssert.assertEquals(secondContactMethodsDateAddedText.getText(), createdDate,
				"Second Contact method date added value is wrong");

		softAssert.assertEquals(secondContactMethodsAddedByText.getText(), addedBy,
				"Second contact methods adedd by value is wrong");

		softAssert.assertTrue(secondContactMethodsEditButton.isEnabled(),
				"Second contact methods edit button is not enabled");

		softAssert.assertTrue(secondContactMethodsDeleteButton.isEnabled(),
				"Second contact methods delete button si not enabled");
		softAssert.assertTrue(secondContactMethodsAddButton.isEnabled(),
				"Second contact methods add button is not enabled");
		softAssert.assertAll();

	}

	public void verifySecondOrganization(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(secondOrganizationTableTitle.getText().equals("Organizations"),
				"Organization table title is wrong");
		softAssert.assertTrue(secondOrganizationTypeColumn.getText().equals("Type"),
				"Organization type column name is wrong");
		softAssert.assertTrue(secondOrganizationValueColumn.getText().equals("Value"),
				"Organization value column name is wrong");
		softAssert.assertTrue(secondOrganizatiosDateAddedColumn.getText().equals("Date Added"),
				"Organization DateAdded column name is wrong");
		softAssert.assertTrue(secondOrganizationAddedByColumn.getText().equals("Added By"),
				"Organization added by column name is wrong");
		softAssert.assertEquals(secondOrganizationTypeText.getText(), type, "Organization type is wrong");
		softAssert.assertEquals(secondOrganizationValueText.getText(), value, "Organization value is wrong");
		softAssert.assertEquals(secondOrganizationDateAddedText.getText(), createdDate,
				"Organization date added is wrong");
		softAssert.assertEquals(secondOrganizationAddedBytext.getText(), addedBy,
				"Organization added by user is wrong");
		softAssert.assertTrue(secondeditOrganization.isDisplayed(), "Edit organization icon is not displayed");
		softAssert.assertTrue(secondDeleteOrganization.isDisplayed(), "Delete organization is not displayed");
		softAssert.assertTrue(secondAddOrganizationButton.isDisplayed(), "Add organization button is not displayed");
		softAssert.assertAll();

	}

	public void verifySecondAddressesTable(String type, String address, String city, String state, String zip,
			String country, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(secondAddressTableTitle.getText().equals("Addresses"), "Address table title is wrong");
		softAssert.assertTrue(secondAddressTypeColumn.getText().equals("Type"), "Address type column name  is wrong");
		softAssert.assertTrue(secondAddresseAddress.getText().equals("Address"),
				"Address address column name is wrong");
		softAssert.assertTrue(secondAddressCity.getText().equals("City"), "Address city column name is wrong");
		softAssert.assertTrue(secondAddressState.getText().equals("State"));
		softAssert.assertTrue(secondAddressZip.getText().equals("Zip"), "Addres zip column name is wrong");
		softAssert.assertTrue(secondAddressCountry.getText().equals("Country"), "Address country column name is wrong");
		softAssert.assertTrue(secondAddressDateAdded.getText().equals("Date Added"),
				"Address Date added column name is wrong");
		softAssert.assertTrue(secondAddressAddedBy.getText().equals("Added By"),
				"Address Added by column name is wrong");
		softAssert.assertEquals(secondAddressTypeText.getText(), type, "Address type text is wrong");
		softAssert.assertEquals(secondAddressAddressText.getText(), address, "Address address text is wrong");
		softAssert.assertEquals(secondAddressCityText.getText(), city, "Address city text is wrong");
		softAssert.assertEquals(secondAddressStateText.getText(), state, "Address state text is wrong");
		softAssert.assertEquals(secondAddressCountryText.getText(), country, "Address country text is wrong");
		softAssert.assertEquals(secondAddressZipText.getText(), zip, "Address zip text is wrong");
		softAssert.assertEquals(secondAddressAddedByText.getText(), addedBy, "Address added by text is wrong");
		softAssert.assertEquals(secondAddressDateAdddedText.getText(), createdDate, "Address date added text is wrong");
		softAssert.assertTrue(secondEditAddress.isDisplayed(), "Edit Address icon is not displayed");
		softAssert.assertTrue(secondDeleteAddress.isDisplayed(), "Delete address icon is not displayed");
		softAssert.assertTrue(secondAddAddressButton.isDisplayed(), "Add address button is not displayed");
		softAssert.assertAll();

	}

	public void verifySecondAddressIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("Address is not deleted", i == 0);

	}

	public void verifySecondAliasesTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(secondAliasesTableTitle.getText().equals("Aliases"),
				"Second aliases table title is wrong");
		softAssert.assertTrue(secondAliasesTypeColumn.getText().equals("Type"),
				"Second aliases table,type column name is wrong");
		softAssert.assertTrue(secondAliasesValueColumn.getText().equals("Value"),
				"Second aliases table,value column name is wrong");
		softAssert.assertTrue(secondAliaseDateAddColumn.getText().equals("Date Added"),
				"Second aliases table,date added column name is wrong");
		softAssert.assertTrue(secondAliasesAddedByColumn.getText().equals("Added By"),
				"Second aliases table,added by column name is wrong");
		softAssert.assertAll();

	}

	public void addNewAliasFKA(String value) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(addAliasTitle.getText().equals("Add Alias"), "Add new alias title popup is wrong");
		softAssert.assertTrue(aliasTypesLabel.getText().equals("Alias Types"), "Alias types label name is wrong");
		aliasTypeDropDown.click();
		softAssert.assertEquals(asliasTypeFKA.getText(), "FKA", "Alias type is wrong");
		asliasTypeFKA.click();
		softAssert.assertTrue(aliasValueLabel.getText().equals("Value"));
		softAssert.assertAll();
		aliasValueInput.click();
		Thread.sleep(2000);
		aliasValueInput.sendKeys(value);
		saveAliasButton.click();

	}

	public void verifyAddedAlias(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondAliasTypeText.getText(), type, "Second alias table, added alias type is wrong");
		softAssert.assertEquals(secondAliasValueText.getText(), value, "Second alias table,added alias value is wrong");
		softAssert.assertEquals(secondAliasDateAddedText.getText(), createdDate,
				"Second alias table,added alias created date is wrong");
		softAssert.assertEquals(secondAliasAddedByText.getText(), addedBy,
				"Second alias table, added alias added by text is wrong");
		softAssert.assertAll();

	}

	public void verifyFirstAddedAlias(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(aliasesTypeText.getText(), type, "Second alias table, added alias type is wrong");
		softAssert.assertEquals(aliasesValueText.getText(), value, "Second alias table,added alias value is wrong");
		softAssert.assertEquals(aliasesDateAddedText.getText(), createdDate,
				"Second alias table,added alias created date is wrong");
		softAssert.assertEquals(aliasesAddedBytext.getText(), addedBy,
				"Second alias table, added alias added by text is wrong");
		softAssert.assertAll();

	}

	public void editAlias(String value) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(editAliasPopUpTitle.getText().equals("Edit Record"), "Edit alias popup title is wrong");
		aliasTypeDropDown.click();
		softAssert.assertEquals(aliasTypeMaried.getText(), "Married", "Alias type married text is wrong");
		aliasTypeMaried.click();
		Thread.sleep(2000);
		aliasValueInput.click();
		aliasValueInput.clear();
		aliasValueInput.sendKeys(value);
		softAssert.assertAll();
		saveAliasButton.click();
	}

	public void verifySecondAliasDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue(i == 0);

	}

	public void verifyFirstAliasDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();

		Assert.assertTrue(i == 0);

	}

	public void editFirstContactMethodsMobile(String value) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(editContactMethodsTitle.getText().equals("Edit Record"),
				"Edit contact methods popup title is wrong");
		softAssert.assertTrue(editContactMethodsTypes.getText().equals("Contact Method Types"),
				"Edit contact methods Types label text is wrong");
		editContactMethodsDropDown.click();
		Thread.sleep(2000);
		editContactMethodsMobile.click();
		Thread.sleep(2000);
		editContactMethodsDropDown.click();
		Thread.sleep(2000);
		editContactMethodsMobile.click();
		Thread.sleep(2000);
		softAssert.assertTrue(editContactMethodsValueLabel.getText().equals("Value"));
		softAssert.assertAll();
		editContactMethodsValueInput.click();
		editContactMethodsValueInput.clear();
		editContactMethodsValueInput.sendKeys(value);
		saveEditContactButton.click();
		Thread.sleep(2000);

	}

	public void verifyFirstContactMethodsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("Contact method is not deleted", i == 0);

	}

	public void editOrganizationsTypeGovernment(String value) throws InterruptedException {

		Thread.sleep(2000);
		Assert.assertTrue("Edit record title is wrond", editOrganizationTitle.getText().equals("Edit Record"));
		organizationTypeDropDown.click();
		organizationTypeGovernment.click();
		Thread.sleep(2000);
		organizationTypeDropDown.click();
		organizationTypeGovernment.click();
		Thread.sleep(2000);
		editOrganizationValueInput.click();
		editOrganizationValueInput.clear();
		editOrganizationValueInput.sendKeys(value);
		Thread.sleep(2000);
		editOrganizationSaveButton.click();

	}

	public void verifyFirstOrganizationDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[1]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("The organization is not deleted", i == 0);

	}

	public void verifySecondOrganizationIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]"))
				.size();
		Assert.assertTrue("The organization is not deleted", i == 0);

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
		editInsertedLink.click();
		Thread.sleep(2000);
		detailsLinkTextToDisplay.click();
		detailsLinkTextToDisplay.clear();
		detailsLinkTextToDisplay.sendKeys("ArkCase1");
		detailsLinkInsertButton.click();
	}

	public void insertPicture(String file) throws InterruptedException, IOException, AWTException {

		insertPicture.click();
		Thread.sleep(2000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(picturePopUpTitle.getText().equals("Insert Image"), "Insert image title is not corect");
		softAssert.assertTrue(browsePictureButton.isDisplayed(), "Browse picture button is not displayed");
		softAssert.assertFalse(insertImageButton.isEnabled(), "Insert image button should be disabled");
		softAssert.assertAll();
		browsePictureButton.click();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPdf();
		Thread.sleep(2000);
		detailsSaveButton.click();
		Thread.sleep(3000);
		Assert.assertTrue(caseDetailsSavedPopup.getText().equals("Case details saved"));

	}

	public void verifyInsertedImage() {

		Assert.assertTrue(insertedImage.isDisplayed());
	}

	public void deleteInsertedImage() throws InterruptedException {

		insertedImage.click();
		deleteImageIcon.click();
		detailsSaveButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(detailsText.getText().isEmpty());

	}

	public void verifyEditAddressTitle() {

		Assert.assertEquals("Edit Record", editAddressTitle.getText());

	}

	public void editAddress(String street, String city, String state, String zip, String country)
			throws InterruptedException {

		editAddressTypeDropDown.click();
		Thread.sleep(2000);
		addressTypeHome.click();
		editAddressTypeDropDown.click();
		Thread.sleep(2000);
		addressTypeHome.click();
		addressStreetInput.click();
		addressStreetInput.clear();
		addressStreetInput.sendKeys(street);
		addressCityInput.click();
		addressCityInput.clear();
		addressCityInput.sendKeys(city);
		addressStateInput.click();
		addressStateInput.clear();
		addressStateInput.sendKeys(state);
		addressZipInput.click();
		addressZipInput.clear();
		addressZipInput.sendKeys(zip);
		addressCountryInput.click();
		addressCountryInput.clear();
		addressCountryInput.sendKeys(country);
		Thread.sleep(2000);
		editAddressSaveButton.click();
		Thread.sleep(3000);
	}

	public void editSecondContactMethods(String value) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(editContactMethodsTitle.getText().equals("Edit Record"),
				"Edit contact methods popup title is wrong");
		softAssert.assertTrue(editContactMethodsTypes.getText().equals("Contact Method Types"),
				"Edit contact methods Types label text is wrong");
		editContactMethodsDropDown.click();
		Thread.sleep(2000);
		editContactMethodsFacebook.click();
		Thread.sleep(2000);
		editContactMethodsDropDown.click();
		Thread.sleep(2000);
		editContactMethodsFacebook.click();
		Thread.sleep(2000);
		softAssert.assertTrue(editContactMethodsValueLabel.getText().equals("Value"));
		softAssert.assertAll();
		editContactMethodsValueInput.click();
		editContactMethodsValueInput.clear();
		editContactMethodsValueInput.sendKeys(value);
		saveEditContactButton.click();
		Thread.sleep(2000);

	}

	public void verifySecondContactMethodIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[2]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("Contact method is not deleted", i == 0);

	}

	public void verifyAddPersonTitle() {

		Assert.assertEquals("Add person title is wrong", "Add Person", addPersonTitle.getText());

	}

	public void verifyIfPersonIsAdded() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[2]"))
				.size();
		Assert.assertTrue("Person is not added", i != 0);

	}

	public void verifyAddedPerson(String type, String firstName, String LastName) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertTrue(thirdContactMethodsIcon.isDisplayed(),
				"Third row, people section, contact methods icon is not displayed");
		softAssert.assertTrue(thirdOrganizationsIcon.isDisplayed(),
				"Tird row, people sectin, organizations icon is not displayed");
		softAssert.assertTrue(thirdAddressIcon.isDisplayed(),
				"Third row, people section, address icon is not displayed");
		softAssert.assertTrue(thirdAliasesIcon.isDisplayed(),
				"Third row, people section, aliases icon is not displayed");
		softAssert.assertEquals(thirdTypeText.getText(), type, "Third row, people section, people type name is wrong");
		softAssert.assertEquals(thirdFirstNameText.getText(), firstName,
				"Third row, people section, people first name is wrong");
		softAssert.assertEquals(thirdLastNameText.getText(), LastName,
				"Third row, people section, people last name is wrong");
		softAssert.assertTrue(thirdEditPeopleButton.isDisplayed(),
				"Third row, people section, edit button is not displayed");
		softAssert.assertTrue(thirdDeletePeopleButton.isDisplayed(),
				"Third row, people section, delete button is not displayed");
		softAssert.assertAll();
	}

	public void verifyIfAddedPersonIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div/div[2]"))
				.size();
		Assert.assertTrue("Added person is not deleted", i == 0);

	}

	public void addPersonTypeVictim(String firstName, String lastName) throws InterruptedException {

		personTypesDropDown.click();
		Thread.sleep(3000);
		personTypesVictim.click();
		Thread.sleep(2000);
		personfirstNameInput.click();
		personfirstNameInput.clear();
		personfirstNameInput.sendKeys(firstName);
		personlastNameInput.click();
		personlastNameInput.clear();
		personlastNameInput.sendKeys(lastName);
		Thread.sleep(2000);
		editRecordSaveButton.click();
		Thread.sleep(3000);

	}

	public void addContactMethodsMobile(String value) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();

		softAssert.assertTrue(editContactMethodsTypes.getText().equals("Contact Method Types"),
				"Edit contact methods Types label text is wrong");
		editContactMethodsDropDown.click();
		Thread.sleep(2000);
		editContactMethodsMobile.click();
		Thread.sleep(2000);
		editContactMethodsDropDown.click();
		Thread.sleep(2000);
		editContactMethodsMobile.click();
		Thread.sleep(2000);
		softAssert.assertTrue(editContactMethodsValueLabel.getText().equals("Value"));
		softAssert.assertAll();
		editContactMethodsValueInput.click();
		editContactMethodsValueInput.clear();
		editContactMethodsValueInput.sendKeys(value);
		saveEditContactButton.click();
		Thread.sleep(2000);

	}

	public void verifyAdedThirdContactMethod(String type, String value, String user) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(thirdContactMethodsTypeText.getText(), type,
				"Third contact methods table, added contact methods type is wrong");
		softAssert.assertEquals(thirdContactMethodsValueText.getText(), value,
				"Third contact methods table, added contact methods value is wrong");
		softAssert.assertEquals(thirdContactMethodDateAddedText.getText(), createdDate,
				"Third contact methods table, added contact methods date is wrong");
		softAssert.assertEquals(thirdContactMethodAddedBy.getText(), user,
				"Third contact methods tbale, added contact method adede by is wrong");
		softAssert.assertAll();

	}

	public void verifyThirdContactMethodIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[1]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]"))
				.size();

		Assert.assertTrue("Add record in third contact methods is not deleted", i == 0);

	}

	public void addOrganization(String value) throws InterruptedException {

		Thread.sleep(2000);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(addOrganizationTitle.getText(), "Add Organization",
				"Add organization title name is wrong");
		organizationTypeDropDown.click();
		organizationTypeGovernment.click();
		Thread.sleep(2000);
		organizationTypeDropDown.click();
		organizationTypeGovernment.click();
		Thread.sleep(2000);
		editOrganizationValueInput.click();
		editOrganizationValueInput.clear();
		editOrganizationValueInput.sendKeys(value);
		Thread.sleep(2000);
		editOrganizationSaveButton.click();

	}

	public void verifyThirdAddedOrganization(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(thirdOrganizationTypeText.getText(), type, "Organizaation type text is wrong");
		softAssert.assertEquals(thirdOrganizationValueText.getText(), value, "Organization value text is wrong");
		softAssert.assertEquals(thirdOrganizationDateAddedText.getText(), createdDate,
				"Organization date added text is wrong");
		softAssert.assertEquals(thirdOrganizationAddedByText.getText(), addedBy, "Organization added by text is wrong");
		softAssert.assertAll();

	}

	public void verifyThirdAddedOrganizationIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]"))
				.size();
		Assert.assertTrue("Added organization is not deleted", i == 0);

	}

	public void addAddress(String street, String city, String state, String zip, String country)
			throws InterruptedException {

		editAddressTypeDropDown.click();
		Thread.sleep(2000);
		addressTypeHome.click();
		editAddressTypeDropDown.click();
		Thread.sleep(2000);
		addressTypeHome.click();
		addressStreetInput.click();
		addressStreetInput.clear();
		addressStreetInput.sendKeys(street);
		addressCityInput.click();
		addressCityInput.clear();
		addressCityInput.sendKeys(city);
		addressStateInput.click();
		addressStateInput.clear();
		addressStateInput.sendKeys(state);
		addressZipInput.click();
		addressZipInput.clear();
		addressZipInput.sendKeys(zip);
		addressCountryInput.click();
		addressCountryInput.clear();
		addressCountryInput.sendKeys(country);
		Thread.sleep(2000);
		editAddressSaveButton.click();
		Thread.sleep(3000);
	}

	public void verifyThirdAddedAddress(String type, String street, String city, String state, String zip,
			String country, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(thirdAddressTypeText.getText(), type, "Third added address type text is wrong");
		softAssert.assertEquals(thirdAddressText.getText(), street, "Third added address address text is wrong");
		softAssert.assertEquals(thirdAddressCityText.getText(), city, "Third added address city text is wrong");
		softAssert.assertEquals(thirdAddressStateText.getText(), state, "Third added address state text is wrong");
		softAssert.assertEquals(thirdAddressZipText.getText(), zip, "Third added address zip  text is wrong");
		softAssert.assertEquals(thirdAddressCountryText.getText(), country,
				"Third added address country text is wrong");
		softAssert.assertEquals(thirdAddedDateText.getText(), createdDate,
				"Third added address date Added text is wrong");
		softAssert.assertEquals(thirdAddedByText.getText(), addedBy, "Third added addredd added by text is wrong");
		softAssert.assertAll();
	}

	public void verifyThirdAddedAddressIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[3]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]"))
				.size();
		Assert.assertTrue("The address is not deleted", i == 0);
	}

	public void verifyThirdAddedAlias(String type, String value, String addedBy) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(thirdAliasTypeText.getText(), type, "Aliases type text is wrong");
		softAssert.assertEquals(thirdAliasValueText.getText(), value, "Aliases value text is wrong");
		softAssert.assertEquals(thirdAliasDateAddedText.getText(), createdDate, "Aliases created date is wrong");
		softAssert.assertEquals(thirdAliasAddedByText.getText(), addedBy, "Aliases added by is wrong");
		softAssert.assertAll();

	}

	public void verifyThirdAddedAliasesIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div[3]/div[2]/div[4]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]"))
				.size();
		Assert.assertTrue("Alias is not deleted", i == 0);

	}

	public void checkPeopleTypeFilter(String typeFilter) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		peopleTypeFilter.click();
		peopleTypeFilter.sendKeys(typeFilter);
		Thread.sleep(1000);
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div/div/div[2]"))
				.size();
		softAssert.assertTrue(i == 0, "The people type filter is not working");
		peopleTypeFilter.clear();
		softAssert.assertAll();

	}

	public void checkPeopleFirstNameFilter(String firstNameFilter) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		peopleFirstNameFilter.click();
		peopleFirstNameFilter.sendKeys(firstNameFilter);
		Thread.sleep(1000);
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div/div/div[2]"))
				.size();
		softAssert.assertTrue(i == 0, "The people first name filter is not working");
		peopleFirstNameFilter.clear();
		softAssert.assertAll();

	}

	public void checkPeopleLastNameFilter(String lastNameFilter) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		peopleLastNameFilter.click();
		peopleLastNameFilter.sendKeys(lastNameFilter);
		Thread.sleep(1000);
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-people/div/div[2]/div/div[1]/div[2]/div[2]/div/div/div/div[2]"))
				.size();
		softAssert.assertTrue(i == 0, "The people last name filter is not working");
		peopleLastNameFilter.clear();
		softAssert.assertAll();

	}

	public void verifyNotesTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(notesTitleTable.getText(), "Notes", "Notes table title is wrong");
		softAssert.assertEquals(notesNoteColumnName.getText(), "Note", "Note column name in Notes table is wrong");
		softAssert.assertEquals(notesCreatedColumnName.getText(), "Created",
				"Credated column name in Notes table is wrong");
		softAssert.assertEquals(notesAuthorColumnName.getText(), "Author", "Author column name is Notes is wrong");
		softAssert.assertTrue(addNewNoteButton.isDisplayed(), "Add new note button is not displayed");
		softAssert.assertAll();

	}

	public void addNote(String note) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(addNoteTitle.getText(), "Add Note", "Add note title is wrong");
		softAssert.assertFalse(addNoteButton.isEnabled(), "Add note button it should nt be enabled without note text");
		softAssert.assertAll();
		noteInput.click();
		noteInput.sendKeys(note);
		Thread.sleep(2000);
		addNoteButton.click();
	}

	public void verifyAddedNote(String note, String author) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(notesNote.getText(), note, "Added note name is wrong");
		softAssert.assertEquals(notesCreated.getText(), createdDate, "Added note created date is wrong");
		softAssert.assertEquals(notesAuthor.getText(), author, "Added note author is wrong");
		softAssert.assertTrue(editNoteButton.isDisplayed(), "Edit note button is not displayed");
		softAssert.assertTrue(deleteNoteButton.isDisplayed(), "Delete note button is not displayed");
		softAssert.assertAll();
	}

	public void verifyIfNoteIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/core-notes/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("The note is not deletd", i == 0);

	}

	public void editNote(String note) throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(editNoteTitle.getText(), "Edit Record", "Edit note title is wrong");
		softAssert.assertAll();
		noteInput.click();
		noteInput.clear();
		noteInput.sendKeys(note);
		Thread.sleep(2000);
		addNoteButton.click();

	}

	public void verifyTaskTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(taskTableTitle.getText(), "Tasks", "Task table title is wrong");
		softAssert.assertTrue(taskAddButton.isDisplayed(), "Add new task button is not displayed");
		softAssert.assertEquals(taskTitleColumn.getText(), "Title", "Task title column name is wrong");
		softAssert.assertEquals(assigneeCoumnName.getText(), "Assignee", "Assignee column name is wrong");
		softAssert.assertEquals(createdColumnName.getText(), "Created", "Created column name is wrong");
		softAssert.assertEquals(priorityColumnName.getText(), "Priority", "Priority column name is wrong");
		softAssert.assertEquals(dueColumnName.getText(), "Due", "Due column name is wrong");
		softAssert.assertEquals(statusColumnName.getText(), "Status", "Status column name si wrong");
		softAssert.assertEquals(actionColumnName.getText(), "Action", "Action column name is wrong");
		softAssert.assertAll();

	}

	public void verifyCaseWIthAddedTask(String asignTo, String type, String group, String priority)
			throws InterruptedException {

		SoftAssert softAssert = new SoftAssert();
		refreshButtonInTaskPage.click();
		Thread.sleep(3000);

		softAssert.assertEquals(caseTitleInTasks.getText(), createdCaseTitle.getText(),
				"Case title in task page is not the same as the created case");
		softAssert.assertEquals(caseIdInTaskPage.getText(), caseId.getText(),
				"Case ID in the task page is not the same as the created case");
		softAssert.assertTrue(signButtonInTaskPage.isDisplayed(), "Sign button is not displayed");
		softAssert.assertTrue(subscribeButtonInTaskPage.isDisplayed(), "Subscribe button is not displayed");
		softAssert.assertTrue(deleteButtonInTaskPage.isDisplayed(), "Delete button is not displayed ");
		softAssert.assertTrue(completeButtonInTaskPage.isDisplayed(), "Complete button is not displayed");
		softAssert.assertEquals(assignToInTheTaskPage.getText(), asignTo, "Asign to user is wrong ");
		softAssert.assertEquals(caseTypeInTaskPage.getText(), type, "Case type is wrong");
		softAssert.assertEquals(incidentDate.getText(), createdDateCase.getText(),
				"Incident date is not same as the one in created case");
		softAssert.assertEquals(owningGroupInTaskPage.getText(), group,
				"Owning group is not same as the one in the created case");
		softAssert.assertEquals(priorityInTaskPage.getText(), priority,
				"Priority is not the same as the one in the created case");
		softAssert.assertAll();

	}

	public void verifyAddedTaskInCase(String title, String percent, String asignee, String dueDate, String priority,
			String state) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(taskTitle.getText(), title, "Task title is wrong");
		softAssert.assertEquals(percentCompletition.getText(), percent, "Percent of completition is wrong");
		softAssert.assertEquals(startDate.getText(), createdDate, "Created date is wrong");
		softAssert.assertEquals(assignee.getText(), asignee, "Assignee name is wrong");
		softAssert.assertEquals(dueDateTask.getText(), dueDate, "Due date is wrong");
		softAssert.assertEquals(priorityTask.getText(), priority, "Priority is wrong");
		softAssert.assertEquals(stateTask.getText(), state, "State of the Task is wrong");
		softAssert.assertAll();
	}

	public void verifyTaskInTheTaskTable(String title, String assignee, String priority, String dueDate,
			String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(titleField.getText(), title, "Task title in the Task table is wrong");
		softAssert.assertEquals(assigneeField.getText(), assignee, "Assignee name in the Tasks table is wrong");
		softAssert.assertEquals(createdField.getText(), createdDate, "Created date in Task table is wrong");
		softAssert.assertEquals(priorityFiled.getText(), priority, "Priority in the Task table is wrong");
		softAssert.assertEquals(dueField.getText(), dueDate, "Due date in the Tasks table is wrong");
		softAssert.assertEquals(statusField.getText(), status, "Status in the Tasks table is wrong");
		softAssert.assertEquals(actionField.getText(), "", "Action in the Tasks table is wrong");

	}

	public void checkMedicalReleaseHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void checkChairmanResponseHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void checkClearanceDeniedHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void checkCleranceGrantedHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void checkGeneralReleaseHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void checkInterviewRequestHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void checkNoticeOfInvestigationHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void verifyReferenceTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(referencesTableTitle.getText(), "References", "Reference table title is wrong");
		softAssert.assertEquals(referenceNumberColumnName.getText(), "Number", "Reference number column name is wrong");
		softAssert.assertEquals(referenceModifiedColumnName.getText(), "Modified",
				"Reference modified column name is wrong");
		softAssert.assertEquals(referenceTypeColumnName.getText(), "Reference Type",
				"Reference type column name is wrong");
		softAssert.assertEquals(referenceStatusColumnName.getText(), "Status", "Reference status column name is wrong");
		softAssert.assertTrue(addReferenceButton.isDisplayed(), "Add reference button is not displayed");
		softAssert.assertAll();
	}

	public void AddReferenceInput(String reference) {

		searchReferenceInput.click();
		searchReferenceInput.sendKeys(reference);

	}

	public void verifySearchedReference(String name, String type, String title) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedReferenceName.getText(), name,
				"Searched reference name is wrong, Shouldn't be able to add a reference for the same case you are referencing.");
		softAssert.assertEquals(searchedreferenceType.getText(), type,
				"Searched reference type is wrong, Shouldn't be able to add a reference for the same case you are referencing.");
		softAssert.assertEquals(searchedReferenceTitle.getText(), title,
				"Searched reference title is wrong,Shouldn't be able to add a reference for the same case you are referencing.");
		softAssert.assertAll();

	}

	public void verifyAddedReference(String number, String title, String type, String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(referenceNumberField.getText(), number, "Added Reference number is wrong");
		softAssert.assertEquals(referenceTitleField.getText(), title, "Added Reference title is wrong");
		softAssert.assertEquals(referenceModifiedField.getText(), createdDate,
				"Added reference modified date is wrong");
		softAssert.assertEquals(referenceTypeField.getText(), type, "Added reference type is wrong");
		softAssert.assertEquals(referenceStatusField.getText(), status, "Added reference status is wrong");
		softAssert.assertAll();

	}

	public void checkReferenceTitleHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/a");

	}

	public void checkReferenceNumberHttpResponse() throws IOException {

		HttpResponseCode responseCode = new HttpResponseCode();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/a");

	}

	public void searchCasesInput(String name) {

		casesSearchInput.click();
		casesSearchInput.sendKeys(name);

	}

	public void searchCasesButtonClick() {

		casesGoButton.click();
	}

	public void verifySecondAddedReference(String number, String title, String type, String status) {

		SoftAssert softAssert = new SoftAssert();
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		softAssert.assertEquals(secondRowReferenceNumber.getText(), number,
				"Should not be able same case to be added more then once");
		softAssert.assertEquals(secondRowReferenceTitle.getText(), title,
				"Should not be able same case to be added more then once");
		softAssert.assertEquals(secondRowReferenceModified.getText(), createdDate,
				"Should not be able same case to be added more then once");
		softAssert.assertEquals(secondRowReferenceType.getText(), type,
				"Should not be able same case to be added more then once");
		softAssert.assertEquals(secondRowReferenceStatus.getText(), status,
				"Should not be able same case to be added more then once");

		softAssert.assertAll();

	}

	public void clickPriority() {

		priority.click();

	}

	public void clickPriorityDropDown() {
		priorityDropDown.click();
	}

	public void clickPriorityLow() {

		priorityLow.click();

	}

	public void clickPriorityConfirmBtn() {

		priorityConfirmBtn.click();

	}

	public void clickPriorityHigh() {

		priorityHigh.click();
	}

	public void clickPriorityExpedite() {

		priorityExpedite.click();
	}

	public void assignedToDropDownClick() {

		assignedToDropDown.click();
	}

	public void assignedToSelectAnnAdministrator() {

		assignedToAnn.click();
	}

	public void assignedToConfirmButtonClick() {

		assignedToConfirmBtn.click();

	}

	public void caseTitleInput(String name) throws InterruptedException {

		createdCaseTitle.click();
		Thread.sleep(3000);
		caseTitleInput.click();
		caseTitleInput.clear();
		caseTitleInput.sendKeys(name);

	}

	public void clickCaseTitleConfirmButton() {

		caseTitleConfirmBtn.click();
	}

	public void clickCaseTypeDropDown() {

		caseTypeDropDown.click();
	}

	public void selectCaseTypeDrugTrafficking() {

		caseTypeDrugTrafficking.click();

	}

	public void clickCaseTypeConfirmButton() {

		editCaseTypeConfirmBtn.click();
	}

	public void clickCaseOwningGroupDropDown() {

		caseOwningGroupDropDown.click();

	}

	public void selectOwningGroupACM_SUPERVISOR_DEV() {

		ownigGroupACM_SUPERVISOR_DEV.click();
	}

	public void clickOwningGroupConfirmBtn() {

		owningGroupConfirmBtn.click();
	}

	public void clickChnageCaseStatusAddFilesBtn() {

		chnageCaseStatusAddFilesBtn.click();
	}

	public void clickChangeCaseStatusBrowseBtn() {
		changeCaseStatusBrowseBtn.click();
	}

	public void clickChangeCaseStatusUploadBtn() {
		changeCaseStatusUploadBtn.click();
	}

	public void verifyMergePopUpTitle() {
		Assert.assertEquals("Title of merge popup form is wrong", "Merge", mergePopUpTitle.getText());
	}

	public void searchForCase(String name) {

		mergeSearchForCaseInput.click();
		mergeSearchForCaseInput.sendKeys(name);

	}

	public void clickSearchCaseBtn() {
		mergeSearchBtn.click();
	}

	public void verifySearchedCaseForMerge(String name, String type, String title, String parent, String assignee) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedCaseName.getText(), name, "Searched case name is wrong");
		softAssert.assertEquals(searchedCaseType.getText(), type, "Searched case type is wrong");
		softAssert.assertEquals(searchedCaseTitle.getText(), title, "Searched case title is wrong");
		softAssert.assertEquals(searchedCaseParent.getText(), parent, "Searched case parent is wrong");
		softAssert.assertEquals(searchedCaseAssignee.getText(), assignee, "Searched case assignee is wrong");
		softAssert.assertAll();

	}

	public void clickCancelMergeBtn() {
		mergeCancelBtn.click();
	}

	public void clickMergeBtn() {
		mergeBtn.click();
	}

	public void verifyIfSearchedCaseIsShown() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[5]/div/div/search-modal/div[2]/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div"))
				.size();
		Assert.assertTrue("The case it self should not appear to be merged", i == 0);

	}

	public void verifyAlertText() throws AWTException, InterruptedException {

		Alert alert = driver.switchTo().alert();
		Thread.sleep(1000);
		driver.switchTo().alert();
		Assert.assertEquals("Alert message is wrong", "Case can't be merged.", alert.getText());
		alert.accept();
		Thread.sleep(3000);
		driver.switchTo().defaultContent();
	}

	public void verifyIfCasesAreMerged() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Case is not merged, is not shown in the documents table", i > 0);

	}

	public void changeCaseStatusAprovedWithROI() throws InterruptedException, IOException {
		HttpResponseCode responseCode = new HttpResponseCode();
		caseTasks.click();
		Thread.sleep(6000);
		refreshPage.click();
		Thread.sleep(10000);
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]"))
				.size();
		Assert.assertTrue("Created task for deleting the case is not shown", i > 0);
		taskTitleColumn.click();
		Thread.sleep(5000);
		Assert.assertEquals(
				"Review request to change task name is wrong or automated task for deleting case is not created",
				"Review Request to Change Case Status" + " '" + caseId.getText() + "'",
				ReviewREquestToChange.getText());
		ReviewREquestToChange.click();
		responseCode.checkHttpResponse(
				"/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]");
		Thread.sleep(10000);
		approveDocumenButton.click();
		Thread.sleep(2000);
		Assert.assertTrue(reviewRequestState.getText().equals("CLOSED"));

	}

	public void verifySearchedReferenceNoResult() {
		Assert.assertEquals("Searched result name is wrong", "No Results", searchedRefNoResult.getText());
	}
}
