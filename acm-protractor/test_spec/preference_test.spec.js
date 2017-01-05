var casePage = require('../Pages/case_page.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var loginPage = require('../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../Pages/time_tracking_page.js');
var costTrackingPage = require('../Pages/cost_tracking_page.js');
var preferencesPage = require('../Pages/preference_page.js');
var complaintPage = require('../Pages/complaint_page.js');




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

    it('should disabled the Complaints Details widget in preference page and verify it in the complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Details");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("details", "Details");
    });

    it('should enable the Complaints Details widget in preference page and verify it in the complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Details");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("details", "Details");
    });

    it('should disabled the Tasks Details widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "Details");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("details", "Details");
    });

    it('should enable the Tasks Details widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "Details");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("details", "Details");
    });

    it('should disabled the Cases Details widget in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Details");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("details", "Details");
    });

    it('should enable the Cases Details widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Details");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("details", "Details");
    });

    it('should disabled the Time Tracking Details widget in preference page and verify it in the cases  overview page ', function() {

        timeTrackingPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Time Tracking", "Details");
        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.waitForOverView();
        timeTrackingPage.verifyIfWidgetIsNotDisplayed("details", "Details");
    });

    it('should enable the Time Tracking Details widget in preference page and verify it in the cases overview page ', function() {

        timeTrackingPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Time Tracking", "Details");
        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.waitForOverView();
        timeTrackingPage.verifyIfWidgetIsDisplayed("details", "Details");
    });

    it('should disabled the Cost Tracking Details widget in preference page and verify it in the cases  overview page ', function() {

        costTrackingPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cost Tracking", "Details");
        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.waitForOverView();
        costTrackingPage.verifyIfWidgetIsNotDisplayed("details", "Details");
    });

    it('should enable the Cost Tracking Details widget in preference page and verify it in the cases overview page ', function() {

        costTrackingPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cost Tracking", "Details");
        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.waitForOverView();
        costTrackingPage.verifyIfWidgetIsDisplayed("details", "Details");
    });


    it('should disabled the Cases People widget in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "People");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("people", "People");
    });

    it('should enable the Cases People widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "People");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("people", "People");
    });



    it('should disabled the Cases Documents widget in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Documents");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("documents", "Documents");
    });

    it('should enable the Cases Documents widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Documents");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("documents", "Documents");
    });


    it('should disabled the Cases Participants widget in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Participants");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("participants", "Participants");
    });

    it('should enable the Cases Participants widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Participants");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("participants", "Participants");
    });

    it('should disabled the Cases  Notes in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Notes");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("notes", "Notes");
    });

    it('should enable the Cases Notes widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Notes");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("notes", "Notes");
    });

    it('should disabled the Cases  Tasks in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Tasks");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("tasks", "Tasks");
    });

    it('should enable the Cases Tasks widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Tasks");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("tasks", "Tasks");
    });

    it('should disabled the Cases  References in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "References");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("references", "References");
    });

    it('should enable the Cases References widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "References");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("references", "References");
    });

    it('should disabled the Cases  History in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "History");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("history", "History");
    });

    it('should enable the Cases History widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "History");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("history", "History");
    });

    it('should disabled the Cases  Time in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Time");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("time", "Time");
    });

    it('should enable the Cases Time widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Time");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("time", "Time");
    });

    it('should disabled the Cases  Cost in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Cost");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("cost", "Cost");
    });

    it('should enable the Cases Cost widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Cost");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("cost", "Cost");
    });

    it('should disabled the Cases  Calendar in preference page and verify it in the cases  overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cases", "Calendar");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsNotDisplayed("calendar", "Calendar");
    });

    it('should enable the Cases Calendar widget in preference page and verify it in the cases overview page ', function() {

        casePage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cases", "Calendar");
        casePage.clickModuleCasesFiles();
        casePage.waitForOverView();
        casePage.verifyIfWidgetIsDisplayed("calendar", "Calendar");
    });


    it('should disabled the Cost Tracking  Person in preference page and verify it in the cases  overview page ', function() {

        costTrackingPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cost Tracking", "Person");
        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.waitForOverView();
        costTrackingPage.verifyIfWidgetIsNotDisplayed("person", "Person");
    });

    it('should enable the Cost Tracking Calendar widget in preference page and verify it in the cases overview page ', function() {

        costTrackingPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cost Tracking", "Person");
        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.waitForOverView();
        costTrackingPage.verifyIfWidgetIsDisplayed("person", "Person");
    });

    it('should disabled the Cost Tracking  Expenses in preference page and verify it in the cases  overview page ', function() {

        costTrackingPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Cost Tracking", "Expenses");
        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.waitForOverView();
        costTrackingPage.verifyIfWidgetIsNotDisplayed("expenses", "Expenses");
    });

    it('should enable the Cost Tracking Expenses widget in preference page and verify it in the cases overview page ', function() {

        costTrackingPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Cost Tracking", "Expenses");
        costTrackingPage.clickModuleCostTracking();
        costTrackingPage.waitForOverView();
        costTrackingPage.verifyIfWidgetIsDisplayed("expenses", "Expenses");
    });

    it('should disabled the Time Tracking  Person in preference page and verify it in the cases  overview page ', function() {

        timeTrackingPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Time Tracking", "Person");
        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.waitForOverView();
        timeTrackingPage.verifyIfWidgetIsNotDisplayed("person", "Person");
    });

    it('should enable the Time Tracking Calendar widget in preference page and verify it in the cases overview page ', function() {

        timeTrackingPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Time Tracking", "Person");
        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.waitForOverView();
        timeTrackingPage.verifyIfWidgetIsDisplayed("person", "Person");
    });

    it('should disabled the Time Tracking  Hours Summary in preference page and verify it in the cases  overview page ', function() {

        timeTrackingPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Time Tracking", "Hours Summary");
        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.waitForOverView();
        timeTrackingPage.verifyIfWidgetIsNotDisplayed("expenses", "Hours Summary");
    });

    it('should enable the Time Tracking Hours Summary widget in preference page and verify it in the cases overview page ', function() {

        timeTrackingPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Time Tracking", "Hours Summary");
        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.waitForOverView();
        timeTrackingPage.verifyIfWidgetIsDisplayed("hoursSummary", "Hours Summary");
    });

    it('should disabled the Tasks Documents widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "Documents");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("documents", "Documents");
    });

    it('should enable the Tasks Documents widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "Documents");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("documents", "Documents");
    });

    it('should disabled the Tasks Rework Details widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "Rework Details");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("reworkDetails", "Rework Details");
    });

    it('should enable the Tasks Rework Details widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "Rework Details");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("reworkDetails", "Rework Details");
    });

    it('should disabled the Tasks  Documents Under Review  widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "Documents Under Review");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("docReview", " Documents Under Revi ");
    });

    it('should enable the Tasks Documents Under Review widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "Documents Under Review");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("docReview", "Documents Under Revi");
    });

    it('should disabled the Tasks  Notes widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "Notes");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("notes", " Notes");
    });

    it('should enable the Tasks Notes widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "Notes");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("notes", "Notes");
    });

    it('should disabled the Tasks   Workflow Overview  widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "Workflow Overview");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("workflow", "  Workflow Overview ");
    });

    it('should enable the Tasks  Workflow Overview  widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "Workflow Overview");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("workflow", "Workflow Overview");
    });

    it('should disabled the Tasks  History  widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "History");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("history", "History");
    });

    it('should enable the Tasks  History widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "History");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("history", "History");
    });

    it('should disabled the Tasks   eSignature   widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Tasks", "eSignature");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsNotDisplayed("signature", "eSignature");
    });

    it('should enable the Tasks   eSignature  widget in preference page and verify it in the tasks overview page ', function() {

        taskPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Tasks", "eSignature");
        taskPage.clickModuleTasks();
        taskPage.waitForOverView();
        taskPage.verifyIfWidgetIsDisplayed("signature", "eSignature");
    });

    it('should disabled the Complaints People widget in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "People");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("people", "People");
    });

    it('should enable the Complaints People widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "People");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("people", "People");
    });

    it('should disabled the Complaints Documents widget in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Documents");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("documents", "Documents");
    });

    it('should enable the Complaints Documents widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Documents");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("documents", "Documents");
    });


    it('should disabled the Complaints Participants widget in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Participants");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("participants", "Participants");
    });

    it('should enable the Complaints Participants widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Participants");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("participants", "Participants");
    });

    it('should disabled the Complaints  Notes in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Notes");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("notes", "Notes");
    });

    it('should enable the Complaints Notes widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Notes");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("notes", "Notes");
    });

    it('should disabled the Complaints  Tasks in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Tasks");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("tasks", "Tasks");
    });

    it('should enable the Complaints Tasks widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Tasks");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("tasks", "Tasks");
    });

    it('should disabled the Complaints  References in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "References");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("references", "References");
    });

    it('should enable the Complaints References widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "References");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("references", "References");
    });

    it('should disabled the Complaints  History in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "History");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("history", "History");
    });

    it('should enable the Complaints History widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "History");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("history", "History");
    });

    it('should disabled the Complaints  Time in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Time");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("time", "Time");
    });

    it('should enable the Complaints Time widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Time");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("time", "Time");
    });

    it('should disabled the Complaints  Cost in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Cost");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("cost", "Cost");
    });

    it('should enable the Complaints Cost widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Cost");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("cost", "Cost");
    });

    it('should disabled the Complaints  Calendar in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Calendar");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("calendar", "Calendar");
    });

    it('should enable the Complaints Calendar widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Calendar");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("calendar", "Calendar");
    });


    it('should disabled the Complaints  Location in preference page and verify it in the Complaints  overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.disabledWidget("Complaints", "Locations");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsNotDisplayed("locations", "Locations");
    });

    it('should enable the Complaints Location widget in preference page and verify it in the Complaints overview page ', function() {

        complaintPage.navigateToPreferencePage();
        preferencesPage.enableWidget("Complaints", "Locations");
        complaintPage.clickModuleComplaints();
        complaintPage.waitForOverView();
        complaintPage.verifyIfWidgetIsDisplayed("locations", "Locations");
    });

});
