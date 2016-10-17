var logger = require('../log');
var taskPage = require('../Pages/task_page.js');
var tasksPage = require('../Pages/tasks_page.js');
var authentication = require('../authentication.js');
var Objects = require('../json/Objects.json');
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
        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
        expect(taskPage.returnDueDateText()).not.toBeTruthy();
        taskPage.clickSave();
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityLow);        
    });

    it('should create new task with priority High', function() {
        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
        expect(taskPage.returnDueDateText()).not.toBeTruthy();
        taskPage.clickSave();
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityHigh);        
    });
    it('should create new task with priority Expedite', function() {
        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
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

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	expect(taskPage.returnTaskSubject()).toEqual(Objects.taskpage.data.Subject);
    	expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.assigneeSamuel);
    	expect(taskPage.returnInsertedStartDate()).toEqual(today);
    	expect(taskPage.returnInsertedDueDate()).toEqual(today);
    	expect(taskPage.returnPercent()).toEqual(Objects.taskpage.data.percentCompleteInput);
    	taskPage.clickDetailsLink();
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.notesTextArea);        
        
    });  


    it('should create new task click complete button and verify task state', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickCompleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed);
       
    });

    it('should create new task click delete button and verify task state', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickDeleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateDelete);
        
    });

    it('should create new task with different user', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskpage.data.searchUser, Objects.taskpage.data.Subject, "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator);
       
    });

    it('should create new task add link verify', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskDataLinkNote(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskpage.data.linkInputText, Objects.taskpage.data.linkInputUrl).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle);
        taskPage.clickDetailsLink();
        expect(taskPage.returnDetailsTextArea()).toEqual(Objects.taskpage.data.linkInputText);
       
    });

    it('should create new task click subscribe button verify if it is changed to unsubscribe', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn);
        
    });

    it('should create new task click unsubscribe button verify if it is changed to subscribe', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn);
        taskPage.clickUnsubscribeButton();
        expect(taskPage.returnSubscribeButtonText()).toEqual(Objects.taskspage.data.subscribeBtn);
    });

    it('should create new task and add note', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickNotesLink();
        expect(taskPage.returnNotesTableTitle()).toEqual(Objects.taskspage.data.notesTableTitle)
        taskPage.clickAddNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.notePopUpTitle);
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.notesTextArea); 
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        expect("Note name is not correct", taskPage.returnNoteName()).toEqual(Objects.taskspage.data.notesTextArea);
        expect("Created date of note is not correct", taskPage.returnNoteCreatedDate()).toEqual(today);
        expect("Supervisor is not correct", taskPage.returnNoteAuthor()).toEqual(Objects.taskspage.data.supervisor);
       
        

    });
    it('should create new task add note and delete the note', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);  
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickDeleteNoteButton();
        expect(taskPage.addedNoteNameIsPresent()).toBe(false, 'The note is not deleted');

    });
    it('should create new task add note and edit the note', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickEditNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.noteTitleEditRecord);
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.noteText);        
        expect(taskPage.returnNoteName()).toEqual(Objects.taskspage.data.noteText, 'The note is not updated');
    		
    
    	
    });
    it('should create new task and edit priority to high', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.editPriority("High");        
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityHigh, "Priority is not updated");            
       
       
    });
    it('should create new task and edit priority to Expedite', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
    	taskPage.editPriority("Expedite");        
        expect(taskPage.returnPriority()).toEqual(Objects.taskspage.data.priorityExpedite, "Priority is not updated");  
    });
    it('should create new task and edit percent of completition', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editPercent(Objects.taskspage.data.percentCompletitionInput);            
        expect(taskPage.returnPercent()).toEqual(Objects.taskspage.data.percentCompletitionInput, 'Percent is not updated');
       
    });
    it('should create new task and edit task subject', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editTaskSubject(Objects.taskspage.data.taskSubjectInput);
        expect(taskPage.returnTaskSubject()).toEqual(Objects.taskspage.data.taskSubjectInput, 'Task subject is not updated');

    });
    it('should create new task and edit assignee from samuel to ann', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "High", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editAssignee("ann-acm");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');

    });
    it('should create new task change assignee and verify is button complete and delete are not displyed', function() {

    	taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        taskPage.editAssignee("ann-acm");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');
        expect(taskPage.completeButtonIsPresent()).toBe(false, 'Complete button should not be displyed');
        expect(taskPage.deleteButtonIsPresent()).toBe(false, 'Delete  button should not be displyed');
       
    });
    
})

        
    

