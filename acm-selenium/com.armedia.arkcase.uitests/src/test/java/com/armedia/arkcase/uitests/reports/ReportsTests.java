package com.armedia.arkcase.uitests.reports;

import com.armedia.arkcase.uitests.base.ArkCaseAuthentication;
import com.armedia.arkcase.uitests.base.ArkCaseTestBase;
import com.armedia.arkcase.uitests.base.ArkCaseUtils;
import com.armedia.arkcase.uitests.base.Constant;
import com.armedia.arkcase.uitests.base.Utility;
import com.armedia.arkcase.uitests.cases.documents.CaseDocumentsPage;
import com.armedia.arkcase.uitests.group.SmokeTests;
import com.armedia.arkcase.uitests.user.UserProfilePage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.support.PageFactory;

public class ReportsTests extends ArkCaseTestBase
{

    ReportsPage reportPage = PageFactory.initElements(driver, ReportsPage.class);
    UserProfilePage user = PageFactory.initElements(driver, UserProfilePage.class);
    ArkCaseUtils checkDownload = new ArkCaseUtils();
    CaseDocumentsPage documents = PageFactory.initElements(driver, CaseDocumentsPage.class);

    @Test
    @Category({ SmokeTests.class })
    public void generateCaseReportforDrafts() throws Exception
    {
        // generate report
        Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
        reportPage.ReportsMenuClick();
        reportPage.generateReport(Utility.getCellData(1, 1), Utility.getCellData(1, 2), Utility.getCellData(1, 3),
                Utility.getCellData(1, 4));
        reportPage.switchToReportFrame();
        reportPage.switchToReportContentFrame();
        Assert.assertEquals("Case Number column header is not correct", "Case Number", reportPage.readCaseNumberColumnHeader());
        Assert.assertEquals("Status column header is not correct", "Status", reportPage.readStatusColumnHeader());
        Assert.assertEquals("Title column header is not correct", "Title", reportPage.readTitleColumnHeader());
        Assert.assertEquals("Incident Date column header is not correct", "Incident Date", reportPage.readIncidentDateColumnHeader());
        Assert.assertEquals("Priority column header is not correct", "Priority", reportPage.readPriorityColumnHeader());
        Assert.assertEquals("Due Date column header is not correct", "Due Date", reportPage.readDueDateColumnHeader());
        Assert.assertEquals("Type column header is not correct", "Type", reportPage.readTypecolumnHeader());

        reportPage.switchToDefaultContent();
        ArkCaseAuthentication.logOut(driver);
        Utility.setCellData("Pass", 1, 5);
        // test

    }

    @Test
    @Category({ SmokeTests.class })
    public void generateCaseReportforInApproval() throws Exception
    {
        // generate report
        Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
        reportPage.ReportsMenuClick();
        reportPage.generateReport(Utility.getCellData(2, 1), Utility.getCellData(2, 2), Utility.getCellData(2, 3),
                Utility.getCellData(2, 4));
        reportPage.switchToReportFrame();
        reportPage.switchToReportContentFrame();
        Assert.assertEquals("Case Number column header is not correct", "Case Number", reportPage.readCaseNumberColumnHeader());
        Assert.assertEquals("Status column header is not correct", "Status", reportPage.readStatusColumnHeader());
        Assert.assertEquals("Title column header is not correct", "Title", reportPage.readTitleColumnHeader());
        Assert.assertEquals("Incident Date column header is not correct", "Incident Date", reportPage.readIncidentDateColumnHeader());
        Assert.assertEquals("Priority column header is not correct", "Priority", reportPage.readPriorityColumnHeader());
        Assert.assertEquals("Due Date column header is not correct", "Due Date", reportPage.readDueDateColumnHeader());
        Assert.assertEquals("Type column header is not correct", "Type", reportPage.readTypecolumnHeader());
        reportPage.switchToDefaultContent();
        ArkCaseAuthentication.logOut(driver);
        Utility.setCellData("Pass", 2, 5);
        // test

    }

    @Test
    @Category({ SmokeTests.class })
    public void generateCaseReportforActive() throws Exception
    {
        // generate report
        Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
        reportPage.ReportsMenuClick();
        reportPage.generateReport(Utility.getCellData(3, 1), Utility.getCellData(3, 2), Utility.getCellData(3, 3),
                Utility.getCellData(3, 4));
        reportPage.switchToReportFrame();
        reportPage.switchToReportContentFrame();
        Assert.assertEquals("Case Number column header is not correct", "Case Number", reportPage.readCaseNumberColumnHeader());
        Assert.assertEquals("Status column header is not correct", "Status", reportPage.readStatusColumnHeader());
        Assert.assertEquals("Title column header is not correct", "Title", reportPage.readTitleColumnHeader());
        Assert.assertEquals("Incident Date column header is not correct", "Incident Date", reportPage.readIncidentDateColumnHeader());
        Assert.assertEquals("Priority column header is not correct", "Priority", reportPage.readPriorityColumnHeader());
        Assert.assertEquals("Due Date column header is not correct", "Due Date", reportPage.readDueDateColumnHeader());
        Assert.assertEquals("Type column header is not correct", "Type", reportPage.readTypecolumnHeader());
        reportPage.switchToDefaultContent();
        ArkCaseAuthentication.logOut(driver);
        Utility.setCellData("Pass", 3, 5);
        // test

    }

