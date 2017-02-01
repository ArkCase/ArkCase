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
var saveBtn = element(by.xpath(Objects.timetrackingPage.locators.saveBtn));
var editTimesheet = element(by.css(Objects.timetrackingPage.locators.editTimesheetBtn));
var newTimesheet = element(by.buttonText(Objects.timetrackingPage.locators.newTimesheetBtn));
var timeSheetsPageTitle = element(by.xpath(Objects.timetrackingPage.locators.timesheetsPageTitle));
var nextWeekBtn = element(by.buttonText(Objects.timetrackingPage.locators.nextWeekBtn));
var periodInput = element(by.xpath(Objects.timetrackingPage.locators.periodInput));
var timeTrackingDate = element(by.xpath(Objects.timetrackingPage.locators.timeTrackingDate));
var chargeCodeAlertMessage = element(by.xpath(Objects.timetrackingPage.locators.chargeCodeAlertMessage));
var timeTotal=element(by.name(Objects.timetrackingPage.locators.timeTotal));






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
            timesheetTypeDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((timesheetType), type), 10000);
            }).then(function() {
                timesheetType.click();
            }).then(function() {
                chargeCodeDropDown.click();
            }).then(function() {
                browser.wait(EC.textToBePresentInElement((chargeCode), code), 10000, "The" + code + "is not present in the dropdown");
            }).then(function() {
                chargeCode.click();
            }).then(function() {
                sundayInput.click().clear().sendKeys(hours);
            }).then(function() {
                mondayInput.click().clear().sendKeys(hours);
            }).then(function() {
                tuesdayInput.click().clear().sendKeys(hours);
            }).then(function() {
                wednsdayInput.click().clear().sendKeys(hours);
            }).then(function() {
                thursdayInput.click().clear().sendKeys(hours);
            }).then(function() {
                fridayInput.click().clear().sendKeys(hours);
            }).then(function() {
                saturdayInput.click().clear().sendKeys(hours);
            });
        });

        return this;
    }

    this.clickSaveBtn = function() {
        browser.executeScript('arguments[0].click()', saveBtn);
        browser.driver.switchTo().defaultContent();
        browser.sleep(15000);
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.timetrackingPage.locators.timesheetsPageTitle))), 30000, "Timesheets page title is not displayed");
        return this;
    }

    this.returnTimeTrackingDate = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.timetrackingPage.locators.timeTrackingDate))), 10000, "The time tracking date label is not displayed");
        return timeTrackingDate.getText();
    }

    this.returnchargeCodeAlertMessage = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.timetrackingPage.locators.chargeCodeAlertMessage))), 10000, "Alert message for empty charge code is not displayed");
        return chargeCodeAlertMessage.getText();
    }

    this.selectTimesheetType = function(type) {

        var timesheetType = element(by.linkText(type));
        browser.wait(EC.visibilityOf(element.all(by.xpath(Objects.timetrackingPage.locators.timesheetDropDown)).get(2)), 30000).then(function() {
            timesheetTypeDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((timesheetType), type), 10000);
            }).then(function() {
                timesheetType.click();
            });
        });
        return this;
    };

    this.clickEditTimesheetBtn = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.timetrackingPage.locators.editTimesheetBtn))), 30000, "Edit Timesheet button is not displayed").then(function() {
            editTimesheet.click();
        });
        return this;
    }

};
timeTrackingPage.prototype = basePage;
module.exports = new timeTrackingPage();
