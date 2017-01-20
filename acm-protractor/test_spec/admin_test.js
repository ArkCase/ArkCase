var logger = require('../log');
var utils = require('../util/utils.js');
var adminPage = require('../Pages/admin_page.js');
var Objects = require('../json/Objects.json');
var loginPage = require('../Pages/login_page.js');
var Users = require('../json/Users.json');
var flag = false;
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');

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

    using([{reportName: "Case Summary Report"}, {reportName: "ComplaintDispositionCount"}, {reportName: "Complaint Report"}], function(data) {

        it('should validate that logged in user group is in authorized groups in ' +data.reportName+ 'configuration', function () {
            adminPage.navigateToPage("Admin").clickSubLink("Reports Configuration").ChooseListBoxValue(data.reportName);
            var groups = utils.readGroupsFromJson("samuel-acm");
            for (var i in groups) {
                expect(adminPage.returnAuthorized()).toContain(groups[i]);
            }
        });
    });

    it('should validate that LDAP configuration for Armedia is set', function () {
        adminPage.navigateToPage("Admin").clickSubLink("LDAP Configuration");
        expect(adminPage.returnArmediaDirectoryName()).toEqual(Objects.adminPage.data.ArmediaDirectoryName);
        expect(adminPage.returnArmediaLDAPUrl()).toEqual(Objects.adminPage.data.ArmediaLDAPUrl);
    });

});









