var logger = require('../log');
var userPage = require('../Pages/user_profile_page.js');
var loginPage = require('../Pages/login_page.js');
var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
var utils = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var flag = false;


function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 20000);
}
//Specs
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

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        logger.log('Info', 'User succesfully logged in as supervisor');

       it('should navigate to user profile page', function() {

        userPage.clickUserNavigation();
        expect(userPage.returnUserNavigationProfile()).toEqual(Objects.userpage.data.userNavigationProfile);
        userPage.clickUserNavigationProfile();
        expect(userPage.returnUserPageHeader()).toEqual(Objects.userpage.data.userPageHeader);
    });

    it('should edit username', function() {

        userPage.editUsername(Objects.userpage.data.userNameInput);
        expect(userPage.returnUsername()).toEqual(Objects.userpage.data.userNameInput);

    });

    it('should edit location in  contact information', function() {

        userPage.editLocation(Objects.userpage.data.userLocationInput);
        expect(userPage.returnUserLocation()).toEqual(Objects.userpage.data.userLocationInput);
    });
    it('should edit office phone in contact information', function() {

        userPage.editOfficePhone(Objects.userpage.data.officePhoneInput);
        expect(userPage.returnOfficePhone()).toEqual(Objects.userpage.data.officePhoneInput);
    });

    it('should edit im account', function() {

        userPage.editImAccount(Objects.userpage.data.imAccountInput);
        expect(userPage.returnImAccount()).toEqual(Objects.userpage.data.imAccountInput);

    });

    it('should edit short im account', function() {

        userPage.editShortImAccount(Objects.userpage.data.shortImaccountInput);
        expect(userPage.returnShortImAccount()).toEqual(Objects.userpage.data.shortImaccountInput);

    });

    it('should edit mobile phone', function() {

        userPage.editMobilePhone(Objects.userpage.data.mobilephoneInput);
        expect(userPage.returnMobilePhone()).toEqual(Objects.userpage.data.mobilephoneInput);

    });

    it('should edit company name', function() {

        userPage.editCompanyName(Objects.userpage.data.companyNameInput);
        expect(userPage.returnCompanyName()).toEqual(Objects.userpage.data.companyNameInput);

    });

    it('should edit address 1', function() {

        userPage.editAddressOne(Objects.userpage.data.addressOneInput);
        expect(userPage.returnAddressOne()).toEqual(Objects.userpage.data.addressOneInput);

    });

    it('should edit address 2', function() {

        userPage.editAddressTwo(Objects.userpage.data.addressTwoInput);
        expect(userPage.returnAddressTwo()).toEqual(Objects.userpage.data.addressTwoInput);

    });

    it('should edit city', function() {

        userPage.editCity(Objects.userpage.data.cityInput);
        expect(userPage.returnCity()).toEqual(Objects.userpage.data.cityInput);

    });

    it('should edit state', function() {

        userPage.editState(Objects.userpage.data.stateInput);
        expect(userPage.returnState()).toEqual(Objects.userpage.data.stateInput);

    });

    it('should edit zip', function() {

        userPage.editZip(Objects.userpage.data.zipInput);
        expect(userPage.returnZip()).toEqual(Objects.userpage.data.zipInput);

    });

    it('should edit main office phone', function() {

        userPage.editMainOfficePhone(Objects.userpage.data.mainOfficePhoneInput);
        expect(userPage.returnMainOfficePhone()).toEqual(Objects.userpage.data.mainOfficePhoneInput);

    });

    it('should edit fax', function() {

        userPage.editFax(Objects.userpage.data.faxInput);
        expect(userPage.returnFax()).toEqual(Objects.userpage.data.faxInput);

    });

    it('should edit website', function() {

        userPage.editWebSite(Objects.userpage.data.websiteInput);
        expect(userPage.returnWebSite()).toEqual(Objects.userpage.data.websiteInput);

    });


    it('should change profile picture', function() {

        userPage.changePicture();

    });

    it('should logout', function() {

        loginPage.Logout();

    });

});
