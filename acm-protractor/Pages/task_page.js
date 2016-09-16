var TaskPage = function() {

    this.newBtn = element(by.linkText("New"));
    this.taskBtn = element(by.linkText('Task'));
    this.taskTitle = element(by.css('.module-header'));
    this.Subject = element(by.id('subject'));
    this.StartDate = element(by.xpath('.//*[@id="taskStartDate"]'));
    this.DueDateBtn = element.all(by.xpath('.//*[@class="input-group"]/span/button')).get(1);
    this.todayDateFromCalendar = element(by.buttonText('Today'));
    this.DueDateInput = element(by.id('dueDate'));
    this.percentCompleteInput = element(by.id('percentComplete'));
    this.saveButton = element(by.id('saveButton'));
    this.tasksTitle = element(by.xpath('.//*[@class="module-header"]/h3/span'));
    this.priorityDropDown = element(by.model('config.data.priority'));
    this.priorityLow = element(by.xpath('.//*[.="Low"]'));
    this.priorityMedium = element(by.xpath('.//*[.="Medium"]'));
    this.priorityHigh = element(by.xpath('.//*[.="High"]'));
    this.priorityExpedite = element(by.xpath('.//*[.="Expedite"]'));
    this.assigneeInput = element(by.id('assignee'));
    this.userSearchTitle = element(by.xpath('.//*[@class="modal-header ng-binding ng-scope"]'));
    this.searchUserInput = element(by.xpath('.//*[@class="modal-search"]/div[1]/div/input'));
    this.searchUserBtn = element(by.xpath('.//*[@class="btn btn-md btn-primary ng-binding"]'));
    this.searchedName = element.all(by.repeater('rowRenderIndex, row) in rowContainer.renderedRows track by $index')).get(0);
    this.confimrBtn = element(by.xpath('.//*[@class="btn btn-primary ng-binding"]'));
    this.notesTextArea = element(by.xpath('.//*[@class="note-editable panel-body"]'));
    this.linkButton = element.all(by.xpath('.//*[@class="note-insert btn-group"]/button[1]')).get(0);
    this.linkInputText = element(by.xpath('/html/body/div[1]/div[2]/div/div/div[2]/div[1]/input'));
    this.linkInputUrl = element(by.xpath('/html/body/div[1]/div[2]/div/div/div[2]/div[2]/input'));
    this.insertLinkBtn = element(by.buttonText("Insert Link"));
    this.pictureButton=element.all(by.xpath('.//*[@class="note-insert btn-group"]/button[2]')).get(0);
    this.chooseFilesBtn=element(by.xpath('/html/body/div[1]/div[1]/div/div/div[2]/div[1]/input'));



};

module.exports = new TaskPage();
