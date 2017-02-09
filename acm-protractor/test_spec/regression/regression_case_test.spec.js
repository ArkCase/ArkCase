var logger = require('../../log');
var casePage = require('../../Pages/case_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var Objects = require('../../json/Objects.json');
var taskPage = require('../../Pages/task_page.js');
var utils = require('../../util/utils.js');
var userPage = require('../../Pages/user_profile_page.js');
var loginPage = require('../../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var preferencesPage = require('../../Pages/preference_page.js');
var auditPage = require('../../Pages/audit_page.js');


function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

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

    it('should create new case and try to add owner and no access from participant tab for same user and verify the alert message', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab().selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes().clickAddParticipantTypeSecondRowbtn();
        casePage.selectParticipantSecondRow("No Access", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        expect(casePage.returnParticipantTypeAlert()).toEqual("This action is not allowed. No Access and Owner is conflict combination.");
        casePage.switchToDefaultContent();
    });

    it('should create new case add/edit timeSheet and verify the time widget data in cases overview page', function() {


        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            casePage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Case");
            timeTrackingPage.clickChargeCode();
            timeTrackingPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            timeTrackingPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickLastElementInTreeData();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Case");
            timeTrackingPage.clickChargeCode();
            timeTrackingPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            timeTrackingPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            casePage.verifyTimeWidgetData("7");
        });
    });


    it('should create new case with owner  and edit the assignee ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.editAssignee(Objects.basepage.data.IanInvestigator).waitForAssignee();
        expect(casePage.returnAssignee()).toEqual(Objects.basepage.data.IanInvestigator, "The assignee is not updated");
    });

    it('should create new case verify the notification message and no access of the object name ', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.editOwningGroup(Objects.basepage.data.owningGroupAdministratorDev);
        expect(casePage.returnOwningGroup()).toEqual(Objects.basepage.data.owningGroupAdministratorDev, "Owning group is not updated");
        casePage.verifyTheNotificationMessage("Case File ");
        casePage.editPriority("High");
        casePage.verifyFirstElementNameNoAccess();
    });

    it('should open document and send email and then check history table and auditing', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCaseID();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents").doubleClickRootFolder().rightClickDocument().clickDocAction("Email").sendEmail();
        casePage.clickSublink("History");
        //currently sending emails is not working to be added code for check in history table and also audit report
        // expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent);
        // expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"));
        // expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel);
        // casePage.navigateToPage("Audit");
        // auditPage.runReport("Files", "", utils.returnToday("/"), utils.returnToday("/"));
        // auditPage.switchToAuditframes();
        // auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
        // auditPage.validateAuditReportValues(utils.returnToday("/"), Objects.taskspage.data.assigneeSamuel, "Case Viewed", "success", caseid, "CASE_FILE" );

    });

    it('should click on sorter tree button and verify the name of sort by id desc name', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickTreeSortersBtn();
        casePage.returnSortByIdDesc(Objects.basepage.data.sortByIdDesc);
    });

    it('should navigate to cases page and create new cost sheet and verify it in the cost table', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            costTrackingPage.navigateToExpensesPage();
            casePage.switchToIframes();
            costTrackingPage.selectType("Case");
            costTrackingPage.populateExpensesTable(Objects.costsheetPage.data.Taxi, Objects.costsheetPage.data.Ammount);
            costTrackingPage.clickCodeType();
            costTrackingPage.switchToDefaultContent();
            costTrackingPage.searchForObject(text);
            costTrackingPage.switchToIframes();
            costTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            casePage.CostTable();
            expect(casePage.returncostSheetFormName()).toContain("Costsheet", "Name is not correct on added costsheet on case");
            expect(casePage.returncostSheetUser()).toEqual(Objects.casepage.data.assigneeSamuel, "User is not correct on added costsheet on case");
            expect(casePage.returncostSheetModifiedDate()).toEqual(utils.returnToday("/"), "Modified date is not correct on added costsheet on case");
            expect(casePage.returncostSheetTotalCost()).toEqual(Objects.costsheetPage.data.verifyAmmount, "Total cost is not correct on added costsheet on case");
            expect(casePage.returncostSheetStatus()).toEqual(Objects.casepage.data.statusDraft, "Status is not correct on added costsheet on case");

        });

    });

    it('should navigate to the csaes page and create new timesheet and verify it in the time table', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            casePage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Case");
            timeTrackingPage.clickChargeCode();
            timeTrackingPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            casePage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            casePage.TimeTable();
            expect(casePage.returnTimesheetFormName()).toContain(Objects.casepage.data.timeSheet, "Name is not correct on added timesheet on case");
            expect(casePage.returnTimesheetUser()).toEqual(Objects.casepage.data.assigneeSamuel, "User is not correct on added timesheet on case");
            expect(casePage.returnTimesheetModifiedDate()).toEqual(utils.returnToday("/"), "Modified date is not correct on added timesheet on case");
            expect(casePage.returnTimesheetStatus()).toEqual(Objects.casepage.data.statusDraft, "Status is not correct on added timesheet on case");
            expect(casePage.returnTimesheetHours()).toEqual(Objects.casepage.data.totalHours, "Hours is not correct on added timesheet on case");
        });
    });

});
