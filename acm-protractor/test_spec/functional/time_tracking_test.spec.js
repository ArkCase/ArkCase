var Objects = require('../../json/Objects.json');
var utils = require('../../util/utils.js');
var loginPage = require('../../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../../Pages/time_tracking_page.js');

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

}

function waitUrl(myUrl) {
    return function() {
        return browser.getCurrentUrl().then(function(url) {
            return myUrl.test(url);
        });
    }
}


describe('Time Tracking page tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });


    it(' should navigate to the timeSheet page and verify the period', function() {

        timeTrackingPage.clickNewButton();
        timeTrackingPage.navigateToTimeTrackingPage();
        timeTrackingPage.switchToIframes();
        expect(timeTrackingPage.returnTimeTrackingDate()).toEqual(utils.returnTimeTrackingWeek());
        timeTrackingPage.switchToDefaultContent();
    });

    it('should navigate to timesheet page and verify if save button can be clicked without selected charge code', function() {

        timeTrackingPage.clickNewButton();
        timeTrackingPage.navigateToTimeTrackingPage();
        timeTrackingPage.switchToIframes();
        timeTrackingPage.selectTimesheetType("Case")
        expect(timeTrackingPage.returnchargeCodeAlertMessage()).toEqual("You can't leave this empty: Charge Code");
        timeTrackingPage.switchToDefaultContent();
    });

    it('should navigate to time tracking page and add sugested tag', function() {

        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.clickLastElementInTreeData();
        timeTrackingPage.clickTagsLinkBtn();
        timeTrackingPage.addSugestedTag(Objects.timetrackingPage.data.tagname);
        expect(timeTrackingPage.returnTagName()).toEqual(Objects.timetrackingPage.data.tagname);
        expect(timeTrackingPage.returntagCratedDate()).toEqual(utils.returnToday("/"));
        expect(timeTrackingPage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel);
        timeTrackingPage.deleteTag();
    });
});
