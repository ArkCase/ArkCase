var logger = require('../../log');
var userPage = require('../../Pages/user_profile_page.js');
var loginPage = require('../../Pages/login_page.js');
var robot = require(process.env['USERPROFILE'] + '/node_modules/robotjs');
var utils = require('../../util/utils.js');
var Objects = require('../../json/Objects.json');
var dashPage = require('../../Pages/dashboard_page.js');
var auditPage = require('../../Pages/audit_page.js');
var casePage = require('../../Pages/case_page.js');
var complaintPage = require('../../Pages/complaint_page.js');
var taskPage = require('../../Pages/task_page.js');
var notificationPage = require('../../Pages/notifications_page.js');
var reportPage = require('../../Pages/report_page.js');
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var preferencesPage = require('../../Pages/preference_page.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var flag = false;
var EC = protractor.ExpectedConditions;




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


describe('edit user profile page', function() {

    loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
    logger.log('Info', 'User succesfully logged in as supervisor');

    //Update Profile Information

    it('should navigate to user profile page', function () {

        userPage.clickUserNavigation();
        expect(userPage.returnUserNavigationProfile()).toEqual(Objects.userpage.data.userNavigationProfile, "User navigation profile is not correct");
        userPage.clickUserNavigationProfile();
        expect(userPage.returnUserPageHeader()).toEqual(Objects.userpage.data.userPageHeader, "User page header is not correct");
    });

    it('should edit username', function () {

        userPage.editUsername(Objects.userpage.data.userNameInput);
        expect(userPage.returnUsername()).toEqual(Objects.userpage.data.userNameInput, "Username is not updated");

    });

    it('should edit location in  contact information', function () {

        userPage.editLocation(Objects.userpage.data.userLocationInput);
        expect(userPage.returnUserLocation()).toEqual(Objects.userpage.data.userLocationInput, "User location is not updated");
    });
    it('should edit office phone in contact information', function () {

        userPage.editOfficePhone(Objects.userpage.data.officePhoneInput);
        expect(userPage.returnOfficePhone()).toEqual(Objects.userpage.data.officePhoneInput, "User office phone is not updated");
    });

    it('should edit im account', function () {

        userPage.editImAccount(Objects.userpage.data.imAccountInput);
        expect(userPage.returnImAccount()).toEqual(Objects.userpage.data.imAccountInput, "User IM account is not updated");

    });

    it('should edit short im account', function () {

        userPage.editShortImAccount(Objects.userpage.data.shortImaccountInput);
        expect(userPage.returnShortImAccount()).toEqual(Objects.userpage.data.shortImaccountInput, "User short IM account is not updated");

    });

    it('should edit mobile phone', function () {

        userPage.editMobilePhone(Objects.userpage.data.mobilephoneInput);
        expect(userPage.returnMobilePhone()).toEqual(Objects.userpage.data.mobilephoneInput, "User mobile phone is not updated");

    });

    it('should edit company name', function () {

        userPage.editCompanyName(Objects.userpage.data.companyNameInput);
        expect(userPage.returnCompanyName()).toEqual(Objects.userpage.data.companyNameInput, "Company name is not updated");

    });

    it('should edit address 1', function () {

        userPage.editAddressOne(Objects.userpage.data.addressOneInput);
        expect(userPage.returnAddressOne()).toEqual(Objects.userpage.data.addressOneInput, "User address one is not updated");

    });

    it('should edit address 2', function () {

        userPage.editAddressTwo(Objects.userpage.data.addressTwoInput);
        expect(userPage.returnAddressTwo()).toEqual(Objects.userpage.data.addressTwoInput, "User address two is not updated");

    });

    it('should edit city', function () {

        userPage.editCity(Objects.userpage.data.cityInput);
        expect(userPage.returnCity()).toEqual(Objects.userpage.data.cityInput, "User city is not updated");

    });

    it('should edit state', function () {

        userPage.editState(Objects.userpage.data.stateInput);
        expect(userPage.returnState()).toEqual(Objects.userpage.data.stateInput, "User state is not updated");

    });

    it('should edit zip', function () {

        userPage.editZip(Objects.userpage.data.zipInput);
        expect(userPage.returnZip()).toEqual(Objects.userpage.data.zipInput, "User zip is not updated");

    });

    it('should edit main office phone', function () {

        userPage.editMainOfficePhone(Objects.userpage.data.mainOfficePhoneInput);
        expect(userPage.returnMainOfficePhone()).toEqual(Objects.userpage.data.mainOfficePhoneInput, "User main office phone is not updated");

    });

    it('should edit fax', function () {

        userPage.editFax(Objects.userpage.data.faxInput);
        expect(userPage.returnFax()).toEqual(Objects.userpage.data.faxInput, "User fax is not updated");

    });

    it('should edit website', function () {

        userPage.editWebSite(Objects.userpage.data.websiteInput);
        expect(userPage.returnWebSite()).toEqual(Objects.userpage.data.websiteInput, "User web site is not updated");

    });


    it('should change profile picture', function () {

        userPage.changePicture();

    });

    it('should logout', function () {

        loginPage.Logout();

    });

    describe('dashboard page test', function () {

        beforeEach(function (done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function () {
            loginPage.Logout();

        });


        //Change Dashboard configuration

        using([{widgetName: "News", widgetTitle: Objects.dashboardpage.data.widgetTitleNews},
            {widgetName: "MyTasks", widgetTitle: Objects.dashboardpage.data.widgetTitleMyTasks},
            {widgetName: "MyCases", widgetTitle: Objects.dashboardpage.data.widgetTitleMyCases},
            {widgetName: "MyComplaints", widgetTitle: Objects.dashboardpage.data.widgetTitleMyComplaints},
            {widgetName: "NewComplaints", widgetTitle: Objects.dashboardpage.data.widgetTitleNewComplaints},
            {widgetName: "TeamWorkload", widgetTitle: Objects.dashboardpage.data.widgetTitleTeamWorkload},
            {widgetName: "CasesByStatus", widgetTitle: Objects.dashboardpage.data.widgetTitleCasesByStatus},
            {widgetName: "Weather", widgetTitle: Objects.dashboardpage.data.widgetTitleWeather}
        ], function (data) {
            it('should add/delete ' + data.widgetName, function () {

                dashPage.clickEditButton().clickAddWidgetButton().addWidget(data.widgetName).clickSaveChangesButton();
                expect(dashPage.returnWidgetTitle()).toEqual(data.widgetTitle, "Widget title is not correct in widget " + data.widgetName);
                dashPage.clickEditButton().removeWidgetButton().clickSaveChangesButton();

            });
        });
   });

    describe('case page tests', function () {

        beforeEach(function (done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function () {
            loginPage.Logout();

        });

        // Create New Case, make sure the new object is created

        it('should create new case ', function () {

            casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
            casePage.clickNextBtn();
            casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
            casePage.switchToDefaultContent();
            casePage.waitForCaseTitle();
            expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle, "Case title is not correct");
        });

        //verify that case type is correct on new created case

        it('should verify case type', function () {

            casePage.clickModuleCasesFiles();
            expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType, "Case type is not correct");
        });

        //verify that priority is correct on new created case

        it('should verify the priority filed', function () {

            casePage.clickModuleCasesFiles();
            expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium, "Priority is not correct");
        });

        //verify that created date is correct on new created case

        it('should verify the created date', function () {

            casePage.clickModuleCasesFiles();
            expect(casePage.returnCreatedDate()).toEqual(utils.returnToday("/"), "Created date is not correct");

        });

        //verify people initiator on new created case

        it('should  verify the people initiator', function () {

            casePage.clickModuleCasesFiles();
            casePage.clickPeopleLinkBtn();
            expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor, "People type is not correct");
            expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstName, "People first name is not correct");
            expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastName, "People last name is not correct");
        });

        //verify history table on new created case

        it('should verify the history table', function () {

            casePage.clickModuleCasesFiles();
            casePage.historyTable();
            expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent, "History event name is not correct");
            expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date is not correct");
            expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel, "History assignee is not correct");

        });

        //verify assignee by default on new created case

        it('should verify the assignee by default', function () {

            casePage.clickModuleCasesFiles();
            casePage.participantTable();
            expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct");
            expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is not correct");
            expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "assignee label is not correct");
            expect(casePage.returnParticipantNameSecondRow()).toEqual("", "assignee should be empty");
            expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "owning group label is not correct");
            expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "owning group is not correct");
            expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "reader label is not correct");
            expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "reader value is not current user");
        });

        //verify assigned to, owning group and due date

        it('should  verify assigned to, owning group and due date', function () {

            casePage.clickModuleCasesFiles();
            expect(casePage.returnDueDate()).toEqual(utils.returnDate("/", 180), "Default due date is not correct");
            expect(casePage.returnAssignee()).toEqual("", "Asignee by default should be empty");
            expect(casePage.returnOwningGroup()).toEqual(Objects.casepage.data.owningGroup, "Default owning group is not correct");

        });

        // close case and make sure the files are declared as records on the Alfresco site

        it('should create new case and change case status to closed, verify the automated task in tasks table and approve', function () {

            casePage.clickModuleCasesFiles();
            casePage.waitForCaseTitle();
            casePage.waitForChangeCaseButton();
            casePage.clickChangeCaseBtn();
            casePage.switchToIframes().changeCaseSubmit(Objects.casepage.data.approverSamuel, "Closed");
            casePage.clickTasksLinkBtn().waitForTasksTable();
            expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
            casePage.clickTaskTitle();
            taskPage.clickApproveBtn();
            expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
        });

        //Create a task associated to case

        it('should  add add hoc task from tasks table and verify the task', function () {

            casePage.clickModuleCasesFiles();
            casePage.clickTasksLinkBtn();
            casePage.clickAddTaskButton();
            taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDate(utils.returnToday("/")).clickSave();
            taskPage.clickCaseTitleInTasks();
            casePage.clickExpandFancyTreeTopElementAndSubLink("Tasks").waitForTasksTable();
            expect(casePage.returnTaskTableTitle()).toContain(Objects.taskpage.data.Subject, "Task subject is not correct");
            expect(casePage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Task assignee is not correct");
            expect(casePage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"), "Task created date is not correct");
            expect(casePage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium, "Task priority is not correct");
            expect(casePage.returnTaskTableDueDate()).toEqual(utils.returnToday("/"), "Task due date is not correct");
            expect(casePage.returnTaskTableStatus()).toEqual("ACTIVE", "Task status is not correct");
        });

        //verify Add Notes

        it('should  add/delete note', function () {

            casePage.clickModuleCasesFiles();
            casePage.clickNotesLink();
            casePage.addNote(Objects.casepage.data.note);
            casePage.deleteNote();
        });


        //verify adding document to document management

        it('should verify adding correspondence document', function () {

            casePage.clickModuleCasesFiles();
            casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
            casePage.rightClickRootFolder().addCorrespondence("case", "Notice of Investigation");
            casePage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

        });

        //View document (Click Open)

        it('should verify view document', function () {

            casePage.clickModuleCasesFiles();
            casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
            casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
            casePage.moveToTab().switchToDocIframes();
            casePage.returnDoc();

        });

        //Email document (Click Email)

        it('should verify sending document through email', function () {

            casePage.clickModuleCasesFiles();
            casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
            casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Email");
            casePage.sendEmail(Objects.basepage.data.email);
        });

    });
    describe('Complaint page tests ', function () {

        beforeEach(function (done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function () {

            loginPage.Logout();

        });

        //Create New Complaint, Make sure new object is created

        it('should create new complaint ', function () {

            complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName);
            expect(complaintPage.returnFirstNameValue()).toEqual(Objects.complaintPage.data.firstName);
            expect(complaintPage.returnLastNameValue()).toEqual(Objects.complaintPage.data.lastName);
            complaintPage.clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
            complaintPage.switchToDefaultContent();
            complaintPage.waitForComplaintsPage();
            expect(complaintPage.returnComplaintsTitle()).toEqual(Objects.complaintPage.data.title);

        });

        // verify people initiator on new added complaint

        it('Verify people initiator', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.waitForComplaintsPage();
            complaintPage.clickPeopleLinkBtn();
            expect(complaintPage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor, "People type is not correct");
            expect(complaintPage.returnPeopleFirstName()).toEqual(Objects.complaintPage.data.firstName, "First name is not correct");
            expect(complaintPage.returnPeopleLastName()).toEqual(Objects.complaintPage.data.lastName, "Last name is not correct");
        });

        //verify the assignee on new added complaint

        it('Verify the assignee by default', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.participantTable();
            expect(complaintPage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct");
            expect(complaintPage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is not correct");
            expect(complaintPage.returnParticipantTypeSecondRow()).toEqual("assignee", "assignee label is not correct");
            expect(complaintPage.returnParticipantNameSecondRow()).toEqual("", "assignee should be empty");
            expect(complaintPage.returnParticipantTypeThirdRow()).toEqual("owning group", "owning group label is not correct");
            expect(complaintPage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "owning group is not correct");
            expect(complaintPage.returnParticipantTypeForthRow()).toEqual("reader", "reader label is not correct");
            expect(complaintPage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "reader value is not current user");
        });

        // verify the event in history on new added complaint

        it('should Verify the event in the history table', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.historyTable();
            expect(complaintPage.returnHistoryEventName()).toEqual("Complaint Created", "History event name is not correct");
            expect(complaintPage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date is not correct");
            expect(complaintPage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel, "Assignee in history is not correct");
        });

        //Add Notes

        it('Add new note and verify added note', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.clickNotesLink();
            complaintPage.addNote(Objects.casepage.data.note);
            expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.note, "The note is succesfully added");
        });

        //Email document (Click Email)

        it('should verify sending document through email', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.clickExpandFancyTreeTopElementAndSubLink("Documents");
            complaintPage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Email");
            complaintPage.sendEmail(Objects.basepage.data.email);
        });

        //Close complaint (open case) and approve task and make sure the new case was created

        it('should navigate to complaints and close complaint with Open Investigation, approve automatic generated task and validate created case', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.clickCloseComplaint().switchToIframes().closeComplaint("Open Investigation", Objects.complaintPage.data.description, Objects.complaintPage.data.approver);
            complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Tasks");
            complaintPage.waitForTasksTable();
            complaintPage.clickRefreshButton();
            expect(complaintPage.returnAutomatedTask()).toContain(Objects.complaintPage.data.automaticTaskNameCloseComplaint, "Automated task name is not correct");
            complaintPage.clickTaskTitle();
            taskPage.clickApproveBtn();
            expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
            complaintPage.clickExpandFancyTreeTopElementAndSubLink("Details");
            expect(complaintPage.returnDetailsTextArea()).toContain(Objects.casepage.data.automatedTaskTitle, "Details text area does not containt automated task title");

        });

        //Create a task associated to complaint

        it('Add task from tasks table verify the task', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.clickTasksLinkBtn();
            complaintPage.clickAddTaskButton();
            taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
            taskPage.clickComplaintTitleInTasks();
            complaintPage.clickTasksLinkBtn().waitForTasksTable();
            expect(complaintPage.returnTaskTableTitle()).toContain(Objects.taskpage.data.Subject, "Task subject is not correct in the grid");
            expect(complaintPage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Asignee is not correct in grid");
            expect(complaintPage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"), "Created date is not correct in grid");
            expect(complaintPage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium, "Priority is not correct in grid");
            expect(complaintPage.returnTaskTableDueDate()).toEqual(utils.returnToday("/"), "Task due date is not correct in grid");
            expect(complaintPage.returnTaskTableStatus()).toEqual("ACTIVE", "Task status is not correct in grid");
        });

        //Add a document to document management

        it('should create new complaint and verify adding new document', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.clickExpandFancyTreeTopElementAndSubLink("Documents");
            complaintPage.rightClickRootFolder().addDocument("Notice of Investigation");
            utils.uploadDocx();
            complaintPage.validateDocGridData(true, "ArkCaseTesting", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

        });

        //Add details on new created complaint

        it('Verify text details add verify if is saved', function () {

            complaintPage.clickModuleComplaints();
            complaintPage.clickExpandFancyTreeTopElementAndSubLink("Details");
            complaintPage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
            complaintPage.clickSaveDetailsButton();
            complaintPage.clickRefreshButton();
            expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');

        });

    });
    describe('notification page test', function() {


        beforeEach(function(done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function() {
            loginPage.Logout();

        });

        //Click on the Notification Module and verify that description date/time is equal to modified column date/time

        it('should verify that notifications module is successfully opened', function() {
            notificationPage.navigateToPage(Objects.notificationPage.data.notificationsTitle);
            notificationPage.vaidateNotificationTitle();
        });
    });
    describe('report page tests', function() {

        beforeEach(function(done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function() {
            loginPage.Logout();

        });

        // Run each Report

        it('should navigate to case files and verify that case is displayed in case summary drafts report', function() {

            casePage.navigateToPage("Case Files");
            casePage.waitForCaseID();
            var caseid = casePage.getCaseId();
            var createdDate = casePage.returnCreatedDate();
            var dueDate = casePage.returnDueDate();
            var caseType = casePage.returnCaseType();
            var priority = casePage.returnPriority();
            casePage.navigateToPage("Reports");
            reportPage.runReport("CASE SUMMARY", "Draft", createdDate, createdDate);
            reportPage.switchToReportframes();
            reportPage.validateCaseReportTitles(Objects.reportPage.data.CaseSummaryReportTitleName, Objects.reportPage.data.CaseSummaryColumn1Title, Objects.reportPage.data.CaseSummaryColumn2Title, Objects.reportPage.data.CaseSummaryColumn3Title, Objects.reportPage.data.CaseSummaryColumn4Title, Objects.reportPage.data.CaseSummaryColumn5Title, Objects.reportPage.data.CaseSummaryColumn6Title, Objects.reportPage.data.CaseSummaryColumn7Title);
            reportPage.validateCaseReportValues(caseid, caseStatus, Objects.casepage.data.caseTitle, createdDate, priority, dueDate, caseType);
            reportPage.switchToDefaultContent();

        });

        it('should navigate to complaints and verify that complaint draft is displayed in complaint drafts report ', function() {

            complaintPage.navigateToPage("Complaints").waitForComplaintID();
            var createdDate = complaintPage.returnCreatedDate();
            var type = complaintPage.returnComplaintType();
            var priority = complaintPage.returnComplaintPriority();
            var complaintTitle = complaintPage.returnComplaintTitle();
            reportPage.navigateToPage("Reports");
            reportPage.runReport("COMPLAINT REPORT", "Draft", createdDate, createdDate);
            reportPage.switchToReportframes();
            reportPage.validateComplaintReportTitles(Objects.reportPage.data.ComplaintReportTitleName, Objects.reportPage.data.ComplaintReportColumn1Title, Objects.reportPage.data.CaseSummaryColumn2Title, Objects.reportPage.data.ComplaintReportColumn3Title, Objects.reportPage.data.ComplaintReportColumn4Title, Objects.reportPage.data.ComplaintReportColumn5Title, Objects.reportPage.data.ComplaintReportColumn6Title);
            reportPage.validateComplaintReportValues(complaintTitle, "Draft", type, priority, createdDate, createdDate);
            reportPage.switchToDefaultContent();

        });

        it('should navigate to complaints, close it with No further action and verify that is displayed in complaint disposition count report', function() {

            complaintPage.navigateToPage("Reports");
            reportPage.runReport("COMPLAINT DISPOSITION COUNT", "Draft", utils.returnToday("/"), utils.returnToday("/"));
            reportPage.switchToReportframes();
            reportPage.validateCDCReportTitles(Objects.reportPage.data.CDCReportDispositionTitle, Objects.reportPage.data.CDCReportCountTitle, Objects.reportPage.data.CDCFirstRowTitle, Objects.reportPage.data.CDCSecondRowTitle, Objects.reportPage.data.CDCThirdRowTitle, Objects.reportPage.data.CDCForthRowTitle);
            var closedNoFurtherAction = reportPage.returnCDCNoFurtherActionValue();
            var closedAddToExistingCase = reportPage.returnCDCAddToExistingCaseValue();
            var closedOpenInvestigation = reportPage.returnCDCOpenInvestigationValue();
            var closedReferExternal = reportPage.returnCDCReferExternalValue();
            reportPage.switchToDefaultContent().navigateToPage("Complaints").waitForComplaintID();
            complaintPage.clickCloseComplaint().switchToIframes().closeComplaint("No Further Action", Objects.complaintPage.data.description, Objects.complaintPage.data.approver);
            complaintPage.navigateToPage("Reports");
            reportPage.runReport("COMPLAINT DISPOSITION COUNT", "Draft", utils.returnToday("/"), utils.returnToday("/")).switchToReportframes();
            reportPage.validateCDCReportValues(closedAddToExistingCase.toString(), (closedNoFurtherAction + 1).toString(), closedOpenInvestigation.toString(), closedReferExternal.toString());
            reportPage.switchToDefaultContent();
        });

    });
    describe('audit page tests', function() {

        beforeEach(function(done) {

            loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
            testAsync(done);

        });

        afterEach(function() {
            loginPage.Logout();

        });

        //Run audit report

        it('should navigate to case files and verify that case is displayed in audit case report', function() {

            casePage.navigateToPage("Case Files").waitForCaseID();
            var caseid = casePage.getCaseId();
            var createdDate = casePage.returnCreatedDate();
            casePage.navigateToPage("Audit");
            auditPage.runReport("Case Files", caseid, utils.returnToday("/"), utils.returnToday("/"));
            auditPage.switchToAuditframes();
            auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
            auditPage.validateAuditReportValues(utils.returnToday("/"), Objects.taskspage.data.assigneeSamuel, "Case Viewed", "success", caseid, "CASE_FILE" );
            auditPage.switchToDefaultContent();

        });

     });

});


