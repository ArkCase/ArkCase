var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');
var fullnameLink = element(by.css(Objects.basepage.locators.fullnameLink));
var logoutLink = element(by.linkText(Objects.basepage.locators.logoutLink));
//var logoutSucesfullMessage = element(by.css(Objects.basepage.locators.logoutSucesfullMessage));
var EC = protractor.ExpectedConditions;

var LoginPage = function() {

    this.insertUserName = function(username) {
        browser.driver.findElement(by.id(Objects.loginpage.locators.username)).sendKeys(username);
        return this;
    };
    this.insertPassword = function(password) {
        browser.driver.findElement(by.id(Objects.loginpage.locators.password)).sendKeys(password);
        return this;
    };
    this.clickLogin = function() {
        browser.driver.findElement(by.id(Objects.loginpage.locators.loginbutton)).click();
        return this;
    };
    this.Login = function(username, password) {
        browser.ignoresynchronization = true;
        browser.executeScript('window.sessionStorage.clear();');
        browser.executeScript('window.localStorage.clear();');
        this.insertUserName(username);
        this.insertPassword(password);
        this.clickLogin();
    };
    this.clickFullNameLink = function() {
        browser.wait(EC.visibilityOf(element(by.css('.fullname'))), 30000).then(function() {
            browser.wait(EC.elementToBeClickable(element(by.css('.fullname'))), 30000).then(function() {
                browser.executeScript('arguments[0].click()', fullnameLink);
            });
        });
        return this;
    };

    this.clickLogout = function() {
        browser.wait(EC.visibilityOf(element(by.linkText("Logout"))), 30000).then(function() {
            logoutLink.click().then(function() {
                browser.ignoresynchronization = true;
                browser.sleep(10000);
                var logoutSucesfullMessage = browser.driver.findElement(by.css(Objects.basepage.locators.logoutSucesfullMessage));
                expect(logoutSucesfullMessage.getText()).toEqual('You have been logged out successfully.', 'Logout was unsuccessfull');
            });
        });
        return this;
    };
    this.Logout = function() {
        this.clickFullNameLink();
        this.clickLogout();
        return this;
    };
};
LoginPage.prototype = basePage;
module.exports = new LoginPage();
