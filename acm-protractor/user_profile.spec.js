var userPage = require('./Pages/user_profile_page.js');
var authentication = require('./authentication.js');
var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
var utils = require('./utils.js');
var Objects = require('./Objects.json');
var flag = false;


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
        expect(userPage.userNavigationProfile.getText()).toEqual(Objects.userpage.data.userNavigationProfile);
        userPage.userNavigationProfile.click().then(function() {
            expect(userPage.userPageHeader.getText()).toEqual(Objects.userpage.data.userPageHeader);
        });
    });

    it('should edit username', function() {

        userPage.userName.click();
        userPage.userNameInput.clear();
        userPage.userNameInput.sendKeys(Objects.userpage.data.userNameInput);
        userPage.userNameConfirmBtn.click().then(function() {
            expect(userPage.userName.getText()).toEqual(Objects.userpage.data.userNameInput);
        });
    });

    it('should edit location in  contact information', function() {

        userPage.userLocation.click();
        userPage.userLocationInput.clear();
        userPage.userLocationInput.sendKeys(Objects.userpage.data.userLocationInput);
        userPage.userLocationConfirmBtn.click().then(function() {
            expect(userPage.userLocation.getText()).toEqual(Objects.userpage.data.userLocationInput);
        });
    });
    it('should edit office phone in contact information', function() {

        userPage.officePhone.click();
        userPage.officePhoneInput.clear();
        userPage.officePhoneInput.sendKeys(Objects.userpage.data.officePhoneInput);
        userPage.officePhoneConfirmBtn.click().then(function() {
            expect(userPage.officePhone.getText()).toEqual(Objects.userpage.data.officePhoneInput);
        });
    });

    it('should edit im account', function() {

        userPage.imAccount.click();
        userPage.imAccountInput.clear();
        userPage.imAccountInput.sendKeys(Objects.userpage.data.imAccountInput);
        userPage.imAccountConfirmBtn.click().then(function() {
            expect(userPage.imAccount.getText()).toEqual(Objects.userpage.data.imAccountInput);
        });
    });

    it('should edit short im account', function() {

        userPage.shortImaccount.click();
        userPage.shortImaccountInput.clear();
        userPage.shortImaccountInput.sendKeys(Objects.userpage.data.shortImaccountInput);
        userPage.shortImaccountConfirmBtn.click().then(function() {
            expect(userPage.shortImaccount.getText()).toEqual(Objects.userpage.data.shortImaccountInput);
        });
    });

    it('should edit mobile phone', function() {

        userPage.mobilephone.click();
        userPage.mobilephoneInput.clear();
        userPage.mobilephoneInput.sendKeys(Objects.userpage.data.mobilephoneInput);
        userPage.mobilephoneConfirmBtn.click().then(function() {
            expect(userPage.mobilephone.getText()).toEqual(Objects.userpage.data.mobilephoneInput);

        });
    });

    it('should edit company name', function() {

        userPage.companyName.click();
        userPage.companyNameInput.clear();
        userPage.companyNameInput.sendKeys(Objects.userpage.data.companyNameInput);
        userPage.companyNameConfirmBtn.click().then(function() {
            expect(userPage.companyName.getText()).toEqual(Objects.userpage.data.companyNameInput);
        });
    });

    it('should edit address 1', function() {

        userPage.addressOne.click();
        userPage.addressOneInput.clear();
        userPage.addressOneInput.sendKeys(Objects.userpage.data.addressOneInput);
        userPage.addressOneConfirmBtn.click();
        expect(userPage.addressOne.getText()).toEqual(Objects.userpage.data.addressOneInput);

    });

    it('should edit address 2', function() {

        userPage.addressTwo.click();
        userPage.addressTwoInput.clear();
        userPage.addressTwoInput.sendKeys(Objects.userpage.data.addressTwoInput);
        userPage.addressTwoConfirmBtn.click().then(function() {
            expect(userPage.addressTwo.getText()).toEqual(Objects.userpage.data.addressTwoInput);
        });
    });

    it('should edit city', function() {

        userPage.city.click();
        userPage.cityInput.clear();
        userPage.cityInput.sendKeys(Objects.userpage.data.cityInput);
        userPage.cityConfirmBtn.click().then(function() {
            expect(userPage.city.getText()).toEqual(Objects.userpage.data.cityInput);
        });
    });

    it('should edit state', function() {

        userPage.state.click();
        userPage.stateInput.clear();
        userPage.stateInput.sendKeys(Objects.userpage.data.stateInput);
        userPage.stateConfirmBtn.click().then(function() {
            expect(userPage.state.getText()).toEqual(Objects.userpage.data.stateInput);
        });
    });

    it('should edit zip', function() {

        userPage.zip.click();
        userPage.zipInput.clear();
        userPage.zipInput.sendKeys(Objects.userpage.data.zipInput);
        userPage.zipConfirmBtn.click().then(function() {
            expect(userPage.zip.getText()).toEqual(Objects.userpage.data.zipInput);
        });
    });

    it('should edit main office phone', function() {

        userPage.mainOfficePhone.click();
        userPage.mainOfficePhoneInput.clear();
        userPage.mainOfficePhoneInput.sendKeys(Objects.userpage.data.mainOfficePhoneInput);
        userPage.mainOfficePhoneConfirmBtn.click().then(function() {
            expect(userPage.mainOfficePhone.getText()).toEqual(Objects.userpage.data.mainOfficePhoneInput);
        });
    });

    it('should edit fax', function() {

        userPage.fax.click();
        userPage.faxInput.clear();
        userPage.faxInput.sendKeys(Objects.userpage.data.faxInput);
        userPage.faxConfirmBtn.click().then(function() {
            expect(userPage.fax.getText()).toEqual(Objects.userpage.data.faxInput);
        });
    });

    it('should edit website', function() {

        userPage.website.click();
        userPage.websiteInput.clear();
        userPage.websiteInput.sendKeys(Objects.userpage.data.websiteInput);
        userPage.websiteConfirmBtn.click().then(function() {
            expect(userPage.website.getText()).toEqual(Objects.userpage.data.websiteInput);
        });
    });


    it('should change profile picture', function() {

        userPage.changeProfilePic.click().then(function() {
            utils.uploadPng();

        });
    });

    it('should logout', function() {

        authentication.logout();

    });

});
