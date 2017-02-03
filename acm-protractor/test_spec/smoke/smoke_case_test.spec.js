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

    beforeEach(function (done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function () {
        loginPage.Logout();

    });

    //Create New Case, make sure the new object is created

    it('should create new case ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle, "Case title is not correct");
    });

    //verify that case type is correct on new created case

    it('should create new case and verify case type', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType, "Case type is not correct");
    });

   // close case and make sure the files are declared as records on the Alfresco site

    it('should create new case and change case status to closed, verify the automated task in tasks table and approve', function () {

        casePage.clickModuleCasesFiles();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatus("Closed");
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    //verify that priority is correct on new created case

    it('should verify the priority filed', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium, "Priority is not correct");
    });

    //verify that created date is correct on new created case

    it('should   verify the created date', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnCreatedDate()).toEqual(utils.returnToday("/"), "Created date is not correct");

    });

    //verify people initiator on new created case

    it('should  verify the people initiator', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickPeopleLinkBtn();
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor, "People type is not correct");
        expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstName, "People first name is not correct");
        expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastName, "People last name is not correct");
    });

    //verify history table on new created case

    it('should create new case and verify the history table', function () {

        casePage.clickModuleCasesFiles();
        casePage.historyTable();
        expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent, "History event name is not correct");
        expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date is not correct");
        expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel, "History assignee is not correct");

    });

    //verify assignee by default on new created case

    it('should create new case and verify the assignee by default', function () {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is not correct");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator", "Participant name in second row is not correct");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "Participant type in third row is not correct");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant row in third row is not correct");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "Participant type in forth row is not correct");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name in forth row is not correct");
    });

    //verify assigned to, owning group and due date

    it('should  verify assigned to, owning group and due date', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnDueDate()).toEqual(utils.returnDate("/", 180), "Default due date is not correct");
        expect(casePage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, "Asignee is not correct");
        expect(casePage.returnOwningGroup()).toEqual(Objects.casepage.data.owningGroup, "Default owning group is not correct");

    });

    //verify Add Notes

    it('should  add/delete note', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.deleteNote();
    });

    //Create a task associated to case

    it('should  add task from tasks table verify the task', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnTaskTableTitle()).toContain(Objects.taskpage.data.Subject, "Task subject is not correct");
        expect(casePage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Task assignee is not correct");
        expect(casePage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"), "Task created date is not correct");
        expect(casePage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium, "Task priority is not correct");
        expect(casePage.returnTaskTableDueDate()).toEqual(utils.returnToday("/"), "Task due date is not correct");
        expect(casePage.returnTaskTableStatus()).toEqual("ACTIVE", "Task status is not correct");
    });



    //verify adding document to document management

    it('should create new case and verify adding correspondence document', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElement();
        casePage.rightClickRootFolder().addCorrespondence("case", "Notice of Investigation");
        casePage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    //View document (Click Open)

    it('should verify adding note in document viewer in cases', function () {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
        casePage.moveToTab().clickDocViewNotesLink().submitNote(Objects.basepage.data.note);
        expect(casePage.returnSavedNoteInGrid()).toEqual(Objects.basepage.data.note, "Note is not saved in document viewer");

    });

    //Email document (Click Email)

    it('should verify sending document through email', function () {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Email");
        casePage.sendEmail(Objects.basepage.data.email);
    });



    //Add details on new created case

    it('Verify text details add verify if is saved', function() {

        casePage.clickModuleCasesFiles();
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        casePage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        casePage.clickSaveDetailsButton();
        casePage.clickRefreshButton();
        expect(casePage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');

    });
});
