package com.armedia.arkcase.uitests.audit;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.support.PageFactory;

import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseTestUtils;
import com.armedia.arkcase.uitests.base.ArkCaseUtils;
import com.armedia.arkcase.uitests.base.Constant;
import com.armedia.arkcase.uitests.base.Utility;
import com.armedia.arkcase.uitests.cases.documents.CaseDocumentsPage;
import com.armedia.arkcase.uitests.user.UserProfilePage;
import com.armedia.arkcase.uitests.base.Utility;

public class AuditTests extends ArkCaseTestBase {

	AuditPage casePom = PageFactory.initElements(driver, AuditPage.class);
	AuditPage casesPom = PageFactory.initElements(driver, AuditPage.class);
	UserProfilePage user = PageFactory.initElements(driver, UserProfilePage.class);
	ArkCaseUtils checkDownload = new ArkCaseUtils();
	CaseDocumentsPage documents = PageFactory.initElements(driver, CaseDocumentsPage.class);

	@Test
	public void generateAuditReportforALL() throws Exception {
		
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet2");
		super.logIn();
		casePom.ReportsMenuClick();
		casePom.generateAuditReport(Utility.getCellData(1, 1), Utility.getCellData(1, 2),Utility.getCellData(1, 3),Utility.getCellData(1, 4));	
		driver.switchTo().frame("reports-iframe");		
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 1, 5);

	}
	@Test
	public void generateAuditReportforCaseFiles() throws Exception {
		
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet2");
		super.logIn();
		casePom.ReportsMenuClick();
		casePom.generateAuditReport(Utility.getCellData(2, 1), Utility.getCellData(2, 2),Utility.getCellData(2, 3),Utility.getCellData(2, 4));	
		driver.switchTo().frame("reports-iframe");		
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 2, 5);

	}
	@Test
	public void generateAuditReportforComplaints() throws Exception {
		
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet2");
		super.logIn();
		casePom.ReportsMenuClick();
		casePom.generateAuditReport(Utility.getCellData(3, 1), Utility.getCellData(3, 2),Utility.getCellData(3, 3),Utility.getCellData(3, 4));	
		driver.switchTo().frame("reports-iframe");		
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 3, 5);

	}
	@Test
	public void generateAuditReportforTasks() throws Exception {
		
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet2");
		super.logIn();
		casePom.ReportsMenuClick();
		casePom.generateAuditReport(Utility.getCellData(4, 1), Utility.getCellData(4, 2),Utility.getCellData(4, 3),Utility.getCellData(4, 4));
		driver.switchTo().frame("reports-iframe");		
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 4, 5);

	}
		@Test
	public void generateAuditReportforFiles() throws Exception {
		
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet2");
		super.logIn();
		casePom.ReportsMenuClick();
		casePom.generateAuditReport(Utility.getCellData(5, 1), Utility.getCellData(5, 2),Utility.getCellData(5, 3),Utility.getCellData(5, 4));
		driver.switchTo().frame("reports-iframe");		
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 5, 5);

	}


}