var logger = require('./log');
var Objects = require('./json/Objects.json');
var logs = require(process.env['USERPROFILE'] + '/node_modules/winston');
var EC = protractor.ExpectedConditions;

var helpers = function helpers() {

    this.loginAsSupervisor = function() { 
        browser.ignoresynchronization = true;
        browser.driver.get('https://cloud.arkcase.com/arkcase/login').then(function(){
        browser.driver.findElement(by.id(Objects.loginpage.locators.username))
            .sendKeys(Objects.loginpage.data.supervisoruser.username);
        browser.driver.findElement(by.id(Objects.loginpage.locators.password))
            .sendKeys(Objects.loginpage.data.supervisoruser.password);
        browser.driver.findElement(
            by.id(Objects.loginpage.locators.loginbutton)).click();        
        browser.wait(EC.visibilityOf(element(by.xpath(".//*[@class='well-sm clearfix ng-scope']/span/a/i"))),20000);
      browser.driver.switchTo().defaultContent();
        logger.log('Info', 'User succesfully logged in');
        return require('./Pages/dashboard_page.js')
    });
    },

    this.logout = function() {
        var logoutLink=element(by.css('.fullname'));
        var logout = element(by.linkText('Logout'));
        browser.wait(EC.elementToBeClickable(element(by.css('.fullname'))),10000).then(function(){
         browser.executeScript('arguments[0].click()', logoutLink).then(function(){
                 browser.executeScript('arguments[0].click()', logout).then(function(){
        browser.wait(EC.visibilityOf(element(by.xpath(".//*[@class='alert alert-success']"))), 20000);      
        expect(
            element(by.xpath('.//*[@class="alert alert-success"]'))
            .getText()).toEqual(
            'You have been logged out successfully.');
        logger.log('Info', 'User succesfully logged out');
    });
     });
          });
    }

};

module.exports = new helpers();

