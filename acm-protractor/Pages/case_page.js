var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var basePage = require('./base_page.js');
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
var changeCaseStatusBtn = element(by.css(Objects.casepage.locators.changeCaseStatusBtn));
var newCaseButton = element(by.css(Objects.casepage.locators.newCasesButton));
var editBtn = element(by.css(Objects.casepage.locators.editBtn));
var mergeBtn = element(by.xpath(Objects.casepage.locators.mergeBtn));
var splitBtn = element(by.xpath(Objects.casepage.locators.splitBtn));
var changeCaseStatusTitle = element(by.className(Objects.casepage.locators.changeCaseStatusTitle));
var changeStatusDropDown = element(by.className(Objects.casepage.locators.changeStatusDropDown));
var statusClosed = element(by.xpath(Objects.casepage.locators.statusClosed));
var refreshBtn = element(by.xpath(Objects.casepage.locators.refreshBtn));
var taskTitle = element(by.xpath(Objects.casepage.locators.taskTitle));
var priorityLink = element(by.xpath(Objects.casepage.locators.priority));
var priorityDropDownEdit = element(by.xpath(Objects.casepage.locators.priorityDropDown));
var priorityBtn = element(by.xpath(Objects.casepage.locators.priorityBtn));
var createdDate = element(by.xpath(Objects.casepage.locators.createdDate));
var assigneeLink = element(by.xpath(Objects.casepage.locators.assignee));
var assigneeDropDown = element(by.xpath(Objects.casepage.locators.assigneeDropDown));
var assigneeBtn = element(by.xpath(Objects.casepage.locators.assigneeBtn));
var expandLinksButton = element(by.xpath(Objects.casepage.locators.expandLinksButton));
var notesLink = element(by.xpath(Objects.casepage.locators.notesLink));
var addNoteBtn = element(by.xpath(Objects.casepage.locators.addNoteBtn));
var noteTextArea = element(by.model(Objects.casepage.locators.noteTextArea));
var saveNoteBtn = element(by.xpath(Objects.casepage.locators.saveNoteBtn));
var addedNoteName = element.all(by.repeater(Objects.casepage.locators.addedNoteName)).get(0);
var deleteNoteBtn = element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var editNoteBtn = element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var emptyNoteTable = element(by.xpath(Objects.casepage.locators.emptyNoteTable));
var showLinksBtn = element(by.xpath(Objects.casepage.locatorsshowLinksBtn));
var addNewTaskBtn = element(by.xpath(Objects.casepage.locators.addNewTaskBtn));
var taskAssighnee = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(1);
var taskCreated = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(2);
var taskPriority = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(3);
var taskDueDate = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(4);
var taskStatus = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(5);
var refreshCasesList = element(by.css(Objects.casepage.locators.refreshCasesList));
var firstCaseInCasesList = element(by.xpath(Objects.casepage.locators.firstCaseInCasesList));
var caseID = element(by.xpath(Objects.casepage.locators.caseID));





var CasePage = function() {

    browser.ignoreSynchronization = true;

    this.navigateToNewCasePage = function() {
        newCaseBtn.click();
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

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.caseTitle))), 30000);
        var caseType = element(by.linkText(type));
        caseTitle.click().then(function() {
            caseTitle.sendKeys(title).then(function() {
                caseTypeDropDown.click().then(function() {
                    browser.wait(EC.textToBePresentInElement((caseType), type), 10000).then(function() {
                        caseType.click();
                    });
                });
            });
        });
        return this;
    }

    this.clickNextBtn = function() {
        nextBtn.click();
        return this;
    }

    this.initiatorInformation = function(firstname, lastname) {

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.firstName))), 30000);
        firstName.click().then(function() {
            firstName.sendKeys(firstname);
        });
        lastName.click().then(function() {
            lastName.sendKeys(lastname);
        });
    }

    this.initiatorInformation = function(firstname, lastname) {

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.firstName))), 10000);
        firstName.click().then(function() {
            firstName.sendKeys(firstname).then(function() {
                lastName.click().then(function() {
                    lastName.sendKeys(lastname);
                });
            });
        });

        return this;
    }


    this.waitForCaseType = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesType))), 30000);
    }

    this.waitForCaseTitle = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000);
    }

    this.clickSubmitBtn = function() {

        submitBtn.click();
        return this;
    }

    this.switchToDefaultContent = function() {

        browser.driver.switchTo().defaultContent();
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 60000).then(function() {
            browser.sleep(10000);
        });
        return this;
    }

    this.waitForCaseType = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesType))), 30000, "Case type is not displayed");
    }

    this.waitForCaseTitle = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 60000, "Case title is not displayed");
    }


    this.returnCasesPageTitle = function() {

        return casesPageTitle.getText();

    };

    this.returnCaseTitle = function() {

        return casesTitle.getText();
    };


    this.returnCaseType = function() {


        return casesType.getText();

    };

    this.waitForChangeCaseButton = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.changeCaseStatusBtn))), 30000);

    };



    this.clickChangeCaseBtn = function() {

        changeCaseStatusBtn.click().then(function() {
            browser.ignoreSynchronization = true;
        });

        return this;

    };

    this.selectCaseStatus = function(status) {

        var caseStatus = element(by.linkText(status));
        browser.wait(EC.visibilityOf(element(by.className(Objects.casepage.locators.changeCaseStatusTitle))), 15000).then(function() {
            changeStatusDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((caseStatus), status), 10000, "The option " + status + " Is not displayed").then(function() {
                    caseStatus.click();
                });
            });
        });

    }

    this.chnageCaseSubmit = function() {
        browser.executeScript('arguments[0].click()', submitBtn);
    }


    this.waitForPriority = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.priority))), 20000);
    };

    this.returnPriority = function() {
        return priorityLink.getText();
    };

    this.waitForCreatedDate = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.createdDate))), 20000);
    };

    this.returnCreatedDate = function() {
        return createdDate.getText();
    };

    this.editPriority = function(priority) {

        priorityLink.click().then(function() {
            priorityDropDownEdit.$('[value="string:' + priority + '"]').click().then(function() {
                priorityBtn.click();
            });
        });
        return this;
    };

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
        browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.assignee))), 20000);
    };

    this.returnAssignee = function() {
        return assigneeLink.getText();
    };

    this.clickExpandLinks = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn))), 30000);
        expandLinksButton.click();
        return this;
    }

    this.clickNewCaseButton = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.newCasesButton))), 30000, "New case button in cases page is not displayed").then(function() {
            newCaseButton.click();
        });
        return this;
    }

    this.clickEditCaseBtn = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.editBtn))), 30000, "Edit button is not displayed").then(function() {
            editBtn.click();
        });
        return this;
    }

    this.waitForCaseID = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.caseID))), 60000, "Case ID is not present").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.caseID))), 60000, "Case ID is not displayed");
        });
    }
    this.getCaseId = function() {
        return caseID.getText();
    }




};


CasePage.prototype = basePage;
module.exports = new CasePage();
