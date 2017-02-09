var logger = require('../../log');
var notificationPage = require('../../Pages/notifications_page.js');
var loginPage = require('../../Pages/login_page.js');
var Objects = require('../../json/Objects.json');
var utils = require('../../util/utils.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
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

    it('should select previous month and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Month")
        notificationPage.clickSortModifiedBy();
        notificationPage.returnModifiedByMonth();

    });

    it('should select previous Year and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Year")
        notificationPage.clickSortModifiedBy();
        notificationPage.returnModifiedByYear();
    });

    it('should select previous week and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Week")
        notificationPage.clickSortModifiedBy();
        notificationPage.returnModifiedByWeek();
    });

    using([{ parentType: "File", expected: "FILE" }, { parentType: "Note", expected: "NOTE" },
        { parentType: "Task", expected: "TASK" }, { parentType: "Complaint", expected: "COMPLAINT" },
        { parentType: "Case File", expected: "CASE_FILE" }, { parentType: "Participant", expected: "PARTICIPANT" }
    ], function(data) {

        it('should select parent type and verify the object type in the result table ', function() {

            notificationPage.navigateToPage("Notifications");
            notificationPage.selectNotification(data.parentType)
            notificationPage.clicksortObjectType();
            expect(notificationPage.returnObjectType()).toEqual(data.expected, "Object type is not correct in result table");
        });

    });

    //Click on the Notification Module and verify that description date/time is equal to modified column date/time

    it('should verify that description date/time is equal to modified column date/time', function() {
        notificationPage.navigateToPage("Notifications");
        expect(notificationPage.returnDescription()).toContain(notificationPage.returnModifiedBy(), "In description of notification modified date is not correct");
    });
});
