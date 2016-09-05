var helpers = function helpers() {
    this.loginAsSupervisor = function() {
            browser.driver.findElement(by.id('j_username')).sendKeys('samuel-acm');
            browser.driver.findElement(by.id('j_password')).sendKeys('Armedia#1');
            browser.driver.findElement(by.id('submit')).click();
            browser.driver.sleep(12000);
        },

        this.logout = function() {
            element(by.css('.fullname')).click();
            var logout = element(by.linkText('Logout'));
            logout.click();
            expect(element(by.xpath('.//*[@class="alert alert-success"]')).getText()).toEqual('You have been logged out successfully.');
        }

};

module.exports = new helpers();
