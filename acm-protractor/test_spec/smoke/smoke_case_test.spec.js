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

    // Create New Case, make sure the new object is created

    it('should create new case ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle, "Case title is not correct");
    });

    // close case and make sure the files are declared as records on the Alfresco site

    it('should create new case and change case status to closed, verify the automated task in tasks table and approve', function () {

        casePage.clickModuleCasesFiles();
        casePage.waitForCaseTitle();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().changeCaseSubmit(Objects.casepage.data.approverSamuel, "Closed");
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    //Create a task associated to case

    it('should  add add hoc task from tasks table and verify the task', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDate(utils.returnToday("/")).clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Tasks").waitForTasksTable();
        expect(casePage.returnTaskTableTitle()).toContain(Objects.taskpage.data.Subject, "Task subject is not correct");
        expect(casePage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Task assignee is not correct");
        expect(casePage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"), "Task created date is not correct");
        expect(casePage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium, "Task priority is not correct");
        expect(casePage.returnTaskTableDueDate()).toEqual(utils.returnToday("/"), "Task due date is not correct");
        expect(casePage.returnTaskTableStatus()).toEqual("ACTIVE", "Task status is not correct");
    });

    //verify that case type is correct on new created case

    it('should verify case type', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType, "Case type is not correct");
    });

    //verify that priority is correct on new created case

    it('should verify the priority filed', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium, "Priority is not correct");
    });

    //verify that created date is correct on new created case

    it('should verify the created date', function () {

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

    it('should verify the history table', function () {

        casePage.clickModuleCasesFiles();
        casePage.historyTable();
        expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent, "History event name is not correct");
        expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date is not correct");
        expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel, "History assignee is not correct");

    });

    //verify assignee by default on new created case

    it('should verify the assignee by default', function () {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is not correct");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "assignee label is not correct");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("", "assignee should be empty");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "owning group label is not correct");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "owning group is not correct");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "reader label is not correct");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "reader value is not current user");
    });

    //verify assigned to, owning group and due date

    it('should  verify assigned to, owning group and due date', function () {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnDueDate()).toEqual(utils.returnDate("/", 180), "Default due date is not correct");
        expect(casePage.returnAssignee()).toEqual("", "Asignee by default should be empty");
        expect(casePage.returnOwningGroup()).toEqual(Objects.casepage.data.owningGroup, "Default owning group is not correct");

    });

    //verify Add Notes

    it('should  add/delete note', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.deleteNote();
    });


    //verify adding document to document management

    it('should verify adding correspondence document', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElement();
        casePage.rightClickRootFolder().addCorrespondence("case", "Notice of Investigation");
        casePage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    //View document (Click Open)

    it('should verify view document', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
        casePage.moveToTab().switchToDocIframes();
        casePage.returnDoc();

    });

    //Email document (Click Email)

    it('should verify sending document through email', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Email");
        casePage.sendEmail(Objects.basepage.data.email);
    });



    //Add details on new created case

    it('Verify text details add verify if is saved', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Details");
        casePage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        casePage.clickSaveDetailsButton();
        casePage.clickRefreshButton();
        expect(casePage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');

    });



 });
