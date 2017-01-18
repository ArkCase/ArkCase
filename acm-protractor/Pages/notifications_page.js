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




var NotificationPage = function() {


    this.clickNotificationsModule = function() {

        browser.executeScript('arguments[0].click()', notificationModule);
        browser.sleep(10000);
    }

    this.selectNotification = function(notification) {

        browser.wait(EC.visibilityOf(element(by.cssContainingText(Objects.notificationPage.locators.facets, notification))), 20000, "Checkbox for" + " " + notification + "Is not displayed").then(function() {
            element(by.cssContainingText(Objects.notificationPage.locators.facets, notification)).click().then(function() {
                browser.sleep(5000);
            });
        });
    }

    this.searchForNotification = function(data) {

        browser.wait(EC.visibilityOf(element(by.model(Objects.notificationPage.locators.notificationsInput))), 20000, "Search input for notification is not displayed").then(function() {
            searchNotificationInput.sendKeys(data).then(function() {
                searchBtn.click().then(function() {
                    browser.sleep(5000);
                });
            });
        });
    }

    this.returnObjectType = function() {
        return objectType.getText();
    }

    this.returnDescription = function() {
        return description.getText();
    };

    this.returnModifiedBy = function() {
        return modifiedBy.getText();
    }

    this.returnParentNnumber = function() {
        return parentNumber.getText();
    }

};


NotificationPage.prototype = basePage;
module.exports = new NotificationPage();
