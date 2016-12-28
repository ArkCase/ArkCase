var logger = require('../log');
var auditPage = require('../Pages/audit_page.js');
var casePage = require('../Pages/case_page.js');
var complaintPage = require('../Pages/complaint_page.js');
var Objects = require('../json/Objects.json');
var utils = require('../util/utils.js');
var loginPage = require('../Pages/login_page.js');
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


describe('case page tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    it('should navigate to case files and verify that case is displayed in audit case report', function() {

        casePage.navigateToPage("Case Files").waitForCaseID();
        var caseid = casePage.getCaseId();
        var createdDate = casePage.returnCreatedDate();
        casePage.navigateToPage("Audit");
        auditPage.runReport("Case Files", caseid, utils.returnToday("/"), utils.returnToday("/"));
        auditPage.switchToAuditframes();
        auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
        //auditPage.validateAuditReportValues();
        auditPage.switchToDefaultContent();

    });

    it('should navigate to complaints and verify that complaint is displayed in audit complaint report ', function() {

        complaintPage.navigateToPage("Complaints").waitForComplaintID();
        var createdDate = complaintPage.returnCreatedDate();
        var complaintId = complaintPage.getComplaintId();
        complaintPage.navigateToPage("Audit");
        auditPage.runReport("Complaints",complaintId , utils.returnToday("/"), utils.returnToday("/"));
        auditPage.switchToAuditframes();
        auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
        //auditPage.validateAuditReportValues();
        auditPage.switchToDefaultContent();

    });

});
