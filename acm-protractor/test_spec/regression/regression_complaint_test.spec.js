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
        expect(complaintPage.returnParticipantTypeForthRow()).toEqual("reader");
        expect(complaintPage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor");
    });

    it('should create new complaint add/edit timeSheet and verify the time widget data in cases overview page', function() {


        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Complaint", text, "8");
            complaintPage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Complaint", text, "1");
            complaintPage.selectApprover(Objects.casepage.data.approverSamuel);
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
        expect(complaintPage.returnAssignee()).toEqual("Bill Thomas");
    });

});
