var Objects = require('../../json/Objects.json');
var utils = require('../../util/utils.js');
var loginPage = require('../../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var costTrackingPage = require('../../Pages/cost_tracking_page.js');

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

describe('Cost Tracking page tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    it('should navigate to cost tracking page and add/delete sugested tag', function() {

        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.clickLastElementInTreeData();
        costTrackingPage.clickTagsLinkBtn();
        costTrackingPage.addSugestedTag(Objects.timetrackingPage.data.tagname);
        expect(costTrackingPage.returnTagName()).toEqual(Objects.timetrackingPage.data.tagname, "Tag name is not correct");
        expect(costTrackingPage.returntagCratedDate()).toEqual(utils.returnToday("/"), "Tag created date is not correct");
        expect(costTrackingPage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Tag created by is not correct");
        costTrackingPage.deleteTag();
    });

});
