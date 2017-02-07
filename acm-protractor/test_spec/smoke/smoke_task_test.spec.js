var logger = require('../../log');
var utils = require('../../util/utils.js');
var taskPage = require('../../Pages/task_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var loginPage = require('../../Pages/login_page.js');
var Objects = require('../../json/Objects.json');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var preferencesPage = require('../../Pages/preference_page.js');
var flag = false;

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 30000);
}


describe('Create new task ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();
    });

    //Validate create new add hoc task

   it('should create new task status active', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskspage.data.taskTitle, "Tasks module title is not correct");
        taskPage.insertSubject(Objects.taskpage.data.Subject);
        expect(taskPage.returnStartDateInput()).not.toBeTruthy();
        taskPage.insertDueDateToday();
        expect(taskPage.returnDueDateInput()).not.toBeTruthy();
        taskPage.insertPercentComplete(Objects.taskpage.data.percentCompleteInput).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle, "Task title is not correct");
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateActive, "Task status is not correct");

    });

   //Complete the adhoc task, Make sure the automated task is created and approve it

   it('should create new task click complete button and verify task state', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickCompleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, "After complete task status is not changed into closed");

    });

   //verify workflow table data on new created task

    it('should create new task and verify workflow table data', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.workflowLink.click();
        expect(taskPage.returnWorkflowTitle()).toEqual(Objects.taskspage.data.workflowTitle, "Workflow title is not correct");
        expect(taskPage.returnWorkflowParticipant()).toEqual(Objects.taskspage.data.supervisor, "Workflow participant is not correct");
        expect(taskPage.returnWorkflowStatus()).toEqual(Objects.taskspage.data.workflowStatus, "Workflow status is not correct");
        expect(taskPage.returnWorkflowStartDate()).toEqual(utils.returnToday("/"), "Workflow start date is not correct");

    });

    //verify history data on new created task

    it('should create new task and verify history table data', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickHistoryLink();
        expect(taskPage.returnHistoryTableTitle()).toEqual(Objects.taskspage.data.historyTableTitle, "History title is not correct");
        expect(taskPage.returnHistoryEventName()).toEqual(Objects.taskspage.data.historyEventName, "History event name is not correct");
        expect(taskPage.returnHistoryUser()).toEqual(Objects.taskspage.data.supervisor, "History user is not correct");
        expect(taskPage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date is not correct");

    });


});
