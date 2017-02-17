var logger = require('../../log');
var casePage = require('../../Pages/case_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var Objects = require('../../json/Objects.json');
var taskPage = require('../../Pages/task_page.js');
var utils = require('../../util/utils.js');
var loginPage = require('../../Pages/login_page.js');
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
var preferencesPage = require('../../Pages/preference_page.js');
var adminPage = require('../../Pages/admin_page.js');
var Users = require('../../json/Users.json');
var dashPage = require('../../Pages/dashboard_page.js');
var notificationPage = require('../../Pages/notifications_page.js');
var subscriptionPage = require('../../Pages/subscriptions_page.js');
var complaintPage = require('../../Pages/complaint_page.js');
var auditPage = require('../../Pages/audit_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;



function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 25000);

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

    it('should create new case and verify case type', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle, "Case title is not correct in new added case");
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType, "Case type is not correct in new added case");
    });

    it('should create new case and verify the history table', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.historyTable();
        expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent, "History event name for added case is not correct ");
        expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date for added case is not correct");
        expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel, "History user for added case is not correct");
    });

    using([{ priority: "High", prioritySaved: Objects.casepage.data.priorityHigh }, {
        priority: "Medium",
        prioritySaved: Objects.casepage.data.priorityMedium
    }, { priority: "Expedite", prioritySaved: Objects.casepage.data.priorityExpedite }], function(data) {
        it('should create new case and edit the priority to ' + data.priority, function() {

            casePage.clickModuleCasesFiles();
            casePage.waitForCasesPage();
            casePage.editPriority(data.priority);
            expect(casePage.returnPriority()).toEqual(data.prioritySaved, "Priority is not updated");
        });
    });

    it('should  add/edit note', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
    });

    it('should  edit note', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickNotesLink();
        casePage.editNote(Objects.casepage.data.editnote);
        expect(casePage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is not updated");
    });

    it('should  add task from tasks table verify the task', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        casePage.validateTaskTableValue("Ad hoc task", "Title", Objects.taskpage.data.Subject);
        casePage.validateTaskTableValue("Ad hoc task", "Assignee", Objects.casepage.data.assigneeSamuel);
        casePage.validateTaskTableValue("Ad hoc task", "Created", utils.returnToday("/"));
        casePage.validateTaskTableValue("Ad hoc task", "Priority", Objects.casepage.data.priorityMedium);
        casePage.validateTaskTableValue("Ad hoc task", "Due", utils.returnToday("/"));
        casePage.validateTaskTableValue("Ad hoc task", "Status", "ACTIVE");

    });

    it('should  verify the people initiator', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor, "Default people type is not correct on added case");
        expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstName, "Default people first name is not correct on added case");
        expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastName, "Default people last name is not correct on added case");
    });

    it('should create new case add person and verify the added person', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickPeopleLinkBtn();
        casePage.waitForCasesPage();
        casePage.addPerson(Objects.casepage.data.peopleTypeWitness, Objects.casepage.data.peopleFirstName, Objects.casepage.data.peopleLastName);
        expect(casePage.returnPeopleTypeSecondRow()).toEqual(Objects.casepage.data.peopleTypeWitness, "People type is not correct");
        expect(casePage.returnPeopleFirstNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleFirstName, "People first name is not correct");
        expect(casePage.returnPeopleLastNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleLastName, "People last name is not correct");

    });


    it('should create new case and edit person initiator', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.editInitiator(Objects.casepage.data.peopleFirstNameEdit, Objects.casepage.data.peopleLastNameedit);
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor, "People type in person is not updated");
        expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstNameEdit, "People first name is not updated");
        expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastNameedit, "People last name is not updated");

    });

    it('should crete new case and add contact method ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        expect(casePage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodFacebook, "type is not correct on inserted contact method");
        expect(casePage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodFacebook, "value is not correct in inserted contact method");
        expect(casePage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "last modified date is not correct in inserted contact method");
        expect(casePage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "modified by is not correct in inserted contact method");

    });

    it('should create new case add contact method and delete it', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        casePage.deleteContactMethod();
    });

    it('should create new case and add contact method and edit it', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        casePage.editContactMethod(Objects.casepage.data.contactMethodEmail, Objects.casepage.data.contactMethodEmail);
        expect(casePage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodEmail, "Contact method type is not updated");
        expect(casePage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodEmail, "Contact method value is not updated");
        expect(casePage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Contact method last modified date is not updated");
        expect(casePage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Contact method modified by is not updated");
    });

    it('should create new case and add organization', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        expect(casePage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment, "Organization type is not correct on added organization");
        expect(casePage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment, "Organization value is not correct on added organization");
        expect(casePage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Organization last modified date is not correct on added organization");
        expect(casePage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Organization modified by is not correct on added organization");
    });

    it('should create new case add/delete organization', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        casePage.deleteOrganization();

    });

    it('should create new case add/edit organization', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        casePage.editOrganization(Objects.casepage.data.organizationTypeCorporation, Objects.casepage.data.organizationTypeCorporation);
        expect(casePage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation, "Organization type is not updated");
        expect(casePage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation, "Organization value is not updated");
        expect(casePage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Organization last modified is not updated");
        expect(casePage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Organization modified by is not updated");

    });

    it('should create new case and add address', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        expect(casePage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeHome, "Type is not correct in added address");
        expect(casePage.returnAddressStreet()).toEqual(Objects.casepage.data.street, "Street is not correct in added address");
        expect(casePage.returnAddressCity()).toEqual(Objects.casepage.data.city, "City is not correct in added address");
        expect(casePage.returnAddressState()).toEqual(Objects.casepage.data.state, "State is not correct in added address");
        expect(casePage.returnAddressZip()).toEqual(Objects.casepage.data.zip, "Zip is not correct in added address");
        expect(casePage.returnaddressCountryValue()).toEqual(Objects.casepage.data.country, "Country is not correct in added address");
        expect(casePage.returnAddressLastModified()).toEqual(utils.returnToday("/"), "Last modified is not correct in added address");
        expect(casePage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Modified by is not correct in added address");

    });

    it('should create new case and add/delete address', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        casePage.deleteAddress();
    });

    it('should create new case and add/edit address', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        casePage.editAddress(Objects.casepage.data.addressTypeBusiness, Objects.casepage.data.editStreet, Objects.casepage.data.editCity, Objects.casepage.data.editState, Objects.casepage.data.editZip, Objects.casepage.data.editCountry);
        expect(casePage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeBusiness, "Address type is not updated");
        expect(casePage.returnAddressStreet()).toEqual(Objects.casepage.data.editStreet, "Address street is not updated");
        expect(casePage.returnAddressCity()).toEqual(Objects.casepage.data.editCity, "Address city is not updated");
        expect(casePage.returnAddressState()).toEqual(Objects.casepage.data.editState, "Address state is not updated");
        expect(casePage.returnAddressZip()).toEqual(Objects.casepage.data.editZip, "Address zip is not updated");
        expect(casePage.returnaddressCountryValue()).toEqual(Objects.casepage.data.editCountry, "Address country is not updated");
        expect(casePage.returnAddressLastModified()).toEqual(utils.returnToday("/"), "Address last modified is not updated");
        expect(casePage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Address modified by is not updated");
    });

    it('should create new case and add alias', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        expect(casePage.returnAliasesType()).toEqual(Objects.casepage.data.aliaseFKA, "Aliases type is not correct");
        expect(casePage.returnAliasesValue()).toEqual(Objects.casepage.data.aliasValue, "Aliases value is not correct");
        expect(casePage.returnAliasesLastModified()).toEqual(utils.returnToday("/"), "Aliases last modified is not correct");
        expect(casePage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Aliases modified by is not correct");

    });

    it('should create new case and add/delete alias', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        casePage.deleteAlias();

    });

    it('should create new case and add/edit alias', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        casePage.editAlias(Objects.casepage.data.aliasMarried, Objects.casepage.data.editAlias);
        expect(casePage.returnAliasesType()).toEqual(Objects.casepage.data.aliasMarried, "Alias type is not updated");
        expect(casePage.returnAliasesValue()).toEqual(Objects.casepage.data.editAlias, "Alias value is not updated");
        expect(casePage.returnAliasesLastModified()).toEqual(utils.returnToday("/"), "Alias last modified is not updated");
        expect(casePage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Alias modified by is not updated");

    });

    it('should create new case and add/delete tag', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickTagsLinkBtn();
        casePage.addSugestedTag(Objects.timetrackingPage.data.tagname);
        expect(casePage.returnTagName()).toEqual(Objects.timetrackingPage.data.tagname, "Name is not correct on added tag");
        expect(casePage.returntagCratedDate()).toEqual(utils.returnToday("/"), "Created date is not correct on added tag");
        expect(casePage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Created by is not correct on added tag");
        casePage.deleteTag();

    });

    it('should create new case and click subscribe button and verify if unubscribe btn is displayed', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickSubscribeBtn();
        expect(casePage.returnUnsubscribeBtnText()).toEqual(Objects.casepage.data.unsubscribeBtn, "After click on subscribe text on button is not changed into Unsubscribe");
    });

    it('should verify if another assignee can be added  from participant table', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.participantTable();
        casePage.addParticipantFromParticipantTable("Assignee", "Samuel Supervisor");
        casePage.verifyTheNotificationMessage("Only one assignee is allowed");
    });

    it('should create new case and click unubscribe button, verify if is changed to subscribe', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickSubscribeBtn();
        casePage.clickUnubscribeBtn();
        expect(casePage.returnSubscribeBtnText()).toEqual(Objects.casepage.data.subscribeBtn, "After click on unsubscribe text on button is not changed into Subscribe");

    });

    using([{ status: "Active" }, { status: "Inactive" }, { status: "Deleted" }], function(data) {
        it('should create new case and change case status to ' + data.status + ', verify the automated task in tasks table and approve', function() {

            casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
            casePage.clickNextBtn();
            casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
            casePage.switchToDefaultContent();
            casePage.waitForCasesPage();
            casePage.clickChangeCaseBtn();
            casePage.switchToIframes().selectCaseStatus(data.status);
            casePage.selectApprover(Objects.casepage.data.approverSamuel).clickSubmitBtn();
            casePage.clickTasksLinkBtn().waitForTasksTable();
            casePage.clickOnTask("Automatic Task on Change Case Status");
            taskPage.clickApproveBtn();
            expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
        });
    });

    it('should create new case and and create new case from new case button', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickNewCaseButton();
        casePage.switchToIframes().submitGeneralInformation(Objects.casepage.data.caseName, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.waitForCasesPage();
        casePage.clickFirstTopElementInList();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseName, "Name is not correct in new created case");
    });

    it(' Click edit button verify updated case title and case type', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickEditCaseBtn();
        casePage.switchToIframes().submitGeneralInformation(Objects.casepage.data.caseName, "Benefits Appeal");
        casePage.clickParticipantTab().selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickFirstTopElementInList().clickRefreshBtn();
        casePage.waitForCaseType("Benefits Appeal");
        expect(casePage.returnCaseTitle()).toContain(Objects.casepage.data.caseName, "Case name is not updated");
        expect(casePage.returnCaseType()).toEqual("Benefits Appeal", "Case type is not updated");
    });

    it('should create two cases and put one as reference to the anotherone', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickFirstTopElementInList().addReference(caseid);
        expect(casePage.returnReferenceNumber()).toEqual(caseid, "id is not correct in column on added case as reference");
        expect(casePage.returnReferenceTitle()).toEqual(Objects.casepage.data.referenceCaseName, "name is not correct in column on added case as reference");
        expect(casePage.returnReferenceModified()).toEqual(utils.returnToday("/"), "modified date is not correct in column on added case as reference");
        expect(casePage.returnReferenceType()).toEqual(Objects.casepage.data.referenceType, "type is not correct in column on added case as reference");
        expect(casePage.returnReferenceStatus()).toEqual(Objects.casepage.data.referenceStatusDraft, "status is not correct in column on added case as reference");

    });

    it('should create case and verify if the same case be added as reference to itself', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.addReferenceAsItself(caseid);

    });

    it('should  edit the due date', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.editDueDate();
        expect(casePage.returnDueDate()).toEqual(utils.returnToday("/"), "Due date is not updated");
    });

    it('should create new case and verify adding correspondence document', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.rightClickRootFolder().addCorrespondence("case", "Notice of Investigation");
        casePage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    it('should create new case closed it, reinvestigate and verify in the reference table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.waitForChangeCaseButton();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickChangeCaseBtn();
            casePage.switchToIframes().selectCaseStatus("Closed");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            casePage.clickSubmitBtn();
            casePage.clickTasksLinkBtn().waitForTasksTable();
            casePage.clickOnTask("Automatic Task on Change Case Status");
            taskPage.clickApproveBtn();
            expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
            taskPage.clickCaseTitleInTasks();
            casePage.clickRefreshBtn();
            casePage.caseTitleStatus("CLOSED");
            casePage.clickReinvesigateBtn();
            casePage.switchToIframes().submitGeneralInformation("New", "Agricultural");
            casePage.clickParticipantTab().selectParticipant("Owner", Objects.casepage.data.approverSamuel);
            casePage.switchToIframes();
            casePage.clickSubmitBtn();
            casePage.waitForCasesPage();
            casePage.clickSecondElementInList();
            expect(casePage.returnCaseType()).toEqual("Agricultural")
            casePage.clikReferenceLink();
            expect(casePage.returnReferenceNumber()).toEqual(text, "Reference number is not correct after closing case and reinvestigate it");
            expect(casePage.returnReferenceTitle()).toEqual("New Case", "Reference title is not correct after closing case and reinvestigate it");
            expect(casePage.returnReferenceModified()).toEqual(utils.returnToday("/"), "Modified is not correct after closing case and reinvestigate it");
            expect(casePage.returnReferenceType()).toEqual(Objects.casepage.data.referenceType, "Type is not correct after closing case and reinvestigate it");
            expect(casePage.returnReferenceStatus()).toEqual("CLOSED", "Status is not closed after closing case and reinvestigate it");
        });
    });

    it('should craete new case and verify the assighnee by default', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant owner");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct ");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("", "Participant name in second row is should be empty");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "Participant type in third row is not correct");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in third row is not correct");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "Participant type in forth row is not correct ");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name in forth row is not correct");
    });

    using([{ participant: "Collaborator", participantSaved: "collaborator" },
        { participant: "Follower", participantSaved: "follower" },
        { participant: "Co-Owner", participantSaved: "co-owner" },
        { participant: "No Access", participantSaved: "no access" }
    ], function(data) {

        it('should create new case  select ' + data.participant + ' from paricipant tab and verify it in the paricipants table', function() {

            casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
            casePage.clickNextBtn();
            casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
            casePage.clickParticipantTab();
            casePage.selectParticipant(data.participant, "Sally");
            casePage.switchToIframes();
            casePage.clickSubmitBtn();
            casePage.switchToDefaultContent();
            casePage.waitForCasesPage();
            casePage.participantTable();
            expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct after adding participant  " + data.participant);
            expect(casePage.returnParticipantNameSecondRow()).toEqual("", "Participant name in second row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeThirdRow()).toEqual(data.participantSaved, "Participant type in third row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameThirdRow()).toEqual("Sally Supervisor", "Participant name in third row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group", "Participant type in forth row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in forth row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader", "Participant type in fifth row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor", "Participant name in fifth row is not correct after adding participant " + data.participant);
        });
    });

    it('should create new case  select Reader from paricipant tab and verify it in the paricipants table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Reader", "Sally");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding participant ");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant ");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct after adding participant");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("", "Participant name in second row is not correct after adding participant ");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "Participant type in forth row is not correct after adding participant ");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in forth row is not correct after adding participant ");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "Participant type in forth row is not correct after adding participant ");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Sally Supervisor", "Participant name in forth row is not correct after adding participant ");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader", "Participant type in fifth row is not correct after adding participant ");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor", "Participant name in fifth row is not correct after adding participant ");
    });

    it('should create new case  select Approver from paricipant tab and verify it in the paricipants table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Approver", "Sally");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding participant ");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant ");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("approver", "Participant type in second row is not correct after adding participant");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Sally Supervisor", "Participant name in second row is not correct after adding participant ");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("assignee", "Participant type in third row is not correct after adding participant ");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("", "Participant name in third row is not correct after adding participant ");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group", "Participant type in forth row is not correct after adding participant");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in forth row is not correct after adding participant");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader", "Participant type in fifth row is not correct after adding participant ");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor", "Participant name in fifth row is not correct after adding participant ");
    });

    it('should create new case and verify adding new Report of Investigation document', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.rightClickRootFolder().addDocument("Report of Investigation");
        casePage.switchToIframes().submitReportOfInvestigation(Objects.basepage.data.reportTitle, Objects.taskspage.data.assigneeSamuel);
        casePage.switchToDefaultContent().validateDocGridData(true, "Report of Investigation", ".pdf", "Report of Investigation", utils.returnToday("/"), utils.returnToday("/"), "Samuel Supervisor", "1.0", "ACTIVE");

    });

    it('should  verify if special type can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.participantTable();
        casePage.clickSpecialTypeDeleteBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfAssigneeCanBeDeleted();

    });

    it('should  verify if assignee can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.participantTable();
        casePage.clickDeleteAsigneeBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfSpecialTypeCaneBeDeleted();

    });

    it('should verify if owning  can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.participantTable();
        casePage.clickOwningGroupDeleteBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfOwningGroupCanBeDeleted();
    });

    it('should  verify if reader  can be deleted', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        casePage.clickReaderDeleteBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfReaderCanBeDeleted();
    });

    it('should verify adding note in document viewer in cases', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
        casePage.moveToTab().clickDocViewNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        expect(casePage.returnDocumentNoteName()).toEqual(Objects.casepage.data.note, "Note is not succcessfully saved in document viewer");

    });

    it('should edit assignee from participant table', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.participantTable();
        casePage.clickEditAssigneeBtn();
        casePage.editAssigneeInParticipantTable("Samuel Supervisor");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor", "Edited assignee is not saved");
    });

    it('should verify if the people intiator delete button is displayed', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickPeopleLinkBtn();
        casePage.verifyIfInitiatorCanBeDeleted();
    });

    it('should create new case and add task from tasks table verify the task and verify the task table column number', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        casePage.verifyTasksTableColumnsNumber();
    });

    it('should create new case and verify the alert message for created case', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.verifyTheNotificationMessage("Case File ", "The notification message after save is not correct");
    });

    it('should create new case by default assignee, claim it and verify the assignee', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickClaimButton();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "After claim assignee is not correct");
    });

    it('should create new case by default assignee, claim it verify the assignee then uncalaim it and verify if the assignee is removed ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickClaimButton();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Default assignee is not correct when case is created");
        casePage.clickUnclaimButton();
        expect(casePage.returnAssignee()).toEqual("", "The assignee name is displayed");
    });


    it('should verify replace of document and return to previous version', function() {

        casePage.clickModuleCasesFiles();
        casePage.waitForCasesPage();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents").doubleClickRootFolder().rightClickDocument().clickDocAction("Replace").uploadFile().replaceVersion("1.0");
        expect(casePage.returnDocVersionGrid()).toEqual("1.0", "The version of document is not reverted to 1.0");
    });

    it('should crate new case and try to add owner and no access from participant tab for same user and verify the alert message', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        casePage.clickAddParticipantTypeSecondRowbtn();
        casePage.selectParticipantSecondRow("No Access", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        expect(casePage.returnParticipantTypeAlert()).toEqual("This action is not allowed. No Access and Owner is conflict combination.");
        casePage.switchToDefaultContent();
    });

    it('should create new case and add/edit timesheet and verify it in the in Complaints overview page', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            casePage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Case");
            timeTrackingPage.clickChargeCode();
            casePage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            casePage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Case");
            timeTrackingPage.clickChargeCode();
            complaintPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            timeTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            complaintPage.verifyTimeWidgetData("7");

        });
    });

});

