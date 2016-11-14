var Objects=require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var SelectWrapper = require('../util/select-wrapper.js');
var waitHelper = require('../util/waitHelper.js');
var complaintButton = element(By.repeater(Objects.complaintPage.locators.complaintButton));
var firstName = element(By.name(Objects.complaintPage.locators.firstName));
var lastName = element(By.name(Objects.complaintPage.locators.lastName));
var initiatorTab = element(By.xpath(Objects.complaintPage.locators.initiatorTab));
var incidentTab = element(By.xpath(Objects.complaintPage.locators.incidentTab));
var peopleTab = element(By.xpath(Objects.complaintPage.locators.peopleTab));
var attachmentsTab = element(By.xpath(Objects.complaintPage.locators.attachmentsTab));
var participantsTab = element(By.xpath(Objects.complaintPage.locators.participantsTab));
var incidentCategoryDDListBox = element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox));
var complaintTitle = element(By.name(Objects.complaintPage.locators.complaintTitle));
var submitButton = element(By.buttonText(Objects.complaintPage.locators.submitButton));
var nextButton = element(by.xpath(Objects.casepage.locators.nextBtn));
var radioButtonNewInitiator = element(by.xpath(Objects.complaintPage.locators.radioButtonNewInitiator));

var ComplaintPage = function() {
    browser.ignoreSynchronization = true;
    this.clickComplaintButton = function () {
        complaintButton.click();
        return this;
    };

    this.submitInitiatorInformation = function (name, surname){
        this.clickRadioBtnNewInitiator();
        browser.wait(EC.presenceOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function () {
            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function () {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function () {
                    firstName.click().then(function () {
                        firstName.sendKeys(name);
                    })
                });
            });
        });
        browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.lastName))), 30000).then(function () {
            lastName.click().then(function () {
                lastName.sendKeys(surname);
            })
        });
        return this;
    };
    
    this.clickNextButton = function () {
        browser.wait(EC.presenceOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function () {
            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function () {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function () {
                    nextButton.click();
                 });
            });
        });
    };

    this.selectIncidentCategory = function (category) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000).then(function () {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000).then(function () {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000).then(function () {
                    incidentCategoryDDListBox.click().then(function() {
                        var incidentCategory = element(by.linkText(category));
                        waitHelper.waitElementToBeVisible(incidentCategory);
                        incidentCategory.click();

                    });

                })
            })

        })
        return this;
    };

    this.insertTitle = function (title){
        complaintTitle.click();
        complaintTitle.clear();
        complaintTitle.sendKeys(title);
        return this;
    };
    this.insertIncidentInformation = function(category, title){
        this.selectIncidentCategory(category);
        this.insertTitle(title);
        return this;
    };
    this.clickTab = function(tabname){
        switch (tabname) {
            case "Initiator":
                initiatorTab.click();
                break;
            case "Incident":
                incidentTab.click();
                break;
            case "People":
                peopleTab.click();
                break;
            case "Attachments":
                attachmentsTab.click();
                break;
            case "Participants":
                participantsTab.click();
                break;
            default:
                break;
        }
        return this;
    };
    this.clickSubmitButton = function () {
        submitButton.click();
        return this;
    };
    this.clickRadioBtnNewInitiator = function () {
        browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.radioButtonNewInitiator))), 30000).then(function () {
            radioButtonNewInitiator.click();
        });
        return this;
    }

};
ComplaintPage.prototype = basePage;
module.exports = new ComplaintPage();