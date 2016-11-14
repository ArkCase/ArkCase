var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var SelectWrapper = require('../util/select-wrapper.js');
var taskBtn = element(by.linkText(Objects.taskpage.locators.taskButton));
var taskTitle = element(by.css(Objects.taskpage.locators.taskTitle));
var Subject = element(by.id(Objects.taskpage.locators.subject));
var StartDateInput = element(by.id(Objects.taskpage.locators.startDate));
var DueDateBtn = element.all(by.xpath(Objects.taskpage.locators.dueDateBtn)).get(1);
var todayDateFromCalendar = element(by.buttonText(Objects.taskpage.locators.todayDateFromCalendar));
var DueDateInput = element(by.id(Objects.taskpage.locators.DueDateInput));
var percentCompleteInput = element(by.id(Objects.taskpage.locators.percentCompleteInput));
var saveButton = element(by.id(Objects.taskpage.locators.saveButton));
var tasksTitle = element(by.xpath(Objects.taskpage.locators.tasksTitle));
var priorityDropDown = new SelectWrapper(by.model(Objects.taskpage.locators.priorityDropDown));
var assigneeInput = element(by.id(Objects.taskpage.locators.assigneeInput));
var userSearchTitle = element(by.xpath(Objects.taskpage.locators.userSearchTitle));
var searchUserInput = element(by.xpath(Objects.taskpage.locators.searchUserInput));
var searchUserBtn = element(by.xpath(Objects.taskpage.locators.searchUserBtn));
var searchedName = element.all(by.repeater(Objects.taskpage.locators.searchedName)).get(0);
var confimrBtn = element(by.xpath(Objects.taskpage.locators.confimrBtn));
var noteTextArea = element(by.xpath(Objects.taskpage.locators.notesTextArea));
var linkButton = element.all(by.xpath(Objects.taskpage.locators.linkButton)).get(0);
var linkInputText = element(by.xpath(Objects.taskpage.locators.linkInputText));
var linkInputUrl = element(by.xpath(Objects.taskpage.locators.linkInputUrl));
var insertLinkBtn = element(by.buttonText(Objects.taskpage.locators.insertLinkBtn));
var pictureButton = element.all(by.xpath(Objects.taskpage.locators.pictureButton)).get(0);
var chooseFilesBtn = element(by.xpath(Objects.taskpage.locators.chooseFilesBtn));
var priority = element(by.xpath(Objects.taskspage.locators.priority));
var taskSubject = element(by.xpath(Objects.taskspage.locators.taskSubject));
var detailsLink = element.all(by.repeater(Objects.taskspage.locators.detailsLink)).get(1);
var detailsTextArea = element(by.xpath(Objects.taskspage.locators.detailsTextArea));
var assignee = element(by.xpath(Objects.taskspage.locators.assignee));
var percent = element(by.xpath(Objects.taskspage.locators.percent));
var startDate = element(by.xpath(Objects.taskspage.locators.startDate));
var dueDate = element(by.xpath(Objects.taskspage.locators.dueDate));
var completeBtn = element(by.buttonText(Objects.taskspage.locators.completeBtn));
var deleteBtn = element(by.buttonText(Objects.taskspage.locators.deleteBtn));
var taskState = element(by.xpath(Objects.taskpage.locators.taskState));
var subscribeBtn = element(by.buttonText(Objects.taskspage.locators.subscribeBtn));
var unsubscribeBtn = element(by.buttonText(Objects.taskspage.locators.unsubscribeBtn));
var notesLink = element.all(by.repeater(Objects.taskspage.locators.notesLink)).get(4);
var notesTableTitle = element(by.css(Objects.taskspage.locators.notesTableTitle));
var addNoteBtn = element(by.xpath(Objects.taskspage.locators.addNoteBtn));
var notesTextArea = element(by.model(Objects.taskspage.locators.notesTextArea));
var noteName = element.all(by.repeater(Objects.taskspage.locators.noteName)).get(0);
var noteCreatedDate = element.all(by.repeater(Objects.taskspage.locators.noteCreatedDate)).get(1);
var noteAuthor = element.all(by.repeater(Objects.taskspage.locators.noteAuthor)).get(2);
var notePopUpTitle = element(by.xpath(Objects.taskspage.locators.notePopUpTitle));
var noteSaveBtn = element(by.buttonText(Objects.taskspage.locators.noteSaveBtn));
var deleteNoteBtn = element.all(by.repeater(Objects.taskspage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.taskspage.locators.tag)).get(1);
var editNoteBtn = element.all(by.repeater(Objects.taskspage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.taskspage.locators.tag)).get(0);
var priorityLink = element(by.xpath(Objects.taskspage.locators.priority));
var priorityDropDownEdit = new SelectWrapper(by.xpath(Objects.taskspage.locators.priorityDropDown));
var editSubmitButton = element(by.css(Objects.taskpage.locators.editSubmitButton));
var percentCompletition = element(by.xpath(Objects.taskspage.locators.percentCompletition));
var percentCompletitionInput = element(by.xpath(Objects.taskspage.locators.percentCompletitionInput));
var taskSubjectEdit = element(by.xpath(Objects.taskspage.locators.taskSubject));
var taskSubjectInput = element(by.xpath(Objects.taskspage.locators.taskSubjectInput));
var assigneeDropDown = new SelectWrapper(by.model(Objects.taskspage.locators.assigneeDropDown));
var tagsLink = element.all(by.repeater(Objects.taskspage.locators.tagsLink)).get(9);
var tagsTableTitle = element(by.css(Objects.taskspage.locators.tagsTableTitle));
var addTagBtn = element(by.xpath(Objects.taskspage.locators.addTagBtn));
var addTagPopUpTitle = element(by.xpath(Objects.taskspage.locators.addTagPopUpTitle));
var tagTextArea = element(by.xpath(Objects.taskspage.locators.tagTextArea));
var saveTagBtn = element(by.buttonText(Objects.taskspage.locators.saveTagBtn));
var tagName = element.all(by.repeater(Objects.taskspage.locators.addedtagName)).get(0);
var tagCreatedDate = element.all(by.repeater(Objects.taskspage.locators.tagCreatedDate)).get(1);
var tagCreatedBy = element.all(by.repeater(Objects.taskspage.locators.tagCreatedBy)).get(2);
var tagDeleteBtn = element.all(by.repeater(Objects.taskspage.locators.tagDeleteBtn)).get(3).all(by.tagName(Objects.taskspage.locators.tag)).get(0);
var detailsSaveBtn = element(by.xpath(Objects.taskspage.locators.detailsSaveBtn));
var refreshBtn = element(by.css(Objects.taskspage.locators.refreshBtn));
var workflowLink = element.all(by.repeater(Objects.taskspage.locators.workflowLink)).get(6);
var workflowTitle = element(by.css(Objects.taskspage.locators.workflowTitle));
var workflowParticipant = element.all(by.repeater(Objects.taskspage.locators.workflowParticipant)).get(0);
var workflowStatus = element.all(by.repeater(Objects.taskspage.locators.workflowStatus)).get(2);
var workflowStartDate = element.all(by.repeater(Objects.taskspage.locators.workflowStartDate)).get(3);
var historyLink = element.all(by.repeater(Objects.taskspage.locators.historyLink)).get(7);
var historyTableTitle = element(by.css(Objects.taskspage.locators.historyTableTitle));
var historyEventName = element.all(by.repeater(Objects.taskspage.locators.historyEventName)).get(0);
var historyDate = element.all(by.repeater(Objects.taskspage.locators.historyDate)).get(1);
var historyUser = element.all(by.repeater(Objects.taskspage.locators.historyUser)).get(2);
var detailsLinkBtn = element(by.xpath(Objects.taskspage.locators.detailsLinkBtn));
var insertLinkTitle = element(by.xpath(Objects.taskspage.locators.insertLinkTitle));
var insertLinkText = element(by.xpath(Objects.taskspage.locators.insertLinkText));
var insertLinkUrl = element(by.xpath(Objects.taskspage.locators.insertLinkUrl));
var insertLinkBtn = element(by.buttonText(Objects.taskspage.locators.insertLinkBtn));
var headerImageLink = element(by.css(Objects.taskspage.locators.headerImageLink));
var attachmentsLink = element.all(by.repeater(Objects.taskspage.locators.attachmentsLink)).get(3);
var attachmentsTableTitle = element(by.css(Objects.taskspage.locators.attachmentsTableTitle));
var root = element(by.xpath(Objects.taskspage.locators.root));
var newDocument = element.all(by.css(Objects.taskspage.locators.newDocument)).get(1);
var otherDocument = element(by.xpath(Objects.taskspage.locators.otherDocument));
var documentTitle = element(by.xpath(Objects.taskspage.locators.documentTitle));
var newFolder = element.all(by.css(Objects.taskspage.locators.newFolder)).get(0);
var documentTitle = element(by.xpath(Objects.taskspage.locators.documentTitle));
var documentTitleInput = element(by.xpath(Objects.taskspage.locators.documentTitleInput));
var startDateInputEdit = element(by.model(Objects.taskspage.locators.startDateInput))
var dueDateValue = element(by.model(Objects.taskspage.locators.dueDateInput));
var approveBtn = element(by.xpath(Objects.taskspage.locators.approveBtn));
var caseTitleInTasks = element(by.xpath(Objects.taskspage.locators.caseTitleInTasks));

