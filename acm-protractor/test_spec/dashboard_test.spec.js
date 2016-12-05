var logger = require('../log');
var dashPage = require('../Pages/dashboard_page.js');
var authentication = require('../authentication.js');
var loginPage = require('../Pages/login_page.js');
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

describe('dashboard page test', function() {


    loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
    logger.log('Info', 'User succesfully logged in as supervisor');

    it('should add/delete widget cases by status', function() {
        dashPage.clickEditButton().clickAddWidgetButton().addWidget("CasesByStatus").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleCasesByStatus);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();
    });

    it('should add/delete widget my cases', function() {

        dashPage.clickEditButton().clickAddWidgetButton().addWidget("MyCases").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleMyCases);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();

    });

    it('should add/delete widget my complaints', function() {
        dashPage.clickEditButton().clickAddWidgetButton().addWidget("MyComplaints").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleMyComplaints);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();
    });


    it('should add/delete widget new complaints', function() {
        dashPage.clickEditButton().clickAddWidgetButton().addWidget("NewComplaints").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleNewComplaints);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();

    });


    it('should add/delete widget team workload', function() {
        dashPage.clickEditButton().clickAddWidgetButton().addWidget("TeamWorkload").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleTeamWorkload);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();

    });

    it('should add/delete widget weather', function() {
        dashPage.clickEditButton().clickAddWidgetButton().addWidget("Weather").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleWeather);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();

    });

    it('should add/delete widget news', function() {

        dashPage.clickEditButton().clickAddWidgetButton().addWidget("News").clickSaveChangesButton();
        expect(dashPage.returnWidgetTitle()).toEqual(Objects.dashboardpage.data.widgetTitleNews);
        dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();
    });

    it('should logout', function() {

        loginPage.Logout();

    });
  });
});
