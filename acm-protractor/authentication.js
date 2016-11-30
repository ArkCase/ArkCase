var logger = require('./log');
var Objects = require('./json/Objects.json');
var EC = protractor.ExpectedConditions;
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');

var helpers = function helpers() {

    this.loginAsSupervisor = function () {
        browser.ignoresynchronization = true;
        browser.driver.get('https://core.arkcase.dev.armedia.com/arkcase/login').then(function () {
            browser.driver.findElement(by.id(Objects.loginpage.locators.username))
                .sendKeys(Objects.loginpage.data.supervisoruser.username);
            browser.driver.findElement(by.id(Objects.loginpage.locators.password))
                .sendKeys(Objects.loginpage.data.supervisoruser.password);
            browser.driver.findElement(by.id(Objects.loginpage.locators.loginbutton)).click();
            logger.log('Info', 'User succesfully logged in');
            return require('./Pages/dashboard_page.js')
        });
    },

        this.logout = function () {
            browser.wait(EC.visibilityOf(element(by.css('.fullname'))), 30000).then(function () {
                var logoutLink = element(by.css('.fullname'));
                browser.wait(EC.elementToBeClickable(element(by.css('.fullname'))), 10000).then(function () {
                    browser.executeScript('arguments[0].click()', logoutLink).then(function () {
                        browser.wait(EC.visibilityOf(element(by.linkText("Logout"))), 30000).then(function () {
                            var logout = element(by.linkText("Logout"));
                            browser.executeScript('arguments[0].click()', logout).then(function () {
                                browser.ignoresynchronization = true;
                                        expect(
                                            element(by.css(".alert.alert-success"))
                                                .getText()).toEqual(
                                            'You have been logged out successfully.');
                                        logger.log('Info', 'User succesfully logged out');
                                    })

                        })

                    });
                });
            });

        };
};

module.exports = new helpers();

