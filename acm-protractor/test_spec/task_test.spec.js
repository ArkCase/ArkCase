var logger = require('../log');
var utils = require('../util/utils.js');
var taskPage = require('../Pages/task_page.js');
var authentication = require('../authentication.js');
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

    	taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskspage.data.taskTitle);
        taskPage.insertSubject(Objects.taskpage.data.Subject);
        expect(taskPage.returnStartDateInput()).not.toBeTruthy();
        taskPage.insertDueDateToday();
        expect(taskPage.returnDueDateInput()).not.toBeTruthy();
        taskPage.insertPercentComplete(Objects.taskpage.data.percentCompleteInput).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle);
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateActive);

    });


    it('should create new task with priority low', function() {
        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
        expect(taskPage.returnDueDateText()).not.toBeTruthy();
        taskPage.clickSave();
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityLow);
    });

    it('should create new task with priority High', function() {
        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
        expect(taskPage.returnDueDateText()).not.toBeTruthy();
        taskPage.clickSave();
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityHigh);
    });
    it('should create new task with priority Expedite', function() {
        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
        expect(taskPage.returnDueDateText()).not.toBeTruthy();
        taskPage.clickSave();
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityExpedite);
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
    	expect(taskPage.returnTaskSubject()).toEqual(Objects.taskpage.data.Subject);
    	expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.assigneeSamuel);
    	expect(taskPage.returnInsertedStartDate()).toEqual(utils.returnToday("/"));
    	expect(taskPage.returnInsertedDueDate()).toEqual(utils.returnToday("/"));
    	expect(taskPage.returnPercent()).toEqual(Objects.taskpage.data.percentCompleteInput);
    	taskPage.clickDetailsLink();
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.notesTextArea);

    });


    it('should create new task click complete button and verify task state', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickCompleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed);

    });

    it('should create new task click delete button and verify task state', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickDeleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateDelete);

    });

    it('should create new task with different user', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskpage.data.searchUser, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator);

    });

    it('should create new task add link verify', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskDataLinkNote(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskpage.data.linkInputText, Objects.taskpage.data.linkInputUrl).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle);
        taskPage.clickDetailsLink();
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskpage.data.linkInputText);

    });

    it('should create new task click subscribe button verify if it is changed to unsubscribe', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn);

    });

    it('should create new task click unsubscribe button verify if it is changed to subscribe', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn);
        taskPage.clickUnsubscribeButton();
        expect(taskPage.returnSubscribeButtonText()).toEqual(Objects.taskspage.data.subscribeBtn);
    });

    it('should create new task and add note', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
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

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickDeleteNoteButton();
        expect(taskPage.addedNoteNameIsPresent()).toBe(false, 'The note is not deleted');

    });
    it('should create new task add note and edit the note', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickEditNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.noteTitleEditRecord);
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.noteText);
        expect(taskPage.returnNoteName()).toEqual(Objects.taskspage.data.noteText, 'The note is not updated');



    });
    it('should create new task and edit priority to high', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.editPriority("High");
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityHigh, "Priority is not updated");


    });
    it('should create new task and edit priority to Expedite', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.editPriority("Expedite");
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityExpedite, "Priority is not updated");
    });
    it('should create new task and edit percent of completition', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editPercent(Objects.taskspage.data.percentCompletitionInput);
        expect(taskPage.returnPercent()).toEqual(Objects.taskspage.data.percentCompletitionInput, 'Percent is not updated');

    });
    it('should create new task and edit task subject', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editTaskSubject(Objects.taskspage.data.taskSubjectInput);
        expect(taskPage.returnTaskSubject()).toEqual(Objects.taskspage.data.taskSubjectInput, 'Task subject is not updated');

    });
    it('should create new task and edit assignee from samuel to ann', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editAssignee("ann-acm");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');

    });
    it('should create new task change assignee and verify is button complete and delete are not displyed', function() {

    	 taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editAssignee("ann-acm");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');
        expect(taskPage.completeButtonIsPresent()).toBe(false, 'Complete button should not be displyed');
        expect(taskPage.deleteButtonIsPresent()).toBe(false, 'Delete  button should not be displyed');

    });
    it('should create new task add tag and verify added tag', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickTagsLink();
        expect(taskPage.returnTagsTableTittle()).toEqual(Objects.taskspage.data.tagsTableTitle);
        taskPage.clickAddTagButton();
        expect(taskPage.returnAddTagPopUpTitle()).toEqual(Objects.taskspage.data.addTagPopUpTitle);
        browser.driver.actions().mouseDown(taskPage.tagTextArea).click().sendKeys(Objects.taskspage.data.tagTextArea).perform();
        taskPage.clickAddTagPopUpTitle().clickSaveTagButton();
        expect(taskPage.returnTagName()).toEqual(Objects.taskspage.data.tagTextArea, 'Created tag name is wrong');
        expect(taskPage.returnTagCreatedDate()).toEqual(today, 'Created tag date is wrong');
        expect(taskPage.returnTagCreatedBy()).toEqual(Objects.taskspage.data.supervisor, 'Created tag by is wrong');

    });
    it('should create new task add tag and delete added tag', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickTagsLink();
        expect(taskPage.returnTagsTableTittle()).toEqual(Objects.taskspage.data.tagsTableTitle);
        taskPage.clickAddTagButton();
        expect(taskPage.returnAddTagPopUpTitle()).toEqual(Objects.taskspage.data.addTagPopUpTitle);
        browser.driver.actions().mouseDown(taskPage.tagTextArea).click().sendKeys(Objects.taskspage.data.tagTextArea).perform();
        taskPage.clickAddTagPopUpTitle().clickSaveTagButton();
        taskPage.clickDeleteTagButton();
        expect(taskPage.returnTagNameisPresent()).toBe(false, 'The tag is not deleted');

    });
    it('should create new task add text task details verify if it saved', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickDetailsLink();
        taskPage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        taskPage.clickSaveDetailsButton();
        taskPage.clickRefreshButton();
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');


    });
    it('should create new task and verify workflow table data', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.workflowLink.click();
        expect(taskPage.returnWorkflowTitle()).toEqual(Objects.taskspage.data.workflowTitle);
        expect(taskPage.returnWorkflowParticipant()).toEqual(Objects.taskspage.data.supervisor);
        expect(taskPage.returnWorkflowStatus()).toEqual(Objects.taskspage.data.workflowStatus);
        expect(taskPage.returnWorkflowStartDate()).toEqual(utils.returnToday("/"));

    });
    it('should create new task and verify history table data', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickHistoryLink();
        expect(taskPage.returnHistoryTableTitle()).toEqual(Objects.taskspage.data.historyTableTitle);
        expect(taskPage.returnHistoryEventName()).toEqual(Objects.taskspage.data.historyEventName);
        expect(taskPage.returnHistoryUser()).toEqual(Objects.taskspage.data.supervisor);
        expect(taskPage.returnHistoryDate()).toContain(utils.returnToday("/"));

    });
    it('should create new task add link from task details', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.clickDetailsLink();
        taskPage.clickInsertLinkInDetails();
        expect(taskPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle);
        taskPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.insertLinkText, 'The link is not added');
        taskPage.clickSaveDetailsButton();
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.insertLinkText, 'The link is not added');

    });
    it('should navigate to task page and click on header image and verify if redirects to home page', function() {

    	taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTaskTitle()).toEqual(Objects.taskspage.data.taskTitle);
        taskPage.clickHeaderImageLink();
        expect(taskPage.returnDashboardTitle()).toEqual(Objects.taskspage.data.dashboardTitle, 'Header image does not redirects to home page');

    });
    it('should create new task navigate to attachments section add png document', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.clickAttachmentsLink();
        expect(taskPage.returnAttachementsTableTitle()).toEqual(Objects.taskspage.data.attachmentsTableTitle, 'Attachments table title is wrong');
    	taskPage.clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadPng();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentImageTitle, 'Added document name is wrong, or document is not added');
    });
    it('should create new task navigate to attachments section add docx document', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave().clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadDocx();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentWordTitle, 'Added document name is wrong');


    });
    it('should create new task navigate to attachemnts section add xlsx document', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave().clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadXlsx();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentExcelWordTitle, 'Added document name is wrong');

    });
    it('should create new task navigate to attachemnts section add pdf document', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave().clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadPdf();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentPdfTitle, 'Added document name is wrong');

    });
    it('should create new task and add new folder in documents section', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave().clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewFolder();
        taskPage.insertDocumentTitle(Objects.taskspage.data.documentTitleInput);
        taskPage.clickAttachmentTitle();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentTitleInput);


    });
   it('should create new task and edit the start date', function() {

       taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, Objects.taskpage.data.StartDate, utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
       taskPage.editStartDate(utils.returnToday("/"));
       expect(taskPage.returnStartDateInput()).toEqual(today);

    });


    it('should create new task and edit due date', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), Objects.taskpage.data.DueDateInput, "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editDueDate(utils.returnToday("/"));
        expect(taskPage.returnInsertedDueDate()).toEqual(today);

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




    

})

        
    

