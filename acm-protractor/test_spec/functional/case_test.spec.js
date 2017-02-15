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

    it('should create new case and verify case type', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle, "Case title is not correct in new added case");
        casePage.clickModuleCasesFiles();
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType, "Case type is not correct in new added case");
    });

    using([{ priority: "High", prioritySaved: Objects.casepage.data.priorityHigh }, {
        priority: "Medium",
        prioritySaved: Objects.casepage.data.priorityMedium
    }, { priority: "Expedite", prioritySaved: Objects.casepage.data.priorityExpedite }], function(data) {
        it('should create new case and edit the priority to ' + data.priority, function() {

            casePage.clickModuleCasesFiles();
            casePage.editPriority(data.priority);
            expect(casePage.returnPriority()).toEqual(data.prioritySaved, "Priority is not updated");
        });
    });

    it('should  create new case and edit assgined' , function() {
        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForAssignee();
        casePage.editAssignee("samuel-acm").waitForAssignee();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel, "Assignee is not updated");
    });

    it('should  add/edit note', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.editNote(Objects.casepage.data.editnote);
        expect(casePage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is not updated");
    });

    it('should  add add hoc task from tasks table and verify the task', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDate(utils.returnToday("/")).clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Tasks").waitForTasksTable();
        casePage.validateTaskTableValue("Ad hoc task", "Title", Objects.taskpage.data.Subject);
        casePage.validateTaskTableValue("Ad hoc task", "Assignee", Objects.casepage.data.assigneeSamuel);
        casePage.validateTaskTableValue("Ad hoc task", "Created", utils.returnToday("/"));
        casePage.validateTaskTableValue("Ad hoc task", "Priority", Objects.casepage.data.priorityMedium);
        casePage.validateTaskTableValue("Ad hoc task", "Due", utils.returnToday("/"));
        casePage.validateTaskTableValue("Ad hoc task", "Status", "ACTIVE");
    });

    it('should  verify the people initiator', function() {

        casePage.clickModuleCasesFiles();
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

    it('should create new case and add tag', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.clickTagsLinkBtn();
        casePage.addTag(Objects.casepage.data.tagname);
        expect(casePage.returnTagName()).toEqual(Objects.casepage.data.tagname, "Name is not correct on added tag");
        expect(casePage.returntagCratedDate()).toEqual(utils.returnToday("/"), "Created date is not correct on added tag");
        expect(casePage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel, "Created by is not correct on added tag");
    });

    it('should create new case and add/delete tag', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickTagsLinkBtn();
        casePage.addTag(Objects.casepage.data.tagname);
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
            casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
            casePage.clickTasksLinkBtn().waitForTasksTable();
            expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle, "Automated task title does not contain task title");
            casePage.clickTaskTitle();
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


    it('should create new case and click edit button verify updated case title and case type', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.clickEditCaseBtn();
        casePage.switchToIframes().submitGeneralInformation(Objects.casepage.data.caseName, "Benefits Appeal").clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle()
        casePage.clickFirstTopElementInList();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseName, "Case name is not updated");
        expect(casePage.returnCaseType()).toEqual("Benefits Appeal", "Case type is not updated");
    });

    it('should create two cases and put one as reference to the anotherone', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.waitForCaseTitle();
        casePage.clickFirstTopElementInList().addReference(caseid);
        expect(casePage.returnReferenceNumber()).toEqual(caseid, "id is not correct in column on added case as reference");
        expect(casePage.returnReferenceTitle()).toEqual(Objects.casepage.data.referenceCaseName, "name is not correct in column on added case as reference");
        expect(casePage.returnReferenceModified()).toEqual(utils.returnToday("/"), "modified date is not correct in column on added case as reference");
        expect(casePage.returnReferenceType()).toEqual(Objects.casepage.data.referenceType, "type is not correct in column on added case as reference");
        expect(casePage.returnReferenceStatus()).toEqual(Objects.casepage.data.referenceStatusDraft, "status is not correct in column on added case as reference");

    });

    it('should create case and verify if the same case be added as reference to itself', function() {

        casePage.clickModuleCasesFiles();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.addReferenceAsItself(caseid);

    });

    it('should  edit the due date', function() {

        casePage.clickModuleCasesFiles();
        casePage.editDueDate();
        expect(casePage.returnDueDate()).toEqual(utils.returnToday("/"), "Due date is not updated");
    });

    it('should create new case and verify the history table', function() {

        casePage.clickModuleCasesFiles();
        casePage.historyTable();
        expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent, "History event name for added case is not correct ");
        expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"), "History date for added case is not correct");
        expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel, "History user for added case is not correct");
    });

    it('should create new case and verify adding correspondence document', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElement();
        casePage.rightClickRootFolder().addCorrespondence("case", "Notice of Investigation");
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn1, "Notice of Investigation");
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn2, ".docx");
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn3, "Notice Of Investigation");
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn6, userPage.returnUserNavigationProfile());
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn7, "1.0");
        complaintPage.validateDocGridValue("Notice of Investigation", Objects.basepage.data.docGridColumn8, "ACTIVE");

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
            casePage.chnageCaseSubmit();
            casePage.clickTasksLinkBtn().waitForTasksTable();
            expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
            casePage.clickTaskTitle();
            taskPage.clickApproveBtn();
            taskPage.clickCaseTitleInTasks();
            casePage.clickRefreshBtn();
            casePage.caseTitleStatus("CLOSED");
            casePage.clickReinvesigateBtn();
            casePage.switchToIframes().submitGeneralInformation("New", "Agricultural");
            casePage.clickSubmitBtn();
            casePage.waitForCaseTitle();
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

    it('should create new case and add participant owner', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Owner", Objects.casepage.data.approverSamuel);
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant owner");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor", "Participant name in second row is not correct after adding participant owner");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "Participant type in third row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in third row is not correct after adding participant owner");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "Participant type in forth row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name in forth row is not correct after adding participant owner");

    });

    it('should craete new case and verify the assighnee by default', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant owner");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor", "Participant name in second row is not correct after adding participant owner");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group", "Participant type in third row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in third row is not correct after adding participant owner");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader", "Participant type in forth row is not correct after adding participant owner");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor", "Participant name in forth row is not correct after adding participant owner");
    });

    using([{ participant: "Collaborator", participantSaved: "collaborator" }, {
        participant: "Follower",
        participantSaved: "follower"
    }, { participant: "Reader", participantSaved: "reader" }, {
        participant: "Co-Owner",
        participantSaved: "co-owner"
    }, { participant: "Supervisor", participantSaved: "supervisor" }, {
        participant: "No Access",
        participantSaved: "no access"
    }, { participant: "Approver", participantSaved: "approver" }], function(data) {

        it('should create new case  select ' + data.participant + ' from paricipant tab and verify it in the paricipants table', function() {

            casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
            casePage.clickNextBtn();
            casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
            casePage.selectParticipant(data.participant, "Sally");
            casePage.switchToIframes();
            casePage.clickSubmitBtn();
            casePage.switchToDefaultContent();
            casePage.waitForCasesPage();
            casePage.participantTable();
            expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is no correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee", "Participant type in second row is not correct after adding participant  " + data.participant);
            expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator", "Participant name in second row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeThirdRow()).toEqual(data.participantSaved, "Participant type in third row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameThirdRow()).toEqual("Sally Supervisor", "Participant name in third row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group", "Participant type in forth row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in forth row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader", "Participant type in fifth row is not correct after adding participant " + data.participant);
            expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor", "Participant name in fifth row is not correct after adding participant " +data.participant);
        });
    });

    it('should create new case and verify adding new Report of Investigation document', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.rightClickRootFolder().addDocument("Report of Investigation");
        casePage.switchToIframes().submitReportOfInvestigation(Objects.basepage.data.reportTitle, Objects.taskspage.data.assigneeSamuel);
        casePage.switchToDefaultContent();
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn1, "Report of Investigation");
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn2, ".pdf");
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn3, "Report of Investigation");
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn6, Objects.taskspage.data.assigneeSamuel);
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn7, "1.0");
        casePage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn8, "ACTIVE");
    });

    it('should create new case and select Approver from participant tab and verify it in the paricipant table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Approver", "Ann");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCasesPage();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*", "Participant type in first row is not correct after adding approver from participant");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*", "Participant name in first row is not correct after adding approver from participant");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("approver", "Participant type in second row is not correct after adding approver from participant");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator", "Participant name in second row is not correct after adding approver from participant");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("assignee", "Participant type in third row is not correct after adding approver from participant");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("Ann Administrator", "Participant name in third row is not correct after addinf approver from participant");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group", "Participant type in forth row is not correct after adding approver from participant");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV", "Participant name in forth row is not correct after adding approver from participant");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader", "Participant type in fifth row is not correct after adding approver from participant");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor", "Participant name in fifth row is not correct after adding approver from participant");
    });

    it('should  verify if special type can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickSpecialTypeDeleteBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfAssigneeCanBeDeleted();

    });

    it('should  verify if assignee can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickDeleteAsigneeBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfSpecialTypeCaneBeDeleted();

    });

    it('should verify if owning  can be deleted', function() {

        casePage.clickModuleCasesFiles();
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
        casePage.moveToTab().clickDocViewNotesLink().submitNote(Objects.basepage.data.note);
        expect(casePage.returnSavedNoteInGrid()).toEqual(Objects.basepage.data.note, "Note is not succcessfully saved in document viewer");

    });

    it('should edit assignee from participant table', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickEditAssigneeBtn();
        casePage.editAssigneeInParticipantTable("Samuel Supervisor");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor", "Edited assignee is not saved");
    });

    it('should verify if the people intiator delete button is displayed', function() {

        casePage.clickModuleCasesFiles();
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

    it('should verify if another assignee can be added  from participant table', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.addParticipantFromParticipantTable("Assignee", "Samuel Supervisor");
        casePage.verifyTheNotificationMessage("Only one assignee is allowed");
    });

    it('should verify replace of document and return to previous version', function() {

        casePage.clickModuleCasesFiles();
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

    it('should verify adding note in document viewer in cases', function () {

        casePage.clickModuleCasesFiles();
        casePage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
        casePage.moveToTab().clickDocViewNotesLink().submitNote(Objects.basepage.data.note);
        expect(casePage.returnSavedNoteInGrid()).toEqual(Objects.basepage.data.note, "Note is not saved in document viewer");

    });

});
