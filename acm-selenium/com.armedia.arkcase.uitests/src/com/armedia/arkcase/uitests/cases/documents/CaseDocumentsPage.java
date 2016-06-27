package com.armedia.arkcase.uitests.cases.documents;

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

public class CaseDocumentsPage extends ArkCaseTestBase {

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/span")
	WebElement documentsTableTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[3]")
	WebElement titleColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[4]")
	WebElement typeColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[5]")
	WebElement cretedColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[6]")
	WebElement authorColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[7]")
	WebElement versionColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/thead/tr/th[8]")
	WebElement statusColumnName;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[1]/div/div/button")
	WebElement refreshTableButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[1]/td[3]/span/span[1]")
	WebElement rootExpander;
	// first row
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]")
	WebElement firstDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[4]")
	WebElement firstDocumentType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[5]")
	WebElement firstDocumentCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[6]")
	WebElement firstDocumentAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[7]")
	WebElement firstDocumentVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[8]")
	WebElement firstDocumentStatus;
	// second row

	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]")
	WebElement secondDocumentTitle;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[4]")
	WebElement secondDocumentType;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[5]")
	WebElement secondDocumentCreated;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[6]")
	WebElement secondDocumentAuthor;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[7]")
	WebElement secondDocumentVersion;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[8]")
	WebElement secondDocumentStatus;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[9]")
	WebElement clearCachButton;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[2]/div/div/div[2]/div/button[3]")
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
	WebElement clearCachBtn;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[7]/span/select/option[2]")
	WebElement version2;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[7]")
	WebElement cancelEditing;
	@FindBy(how = How.XPATH, using = "/html/body/ul/li[13]")
	WebElement renameDocument;
	@FindBy(how = How.XPATH, using = "/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[3]/span/span[3]/input")
	WebElement firstDocumentRenameInput;
	@FindBy(how=How.XPATH,using="/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[3]/td[3]/span/span[3]/input")
    WebElement secondDocumentRenameInput;
	@FindBy(how=How.XPATH,using="/html/body/ul/li[17]")
	WebElement replaceDocument;

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

	public void verifyDocumentsTable() {

		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(documentsTableTitle.getText(), "Documents", "Documents table title is wrong");
		softAssert.assertEquals(titleColumnName.getText(), "Title", "Documents title column name is wrong");
		softAssert.assertEquals(typeColumnName.getText(), "Type", "Documents type column name is wrong");
		softAssert.assertEquals(cretedColumnName.getText(), "Created", "Documents created column name is wrong");
		softAssert.assertEquals(authorColumnName.getText(), "Author", "Documents author column name is wrong");
		softAssert.assertEquals(versionColumnName.getText(), "Version", "Documents version column name is wrong");
		softAssert.assertEquals(statusColumnName.getText(), "Status", "Documents status column name is wrong");
		softAssert.assertTrue(refreshTableButton.isDisplayed(), "Documents refresh button is not displayed");
		softAssert.assertAll();

	}

	public void clickRootExpander() {
		rootExpander.click();
	}

	public void verifyFirstDocument(String status) {

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String createdDate = formatter.format(date);
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(firstDocumentTitle.getText(), "Case File.pdf", "First Document title is wrong");
		softAssert.assertEquals(firstDocumentType.getText(), "Case File", "First Document type is wrong");
		softAssert.assertEquals(firstDocumentCreated.getText(), createdDate, "First Document crated date is wrong");
		softAssert.assertEquals(firstDocumentAuthor.getText(), "Samuel Supervisor", "First Document author is wrong");
		softAssert.assertEquals(firstDocumentVersion.getText(), "1.0", "First Document version is wrong");
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
		softAssert.assertEquals(secondDocumentAuthor.getText(), "Samuel Supervisor", "Second Docuemnt author is wrong");
		softAssert.assertEquals(secondDocumentVersion.getText(), version, "Second Document version is worng");
		softAssert.assertEquals(secondDocumentStatus.getText(), status, "Second Document status is wrong");
		softAssert.assertAll();

	}

	public void performRightClickOnRoot() {

		Actions act = new Actions(driver);
		act.contextClick(root).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();

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

		Actions act = new Actions(driver);
		act.contextClick(secondDocumentTitle).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();

	}

	public void checkIfRightClickOnSecondDocumentIsWorking() {

		int i = driver.findElements(By.xpath("/html/body/ul/li[14]")).size();
		Assert.assertTrue("Right Click on root is not working", i > 0);

	}

	public void deleteDocument() {

		Assert.assertEquals("Delete document name is wrong ", "Delete Ctrl+D", deleteDocument.getText());
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

		Assert.assertTrue("The document is not locked", i > 0);
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

		Actions act = new Actions(driver);
		act.contextClick(firstDocumentTitle).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).build().perform();
	}

	public void verifyUnlockedDocument() {

		int i = driver
				.findElements(By
						.xpath("/html/body/div[1]/div/div[2]/section/div/div/section[1]/div[3]/div/div/div/div[2]/doc-tree/table/tbody/tr[2]/td[2]/span"))
				.size();

		Assert.assertTrue("The document is still locked", i == 0);

	}

	public void verifyVersion2() {

		Assert.assertEquals("Version 2 of the document is not there", "2.0", version2.getText());

	}

	public void clickCancelEditing() {

		Assert.assertEquals("Cancel Editing is wrong", "Cancel Editing", cancelEditing.getText());
		cancelEditing.click();

	}

	public void clicRenameDocument() {

		Assert.assertEquals("Rename name is wrong", "Rename [F2]", renameDocument.getText());
		renameDocument.click();

	}

	public void renameFirstDocument(String name) {

		firstDocumentRenameInput.sendKeys(name);
	}

	public void verifyRenamedDocument(String name) {
		
    Assert.assertEquals("Renamed document name is wrong", name, firstDocumentTitle.getText());
	}

	public void renameSecondDocument(String name){
		
	secondDocumentRenameInput.sendKeys(name);	
	}
	
	
	public void verifySecondRenamedDocument(String name){
		
	Assert.assertEquals("Second renamed document name is wrong", name , secondDocumentTitle.getText());	
	}
	
	public void replaceDocument(){
		
	Assert.assertEquals("Replace document name is wrong", "Replace", replaceDocument.getText());
	replaceDocument.click();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
