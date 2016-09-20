var Objects = require('./Objects.json');
var helpers = function helpers() {

    this.loginAsSupervisor = function() {
        browser.driver.findElement(by.id(Objects.loginpage.locators.username))
            .sendKeys(Objects.loginpage.data.supervisoruser.username);
        browser.driver.findElement(by.id(Objects.loginpage.locators.password))
            .sendKeys(Objects.loginpage.data.supervisoruser.password);
        browser.driver.findElement(
            by.id(Objects.loginpage.locators.loginbutton)).click();
        browser.driver.sleep(12000);
    },

    this.logout = function() {
        element(by.css('.fullname')).click();
        var logout = element(by.linkText('Logout'));
        logout.click();
        expect(
            element(by.xpath('.//*[@class="alert alert-success"]'))
            .getText()).toEqual(
            'You have been logged out successfully.');
    }
};

module.exports = new helpers();
