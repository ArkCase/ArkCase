var taskPage = require('./Pages/task_page.js');
var tasksPage = require('./Pages/tasks_page.js');
var authentication = require('./authentication.js');
var utils = require('./utils.js');
var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
var bot = require(process.env['USERPROFILE'] + '/node_modules/robot-js');
var flag = false;
var now = new Date();
var day = ("0" + now.getDate()).slice(-2);
var month = ("0" + (now.getMonth() + 1)).slice(-2);
var today = (month) + "/" + (day) + "/" + now.getFullYear();

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 20000);
}


describe('tasks page test', function() {

    beforeEach(function(done) {

        authentication.loginAsSupervisor();
        testAsync(done);

    });

    afterEach(function() {

        authentication.logout();

    });

    it('should create new task and add note', function() {

        taskPage.newBtn.click();
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
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('edit percen');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.percentCompletition.click();
        tasksPage.percentCompletitionInput.clear();
        tasksPage.percentCompletitionInput.sendKeys('80');
        tasksPage.percentCompletitionBtn.click();
        expect(tasksPage.percentCompletition.getText()).toEqual('80', 'Percent is not updated');

    });


    it('should create new task and edit task subject', function() {

        taskPage.newBtn.click();
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
        tasksPage.assigneeBtn.click().then(function() {
            expect(tasksPage.completeBtn.isPresent()).toBe(false, 'Comeplete button should not be displyed');
            expect(tasksPage.deleteBtn.isPresent()).toBe(false, 'Delete  button should not be displyed');

        });
    });

    it('should create new task add tag and verify added tag', function() {

        taskPage.newBtn.click();
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
        browser.driver.actions().mouseDown(tasksPage.tagTextArea).click().sendKeys("south").perform();
        tasksPage.addTagPopUpTitle.click();
        tasksPage.saveTagBtn.click();
        expect(tasksPage.tagname.getText()).toEqual('south', 'Created tag name is wrong');
        expect(tasksPage.tagCreatedDate.getText()).toEqual(today, 'Created tag date is wrong');
        expect(tasksPage.tagCreatedBy.getText()).toEqual('samuel-acm', 'Created tag by is wrong');

    });

    it('should create new task add tag and delete added tag', function() {

        taskPage.newBtn.click();
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
        browser.driver.actions().mouseDown(tasksPage.tagTextArea).click().sendKeys("north").perform();
        tasksPage.addTagPopUpTitle.click();
        tasksPage.saveTagBtn.click();
        tasksPage.tagDeleteBtn.click();
        expect(tasksPage.tagname.isPresent()).toBe(false, 'The tag is not deleted');

    });

    it('should create new task add text task details verify if it saved', function() {

        taskPage.newBtn.click();
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
        expect(tasksPage.historyUser.getText()).toEqual('Samuel Supervisor');
        expect(tasksPage.historyDate.getText()).toContain(today);

    });

    it('should create new task add link from task details', function() {

        taskPage.newBtn.click();
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
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        tasksPage.headerImageLink.click();
        expect(tasksPage.dashboardTitle.getText()).toEqual('Dashboard', 'Header image does not redirects to home page');

    });

    it('should create new task navigate to attachments section add png document', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add document png');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.attachmentsLink.click();
        });
        expect(tasksPage.attachmentsTableTitle.getText()).toEqual('Attachments', 'Attachments table title is wrong');
        tasksPage.root.click().then(function() {
            utils.mouseMoveToRoot();
        });
        tasksPage.newDocument.click().then(function() {
            tasksPage.otherDocument.click().then(function() {
                utils.uploadPng();
                expect(tasksPage.documentTitle.getText()).toEqual('imageprofile', 'Added document name is wrong, or document is not added');

            });
        });

    });


    it('should create new task navigate to attachments section add docx document', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add document docx');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.attachmentsLink.click();
        });
        tasksPage.root.click().then(function() {
            utils.mouseMoveToRoot();
        });
        tasksPage.newDocument.click().then(function() {
            tasksPage.otherDocument.click().then(function() {
                utils.uploadDocx();
                expect(tasksPage.documentTitle.getText()).toEqual('ArkCaseTesting', 'Added document name is wrong');

            });

        });
    });


    it('should create new task navigate to attachemnts section add xlsx document', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add document xlsx');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.attachmentsLink.click();
        });
        tasksPage.root.click().then(function() {
            utils.mouseMoveToRoot();
        });
        tasksPage.newDocument.click().then(function() {
            tasksPage.otherDocument.click().then(function() {
                utils.uploadXlsx();
                expect(tasksPage.documentTitle.getText()).toEqual('caseSummary', 'Added document name is wrong');

            });

        });
    });

    it('should create new task navigate to attachemnts section add pdf document', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add document pdf');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.attachmentsLink.click();
        });
        tasksPage.root.click().then(function() {
            utils.mouseMoveToRoot();
        });
        tasksPage.newDocument.click().then(function() {
            tasksPage.otherDocument.click().then(function() {
                browser.driver.sleep(2000);
                utils.uploadPdf();
                expect(tasksPage.documentTitle.getText()).toEqual('caseSummary', 'Added document name is wrong');

            });

        });
    });


    it('should create new task and add new folder in documents section', function() {


        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add new folder');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click().then(function() {
            tasksPage.attachmentsLink.click();
        });
        tasksPage.root.click().then(function() {
            utils.mouseMoveToRoot();
        });
        tasksPage.newFolder.click().then(function() {
            tasksPage.documentTitleInput.clear();
            tasksPage.documentTitleInput.sendKeys('folder');
            tasksPage.attachmentsTableTitle.click();
            expect(tasksPage.documentTitle.getText()).toEqual('folder');

        });
    });

    it('should create new task and edit the start date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('edit start date');
        taskPage.StartDate.click();
        taskPage.StartDate.clear();
        taskPage.StartDate.sendKeys('08/15/2016');
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.saveButton.click();
        tasksPage.startDate.click().then(function() {
            tasksPage.startDateInput.click();
            tasksPage.startDateToday.click();
            tasksPage.startDateConfrimBtn.click();
            expect(tasksPage.startDate.getText()).toEqual(today);
        });
    });


    it('should create new task and edit due date', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('edit due date');
        taskPage.DueDateInput.click();
        taskPage.DueDateInput.sendKeys('10/15/2016');
        taskPage.saveButton.click().then(function() {
            tasksPage.dueDate.click().then(function() {
                tasksPage.dueDateInput.click();
                tasksPage.dueDateToday.click();
                tasksPage.dueDateConfirmBtn.click();
            });

            expect(tasksPage.dueDate.getText()).toEqual(today);
        });
    });



    it('should create new task,add picture and verify in details', function() {

        taskPage.newBtn.click();
        taskPage.taskBtn.click();
        expect(taskPage.taskTitle.getText()).toEqual('New Task');
        taskPage.Subject.click();
        taskPage.Subject.sendKeys('add picture');
        expect(taskPage.StartDate.getText()).not.toBeTruthy();
        taskPage.DueDateBtn.click();
        taskPage.todayDateFromCalendar.click();
        taskPage.pictureButton.click();
        taskPage.chooseFilesBtn.click().then(function() {
            utils.uploadPng();
            expect(taskPage.notesTextArea.getText()).not.toBeTruthy();
        });
        taskPage.saveButton.click().then(function() {
            tasksPage.detailsLink.click().then(function() {
                expect(tasksPage.detailsTextArea.getText()).not.toBeTruthy();

            });
        });
    });

});
