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

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();

    });

    it('should edit dashboard title', function() {

        dashPage.editDashboardTitle(Objects.dashboardpage.data.DashbordTitle);
        expect(dashPage.returnDashboardTitle()).toEqual(Objects.dashboardpage.data.DashbordTitle, "Dashboard title is not updated");
    });

    it('should add my tasks widget and select items per page elements', function() {

        dashPage.clickEditButton().clickAddWidgetButton().addWidget("MyTasks").clickSaveChangesButton();
        using([{ items: "10", }, { items: "25" }], function(data) {
            dashPage.selectPageSizeOnWidget(data.items);
            expect(dashPage.returnItemsPerPage()).toContain(data.items, "Items per page is incorect");

        });
    });


});

