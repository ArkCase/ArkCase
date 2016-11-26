var logger = require('../log');
var casePage = require('../Pages/case_page.js');
var userPage = require('../Pages/user_profile_page.js');
var authentication = require('../authentication.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var utils = require('../util/utils.js');
var userPage = require('../Pages/user_profile_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../Pages/time_tracking_page.js');


function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 60000);

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

        authentication.loginAsSupervisor();
        testAsync(done);

    });

    afterEach(function() {

        authentication.logout();

    });

    it('should create new case and verify case title', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle);
    });

    it('should create new case and verify case type', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseType();
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType);
    });
    it('should create new case and change case status to closed, verify the automated task in tasks table and approve', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatus("Closed");
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    it('should create new case and verify the priority filed', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForPriority();
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium);
    });

    it('should create new case and verify the created date', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCreatedDate();
        expect(casePage.returnCreatedDate()).toEqual(utils.returnToday("/"));

    });

    it('should create new case and edit the priority to High', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForPriority();
        casePage.editPriority('High');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityHigh);
    });

    it('should create new case and edit the priority to Medium', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForPriority();
        casePage.editPriority('Medium');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium);
    });

    it('should create new case and edit the priority to Medium', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForPriority();
        casePage.editPriority('Expedite');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityExpedite);
    });

    it('should create new case and edit the assignee from ann to samuel', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForAssignee();
        casePage.editAssignee("samuel-acm").waitForAssignee();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should craete new case add/delete note', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.deleteNote();
    });
    it('should create new case add new note and verify added note', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.editNote(Objects.casepage.data.editnote);
        expect(casePage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is not updated");
    });

    it('should create new case and add task from tasks table verify the task', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnTaskTitle()).toContain(Objects.taskpage.data.Subject);
        expect(casePage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
        expect(casePage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"));
        expect(casePage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium);
        expect(casePage.returnTaskTableDueDate()).toEqual(Objects.taskspage.datataskStateActive);
    });

    it('should create new case and verify the people initiator', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor);
        expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstName);
        expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastName);
    });

    it('should create new case add person and verify the added person', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addPerson(Objects.casepage.data.peopleTypeWitness, Objects.casepage.data.peopleFirstName, Objects.casepage.data.peopleLastName);
        expect(casePage.returnPeopleTypeSecondRow()).toEqual(Objects.casepage.data.peopleTypeWitness);
        expect(casePage.returnPeopleFirstNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleFirstName);
        expect(casePage.returnPeopleLastNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleLastName);

    });

    it('should create new case and  delete initiator', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.deletePerson();
    });

    it('should create new case and edit person initiator', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.editPerson(Objects.casepage.data.peopleTypeWitness, Objects.casepage.data.peopleFirstNameEdit, Objects.casepage.data.peopleLastNameedit);
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeWitness);
        expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstNameEdit);
        expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastNameedit);

    });

    it('should crete new case and add contact method ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        expect(casePage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodFacebook);
        expect(casePage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodFacebook);
        expect(casePage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(casePage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new case add contact method and delete it', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        casePage.deleteContactMethod();
    });

    it('should create new case and add contact method and edit it', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        casePage.editContactMethod(Objects.casepage.data.contactMethodEmail, Objects.casepage.data.contactMethodEmail);
        expect(casePage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodEmail);
        expect(casePage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodEmail);
        expect(casePage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(casePage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new case and add organization', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        expect(casePage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment);
        expect(casePage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment);
        expect(casePage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(casePage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new case add/delete organization', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        casePage.deleteOrganization();

    });

    it('should create new case add/edit organization', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        casePage.editOrganization(Objects.casepage.data.organizationTypeCorporation, Objects.casepage.data.organizationTypeCorporation);
        expect(casePage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation);
        expect(casePage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation);
        expect(casePage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(casePage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new case and add address', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        expect(casePage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeHome);
        expect(casePage.returnAddressStreet()).toEqual(Objects.casepage.data.street);
        expect(casePage.returnAddressCity()).toEqual(Objects.casepage.data.city);
        expect(casePage.returnAddressState()).toEqual(Objects.casepage.data.state);
        expect(casePage.returnAddressZip()).toEqual(Objects.casepage.data.zip);
        expect(casePage.returnaddressCountryValue()).toEqual(Objects.casepage.data.country);
        expect(casePage.returnAddressLastModified()).toEqual(utils.returnToday("/"));
        expect(casePage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new case and add/delete address', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        casePage.deleteAddress();
    });

    it('should create new case and add/edit', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        casePage.editAddress(Objects.casepage.data.addressTypeBusiness, Objects.casepage.data.editStreet, Objects.casepage.data.editCity, Objects.casepage.data.editState, Objects.casepage.data.editZip, Objects.casepage.data.editCountry);
        expect(casePage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeBusiness);
        expect(casePage.returnAddressStreet()).toEqual(Objects.casepage.data.editStreet);
        expect(casePage.returnAddressCity()).toEqual(Objects.casepage.data.editCity);
        expect(casePage.returnAddressState()).toEqual(Objects.casepage.data.editState);
        expect(casePage.returnAddressZip()).toEqual(Objects.casepage.data.editZip);
        expect(casePage.returnaddressCountryValue()).toEqual(Objects.casepage.data.editCountry);
        expect(casePage.returnAddressLastModified()).toEqual(utils.returnToday("/"));
        expect(casePage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new case and add alias', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        expect(casePage.returnAliasesType()).toEqual(Objects.casepage.data.aliaseFKA);
        expect(casePage.returnAliasesValue()).toEqual(Objects.casepage.data.aliasValue);
        expect(casePage.returnAliasesLastModified()).toEqual(utils.returnToday("/"));
        expect(casePage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new case and add/delete alias', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        casePage.deleteAlias();

    });

    it('should create new case and add/edit alias', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        casePage.editAlias(Objects.casepage.data.aliasMarried, Objects.casepage.data.editAlias);
        expect(casePage.returnAliasesType()).toEqual(Objects.casepage.data.aliasMarried);
        expect(casePage.returnAliasesValue()).toEqual(Objects.casepage.data.editAlias);
        expect(casePage.returnAliasesLastModified()).toEqual(utils.returnToday("/"));
        expect(casePage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new case and add tag', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickTagsLinkBtn();
        casePage.addTag(Objects.casepage.data.tagname);
        expect(casePage.returnTagName()).toEqual(Objects.casepage.data.tagname);
        expect(casePage.returntagCratedDate()).toEqual(utils.returnToday("/"));
        expect(casePage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new case and add/delete tag', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickTagsLinkBtn();
        casePage.addTag(Objects.casepage.data.tagname);
        casePage.deleteTag();

    });

    it('should create new case and click subscribe button and verify if unubscribe btn is displayed', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickSubscribeBtn();
        expect(casePage.returnUnsubscribeBtnText()).toEqual(Objects.casepage.data.unsubscribeBtn);
    });

    it('should create new case and click unubscribe button, verify if is changed to subscribe', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickSubscribeBtn();
        casePage.clickUnubscribeBtn();
        expect(casePage.returnSubscribeBtnText()).toEqual(Objects.casepage.data.subscribeBtn);

    });

    it('should create new case and change case status to active, verify the automated task in tasks table and approve', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatus("Active");
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    it('should create new case and change case status to active, verify the automated task in tasks table and approve', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatus("Inactive");
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    it('should create new case and change case status to active, verify the automated task in tasks table and approve', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatus("Deleted");
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    it('should create new case and and create new case from new case button', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Benefits Appeal");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickNewCaseButton();
        casePage.switchToIframes().submitGeneralInformation(Objects.casepage.data.caseName, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.waitForCaseTitle();
        casePage.clickFirstCaseInCasesList();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseName);
    });


    it('should create new case and click edit button verify updated case title and case type', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.clickEditCaseBtn();
        casePage.switchToIframes().submitGeneralInformation(Objects.casepage.data.caseName, "Benefits Appeal").clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle()
        casePage.clickFirstCaseInCasesList();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseName, "Case name is not updated");
        expect(casePage.returnCaseType()).toEqual("Benefits Appeal", "Case type is not updated");
    });

    it('should create two cases and put one as reference to the anotherone', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseID();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.waitForCaseTitle();
        casePage.clickFirstCaseInCasesList().addReference(caseid);
        expect(casePage.returnReferenceNumber()).toEqual(caseid);
        expect(casePage.returnReferenceTitle()).toEqual(Objects.casepage.data.referenceCaseName);
        expect(casePage.returnReferenceModified()).toEqual(utils.returnToday("/"));
        expect(casePage.returnReferenceType()).toEqual(Objects.casepage.data.referenceType);
        expect(casePage.returnReferenceStatus()).toEqual(Objects.casepage.data.referenceStatusDraft);

    });

    it('should create case and verify if the same case be added as reference to itself', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.waitForCaseID();
        var caseid = element(by.xpath(Objects.casepage.locators.caseID)).getText();
        casePage.addReferenceAsItself(caseid);

    });

    it('should create new case and edit the due date', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.editDueDate();
        expect(casePage.returnDueDate()).toEqual(utils.returnToday("/"), "Due date is not updated");
    });

    it('should create new case and verify the history table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.historyTable();
        expect(casePage.returnHistoryEventName()).toEqual(Objects.casepage.data.historyEvent);
        expect(casePage.returnHistoryDate()).toContain(utils.returnToday("/"));
        expect(casePage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new case and verify adding correspondence document', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElement();
        casePage.rightClickRootFolder().addCorrespondence("case", "Notice of Investigation");
        casePage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    it('should create new case add timesheet and verify in cases timesheet table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            casePage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Case", text, "8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            timeTrackingPage.clickSaveBtn().clickModuleCasesFiles();
            casePage.TimeTable();
            expect(casePage.returnTimesheetFormName()).toContain(Objects.casepage.data.timeSheet);
            expect(casePage.returnTimesheetUser()).toEqual(Objects.casepage.data.assigneeSamuel);
            expect(casePage.returnTimesheetModifiedDate()).toEqual(utils.returnToday("/"));
            expect(casePage.returnTimesheetStatus()).toEqual(Objects.casepage.data.statusDraft);
            expect(casePage.returnTimesheetHours()).toEqual(Objects.casepage.data.totalHours);

        });
    });
});
