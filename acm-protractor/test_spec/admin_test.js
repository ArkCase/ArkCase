var logger = require('../log');
var utils = require('../util/utils.js');
var adminPage = require('../Pages/admin_page.js');
var Objects = require('../json/Objects.json');
var loginPage = require('../Pages/login_page.js');
var flag = false;

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 20000);
}


describe('Validate that group in which is logged in user is in authorized group', function() {

    beforeEach(function (done) {

        loginPage.Login(Objects.loginpage.data.adminuser.username, Objects.loginpage.data.adminuser.password);
        testAsync(done);

    });

    afterEach(function () {

        loginPage.Logout();

    });

    it('should validate that logged in user group is in authorized groups in case summary report configuration', function () {
        adminPage.navigateToPage("Admin").clickSubLink("Reports Configuration").ChooseReport("Case Summary Report");
        expect(adminPage.returnAuthorized()).toContain("ACM_INVESTIGATOR_DEV");
    });

    it('should validate that logged in user group is in authorized groups in complaint disposition count report configuration', function () {
        adminPage.navigateToPage("Admin").clickSubLink("Reports Configuration").ChooseReport("ComplaintDispositionCount");
        expect(adminPage.returnAuthorized()).toContain("ACM_INVESTIGATOR_DEV");
    });

    it('should validate that logged in user group is in authorized groups in complaint report configuration', function () {
        adminPage.navigateToPage("Admin").clickSubLink("Reports Configuration").ChooseReport("Complaint Report");
        expect(adminPage.returnAuthorized()).toContain("ACM_INVESTIGATOR_DEV");
    });

});









