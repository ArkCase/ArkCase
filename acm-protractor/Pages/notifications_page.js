var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var description = element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(0);
var parentNumber = element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(1);
var objectType = element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(2);
var modifiedBy = element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(3);
var notificationModule = element(by.css(Objects.notificationPage.locators.notificationModule));
var searchNotificationInput = element(by.model(Objects.notificationPage.locators.notificationsInput));
var searchBtn = element(by.css(Objects.notificationPage.locators.searchBtn));
var sortObjectType = element.all(by.xpath(Objects.notificationPage.locators.sort)).get(2);
var sortModifiedBy = element.all(by.xpath(Objects.notificationPage.locators.sort)).get(3);
var notificationTitle = element(by.xpath(Objects.notificationPage.locators.notificationTitle));


var NotificationPage = function() {


    this.clickNotificationsModule = function() {

        browser.executeScript('arguments[0].click()', notificationModule);

    }

    this.selectNotification = function(notification) {

        browser.wait(EC.visibilityOf(element(by.cssContainingText(Objects.notificationPage.locators.facets, notification))), 20000, "Checkbox for" + " " + notification + "Is not displayed").then(function() {
            element(by.cssContainingText(Objects.notificationPage.locators.facets, notification)).click().then(function() {
                browser.wait(EC.visibilityOf(element(by.cssContainingText(Objects.notificationPage.locators.facets, notification))), 10000, "The facet for" + notification + "Is not displayed");
            });
        });
    }

    this.searchForNotification = function(data) {

        browser.wait(EC.visibilityOf(element(by.model(Objects.notificationPage.locators.notificationsInput))), 20000, "Search input for notification is not displayed").then(function() {
            searchNotificationInput.sendKeys(data).then(function() {
                searchBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.notificationPage.locators.notificationCol)).get(2)), 10000, "Notification column after search is not visible");
                });
            });
        });
    }

    this.clicksortObjectType = function() {
        sortObjectType.click();
    }


    this.returnObjectType = function() {
        return objectType.getText();
    }

    this.returnDescription = function() {
        return description.getText();
    };

    this.returnModifiedBy = function () {
        return modifiedBy.getText();
    }

    this.returnModifiedByMonth = function() {

        modifiedBy.getText().then(function(text) {
            var res = text.substring(0, 2);
            var months = [util.returnCurrentMonth(), util.returnPreviousMonth()];
            expect(months).toContain(res);

        });
    }

    this.returnModifiedByYear = function() {

        modifiedBy.getText().then(function(text) {
            var res = text.substring(6, 10);
            var years = [util.returnpreviousYear(), util.returnCurrentYear()];
            expect(years).toContain(res);
        });

    }

    this.returnModifiedByWeek = function() {

        modifiedBy.getText().then(function(text) {
            var res = text.substring(3, 5);
            var week=util.previousWeek();
            expect(week).toContain(res);
        });
    }

    this.clickSortModifiedBy = function() {
        sortModifiedBy.click();
    }


    this.returnParentNnumber = function() {
        return parentNumber.getText();
    }

    this.vaidateNotificationTitle = function () {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.notificationPage.locators.notificationTitle))), 30000, "Notification title is not visible");
            expect(notificationTitle.getText()).toEqual(Objects.notificationPage.data.notificationsTitle, "Notification page is not succesfully opened");
    }

};


NotificationPage.prototype = basePage;
module.exports = new NotificationPage();
