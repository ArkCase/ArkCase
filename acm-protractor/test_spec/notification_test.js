var logger = require('../log');
var notificationPage = require('../Pages/notifications_page.js');
var loginPage = require('../Pages/login_page.js');
var Objects = require('../json/Objects.json');
var flag = false;

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 60000);

}


describe('notification page test', function() {


    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    it('should verify that description date/time is equal to modified column date/time', function() {
        notificationPage.navigateToPage("Notifications");
        expect(notificationPage.returnDescription()).toContain(notificationPage.returnModifiedBy());
    });


});
