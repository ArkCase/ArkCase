var logger = require('../log');
var utils = require('../util/utils.js');
var complaintPage = require('../Pages/complaint_page.js');
var userPage = require('../Pages/user_profile_page.js');
var authentication = require('../authentication.js');
var taskPage = require('../Pages/task_page.js');
var Objects = require('../json/Objects.json');
var flag = false;

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 20000);
}


describe('Create new complaint ', function() {

    beforeEach(function(done) {

        authentication.loginAsSupervisor();
        testAsync(done);

    });

    afterEach(function() {

        authentication.logout();

    });

    it('should create new complaint and verify adding correspondence document', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName);
        expect(complaintPage.returnFirstNameValue()).toEqual(Objects.complaintPage.data.firstName);
        expect(complaintPage.returnLastNameValue()).toEqual(Objects.complaintPage.data.lastName);
        complaintPage.reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addCorrespondence("complaint", "Notice of Investigation");
        complaintPage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    it('should create new complaint add new note and verify added note', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Notes");
        complaintPage.addNote(Objects.casepage.data.note);
        expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.note, "The note is succesfully added");
    });

    it('should create new complaint add/delete note', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Notes");
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.deleteNote();
        expect(complaintPage.returnNoteName()).toEqual("", "The note is not deleted");
    });

    it('should create new complaint add new note and edit added note', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Notes");
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.editNote(Objects.casepage.data.editnote);
        expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is succesfully edited");
    });

    it('should create new complaint and add task from tasks table verify the task', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Tasks");
        complaintPage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickCaseTitleInTasks();
        complaintPage.clickTasksLinkBtn().waitForTasksTable();
        expect(complaintPage.returnTaskTitle()).toContain(Objects.casepage.data.automatedTaskTitle);
        expect(complaintPage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
        expect(complaintPage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium);
        expect(complaintPage.returnTaskTableDueDate()).toEqual(Objects.taskspage.datataskStateActive);
    });

    it('should create new complaint and verify adding new document', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName);
        expect(complaintPage.returnFirstNameValue()).toEqual(Objects.complaintPage.data.firstName);
        expect(complaintPage.returnLastNameValue()).toEqual(Objects.complaintPage.data.lastName);
        complaintPage.reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addDocument("Notice of Investigation");
        utils.uploadDocx();
        complaintPage.validateDocGridData(true, "ArkCaseTesting", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });


})


