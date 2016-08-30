var TaskPage = function() {

    this.newBtn = element(by.xpath('html/body/header/div/nav/ul/li/a/span'));
    this.taskBtn = element(by.xpath('html/body/header/div/nav/ul/li/div/div[3]/div/a/i'));
    this.taskBtnName = element(by.xpath('html/body/header/div/nav/ul/li/div/div[3]/div/a/span'));
    this.taskTitle = element(by.css('.module-header'));
    this.Subject = element(by.id('subject'));
    this.StartDate = element(by.xpath('.//*[@id="taskStartDate"]'));
    this.DueDateBtn = element(by.xpath('html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[3]/div/div/span/button'));
    this.todayDateFromCalendar = element(by.xpath('html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[4]/div[3]/div/div/ul/li[2]/span/button[1]'));
    this.DueDateInput = element(by.id('dueDate'));
    this.percentCompleteInput = element(by.id('percentComplete'));
    this.saveButton = element(by.id('saveButton'));
    this.tasksTitle = element(by.xpath('html/body/div[1]/div/div[2]/section/div/div/div/div[1]/h3/span'));
    this.statusDropDown = element(by.model('config.data.status'));
    this.statusClosed = element(by.xpath('.//*[.="Closed"]'));
    this.statusInactive = element(by.xpath('.//*[.="Inactive"]'));
    this.priorityDropDown = element(by.model('config.data.priority'));
    this.priorityLow = element(by.xpath('.//*[.="Low"]'));
    this.priorityMedium = element(by.xpath('.//*[.="Medium"]'));
    this.priorityHigh = element(by.xpath('.//*[.="High"]'));
    this.priorityExpedite = element(by.xpath('.//*[.="Expedite"]'));
    this.assigneeInput = element(by.id('assignee'));
    this.userSearchTitle = element(by.xpath('.//*[@class="modal-header ng-binding ng-scope"]'));
    this.searchUserInput = element(by.xpath('/html/body/div[6]/div/div/search-modal/div[2]/div/div[1]/div/input'));
    this.searchUserBtn = element(by.xpath('.//*[@class="btn btn-md btn-primary ng-binding"]'));
    this.searchedName = element.all(by.repeater('rowRenderIndex, row) in rowContainer.renderedRows track by $index')).get(0);
    this.confimrBtn = element(by.xpath('.//*[@class="btn btn-primary ng-binding"]'));
    this.notesTextArea = element(by.xpath('.//*[@class="note-editable panel-body"]'));
    this.linkButton = element(by.xpath('/html/body/div[2]/div/div[2]/section/div/div/form/div[2]/div[6]/div/div[2]/div[2]/div[9]/button[1]'));
    this.linkInputText = element(by.xpath('/html/body/div[1]/div[2]/div/div/div[2]/div[1]/input'));
    this.linkInputUrl = element(by.xpath('/html/body/div[1]/div[2]/div/div/div[2]/div[2]/input'));
    this.insertLinkBtn = element(by.buttonText("Insert Link"));

};

module.exports = new TaskPage();
