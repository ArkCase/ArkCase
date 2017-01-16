var logger = require('../log');
var casePage = require('../Pages/case_page.js');
var userPage = require('../Pages/user_profile_page.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var utils = require('../util/utils.js');
var userPage = require('../Pages/user_profile_page.js');
var loginPage = require('../Pages/login_page.js');
var flag = false;
var EC = protractor.ExpectedConditions;
var timeTrackingPage = require('../Pages/time_tracking_page.js');
var costTrackingPage = require('../Pages/cost_tracking_page.js');
var preferencesPage = require('../Pages/preference_page.js');


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



    it('should create new case and verify case title', function() {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle);
    });

    it('should create new case and verify case type', function() {

        casePage.clickModuleCasesFiles();
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

        casePage.clickModuleCasesFiles();
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium);
    });

    it('should create new case and verify the created date', function() {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnCreatedDate()).toEqual(utils.returnToday("/"));

    });

    it('should create new case and edit the priority to High', function() {

        casePage.clickModuleCasesFiles();
        casePage.editPriority('High');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityHigh);
    });

    it('should create new case and edit the priority to Medium', function() {

        casePage.clickModuleCasesFiles();
        casePage.editPriority('Medium');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium);
    });

    it('should create new case and edit the priority to Medium', function() {

        casePage.clickModuleCasesFiles();
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

        casePage.clickModuleCasesFiles();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.deleteNote();
    });

    it('should create new case add/edit note', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickNotesLink();
        casePage.addNote(Objects.casepage.data.note);
        casePage.editNote(Objects.casepage.data.editnote);
        expect(casePage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is not updated");
    });

    it('should create new case and add task from tasks table verify the task', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickTasksLinkBtn();
        casePage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickCaseTitleInTasks();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnTaskTableTitle()).toContain(Objects.taskpage.data.Subject);
        expect(casePage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
        expect(casePage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"));
        expect(casePage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium);
        expect(casePage.returnTaskTableDueDate()).toEqual(utils.returnToday("/"));
        expect(casePage.returnTaskTableStatus()).toEqual("ACTIVE");
    });

    it('should create new case and verify the people initiator', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickPeopleLinkBtn();
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor);
        expect(casePage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstName);
        expect(casePage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastName);
    });

    it('should create new case add person and verify the added person', function() {

        casePage.clickModuleCasesFiles();
        casePage.clickPeopleLinkBtn();
        casePage.addPerson(Objects.casepage.data.peopleTypeWitness, Objects.casepage.data.peopleFirstName, Objects.casepage.data.peopleLastName);
        expect(casePage.returnPeopleTypeSecondRow()).toEqual(Objects.casepage.data.peopleTypeWitness);
        expect(casePage.returnPeopleFirstNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleFirstName);
        expect(casePage.returnPeopleLastNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleLastName);

    });


    it('should create new case and edit person initiator', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickPeopleLinkBtn();
        casePage.editInitiator(Objects.casepage.data.peopleFirstNameEdit, Objects.casepage.data.peopleLastNameedit);
        expect(casePage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor);
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

    it('should create new case and add/edit address', function() {

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

    it('should create new case and change case status to Inactive, verify the automated task in tasks table and approve', function() {

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

        casePage.clickModuleCasesFiles();
        casePage.clickNewCaseButton();
        casePage.switchToIframes().submitGeneralInformation(Objects.casepage.data.caseName, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.waitForCaseTitle();
        casePage.clickFirstTopElementInList();
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
        casePage.clickFirstTopElementInList();
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
        casePage.clickFirstTopElementInList().addReference(caseid);
        expect(casePage.returnReferenceNumber()).toEqual(caseid);
        expect(casePage.returnReferenceTitle()).toEqual(Objects.casepage.data.referenceCaseName);
        expect(casePage.returnReferenceModified()).toEqual(utils.returnToday("/"));
        expect(casePage.returnReferenceType()).toEqual(Objects.casepage.data.referenceType);
        expect(casePage.returnReferenceStatus()).toEqual(Objects.casepage.data.referenceStatusDraft);

    });

    it('should create case and verify if the same case be added as reference to itself', function() {

        casePage.clickModuleCasesFiles();
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

        casePage.clickModuleCasesFiles();
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

        casePage.clickModuleCasesFiles();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            casePage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Case", text, "8");
            casePage.selectApprover(Objects.casepage.data.approverSamuel);
            casePage.switchToIframes();
            timeTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            casePage.TimeTable();
            expect(casePage.returnTimesheetFormName()).toContain(Objects.casepage.data.timeSheet);
            expect(casePage.returnTimesheetUser()).toEqual(Objects.casepage.data.assigneeSamuel);
            expect(casePage.returnTimesheetModifiedDate()).toEqual(utils.returnToday("/"));
            expect(casePage.returnTimesheetStatus()).toEqual(Objects.casepage.data.statusDraft);
            expect(casePage.returnTimesheetHours()).toEqual(Objects.casepage.data.totalHours);
        });
    });

    it('should create new case add costsheet and verify in the cases costsheet table', function() {

        casePage.clickModuleCasesFiles();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            casePage.clickNewButton();
            costTrackingPage.navigateToExpensesPage();
            casePage.switchToIframes();
            costTrackingPage.submitExpenses("Case", text, Objects.costsheetPage.data.Taxi, Objects.costsheetPage.data.taxi, Objects.costsheetPage.data.Ammount);
            costTrackingPage.clickSaveBtn();
            casePage.clickModuleCasesFiles();
            casePage.CostTable();
            expect(casePage.returncostSheetFormName()).toContain("Costsheet");
            expect(casePage.returncostSheetUser()).toEqual(Objects.casepage.data.assigneeSamuel);
            expect(casePage.returncostSheetModifiedDate()).toEqual(utils.returnToday("/"));
            expect(casePage.returncostSheetTotalCost()).toEqual(Objects.costsheetPage.data.verifyAmmount);
            expect(casePage.returncostSheetStatus()).toEqual(Objects.casepage.data.statusDraft);
        });
    });

    it('should create new case closed it, reinvestigate and verify in the reference table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
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
            expect(casePage.returnReferenceNumber()).toEqual(text);
            expect(casePage.returnReferenceTitle()).toEqual("New Case");
            expect(casePage.returnReferenceModified()).toEqual(utils.returnToday("/"));
            expect(casePage.returnReferenceType()).toEqual(Objects.casepage.data.referenceType);
            expect(casePage.returnReferenceStatus()).toEqual("CLOSED");
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
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor");

    });

    it('should craete new case and verify the assighnee by default', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor");
    });

    it('should create new case  select collaborator from paricipant tab and verify it in the paricipants table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Collaborator", "Sally");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("collaborator");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("Sally Supervisor");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor");

    });

    it('should create new case  select follower from paricipant tab and verify it in the paricipants table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Follower", "Sally");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("follower");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("Sally Supervisor");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor");

    });

    it('should create new case and select reader from participant tab and verify it in the paricipant table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Reader", "Sally");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Sally Supervisor");

    });

    it('should create new case and select co-owner from paricipant tab and verify it in the participant table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Co-Owner", "Samuel");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("co-owner");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("Samuel Supervisor");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor");

    });

    it('should create new case and select supervisor from participant tab and verify it in the participant table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("Supervisor", "Samuel");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("supervisor");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor");

    });

    it('should create new case and select No Access from participant tab and verify it in the paricipant table', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.clickParticipantTab();
        casePage.selectParticipant("No Access", "Ann");
        casePage.switchToIframes();
        casePage.clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("no access");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("Ann Administrator'");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor");
    });

    it('should create new case and verify adding new Report of Investigation document', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson").clickNextBtn().initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        casePage.rightClickRootFolder().addDocument("Report of Investigation");
        casePage.switchToIframes().submitReportOfInvestigation(Objects.basepage.data.reportTitle, Objects.taskspage.data.assigneeSamuel);
        casePage.switchToDefaultContent().validateDocGridData(true, "Report of Investigation", ".pdf", "Report of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

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
        casePage.participantTable();
        expect(casePage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(casePage.returnParticipantNameFirstRow()).toEqual("*");
        expect(casePage.returnParticipantTypeSecondRow()).toEqual("approver");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeThirdRow()).toEqual("assignee");
        expect(casePage.returnParticipantNameThirdRow()).toEqual("Ann Administrator");
        expect(casePage.returnParticipantTypeForthRow()).toEqual("owning group");
        expect(casePage.returnParticipantNameForthRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(casePage.returnParticipantTypeFifthRow()).toEqual("reader");
        expect(casePage.returnParticipantNameFifthRow()).toEqual("Samuel Supervisor");
    });

    it('should create new case and verify if special type can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickSpecialTypeDeleteBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfAssigneeCanBeDeleted();

    });

    it('should create new case and verify if assignee can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickDeleteAsigneeBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfSpecialTypeCaneBeDeleted();

    });

    it('should create new case and verify if owning  can be deleted', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickOwningGroupDeleteBtn();
        casePage.clickRefreshBtn();
        casePage.verifyIfOwningGroupCanBeDeleted();
    });

    it('should create new case and verify if reader  can be deleted', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
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
        expect(casePage.returnSavedNoteInGrid()).toEqual(Objects.basepage.data.note);

    });

    it('should create new case and verify assigned to, owning group and due date', function() {

        casePage.clickModuleCasesFiles();
        expect(casePage.returnDueDate()).toEqual(utils.returnDate("/", 180));
        expect(casePage.returnAssignee()).toEqual(Objects.taskspage.data.administrator);
        expect(casePage.returnOwningGroup()).toEqual(Objects.casepage.data.owningGroup);

    });

    it('should create new case and edit assignee from participant table', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.clickEditAssigneeBtn();
        casePage.editAssigneeInParticipantTable("Samuel Supervisor");
        expect(casePage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor");
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
        casePage.verifyTheNotificationMessage("Case File ");
    });

    it('should create new case by default assignee, claim it and verify the assignee', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickClaimButton();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new case by default assignee, claim it verify the assignee then uncalaim it and verify if the assignee is removed ', function() {

        casePage.clickNewButton().navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.clickNextBtn();
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName).clickSubmitBtn();
        casePage.switchToDefaultContent();
        casePage.clickClaimButton();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
        casePage.clickUnclaimButton();
        expect(casePage.returnAssignee()).toEqual("", "The assignee name is displayed");
    });

    it('should verify if another assignee can be added  from participant table', function() {

        casePage.clickModuleCasesFiles();
        casePage.participantTable();
        casePage.addParticipantFromParticipantTable("Assignee", "Samuel Supervisor");
        casePage.verifyTheNotificationMessage("Only one assignee is allowed");

    });

});