describe('Complaints Tests ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);
    });

    afterEach(function() {

        loginPage.Logout();

    });

    it('should create new complaint ', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        expect(complaintPage.returnComplaintsTitle()).toEqual(Objects.complaintPage.data.title, "Title is not correct on new created complaint");

    });

    it('Add/delete note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.deleteNote();
    });

    it('Add new note and edit added note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.editNote(Objects.casepage.data.editnote);
        expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is not sucessfully edited");
    });

    it('Add link from details', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickInsertLinkInDetails();
        expect(complaintPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle);
        complaintPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        complaintPage.validateDetailsTextArea(Objects.taskspage.data.insertLinkText, 'The link is not added');
        complaintPage.clickSaveDetailsButton();
        complaintPage.validateDetailsTextArea(Objects.taskspage.data.insertLinkText, 'The link is not mathcing the expected value');

    });

    it('should create new complaint and add picture from details', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickDetailsAddPicture();
        complaintPage.uploadPicture();
        expect(complaintPage.returnDetailsUploadedImage(), "Image is not succesfully added in details");

    });

    it('Verify adding new Report of Investigation document', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addDocument("Report of Investigation");
        complaintPage.switchToIframes().submitReportOfInvestigation(Objects.basepage.data.reportTitle, Objects.taskspage.data.assigneeSamuel);
        complaintPage.switchToDefaultContent().validateDocGridData(true, "Report of Investigation", ".pdf", "Report of Investigation", utils.returnToday("/"), utils.returnToday("/"), Objects.casepage.data.approverSamuel, "1.0", "ACTIVE");

    });

    it('should verify adding notes in document viewer in complaints', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
        complaintPage.moveToTab().clickDocViewNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        expect(complaintPage.returnDocumentNoteName()).toEqual(Objects.casepage.data.note, "Note is not succesfulluly added in document viewer in complaints");
    });

    using([{ priority: "High", prioritySaved: Objects.casepage.data.priorityHigh }, {
        priority: "Medium",
        prioritySaved: Objects.casepage.data.priorityMedium
    }, { priority: "Expedite", prioritySaved: Objects.casepage.data.priorityExpedite }, { priority: "Low", prioritySaved: Objects.taskspage.data.priorityLow }], function(data) {
        it('should create new case and edit the priority to ' + data.priority, function() {


            complaintPage.clickModuleComplaints();
            complaintPage.waitForComplaintsPage();
            complaintPage.editPriority(data.priority);
            expect(complaintPage.returnPriority()).toEqual(data.prioritySaved, "Priority is not updated into " + data.priority);
        });
    });

    it('Edit assignee', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.editAssignee("bthomas");
        expect(complaintPage.returnAssignee()).toEqual("Bill Thomas", "Assignee is not updated");
    });

    it('should create new complaint and add person', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addPerson(Objects.casepage.data.peopleTypeWitness, Objects.casepage.data.peopleFirstName, Objects.casepage.data.peopleLastName);
        expect(complaintPage.returnPeopleTypeSecondRow()).toEqual(Objects.casepage.data.peopleTypeWitness, "Type of added person in complaints is not correct");
        expect(complaintPage.returnPeopleFirstNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleFirstName, "First name of added person in complaints is not correct");
        expect(complaintPage.returnPeopleLastNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleLastName, "Last name of addede person in complaints is not correct");
    });

    it('should create new complaint and edit person initiator', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.editInitiator(Objects.casepage.data.peopleFirstNameEdit, Objects.casepage.data.peopleLastNameedit);
        expect(complaintPage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor, "People type is not updated");
        expect(complaintPage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstNameEdit, "People first name is not updated");
        expect(complaintPage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastNameedit, "People last name is not updated");
    });

    it('should create new complaint and add contact method', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        expect(complaintPage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodFacebook, "Contact method type is not correct");
        expect(complaintPage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodFacebook, "Contact method value is not correct");
        expect(complaintPage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Contact method modified is not correct");
        expect(complaintPage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Contact method modified by is not correct");
    });

    it('should  add contact method and delete it', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        complaintPage.deleteContactMethod();
    });

    it('should create new complaint add contact method and edit it', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        complaintPage.editContactMethod(Objects.casepage.data.contactMethodEmail, Objects.casepage.data.contactMethodEmail);
        expect(complaintPage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodEmail, "Contact method type is not updated");
        expect(complaintPage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodEmail, "Contact method email is not updated");
        expect(complaintPage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Contact method last modified is not updated");
        expect(complaintPage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Contact method modified by is not updated");
    });

    it('should create new complaint and add organization', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        expect(complaintPage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment, "Organization type goverment is not correct");
        expect(complaintPage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment, "Organization value is not correct");
        expect(complaintPage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Organization last modified is not correct");
        expect(complaintPage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Organization modified by is not correct");
    });

    it('should create new complaint add/edit organization', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        complaintPage.editOrganization(Objects.casepage.data.organizationTypeCorporation, Objects.casepage.data.organizationTypeCorporation);
        expect(complaintPage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation, "Organization type is not updated");
        expect(complaintPage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation, "Organization value is not updated");
        expect(complaintPage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"), "Organization last modified is not updated");
        expect(complaintPage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel, "Organization modified by is not updated");

    });

    it('should create new complaint add/delete organization', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        complaintPage.deleteOrganization();
    });

    it('should create new complaint and add address', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        expect(complaintPage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeHome, "Address type is not correct");
        expect(complaintPage.returnAddressStreet()).toEqual(Objects.casepage.data.street, "Address street is not correct");
        expect(complaintPage.returnAddressCity()).toEqual(Objects.casepage.data.city, "Address city is not correct");
        expect(complaintPage.returnAddressState()).toEqual(Objects.casepage.data.state, "Address state is not correct");
        expect(complaintPage.returnAddressZip()).toEqual(Objects.casepage.data.zip, "Address zip is not correct");
        expect(complaintPage.returnaddressCountryValue()).toEqual(Objects.casepage.data.country, "Address country is not correct");
        expect(complaintPage.returnAddressLastModified()).toEqual(utils.returnToday("/"), "Address last modified is not correct");
        expect(complaintPage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Address modified by is not correct");
    });

    it('should create new complaint and add/edit address', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        complaintPage.editAddress(Objects.casepage.data.addressTypeBusiness, Objects.casepage.data.editStreet, Objects.casepage.data.editCity, Objects.casepage.data.editState, Objects.casepage.data.editZip, Objects.casepage.data.editCountry);
        expect(complaintPage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeBusiness, "Address type is not updated");
        expect(complaintPage.returnAddressStreet()).toEqual(Objects.casepage.data.editStreet, "Address street is not updated");
        expect(complaintPage.returnAddressCity()).toEqual(Objects.casepage.data.editCity, "Address city is not updated");
        expect(complaintPage.returnAddressState()).toEqual(Objects.casepage.data.editState, "Address state is not updated");
        expect(complaintPage.returnAddressZip()).toEqual(Objects.casepage.data.editZip, "Address zip is not updated");
        expect(complaintPage.returnaddressCountryValue()).toEqual(Objects.casepage.data.editCountry, "Address country is not updated");
        expect(complaintPage.returnAddressLastModified()).toEqual(utils.returnToday("/"), "Address last modified is not updated");
        expect(complaintPage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Address modified by is not updated");
    });

    it('should create new complaint and add/delete address', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        complaintPage.deleteAddress();
    });

    it('should create new complaint and add alias', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        expect(complaintPage.returnAliasesType()).toEqual(Objects.casepage.data.aliaseFKA, "Alias type is not correct");
        expect(complaintPage.returnAliasesValue()).toEqual(Objects.casepage.data.aliasValue, "Alias value is not correct");
        expect(complaintPage.returnAliasesLastModified()).toEqual(utils.returnToday("/"), "Alias last modified is not correct");
        expect(complaintPage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Alias modified by is not correct");

    });

    it('should create new complaint and add/edit alias', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        complaintPage.editAlias(Objects.casepage.data.aliasMarried, Objects.casepage.data.editAlias);
        expect(complaintPage.returnAliasesType()).toEqual(Objects.casepage.data.aliasMarried, "Alias type is not updated");
        expect(complaintPage.returnAliasesValue()).toEqual(Objects.casepage.data.editAlias, "Alias value is not updated");
        expect(complaintPage.returnAliasesLastModified()).toEqual(utils.returnToday("/"), "Alias last modified is not updated");
        expect(complaintPage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Alias modified by is not updated");

    });

    it('should create new complaint and click subscribe button and verify if unubscribe btn is displayed', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickSubscribeBtn();
        expect(complaintPage.returnUnsubscribeBtnText()).toEqual(Objects.casepage.data.unsubscribeBtn, "Subscribe button text is not changed into unsubscribe after click on subscribe");

    });

    it('should create new complaint and click unubscribe button, verify if is changed to subscribe', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickSubscribeBtn();
        complaintPage.clickUnubscribeBtn();
        expect(complaintPage.returnSubscribeBtnText()).toEqual(Objects.casepage.data.subscribeBtn, "Unsubscribe button text is not changed into subscribe after click on unsubscribe");

    });

    it('Edit due date', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.editDueDate();
        expect(complaintPage.returnDueDate()).toEqual(utils.returnToday("/"), "Due date is not updated");
    });

    it('should create new complaint and add location', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Locations");
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        expect(complaintPage.returnLocationAddress()).toEqual("street", "Location address is not correct");
        expect(complaintPage.returnLocationType()).toEqual("Home", "Location type is not correct");
        expect(complaintPage.returnLocationCity()).toEqual("city", "Location city is not correct");
        expect(complaintPage.returnLocationState()).toEqual("state", "Location state is not correct");
        expect(complaintPage.returnLocationZip()).toEqual("zip", "Location zip is not correct");

    });

    it('should create new complaint add location and verify if add location button is still displayed', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Locations");
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        complaintPage.verifyIfAddLocationsBtnIsDisplayed();
    });

    it('should create new complaint add location and delete it', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Locations");
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        complaintPage.deleteLocation();

    });

    it('should create new complaint add/edit location', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Locations");
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        complaintPage.editLocation("Business", "street1", "city1", "state1", "zip1");
        expect(complaintPage.returnLocationAddress()).toEqual("street1", "Location address is not updated");
        expect(complaintPage.returnLocationType()).toEqual("Business", "Location type is not updated");
        expect(complaintPage.returnLocationCity()).toEqual("city1", "Location city is not updated");
        expect(complaintPage.returnLocationState()).toEqual("state1", "Location state is not updated");
        expect(complaintPage.returnLocationZip()).toEqual("zip1", "Location zip is not updated");
    });

    it('should create new complaint and add/delete tag', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Tags");
        complaintPage.addSugestedTag(Objects.timetrackingPage.data.tagname);
        complaintPage.deleteTag();

    });

    it('should verify if complaint can be added as reference to itself', function() {

        complaintPage.clickModuleComplaints();

        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        complaintPage.addReferenceAsItself(caseid);
    });

    it('should create two complaints and add one as reference to the anotheerone', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickFirstTopElementInList().addReference(caseid);
        expect(complaintPage.returnReferenceNumber()).toEqual(caseid, "Complaint id on added reference is not correct");
        expect(complaintPage.returnReferenceTitle()).toEqual("ComplaintTitle", "Complaint title on added reference is not correct");
        expect(complaintPage.returnReferenceModified()).toEqual(utils.returnToday("/"), "Complaint modified date in added reference is not correct");
        expect(complaintPage.returnReferenceType()).toEqual("COMPLAINT", "Reference type in added reference is not correct");
        expect(complaintPage.returnReferenceStatus()).toEqual(Objects.casepage.data.referenceStatusDraft, "Status in added reference is not correct");
    });

    it('should  click new complaint button to create new complaint', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickNewComplaintBtn();
        complaintPage.switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.titleComplaint);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickFirstTopElementInList();
        expect(complaintPage.returnComplaintsTitle()).toEqual(Objects.complaintPage.data.titleComplaint, "Title on added complaint is not correct");
    });

    it('should create new complaint and add task from tasks table verify the task column number', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickTasksLinkBtn();
        complaintPage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickComplaintTitleInTasks();
        complaintPage.clickTasksLinkBtn().waitForTasksTable();
        complaintPage.verifyTasksTableColumnsNumber();
    });

    it('should verify if the people intiator delete button is displayed', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.verifyIfInitiatorCanBeDeleted();
    });

    it('should verify that searching of Case id during close complaint is retrieving the data in the fields', function() {

        casePage.navigateToPage("Case Files").waitForCaseID();
        var caseid = casePage.getCaseId();
        var caseTitle = casePage.returnCaseTitle();
        var caseCreateDate = casePage.returnCreatedDate();
        var casePriority = casePage.returnPriority();
        complaintPage.clickModuleComplaints();
        complaintPage.clickCloseComplaint().switchToIframes().selectComplaintDisposition("Add to Existing Case").insertCaseNumber(caseid).clickSearchButton();
        expect(complaintPage.returnCaseTitle()).toEqual(caseTitle, "Filled case title after search is not correct");
        expect(complaintPage.returnCaseCreatedDate()).toEqual(caseCreateDate, "Filled created date after search is not correct");
        expect(complaintPage.returnCasePriority()).toEqual(casePriority, "Filled priority after search is not correct");
        complaintPage.switchToDefaultContent();
    });

    it('should create new Complaint by default assignee, claim it and verify the assignee', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes();
        complaintPage.submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickClaimButton();
        expect(complaintPage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Assignee is not correct after claim");
    });

    it('should create new Complaint by default assignee, claim it verify the assignee then uncalaim it and verify if the assignee is removed ', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes();
        complaintPage.submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickClaimButton();
        expect(complaintPage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Default assignee of creted complaint is not correct");
        complaintPage.clickUnclaimButton();
        expect(complaintPage.returnAssignee()).toEqual("", "The assignee name is displayed");
    });

    it('Verify if reader is displayed in participants table', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Participants");
        complaintPage.participantTable();
        expect(complaintPage.returnParticipantTypeForthRow()).toEqual("reader", "participant type is not correct in forth row");
        expect(complaintPage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name is not correct in forth row");
    });

    it('should create new complaint add/edit timeSheet and verify the time widget data in Complaints overview page', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes();
        complaintPage.submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            complaintPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            complaintPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            complaintPage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn();
            timeTrackingPage.clickEditTimesheetBtn();
            timeTrackingPage.switchToIframes();
            timeTrackingPage.selectTimesheetType("Complaint");
            timeTrackingPage.clickChargeCode();
            complaintPage.switchToDefaultContent();
            timeTrackingPage.searchForObject(text);
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("8");
            timeTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.verifyTimeWidgetData("7");

        });
    });

    //Add a document to document management

    it('Verify adding correspondence document', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addCorrespondence("complaint", "Notice of Investigation");
        complaintPage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), Objects.csaepage.data.approverSamuel, "1.0", "ACTIVE");
    });

});

