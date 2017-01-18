var Objects = require('../json/Objects.json');
var EC = protractor.ExpectedConditions;
var subscriptionModule = element(by.css(Objects.subscriptionsPage.locators.subscriptionsModule));
var subscriptionsTitle = element(by.xpath(Objects.subscriptionsPage.locators.subscriptionsTitle));
var searchSubscriptionInput = element(by.model(Objects.subscriptionsPage.locators.subscriptionsInput));
var searchBtn = element(by.css(Objects.subscriptionsPage.locators.searchBtn));
var resultEvent = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(0);
var resultParentName = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(1);
var resultParentType = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(2);
var resultModfied = element.all(by.repeater(Objects.subscriptionsPage.locators.resultTable)).get(3);


var subscriptionPage = function() {

    this.clickSubcriptionsModule = function() {

        browser.executeScript('arguments[0].click()', subscriptionModule);
        browser.sleep(10000);
    }


    this.selectSubscription = function(subscription) {

        browser.wait(EC.visibilityOf(element(by.cssContainingText(Objects.subscriptionsPage.locators.facets, subscription))), 20000, "Checkbox for" + " " + subscription + "Is not displayed").then(function() {
            element(by.cssContainingText(Objects.subscriptionsPage.locators.facets, subscription)).click().then(function() {
                browser.sleep(5000);
            });
        });
    }

    this.searchForSubscription = function(data) {

        browser.wait(EC.visibilityOf(element(by.model(Objects.subscriptionsPage.locators.subscriptionsInput))), 20000, "Search input for subscription is not displayed").then(function() {
            searchSubscriptionInput.sendKeys(data).then(function() {
                searchBtn.click().then(function() {
                    browser.sleep(5000);
                });
            });
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
