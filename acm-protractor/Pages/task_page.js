var Objects = require('../json/Objects.json');
var newBtn = element(by.linkText(Objects.taskpage.locators.newButton));
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
var priorityDropDown = element(by.model(Objects.taskpage.locators.priorityDropDown));
var priorityLow = element(by.xpath(Objects.taskpage.locators.priorityLow));
var priorityMedium = element(by.xpath(Objects.taskpage.locators.priorityMedium));
var priorityHigh = element(by.xpath(Objects.taskpage.locators.priorityHigh));
var priorityExpedite = element(by.xpath(Objects.taskpage.locators.priorityExpedite));
var assigneeInput = element(by.id(Objects.taskpage.locators.assigneeInput));
var userSearchTitle = element(by.xpath(Objects.taskpage.locators.userSearchTitle));
var searchUserInput = element(by.xpath(Objects.taskpage.locators.searchUserInput));
var searchUserBtn = element(by.xpath(Objects.taskpage.locators.searchUserBtn));
var searchedName = element.all(by.repeater(Objects.taskpage.locators.searchedName)).get(0);
var confimrBtn = element(by.xpath(Objects.taskpage.locators.confimrBtn));
var notesTextArea = element(by.xpath(Objects.taskpage.locators.notesTextArea));
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
var TaskPage = function() {
	this.clickNewButton = function() {
		newBtn.click();
		return this;
	};
	this.clickTaskButton = function() {
		taskBtn.click();
		return this;
	};
	this.insertSubject = function(subject) {
		Subject.clear();
		Subject.click();
		Subject.sendKeys(subject);
		return this;
	};
	this.insertDueDateToday = function(){
		DueDateBtn.click();
		todayDateFromCalendar.click();
		return this;
	};
	this.clickSave = function(){
		saveButton.click();
		return this;
	};
	this.insertTaskData = function(assignee, subject, priority, percent, note){
		this.addAssignee(assignee);
		this.insertSubject(subject);
		this.insertDueDateToday();	
		this.selectPriority(priority);
		this.insertPercentComplete(percent);	
		this.insertTextNote(note);
//		switch (notetype) {
//		case "text":
//			this.insertTextNote(note);
//			break;
//		case "link":
//			this.insertLinkNote(text, url);
//			break;
//		default:
//			this.insertTextNote(note);
//			break;
//		}		
		return this;
	};
	this.insertPercentComplete = function(percent){
		percentCompleteInput.click();
		percentCompleteInput.clear();
		percentCompleteInput.sendKeys(percent);
		return this;
	};
	this.insertTextNote = function(note){
		notesTextArea.click();
		notesTextArea.clear();
		notesTextArea.sendKeys(note);
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
	this.selectPriority = function(priority){		
		priorityDropDown.$('[value="string:'+ priority +'"]').click();
		return this;
	}
	this.returnDueDateText = function(){
		return DueDateInput.getText();
	}
	this.returnPriority = function(){
		return priority.getText();
	}
	this.returnSaveButtonEnabled = function(){
		return saveButton.isEnabled();
	} 
	this.clearPercentInput = function(){
		percentCompleteInput.click();
		percentCompleteInput.clear();
		return this;
	}
	this.clickDetailsLink = function(){
		detailsLink.click();
		return this;
	}
	this.returnDetailsTextArea = function(){
		return detailsTextArea.getText();
	}
	this.returnTaskSubject = function(){
		return taskSubject.getText();
	}
	this.returnAssignee = function(){
		return assignee.getText();
	}
	this.returnPercent = function(){
		return percent.getText();
	}
	this.returnInsertedStartDate = function(){
		return startDate.getText();
	}
	this.returnInsertedDueDate = function(){
		return dueDate.getText();
	}
	this.clickCompleteButton = function(){
		completeBtn.click();
		return this;
	}
	this.returnTaskState = function(){
		return taskState.getText();
	}
	this.clickDeleteButton = function(){
		deleteBtn.click();
		return this;
	}
	this.clickSubscribeButton = function(){
		subscribeBtn.click();
		return this;
	}
	this.returnUnsubscribeButtonText = function(){
		return unsubscribeBtn.getText(); 		
	}
	this.clickUnsubscribeButton = function(){
		unsubscribeBtn.click();
		return this;
	}
	this.returnSubscribeButtonText = function(){
		return subscribeBtn.getText();
	}
	this.returnTasksTitle = function(){
		return tasksTitle.getText();
	}
	this.returnStartDateInput = function(){
		return StartDateInput.getText(); 
	}
	this.returnDueDateInput = function(){
		return DueDateInput.getText();
	}
	this.insertLinkNote = function(text, url){
		linkButton.click();
        linkInputText.sendKeys(text); 
        linkInputUrl.clear();
        linkInputUrl.sendKeys(url);
        insertLinkBtn.click();
        return this;
	}
	this.insertTaskDataLinkNote = function(assignee, subject, priority, percent, text, url){
		this.addAssignee(assignee);
		this.insertSubject(subject);
		this.insertDueDateToday();	
		this.selectPriority(priority);
		this.insertPercentComplete(percent);	
		this.insertTextNote(text, url);
		return this;
		
	}

};

module.exports = new TaskPage();
