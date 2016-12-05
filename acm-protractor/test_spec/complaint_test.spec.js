var logger = require('../log');
var utils = require('../util/utils.js');
var complaintPage = require('../Pages/complaint_page.js');
var userPage = require('../Pages/user_profile_page.js');
var authentication = require('../authentication.js');
var taskPage = require('../Pages/task_page.js');
var Objects = require('../json/Objects.json');
var loginPage = require('../Pages/login_page.js');
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

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();

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

    it('should create new complaint and verify text details add verify if is saved', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        complaintPage.clickSaveDetailsButton();
        complaintPage.clickRefreshButton();
        expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');

    });

    it('should create new complaint and add link from details', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickInsertLinkInDetails();
        expect(complaintPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle);
        complaintPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.insertLinkText, 'The link is not added');
        complaintPage.clickSaveDetailsButton();
        expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.insertLinkText, 'The link is not mathcing the expected value');

    });

    it('should create new complaint and add picture from details', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickDetailsAddPicture();
        complaintPage.uploadPicture();
        expect(complaintPage.returnDetailsUploadedImage().toBeTruthy(), "The picture is not uploaded succesfully");

    });

    it('should create new complaint and close complaint with Open Investigation, approve automatic generated task and validate created case', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.clickCloseComplaint().switchToIframes().closeComplaint("Open Investigation", Objects.complaintPage.data.description, Objects.complaintPage.data.approver);
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Tasks").waitForTasksTable();
        complaintPage.clickRefreshButton();
        expect(complaintPage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        complaintPage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
        complaintPage.navigateToPage("Cases").clickExpandFancyTreeTopElementAndSubLink("Details");
        expect(complaintPage.returnDetailsTextArea()).toContain(Objects.casepage.data.automatedTaskTitle);

    });

    it('should create new complaint and verify adding new Report of Investigation document', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName);
        complaintPage.reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addDocument("Report of Investigation");
        complaintPage.switchToIframes().submitReportOfInvestigation(Objects.basepage.data.reportTitle, Objects.taskspage.data.assigneeSamuel);
        complaintPage.switchToDefaultContent().validateDocGridData(true, "Report of Investigation", ".pdf", "Report of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

})


