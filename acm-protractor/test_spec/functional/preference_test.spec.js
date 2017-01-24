var casePage = require('../../Pages/case_page.js');
var Objects = require('../../json/Objects.json');
var taskPage = require('../../Pages/task_page.js');
var loginPage = require('../../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var preferencesPage = require('../../Pages/preference_page.js');
var complaintPage = require('../../Pages/complaint_page.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');





function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

}

describe('preference page  tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    using([{ preference: "Complaints", widget: "Locations", expected: "locations" }, { preference: "Complaints", widget: "People", expected: "people" },
        { preference: "Complaints", widget: "Details", expected: "details" }, { preference: "Complaints", widget: "Documents", expected: "documents" },
        { preference: "Complaints", widget: "Notes", expected: "notes" }, { preference: "Complaints", widget: "Tasks", expected: "tasks" },
        { preference: "Complaints", widget: "History", expected: "history" }, { preference: "Complaints", widget: "Time", expected: "time" },
        { preference: "Complaints", widget: "Cost", expected: "cost" }, { preference: "Complaints", widget: "Calendar", expected: "calendar" },
        { preference: "Complaints", widget: "References", expected: "references" }, { preference: "Complaints", widget: "Participants", expected: "participants" }
    ], function(data) {

        it('should enable the Complaints widgets in preference page and verify it in the Complaints overview page ', function() {
            complaintPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            complaintPage.clickModuleComplaints();
            complaintPage.waitForOverView();
            complaintPage.verifyIfWidgetIsDisplayed(data.expected, data.widget);
        });

        it('should disabled the Complaints wdgets in preference page and verify it in the Complaints  overview page ', function() {

            complaintPage.navigateToPreferencePage();
            preferencesPage.disabledWidget(data.preference, data.widget);
            complaintPage.clickModuleComplaints();
            complaintPage.waitForOverView();
            complaintPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget);
        });

    });

    using([{ preference: "Cases", widget: "People", expected: "people" }, { preference: "Cases", widget: "Details", expected: "details" },
        { preference: "Cases", widget: "Documents", expected: "documents" }, { preference: "Cases", widget: "Notes", expected: "notes" },
        { preference: "Cases", widget: "Tasks", expected: "tasks" }, { preference: "Cases", widget: "History", expected: "history" },
        { preference: "Cases", widget: "Time", expected: "time" }, { preference: "Cases", widget: "Cost", expected: "cost" },
        { preference: "Cases", widget: "Calendar", expected: "calendar" }, { preference: "Cases", widget: "References", expected: "references" },
        { preference: "Cases", widget: "Participants", expected: "participants" }
    ], function(data) {

        it('should disabled the Cases widgets in preference page and verify it in the cases  overview page ', function() {

            casePage.navigateToPreferencePage();
            preferencesPage.disabledWidget(data.preference, data.widget);
            casePage.clickModuleCasesFiles();
            casePage.waitForOverView();
            casePage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget);
        });

        it('should enable the Cases widgets in preference page and verify it in the cases overview page ', function() {

            casePage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            casePage.clickModuleCasesFiles();
            casePage.waitForOverView();
            casePage.verifyIfWidgetIsDisplayed(data.expected, data.widget);
        });
    });

    using([{ preference: "Tasks", widget: "Rework Details", expected: "reworkDetails" }, { preference: "Tasks", widget: "Details", expected: "details" },
        { preference: "Tasks", widget: "Documents Under Review", expected: "docReview" }, { preference: "Tasks", widget: "Notes", expected: "notes" },
        { preference: "Tasks", widget: "Documents", expected: "documents" }, { preference: "Tasks", widget: "History", expected: "history" },
        { preference: "Tasks", widget: "Workflow Overview", expected: "workflow" }, { preference: "Tasks", widget: "eSignature", expected: "signature" }

    ], function(data) {

        it('should disabled the Tasks widgets in preference page and verify it in the tasks overview page ', function() {

            taskPage.navigateToPreferencePage();
            preferencesPage.disabledWidget(data.preference, data.widget);
            taskPage.clickModuleTasks();
            taskPage.waitForOverView();
            taskPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget);
        });

        it('should enable the Tasks widgets in preference page and verify it in the tasks overview page ', function() {

            taskPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            taskPage.clickModuleTasks();
            taskPage.waitForOverView();
            taskPage.verifyIfWidgetIsDisplayed(data.expected, data.widget);
        });

    });

    using([{ preference: "Cost Tracking", widget: "Details", expected: "details" }, { preference: "Cost Tracking", widget: "Person", expected: "person" },
        { preference: "Cost Tracking", widget: "Expenses", expected: "expenses" }
    ], function(data) {

        it('should disabled the Cost Tracking widgets in preference page and verify it in the cases  overview page ', function() {

            costTrackingPage.navigateToPreferencePage();
            preferencesPage.disabledWidget(data.preference, data.widget);
            costTrackingPage.clickModuleCostTracking();
            costTrackingPage.waitForOverView();
            costTrackingPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget);
        });

        it('should enable the Cost Tracking widgets in preference page and verify it in the cases overview page ', function() {

            costTrackingPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            costTrackingPage.clickModuleCostTracking();
            costTrackingPage.waitForOverView();
            costTrackingPage.verifyIfWidgetIsDisplayed(data.expected, data.widget);
        });
    });

    using([{ preference: "Time Tracking", widget: "Details", expected: "details" }, { preference: "Time Tracking", widget: "Person", expected: "person" },
        { preference: "Time Tracking", widget: "Hours Summary", expected: "hoursSummary" }
    ], function(data) {

        it('should disabled the Time Tracking widgets in preference page and verify it in the cases  overview page ', function() {

            timeTrackingPage.navigateToPreferencePage();
            preferencesPage.disabledWidget(data.preference, data.widget);
            timeTrackingPage.clickModuleTimeTracking();
            timeTrackingPage.waitForOverView();
            timeTrackingPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget);
        });

        it('should enable the Time Tracking widgets in preference page and verify it in the cases overview page ', function() {

            timeTrackingPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            timeTrackingPage.clickModuleTimeTracking();
            timeTrackingPage.waitForOverView();
            timeTrackingPage.verifyIfWidgetIsDisplayed(data.expected, data.widget);
        });
    });
});
