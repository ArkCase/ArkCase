var Objects = require('../json/Objects.json');
var TaskPage = function() {



    this.newBtn = element(by.linkText(Objects.taskpage.locators.newButton));
    this.taskBtn = element(by.linkText(Objects.taskpage.locators.taskButton));
    this.taskTitle = element(by.css(Objects.taskpage.locators.taskTitle));
    this.Subject = element(by.id(Objects.taskpage.locators.subject));
    this.StartDate = element(by.xpath(Objects.taskpage.locators.startDate));
    this.DueDateBtn = element.all(by.xpath(Objects.taskpage.locators.dueDateBtn)).get(1);
    this.todayDateFromCalendar = element(by.buttonText(Objects.taskpage.locators.todayDateFromCalendar));
    this.DueDateInput = element(by.id(Objects.taskpage.locators.DueDateInput));
    this.percentCompleteInput = element(by.id(Objects.taskpage.locators.percentCompleteInput));
    this.saveButton = element(by.id(Objects.taskpage.locators.saveButton));
    this.tasksTitle = element(by.xpath(Objects.taskpage.locators.tasksTitle));
    this.priorityDropDown = element(by.model(Objects.taskpage.locators.priorityDropDown));
    this.priorityLow = element(by.xpath(Objects.taskpage.locators.priorityLow));
    this.priorityMedium = element(by.xpath(Objects.taskpage.locators.priorityMedium));
    this.priorityHigh = element(by.xpath(Objects.taskpage.locators.priorityHigh));
    this.priorityExpedite = element(by.xpath(Objects.taskpage.locators.priorityExpedite));
    this.assigneeInput = element(by.id(Objects.taskpage.locators.assigneeInput));
    this.userSearchTitle = element(by.xpath(Objects.taskpage.locators.userSearchTitle));
    this.searchUserInput = element(by.xpath(Objects.taskpage.locators.searchUserInput));
    this.searchUserBtn = element(by.xpath(Objects.taskpage.locators.searchUserBtn));
    this.searchedName = element.all(by.repeater(Objects.taskpage.locators.searchedName)).get(0);
    this.confimrBtn = element(by.xpath(Objects.taskpage.locators.confimrBtn));
    this.notesTextArea = element(by.xpath(Objects.taskpage.locators.notesTextArea));
    this.linkButton = element.all(by.xpath(Objects.taskpage.locators.linkButton)).get(0);
    this.linkInputText = element(by.xpath(Objects.taskpage.locators.linkInputText));
    this.linkInputUrl = element(by.xpath(Objects.taskpage.locators.linkInputUrl));
    this.insertLinkBtn = element(by.buttonText(Objects.taskpage.locators.insertLinkBtn));
    this.pictureButton = element.all(by.xpath(Objects.taskpage.locators.pictureButton)).get(0);
    this.chooseFilesBtn = element(by.xpath(Objects.taskpage.locators.chooseFilesBtn));

};

module.exports = new TaskPage();