describe('Tasks tests ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();
    });

    it('should create new task status active', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskspage.data.taskTitle);
        taskPage.insertSubject(Objects.taskpage.data.Subject);
        expect(taskPage.returnStartDateInput()).not.toBeTruthy();
        taskPage.insertDueDateToday();
        expect(taskPage.returnDueDateInput()).not.toBeTruthy();
        taskPage.insertPercentComplete(Objects.taskpage.data.percentCompleteInput).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle, "Task title is not correct");
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateActive, "Task status is not correct");

    });

    using([{ priority: "High", prioritySaved: Objects.taskspage.data.priorityHigh }, { priority: "Low", prioritySaved: Objects.taskspage.data.priorityLow }, { priority: "Expedite", prioritySaved: Objects.taskspage.data.priorityExpedite }], function(data) {
        it('should create new task with priority ' + data.priority, function() {
            taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), data.priority, Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea);
            expect(taskPage.returnDueDateText()).not.toBeTruthy();
            taskPage.clickSave();
            expect(taskPage.returnPriority()).toEqual(data.prioritySaved, "priority" + data.priority + " is not updated");
        });
    });

    it('should verify save button is disabled when subject is empty and due date', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnSaveButtonEnabled()).toBe(false);
    });

    it('should verify save button disabled when percent is empty', function() {

        taskPage.clickNewButton().clickTaskButton().clearPercentInput();
        expect(taskPage.returnSaveButtonEnabled()).toBe(false);
    });

    it('should create new task with notes verify subject, assignee, start date, note', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        expect(taskPage.returnTaskSubject()).toEqual(Objects.taskpage.data.Subject, "task subject is not correct");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.assigneeSamuel, "task assignee is not correct");
        expect(taskPage.returnInsertedStartDate()).toEqual(utils.returnToday("/"), "task inserted date is not correct");
        expect(taskPage.returnInsertedDueDate()).toEqual(utils.returnToday("/"), "task due date is not correct");
        expect(taskPage.returnPercent()).toEqual(Objects.taskpage.data.percentCompleteInput, "task percent is not correct");
        taskPage.clickDetailsLink();
        taskPage.validateDetailsTextArea(Objects.taskspage.data.notesTextArea, "notes text area value is not correct");
    });

    it('should create new task click delete button and verify task state', function() {

        taskPage.clickModuleTasks();
        taskPage.clickDeleteButton();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateDelete, "Task status is not changed into deleted after deleting");
    });

    it('should create new task with different user', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskData(Objects.taskpage.data.searchUser, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Expedite", Objects.taskpage.data.percentCompleteInput, Objects.taskspage.data.notesTextArea).clickSave();
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, "Task assignee is not correct");
    });

    it('should create new task add link verify', function() {

        taskPage.clickNewButton().clickTaskButton().insertTaskDataLinkNote(Objects.taskspage.data.assigneeSamuel, Objects.taskpage.data.Subject, utils.returnToday("/"), utils.returnToday("/"), "Low", Objects.taskpage.data.percentCompleteInput, Objects.taskpage.data.linkInputText, Objects.taskpage.data.linkInputUrl).clickSave();
        expect(taskPage.returnTasksTitle()).toEqual(Objects.taskpage.data.tasksTitle, "Task title is not correct");
        taskPage.clickDetailsLink();
        taskPage.validateDetailsTextArea(Objects.taskpage.data.linkInputText, "details link is not correct");
    });

    it('should create new task click subscribe button verify if it is changed to unsubscribe', function() {

        taskPage.clickModuleTasks();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn, "Subscribe button text is not changed into unsubscribe after click on subscribe");

    });

    it('should create new task click unsubscribe button verify if it is changed to subscribe', function() {

        taskPage.clickModuleTasks();
        taskPage.clickSubscribeButton();
        expect(taskPage.returnUnsubscribeButtonText()).toEqual(Objects.taskspage.data.unsubscribeBtn, "Subscribe button text is not changed into unsubscribe after click on subscribe");
        taskPage.clickUnsubscribeButton();
        expect(taskPage.returnSubscribeButtonText()).toEqual(Objects.taskspage.data.subscribeBtn, "Unsubscribe button text is not changed into subscribe after click on unsubscribe");
    });

    it('should create new task and add note', function() {

        taskPage.clickModuleTasks();
        taskPage.clickNotesLink();
        expect(taskPage.returnNotesTableTitle()).toEqual(Objects.taskspage.data.notesTableTitle)
        taskPage.clickAddNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.notePopUpTitle);
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.notesTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        expect("Note name is not correct", taskPage.returnNoteName()).toEqual(Objects.taskspage.data.notesTextArea);
        expect("Created date of note is not correct", taskPage.returnNoteCreatedDate()).toEqual(utils.returnToday("/"));
        expect("Supervisor is not correct", taskPage.returnNoteAuthor()).toEqual(Objects.taskspage.data.supervisor);
    });

    it('should create new task add note and delete the note', function() {

        taskPage.clickModuleTasks();
        taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickDeleteNoteButton();
        expect(taskPage.addedNoteNameIsPresent()).toBe(false, 'The note is not deleted');

    });
    it('should create new task add note and edit the note', function() {

        taskPage.clickModuleTasks();
        taskPage.clickNotesLink().clickAddNoteButton().insertNoteFromOverviewTab(Objects.taskspage.data.noteTextArea);
        expect(taskPage.addedNoteNameIsPresent()).toBe(true, 'Added note does not exist in the grid');
        taskPage.clickEditNoteButton();
        expect(taskPage.returnNotePopUpTitle()).toEqual(Objects.taskspage.data.noteTitleEditRecord, "Note pop up title is not correct");
        taskPage.insertNoteFromOverviewTab(Objects.taskspage.data.noteText);
        expect(taskPage.returnNoteName()).toEqual(Objects.taskspage.data.noteText, 'The note is not updated');

    });

    using([{ priority: "High", prioritySaved: Objects.taskspage.data.priorityHigh }, { priority: "Expedite", prioritySaved: Objects.taskspage.data.priorityExpedite }], function(data) {
        it('should create new task and edit priority to ' + data.priority, function() {

            taskPage.clickModuleTasks();
            taskPage.editPriority(data.priority);
            expect(taskPage.returnPriority()).toEqual(data.prioritySaved, "Priority is not updated");
        });
    });

    it('should create new task and edit percent of completition', function() {

        taskPage.clickModuleTasks();
        taskPage.editPercent(Objects.taskspage.data.percentCompletitionInput);
        expect(taskPage.returnPercent()).toEqual(Objects.taskspage.data.percentCompletitionInput, 'Percent is not updated');

    });
    it('should create new task and edit task subject', function() {

        taskPage.clickModuleTasks();
        taskPage.editTaskSubject(Objects.taskspage.data.taskSubjectInput);
        expect(taskPage.returnTaskSubject()).toEqual(Objects.taskspage.data.taskSubjectInput, 'Task subject is not updated');

    });
    it('should create new task and edit assignee from samuel to ann', function() {

        taskPage.clickModuleTasks();
        taskPage.editAssignee("Ann Administrator");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');
    });
    it('should create new task change assignee and verify is button complete and delete are not displayed', function() {

        taskPage.clickModuleTasks();
        taskPage.editAssignee("Ann Administrator");
        expect(taskPage.returnAssignee()).toEqual(Objects.taskspage.data.administrator, 'The assignee is not updated');
        expect(taskPage.completeButtonIsPresent()).toBe(false, 'Complete button should not be displyed');
        expect(taskPage.deleteButtonIsPresent()).toBe(false, 'Delete  button should not be displyed');

    });
    it('should create new task add tag and verify added tag', function() {

        taskPage.clickModuleTasks();
        taskPage.clickTagsLink();
        expect(taskPage.returnTagsTableTittle()).toEqual(Objects.taskspage.data.tagsTableTitle);
        taskPage.clickAddTagButton();
        expect(taskPage.returnAddTagPopUpTitle()).toEqual(Objects.taskspage.data.addTagPopUpTitle);
        browser.driver.actions().mouseDown(taskPage.tagTextArea).click().sendKeys(Objects.taskspage.data.tagTextArea).perform();
        taskPage.clickSaveTagButton();
        expect(taskPage.returnTagName()).toEqual(Objects.taskspage.data.tagTextArea, 'Created tag name is wrong');
        expect(taskPage.returnTagCreatedDate()).toEqual(today, 'Created tag date is wrong');
        expect(taskPage.returnTagCreatedBy()).toEqual(Objects.taskspage.data.supervisor, 'Created tag by is wrong');
    });
    it('should create new task add tag and delete added tag', function() {

        taskPage.clickModuleTasks();
        taskPage.clickTagsLink();
        expect(taskPage.returnTagsTableTittle()).toEqual(Objects.taskspage.data.tagsTableTitle, "Tags table title is not correct");
        taskPage.clickAddTagButton();
        expect(taskPage.returnAddTagPopUpTitle()).toEqual(Objects.taskspage.data.addTagPopUpTitle, "Add tag pop up title is not correct");
        browser.driver.actions().mouseDown(taskPage.tagTextArea).click().sendKeys(Objects.taskspage.data.tagTextArea).perform();
        taskPage.clickSaveTagButton();
        taskPage.clickDeleteTagButton();
        expect(taskPage.returnTagNameisPresent()).toBe(false, 'The tag is not deleted');
    });
    it('should create new task add text task details verify if it saved', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        taskPage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        taskPage.clickSaveDetailsButton();
        taskPage.clickRefreshButton();
        taskPage.validateDetailsTextArea(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');
    });

    it('should create new task add link from task details', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        taskPage.clickInsertLinkInDetails();
        expect(taskPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle, "Insert link title in details is not correct");
        taskPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        taskPage.validateDetailsTextAres(Objects.taskspage.data.insertLinkText, 'The link is not added');
        taskPage.clickSaveDetailsButton();
        taskPage.validateDetailsTextArea(Objects.taskspage.data.insertLinkText, 'The link is not added');

    });
    it('should navigate to task page and click on header image and verify if redirects to home page', function() {

        taskPage.clickNewButton().clickTaskButton();
        expect(taskPage.returnTaskTitle()).toEqual(Objects.taskspage.data.taskTitle, "");
        taskPage.clickHeaderImageLink();
        expect(taskPage.returnDashboardTitle()).toEqual(Objects.taskspage.data.dashboardTitle, 'Header image does not redirects to home page');

    });
    it('should create new task navigate to attachments section add png document', function() {

        taskPage.clickModuleTasks();
        expect(taskPage.returnAttachementsTableTitle()).toEqual(Objects.taskspage.data.attachmentsTableTitle, 'Attachments table title is wrong');
        taskPage.clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadPng();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentImageTitle, 'Added document name is wrong, or document is not added');
    });
    it('should create new task navigate to attachments section add docx document', function() {

        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadDocx();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentWordTitle, 'Added document name is wrong');
    });

    it('should create new task navigate to attachemnts section add xlsx document', function() {

        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadXlsx();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentExcelWordTitle, 'Added document name is wrong');
    });

    it('should create new task navigate to attachemnts section add pdf document', function() {


        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewDocument().clickOtherDocument();
        utils.uploadPdf();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentPdfTitle, 'Added document name is wrong');

    });
    it('should create new task and add new folder in documents section', function() {

        taskPage.clickModuleTasks();
        taskPage.clickAttachmentsLink().clickRootFolder();
        utils.mouseMoveToRoot();
        taskPage.clickNewFolder();
        taskPage.insertDocumentTitle(Objects.taskspage.data.documentTitleInput);
        taskPage.clickAttachmentTitle();
        expect(taskPage.returnDocumentTitle()).toEqual(Objects.taskspage.data.documentTitleInput, "Document title is not corrrect");
    });

    it('should create new task and edit the start date', function() {

        taskPage.clickModuleTasks();
        //this line was added to verify that issue https://project.armedia.com/jira/browse/AFDP-2797 does not exist any more
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Attachments");
        taskPage.editStartDate(utils.returnToday("/"));
        expect(taskPage.returnStartDateInput()).toEqual(today, "Start date is not updated");

    });

    it('should create new task and edit due date', function() {

        taskPage.clickModuleTasks();
        taskPage.editDueDate(utils.returnToday("/"));
        expect(taskPage.returnInsertedDueDate()).toEqual(today, "Due date is not updated");

    });

    it('should create new task,add picture and verify in details', function() {

        taskPage.clickNewButton().clickTaskButton().insertSubject(Objects.taskpage.data.Subject).insertDueDateToday();
        taskPage.clickPictureButton();
        taskPage.clickChooseFileButton();
        utils.uploadPng();
        expect(taskPage.returnNotesTextArea()).not.toBeTruthy();
        taskPage.clickSave();
        taskPage.clickDetailsLink();
        expect(taskPage.returnDetailsTextArea()).not.toBeTruthy();
    });

    it('should create new task, verify checkout, cancel editing ', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Attachments");
        taskPage.rightClickRootFolder();
        taskPage.addDocument("Notice of Investigation");
        taskPage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");
        expect(taskPage.lockIconIsPresent()).not.toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).not.toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).not.toBeTruthy();
        taskPage.clickCheckOut();
        expect(taskPage.lockIconIsPresent()).toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).not.toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).toBeTruthy();
        taskPage.clickCancelEditing();
        expect(taskPage.lockIconIsPresent()).not.toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).not.toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).not.toBeTruthy();
    });

    it('should create new task, verify checkout, checkin', function() {

        taskPage.clickModuleTasks();
        taskPage.clickExpandFancyTreeTopElementAndSubLink("Attachments");
        taskPage.rightClickRootFolder();
        taskPage.addDocument("Notice of Investigation");
        taskPage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");
        taskPage.rightClickDocument().clickCheckOut();
        expect(taskPage.lockIconIsPresent()).toBeTruthy();
        taskPage.clickCheckin();
        expect(taskPage.lockIconIsPresent()).not.toBeTruthy();
        taskPage.rightClickDocument();
        expect(taskPage.validateChekoutEnabled()).toBeTruthy();
        expect(taskPage.validateCheckinEnabled()).not.toBeTruthy();
        expect(taskPage.validateCancelEditingEnabled()).not.toBeTruthy();

    });

});

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
        expect(timeTrackingPage.returnTimeTrackingDate()).toEqual(utils.returnTimeTrackingWeek(), "Time tracking period is not correct");
        timeTrackingPage.switchToDefaultContent();
    });

    it('should navigate to timesheet page and verify if save button can be clicked without selected charge code', function() {

        timeTrackingPage.clickNewButton();
        timeTrackingPage.navigateToTimeTrackingPage();
        timeTrackingPage.switchToIframes();
        timeTrackingPage.selectTimesheetType("Case");
        expect(timeTrackingPage.returnchargeCodeAlertMessage()).toEqual("You can't leave this empty: Charge Code");
        timeTrackingPage.switchToDefaultContent();
    });

    it('should navigate to time tracking page and add sugested tag', function() {

        timeTrackingPage.clickModuleTimeTracking();
        timeTrackingPage.clickLastElementInTreeData();
        timeTrackingPage.clickTagsLinkBtn();
        timeTrackingPage.addSugestedTag(Objects.timetrackingPage.data.tagname);
        expect(timeTrackingPage.returnTagName()).toEqual(Objects.timetrackingPage.data.tagname, "Tag name is not correct");
        expect(timeTrackingPage.returntagCratedDate()).toEqual(utils.returnToday("/"), "Tag created date is not correct");
        expect(timeTrackingPage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "tag created by is not correct");
        timeTrackingPage.deleteTag();
    });
});


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

