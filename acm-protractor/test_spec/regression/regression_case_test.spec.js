var logger = require('../../log');
var casePage = require('../../Pages/case_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var Objects = require('../../json/Objects.json');
var taskPage = require('../../Pages/task_page.js');
var utils = require('../../util/utils.js');
var userPage = require('../../Pages/user_profile_page.js');
var loginPage = require('../../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var preferencesPage = require('../../Pages/preference_page.js');

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


describe('case page tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    it('should create new case and try to add owner and no access from participant tab for same user and verify the alert message', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab().selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes().clickAddParticipantTypeSecondRowbtn();
        casePage.selectParticipantSecondRow("No Access", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        expect(casePage.returnParticipantTypeAlert()).toEqual("This action is not allowed. No Access and Owner is conflict combination.");
        casePage.switchToDefaultContent();
    });

    it('should create new case add/edit timeSheet and verify the time widget data in cases overview page', function() {


        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            casePage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Case", text, "8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickLastElementInTreeData();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Case", text, "1");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            casePage.verifyTimeWidgetData("7");

        });
    });

    it('should create new case verify the notification message and no access of the object name ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.editOwningGroup(Objects.basepage.data.owningGroupAdministratorDev);
        expect(casePage.returnOwningGroup()).toEqual(Objects.basepage.data.owningGroupAdministratorDev);
        casePage.verifyTheNotificationMessage("Case File ");
        casePage.editPriority("High");
        casePage.verifyFirstElementNameNoAccess();
    });

});
