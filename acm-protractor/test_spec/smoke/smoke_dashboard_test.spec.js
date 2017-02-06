var logger = require('../../log');
var dashPage = require('../../Pages/dashboard_page.js');
var loginPage = require('../../Pages/login_page.js');
var Objects = require('../../json/Objects.json');
var flag = false;
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');

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

        beforeEach(function (done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function () {
            loginPage.Logout();

        });


    //Change Dashboard configuration

    using([{ widgetName: "News", widgetTitle: Objects.dashboardpage.data.widgetTitleNews },
        { widgetName: "MyTasks", widgetTitle: Objects.dashboardpage.data.widgetTitleMyTasks },
        { widgetName: "MyCases", widgetTitle: Objects.dashboardpage.data.widgetTitleMyCases },
        { widgetName: "MyComplaints", widgetTitle: Objects.dashboardpage.data.widgetTitleMyComplaints },
        { widgetName: "NewComplaints", widgetTitle: Objects.dashboardpage.data.widgetTitleNewComplaints },
        { widgetName: "TeamWorkload", widgetTitle: Objects.dashboardpage.data.widgetTitleTeamWorkload },
        { widgetName: "CasesByStatus", widgetTitle: Objects.dashboardpage.data.widgetTitleCasesByStatus },
        { widgetName: "Weather", widgetTitle: Objects.dashboardpage.data.widgetTitleWeather }
    ], function(data) {
        it('should add/delete ' + data.widgetName, function() {

            dashPage.clickEditButton().clickAddWidgetButton().addWidget(data.widgetName).clickSaveChangesButton();
            expect(dashPage.returnWidgetTitle()).toEqual(data.widgetTitle, "Widget title is not correct in widget " + data.widgetName);
            dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();

        });
    });
    it('should edit dashboard title', function() {

        dashPage.editDashboardTitle(Objects.dashboardpage.data.DashbordTitle);
        expect(dashPage.returnDashboardTitle()).toEqual(Objects.dashboardpage.data.DashbordTitle, "Dashboard title is not updated");
    });

});
