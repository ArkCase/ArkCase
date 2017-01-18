var logger = require('../log');
var notificationPage = require('../Pages/notifications_page.js');
var loginPage = require('../Pages/login_page.js');
var Objects = require('../json/Objects.json');
var utils = require('../util/utils.js');
var flag = false;

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

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

    it('should select parent type participant and verify the object type in the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Participant")
        expect(notificationPage.returnObjectType()).toEqual("PARTICIPANT");
    });


    it('should select parent type task and verify it the object type in result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Task")
        expect(notificationPage.returnObjectType()).toEqual("TASK");
    });

    it('should select parent type task and verify it the object type in result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Task")
        expect(notificationPage.returnObjectType()).toEqual("TASK");
    });

    it('should select previous month and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Month")
        expect(notificationPage.returnModifiedBy()).toContain(utils.returnToday('/'))
    });

    it('should select previous week and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Week")
        expect(notificationPage.returnModifiedBy()).toContain(utils.returnToday('/'))
    });

});
