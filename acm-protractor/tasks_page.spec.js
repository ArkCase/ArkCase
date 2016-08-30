var taskPage = require('./Pages/task_page.js');
var tasksPage = require('./Pages/tasks_page.js');
var authentication = require('./authentication.js');

var flag = false;
var now = new Date();
var day = ("0" + now.getDate()).slice(-2);
var month = ("0" + (now.getMonth() + 1)).slice(-2);
var today = (month) + "/" + (day) + "/" + now.getFullYear();

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 10000);
}

// Specs
describe("Testing async calls with beforeEach and passing the special done callback around", function() {

    beforeEach(function(done) {
        // Make an async call, passing the special done callback        
        testAsync(done);
    });

    it("Should be true if the async call has completed", function() {
        expect(flag).toEqual(true);
    });

});

describe('tasks page test', function() {

    beforeEach(function() {

        authentication.loginAsSupervisor();

    });

    afterEach(function() {

        authentication.logout();

    });

   
        it('should create new task and add note', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('Add note');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
            taskPage.saveButton.click();
            tasksPage.notesLink.click();
            expect(tasksPage.notesTableTitle.getText()).toEqual('Notes')
            tasksPage.addNoteBtn.click();
            expect(tasksPage.notePopUpTitle.getText()).toEqual('Add Note');
            tasksPage.noteTextArea.click();
            tasksPage.noteTextArea.sendKeys('note');
            tasksPage.noteSaveBtn.click();
            expect(tasksPage.noteName.getText()).toEqual('note');
            expect(tasksPage.noteCreatedDate.getText()).toEqual(today);
            expect(tasksPage.noteAuthor.getText()).toEqual('samuel-acm');

        });

        it('sould create new task add note and delete the note', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('Add note');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
            taskPage.saveButton.click();
            tasksPage.notesLink.click();
            expect(tasksPage.notesTableTitle.getText()).toEqual('Notes')
            tasksPage.addNoteBtn.click();
            expect(tasksPage.notePopUpTitle.getText()).toEqual('Add Note');
            tasksPage.noteTextArea.click();
            tasksPage.noteTextArea.sendKeys('note');
            tasksPage.noteSaveBtn.click();
            tasksPage.deleteNoteBtn.click();
            expect(tasksPage.noteName.isPresent()).toBe(false, 'The note is not deleted');

        });

        it('should create new task add note and edit the note', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('Add note');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            expect(taskPage.DueDateInput.getText()).not.toBeTruthy();
            taskPage.saveButton.click();
            tasksPage.notesLink.click();
            expect(tasksPage.notesTableTitle.getText()).toEqual('Notes')
            tasksPage.addNoteBtn.click();
            expect(tasksPage.notePopUpTitle.getText()).toEqual('Add Note');
            tasksPage.noteTextArea.click();
            tasksPage.noteTextArea.sendKeys('note');
            tasksPage.noteSaveBtn.click();
            tasksPage.editNoteBtn.click();
            expect(tasksPage.notePopUpTitle.getText()).toEqual('Edit Record');
            tasksPage.noteTextArea.click();
            tasksPage.noteTextArea.clear();
            tasksPage.noteTextArea.sendKeys('note2');
            tasksPage.noteSaveBtn.click();
            expect(tasksPage.noteName.getText()).toEqual('note2', 'The note is not updated');

        });

        it('should create new tsak and edit priority to low', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit priority to low');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.priority.click();
            tasksPage.priorityDropDown.click();
            tasksPage.priorityLow.click();
            tasksPage.priorityBtn.click();
            expect(tasksPage.priority.getText()).toEqual('Low', "Priority is not updated");

        });

        it('should create new tsak and edit priority to high', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit priority to high');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.priority.click();
            tasksPage.priorityDropDown.click();
            tasksPage.priorityHigh.click();
            tasksPage.priorityBtn.click();
            expect(tasksPage.priority.getText()).toEqual('High', "Priority is not updated");

        });

        it('should create new tsak and edit priority to Expedite', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit priority to expedite');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.priority.click();
            tasksPage.priorityDropDown.click();
            tasksPage.priorityExpedite.click();
            tasksPage.priorityBtn.click();
            expect(tasksPage.priority.getText()).toEqual('Expedite', "Priority is not updated");

        });

        it('should create new task and edit percent of completition', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit priority to expedite');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            taskPage.percentCompletition.click();
            taskPage.percentCompletitionInput.clear();
            taskPage.percentCompletitionInput.sendKeys('80');
            taskPage.percentCompletitionBtn.click();
            expect(taskPage.percentCompletition.getText()).toEqual('80', 'Percent is not updated');

        });

        it('should create new task and edit task subject', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit subject');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.taskSubject.click();
            tasksPage.taskSubjectInput.clear();
            tasksPage.taskSubjectInput.sendKeys('updated subject');
            tasksPage.taskSubjectBtn.click();
            expect(tasksPage.taskSubject.getText()).toEqual('updated subject', 'Task subject is not updated');

        });

        it('should create new task and edit assignee', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit assignee');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.assignee.click();
            tasksPage.assigneeDropDown.click();
            tasksPage.selectAssignee.click();
            tasksPage.assigneeBtn.click();
            expect(tasksPage.assignee.getText()).toEqual('Ann Administrator', 'The assignee is not updated');

        });

        it('should create new task change assignee and verify is button complete and delete are not displyed', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('edit assignee');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.assignee.click();
            tasksPage.assigneeDropDown.click();
            tasksPage.selectAssignee.click();
            tasksPage.assigneeBtn.click();
            expect(tasksPage.completeBtn.isPresent()).toBe(false, 'Comeplete button should not be displyed');
            expect(tasksPage.deleteBtn.isPresent()).toBe(false, 'Delete  button should not be displyed');

        });

        it('should create new task add tag and verify added tag', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('add tag');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.tagsLink.click();
            expect(tasksPage.tagsTableTitle.getText()).toEqual('Tags');
            tasksPage.addTagBtn.click();
            expect(tasksPage.addTagPopUpTitle.getText()).toEqual('Tag');
            browser.driver.actions().mouseDown(tasksPage.tagTextArea).click().sendKeys("white").perform();
            tasksPage.addTagPopUpTitle.click();
            tasksPage.saveTagBtn.click();
            expect(tasksPage.tagname.getText()).toEqual('white');
            expect(tasksPage.tagCreatedDate.getText()).toEqual(today);
            expect(tasksPage.tagCreatedBy.getText()).toEqual('samuel-acm');

        });

        it('should create new task add tag and delete added tag', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('delete tag');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.tagsLink.click();
            expect(tasksPage.tagsTableTitle.getText()).toEqual('Tags');
            tasksPage.addTagBtn.click();
            expect(tasksPage.addTagPopUpTitle.getText()).toEqual('Tag');
            browser.driver.actions().mouseDown(tasksPage.tagTextArea).click().sendKeys("pink").perform();
            tasksPage.addTagPopUpTitle.click();
            tasksPage.saveTagBtn.click();
            tasksPage.tagDeleteBtn.click();
            expect(tasksPage.tagname.isPresent()).toBe(false, 'The tag is not deleted');

        });

        it('should create new task add text task details verify if it saved', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('details');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.detailsLink.click();
            tasksPage.detailsTextArea.click();
            tasksPage.detailsTextArea.sendKeys('This is test');
            tasksPage.detailsSaveBtn.click();
            tasksPage.refreshBtn.click();
            expect(tasksPage.detailsTextArea.getText()).toEqual('This is test', 'After refresh the details text is not saved');

        });

        it('should create new task and verify workflow table data', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('workflow');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.workflowLink.click();
            expect(tasksPage.workflowTitle.getText()).toEqual('Workflow');
            expect(tasksPage.workflowParticipant.getText()).toEqual('Samuel Supervisor');
            expect(tasksPage.workflowStatus.getText()).toEqual('ACTIVE');
            expect(tasksPage.workflowStartDate.getText()).toEqual(today);

        });

        it('should create new task and verify history table data', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('history');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.historyLink.click();
            expect(tasksPage.historyTableTitle.getText()).toEqual('History');
            expect(tasksPage.historyEventName.getText()).toEqual('Task Created');
            expect(tasksPage.historyUser.getText()).toEqual('samuel-acm');
            expect(tasksPage.historyDate.getText()).toContain(today);

        });

        it('should create new task add link from task details', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            taskPage.Subject.click();
            taskPage.Subject.sendKeys('add link');
            expect(taskPage.StartDate.getText()).not.toBeTruthy();
            taskPage.DueDateBtn.click();
            taskPage.todayDateFromCalendar.click();
            taskPage.saveButton.click();
            tasksPage.detailsLink.click();
            tasksPage.detailsLinkBtn.click();
            expect(tasksPage.insertLinkTitle.getText()).toEqual('Insert Link');
            tasksPage.insertLinkText.click();
            tasksPage.insertLinkText.sendKeys('arkcase');
            tasksPage.insertLinkUrl.clear();
            tasksPage.insertLinkUrl.sendKeys('http://www.arkcase.com/');
            tasksPage.insertLinkBtn.click();
            expect(tasksPage.detailsTextArea.getText()).toEqual('arkcase', 'The link is not added');
            tasksPage.detailsSaveBtn.click();
            expect(tasksPage.detailsTextArea.getText()).toEqual('arkcase', 'The link is not added');

        });

        it('should navigate to task page and click on header image and verify if redirects to home page', function() {

            taskPage.newBtn.click();
            expect(taskPage.taskBtnName.getText()).toEqual('Task');
            taskPage.taskBtn.click();
            expect(taskPage.taskTitle.getText()).toEqual('New Task');
            tasksPage.headerImageLink.click();
            expect(tasksPage.dashboardTitle.getText()).toEqual('Dashboard', 'Header image does not redirects to home page');
            

        });

 

    it('should create new task navigate to attachments section ', function() {

        taskPage.newBtn.click();
        expect(taskPage.taskBtnName.getText()).toEqual('Task');
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add link');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.attachmentsLink.click();
        expect(tasksPage.attachmentsTableTitle.getText()).toEqual('Attachments', 'Attachments table title is wrong');
        

    });

});
