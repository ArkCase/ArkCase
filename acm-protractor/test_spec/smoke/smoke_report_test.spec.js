var logger = require('../../log');
var reportPage = require('../../Pages/report_page.js');
var casePage = require('../../Pages/case_page.js');
var complaintPage = require('../../Pages/complaint_page.js');
var Objects = require('../../json/Objects.json');
var utils = require('../../util/utils.js');
var loginPage = require('../../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;


function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 60000);

}

function waitUrl(myUrl) {
    return function() {
        return browser.getCurrentUrl().then(function(url) {
            return myUrl.test(url);
        });
    }
}


describe('reports tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    // Run each Report

    it('should navigate to case files and verify that case is displayed in case summary drafts report', function() {

        casePage.navigateToPage("Case Files");
        casePage.waitForCaseID();
        var caseid = casePage.getCaseId();
        var createdDate = casePage.returnCreatedDate();
        var dueDate = casePage.returnDueDate();
        var caseType = casePage.returnCaseType();
        var priority = casePage.returnPriority();
        casePage.navigateToPage("Reports");
        reportPage.runReport("CASE SUMMARY", "Draft", createdDate, createdDate);
        reportPage.switchToReportframes();
        reportPage.validateCaseReportTitles(Objects.reportPage.data.CaseSummaryReportTitleName, Objects.reportPage.data.CaseSummaryColumn1Title, Objects.reportPage.data.CaseSummaryColumn2Title, Objects.reportPage.data.CaseSummaryColumn3Title, Objects.reportPage.data.CaseSummaryColumn4Title, Objects.reportPage.data.CaseSummaryColumn5Title, Objects.reportPage.data.CaseSummaryColumn6Title, Objects.reportPage.data.CaseSummaryColumn7Title);
        reportPage.validateCaseReportValues(caseid, "DRAFT", Objects.casepage.data.caseTitle, createdDate, priority, dueDate, caseType);
        reportPage.switchToDefaultContent();

    });

    it('should navigate to complaints and verify that complaint draft is displayed in complaint drafts report ', function() {

        complaintPage.navigateToPage("Complaints").waitForComplaintID();
        var createdDate = complaintPage.returnCreatedDate();
        var type = complaintPage.returnComplaintType();
        var priority = complaintPage.returnComplaintPriority();
        var complaintTitle = complaintPage.returnComplaintTitle();
        reportPage.navigateToPage("Reports");
        reportPage.runReport("COMPLAINT REPORT", "Draft", createdDate, createdDate);
        reportPage.switchToReportframes();
        reportPage.validateComplaintReportTitles(Objects.reportPage.data.ComplaintReportTitleName, Objects.reportPage.data.ComplaintReportColumn1Title, Objects.reportPage.data.CaseSummaryColumn2Title, Objects.reportPage.data.ComplaintReportColumn3Title, Objects.reportPage.data.ComplaintReportColumn4Title, Objects.reportPage.data.ComplaintReportColumn5Title, Objects.reportPage.data.ComplaintReportColumn6Title);
        reportPage.validateComplaintReportValues(complaintTitle, "DRAFT", type, priority, createdDate, createdDate);
        reportPage.switchToDefaultContent();

    });

    it('should navigate to complaints, close it with No further action and verify that is displayed in complaint disposition count report', function() {

        complaintPage.navigateToPage("Reports");
        reportPage.runReport("COMPLAINT DISPOSITION COUNT", "Draft", utils.returnToday("/"), utils.returnToday("/"));
        reportPage.switchToReportframes().validateCDCReportTitles(Objects.reportPage.data.CDCReportDispositionTitle, Objects.reportPage.data.CDCReportCountTitle, Objects.reportPage.data.CDCFirstRowTitle, Objects.reportPage.data.CDCSecondRowTitle, Objects.reportPage.data.CDCThirdRowTitle, Objects.reportPage.data.CDCForthRowTitle);
        var closedNoFurtherAction = reportPage.returnCDCNoFurtherActionValue();
        var closedAddToExistingCase = reportPage.returnCDCAddToExistingCaseValue();
        var closedOpenInvestigation = reportPage.returnCDCOpenInvestigationValue();
        var closedReferExternal = reportPage.returnCDCReferExternalValue();
        reportPage.switchToDefaultContent().navigateToPage("Complaints").waitForComplaintID();
        complaintPage.clickCloseComplaint().switchToIframes().closeComplaint("No Further Action", Objects.complaintPage.data.description, Objects.complaintPage.data.approver);
        complaintPage.navigateToPage("Reports");
        reportPage.runReport("COMPLAINT DISPOSITION COUNT", "Draft", utils.returnToday("/"), utils.returnToday("/")).switchToReportframes();
        reportPage.validateCDCReportValues(closedAddToExistingCase.toString(), (closedNoFurtherAction + 1).toString(), closedOpenInvestigation.toString(), closedReferExternal.toString());
        reportPage.switchToDefaultContent();
    });

});
