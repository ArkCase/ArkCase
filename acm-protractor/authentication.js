var logger = require('./log');
var Objects = require('./json/Objects.json');
var EC = protractor.ExpectedConditions;
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');
var EC = protractor.ExpectedConditions;

var helpers = function helpers() {

    this.loginAsSupervisor = function() {
            browser.ignoresynchronization = true;
            browser.driver.get('https://cloud.arkcase.com/arkcase/login').then(function() {
                browser.executeScript('window.sessionStorage.clear();');
                browser.executeScript('window.localStorage.clear();');
                browser.driver.manage().deleteAllCookies();
                browser.driver.findElement(by.id(Objects.loginpage.locators.username))
                    .sendKeys(Objects.loginpage.data.supervisoruser.username);
                browser.driver.findElement(by.id(Objects.loginpage.locators.password))
                    .sendKeys(Objects.loginpage.data.supervisoruser.password);
                browser.driver.findElement(
                    by.id(Objects.loginpage.locators.loginbutton)).click();
                logger.log('Info', 'User succesfully logged in');
                return require('./Pages/dashboard_page.js')
            });
        },

        this.logout = function() {
            var logoutLink = element(by.css('.fullname'));
            var logout = element(by.linkText('Logout'));
            browser.wait(EC.elementToBeClickable(element(by.css('.fullname'))), 10000).then(function() {
                browser.executeScript('arguments[0].click()', logoutLink).then(function() {
                    browser.executeScript('arguments[0].click()', logout).then(function() {
                        browser.ignoresynchronization = true;
                        browser.wait(EC.visibilityOf(element(by.id(Objects.loginpage.locators.username))), 20000, "Logout was unseccsfull");
                        logger.log('Info', 'User succesfully logged out');
                    });
                });
            });
        }
};

module.exports = new helpers();
