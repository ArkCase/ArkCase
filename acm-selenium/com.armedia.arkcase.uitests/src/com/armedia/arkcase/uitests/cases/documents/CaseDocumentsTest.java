package com.armedia.arkcase.uitests.cases.documents;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.support.PageFactory;

import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.base.CheckIfFileIsDownloaded;
import com.armedia.arkcase.uitests.base.TestsPoperties;
import com.armedia.arkcase.uitests.cases.CasePage;
import com.armedia.arkcase.uitests.cases.CasesPage;

public class CaseDocumentsTest extends ArkCaseTestBase {

	CasesPage cases = PageFactory.initElements(driver, CasesPage.class);
	CasePage casePom = PageFactory.initElements(driver, CasePage.class);
	CaseDocumentsPage documentsPage = PageFactory.initElements(driver, CaseDocumentsPage.class);
	CheckIfFileIsDownloaded check = new CheckIfFileIsDownloaded();

	@Test
	public void createNewCaseVerifyDocumentsSection() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Documents");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(4000);
		documentsPage.verifyDocumentsTable();
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.verifyFirstDocument("ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test

	public void createNewCaseAddDocumentFromFrevvoVerify() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Documents");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(3000);
		casePom.attachmentsAddFilesClickButton();
		Thread.sleep(2000);
		casePom.browseButtonClick();
		casePom.addFile();
		Thread.sleep(3000);
		casePom.uploadButtonClick();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		Thread.sleep(2000);
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(4000);
		documentsPage.verifyDocumentsTable();
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.verifySecondDocument("caseSummary.pdf", "Attachment", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewCaseAddNewDocumentOther() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("caseother");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(6000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(3000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(3000);
		documentsPage.verifyDocumentOtherName();
		Thread.sleep(5000);
		documentsPage.clickDocumentOther();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(5000);
		documentsPage.verifySecondDocument("imageprofile.png", "Other", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddNewdocumentWitnessInterview() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Witnes Interview");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(4000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.verifydocumentWitnessInterviewName();
		documentsPage.clickDocumentWitnessInterview();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPdf();
		Thread.sleep(5000);
		documentsPage.verifySecondDocument("caseSummary.pdf", "Witness Interview Request", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test
	public void createNewCaseAddNewdocumentNoticeOfInvestigation()
			throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Notice of Investigation");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.verifyDocumentNoticeOfInvestigationName();
		documentsPage.clickDocumentNoticeOfInvestigation();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(8000);
		documentsPage.verifySecondDocument("ArkCaseTesting.docx", "Notice Of Investigation", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);
	}

	@Test

	public void createNewCaseAddDocumentSF86Signature() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("SF86Signature");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.verifyDocumentSF86Signature();
		documentsPage.clickDocumentSF86Signature();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadXlsx();
		Thread.sleep(8000);
		documentsPage.verifySecondDocument("caseSummary.xlsx", "Sf86 Signature", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddDocumentEDelivery() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Edelivery");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.verifyDocumentEDelivery();
		documentsPage.clickDocumentEDelivery();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(8000);
		documentsPage.verifySecondDocument("imageprofile.png", "Edelivery", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddDocumentGeneralRelease() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("General Release");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.verifyDocumentGeneralRelease();
		documentsPage.clickDocumentGeneralRelease();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(10000);
		documentsPage.verifySecondDocument("ArkCaseTesting.docx", "General Release", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddDocumentMedicalRelease() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Medical Release");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.verifyDocumentMedicalRelease();
		documentsPage.clickDocumentMedicalRelease();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadXlsx();
		Thread.sleep(10000);
		documentsPage.verifySecondDocument("caseSummary.xlsx", "Medical Release", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddDocumentReportOfInvestigation() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Report Of Investigation");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.clickDocumentROI();
		Thread.sleep(10000);
		documentsPage.swithWindow();
		driver.switchTo().frame(documentsPage.roiFirstIframe);
		documentsPage.clickReportTitle();
		Thread.sleep(2000);
		documentsPage.reportTitleInput("Report");
		Thread.sleep(2000);
		documentsPage.clickReportFirstName();
		Thread.sleep(2000);
		documentsPage.reportFirstNameInput("Milan");
		Thread.sleep(2000);
		documentsPage.clickReportLastName();
		Thread.sleep(2000);
		documentsPage.reportLastNameInput("jovanovski");
		Thread.sleep(2000);
		documentsPage.clickSelectApprover();
		Thread.sleep(3000);
		documentsPage.clickSearchForUserInput();
		Thread.sleep(2000);
		documentsPage.searchForUserInput("Samuel Supervisor");
		Thread.sleep(2000);
		documentsPage.clickGoButton();
		Thread.sleep(3000);
		documentsPage.clickSearchedUser();
		Thread.sleep(2000);
		documentsPage.clickAddButton();
		Thread.sleep(3000);
		documentsPage.clickSubmitButton();
		Thread.sleep(10000);
		documentsPage.swithWindow();
		documentsPage.verifySecondDocument("Report of Investigation.pdf", "Report Of Investigation", "1.0", "ACTIVE");
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddDocumentDeleteDocument() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Delete Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.clickDocumentOther();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadPNGPicture();
		Thread.sleep(6000);
		documentsPage.verifySecondDocument("imageprofile.png", "Other", "1.0", "ACTIVE");
		documentsPage.performRighClickOnSecondDocument();
		Thread.sleep(3000);
		documentsPage.deleteDocument();
		Thread.sleep(3000);
		documentsPage.verifySecondDocumentIsDeleted();
		Thread.sleep(3000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewkCaseCheckChangeCaseStatusInDocumentsTable()
			throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Change Case");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		Thread.sleep(5000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.verifySecondDocument("Change Case Status.pdf", "Change Case Status", "1.0", "ACTIVE");
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseAddDocumentDownloadDocument() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Download Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.clickDocumentOther();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadXlsx();
		Thread.sleep(10000);
		documentsPage.verifySecondDocument("caseSummary.xlsx", "Other", "1.0", "ACTIVE");
		Thread.sleep(3000);
		documentsPage.performRighClickOnSecondDocument();
		Thread.sleep(3000);
		documentsPage.downloadDocument();
		Thread.sleep(10000);
		check.checkIfFileIsDownloaded("caseSummary");
		Thread.sleep(5000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseCheckoutChekinUploadWordDocument() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Chekin Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.performRightClickOnFirstDocument();
		Thread.sleep(3000);
		documentsPage.checkoutDocument();
		Thread.sleep(8000);
		check.checkIfFileIsDownloaded("Case_File");
		Thread.sleep(5000);
		documentsPage.verifyLockedDocument();
		Thread.sleep(3000);
		documentsPage.clearCachBtn.click();
		Thread.sleep(4000);
		documentsPage.performRightClickOnFirstDocument();
		Thread.sleep(2000);
		documentsPage.checkinDocument();
		Thread.sleep(5000);
		documentsPage.verifyChekinDocumentTitle();
		documentsPage.clickChooseFilesBtn();
		Thread.sleep(4000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(4000);
		documentsPage.verifySelectedFiles("ArkCaseTesting.docx");
		documentsPage.clickCheckinBtn();
		Thread.sleep(5000);
		documentsPage.verifyUnlockedDocument();
		Thread.sleep(3000);
		documentsPage.verifyVersion2();
		Thread.sleep(2000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseCheckOutCancelEditing() throws InterruptedException, IOException, AWTException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Chekout Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.performRightClickOnFirstDocument();
		Thread.sleep(3000);
		documentsPage.checkoutDocument();
		Thread.sleep(8000);
		check.checkIfFileIsDownloaded("Case_File");
		Thread.sleep(5000);
		documentsPage.verifyLockedDocument();
		Thread.sleep(3000);
		documentsPage.clearCachBtn.click();
		Thread.sleep(4000);
		documentsPage.performRightClickOnFirstDocument();
		Thread.sleep(2000);
		documentsPage.clickCancelEditing();
		Thread.sleep(5000);
		documentsPage.verifyUnlockedDocument();
		Thread.sleep(3000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}

	@Test
	public void createNewCaseRenameDocument() throws InterruptedException, IOException {

		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("rename Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.performRightClickOnFirstDocument();
		Thread.sleep(3000);
		documentsPage.clicRenameDocument();
		Thread.sleep(3000);
		documentsPage.renameFirstDocument("document");
		Thread.sleep(5000);
		documentsPage.documentsTableTitle.click();
		Thread.sleep(3000);
		documentsPage.verifyRenamedDocument("document");
		Thread.sleep(2000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);

	}
	
	
	@Test
	public void createNewCaseAddDocumentRenameTheDocument() throws InterruptedException,IOException, AWTException{
		
		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("rename Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		driver.navigate().refresh();
		Thread.sleep(10000);
		documentsPage.performRightClickOnRoot();
		Thread.sleep(4000);
		documentsPage.checkIfRightClickOnRootIsWorking();
		Thread.sleep(3000);
		documentsPage.verifyNewDocmentName();
		documentsPage.newDocumentClick();
		Thread.sleep(4000);
		documentsPage.clickDocumentOther();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(8000);
		documentsPage.performRighClickOnSecondDocument();
		Thread.sleep(3000);
		documentsPage.clicRenameDocument();
		Thread.sleep(3000);
		documentsPage.renameSecondDocument("document2");
		Thread.sleep(5000);
		documentsPage.documentsTableTitle.click();
		Thread.sleep(3000);
		documentsPage.verifySecondRenamedDocument("document2");
		Thread.sleep(2000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);	
		
		
		
}
	@Test
	public void createNewCaseReplaceDocument() throws InterruptedException,IOException, AWTException{
		
		ArkCaseAuthentication.logIn(TestsPoperties.getSupervisorUserUsername(),
				TestsPoperties.getSupervisorUserPassword(), driver, TestsPoperties.getBaseURL());
		casePom.newCase();
		Thread.sleep(20000);
		driver.switchTo().frame(cases.frameOne);
		driver.switchTo().frame(cases.frameTwo);
		casePom.vrifyGeneralInformationTabName();
		casePom.caseTitleInput("Chekin Document");
		casePom.verifyCaseTypeTitle();
		casePom.caseTypeInputClick();
		Thread.sleep(2000);
		casePom.caseTypeBackgroundInvestigation();
		Thread.sleep(2000);
		casePom.nextButton.click();
		Thread.sleep(3000);
		casePom.verifyInitiatorTab();
		casePom.initiatorTitle.click();
		Thread.sleep(2000);
		casePom.clickInitiatorMr();
		Thread.sleep(2000);
		casePom.initiatorFirstName("Milan");
		casePom.initiatorLastName("Jovanovski");
		Thread.sleep(2000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.nextButtonClick();
		Thread.sleep(3000);
		casePom.verifyAttachmentTab();
		Thread.sleep(2000);
		casePom.participantnsTab.click();
		Thread.sleep(2000);
		casePom.selectParticipantTypeClick();
		Thread.sleep(2000);
		casePom.selectparticipantOwner();
		casePom.selectParticipantClick();
		Thread.sleep(3000);
		casePom.searchForUsers();
		Thread.sleep(3000);
		casePom.searchedName();
		casePom.addSearchedNameClick();
		Thread.sleep(2000);
		casePom.submit.click();
		Thread.sleep(10000);
		driver.switchTo().defaultContent();
		cases.caseDocuments.click();
		Thread.sleep(5000);
		documentsPage.clickRootExpander();
		Thread.sleep(3000);
		documentsPage.performRightClickOnFirstDocument();
		Thread.sleep(3000);
		documentsPage.replaceDocument();
		Thread.sleep(3000);
		ArkCaseTestUtils.uploadDocx();
		Thread.sleep(6000);
		documentsPage.verifyVersion2();
		Thread.sleep(2000);
		documentsPage.chnageCaseStatusButton.click();
		Thread.sleep(10000);
		driver.switchTo().frame(cases.chnageCaseStausFrameOne);
		Thread.sleep(2000);
		driver.switchTo().frame(cases.chnageCaseStatusFrameTwo);
		Thread.sleep(2000);
		cases.deleteCase();
		driver.switchTo().defaultContent();
		cases.changeCaseStatusAproved();
		Thread.sleep(3000);
		ArkCaseAuthentication.logOut(driver);	
	}
	
	
	
	
	
	
	
	
	
	
	

}
