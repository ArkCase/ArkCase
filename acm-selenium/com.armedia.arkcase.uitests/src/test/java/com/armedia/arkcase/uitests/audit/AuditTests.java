package com.armedia.arkcase.uitests.audit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.support.PageFactory;
import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseUtils;
import com.armedia.arkcase.uitests.base.Constant;
import com.armedia.arkcase.uitests.base.Utility;
import com.armedia.arkcase.uitests.base.WaitHelper;
import com.armedia.arkcase.uitests.cases.CasePage;
import com.armedia.arkcase.uitests.cases.documents.CaseDocumentsPage;
import com.armedia.arkcase.uitests.group.SmokeTests;
import com.armedia.arkcase.uitests.user.UserProfilePage;



public class AuditTests extends ArkCaseTestBase {

	AuditPage auditPage = PageFactory.initElements(driver, AuditPage.class);	
	UserProfilePage user = PageFactory.initElements(driver, UserProfilePage.class);
	ArkCaseUtils checkDownload = new ArkCaseUtils();
	CaseDocumentsPage documents = PageFactory.initElements(driver, CaseDocumentsPage.class);
	CasePage casePage = PageFactory.initElements(driver, CasePage.class);

	@Test
	@Category({ SmokeTests.class })
	public void generateAuditReportforALL() throws Exception {		
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Audit");
		auditPage.ReportsMenuClick();
		auditPage.generateAuditReport(Utility.getCellData(1, 1), Utility.getCellData(1, 2), Utility.getCellData(1, 3),
				Utility.getCellData(1, 4));
		auditPage.switchToAuditFrame();
		WaitHelper.waitForElement(auditPage.reportContent, driver);
		WaitHelper.waitForFrameAndSwitchToIt(60, driver, "reportContent");		
		Assert.assertEquals("Date column header is not correct", "Date" , auditPage.readDateColumnHeader());
		Assert.assertEquals("User column header is not correct", "User", auditPage.readUserColumnHeader());
		Assert.assertEquals("Name column header is not correct", "Name", auditPage.readNameColumnHeader());
		Assert.assertEquals("Result column header is not correct", "Result", auditPage.readResultColumnHeader());
		Assert.assertEquals("IP Address column header is not correct", "IP Address", auditPage.readIpAddressColumnHeader());
		Assert.assertEquals("Object ID column header is not correct", "Object ID", auditPage.readObjectIdColumnHeader());
		Assert.assertEquals("Object Type column header is not correct", "Object Type", auditPage.readObjectTypeColumnHeader());
		auditPage.switchToDefaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 1, 5);

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateAuditReportforCaseFiles() throws Exception {
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Audit");
		auditPage.ReportsMenuClick();
		auditPage.generateAuditReport(Utility.getCellData(2, 1), Utility.getCellData(2, 2), Utility.getCellData(2, 3),
				Utility.getCellData(2, 4));
		auditPage.switchToAuditFrame();
		WaitHelper.waitForElement(auditPage.reportContent, driver);
		WaitHelper.waitForFrameAndSwitchToIt(60, driver, "reportContent");
		Assert.assertEquals("Date column header is not correct", "Date" , auditPage.readDateColumnHeader());
		Assert.assertEquals("User column header is not correct", "User", auditPage.readUserColumnHeader());
		Assert.assertEquals("Name column header is not correct", "Name", auditPage.readNameColumnHeader());
		Assert.assertEquals("Result column header is not correct", "Result", auditPage.readResultColumnHeader());
		Assert.assertEquals("IP Address column header is not correct", "IP Address", auditPage.readIpAddressColumnHeader());
		Assert.assertEquals("Object ID column header is not correct", "Object ID", auditPage.readObjectIdColumnHeader());
		Assert.assertEquals("Object Type column header is not correct", "Object Type", auditPage.readObjectTypeColumnHeader());
		auditPage.switchToDefaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 2, 5);

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateAuditReportforComplaints() throws Exception {

		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Audit");
		auditPage.ReportsMenuClick();
		auditPage.generateAuditReport(Utility.getCellData(3, 1), Utility.getCellData(3, 2), Utility.getCellData(3, 3),
				Utility.getCellData(3, 4));
		auditPage.switchToAuditFrame();
		WaitHelper.waitForElement(auditPage.reportContent, driver);
		WaitHelper.waitForFrameAndSwitchToIt(60, driver, "reportContent");
		Assert.assertEquals("Date column header is not correct", "Date" , auditPage.readDateColumnHeader());
		Assert.assertEquals("User column header is not correct", "User", auditPage.readUserColumnHeader());
		Assert.assertEquals("Name column header is not correct", "Name", auditPage.readNameColumnHeader());
		Assert.assertEquals("Result column header is not correct", "Result", auditPage.readResultColumnHeader());
		Assert.assertEquals("IP Address column header is not correct", "IP Address", auditPage.readIpAddressColumnHeader());
		Assert.assertEquals("Object ID column header is not correct", "Object ID", auditPage.readObjectIdColumnHeader());
		Assert.assertEquals("Object Type column header is not correct", "Object Type", auditPage.readObjectTypeColumnHeader());
		auditPage.switchToDefaultContent();	
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 3, 5);

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateAuditReportforTasks() throws Exception {

		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Audit");
		auditPage.ReportsMenuClick();
		auditPage.generateAuditReport(Utility.getCellData(4, 1), Utility.getCellData(4, 2), Utility.getCellData(4, 3),
				Utility.getCellData(4, 4));
		auditPage.switchToAuditFrame();
		WaitHelper.waitForElement(auditPage.reportContent, driver);
		WaitHelper.waitForFrameAndSwitchToIt(60, driver, "reportContent");
		Assert.assertEquals("Date column header is not correct", "Date" , auditPage.readDateColumnHeader());
		Assert.assertEquals("User column header is not correct", "User", auditPage.readUserColumnHeader());
		Assert.assertEquals("Name column header is not correct", "Name", auditPage.readNameColumnHeader());
		Assert.assertEquals("Result column header is not correct", "Result", auditPage.readResultColumnHeader());
		Assert.assertEquals("IP Address column header is not correct", "IP Address", auditPage.readIpAddressColumnHeader());
		Assert.assertEquals("Object ID column header is not correct", "Object ID", auditPage.readObjectIdColumnHeader());
		Assert.assertEquals("Object Type column header is not correct", "Object Type", auditPage.readObjectTypeColumnHeader());
		auditPage.switchToDefaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 4, 5);

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateAuditReportforFiles() throws Exception {
        
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Audit");
		auditPage.ReportsMenuClick();
		WaitHelper.waitPageToLoad(60, driver);
		auditPage.generateAuditReport(Utility.getCellData(5, 1), Utility.getCellData(5, 2), Utility.getCellData(5, 3),
				Utility.getCellData(5, 4));
		WaitHelper.waitPageToLoad(60, driver);
		auditPage.switchToAuditFrame();
		WaitHelper.waitForElement(auditPage.reportContent, driver);
		WaitHelper.waitForFrameAndSwitchToIt(60, driver, "reportContent");
		Assert.assertEquals("Date column header is not correct", "Date" , auditPage.readDateColumnHeader());
		Assert.assertEquals("User column header is not correct", "User", auditPage.readUserColumnHeader());
		Assert.assertEquals("Name column header is not correct", "Name", auditPage.readNameColumnHeader());
		Assert.assertEquals("Result column header is not correct", "Result", auditPage.readResultColumnHeader());
		Assert.assertEquals("IP Address column header is not correct", "IP Address", auditPage.readIpAddressColumnHeader());
		Assert.assertEquals("Object ID column header is not correct", "Object ID", auditPage.readObjectIdColumnHeader());
		Assert.assertEquals("Object Type column header is not correct", "Object Type", auditPage.readObjectTypeColumnHeader());
		auditPage.switchToDefaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 5, 5);

	}
}