var Objects = require('../json/Objects.json');
var utils = require('../util/utils.js');
var EC = protractor.ExpectedConditions;
var util = require('../util/utils.js');
//var typeDropDown = element(by.name(Objects.costsheetPage.locators.typeDropDown));
var newCostSheetLinkBtn = element(by.linkText(Objects.costsheetPage.locators.costsheetLink));
//var typeDropDown = element(by.name(Objects.costsheetPage.locators.typeDropDown));
//var codeDropDown = element(by.name(Objects.costsheetPage.locators.codeDropDown));
var dateInput = element(by.name(Objects.costsheetPage.locators.dateInput));
//var titleDropDown = element(by.name(Objects.costsheetPage.locators.titleInput));
var descriptionInput = element(by.xpath(Objects.costsheetPage.locators.descriptionInput));
var amountInput = element(by.name(Objects.costsheetPage.locators.amountInput));
var saveBtn = element(by.buttonText(Objects.costsheetPage.locators.saveBtn));
var typeDropDown = element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(1);
//var typeDropDown = element(by.name(Objects.costsheetPage.locators.typeDropDown));
var codeDropDown = element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(2);
var titleDropDown = element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(4);
var costsheetPageTitle=element(by.xpath(Objects.costsheetPage.locators.costsheetPageTitle));



var costTrackingPage = function() {

    this.navigateToExpensesPage = function() {
        newCostSheetLinkBtn.click().then(function() {
            browser.ignoreSynchronization = true;
            return this;
        });
    }

    this.submitExpenses = function(type, code, title, description, amount) {

        var costType = element(by.linkText(type));
        var codeType = element(by.linkText(code));
        var titleType = element(by.linkText(title));
        browser.wait(EC.visibilityOf(element.all(by.xpath(Objects.costsheetPage.locators.expensesDropDown)).get(1)), 30000, "Expenses type is not displayed").then(function() {
            typeDropDown.click().then(function() {
                browser.wait(EC.textToBePresentInElement((costType), type), 10000).then(function() {
                    costType.click().then(function() {
                        codeDropDown.click().then(function() {
                            browser.wait(EC.textToBePresentInElement((codeType), code), 10000).then(function() {
                                codeType.click().then(function() {
                                    dateInput.sendKeys(utils.returnToday("/")).then(function() {
                                        titleDropDown.click().then(function() {
                                            browser.wait(EC.textToBePresentInElement((titleType), title), 10000).then(function() {
                                                titleType.click().then(function() {
                                                    amountInput.sendKeys(amount);
                                                    browser.sleep(5000);
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
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.costsheetPage.locators.costsheetPageTitle))), 30000, "Costsheet page title is not displayed");
        browser.sleep(10000);
        return this;
    }

};
module.exports = new costTrackingPage();