    @Test
    @Category({ SmokeTests.class })
    public void generateCaseReportforInactive() throws Exception
    {
        // generate report
        Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
        reportPage.ReportsMenuClick();
        reportPage.generateReport(Utility.getCellData(4, 1), Utility.getCellData(4, 2), Utility.getCellData(4, 3),
                Utility.getCellData(4, 4));
        reportPage.switchToReportFrame();
        reportPage.switchToReportContentFrame();
        Assert.assertEquals("Case Number column header is not correct", "Case Number", reportPage.readCaseNumberColumnHeader());
        Assert.assertEquals("Status column header is not correct", "Status", reportPage.readStatusColumnHeader());
        Assert.assertEquals("Title column header is not correct", "Title", reportPage.readTitleColumnHeader());
        Assert.assertEquals("Incident Date column header is not correct", "Incident Date", reportPage.readIncidentDateColumnHeader());
        Assert.assertEquals("Priority column header is not correct", "Priority", reportPage.readPriorityColumnHeader());
        Assert.assertEquals("Due Date column header is not correct", "Due Date", reportPage.readDueDateColumnHeader());
        Assert.assertEquals("Type column header is not correct", "Type", reportPage.readTypecolumnHeader());
        reportPage.switchToDefaultContent();
        ArkCaseAuthentication.logOut(driver);
        Utility.setCellData("Pass", 4, 5);
        // test

    }

    @Test
    @Category({ SmokeTests.class })
    public void generateCaseReportforClosed() throws Exception
    {
        // generate report
        Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
        reportPage.ReportsMenuClick();
        reportPage.generateReport(Utility.getCellData(5, 1), Utility.getCellData(5, 2), Utility.getCellData(5, 3),
                Utility.getCellData(5, 4));
        reportPage.switchToReportFrame();
        reportPage.switchToReportContentFrame();
        Assert.assertEquals("Case Number column header is not correct", "Case Number", reportPage.readCaseNumberColumnHeader());
        Assert.assertEquals("Status column header is not correct", "Status", reportPage.readStatusColumnHeader());
        Assert.assertEquals("Title column header is not correct", "Title", reportPage.readTitleColumnHeader());
        Assert.assertEquals("Incident Date column header is not correct", "Incident Date", reportPage.readIncidentDateColumnHeader());
        Assert.assertEquals("Priority column header is not correct", "Priority", reportPage.readPriorityColumnHeader());
        Assert.assertEquals("Due Date column header is not correct", "Due Date", reportPage.readDueDateColumnHeader());
        Assert.assertEquals("Type column header is not correct", "Type", reportPage.readTypecolumnHeader());
        reportPage.switchToDefaultContent();
        ArkCaseAuthentication.logOut(driver);
        Utility.setCellData("Pass", 5, 5);
    }

    @Test
    @Category({ SmokeTests.class })
    public void generateCDCReport() throws Exception
    {
        // generate report
        Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
        reportPage.ReportsMenuClick();
        reportPage.generateCDCReport(Utility.getCellData(6, 1), Utility.getCellData(6, 3),
                Utility.getCellData(6, 4));
        reportPage.switchToReportFrame();
        reportPage.switchToReportContentFrame();
        Assert.assertEquals("Disposition title is not correct", "Disposition", reportPage.readDispositionTitle());
        Assert.assertEquals("Count title is not correct", "Count", reportPage.readCountTitle());
        Assert.assertEquals("Add to Existing Case label is not correct", "Add to Existing Case", reportPage.readAddToExistingCaseLabel());
        Assert.assertEquals("No Further Action label is not correct", "No Further Action", reportPage.readNoFurtherActionLabel());
        Assert.assertEquals("Open Investigation label is not correct", "Open Investigation", reportPage.readOpenInvestigationLabel());
        Assert.assertEquals("Refer External label is not correct", "Refer External", reportPage.readReferExternalLabel());
        Assert.assertTrue("Add to Existing Case value is not displayed", reportPage.isAddToExistingCaseValueDisplayed());
        Assert.assertTrue("No Further Action value is not displayed", reportPage.isNoFurtherActionValueDisplayed());
        Assert.assertTrue("Open Investigation value is not displayed", reportPage.isOpenInvestigationValueDisplayed());
        Assert.assertTrue("Refer External value is not displayed", reportPage.isReferExternalValueDisplayed());
        Assert.assertTrue("Complaint disposition graph is not displayed", reportPage.isComplaintDispositionGraphDisplayed());
        reportPage.switchToDefaultContent();
        ArkCaseAuthentication.logOut(driver);
        Utility.setCellData("Pass", 6, 5);
    }
    // @Test
    // @Category({ SmokeTests.class })
    // public void generateComplaintReportforDraft() throws Exception {
    // // generate report
    // Utility.setExcelFile(Constant.Path_TestData + Constant.File_TestData, "Report");
    // reportPage.ReportsMenuClick();
    // reportPage.generateReport(Utility.getCellData(7, 1), Utility.getCellData(7, 2), Utility.getCellData(7, 3),
    // Utility.getCellData(7, 4));
    // reportPage.switchToReportFrame();
    // reportPage.switchToReportContentFrame();
    // Assert.assertEquals("Case Number column header is not correct", "Case Number" ,
    // reportPage.readCaseNumberColumnHeader());
    // Assert.assertEquals("Status column header is not correct", "Status" , reportPage.readStatusColumnHeader());
    // Assert.assertEquals("Title column header is not correct", "Title" , reportPage.readTitleColumnHeader());
    // Assert.assertEquals("Incident Date column header is not correct", "Incident Date" ,
    // reportPage.readIncidentDateColumnHeader());
    // Assert.assertEquals("Priority column header is not correct", "Priority" , reportPage.readPriorityColumnHeader());
    // Assert.assertEquals("Due Date column header is not correct", "Due Date" , reportPage.readDueDateColumnHeader());
    // Assert.assertEquals("Type column header is not correct", "Type" , reportPage.readTypecolumnHeader());
    // reportPage.switchToDefaultContent();
    // ArkCaseAuthentication.logOut(driver);
    // Utility.setCellData("Pass", 7, 5);
    // }
}
