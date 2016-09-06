var userPage = require('./Pages/user_profile_page.js');
var authentication = require('./authentication.js');
var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
var flag = false;
var home = process.env['USERPROFILE'];
var uplaodPath = home + '\\.arkcase\\seleniumTests\\filesForUpload\\imageprofile.png';

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 20000);
}

// Specs
describe("Testing async calls with beforeEach and passing the special done callback around", function() {

    beforeEach(function(done) {
        // Make an async call, passing the special done callback        
        testAsync(done);
    });

    it("Should be true if the async call has completed", function() {
        expect(flag).toEqual(true);
    });

});

describe('edit user profile page', function() {

    authentication.loginAsSupervisor();


    it('should navigate to user profile page', function() {

        userPage.userNavigation.click();
        expect(userPage.userNavigationProfile.getText()).toEqual('Profile');
        userPage.userNavigationProfile.click();
        expect(userPage.userPageHeader.getText()).toEqual('User Profile');

    });

    it('should edit username', function() {

        userPage.userName.click();
        userPage.userNameInput.clear();
        userPage.userNameInput.sendKeys('Administrator');
        userPage.userNameConfirmBtn.click();
        expect(userPage.userName.getText()).toEqual('Administrator');

    });

    it('should edit location in  contact information', function() {

        userPage.userLocation.click();
        userPage.userLocationInput.clear();
        userPage.userLocationInput.sendKeys('New York');
        userPage.userLocationConfirmBtn.click();
        expect(userPage.userLocation.getText()).toEqual('New York');
    });

    it('should edit office phone in contact information', function() {

        userPage.officePhone.click();
        userPage.officePhoneInput.clear();
        userPage.officePhoneInput.sendKeys('000-111');
        userPage.officePhoneConfirmBtn.click();
        expect(userPage.officePhone.getText()).toEqual('000-111');

    });

    it('should edit im account', function() {

        userPage.imAccount.click();
        userPage.imAccountInput.clear();
        userPage.imAccountInput.sendKeys('test');
        userPage.imAccountConfirmBtn.click();
        expect(userPage.imAccount.getText()).toEqual('test');

    });

    it('should edit short im account', function() {

        userPage.shortImaccount.click();
        userPage.shortImaccountInput.clear();
        userPage.shortImaccountInput.sendKeys('test');
        userPage.shortImaccountConfirmBtn.click();
        expect(userPage.shortImaccount.getText()).toEqual('test');

    });

    it('should edit mobile phone', function() {

        userPage.mobilephone.click();
        userPage.mobilephoneInput.clear();
        userPage.mobilephoneInput.sendKeys('000-000');
        userPage.mobilephoneConfirmBtn.click();
        expect(userPage.mobilephone.getText()).toEqual('000-000');

    });

    it('should edit company name', function() {

        userPage.companyName.click();
        userPage.companyNameInput.clear();
        userPage.companyNameInput.sendKeys('Arkcase');
        userPage.companyNameConfirmBtn.click();
        expect(userPage.companyName.getText()).toEqual('Arkcase');

    });

    it('should edit address 1', function() {

        userPage.addressOne.click();
        userPage.addressOneInput.clear();
        userPage.addressOneInput.sendKeys('address 1');
        userPage.addressOneConfirmBtn.click();
        expect(userPage.addressOne.getText()).toEqual('address 1');

    });

    it('should edit address 2', function() {

        userPage.addressTwo.click();
        userPage.addressTwoInput.clear();
        userPage.addressTwoInput.sendKeys('address 2');
        userPage.addressTwoConfirmBtn.click();

    });

    it('should edit city', function() {

        userPage.city.click();
        userPage.cityInput.clear();
        userPage.cityInput.sendKeys('Washington');
        userPage.cityConfirmBtn.click();
        expect(userPage.city.getText()).toEqual('Washington');

    });

    it('should edit state', function() {

        userPage.state.click();
        userPage.stateInput.clear();
        userPage.stateInput.sendKeys('USA');
        userPage.stateConfirmBtn.click();
        expect(userPage.state.getText()).toEqual('USA');

    });

    it('should edit zip', function() {

        userPage.zip.click();
        userPage.zipInput.clear();
        userPage.zipInput.sendKeys('1010');
        userPage.zipConfirmBtn.click();
        expect(userPage.zip.getText()).toEqual('1010');

    });

    it('should edit main office phone', function() {

        userPage.mainOfficePhone.click();
        userPage.mainOfficePhoneInput.clear();
        userPage.mainOfficePhoneInput.sendKeys('222-333');
        userPage.mainOfficePhoneConfirmBtn.click();
        expect(userPage.mainOfficePhone.getText()).toEqual('222-333');

    });

    it('should edit fax', function() {

        userPage.fax.click();
        userPage.faxInput.clear();
        userPage.faxInput.sendKeys('2222222');
        userPage.faxConfirmBtn.click();
        expect(userPage.fax.getText()).toEqual('2222222');

    });

    it('should edit website', function() {

        userPage.website.click();
        userPage.websiteInput.clear();
        userPage.websiteInput.sendKeys('www.arkcase.com');
        userPage.websiteConfirmBtn.click();
        expect(userPage.website.getText()).toEqual('www.arkcase.com');

    });


    it('should change profile picture', function() {

        userPage.changeProfilePic.click().then(function() {
            browser.driver.sleep(3000);
            robot.setKeyboardDelay(14000);
            robot.typeStringDelayed("/", 500);
            robot.typeStringDelayed(uplaodPath, 14000);
            robot.keyTap("enter");
            browser.driver.sleep(2000);

        });
    });

    it('should logout', function() {

        element(by.css('.fullname')).click();
        var logout = element(by.linkText('Logout'));
        logout.click();
        expect(element(by.xpath('/html/body/div/div[2]')).getText()).toEqual('You have been logged out successfully.');

    });

});
