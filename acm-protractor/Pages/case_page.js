var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var EC = protractor.ExpectedConditions;
var newBtn = element(by.linkText(Objects.casepage.locators.newButton));
var newCaseBtn = element(by.linkText(Objects.casepage.locators.newCaseBtn));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseArson = element(by.linkText(Objects.casepage.locators.caseArson));
var casesTitle = element(by.xpath(Objects.casepage.locators.casesTitle));
var casesType = element(by.xpath(Objects.casepage.locators.casesType));
var casesPageTitle = element(by.xpath(Objects.casepage.locators.casesPageTitle));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseTypeDropDown = element(by.className(Objects.casepage.locators.caseType));
var nextBtn = element(by.xpath(Objects.casepage.locators.nextBtn));
var firstName = element(by.name(Objects.casepage.locators.firstName));
var lastName = element(by.name(Objects.casepage.locators.lastName));
var submitBtn = element(by.xpath(Objects.casepage.locators.submitBtn));
var changeCaseStatusBtn = element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn));
//var newBtn = element(by.xpath(Objects.casepage.locators.newCasesBtn));
var editBtn = element(by.xpath(Objects.casepage.locators.editBtn));
var subscribe = element(by.xpath(Objects.casepage.locators.subscribeBtn));
var mergeBtn = element(by.xpath(Objects.casepage.locators.mergeBtn));
var splitBtn = element(by.xpath(Objects.casepage.locators.splitBtn));
var changeCaseStatusTitle = element(by.className(Objects.casepage.locators.changeCaseStatusTitle));
var changeStatusDropDown = element(by.className(Objects.casepage.locators.changeStatusDropDown));
var statusClosed = element(by.xpath(Objects.casepage.locators.statusClosed));
var selectApprover = element(by.xpath(Objects.casepage.locators.selectApprover));
var searchForUser = element(by.xpath(Objects.casepage.locators.searchForUser));
var goBtn = element(by.xpath(Objects.casepage.locators.goBtn));
var addBtn = element(by.xpath(Objects.casepage.locators.addBtn));
var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUser));
var tasksLinkBtn = element(by.xpath(Objects.casepage.locators.tasksLink));
var refreshBtn = element(by.xpath(Objects.casepage.locators.refreshBtn));
var taskTitle = element(by.xpath(Objects.casepage.locators.taskTitle));
var priorityLink = element(by.xpath(Objects.casepage.locators.priority));
var priorityDropDownEdit = element(by.xpath(Objects.casepage.locators.priorityDropDown));
var priorityBtn = element(by.xpath(Objects.casepage.locators.priorityBtn));
var createdDate = element(by.xpath(Objects.casepage.locators.createdDate));
var assigneeLink = element(by.xpath(Objects.casepage.locators.assignee));
var assigneeDropDown = element(by.xpath(Objects.casepage.locators.assigneeDropDown));
var assigneeBtn = element(by.xpath(Objects.casepage.locators.assigneeBtn));



var CasePage = function() {

    browser.ignoreSynchronization = true;
    this.navigateToNewCasePage = function() {

        newBtn.click().then(function() {
            newCaseBtn.click();
        });
        return this;

    }

    this.switchToIframes = function() {
        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.className("new-iframe ng-scope"))), 30000);
        browser.switchTo().frame(browser.driver.findElement(by.className("new-iframe ng-scope"))).then(function() {
            browser.switchTo().frame(browser.driver.findElement(By.className("frevvo-form")));
        });
        return this;
    }

    this.submitGeneralInformation = function(title, type) {

        caseTitle.click().then(function() {
            caseTitle.sendKeys(title);
        });

        caseTypeDropDown.click().then(function() {

            var caseType = element(by.linkText(type)).click();

        });

        nextBtn.click();
        return this;

    }

    this.initiatorInformation = function(firstname, lastname) {

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.firstName))), 5000);
        firstName.click().then(function() {
            firstName.sendKeys(firstname);
        });
        lastName.click().then(function() {
            lastName.sendKeys(lastname);
        });

        submitBtn.click();
        return this;
    }



    this.switchToDefaultContent = function() {

        browser.driver.switchTo().defaultContent();

    }

    this.waitForCaseType = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesType))), 30000);
    }

    this.waitForCaseTitle = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000);
    }


    this.returnCasesPageTitle = function() {

        return casesPageTitle.getText();
    }

    this.returnCaseTitle = function() {

        return casesTitle.getText();
    }

    this.returnCaseType = function() {


        return casesType.getText();
    }

    this.waitForChangeCaseButton = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn))), 30000);

    }


    this.clickRefreshBtn = function() {

        refreshBtn.click();
    }



    this.clickChangeCaseBtn = function() {

        changeCaseStatusBtn.click().then(function() {
            browser.ignoreSynchronization = true;

        });

        return this;
    }


    this.selectCaseStatusClosed = function() {

        browser.ignoreSynchronization = true;

        browser.wait(EC.visibilityOf(element(by.className(Objects.casepage.locators.changeCaseStatusTitle))), 15000);
        changeStatusDropDown.click().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.statusClosed))), 10000).then(function() {
                statusClosed.click();
            });
        });

        return this;
    }


    this.selectApprover = function(approverSamuel) {

        selectApprover.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addUser))), 10000);
            searchForUser.click();
            searchForUser.sendKeys(approverSamuel);
            goBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUser))), 3000);
                searchedUser.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addBtn))), 3000);
                    addBtn.click();
                });
            });
        });
        return this;
    }

    this.chnageCaseSubmit = function() {
        browser.executeScript('arguments[0].click()', submitBtn);
    }


    this.clickTasksLinkBtn = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tasksLink))), 20000).then(function() {

            tasksLinkBtn.click();
        });

        return this;

    }

    this.waitForTasksTable = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tasksTable))), 20000).then(function() {
            refreshBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.taskTitle))), 30000);
            });
        });
        return this;
    }



    this.returnAutomatedTask = function() {

        return taskTitle.getText();

    }

    this.clickTaskTitle = function() {
        taskTitle.click();
    }

    this.waitForPriority = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.priority))), 20000);
    }

    this.returnPriority = function() {

        return priorityLink.getText();

    }

    this.waitForCreatedDate = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.createdDate))), 20000);
    }

    this.returnCreatedDate = function() {
        return createdDate.getText();
    }


    this.editPriority = function(priority) {

        priorityLink.click().then(function() {
            priorityDropDownEdit.$('[value="string:' + priority + '"]').click().then(function() {
                priorityBtn.click();
            });
        });

    }

    this.editAssignee = function(assignee) {

        assigneeLink.click().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath("//*[@class='clearfix']/div[3]/div[1]/div/form/div/select/option[8]"))), 5000).then(function() {
                assigneeDropDown.$('[value="string:' + assignee + '"]').click().then(function() {
                    assigneeBtn.click();
                });

            });
        });
        return this;
    }


    this.waitForAssignee = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.assignee))), 20000);
    }

    this.returnAssignee = function() {
        return assigneeLink.getText();
    }





};

module.exports = new CasePage();
