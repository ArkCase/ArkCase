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

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]")
	public WebElement secondDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[4]")
	WebElement secondDocumentType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[5]")
	WebElement secondDocumentCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[6]")
	WebElement secondDocumentModified;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[7]")
	WebElement secondDocumentAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[8]")
	WebElement secondDocumentVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[9]")
	WebElement secondDocumentStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[9]")
	WebElement clearCachButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/a[3]/span")
	public WebElement chnageCaseStatusButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[1]/td[3]")
	WebElement root;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]")
	WebElement newDocument;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[2]/ul/li[8]")
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

	public void verifyDocumentsTable() {

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

	}

	public void clickRootExpander() {
		rootExpander.click();
	}

	public void verifyFirstDocument(String title, String type, String author, String version, String status) {

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
	}

	public void verifySecondDocument(String title, String type, String version, String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondDocumentTitle.getText(), title, "Second Document title is wrong");
		softAssert.assertEquals(secondDocumentType.getText(), type, "Second Document type is wrong");
		softAssert.assertEquals(secondDocumentCreated.getText(), createdDate, "Second Document created date is wrong");
		softAssert.assertEquals(secondDocumentModified.getText(), createdDate,
				"Second document modified date is wrong");
		softAssert.assertEquals(secondDocumentAuthor.getText(), "Samuel Supervisor", "Second Docuemnt author is wrong");
		softAssert.assertEquals(secondDocumentVersion.getText(), version, "Second Document version is worng");
		softAssert.assertEquals(secondDocumentStatus.getText(), status, "Second Document status is wrong");
		softAssert.assertAll();

	}

	public void performRightClickOnRoot() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(root).build();
		action.perform();
	}

	public void newDocumentClick() {

		newDocument.click();
	}

	public void checkIfRightClickOnRootIsWorking() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[2]")).size();
		Assert.assertTrue("Right Click on root is not working", i > 0);

	}

	public void verifyNewDocmentName() {
		Assert.assertEquals("New Document name is wrong", "New Document", newDocument.getText());
	}

	public void clickDocumentOther() {

		documentOther.click();

	}

	public void verifyDocumentOtherName() {
		Assert.assertEquals("Document Other name is wrong", "Other", documentOther.getText());
	}

	public void verifydocumentWitnessInterviewName() {

		Assert.assertEquals("Document Witness Interview Request name is wrong", "Witness Interview Request",
				documentWitnessInterview.getText());
	}

	public void clickDocumentWitnessInterview() {
		documentWitnessInterview.click();
	}

	public void verifyDocumentNoticeOfInvestigationName() {
		Assert.assertEquals("Document Notice of Investigation name is wrong", "Notice of Investigation",
				documentNoticeOfInvestigation.getText());
	}

	public void clickDocumentNoticeOfInvestigation() {
		documentNoticeOfInvestigation.click();
	}

	public void verifyDocumentSF86Signature() {

		Assert.assertEquals("Document SF86 Signature name is wrong", "SF86 Signature", documentSF86Signature.getText());

	}

	public void clickDocumentSF86Signature() {

		documentSF86Signature.click();
	}

	public void verifyDocumentEDelivery() {
		Assert.assertEquals("Document eDelivery name is wrong", "eDelivery", documentEDelivery.getText());
	}

	public void clickDocumentEDelivery() {
		documentEDelivery.click();
	}

	public void verifyDocumentGeneralRelease() {

		Assert.assertEquals("Document General Release name is wrong", "General Release",
				documentGeneralRelease.getText());

	}

	public void clickDocumentGeneralRelease() {

		documentGeneralRelease.click();

	}

	public void verifyDocumentMedicalRelease() {

		Assert.assertEquals("Document Medical Release name is wrong", "Medical Release",
				documentMedicalRelease.getText());
	}

	public void clickDocumentMedicalRelease() {

		documentMedicalRelease.click();
	}

	public void clickDocumentROI() {

		Assert.assertEquals("Document Report of Investigation name is wrong", "Report of Investigation",
				documentReportOfInvestigation.getText());
		documentReportOfInvestigation.click();

	}

	public void clickReportTitle() {

		reportTitle.click();
	}

	public void reportTitleInput(String title) {

		reportTitle.sendKeys(title);
	}

	public void clickReportFirstName() {

		firstName.click();
	}

	public void reportFirstNameInput(String name) {

		firstName.sendKeys(name);
	}

	public void clickReportLastName() {

		lastName.click();
	}

	public void reportLastNameInput(String lName) {
		lastName.sendKeys(lName);
	}

	public void clickSelectApprover() {

		selectApprover.click();
	}

	public void clickSearchForUserInput() {

		addUserInput.click();
	}

	public void searchForUserInput(String user) {

		addUserInput.sendKeys(user);
	}

	public void clickGoButton() {
		goButton.click();
	}

	public void clickSearchedUser() {

		Assert.assertEquals("Searched User name is wrong or it not shown", "Samuel Supervisor", searchedName.getText());
		searchedName.click();

	}

	public void clickAddButton() {

		addButton.click();
	}

	public void clickSubmitButton() {

		submitButton.click();
	}

	public void swithWindow() {

		for (String winHandle : driver.getWindowHandles()) {
			driver.switchTo().window(winHandle);
		}

	}

	public void performRighClickOnSecondDocument() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(secondDocumentTitle).build();
		action.perform();
	}

	public void checkIfRightClickOnSecondDocumentIsWorking() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[14]")).size();
		Assert.assertTrue("Right Click on root is not working", i > 0);

	}

	public void deleteDocument() {

		Assert.assertEquals("Delete document name is wrong ", "Delete", deleteDocument.getText());
		deleteDocument.click();

	}

	public void verifySecondDocumentIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Document is not deleted", i == 0);
	}

	public void downloadDocument() {

		Assert.assertEquals("Download document name is wrong ", "Download", downloadDocument.getText());
		downloadDocument.click();

	}

	public void checkoutDocument() {

		Assert.assertEquals("Checkout document name is wrong", "Checkout", checkoutDocument.getText());
		checkoutDocument.click();

	}

	public void verifyLockedDocument() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After the upload, The document is not locked", i > 0);
	}
	
	public void verifyLockedDocumentAfterCheckout(){
		
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After checkout is clicked, The document is not locked", i > 0);	
		
	}
	
	public void verifyLockedDocumentAfterEditWithWordClick(){
		
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After the edit with word is click, The document is not locked", i > 0);
		
	}
	
	
	

	public void checkinDocument() {

		Assert.assertEquals("Checkin Document name is wrong", "Checkin", checkInDocument.getText());
		checkInDocument.click();
	}

	public void verifyChekinDocumentTitle() {

		Assert.assertEquals("Checkin Document title is wrong", "Checkin New Document", chekInDocumentTitle.getText());

	}

	public void clickChooseFilesBtn() {
		chooseFiles.click();
	}

	public void verifySelectedFiles(String name) {
		Assert.assertEquals("Selected file name is wrong", name, selectedFiles.getText());

	}

	public void clickCheckinBtn() {

		checkinBtn.click();
	}

	public void performRightClickOnFirstDocument() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(firstDocumentTitle).build();
		action.perform();
	}

	public void verifyUnlockedDocumentAfterUplaodNewVersion() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("The document is still locked", i == 0);

	}
	
	public void verifyUnlockedDocumentAfterCancelEditWithWord(){
		
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After cancel edit with word is cliked, The document is  locked", i == 0);	
		
	}
	public void verifyUnlockedDocumentAfterCancelEditing(){
		
		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("After cancel editing is clicked, The document is  locked", i == 0);		
		
	}
	
	public void verifyUnlockedDocumentAfterChekin(){
	
			
			int i = driver
					.findElements(By
							.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
					.size();

			Assert.assertTrue("After check in is clicked, The document is  locked", i == 0);		
		
	}
	
	
	

	public void verifyVersion2() {

		Assert.assertEquals("Version 2 of the document is not there", "2.0", version2.getText());

	}

	public void clickCancelEditing() {

		Assert.assertEquals("Cancel Editing is wrong", "Cancel Editing", cancelEditing.getText());
		cancelEditing.click();

	}

	public void clicRenameDocument() {

		Assert.assertEquals("Rename name is wrong", "Rename", renameDocument.getText());
		renameDocument.click();

	}

	public void renameFirstDocument(String name) {

		firstDocumentRenameInput.sendKeys(name);
	}

	public void verifyRenamedDocument(String name) {

		Assert.assertEquals("Renamed document name is wrong", name, firstDocumentTitle.getText());
	}

	public void renameSecondDocument(String name) {

		secondDocumentRenameInput.sendKeys(name);
	}

	public void verifySecondRenamedDocument(String name) {

		Assert.assertEquals("Second renamed document  name is wrong", name, secondDocumentTitle.getText());
	}

	public void replaceDocument() {

		Assert.assertEquals("Replace document label  name is wrong", "Replace", replaceDocument.getText());
		replaceDocument.click();

	}

	public void clickNewFolder() {

		Assert.assertEquals("New folder label  name is wrong", "New Folder", newFolder.getText());
		newFolder.click();

	}

	public void nameTheNewFolder(String name) {

		newFolderInput.sendKeys(name);
	}

	public void verifyCreatedFolder(String title) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(secondDocumentTitle.getText(), title, "Created folder title is wrong");
		softAssert.assertEquals(secondDocumentType.getText(), "", "Created folder type is wrong");
		softAssert.assertEquals(secondDocumentCreated.getText(), "", "Created folder created date is wrong");
		softAssert.assertEquals(secondDocumentAuthor.getText(), "", "Created folder author is wrong");
		softAssert.assertEquals(secondDocumentVersion.getText(), "", "Created folder version is worng");
		softAssert.assertEquals(secondDocumentStatus.getText(), "", "Created folder status is wrong");
		softAssert.assertAll();

	}

	public void deleteFolder() {

		Assert.assertEquals("Delete folder label  name is wrong", "Delete", deleteFolder.getText());
		deleteFolder.click();
	}

	public void verifyFolderIsDeleted() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Folder is not deleted", i == 0);

	}

	public void performRightClickOnFolder() {

		Actions actions = new Actions(driver);
		Action action = actions.contextClick(newFolderTitle).build();
		action.perform();
	}

	public void clickRenameFolder() {

		Assert.assertEquals("Rename folder label  name is wrong", "Rename", renameFolder.getText());
		renameFolder.click();

	}

	public void renameTheFolder(String name) {

		newFolderInput.sendKeys(name);

	}

	public void copyDocument() {

		Assert.assertEquals("Copy document label name is wrong", "Copy", copyDocument.getText());
		copyDocument.click();

	}

	public void pasteInFolder() {

		Assert.assertEquals("Paste in folder label  name is wrong", "Paste", pasteInFolder.getText());
		pasteInFolder.click();

	}

	public void verifyCopyPastedDocumentInFolder(String status) {

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

	}

	public void cutDocument() {

		Assert.assertEquals("Cut document label name is wrong", "Cut Ctrl+X", cutDocument.getText());
		cutDocument.click();

	}

	public void verifyCutPastedDocumentInFolder(String title, String type, String version, String status) {

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

	}

	public void verifyIfCutDocumentIsStillPresent(String title) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(firstDocumentTitle.getText(), title, " Title is wrong");
		softAssert.assertEquals(firstDocumentType.getText(), "", " Type is wrong");
		softAssert.assertEquals(firstDocumentCreated.getText(), "", " Date is wrong");
		softAssert.assertEquals(firstDocumentModified.getText(), "", " Author is wrong");
		softAssert.assertEquals(firstDocumentAuthor.getText(), "", " Version is worng");
		softAssert.assertEquals(firstDocumentVersion.getText(), "", "Status is wrong");
		softAssert.assertAll();

	}

	public void editWithWordClick() {

		Assert.assertEquals("Edit with word label name is wrong", "Edit With Word", editWithWord.getText());
		editWithWord.click();

	}

	public void verifyModifiedDocument(String title, String type, String user, String version, String status) {

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

	}

	public void clickDeclareAsRecordDocument() {

		Assert.assertEquals("Declase as record label name is wrong", "Declare as Record(s)",
				declareAsRecordDocument.getText());
		declareAsRecordDocument.click();
	}

	public void checkIfRightClickWorksInRecordDocument() {
		int i = driver.findElements(By.xpath("/html/body/ul")).size();
		Assert.assertTrue("Right click on record document is not working", i > 0);
	}

	public void verifyOptionOnRightClickOnRecordDocument() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(recordOpen.getText(), "Open", "Open label name  in record document is wrong");
		softAssert.assertEquals(recordEmail.getText(), "Email", "Email option name in record document is wrong");
		softAssert.assertEquals(recordCopy.getText(), "Copy", "Copy option name in record document is wrong");
		softAssert.assertEquals(recordDownload.getText(), "Download",
				"Download option name in record document is wrong");
		softAssert.assertAll();

	}

	public void clickEmailDocument() {

		Assert.assertEquals("Email label name is wrong", "Email", emailDocument.getText());
		emailDocument.click();
	}

	public void verifyEmailPopUpTitle() {
		Assert.assertEquals("Email popup title is wrong", "Email", emailPopupTitle.getText());
	}

	public void searchEmailUserInput(String name) {
		emailSearchInput.click();
		emailSearchInput.sendKeys(name);
	}

	public void clickEmailSearchBtn() {
		emailSearchBtn.click();
		;
	}

	public void verifySearchedUser(String name, String email) {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(searchedUserName.getText(), name, "Searched username is wrong");
		softAssert.assertEquals(searchedUserEmail.getText(), email, "Searched user email is wrong");
		softAssert.assertAll();
	}

	public void clickSendEmailBtn() {
		sendEmailBtn.click();
	}

	public void verifyIfSecondRowDocumentIsPresent() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]"))
				.size();
		Assert.assertTrue("Document si not added", i > 0);
	}

	public void verifyIfDocumentHasVersion2() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]/span/select/option[2]"))
				.size();
		{
			Assert.assertTrue("The document is not uploaded , the version 2 is not displayed ", i != 0);

		}

	}

	public void verifyNewCorrespondeneName() {

		Assert.assertEquals("New Correspondence label name is wrong", "New Correspondence",
				NewCorrespondence.getText());
	}

	public void clickNewCorrespondence() {
		NewCorrespondence.click();
	}

	public void verifyCorrespondenceMenu() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[3]/ul")).size();
		Assert.assertTrue("New corresponce menu is not displayed", i != 0);

	}

	public void verifyWitnessInterviewRequest() {
		Assert.assertEquals("Correspondence Witness Interview Request label name is wrong", "Witness Interview Request",
				corresponceWitnessInterview.getText());
	}

	public void verifyNoticeofInvestigation() {
		Assert.assertEquals("Correspondence Notice of Investigation label name is wrong", "Notice of Investigation",
				correspondenceNoticeOfInvestigation.getText());
	}

	public void verifyClearanceDenied() {
		Assert.assertEquals("Correspondence Clearance Denied label name is wrong", "Clearance Denied",
				correspondenceClearanceDenide.getText());
	}

	public void verifyClearanceGranted() {
		Assert.assertEquals("Correspondence Clearance Granted label name is wrong", "Clearance Granted",
				correspondenceClearanceGranted.getText());
	}

	public void verifyMedicalRelease() {
		Assert.assertEquals("Correspondence Medical Release label name  is wrong", "Medical Release",
				correspondenceMedicalRelease.getText());
	}

	public void verifyGeneralRelease() {
		Assert.assertEquals("Correspondence General Release label name is wrong", "General Release",
				correspondenceGeneralRelease.getText());
	}

}