describe('Subscriptions page tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    using([{ parentType: "Task", expected: "TASK" }, { parentType: "Complaint", expected: "COMPLAINT" },
        { parentType: "Case File", expected: "CASE_FILE" }
    ], function(data) {

        it('should select Task as parent type and verift it the result table ', function() {

            subscriptionPage.clickSubcriptionsModule();
            subscriptionPage.selectSubscription(data.parentType);
            subscriptionPage.clickParentTypeSort();
            expect(subscriptionPage.returnResultParentType()).toEqual(data.expected, "result parent is not correct for " + data.parentType);

        });

    });

    it('should search for case and  verify it in the result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.searchForSubscription("case")
        expect(subscriptionPage.returnResultParentType()).toEqual("CASE_FILE", "result parent is not correct for case");
    });

    it('should select previous week and verify the modified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Week")
        subscriptionPage.clikmodifiedBySort();
        subscriptionPage.returnModifiedByWeek();
    });

    it('should select previous month and verify the modified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Month")
        subscriptionPage.clikmodifiedBySort();
        subscriptionPage.returnModifiedByMonth();

    });

    it('should select previous Year and verify the modified date in result table', function() {

        subscriptionPage.clickSubcriptionsModule();
        subscriptionPage.selectSubscription("Previous Year")
        subscriptionPage.clikmodifiedBySort();
        subscriptionPage.returnModifiedByYear();

    });
});

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
            complaintPage.verifyIfWidgetIsDisplayed(data.expected, data.widget, data.expected + "widget is not displayed in complaint overview page");
        });

        it('should disabled the Complaints wdgets in preference page and verify it in the Complaints  overview page ', function() {

            complaintPage.navigateToPreferencePage();
            preferencesPage.disabledWidget(data.preference, data.widget);
            complaintPage.clickModuleComplaints();
            complaintPage.waitForOverView();
            complaintPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget, data.expected + "widget is displayed in complaint overview page");
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
            casePage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget, data.expected + "widget is displayed in cases overview page");
        });

        it('should enable the Cases widgets in preference page and verify it in the cases overview page ', function() {

            casePage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            casePage.clickModuleCasesFiles();
            casePage.waitForOverView();
            casePage.verifyIfWidgetIsDisplayed(data.expected, data.widget, data.expected + "widget is not displayed in cases overview page");
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
            taskPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget, data.expected + "widget is displayed in tasks overview page");
        });

        it('should enable the Tasks widgets in preference page and verify it in the tasks overview page ', function() {

            taskPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            taskPage.clickModuleTasks();
            taskPage.waitForOverView();
            taskPage.verifyIfWidgetIsDisplayed(data.expected, data.widget, data.expected + "widget is not displayed in tasks overview page");
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
            costTrackingPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget, data.expected + "widget is displayed in cases overview page");
        });

        it('should enable the Cost Tracking widgets in preference page and verify it in the cases overview page ', function() {

            costTrackingPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            costTrackingPage.clickModuleCostTracking();
            costTrackingPage.waitForOverView();
            costTrackingPage.verifyIfWidgetIsDisplayed(data.expected, data.widget, data.expected + "widget is not displayed in cases overview page");
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
            timeTrackingPage.verifyIfWidgetIsNotDisplayed(data.expected, data.widget, data.expected + "widget is dispalyed in cases overview page");
        });

        it('should enable the Time Tracking widgets in preference page and verify it in the cases overview page ', function() {

            timeTrackingPage.navigateToPreferencePage();
            preferencesPage.enableWidget(data.preference, data.widget);
            timeTrackingPage.clickModuleTimeTracking();
            timeTrackingPage.waitForOverView();
            timeTrackingPage.verifyIfWidgetIsDisplayed(data.expected, data.widget, data.expected + "widget is not displayed in cases overview page");
        });
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

    it('should select previous month and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Month")
        notificationPage.clickSortModifiedBy();
        notificationPage.returnModifiedByMonth();

    });

    it('should select previous Year and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Year")
        notificationPage.clickSortModifiedBy();
        notificationPage.returnModifiedByYear();
    });

    it('should select previous week and verify it the result table', function() {

        notificationPage.navigateToPage("Notifications");
        notificationPage.selectNotification("Previous Week")
        notificationPage.clickSortModifiedBy();
        notificationPage.returnModifiedByWeek();
    });

    using([{ parentType: "File", expected: "FILE" }, { parentType: "Note", expected: "NOTE" },
        { parentType: "Task", expected: "TASK" }, { parentType: "Complaint", expected: "COMPLAINT" },
        { parentType: "Case File", expected: "CASE_FILE" }, { parentType: "Participant", expected: "PARTICIPANT" }
    ], function(data) {

        it('should select parent type and verify the object type in the result table ', function() {

            notificationPage.navigateToPage("Notifications");
            notificationPage.selectNotification(data.parentType)
            notificationPage.clicksortObjectType();
            expect(notificationPage.returnObjectType()).toEqual(data.expected, "Object type is not correct in result table");
        });

    });

    //Click on the Notification Module and verify that description date/time is equal to modified column date/time

    it('should verify that description date/time is equal to modified column date/time', function() {
        notificationPage.navigateToPage("Notifications");
        expect(notificationPage.returnDescription()).toContain(notificationPage.returnModifiedBy(), "In description of notification modified date is not correct");
    });

});


