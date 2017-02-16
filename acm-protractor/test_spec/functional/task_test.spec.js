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

    it('should create new task status active', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskspage.data.taskTitle);
        taskPage.insertSubject(Objects.taskpage.data.Subject);
        expect(taskPage.returnStartDateInput()).not.toBeTruthy();
        taskPage.insertDueDateToday();
        expect(taskPage.returnDueDateInput()).not.toBeTruthy();
        taskPage.insertPercentComplete(Objects.taskpage.data.percentCompleteInput).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle, "Task title is not correct");
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateActive, "Task status is not correct");

    });

    using([{ priority: "High", prioritySaved: Objects.taskspage.data.priorityHigh }, { priority: "Low", prioritySaved: Objects.taskspage.data.priorityLow }, { priority: "Expedite", prioritySaved: Objects.taskspage.data.priorityExpedite }], function(data) {
        it('should create new task with priority ' + data.priority, function() {
            taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), data.priority, Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
            expect(taskPage.returnDueDateText()).not.toBeTruthy();
            taskPage.clickSave();
            expect(taskPage.returnPriority()).toEqual(data.prioritySaved, "priority" + data.priority + " is not updated");
        });
    });

    it('should verify save button is disabled when subject is empty and due date', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnSaveButtonEnabled()).toBe(false);
    });

    it('should verify save button disabled when percent is empty', function() {

        taskPage.clickNewButton().clickTaskButton().clearPercentInput();
        expect(taskPage.returnSaveButtonEnabled()).toBe(false);

    });

    it('should create new task with notes verify subject, assignee, start date, note', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        expect(taskPage.returnTaskSubject()).toEqual(Objects.taskpage.data.Subject, "task subject is not correct");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.assigneeSamuel, "task assignee is not correct");
        expect(taskPage.returnInsertedStartDate()).toEqual(utils.returnToday("/"), "task inserted date is not correct");
        expect(taskPage.returnInsertedDueDate()).toEqual(utils.returnToday("/"), "task due date is not correct");
        expect(taskPage.returnPercent()).toEqual(Objects.taskpage.data.percentCompleteInput, "task percent is not correct");
        taskPage.clickDetailsLink();
        taskPage.validateDetailsTextArea(Objects.taskspage.data.notesTextArea, "notes text area value is not correct");

    });

    it('should create new task click delete button and verify task state', function() {

        taskPage.clickModuleTasks();
        taskPage.clickDeleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateDelete, "Task status is not changed into deleted after deleting");

    });

    it('should create new task with different user', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskpage.data.searchUser, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, "Task assignee is not correct");

    });

    it('should create new task add link verify', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskDataLinkNote(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskpage.data.linkInputText, Objects.taskpage.data.linkInputUrl).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle, "Task title is not correct");
        taskPage.clickDetailsLink();
        taskPage.validateDetailsTextArea(Objects.taskpage.data.linkInputText, "details link is not correct");

    });

    it('should create new task click subscribe button verify if it is changed to unsubscribe', function() {

        taskPage.clickModuleTasks();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn, "Subscribe button text is not changed into unsubscribe after click on subscribe");

    });

    it('should create new task click unsubscribe button verify if it is changed to subscribe', function() {

        taskPage.clickModuleTasks();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn, "Subscribe button text is not changed into unsubscribe after click on subscribe");
        taskPage.clickUnsubscribeButton();
        expect(taskPage.returnSubscribeButtonText()).toEqual(Objects.taskspage.data.subscribeBtn, "Unsubscribe button text is not changed into subscribe after click on unsubscribe");
    });

    it('should create new task and add note', function() {

        taskPage.clickModuleTasks();
        taskPage.clickNotesLink();
        expect(taskPage.returnNotesTableTitle()).toEqual(Objects.taskspage.data.notesTableTitle)
        taskPage.clickAddNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.notePopUpTitle);
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.notesTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        expect("Note name is not correct", taskPage.returnNoteName()).toEqual(Objects.taskspage.data.notesTextArea);
        expect("Created date of note is not correct", taskPage.returnNoteCreatedDate()).toEqual(utils.returnToday("/"));
        expect("Supervisor is not correct", taskPage.returnNoteAuthor()).toEqual(Objects.taskspage.data.supervisor);



    });
    it('should create new task add note and delete the note', function() {

        taskPage.clickModuleTasks();
        taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickDeleteNoteButton();
        expect(taskPage.addedNoteNameIsPresent()).toBe(false, 'The note is not deleted');

    });
    it('should create new task add note and edit the note', function() {

        taskPage.clickModuleTasks();
        taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickEditNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.noteTitleEditRecord, "Note pop up title is not correct");
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.noteText);
        expect(taskPage.returnNoteName()).toEqual(Objects.taskspage.data.noteText, 'The note is not updated');



    });


    using([{ priority: "High", prioritySaved: Objects.taskspage.data.priorityHigh }, { priority: "Expedite", prioritySaved: Objects.taskspage.data.priorityExpedite }], function(data) {
        it('should create new task and edit priority to ' + data.priority, function() {

            taskPage.clickModuleTasks();
            taskPage.editPriority(data.priority);
            expect(taskPage.returnPriority()).toEqual(data.prioritySaved, "Priority is not updated");
        });
    });

    it('should create new task and edit percent of completition', function() {

        taskPage.clickModuleTasks();
        taskPage.editPercent(Objects.taskspage.data.percentCompletitionInput);
        expect(taskPage.returnPercent()).toEqual(Objects.taskspage.data.percentCompletitionInput, 'Percent is not updated');

    });
    it('should create new task and edit task subject', function() {

        taskPage.clickModuleTasks();
        taskPage.editTaskSubject(Objects.taskspage.data.taskSubjectInput);
        expect(taskPage.returnTaskSubject()).toEqual(Objects.taskspage.data.taskSubjectInput, 'Task subject is not updated');

    });
    it('should create new task and edit assignee from samuel to ann', function() {

        taskPage.clickModuleTasks();
        taskPage.editAssignee("Ann Administrator");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');

    });
    it('should create new task change assignee and verify is button complete and delete are not displayed', function() {

        taskPage.clickModuleTasks();
        taskPage.editAssignee("Ann Administrator");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');
        expect(taskPage.completeButtonIsPresent()).toBe(false, 'Complete button should not be displyed');
        expect(taskPage.deleteButtonIsPresent()).toBe(false, 'Delete  button should not be displyed');

    });
    it('should create new task add tag and verify added tag', function() {

        taskPage.clickModuleTasks();
        taskPage.clickTagsLink();
        expect(taskPage.returnTagsTableTittle()).toEqual(Objects.taskspage.data.tagsTableTitle);
        taskPage.clickAddTagButton();
        expect(taskPage.returnAddTagPopUpTitle()).toEqual(Objects.taskspage.data.addTagPopUpTitle);
        browser.driver.actions().mouseDown(taskPage.tagTextArea).click().sendKeys(Objects.taskspage.data.tagTextArea).perform();
        taskPage.clickSaveTagButton();
        expect(taskPage.returnTagName()).toEqual(Objects.taskspage.data.tagTextArea, 'Created tag name is wrong');
        expect(taskPage.returnTagCreatedDate()).toEqual(today, 'Created tag date is wrong');
        expect(taskPage.returnTagCreatedBy()).toEqual(Objects.taskspage.data.supervisor, 'Created tag by is wrong');

    });
    it('should create new task add tag and delete added tag', function() {

        taskPage.clickModuleTasks();
        taskPage.clickTagsLink();
        expect(taskPage.returnTagsTableTittle()).toEqual(Objects.taskspage.data.tagsTableTitle, "Tags table title is not correct");
        taskPage.clickAddTagButton();
        expect(taskPage.returnAddTagPopUpTitle()).toEqual(Objects.taskspage.data.addTagPopUpTitle, "Add tag pop up title is not correct");
        browser.driver.actions().mouseDown(taskPage.tagTextArea).click().sendKeys(Objects.taskspage.data.tagTextArea).perform();
        taskPage.clickSaveTagButton();
        taskPage.clickDeleteTagButton();
        expect(taskPage.returnTagNameisPresent()).toBe(false, 'The tag is not deleted');

    });
    it('should create new task add text task details verify if it saved', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        taskPage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        taskPage.clickSaveDetailsButton();
        taskPage.clickRefreshButton();
        taskPage.validateDetailsTextArea(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');


    });

    it('should create new task add link from task details', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        taskPage.clickInsertLinkInDetails();
        expect(taskPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle, "Insert link title in details is not correct");
        taskPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        taskPage.validateDetailsTextAres(Objects.taskspage.data.insertLinkText, 'The link is not added');
        taskPage.clickSaveDetailsButton();
        taskPage.validateDetailsTextArea(Objects.taskspage.data.insertLinkText, 'The link is not added');

    });
    it('should navigate to task page and click on header image and verify if redirects to home page', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTaskTitle()).toEqual(Objects.taskspage.data.taskTitle, "");
        taskPage.clickHeaderImageLink();
        expect(taskPage.returnDashboardTitle()).toEqual(Objects.taskspage.data.dashboardTitle, 'Header image does not redirects to home page');

    });
    it('should create new task navigate to attachments section add png document', function() {

        taskPage.clickModuleTasks();
        expect(taskPage.returnAttachementsTableTitle()).toEqual(Objects.taskspage.data.attachmentsTableTitle, 'Attachments table title is wrong');
        taskPage.clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadPng();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentImageTitle, 'Added document name is wrong, or document is not added');
    });
    it('should create new task navigate to attachments section add docx document', function() {

        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadDocx();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentWordTitle, 'Added document name is wrong');


    });
    it('should create new task navigate to attachemnts section add xlsx document', function() {

        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadXlsx();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentExcelWordTitle, 'Added document name is wrong');

    });
    it('should create new task navigate to attachemnts section add pdf document', function() {


        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadPdf();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentPdfTitle, 'Added document name is wrong');

    });
    it('should create new task and add new folder in documents section', function() {

        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewFolder();
        taskPage.insertDocumentTitle(Objects.taskspage.data.documentTitleInput);
        taskPage.clickAttachmentTitle();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentTitleInput, "Document title is not corrrect");


    });
    it('should create new task and edit the start date', function() {

        taskPage.clickModuleTasks();
        //this line was added to verify that issue https://project.armedia.com/jira/browse/AFDP-2797 does not exist any more
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Attachments");
        taskPage.editStartDate(utils.returnToday("/"));
        expect(taskPage.returnStartDateInput()).toEqual(today, "Start date is not updated");

    });

    it('should create new task and edit due date', function() {

        taskPage.clickModuleTasks();
        taskPage.editDueDate(utils.returnToday("/"));
        expect(taskPage.returnInsertedDueDate()).toEqual(today, "Due date is not updated");

    });

    it('should create new task,add picture and verify in details', function() {

        taskPage.clickNewButton().clickTaskButton().insertSubject(Objects.taskpage.data.Subject).insertDueDateToday();
        taskPage.clickPictureButton();
        taskPage.clickChooseFileButton();
        utils.uploadPng();
        expect(taskPage.returnNotesTextArea()).not.toBeTruthy();
        taskPage.clickSave();
        taskPage.clickDetailsLink();
        expect(taskPage.returnDetailsTextArea()).not.toBeTruthy();

    });

    it('should create new task, verify checkout, cancel editing ', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Attachments");
        taskPage.rightClickRootFolder();
        taskPage.addDocument("Notice of Investigation");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn1, "ArkCaseTesting");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn2, ".docx");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn3, "Notice Of Investigation");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn6, Objects.taskspage.data.assigneeSamuel);
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn7, "1.0");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn8, "ACTIVE");
        expect(taskPage.lockIconIsPresent()).not.toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).not.toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).not.toBeTruthy();
        taskPage.clickCheckOut();
        expect(taskPage.lockIconIsPresent()).toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).not.toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).toBeTruthy();
        taskPage.clickCancelEditing();
        expect(taskPage.lockIconIsPresent()).not.toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).not.toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).not.toBeTruthy();

    });

    it('should create new task, verify checkout, checkin', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Attachments");
        taskPage.rightClickRootFolder();
        taskPage.addDocument("Notice of Investigation");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn1, "ArkCaseTesting");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn2, ".docx");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn3, "Notice Of Investigation");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn6, Objects.taskspage.data.assigneeSamuel);
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn7, "1.0");
        taskPage.validateDocGridValue("ArkCaseTesting", Objects.basepage.data.docGridColumn8, "ACTIVE");
        taskPage.rightClickDocument().clickCheckOut();
        expect(taskPage.lockIconIsPresent()).toBeTruthy();
        taskPage.clickCheckin();
        expect(taskPage.lockIconIsPresent()).not.toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).not.toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).not.toBeTruthy();

    });

});
