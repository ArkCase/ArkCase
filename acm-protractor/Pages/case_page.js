var Objects = require('../json/Objects.json');

var EC = protractor.ExpectedConditions;
var newBtn = element(by.linkText(Objects.casepage.locators.newButton));
var newCaseBtn = element(by.linkText(Objects.casepage.locators.newCaseBtn));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseArson = element(by.xpath(Objects.casepage.locators.caseArson));
var casesTitle = element(by.xpath(Objects.casepage.locators.casesTitle));
var casesType = element(by.xpath(Objects.casepage.locators.casesType));
var casesPageTitle = element(by.xpath(Objects.casepage.locators.casesPageTitle));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseType = element(by.className(Objects.casepage.locators.caseType));
var nextBtn = element(by.xpath(Objects.casepage.locators.nextBtn));
var firstName = element(by.name(Objects.casepage.locators.firstName));
var lastName = element(by.name(Objects.casepage.locators.lastName));
var submitBtn = element(by.xpath(Objects.casepage.locators.submitBtn));



var CasePage = function() {

    browser.ignoreSynchronization = true;
    this.navigateToNewCasePage = function() {

        newBtn.click().then(function() {
            newCaseBtn.click();
        });
        return this;

    }

    this.switchToIframes = function() {

        browser.wait(EC.visibilityOf(element(by.className("new-iframe ng-scope"))), 20000);
        browser.switchTo().frame(browser.driver.findElement(by.className("new-iframe ng-scope"))).then(function() {
            browser.switchTo().frame(browser.driver.findElement(By.className("frevvo-form")));
        });
        return this;
    }

    this.insertCaseTitle = function(title) {

        caseTitle.click().then(function() {
            caseTitle.sendKeys(title);
        });

        return this;
    }

    this.selectCaseType = function() {


        caseType.click().then(function() {

            caseArson.click();
        });

        return this;
    }

    this.clickNextBtn = function() {

        nextBtn.click();
        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.firstName))), 5000);
        return this;
    }

    this.insertFirstName = function(firstname) {


        firstName.click().then(function() {
            firstName.sendKeys(firstname);
        });
        return this;
    }
    this.insertLastName = function(lastname) {

        lastName.click().then(function() {
            lastName.sendKeys(lastname);
        });
        return this;
    }

    this.clickSubmitBtn = function() {

        submitBtn.click();

        return this;
    }

    this.switchToDefaultContent = function() {

        browser.driver.switchTo().defaultContent();
    }

    this.waitForCaseType = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesType))), 30000);
    }

    this.waitForCaseTitle = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000);
    }


    this.returnCasesPageTitle = function() {

        return casesPageTitle.getText();
    }

    this.returnCaseTitle = function() {

        return casesTitle.getText();
    }

    this.returnCaseType = function() {


        return casesType.getText();
    }
};

module.exports = new CasePage();