describe('Validate that group in which is logged in user is in authorized group', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.adminuser.username, Objects.loginpage.data.adminuser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();

    });

    using([{ reportName: "Case Summary Report" }, { reportName: "ComplaintDispositionCount" }, { reportName: "Complaint Report" }], function(data) {

        it('should validate that logged in user group is in authorized groups in ' + data.reportName + 'configuration', function() {
            adminPage.navigateToPage("Admin").clickSubLink("Reports Configuration").ChooseListBoxValue(data.reportName);
            var groups = utils.readGroupsFromJson("samuel-acm");
            for (var i in groups) {
                expect(adminPage.returnAuthorized()).toContain(groups[i], "Samuel is not authorized to view " + data.reportName);
            }
        });
    });

    it('should validate that LDAP configuration for Armedia is set', function() {
        adminPage.navigateToPage("Admin").clickSubLink("LDAP Configuration");
        expect(adminPage.returnArmediaDirectoryName()).toEqual(Objects.adminPage.data.ArmediaDirectoryName, "LDAP Directory name is not correct");
        expect(adminPage.returnArmediaLDAPUrl()).toEqual(Objects.adminPage.data.ArmediaLDAPUrl, "LDAP Directory url is not correct");
    });

    //have to be checked what is Role what is group and how they are connected in order to test this

    // using([{widgetName: "News Widget"}, {widgetName: "My Tasks Widget"}, {widgetName: "My Cases Widget"}, {widgetName: "My Complaints Widget"}, {widgetName: "New Complaints"}, {widgetName: "Weather Widget"}, {widgetName: "Team Workload"}, {widgetName: "Cases By Status"} ], function(data){
    //     it('should validate that samuel supervisor is enabled to add ' + data.widgetName, function () {
    //         adminPage.navigateToPage("Admin").clickSubLink("Dashboard Configuration").ChooseListBoxValue(data.widgetName);
    //         var groups = utils.readGroupsFromJson("samuel-acm");
    //         for (var i in groups)
    //         {
    //             expect(adminPage.returnAuthorized()).toContain(groups[i]);
    //         }
    //     });
    // });

    it('should validate that report of investigation form is configured correctly', function() {
        adminPage.navigateToPage("Admin").clickSubLink("Form Configuration");
        expect(adminPage.returnCaseFormName()).toEqual(Objects.adminPage.data.formName, "Report of investigation form name is not correct");
        expect(adminPage.returnCaseApplicationName()).toEqual(Objects.adminPage.data.applicationName, "Report of investigation case application name is not correct");
        expect(adminPage.returnCaseDescription()).toEqual(Objects.adminPage.data.description, "Report of investigation case description is not correct");
        expect(adminPage.returnCaseTargetFile()).toEqual(Objects.adminPage.data.caseTarget, "Report of investigation case target is not correct");
        expect(adminPage.returnComplaintFormName()).toEqual(Objects.adminPage.data.formName, "Report of investigation complaint form name is not correct");
        expect(adminPage.returnComplaintApplicationName()).toEqual(Objects.adminPage.data.applicationName, "Report of investigation complaint application name is not correct");
        expect(adminPage.returnComplaintDescription()).toEqual(Objects.adminPage.data.description, "Report of investigation complaint description is not correct");
        expect(adminPage.returnComplaintTargetFile()).toEqual(Objects.adminPage.data.complaintTarget, "Report of investigation complaint target file is not correct");
    });

    it('should validate workflow configuration for complaint rewiew and approval process and ACM doc approval', function() {
        adminPage.navigateToPage("Admin").clickSubLink("Workflow Configuration");
        expect(adminPage.returnComplaintBussinessProcessName()).toEqual(Objects.adminPage.data.complaintBussinesProcessName, "Complaint business process name is not correct in workflow configuration");
        expect(adminPage.returnComplaintBPDescription()).toEqual(Objects.adminPage.data.complaintBPDescription, "Complaint business process description is not correct");
        expect(adminPage.returnComplaintBPModified()).toEqual(Objects.adminPage.data.complaintBPModified, "Complaint business process modified is not correct");
        expect(adminPage.returnComplaintBPAuthor()).toEqual(Objects.adminPage.data.complaintBPAuthor, "Complaint business process author is not correct");
        expect(adminPage.returnDocApprovalBussinesProcessName()).toEqual(Objects.adminPage.data.docApprovalBussinesProcessName, "Document approval business process name is not correct");
        expect(adminPage.returnDocApprovalBPDescription()).toEqual(Objects.adminPage.data.docApprovalBPDescriptionc, "Document approval business process description is not correct");
        expect(adminPage.returnDocApprovalBPModified()).toEqual(Objects.adminPage.data.docApprovalBPModified, "Document approval business process modified is not correct");
        expect(adminPage.returnDocApprovalBPAuthor()).toEqual(Objects.adminPage.data.docApprovalBPAuthor, "Document approval business process author ia not correct");
    });

});

