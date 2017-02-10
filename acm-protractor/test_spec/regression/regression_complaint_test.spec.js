var logger = require('../../log');
var utils = require('../../util/utils.js');
var complaintPage = require('../../Pages/complaint_page.js');
var casePage = require('../../Pages/case_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var taskPage = require('../../Pages/task_page.js');
var Objects = require('../../json/Objects.json');
var loginPage = require('../../Pages/login_page.js');
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var preferencesPage = require('../../Pages/preference_page.js');
var flag = false;

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

}

describe(' Complaint page tests ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();

    });


    it('Verify if reader is displayed in participants table', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.participantTable();
        expect(complaintPage.returnParticipantTypeForthRow()).toEqual("reader", "Participant type is correct in forth row");
        expect(complaintPage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name is not correct in forth row");
    });

    it('should create new complaint add/edit timeSheet and verify the time widget data in cases overview page', function() {


        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            complaintPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            timeTrackingPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            timeTrackingPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            timeTrackingPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            timeTrackingPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("1");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.verifyTimeWidgetData("7");
        });
    });

    it('should edit assignee', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.participantsTab();
        complaintPage.selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        complaintPage.switchToIframes();
        complaintPage.clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.editAssignee("bthomas");
        expect(complaintPage.returnAssignee()).toEqual("Bill Thomas", "Assignee is not updated");
    });

    it('should Add timesheet and verify it in the complaint timesheet table', function() {

        complaintPage.clickModuleComplaints();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            complaintPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            timeTrackingPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            complaintPage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.TimeTable();
            expect(complaintPage.returnTimesheetFormName()).toContain(Objects.casepage.data.timeSheet, "Form name in added timesheet is not correct");
            expect(complaintPage.returnTimesheetUser()).toEqual(Objects.casepage.data.assigneeSamuel, "Timesheet user in added timesheet is not correct");
            expect(complaintPage.returnTimesheetModifiedDate()).toEqual(utils.returnToday("/"), "Timesheet modified date in added timesheet is not correct");
            expect(complaintPage.returnTimesheetStatus()).toEqual(Objects.casepage.data.statusDraft, "Timesheet status in added timesheet is not correct");
            expect(complaintPage.returnTimesheetHours()).toEqual(Objects.casepage.data.totalHours, "Timesheet total hours in added timesheet are not correct");

        });
    });

    it('should Add costsheet and verify in complaints costsheet table', function() {

        complaintPage.clickModuleComplaints();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            costTrackingPage.navigateToExpensesPage();
            complaintPage.switchToIframes();
            costTrackingPage.selectType("Complaint");
            costTrackingPage.populateExpensesTable(Objects.costsheetPage.data.Taxi, Objects.costsheetPage.data.Ammount);
            costTrackingPage.clickCodeType();
            costTrackingPage.switchToDefaultContent();
            costTrackingPage.searchForObject(text);
            costTrackingPage.switchToIframes();
            costTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.CostTable();
            expect(complaintPage.returncostSheetFormName()).toContain("Costsheet", "Form name in added costsheet is not correct");
            expect(complaintPage.returncostSheetUser()).toEqual(Objects.casepage.data.assigneeSamuel, "User in added costsheet is not correct");
            expect(complaintPage.returncostSheetModifiedDate()).toEqual(utils.returnToday("/"), "Modified date in added costsheet is not correct");
            expect(complaintPage.returncostSheetTotalCost()).toEqual(Objects.costsheetPage.data.verifyAmmount, "Total cost in added costsheet is not correct");
            expect(complaintPage.returncostSheetStatus()).toEqual(Objects.casepage.data.statusDraft, "Status in added costsheet is not correct");
        });
    });

});
