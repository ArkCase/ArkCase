var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');
var fullnameLink = element(by.css(Objects.basepage.locators.fullnameLink));
var logoutLink = element(by.linkText(Objects.basepage.locators.logoutLink));
var logoutSucesfullMessage = element(by.css(Objects.basepage.locators.logoutSucesfullMessage));
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
        browser.wait(EC.presenceOf(element(by.css('.fullname'))), 30000, "Full name link is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.css('.fullname'))), 30000, "Full name link is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.css('.fullname'))), 30000, "Full name link is not clickable").then(function() {
                    browser.executeScript('arguments[0].click()', fullnameLink);
                });
            });
        });
        return this;
    };

    this.clickLogout = function() {
        browser.wait(EC.visibilityOf(element(by.linkText("Logout"))), 30000, "Logout link is not visible").then(function() {
            logoutLink.click().then(function() {
                browser.ignoresynchronization = true;
                browser.wait(EC.visibilityOf(element(by.id(Objects.loginpage.locators.username))), 20000, "Username field in the login page is not displayed after logout is clicked");
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
