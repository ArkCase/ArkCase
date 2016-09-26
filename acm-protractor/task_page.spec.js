var taskPage = require('./Pages/task_page.js');
var tasksPage = require('./Pages/tasks_page.js');
var authentication = require('./authentication.js');
var Objects = require('./Objects.json');
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
        expect(taskPage.taskTitle.getText()).toEqual(Objects.taskpage.data.taskTitle);
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.percentCompleteInput.click();
        taskPage.percentCompleteInput.clear();
        taskPage.percentCompleteInput.sendKeys(Objects.taskpage.data.percentCompleteInput);
        taskPage.saveButton.click().then(function() {
            expect(taskPage.tasksTitle.getText()).toEqual(Objects.taskpage.data.tasksTitle);
            expect(tasksPage.taskState.getText()).toEqual(Objects.taskpage.data.taskStateActive);
        });
    });


    it('should create new task with priority low', function() {
        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.priorityDropDown.click();
        taskPage.priorityLow.click();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.priority.getText()).toEqual(Objects.taskpage.data.priorityLow);
        });
    });

    it('should create new task with priority high', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.priorityDropDown.click();
        taskPage.priorityHigh.click();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.priority.getText()).toEqual(Objects.taskpage.data.priorityHigh);
        });
    });

    it('should create new task with priority Expedite', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.priorityDropDown.click();
        taskPage.priorityExpedite.click();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.priority.getText()).toEqual(Objects.taskpage.data.priorityExpedite);
        });
    });

    it('should verify save button is disabled when subject is empty and due date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
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
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.notesTextArea.click();
        taskPage.notesTextArea.sendKeys(Objects.taskpage.data.notesTextArea);
        taskPage.saveButton.click().then(function() {
            tasksPage.detailsLink.click();
            expect(tasksPage.detailsTextArea.getText()).toEqual(Objects.taskpage.data.notesTextArea);
        });
    });

    it('should create new task verify task subject in Tasks page', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.taskSubject.getText()).toEqual(Objects.taskpage.data.Subject);
        });
    });

    it('shoudl create new task verify assignee in Tasks page', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        expect(tasksPage.assignee.getText()).toEqual(Objects.taskspage.assigneeSamuel);

    });

    it('should create new task verify cretaed date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.startDate.getText()).toEqual(today);
        });

    });

    it('should create new task verify due date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.dueDate.getText()).toEqual(today);
        });
    });

    it('should create new task verify perecent', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.percentCompleteInput.click();
        taskPage.percentCompleteInput.clear();
        taskPage.percentCompleteInput.sendKeys(Objects.taskpage.data.percentCompleteInput);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.percent.getText()).toEqual(Objects.taskpage.data.percentCompleteInput);
        });
    });

    it('should create new task click complete button and verify task state', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('verify complete state');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.completeBtn.click();
            expect(tasksPage.taskState.getText()).toEqual(taskspage.data.taskStateClosed);
        });
    });

    it('should create new task click delete button and verify task state', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.deleteBtn.click();
            expect(tasksPage.taskState.getText()).toEqual(Objects.taskspage.taskStateDelete);
        });
    });


    it('should create new task with diffrent user', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.assigneeInput.click();
        taskPage.searchUserInput.click();
        taskPage.searchUserInput.sendKeys(Objects.taskpage.data.searchUser);
        taskPage.searchUserBtn.click();
        taskPage.searchedName.click();
        taskPage.confimrBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            expect(tasksPage.assignee.getText()).toEqual(Objects.taskpage.data.searchUser);
        });
    });

    it('should create new task add link verify', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.linkButton.click().then(function() {
            taskPage.linkInputText.sendKeys(Objects.taskpage.data.linkInputText);
            taskPage.linkInputUrl.clear();
            taskPage.linkInputUrl.sendKeys(Objects.taskpage.data.linkInputUrl);
            taskPage.insertLinkBtn.click();
            taskPage.saveButton.click();
            expect(taskPage.tasksTitle.getText()).toEqual(Objects.taskpage.data.tasksTitle);
            tasksPage.detailsLink.click();
            expect(tasksPage.detailsTextArea.getText()).toEqual(Objects.taskpage.data.linkInputText);
        });

    });

    it('should create new task click subscribe button verify if it si changed to unsubscribe', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.subscribeBtn.click();
            expect(tasksPage.unsubscribeBtn.getText()).toEqual(Objects.taskspage.data.unsubscribeBtn);
        });
    });

    it('should create new task click unsubscribe button verify if it is changed to subscribe', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        taskPage.Subject.click();
        taskPage.Subject.sendKeys(Objects.taskpage.data.Subject);
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.subscribeBtn.click();
            expect(tasksPage.unsubscribeBtn.getText()).toEqual(Objects.taskspage.data.unsubscribeBtn);
            tasksPage.unsubscribeBtn.click();
            expect(tasksPage.subscribeBtn.getText()).toEqual(Objects.taskspage.data.subscribeBtn);

        });
    });
});
