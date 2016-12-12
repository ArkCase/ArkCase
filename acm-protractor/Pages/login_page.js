var Objects=require('../json/Objects.json');
var basePage = require('./base_page.js');
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');

var LoginPage = function() {

    this.insertUserName = function(username) {
        browser.driver.findElement(by.id(Objects.loginpage.locators.username)).sendKeys(username);
        return this;
    };
    this.insertPassword = function (password) {
        browser.driver.findElement(by.id(Objects.loginpage.locators.password)).sendKeys(password);
        return this;
    };
    this.clickLogin = function () {
        browser.driver.findElement(by.id(Objects.loginpage.locators.loginbutton)).click();
        return this;
    };
    this.Login = function (username, password) {
        browser.driver.get('https://core.arkcase.dev.armedia.com/arkcase/login');
        browser.ignoresynchronization = true;
        browser.executeScript('window.sessionStorage.clear();');
        browser.executeScript('window.localStorage.clear();');
        this.insertUserName(username);
        this.insertPassword(password);
        this.clickLogin();
    }


};
LoginPage.prototype = basePage;
module.exports = new LoginPage();
