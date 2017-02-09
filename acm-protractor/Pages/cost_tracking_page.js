var Objects = require('../json/Objects.json');
var utils = require('../util/utils.js');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var util = require('../util/utils.js');
var newCostSheetLinkBtn = element(by.linkText(Objects.costsheetPage.locators.costsheetLink));
var dateInput = element(by.name(Objects.costsheetPage.locators.dateInput));
var descriptionInput = element(by.xpath(Objects.costsheetPage.locators.descriptionInput));
var amountInput = element(by.name(Objects.costsheetPage.locators.amountInput));
var saveBtn = element(by.buttonText(Objects.costsheetPage.locators.saveBtn));
var typeDropDown = element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(1);
var codeInput = element(by.name(Objects.costsheetPage.locators.codeInput));
var titleDropDown = element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(3);
var costsheetPageTitle = element(by.xpath(Objects.costsheetPage.locators.costsheetPageTitle));



var costTrackingPage = function() {

    this.navigateToExpensesPage = function() {
        newCostSheetLinkBtn.click().then(function() {
            browser.ignoreSynchronization = true;
            return this;
        });
    }

    this.populateExpensesTable = function(title, amount) {

        var titleType = element(by.linkText(title));
        dateInput.sendKeys(utils.returnToday("/")).then(function() {
            titleDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((titleType), title), 10000, title + " is not present in title drop down list").then(function() {
                    titleType.click().then(function() {
                        amountInput.sendKeys(amount);
                    });
                });
            });
        });
        return this;

    }

    this.clickCodeType = function() {

        browser.wait(EC.elementToBeClickable(element(by.name(Objects.costsheetPage.locators.codeInput))), 30000, "Code input is not clickable").then(function() {
            codeInput.click();
        });
        return this;
    }

    this.selectType = function(type) {

        var costType = element(by.linkText(type));
        browser.wait(EC.visibilityOf(element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(1)), 30000, "Expenses type is not displayed").then(function() {
            typeDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((costType), type), 10000, type + " is not present in type drop down list").then(function() {
                    costType.click();
                });
            });
        });
        return this;

    }


    this.clickSaveBtn = function() {
        browser.executeScript('arguments[0].click()', saveBtn);
        browser.driver.switchTo().defaultContent();
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.costsheetPage.locators.costsheetPageTitle))), 30000, "Costsheet page title is not displayed");
        browser.sleep(10000);
        return this;
    }
};

costTrackingPage.prototype = basePage;
module.exports = new costTrackingPage();
