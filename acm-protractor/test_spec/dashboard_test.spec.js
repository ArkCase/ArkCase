var logger = require('../log');
var dashPage = require('../Pages/dashboard_page.js');
var authentication = require('../authentication.js');
var Objects = require('../json/Objects.json');
var flag = false;


function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 20000);
}

describe('dashboard page test', function() {

    beforeEach(function(done) {

        authentication.loginAsSupervisor();
        testAsync(done);

    });

    afterEach(function() {

        authentication.logout();

    });

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

});