describe('audit tests', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {
        loginPage.Logout();

    });

    it('should navigate to complaints and verify that complaint is displayed in audit complaint report ', function() {

        complaintPage.navigateToPage("Complaints").waitForComplaintID();
        var createdDate = complaintPage.returnCreatedDate();
        var complaintId = complaintPage.getComplaintId();
        complaintPage.navigateToPage("Audit");
        auditPage.runReport("Complaints", complaintId, utils.returnToday("/"), utils.returnToday("/"));
        auditPage.switchToAuditframes();
        auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
        auditPage.validateAuditReportValues(utils.returnToday("/"), Objects.taskspage.data.assigneeSamuel, "Find Complaint", "success", complaintId, "COMPLAINT");
        auditPage.switchToDefaultContent();

    });

    //this tests should be changed after resolution of issue with filtering id, currently filtering by id does not work
    it('should navigate to case files, view one case file and verify that during auditing only one record is generated', function() {

        casePage.navigateToPage("Case Files").waitForCaseID();
        var caseid = casePage.getCaseId();
        casePage.navigateToPage("Audit");
        auditPage.runReport("Case Files", "", utils.returnToday("/"), utils.returnToday("/"));
        auditPage.switchToAuditframes();
        auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
        auditPage.validateAuditReportValues(utils.returnToday("/"), Objects.taskspage.data.assigneeSamuel, "Case Viewed", "success", caseid, "CASE_FILE");
        expect(auditPage.returnObjectIdValue()).not.toEqual(auditPage.returnSecondRowObjectIdValue(), "There is not more that 1 record in audit report for one id");
        auditPage.switchToDefaultContent();

    });

    it('should navigate to complaints, view one complaint and verify that during auditing only one record is generated', function() {

        complaintPage.navigateToPage("Complaints").waitForComplaintID();
        var complaintId = complaintPage.getComplaintId();
        complaintPage.navigateToPage("Audit");
        auditPage.runReport("Complaints", "", utils.returnToday("/"), utils.returnToday("/"));
        auditPage.switchToAuditframes();
        auditPage.validateAuditReportTitles(Objects.auditPage.data.auditReportColumn1Title, Objects.auditPage.data.auditReportColumn2Title, Objects.auditPage.data.auditReportColumn3Title, Objects.auditPage.data.auditReportColumn4Title, Objects.auditPage.data.auditReportColumn5Title, Objects.auditPage.data.auditReportColumn6Title, Objects.auditPage.data.auditReportColumn7Title);
        auditPage.validateAuditReportValues(utils.returnToday("/"), Objects.taskspage.data.assigneeSamuel, "Find Complaint", "success", complaintId, "COMPLAINT");
        expect(auditPage.returnObjectIdValue()).not.toEqual(auditPage.returnSecondRowObjectIdValue(), "There is not more that 1 record in audit report for one id");
        auditPage.switchToDefaultContent();

    });
});
