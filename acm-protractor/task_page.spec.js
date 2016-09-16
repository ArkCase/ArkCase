var taskPage = require('./Pages/task_page.js');
var tasksPage = require('./Pages/tasks_page.js');
var authentication = require('./authentication.js');
var now = new Date();
var day = ("0" + now.getDate()).slice(-2);
var month = ("0" + (now.getMonth() + 1)).slice(-2);
var today = (month) + "/" + (day) + "/" + now.getFullYear();
var flag = false;

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 8000);
}


describe('Create new task ', function() {

    beforeEach(function(done) {

        authentication.loginAsSupervisor();
        testAsync(done);

    });

    afterEach(function() {

        authentication.logout();
        browser.ignoresynchronization = true;
    });


    it('should create new task status active', function() {
        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('This is test');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.percentCompleteInput.click();
        taskPage.percentCompleteInput.clear();
        taskPage.percentCompleteInput.sendKeys('45');
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.taskState.getText()).toEqual('ACTIVE');
    });


    it('should create new task with priority low', function() {
        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('priority low');
        taskPage.priorityDropDown.click();
        taskPage.priorityLow.click();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.priority.getText()).toEqual('Low');
    });

    it('should create new task with priority high', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('priority high');
        taskPage.priorityDropDown.click();
        taskPage.priorityHigh.click();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.priority.getText()).toEqual('High');
    });

    it('should create new task with priority Expedite', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('priority expedite');
        taskPage.priorityDropDown.click();
        taskPage.priorityExpedite.click();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.priority.getText()).toEqual('Expedite');
    });

    it('should verify save button is disabled when subject is empty and due date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        expect(taskPage.saveButton.isEnabled()).toBe(false);
    });

    it('should verify save button disabled when perecent is empty', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.percentCompleteInput.click();
        taskPage.percentCompleteInput.clear();
        expect(taskPage.saveButton.isEnabled()).toBe(false);

    });


    it('should create new task with notes verify in details section', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('notes');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.notesTextArea.click();
        taskPage.notesTextArea.sendKeys('notes');
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        tasksPage.detailsLink.click();
        expect(tasksPage.detailsTextArea.getText()).toEqual('notes');

    });

    it('should create new task verify task subject in Tasks page', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify notes');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.taskSubject.getText()).toEqual('verify notes');

    });

    it('shoudl create new task verify assignee in Tasks page', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify assignee');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.assignee.getText()).toEqual('Samuel Supervisor');

    });

    it('should create new task verify cretaed date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify startDate');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.startDate.getText()).toEqual(today);

    });

    it('should create new task verify due date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify startDate');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        expect(tasksPage.dueDate.getText()).toEqual(today);

    });

    it('should create new task verify perecent', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify due date');
        taskPage.percentCompleteInput.click();
        taskPage.percentCompleteInput.clear();
        taskPage.percentCompleteInput.sendKeys('80');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(tasksPage.percent.getText()).toEqual('80');

    });

    it('should create new task click complete button and verify task state', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify complete state');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.completeBtn.click();
        expect(tasksPage.taskState.getText()).toEqual('CLOSED');

    });

    it('should create new task click delete button and verify task state', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify complete state');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.deleteBtn.click();
        expect(tasksPage.taskState.getText()).toEqual('DELETE');

    });


    it('should create new task with diffrent user', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.assigneeInput.click();
        taskPage.searchUserInput.click();
        taskPage.searchUserInput.sendKeys('Ann administrator');
        taskPage.searchUserBtn.click();
        taskPage.searchedName.click();
        taskPage.confimrBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify complete state');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(tasksPage.assignee.getText()).toEqual('Ann Administrator');

    });

    it('should create new task add link verify', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('link');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.linkButton.click();
        taskPage.linkInputText.sendKeys('armedia');
        taskPage.linkInputUrl.clear();
        taskPage.linkInputUrl.sendKeys('http://armedia.com/');
        taskPage.insertLinkBtn.click();
        taskPage.saveButton.click();
        expect(taskPage.tasksTitle.getText()).toEqual('Tasks');
        tasksPage.detailsLink.click();
        expect(tasksPage.detailsTextArea.getText()).toEqual('armedia');

    });

    it('should create new task click subscribe button verify if it si changed to unsubscribe', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('subscribe button');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.subscribeBtn.click();
        expect(tasksPage.unsubscribeBtn.getText()).toEqual('Unsubscribe');

    });

    it('should create new task click unsubscribe button verify if it is changed to subscribe', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('unsubscribe button');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.subscribeBtn.click();
        expect(tasksPage.unsubscribeBtn.getText()).toEqual('Unsubscribe');
        tasksPage.unsubscribeBtn.click();
        expect(tasksPage.subscribeBtn.getText()).toEqual('Subscribe');

    });


});
