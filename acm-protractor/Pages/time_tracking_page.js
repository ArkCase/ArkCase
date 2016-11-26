var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;

var newTimesheetLinkBtn = element(by.linkText(Objects.timetrackingPage.locators.newTimesheetLink));
var timesheetTypeDropDown = element.all(by.xpath(Objects.timetrackingPage.locators.timesheetDropDown)).get(2);
var chargeCodeDropDown = element.all(by.xpath(Objects.timetrackingPage.locators.timesheetDropDown)).get(3);
var timetrackingTitle = element(by.xpath(Objects.timetrackingPage.locators.timetrackingTitle));
var sundayInput = element(by.name(Objects.timetrackingPage.locators.sundayInput));
var mondayInput = element(by.name(Objects.timetrackingPage.locators.mondayInput));
var tuesdayInput = element(by.name(Objects.timetrackingPage.locators.tuesdayInput));
var wednsdayInput = element(by.name(Objects.timetrackingPage.locators.wednsdayInput));
var thursdayInput = element(by.name(Objects.timetrackingPage.locators.thursdayInput));
var fridayInput = element(by.name(Objects.timetrackingPage.locators.fridayInput));
var saturdayInput = element(by.name(Objects.timetrackingPage.locators.saturdayInput));
var saveBtn = element(by.buttonText(Objects.timetrackingPage.locators.saveBtn));
var editTimesheet = element(by.buttonText(Objects.timetrackingPage.locators.editTimesheetBtn));
var newTimesheet = element(by.buttonText(Objects.timetrackingPage.locators.newTimesheetBtn));
var caseFileModule = element(by.xpath(Objects.timetrackingPage.locators.caseFileModule));
var timeSheetsPageTitle = element(by.xpath(Objects.timetrackingPage.locators.timesheetsPageTitle));
var nextWeekBtn = element(by.buttonText(Objects.timetrackingPage.locators.nextWeekBtn));
var periodInput = element(by.name(Objects.timetrackingPage.locators.periodInput));






var timeTrackingPage = function() {

    this.navigateToTimeTrackingPage = function() {

        newTimesheetLinkBtn.click().then(function() {
            browser.ignoreSynchronization = true;
            return this;
        });
    }

    this.submitTimesheetTable = function(type, code, hours) {

        var timesheetType = element(by.linkText(type));
        var chargeCode = element(by.linkText(code));
        browser.wait(EC.visibilityOf(element.all(by.xpath(Objects.timetrackingPage.locators.timesheetDropDown)).get(2)), 30000).then(function() {
            browser.sleep(5000);
            periodInput.click().then(function() {
                browser.sleep(3000);
                periodInput.clear().then(function() {
                        periodInput.sendKeys(Objects.timetrackingPage.data.date).then(function() {
                            browser.sleep(3000);
                            nextWeekBtn.click().then(function() {
                                browser.sleep(3000);
                                timesheetTypeDropDown.click().then(function() {
                                    browser.wait(EC.textToBePresentInElement((timesheetType), type), 10000).then(function() {
                                        timesheetType.click().then(function() {
                                            chargeCodeDropDown.click().then(function() {
                                                browser.wait(EC.textToBePresentInElement((chargeCode), code), 10000, "The" + code + "is not present in the dropdown").then(function() {
                                                    chargeCode.click().then(function() {
                                                        sundayInput.sendKeys(hours).then(function() {
                                                            mondayInput.sendKeys(hours).then(function() {
                                                                tuesdayInput.sendKeys(hours).then(function() {
                                                                    wednsdayInput.sendKeys(hours).then(function() {
                                                                        thursdayInput.sendKeys(hours).then(function() {
                                                                            fridayInput.sendKeys(hours).then(function() {
                                                                                saturdayInput.sendKeys(hours);
                                                                            });
                                                                        });
                                                                    });
                                                                });
                                                            });
                                                        });
                                                    });
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        return this;
    }

    this.clickSaveBtn = function() {
        browser.executeScript('arguments[0].click()', saveBtn);
          browser.driver.switchTo().defaultContent();
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.timetrackingPage.locators.timesheetsPageTitle))), 30000, "Timesheets page title is not displayed");
        return this;
    }
    this.clickModuleCasesFiles = function() {

         browser.executeScript('arguments[0].click()', caseFileModule);
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.timesheetLinkBtn))), 30000);
    
        return this;
    }
};

module.exports = new timeTrackingPage();
