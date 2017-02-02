var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var basePage = require('../Pages/base_page.js');
var SelectWrapper = require('../util/select-wrapper.js');
var EC = protractor.ExpectedConditions;
var newBtn = element(by.linkText(Objects.casepage.locators.newButton));
var newCaseBtn = element(by.linkText(Objects.casepage.locators.newCaseBtn));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseArson = element(by.linkText(Objects.casepage.locators.caseArson));
var casesTitle = element(by.xpath(Objects.casepage.locators.casesTitle));
var casesType = element(by.xpath(Objects.casepage.locators.casesType));
var casesPageTitle = element(by.xpath(Objects.casepage.locators.casesPageTitle));
var caseTypeDropDown = element(by.className(Objects.casepage.locators.caseType));
var nextBtn = element(by.xpath(Objects.casepage.locators.nextBtn));
var firstName = element(by.name(Objects.casepage.locators.firstName));
var lastName = element(by.name(Objects.casepage.locators.lastName));
var changeCaseStatusBtn = element(by.css(Objects.casepage.locators.changeCaseStatusBtn));
var newCaseButton = element(by.css(Objects.casepage.locators.newCasesButton));
var editBtn = element(by.css(Objects.casepage.locators.editBtn));
var mergeBtn = element(by.xpath(Objects.casepage.locators.mergeBtn));
var splitBtn = element(by.xpath(Objects.casepage.locators.splitBtn));
var changeCaseStatusTitle = element(by.className(Objects.casepage.locators.changeCaseStatusTitle));
var changeStatusDropDown = element(by.className(Objects.casepage.locators.changeStatusDropDown));
var statusClosed = element(by.xpath(Objects.casepage.locators.statusClosed));
var taskTitle = element(by.xpath(Objects.casepage.locators.taskTitle));
var createdDate = element(by.xpath(Objects.casepage.locators.createdDate));
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
var refreshCasesList = element(by.css(Objects.casepage.locators.refreshCasesList));
var firstCaseInCasesList = element(by.xpath(Objects.casepage.locators.firstCaseInCasesList));
var caseID = element(by.xpath(Objects.casepage.locators.caseID));
var reinvestigateBtn = element(by.css(Objects.casepage.locators.reinvestigateBtn));
var casesTitleStatus = element.all(by.xpath(Objects.casepage.locators.caseTitleStatus)).get(0);
var caseTitleWithStatus = element(by.xpath(Objects.casepage.locators.caseTitleWithStatus));
var submitBtn = element(by.xpath(Objects.casepage.locators.submitBtn));
var priority = element(by.xpath(Objects.casepage.locators.priority));


var CasePage = function() {

    browser.ignoreSynchronization = true;

    this.navigateToNewCasePage = function() {
        newCaseBtn.click();
        return this;
    }

    this.switchToIframes = function() {

        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.className("new-iframe ng-scope"))), 30000,"First iframe is not visible");
        browser.switchTo().frame(browser.driver.findElement(by.className("new-iframe ng-scope"))).then(function() {
            browser.switchTo().frame(browser.driver.findElement(By.className("frevvo-form")));
        });
        return this;
    }
    this.submitGeneralInformation = function(title, type) {

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.caseTitle))), 30000, "Case Title is not visible");
        var caseType = element(by.linkText(type));
        caseTitle.click().then(function() {
            caseTitle.sendKeys(title).then(function() {
                caseTypeDropDown.click().then(function() {
                    browser.wait(EC.textToBePresentInElement((caseType), type), 10000, "Selected " + type + "is not present in type drop down list").then(function() {
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

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.firstName))), 10000, "First name field is not visible");
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

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesType))), 30000, "Case type is not displayed");
    }

    this.waitForCaseTitle = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000, "Case title is not visible");
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
    this.caseTitleStatus = function(titleStatus) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.caseTitleStatus))), 30000, "Case title status is not visible").then(function() {
            browser.wait(EC.textToBePresentInElement((casesTitleStatus), titleStatus), 10000, titleStatus + " is not present in case status drop down list");

        });
    }

    this.returnCaseType = function() {

        return casesType.getText();

    };

    this.waitForChangeCaseButton = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.changeCaseStatusBtn))), 30000, "Change case button is not visible");

    };

    this.clickChangeCaseBtn = function() {

        changeCaseStatusBtn.click().then(function() {
            browser.ignoreSynchronization = true;
        });

        return this;

    };

    this.selectCaseStatus = function(status) {
        var caseStatus = element(by.linkText(status));
        browser.wait(EC.visibilityOf(element(by.className(Objects.casepage.locators.changeCaseStatusTitle))), 15000, "Change case status title is not visible").then(function() {
            changeStatusDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((caseStatus), status), 10000, "The option " + status + " Is not displayed").then(function() {
                    caseStatus.click();
                });
            });
        });
    };

    this.chnageCaseSubmit = function() {

        this.switchToIframes();
        browser.executeScript('arguments[0].click()', submitBtn);
    };


    this.waitForCreatedDate = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.createdDate))), 20000, "Created date is not visible");
    };

    this.returnCreatedDate = function() {
        return createdDate.getText();
    };


    this.clickExpandLinks = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn))), 30000, "Change case status button is not visible");
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

    this.clickReinvesigateBtn = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.reinvestigateBtn))), 30000, "Reinvestigate button is not displayed").then(function() {
            reinvestigateBtn.click();
        });
    };

    this.returnPriority = function() {
        return priority.getText();
    }

    this.waitForCasesPage = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000);
        browser.sleep(10000);
    }

};


CasePage.prototype = basePage;
module.exports = new CasePage();
