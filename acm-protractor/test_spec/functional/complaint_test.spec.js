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
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var flag = false;

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

}

describe('Create new complaint ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();

    });

    it('should create new complaint ', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        expect(complaintPage.returnComplaintsTitle()).toEqual(Objects.complaintPage.data.title, "Title is not correct on new created complaint");

    });

    it('Add/delete note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.deleteNote();
    });

    it('Add new note and edit added note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.editNote(Objects.casepage.data.editnote);
        expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is not sucessfully edited");
    });

    it('Add link from details', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickInsertLinkInDetails();
        expect(complaintPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle);
        complaintPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        complaintPage.validateDetailsTextArea(Objects.taskspage.data.insertLinkText, 'The link is not added');
        complaintPage.clickSaveDetailsButton();
        complaintPage.validateDetailsTextArea(Objects.taskspage.data.insertLinkText, 'The link is not mathcing the expected value');

    });

    it('should create new complaint and add picture from details', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickDetailsAddPicture();
        complaintPage.uploadPicture();
        expect(complaintPage.returnDetailsUploadedImage(), "Image is not succesfully added in details");

    });


    it('should verify if the people intiator delete button is displayed', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.verifyIfInitiatorCanBeDeleted();
    });

    it('should verify that searching of Case id during close complaint is retrieving the data in the fields', function() {

        casePage.navigateToPage("Case Files").waitForCaseID();
        var caseid = casePage.getCaseId();
        var caseTitle = casePage.returnCaseTitle();
        var caseCreateDate = casePage.returnCreatedDate();
        var casePriority = casePage.returnPriority();
        complaintPage.clickModuleComplaints();
        complaintPage.clickCloseComplaint().switchToIframes().selectComplaintDisposition("Add to Existing Case").insertCaseNumber(caseid).clickSearchButton();
        expect(complaintPage.returnCaseTitle()).toEqual(caseTitle, "Filled case title after search is not correct");
        expect(complaintPage.returnCaseCreatedDate()).toEqual(caseCreateDate, "Filled created date after search is not correct");
        expect(complaintPage.returnCasePriority()).toEqual(casePriority, "Filled priority after search is not correct");
        complaintPage.switchToDefaultContent();
    });

    it('should create new Complaint by default assignee, claim it and verify the assignee', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes();
        complaintPage.submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickClaimButton();
        expect(complaintPage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Assignee is not correct after claim");
    });

    it('should create new Complaint by default assignee, claim it verify the assignee then uncalaim it and verify if the assignee is removed ', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes();
        complaintPage.submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickClaimButton();
        expect(complaintPage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Default assignee of creted complaint is not correct");
        complaintPage.clickUnclaimButton();
        expect(complaintPage.returnAssignee()).toEqual("", "The assignee name is displayed");
    });

    it('Verify if reader is displayed in participants table', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Participants");
        complaintPage.participantTable();
        expect(complaintPage.returnParticipantTypeForthRow()).toEqual("reader", "participant type is not correct in forth row");
        expect(complaintPage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name is not correct in forth row");
    });

    it('should create new complaint add/edit timeSheet and verify the time widget data in Complaints overview page', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes();
        complaintPage.submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            complaintPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            complaintPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            complaintPage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            complaintPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("1");
            timeTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.verifyTimeWidgetData("7");

        });
    });


    //Add a document to document management

    it('Verify adding correspondence document', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addCorrespondence("complaint", "Clearance Granted");
        complaintPage.verifyTheNotificationMessage("Case File ", "The notification message after adding document is not correct");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn1, "Clearance Granted");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn2, ".docx");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn3, "Clearance Granted");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn6, Objects.taskspage.data.assigneeSamuel);
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn7, "1.0");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn8, "ACTIVE");

    });

});
