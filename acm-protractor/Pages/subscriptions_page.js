var Objects = require('../json/Objects.json');
var util = require('../util/utils.js');
var EC = protractor.ExpectedConditions;
var subscriptionModule = element(by.css(Objects.subscriptionsPage.locators.subscriptionsModule));
var subscriptionsTitle = element(by.xpath(Objects.subscriptionsPage.locators.subscriptionsTitle));
var searchSubscriptionInput = element(by.model(Objects.subscriptionsPage.locators.subscriptionsInput));
var searchBtn = element(by.css(Objects.subscriptionsPage.locators.searchBtn));
var resultEvent = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(0);
var resultParentName = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(1);
var resultParentType = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(2);
var resultModfied = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(3);
var parentTypeSort = element.all(by.xpath(Objects.subscriptionsPage.locators.sort)).get(2);
var modifiedBySort = element.all(by.xpath(Objects.subscriptionsPage.locators.sort)).get(3);

var subscriptionPage = function() {

    this.clickSubcriptionsModule = function() {

        browser.executeScript('arguments[0].click()', subscriptionModule);

    }


    this.selectSubscription = function(subscription) {

        browser.wait(EC.visibilityOf(element(by.cssContainingText(Objects.subscriptionsPage.locators.facets, subscription))), 20000, "Checkbox for" + " " + subscription + "Is not displayed").then(function() {
            element(by.cssContainingText(Objects.subscriptionsPage.locators.facets, subscription)).click().then(function() {
                browser.wait(EC.visibilityOf(element(by.cssContainingText(Objects.subscriptionsPage.locators.facets, subscription))), 10000, subscription + "is not diplayed in facets");
            });
        });
    }

    this.searchForSubscription = function(data) {

        browser.wait(EC.visibilityOf(element(by.model(Objects.subscriptionsPage.locators.subscriptionsInput))), 20000, "Search input for subscription is not displayed").then(function() {
            searchSubscriptionInput.sendKeys(data).then(function() {
                searchBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(2)), 10000, "After searching subscriptions result table is not visible");
                });
            });
        });
    }


    this.clickParentTypeSort = function() {
        parentTypeSort.click();

    }
    this.clikmodifiedBySort = function() {
        modifiedBySort.click();
    }

    this.returnModifiedByMonth = function() {

        resultModfied.getText().then(function(text) {
            var res = text.substring(0, 2);
            var months = [util.returnCurrentMonth(), util.returnPreviousMonth()];
            expect(months).toContain(res);

        });
    }

    this.returnModifiedByYear = function() {

        resultModfied.getText().then(function(text) {
            var res = text.substring(6, 10);
            var years = [util.returnpreviousYear(), util.returnCurrentYear()];
            expect(years).toContain(res);
        });

    }

    this.returnModifiedByWeek = function() {

        resultModfied.getText().then(function(text) {
            var res = text.substring(3, 5);
            var week = util.previousWeek();
            expect(week).toContain(res);
        });
    }


    this.returnResultParentType = function() {
        return resultParentType.getText();
    }
    this.returnResultModified = function() {
        return resultModfied.getText();
    }
    this.returnResultParentName = function() {
        return resultParentName.getText();
    }
    this.returnResultEvent = function() {
        return resultEvent.getText();
    }

};

module.exports = new subscriptionPage();
