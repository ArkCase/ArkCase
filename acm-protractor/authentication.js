var logger = require('./log');
var Objects = require('./json/Objects.json');
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');
var EC = protractor.ExpectedConditions;

var helpers = function helpers() {

    this.loginAsSupervisor = function() { 
    	browser.ignoresynchronization = true;
        browser.driver.findElement(by.id(Objects.loginpage.locators.username))
            .sendKeys(Objects.loginpage.data.adminuser.username);
        browser.driver.findElement(by.id(Objects.loginpage.locators.password))
            .sendKeys(Objects.loginpage.data.adminuser.password);
        browser.driver.findElement(
            by.id(Objects.loginpage.locators.loginbutton)).click();        
        browser.driver.sleep(12000);
        logger.log('Info', 'User succesfully logged in');
        return require('./Pages/dashboard_page.js')
    },

    this.logout = function() {
        element(by.css('.fullname')).click();
        var logout = element(by.linkText('Logout'));
        logout.click();  
         browser.wait(EC.visibilityOf(element(by.xpath(".//*[@class='alert alert-success']"))), 5000);      
        expect(
            element(by.xpath('.//*[@class="alert alert-success"]'))
            .getText()).toEqual(
            'You have been logged out successfully.');
        logger.log('Info', 'User succesfully logged out');
    }
};

module.exports = new helpers();