var TaskPage = function() {
    this.clickTaskButton = function(){
        taskBtn.click();
        return this;
    }
    this.insertSubject = function(subject) {
        Subject.clear();
        Subject.click();
        Subject.sendKeys(subject);
        return this;
    };
    this.insertDueDateToday = function() {
        DueDateBtn.click();
        todayDateFromCalendar.click();
        return this;
    };
    this.clickSave = function() {
        saveButton.click();
        return this;
    };
    this.insertTaskData = function(assignee, subject, startdate, duedate, priority, percent, note) {
        this.addAssignee(assignee);
        this.insertSubject(subject);
        this.insertStartDate(startdate);
        this.insertDueDate(duedate);
        this.selectPriority(priority);
        this.insertPercentComplete(percent);
        this.insertTextNote(note);
        return this;
    };
    this.insertStartDate = function(date) {
        StartDateInput.click();
        StartDateInput.clear();
        StartDateInput.sendKeys(date);
        return this;
    }
    this.insertDueDate = function(date) {
        DueDateInput.click();
        DueDateInput.clear();
        DueDateInput.sendKeys(date);
        return this;

    }
    this.insertPercentComplete = function(percent) {
        percentCompleteInput.click();
        percentCompleteInput.clear();
        percentCompleteInput.sendKeys(percent);
        return this;
    };
    this.insertTextNote = function(note) {
        noteTextArea.click();
        noteTextArea.clear();
        noteTextArea.sendKeys(note);
        return this;
    };
    this.addAssignee = function(assignee) {
        assigneeInput.click();
        searchUserInput.click();
        searchUserInput.sendKeys(assignee);
        searchUserBtn.click();
        searchedName.click();
        confimrBtn.click();
        return this;
    };
    this.selectPriority = function(priority) {
        priorityDropDown.selectByText(priority);
        return this;
    }
    this.returnDueDateText = function() {
        return DueDateInput.getText();
    }
    this.returnPriority = function() {
        return priority.getText();
    }
    this.returnSaveButtonEnabled = function() {
        return saveButton.isEnabled();
    }
    this.clearPercentInput = function() {
        percentCompleteInput.click();
        percentCompleteInput.clear();
        return this;
    }
    this.clickDetailsLink = function() {
        detailsLink.click();
        return this;
    }
    this.returnDetailsTextArea = function() {
        return detailsTextArea.getText();
    }
    this.returnTaskSubject = function() {
        return taskSubject.getText();
    }
    this.returnAssignee = function() {
        return assignee.getText();
    }
    this.returnPercent = function() {
        return percent.getText();
    }
    this.returnInsertedStartDate = function() {
        return startDate.getText();
    }
    this.returnInsertedDueDate = function() {
        return dueDate.getText();
    }
    this.clickCompleteButton = function() {
        completeBtn.click();
        return this;
    }
    this.returnTaskState = function() {
        return taskState.getText();
    }
    this.clickDeleteButton = function() {
        deleteBtn.click();
        return this;
    }
    this.clickSubscribeButton = function() {
        subscribeBtn.click();
        return this;
    }
    this.returnUnsubscribeButtonText = function() {
        return unsubscribeBtn.getText();
    }
    this.clickUnsubscribeButton = function() {
        unsubscribeBtn.click();
        return this;
    }
    this.returnSubscribeButtonText = function() {
        return subscribeBtn.getText();
    }
    this.returnTasksTitle = function() {
        return tasksTitle.getText();
    }
    this.returnStartDateInput = function() {
        return startDate.getText();
    }
    this.returnDueDateInput = function() {
        return DueDateInput.getText();
    }
    this.insertLinkNote = function(text, url) {
        linkButton.click();
        linkInputText.sendKeys(text);
        linkInputUrl.clear();
        linkInputUrl.sendKeys(url);
        insertLinkBtn.click();
        return this;
     }
    this.insertTaskDataLinkNote = function(assignee, subject, startdate, duedate, priority, percent, text, url) {
        this.addAssignee(assignee);
        this.insertSubject(subject);
        this.insertStartDate(startdate);
        this.insertDueDate(duedate);
        this.selectPriority(priority);
        this.insertPercentComplete(percent);
        this.insertLinkNote(text, url);
        return this;
    }
    this.clickNotesLink = function() {
        notesLink.click();
        return this;
    }
    this.returnNotesTableTitle = function() {
        return notesTableTitle.getText();
    }
    this.clickAddNoteButton = function() {
        addNoteBtn.click();
        return this;
    }
    this.returnNotePopUpTitle = function() {
        return notePopUpTitle.getText();
    }
    this.insertNoteFromOverviewTab = function(note) {
        notesTextArea.click();
        notesTextArea.clear();
        notesTextArea.sendKeys(note);
        noteSaveBtn.click();
        return this;
    }
    this.returnNoteName = function() {
        return noteName.getText();
    }
    this.returnNoteCreatedDate = function() {
        return noteCreatedDate.getText();
    }
    this.returnNoteAuthor = function() {
        return noteAuthor.getText();
    }
    this.clickDeleteNoteButton = function() {
        deleteNoteBtn.click();
        return this;
    }
    this.addedNoteNameIsPresent = function() {
        return noteName.isPresent();
    }
    this.clickEditNoteButton = function() {
        editNoteBtn.click();
        return this;
    }
    this.clickPriorityEdit = function() {
        priorityLink.click();
        return this;
    }
    this.selectPriorityEdit = function(priority) {
        priorityDropDownEdit.selectByText(priority);
        return this;
    }
    this.confirmEdit = function() {
        editSubmitButton.click();
        return this;
    }
    this.editPriority = function(priority) {
        this.clickPriorityEdit();
        this.selectPriorityEdit(priority);
        this.confirmEdit();
    }
    this.clickPercentCompletition = function() {
        percentCompletition.click();
        return this;

    }
    this.insertPercentEdit = function(percent) {
        percentCompletitionInput.click();
        percentCompletitionInput.clear();
        percentCompletitionInput.sendKeys(percent);
        return this;
    }
    this.editPercent = function(percent) {
        this.clickPercentCompletition();
        this.insertPercentEdit(percent);
        this.confirmEdit();
    }
    this.clickTaskSubjectEdit = function() {
        taskSubjectEdit.click();
        return this
    }
    this.insertSubjectEdit = function(subject) {
        taskSubjectInput.click();
        taskSubjectInput.clear();
        taskSubjectInput.sendKeys(subject);
    }
    this.editTaskSubject = function(subject) {
        this.clickTaskSubjectEdit();
        this.insertSubjectEdit(subject);
        this.confirmEdit();
    }
    this.clickAssignee = function() {
        assignee.click();
        return this;
    }
    this.selectAssigneeEdit = function(assignee) {
        assigneeDropDown.selectByText(assignee);
        return this;
    }
    this.editAssignee = function(assignee) {
        this.clickAssignee();
        this.selectAssigneeEdit(assignee);
        this.confirmEdit();
    }
    this.completeButtonIsPresent = function() {
        return completeBtn.isDisplayed();
    }
    this.deleteButtonIsPresent = function() {
        return deleteBtn.isDisplayed();
    }
    this.clickTagsLink = function() {
        tagsLink.click();
        return this;
    }
    this.returnTagsTableTittle = function() {
        return tagsTableTitle.getText();
    }
    this.clickAddTagButton = function() {
        addTagBtn.click();
        return this;
    }
    this.returnAddTagPopUpTitle = function() {
        return addTagPopUpTitle.getText();
    }
    this.clickAddTagPopUpTitle = function() {
        addTagPopUpTitle.click();
        return this;
    }
    this.clickSaveTagButton = function() {
        saveTagBtn.click();
        return this;
    }
    this.returnTagName = function() {
        return tagName.getText();
    }
    this.returnTagCreatedDate = function() {
        return tagCreatedDate.getText();
    }
    this.returnTagCreatedBy = function() {
        return tagCreatedBy.getText();
    }
    this.insertTag = function(tag) {
        tagTextArea.click();
        tagTextArea.clear();
        tagTextArea.sendKeys(tag);
        return this;
    }
    this.clickDeleteTagButton = function() {
        tagDeleteBtn.click();
        return this;
    }
    this.returnTagNameisPresent = function() {
        return tagName.isPresent();
    }
    this.insertDetailsTextAreaText = function(details) {
        detailsTextArea.click();
        detailsTextArea.clear();
        detailsTextArea.sendKeys(details);
        return this;
    }
    this.clickSaveDetailsButton = function() {
        detailsSaveBtn.click();
        return this;
    }
    this.clickRefreshButton = function() {
        refreshBtn.click();
        return this;
    }
    this.clickWorkflowLink = function() {
        workflowLink.click();
        return this;
    }
    this.returnWorkflowTitle = function() {
        return workflowTitle.getText();
    }
    this.returnWorkflowParticipant = function() {
        return workflowParticipant.getText();
    }
    this.returnWorkflowStatus = function() {
        return workflowStatus.getText();
    }
    this.returnWorkflowStartDate = function() {
        return workflowStartDate.getText();
    }
    this.clickHistoryLink = function() {
        historyLink.click();
        return this;
    }
    this.returnHistoryTableTitle = function() {
        return historyTableTitle.getText();
    }
    this.returnHistoryEventName = function() {
        return historyEventName.getText();
    }
    this.returnHistoryUser = function() {
        return historyUser.getText();
    }
    this.returnHistoryDate = function() {
        return historyDate.getText();
    }
    this.clickInsertLinkInDetails = function() {
        detailsLinkBtn.click();
        return this;
    }

	this.insertDetailsTextAreaLink = function(text, url){
		insertLinkText.click();
        insertLinkText.sendKeys(text);
        insertLinkUrl.clear();
        insertLinkUrl.sendKeys(url);
        insertLinkBtn.click();
    }
    this.returnInsertLinkTitle = function() {
        return insertLinkTitle.getText();
    }
    this.returnTaskTitle = function() {
        return taskTitle.getText();
    }
    this.clickHeaderImageLink = function() {
        headerImageLink.click();
        return this;
    }
    this.returnDashboardTitle = function() {
        return dashboardTitle.getText();
    }
    this.clickAttachmentsLink = function() {
        attachmentsLink.click();
        return this;
    }
    this.returnAttachementsTableTitle = function() {
        return attachmentsTableTitle.getText();
    }
    this.clickRootFolder = function() {
        root.click();
        return this;
    }
    this.clickNewDocument = function() {
        newDocument.click();
        return this;
    }
    this.clickOtherDocument = function() {
        otherDocument.click();
        return this;
    }
    this.returnDocumentTitle = function() {
        return documentTitle.getText();
    }
    this.clickNewFolder = function() {
        newFolder.click();
        return this;
    }
    this.insertDocumentTitle = function(title) {
        documentTitleInput.click();
        documentTitleInput.clear();
        documentTitleInput.sendKeys(title);
        return this;
    }
    this.clickAttachmentTitle = function() {
        attachmentsTableTitle.click();
        return this;

    }
    this.insertStartDateEdit = function(date) {
        startDateInputEdit.click();
        startDateInputEdit.clear();
        startDateInputEdit.sendKeys(date);
        return this;

    }
    this.clickStartDate = function() {
        startDate.click();
        return this;
    }
    this.editStartDate = function(date) {
        this.clickStartDate();
        this.insertStartDateEdit(date);
        this.confirmEdit();
    }
    this.clickDueDate = function() {
        dueDate.click();
        return this;
    }
    this.insertDueDateEdit = function(date) {
        dueDateValue.click();
        dueDateValue.clear();
        dueDateValue.sendKeys(date);
        return this;
    }
    this.editDueDate = function(date) {
        this.clickDueDate();
        this.insertDueDateEdit(date);
        this.confirmEdit();

    }
    this.clickPictureButton = function() {
        pictureButton.click();
        return this;
    }
    this.clickChooseFileButton = function() {
        chooseFilesBtn.click();
        return this;
    }
    this.returnNotesTextArea = function() {
        return notesTextArea.getText();
    }
    this.clickApproveBtn = function() {

        browser.wait(EC.presenceOf(element(by.xpath(Objects.taskspage.locators.approveBtn))), 30000).then(function() {
            approveBtn.click().then(function() {
                browser.wait(EC.textToBePresentInElement((taskState), Objects.taskspage.data.taskStateClosed), 10000);
            });
        });
        return this;
    }
	this.clickCaseTitleInTasks = function() {
		browser.wait(EC.presenceOf(element(by.xpath(Objects.taskspage.locators.caseTitleInTasks))), 30000).then(function() {
			caseTitleInTasks.click().then(function() {
				browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.tasksLink))), 30000);

			});
		});
	}


};

TaskPage.prototype = basePage;
module.exports = new TaskPage();
