package com.armedia.arkcase.uitests.cases.documents;

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
import com.armedia.arkcase.uitests.base.WaitHelper;
import java.util.List;

public class CaseDocumentsPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement documentsTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[3]")
	WebElement titleColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[4]")
	WebElement typeColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[5]")
	WebElement cretedColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[7]")
	WebElement authorColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[8]")
	WebElement versionColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[9]")
	WebElement statusColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[6]")
	WebElement modifiedColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement refreshTableButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[1]/td[3]/span/span[1]")
	WebElement rootExpander;
	// first row
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span")
	public WebElement firstDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[4]")
	WebElement firstDocumentType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[5]")
	WebElement firstDocumentCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]")
	WebElement firstDocumentModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[7]")
	WebElement firstDocumentAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]")
	WebElement firstDocumentVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[9]")
	WebElement firstDocumentStatus;
	// second row

	@FindBy(how = How.XPATH, using = "//*[@title='imageprofile']")
	public WebElement secondDocumentTitle;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[4]")
	WebElement secondDocumentExtension;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[5]")
	WebElement secondDocumentType;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[6]")
	WebElement secondDocumentCreated;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[7]")
	WebElement secondDocumentModified;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[8]")
	WebElement secondDocumentAuthor;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[9]")
	WebElement secondDocumentVersion;
	@FindBy(how = How.XPATH, using = "//*[@class='fancytree-lastsib fancytree-exp-nl fancytree-ico-c']/td[10]")
	WebElement secondDocumentStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[9]")
	WebElement clearCachButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[3]/span")
	public WebElement chnageCaseStatusButton;
	@FindBy(how = How.XPATH, using = ".//tr[@class='fancytree-folder fancytree-has-children fancytree-lastsib fancytree-lazy fancytree-exp-cdl fancytree-ico-cf']/td[3]")
	WebElement root;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement newDocument;
	@FindBy(how = How.XPATH, using = "//li[@data-command='file/Other']")
	WebElement documentOther;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[7]")
	WebElement documentWitnessInterview;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[6]")
	WebElement documentNoticeOfInvestigation;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[5]")
	WebElement documentSF86Signature;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[4]")
	WebElement documentEDelivery;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[3]")
	WebElement documentGeneralRelease;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[2]")
	WebElement documentMedicalRelease;
	// report of investigation

	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[1]")
	WebElement documentReportOfInvestigation;
	@FindBy(how = How.XPATH, using = "/html/body/iframe")
	public WebElement roiFirstIframe;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[1]/div[1]/input")
	WebElement reportTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[3]/div[1]/input")
	WebElement firstName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[1]/div[2]/div[4]/div[1]/input")
	WebElement lastName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[3]/div[2]/div/div[2]/table/tbody/tr/td[5]")
	WebElement selectApprover;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/input")
	WebElement addUserInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/header/div/div/div/span/button")
	WebElement goButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[2]/div/div[2]/section/div/div/table/tbody/tr/td[2]/a")
	WebElement searchedName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/div/div/div/div/div[3]/button[2]")
	WebElement addButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/div/table/tbody/tr/td/div/form/div[2]/div/div/div[7]/div/input")
	WebElement submitButton;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[14]")
	WebElement deleteDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[16]")
	WebElement downloadDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[5]")
	WebElement checkoutDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[6]")
	WebElement checkInDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[9]")
	WebElement refreshCase;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]/span/select/option[2]")
	WebElement version2;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[7]")
	WebElement cancelEditing;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[13]")
	WebElement renameDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]/input")
	WebElement firstDocumentRenameInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]/input")
	WebElement secondDocumentRenameInput;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[17]")
	WebElement replaceDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[1]")
	WebElement newFolder;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]/input")
	WebElement newFolderInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]")
	WebElement newFolderTitle;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]")
	WebElement deleteFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[8]")
	WebElement renameFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[10]")
	WebElement copyDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[6]")
	WebElement pasteInFolder;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[9]")
	WebElement cutDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement editWithWord;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[18]")
	WebElement declareAsRecordDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[1]")
	WebElement recordOpen;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement recordEmail;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[4]")
	WebElement recordCopy;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[6]")
	WebElement recordDownload;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]")
	WebElement emailDocument;
	// third row in documents table
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[3]/span")
	WebElement rowThreeDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[4]")
	WebElement rowThreeDocumentType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[5]")
	WebElement rowThreeDocumentCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[6]")
	WebElement rowThreeDocumentAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[7]")
	WebElement rowThreeVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[8]")
	WebElement rowThreeStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[4]/td[3]/span/input")
	WebElement rowThreeDocumentTitleInput;
	// fourth row in documents table

	// checkin document popup

	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3")
	WebElement chekInDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/div/button")
	WebElement chooseFiles;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/div/ul/li")
	WebElement selectedFiles;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement checkinBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span")
	WebElement lockedIcon;

	// modified file

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span")
	WebElement modifiedRowTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[4]")
	WebElement modifiedRowType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[5]")
	WebElement modifiedRowCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]")
	WebElement modifiedRowModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[7]")
	WebElement modfiedRowAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]/span/select/option[2]")
	WebElement modifiedRowVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[9]")
	WebElement modifiedRowStatus;

	// email popup form
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[1]/h3")
	WebElement emailPopupTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[1]/div/input")
	WebElement emailSearchInput;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[1]/div/span/button")
	WebElement emailSearchBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[1]/div")
	WebElement searchedUserName;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[2]/search-modal/div/div/div[2]/div[2]/div/div[1]/div[1]/div[2]/div/div/div/div[2]/div")
	WebElement searchedUserEmail;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/textarea")
	WebElement recipientArea;
	@FindBy(how = How.XPATH, using = "/html/body/div[5]/div/div/div[3]/button[2]")
	WebElement sendEmailBtn;
	// Correspondende
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]")
	WebElement NewCorrespondence;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]/ul/li[6]")
	WebElement corresponceWitnessInterview;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]/ul/li[5]")
	WebElement correspondenceNoticeOfInvestigation;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]/ul/li[4]")
	WebElement correspondenceClearanceDenide;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]/ul/li[3]")
	WebElement correspondenceClearanceGranted;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]/ul/li[2]")
	WebElement correspondenceMedicalRelease;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[3]/ul/li[1]")
	WebElement correspondenceGeneralRelease;
	
	public CaseDocumentsPage verifyDocumentsTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(documentsTableTitle.getText(), "Documents", "Documents table title is wrong");
		softAssert.assertEquals(titleColumnName.getText(), "Title", "Documents title column name is wrong");
		softAssert.assertEquals(typeColumnName.getText(), "Type", "Documents type column name is wrong");
		softAssert.assertEquals(cretedColumnName.getText(), "Created", "Documents created column name is wrong");
		softAssert.assertEquals(authorColumnName.getText(), "Author", "Documents author column name is wrong");
		softAssert.assertEquals(versionColumnName.getText(), "Version", "Documents version column name is wrong");
		softAssert.assertEquals(statusColumnName.getText(), "Status", "Documents status column name is wrong");
		softAssert.assertEquals(modifiedColumnName.getText(), "Modified", "Documents modified column name is wrong");
		softAssert.assertTrue(refreshTableButton.isDisplayed(), "Documents refresh button is not displayed");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage clickRootExpander() {
		rootExpander.click();
		return this;
	}

	public CaseDocumentsPage verifyFirstDocument(String title, String type, String author, String version,
			String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(firstDocumentTitle.getText(), title, "First Document title is wrong");
		softAssert.assertEquals(firstDocumentType.getText(), type, "First Document type is wrong");
		softAssert.assertEquals(firstDocumentCreated.getText(), createdDate, "First Document crated date is wrong");
		softAssert.assertEquals(firstDocumentModified.getText(), createdDate, "First Document modified date is wrong");
		softAssert.assertEquals(firstDocumentAuthor.getText(), author, "First Document author is wrong");
		softAssert.assertEquals(firstDocumentVersion.getText(), version, "First Document version is wrong");
		softAssert.assertEquals(firstDocumentStatus.getText(), status, "FIrst Document status is wrong");
		softAssert.assertAll();
		return this;
	}

	public CaseDocumentsPage verifySecondDocument(String title, String extension, String type, String version, String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		Assert.assertTrue("The doc is not uploaded succesfully", pictureUploaded());
		SoftAssert softAssert = new SoftAssert();		
		softAssert.assertEquals(readSecondDocumenttTitle(), title, "Second Document title is wrong");
		softAssert.assertEquals(readSecondDocumentExtension(), extension, "Second Document extension is wrong");
		softAssert.assertEquals(readSecondDocumentType(), type, "Second Document type is wrong");
		softAssert.assertEquals(readSecondDocumentCreated(), createdDate, "Second Document created date is wrong");
		softAssert.assertEquals(readSecondDocumentModified(), createdDate,
				"Second document modified date is wrong");
		softAssert.assertEquals(readSecondDocumentAuthor(), "Samuel Supervisor", "Second Docuemnt author is wrong");
		softAssert.assertEquals(readSecondDocVersion(), version, "Second Document version is worng");
		softAssert.assertEquals(readSecondDocStatus(), status, "Second Document status is wrong");
		softAssert.assertAll();
		return this;

	}
	
	public String readSecondDocumenttTitle(){
		WaitHelper.waitForElement(secondDocumentTitle, driver);
		String secondDocTitle = secondDocumentTitle.getText();
		return secondDocTitle;
	}
	
	public String readSecondDocumentExtension(){
		WaitHelper.waitForElement(secondDocumentExtension, driver);
		String secondDocExtension = secondDocumentExtension.getText();
		return secondDocExtension;
	}
	
	public String readSecondDocumentType(){
		WaitHelper.waitForElement(secondDocumentType, driver);
		String secondDocType = secondDocumentType.getText();
		return secondDocType;
	}
	
	public String readSecondDocumentCreated(){
		WaitHelper.waitForElement(secondDocumentCreated, driver);
		String secondDocCreated = secondDocumentCreated.getText();
		return secondDocCreated;
	}
	
	public String readSecondDocumentModified(){
		WaitHelper.waitForElement(secondDocumentModified, driver);
		String secondDocModified = secondDocumentModified.getText();
	    return secondDocModified;	
	}
	
	public String readSecondDocumentAuthor(){
	    String secondDocAuthor = secondDocumentAuthor.getText();
	    return secondDocAuthor;
	}
	
	public String readSecondDocVersion(){
		WaitHelper.waitForElement(secondDocumentVersion, driver);
		String secondDocVersion = secondDocumentVersion.getText();
		return secondDocVersion;
	}
	
	public String readSecondDocStatus(){
		String secondDocStatus = secondDocumentStatus.getText();
		return secondDocStatus;
	}	

	public CaseDocumentsPage performRightClickOnRoot() {
		WaitHelper.waitForElement(root, driver);
		Actions actions = new Actions(driver);		
		Action action = actions.contextClick(root).build();
		action.perform();
		return this;
	}

	public CaseDocumentsPage newDocumentClick() {
		newDocument.click();
		return this;
	}

	public CaseDocumentsPage checkIfRightClickOnRootIsWorking() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[2]")).size();
		Assert.assertTrue("Right Click on root is not working", i > 0);
		return this;

	}

	public CaseDocumentsPage verifyNewDocmentName() {
		Assert.assertEquals("New Document name is wrong", "New Document", newDocument.getText());
		return this;
	}

	public CaseDocumentsPage clickDocumentOther() {
        WaitHelper.waitForElement(documentOther, driver);
		documentOther.click();
		return this;

	}

	public CaseDocumentsPage verifyDocumentOtherName() {
		Assert.assertEquals("Document Other name is wrong", "Other", documentOther.getText());
		return this;
	}

	public CaseDocumentsPage verifydocumentWitnessInterviewName() {

		Assert.assertEquals("Document Witness Interview Request name is wrong", "Witness Interview Request",
				documentWitnessInterview.getText());
		return this;
	}

	public CaseDocumentsPage clickDocumentWitnessInterview() {
		documentWitnessInterview.click();
		return this;
	}

	public CaseDocumentsPage verifyDocumentNoticeOfInvestigationName() {
		Assert.assertEquals("Document Notice of Investigation name is wrong", "Notice of Investigation",
				documentNoticeOfInvestigation.getText());
		return this;
	}

	public CaseDocumentsPage clickDocumentNoticeOfInvestigation() {
		documentNoticeOfInvestigation.click();
		return this;
	}

	public CaseDocumentsPage verifyDocumentSF86Signature() {

		Assert.assertEquals("Document SF86 Signature name is wrong", "SF86 Signature", documentSF86Signature.getText());
		return this;

	}

	public CaseDocumentsPage clickDocumentSF86Signature() {

		documentSF86Signature.click();
		return this;
	}

	public CaseDocumentsPage verifyDocumentEDelivery() {
		Assert.assertEquals("Document eDelivery name is wrong", "eDelivery", documentEDelivery.getText());
		return this;
	}

	public CaseDocumentsPage clickDocumentEDelivery() {
		documentEDelivery.click();
		return this;
	}

	public CaseDocumentsPage verifyDocumentGeneralRelease() {

		Assert.assertEquals("Document General Release name is wrong", "General Release",
				documentGeneralRelease.getText());
		return this;

	}

	public CaseDocumentsPage clickDocumentGeneralRelease() {

		documentGeneralRelease.click();
		return this;

	}

	public CaseDocumentsPage verifyDocumentMedicalRelease() {

		Assert.assertEquals("Document Medical Release name is wrong", "Medical Release",
				documentMedicalRelease.getText());
		return this;
	}

	public CaseDocumentsPage clickDocumentMedicalRelease() {

		documentMedicalRelease.click();
		return this;
	}

	public CaseDocumentsPage clickDocumentROI() {

		Assert.assertEquals("Document Report of Investigation name is wrong", "Report of Investigation",
				documentReportOfInvestigation.getText());
		documentReportOfInvestigation.click();
		return this;

	}

	public CaseDocumentsPage clickReportTitle() {

		reportTitle.click();
		return this;
	}

	public CaseDocumentsPage reportTitleInput(String title) {

		reportTitle.sendKeys(title);
		return this;
	}

	public CaseDocumentsPage clickReportFirstName() {

		firstName.click();
		return this;
	}

	public CaseDocumentsPage reportFirstNameInput(String name) {

		firstName.sendKeys(name);
		return this;
	}

	public CaseDocumentsPage clickReportLastName() {

		lastName.click();
		return this;
		
	}

	public CaseDocumentsPage reportLastNameInput(String lName) {
		lastName.sendKeys(lName);
		return this;
	}

	public CaseDocumentsPage clickSelectApprover() {

		selectApprover.click();
		return this;
	}

	public CaseDocumentsPage clickSearchForUserInput() {

		addUserInput.click();
		return this;
	}

	public CaseDocumentsPage searchForUserInput(String user) {

		addUserInput.sendKeys(user);
		return this;
	}

	public CaseDocumentsPage clickGoButton() {
		goButton.click();
		return this;
	}

	public CaseDocumentsPage clickSearchedUser() {

		Assert.assertEquals("Searched User name is wrong or it not shown", "Samuel Supervisor", searchedName.getText());
		searchedName.click();
		return this;

	}

	public CaseDocumentsPage clickAddButton() {

		addButton.click();
		return this;
	}

	public CaseDocumentsPage clickSubmitButton() {

		submitButton.click();
		return this;
	}

	public CaseDocumentsPage swithWindow() {

		for (String winHandle : driver.getWindowHandles()) {
			driver.switchTo().window(winHandle);
		}
		return this;

	}

	public CaseDocumentsPage performRighClickOnSecondDocument() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(secondDocumentTitle).build();
		action.perform();
		return this;
	}

	public CaseDocumentsPage checkIfRightClickOnSecondDocumentIsWorking() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[14]")).size();
		Assert.assertTrue("Right Click on root is not working", i > 0);
		return this;

	}

	public CaseDocumentsPage deleteDocument() {

		Assert.assertEquals("Delete document name is wrong ", "Delete", deleteDocument.getText());
		deleteDocument.click();
		return this;

	}

	public CaseDocumentsPage verifySecondDocumentIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Document is not deleted", i == 0);
		return this;
	}

	public CaseDocumentsPage downloadDocument() {

		Assert.assertEquals("Download document name is wrong ", "Download", downloadDocument.getText());
		downloadDocument.click();
		return this;

	}

	public CaseDocumentsPage checkoutDocument() {

		Assert.assertEquals("Checkout document name is wrong", "Checkout", checkoutDocument.getText());
		checkoutDocument.click();
		return this;

	}

	public CaseDocumentsPage verifyLockedDocument() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After the upload, The document is not locked", i > 0);
		return this;
	}

	public CaseDocumentsPage verifyLockedDocumentAfterCheckout() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After checkout is clicked, The document is not locked", i > 0);
		return this;

	}

	public CaseDocumentsPage verifyLockedDocumentAfterEditWithWordClick() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After the edit with word is click, The document is not locked", i > 0);
		return this;

	}

	public CaseDocumentsPage checkinDocument() {

		Assert.assertEquals("Checkin Document name is wrong", "Checkin", checkInDocument.getText());
		checkInDocument.click();
		return this;
	}

	public CaseDocumentsPage verifyChekinDocumentTitle() {

		Assert.assertEquals("Checkin Document title is wrong", "Checkin New Document", chekInDocumentTitle.getText());
		return this;

	}

	public CaseDocumentsPage clickChooseFilesBtn() {
		chooseFiles.click();
		return this;
	}

	public CaseDocumentsPage verifySelectedFiles(String name) {
		Assert.assertEquals("Selected file name is wrong", name, selectedFiles.getText());
		return this;

	}

	public CaseDocumentsPage clickCheckinBtn() {

		checkinBtn.click();
		return this;
	}

	public CaseDocumentsPage performRightClickOnFirstDocument() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(firstDocumentTitle).build();
		action.perform();
		return this;
	}

	public CaseDocumentsPage verifyUnlockedDocumentAfterUplaodNewVersion() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("The document is still locked", i == 0);
		return this;

	}

	public CaseDocumentsPage verifyUnlockedDocumentAfterCancelEditWithWord() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After cancel edit with word is cliked, The document is  locked", i == 0);
		return this;

	}

	public CaseDocumentsPage verifyUnlockedDocumentAfterCancelEditing() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After cancel editing is clicked, The document is  locked", i == 0);
		return this;

	}

	public CaseDocumentsPage verifyUnlockedDocumentAfterChekin() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After check in is clicked, The document is  locked", i == 0);
		return this;

	}

	public CaseDocumentsPage verifyVersion2() {

		Assert.assertEquals("Version 2 of the document is not there", "2.0", version2.getText());
		return this;

	}

	public CaseDocumentsPage clickCancelEditing() {

		Assert.assertEquals("Cancel Editing is wrong", "Cancel Editing", cancelEditing.getText());
		cancelEditing.click();
		return this;

	}

	public CaseDocumentsPage clicRenameDocument() {

		Assert.assertEquals("Rename name is wrong", "Rename", renameDocument.getText());
		renameDocument.click();
		return this;

	}

	public CaseDocumentsPage renameFirstDocument(String name) {

		firstDocumentRenameInput.sendKeys(name);
		return this;
	}

	public CaseDocumentsPage verifyRenamedDocument(String name) {

		Assert.assertEquals("Renamed document name is wrong", name, firstDocumentTitle.getText());
		return this;
	}

	public CaseDocumentsPage renameSecondDocument(String name) {

		secondDocumentRenameInput.sendKeys(name);
		return this;
	}

	public CaseDocumentsPage verifySecondRenamedDocument(String name) {

		Assert.assertEquals("Second renamed document  name is wrong", name, secondDocumentTitle.getText());
		return this;
	}

	public CaseDocumentsPage replaceDocument() {

		Assert.assertEquals("Replace document label  name is wrong", "Replace", replaceDocument.getText());
		replaceDocument.click();
		return this;

	}

	public CaseDocumentsPage clickNewFolder() {

		Assert.assertEquals("New folder label  name is wrong", "New Folder", newFolder.getText());
		newFolder.click();
		return this;

	}

	public CaseDocumentsPage nameTheNewFolder(String name) {

		newFolderInput.sendKeys(name);
		return this;
	}

	public CaseDocumentsPage verifyCreatedFolder(String title) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondDocumentTitle.getText(), title, "Created folder title is wrong");
		softAssert.assertEquals(secondDocumentType.getText(), "", "Created folder type is wrong");
		softAssert.assertEquals(secondDocumentCreated.getText(), "", "Created folder created date is wrong");
		softAssert.assertEquals(secondDocumentAuthor.getText(), "", "Created folder author is wrong");
		softAssert.assertEquals(secondDocumentVersion.getText(), "", "Created folder version is worng");
		softAssert.assertEquals(secondDocumentStatus.getText(), "", "Created folder status is wrong");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage deleteFolder() {

		Assert.assertEquals("Delete folder label  name is wrong", "Delete", deleteFolder.getText());
		deleteFolder.click();
		return this;
	}

	public CaseDocumentsPage verifyFolderIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Folder is not deleted", i == 0);
		return this;

	}

	public CaseDocumentsPage performRightClickOnFolder() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(newFolderTitle).build();
		action.perform();
		return this;
	}

	public CaseDocumentsPage clickRenameFolder() {

		Assert.assertEquals("Rename folder label  name is wrong", "Rename", renameFolder.getText());
		renameFolder.click();
		return this;

	}

	public CaseDocumentsPage renameTheFolder(String name) {

		newFolderInput.sendKeys(name);
		return this;

	}

	public CaseDocumentsPage copyDocument() {

		Assert.assertEquals("Copy document label name is wrong", "Copy", copyDocument.getText());
		copyDocument.click();
		return this;

	}

	public CaseDocumentsPage pasteInFolder() {

		Assert.assertEquals("Paste in folder label  name is wrong", "Paste", pasteInFolder.getText());
		pasteInFolder.click();
		return this;

	}

	public CaseDocumentsPage verifyCopyPastedDocumentInFolder(String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(rowThreeDocumentTitle.getText(), "Case File.pdf", "Pasted Document title is wrong");
		softAssert.assertEquals(rowThreeDocumentType.getText(), "Case File", "Pasted Document type is wrong");
		softAssert.assertEquals(rowThreeDocumentCreated.getText(), createdDate, "Pasted Document crated date is wrong");
		softAssert.assertEquals(rowThreeDocumentAuthor.getText(), "Samuel Supervisor",
				"Pasted Document author is wrong");
		softAssert.assertEquals(firstDocumentAuthor.getText(), "1.0", "Pasted Document version is wrong");
		softAssert.assertEquals(firstDocumentVersion.getText(), status, "Pasted Document status is wrong");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage cutDocument() {

		Assert.assertEquals("Cut document label name is wrong", "Cut Ctrl+X", cutDocument.getText());
		cutDocument.click();
		return this;

	}

	public CaseDocumentsPage verifyCutPastedDocumentInFolder(String title, String type, String version, String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondDocumentTitle.getText(), title, "Cut/Pasted Document title is wrong");
		softAssert.assertEquals(secondDocumentType.getText(), type, "Cut/Pasted Document type is wrong");
		softAssert.assertEquals(secondDocumentCreated.getText(), createdDate,
				"Cut/Pasted Document created date is wrong");
		softAssert.assertEquals(secondDocumentAuthor.getText(), "Samuel Supervisor",
				"Cut/Pasted Docuemnt author is wrong");
		softAssert.assertEquals(secondDocumentVersion.getText(), version, "Cut/Pasted Document version is worng");
		softAssert.assertEquals(secondDocumentStatus.getText(), status, "Cut/Pasted Document status is wrong");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage verifyIfCutDocumentIsStillPresent(String title) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(firstDocumentTitle.getText(), title, " Title is wrong");
		softAssert.assertEquals(firstDocumentType.getText(), "", " Type is wrong");
		softAssert.assertEquals(firstDocumentCreated.getText(), "", " Date is wrong");
		softAssert.assertEquals(firstDocumentModified.getText(), "", " Author is wrong");
		softAssert.assertEquals(firstDocumentAuthor.getText(), "", " Version is worng");
		softAssert.assertEquals(firstDocumentVersion.getText(), "", "Status is wrong");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage editWithWordClick() {

		Assert.assertEquals("Edit with word label name is wrong", "Edit With Word", editWithWord.getText());
		editWithWord.click();
		return this;

	}

	public CaseDocumentsPage verifyModifiedDocument(String title, String type, String user, String version, String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(modifiedRowTitle.getText(), title, "Modified Document title is wrong");
		softAssert.assertEquals(modifiedRowType.getText(), type, "Modified Document type is wrong");
		softAssert.assertEquals(modifiedRowCreated.getText(), createdDate, "Modified Document created date is wrong");
		softAssert.assertEquals(modifiedRowModified.getText(), createdDate,
				"Modified Document modified field is wrong");
		softAssert.assertEquals(modfiedRowAuthor.getText(), user, "Modified Docuemnt author is wrong");
		softAssert.assertEquals(modifiedRowVersion.getText(), version, "Modified version is worng");
		softAssert.assertEquals(modifiedRowStatus.getText(), status, "Modified Document status is wrong");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage clickDeclareAsRecordDocument() {

		Assert.assertEquals("Declase as record label name is wrong", "Declare as Record(s)",
				declareAsRecordDocument.getText());
		declareAsRecordDocument.click();
		return this;
	}

	public CaseDocumentsPage checkIfRightClickWorksInRecordDocument() {
		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on record document is not working", i > 0);
		return this;
	}

	public CaseDocumentsPage verifyOptionOnRightClickOnRecordDocument() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(recordOpen.getText(), "Open", "Open label name  in record document is wrong");
		softAssert.assertEquals(recordEmail.getText(), "Email", "Email option name in record document is wrong");
		softAssert.assertEquals(recordCopy.getText(), "Copy", "Copy option name in record document is wrong");
		softAssert.assertEquals(recordDownload.getText(), "Download",
				"Download option name in record document is wrong");
		softAssert.assertAll();
		return this;

	}

	public CaseDocumentsPage clickEmailDocument() {

		Assert.assertEquals("Email label name is wrong", "Email", emailDocument.getText());
		emailDocument.click();
		return this;
	}

	public CaseDocumentsPage verifyEmailPopUpTitle() {
		Assert.assertEquals("Email popup title is wrong", "Email", emailPopupTitle.getText());
		return this;
	}

	public CaseDocumentsPage searchEmailUserInput(String name) {
		emailSearchInput.click();
		emailSearchInput.sendKeys(name);
		return this;
	}

	public CaseDocumentsPage clickEmailSearchBtn() {
		emailSearchBtn.click();
		return this;
	}

	public CaseDocumentsPage verifySearchedUser(String name, String email) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedUserName.getText(), name, "Searched username is wrong");
		softAssert.assertEquals(searchedUserEmail.getText(), email, "Searched user email is wrong");
		softAssert.assertAll();
		return this;
	}

	public CaseDocumentsPage clickSendEmailBtn() {
		sendEmailBtn.click();
		return this;
	}

	public CaseDocumentsPage verifyIfSecondRowDocumentIsPresent() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Document si not added", i > 0);
		return this;
	}

	public CaseDocumentsPage verifyIfDocumentHasVersion2() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]/span/select/option[2]"))
				.size();
		{
			Assert.assertTrue("The document is not uploaded , the version 2 is not displayed ", i != 0);

		}
		return this;

	}

	public CaseDocumentsPage verifyNewCorrespondeneName() {

		Assert.assertEquals("New Correspondence label name is wrong", "New Correspondence",
				NewCorrespondence.getText());
		return this;
	}

	public CaseDocumentsPage clickNewCorrespondence() {
		NewCorrespondence.click();
		return this;
	}

	public CaseDocumentsPage verifyCorrespondenceMenu() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[3]/ul")).size();
		Assert.assertTrue("New corresponce menu is not displayed", i != 0);
		return this;

	}

	public CaseDocumentsPage verifyWitnessInterviewRequest() {
		Assert.assertEquals("Correspondence Witness Interview Request label name is wrong", "Witness Interview Request",
				corresponceWitnessInterview.getText());
		return this;
	}

	public CaseDocumentsPage verifyNoticeofInvestigation() {
		Assert.assertEquals("Correspondence Notice of Investigation label name is wrong", "Notice of Investigation",
				correspondenceNoticeOfInvestigation.getText());
		return this;
	}

	public CaseDocumentsPage verifyClearanceDenied() {
		Assert.assertEquals("Correspondence Clearance Denied label name is wrong", "Clearance Denied",
				correspondenceClearanceDenide.getText());
		return this;
	}

	public CaseDocumentsPage verifyClearanceGranted() {
		Assert.assertEquals("Correspondence Clearance Granted label name is wrong", "Clearance Granted",
				correspondenceClearanceGranted.getText());
		return this;
	}

	public CaseDocumentsPage verifyMedicalRelease() {
		Assert.assertEquals("Correspondence Medical Release label name  is wrong", "Medical Release",
				correspondenceMedicalRelease.getText());
		return this;
	}

	public CaseDocumentsPage verifyGeneralRelease() {
		Assert.assertEquals("Correspondence General Release label name is wrong", "General Release",
				correspondenceGeneralRelease.getText());
		return this;
	}
	
    public Boolean pictureUploaded(){	
		
		Boolean pictureUploaded = false;
		if (driver.findElements(By.xpath("//*[@title='imageprofile']")).size() > 0)
		{
			pictureUploaded = true;
		}
		else 
		{
			pictureUploaded = false;
		}
		
		return pictureUploaded;
	}

	
	

}
