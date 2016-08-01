package com.armedia.arkcase.uitests.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.support.PageFactory;

import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseUtils;
import com.armedia.arkcase.uitests.base.Constant;
import com.armedia.arkcase.uitests.base.Utility;
import com.armedia.arkcase.uitests.cases.documents.CaseDocumentsPage;
import com.armedia.arkcase.uitests.group.SmokeTests;
import com.armedia.arkcase.uitests.user.UserProfilePage;

public class ReportsTests extends ArkCaseTestBase {

	ReportsPage casePom = PageFactory.initElements(driver, ReportsPage.class);
	ReportsPage casesPom = PageFactory.initElements(driver, ReportsPage.class);
	UserProfilePage user = PageFactory.initElements(driver, UserProfilePage.class);
	ArkCaseUtils checkDownload = new ArkCaseUtils();
	CaseDocumentsPage documents = PageFactory.initElements(driver, CaseDocumentsPage.class);

	@Test
	@Category({ SmokeTests.class })
	public void generateCaseReportforDrafts() throws Exception {
		// generate report
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Sheet1");
		casePom.ReportsMenuClick();
		casePom.generateReport(Utility.getCellData(1, 1), Utility.getCellData(1, 2), Utility.getCellData(1, 3),
				Utility.getCellData(1, 4));
		driver.switchTo().frame("reports-iframe");
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 1, 5);
		// test

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateCaseReportforInApproval() throws Exception {
		// generate report
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Sheet1");
		casePom.ReportsMenuClick();
		casePom.generateReport(Utility.getCellData(2, 1), Utility.getCellData(2, 2), Utility.getCellData(2, 3),
				Utility.getCellData(2, 4));
		driver.switchTo().frame("reports-iframe");
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 2, 5);
		// test

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateCaseReportforActive() throws Exception {
		// generate report
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Sheet1");
		casePom.ReportsMenuClick();
		casePom.generateReport(Utility.getCellData(3, 1), Utility.getCellData(3, 2), Utility.getCellData(3, 3),
				Utility.getCellData(3, 4));
		driver.switchTo().frame("reports-iframe");
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 3, 5);
		// test

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateCaseReportforInactive() throws Exception {
		// generate report
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Sheet1");
		casePom.ReportsMenuClick();
		casePom.generateReport(Utility.getCellData(4, 1), Utility.getCellData(4, 2), Utility.getCellData(4, 3),
				Utility.getCellData(4, 4));
		driver.switchTo().frame("reports-iframe");
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 4, 5);
		// test

	}

	@Test
	@Category({ SmokeTests.class })
	public void generateCaseReportforClosed() throws Exception {
		// generate report
		Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Sheet1");
		casePom.ReportsMenuClick();
		casePom.generateReport(Utility.getCellData(5, 1), Utility.getCellData(5, 2), Utility.getCellData(5, 3),
				Utility.getCellData(5, 4));
		driver.switchTo().frame("reports-iframe");
		driver.switchTo().defaultContent();
		ArkCaseAuthentication.logOut(driver);
		Utility.setCellData("Pass", 5, 5);
		// test

	}
}
