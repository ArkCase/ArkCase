var logger = require('../log');
var utils = require('../util/utils.js');
var complaintPage = require('../Pages/complaint_page.js');
var casePage = require('../Pages/case_page.js');
var userPage = require('../Pages/user_profile_page.js');
var taskPage = require('../Pages/task_page.js');
var Objects = require('../json/Objects.json');
var loginPage = require('../Pages/login_page.js');
var timeTrackingPage = require('../Pages/time_tracking_page.js');
var costTrackingPage = require('../Pages/cost_tracking_page.js');
var preferencesPage = require('../Pages/preference_page.js');
var flag = false;

function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 30000);

}

describe('Create new complaint ', function() {

    beforeEach(function(done) {

        loginPage.Login(Objects.loginpage.data.supervisoruser.username, Objects.loginpage.data.supervisoruser.password);
        testAsync(done);

    });

    afterEach(function() {

        loginPage.Logout();

    });

    it('Verify adding correspondence document', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName);
        expect(complaintPage.returnFirstNameValue()).toEqual(Objects.complaintPage.data.firstName);
        expect(complaintPage.returnLastNameValue()).toEqual(Objects.complaintPage.data.lastName);
        complaintPage.reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addCorrespondence("complaint", "Notice of Investigation");
        complaintPage.validateDocGridData(true, "Notice of Investigation", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");
    });

    it('Add new note and verify added note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.note, "The note is succesfully added");
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
        expect(complaintPage.returnNoteName()).toEqual(Objects.casepage.data.editnote, "The note is succesfully edited");
    });

    it('Add task from tasks table verify the task', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickTasksLinkBtn();
        complaintPage.clickAddTaskButton();
        taskPage.insertSubject(Objects.taskpage.data.Subject).insertDueDateToday().clickSave();
        taskPage.clickComplaintTitleInTasks();
        complaintPage.clickTasksLinkBtn().waitForTasksTable();
        expect(complaintPage.returnTaskTableTitle()).toContain(Objects.taskpage.data.Subject);
        expect(complaintPage.returnTaskTableAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
        expect(complaintPage.returnTaskTableCreatedDate()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnTaskTablePriority()).toEqual(Objects.casepage.data.priorityMedium);
        expect(complaintPage.returnTaskTableDueDate()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnTaskTableStatus()).toEqual("ACTIVE");
    });

    it('should create new complaint and verify adding new document', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName);
        expect(complaintPage.returnFirstNameValue()).toEqual(Objects.complaintPage.data.firstName);
        expect(complaintPage.returnLastNameValue()).toEqual(Objects.complaintPage.data.lastName);
        complaintPage.reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addDocument("Notice of Investigation");
        utils.uploadDocx();
        complaintPage.validateDocGridData(true, "ArkCaseTesting", ".docx", "Notice of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    it('Verify text details add verify if is saved', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.insertDetailsTextAreaText(Objects.taskspage.data.detailsTextArea);
        complaintPage.clickSaveDetailsButton();
        complaintPage.clickRefreshButton();
        expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.detailsTextArea, 'After refresh the details text is not saved');

    });

    it('Add link from details', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickInsertLinkInDetails();
        expect(complaintPage.returnInsertLinkTitle()).toEqual(Objects.taskspage.data.insertLinkTitle);
        complaintPage.insertDetailsTextAreaLink(Objects.taskspage.data.insertLinkText, Objects.taskspage.data.insertLinkUrl);
        expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.insertLinkText, 'The link is not added');
        complaintPage.clickSaveDetailsButton();
        expect(complaintPage.returnDetailsTextArea()).toEqual(Objects.taskspage.data.insertLinkText, 'The link is not mathcing the expected value');

    });

    it('should create new complaint and add picture from details', function() {

        complaintPage.clickModuleComplaints(); complaintPage.clickExpandFancyTreeTopElementAndSubLink("Details");
        complaintPage.clickDetailsAddPicture();
        complaintPage.uploadPicture();
        expect(complaintPage.returnDetailsUploadedImage());

    });

    it('should create new complaint and close complaint with Open Investigation, approve automatic generated task and validate created case', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickCloseComplaint().switchToIframes().closeComplaint("Open Investigation", Objects.complaintPage.data.description, Objects.complaintPage.data.approver);
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Tasks")
        complaintPage.waitForTasksTable();
        complaintPage.clickRefreshButton();
        expect(complaintPage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        complaintPage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
        complaintPage.navigateToPage("Cases").clickExpandFancyTreeTopElementAndSubLink("Details");
        expect(complaintPage.returnDetailsTextArea()).toContain(Objects.casepage.data.automatedTaskTitle);

    });

    it('Verify adding new Report of Investigation document', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addDocument("Report of Investigation");
        complaintPage.switchToIframes().submitReportOfInvestigation(Objects.basepage.data.reportTitle, Objects.taskspage.data.assigneeSamuel);
        complaintPage.switchToDefaultContent().validateDocGridData(true, "Report of Investigation", ".pdf", "Report of Investigation", utils.returnToday("/"), utils.returnToday("/"), userPage.returnUserNavigationProfile(), "1.0", "ACTIVE");

    });

    it('should verify adding notes in document viewer in complaints', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.switchToDefaultContent().clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.clickDocTreeExpand().rightClickFileTitle().clickDocAction("Open");
        complaintPage.moveToTab().clickDocViewNotesLink().submitNote(Objects.basepage.data.note);
        expect(complaintPage.returnSavedNoteInGrid()).toEqual(Objects.basepage.data.note);

    });

    it('Edit priority to High', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.editPriority('High');
        expect(complaintPage.returnPriority()).toEqual(Objects.casepage.data.priorityHigh);
    });

    it('Edit priority to Expedite', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.editPriority('Expedite');
        expect(complaintPage.returnPriority()).toEqual(Objects.casepage.data.priorityExpedite);
    });

    it('Edit priority to Low', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.editPriority('Low');
        expect(complaintPage.returnPriority()).toEqual("Low");
    });

    it('Edit assignee', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.editAssignee("bthomas");
        expect(complaintPage.returnAssignee()).toEqual("Bill Thomas");
    });

    it('Verify people initiator', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickPeopleLinkBtn();
        expect(complaintPage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor);
        expect(complaintPage.returnPeopleFirstName()).toEqual(Objects.complaintPage.data.firstName);
        expect(complaintPage.returnPeopleLastName()).toEqual(Objects.complaintPage.data.lastName);
    });

    it('should create new complaint and add person', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addPerson(Objects.casepage.data.peopleTypeWitness, Objects.casepage.data.peopleFirstName, Objects.casepage.data.peopleLastName);
        expect(complaintPage.returnPeopleTypeSecondRow()).toEqual(Objects.casepage.data.peopleTypeWitness);
        expect(complaintPage.returnPeopleFirstNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleFirstName);
        expect(complaintPage.returnPeopleLastNameColumnSecondRow()).toEqual(Objects.casepage.data.peopleLastName);
    });


    it('should create new complaint and edit person initiator', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.editInitiator(Objects.casepage.data.peopleFirstNameEdit, Objects.casepage.data.peopleLastNameedit);
        expect(complaintPage.returnPeopleType()).toEqual(Objects.casepage.data.peopleTypeInitiaor);
        expect(complaintPage.returnPeopleFirstName()).toEqual(Objects.casepage.data.peopleFirstNameEdit);
        expect(complaintPage.returnPeopleLastName()).toEqual(Objects.casepage.data.peopleLastNameedit);

    });

    it('should create new complaint and add contact method', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        expect(complaintPage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodFacebook);
        expect(complaintPage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodFacebook);
        expect(complaintPage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should  add contact method and delete it', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        complaintPage.deleteContactMethod();
    });

    it('should create new complaint add contact method and edit it', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addContactMethod(Objects.casepage.data.contactMethodFacebook, Objects.casepage.data.contactMethodFacebook);
        complaintPage.editContactMethod(Objects.casepage.data.contactMethodEmail, Objects.casepage.data.contactMethodEmail);
        expect(complaintPage.returnContatMethodType()).toEqual(Objects.casepage.data.contactMethodEmail);
        expect(complaintPage.returncontactMethodValueFirstRow()).toEqual(Objects.casepage.data.contactMethodEmail);
        expect(complaintPage.returncontactMethodLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returncontactMethodModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new complaint and add organization', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        expect(complaintPage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment);
        expect(complaintPage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeGoverment);
        expect(complaintPage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new complaint add/edit organization', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        complaintPage.editOrganization(Objects.casepage.data.organizationTypeCorporation, Objects.casepage.data.organizationTypeCorporation);
        expect(complaintPage.returnorganizationTypeFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation);
        expect(complaintPage.returnorganizationValueFirstRow()).toEqual(Objects.casepage.data.organizationTypeCorporation);
        expect(complaintPage.returnorganizationLastModifiedFirstRow()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnorganizationModifiedByFirstRow()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new complaint add/delete organization', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addOrganization(Objects.casepage.data.organizationTypeGoverment, Objects.casepage.data.organizationTypeGoverment);
        complaintPage.deleteOrganization();
    });

    it('should create new complaint and add address', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        expect(complaintPage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeHome);
        expect(complaintPage.returnAddressStreet()).toEqual(Objects.casepage.data.street);
        expect(complaintPage.returnAddressCity()).toEqual(Objects.casepage.data.city);
        expect(complaintPage.returnAddressState()).toEqual(Objects.casepage.data.state);
        expect(complaintPage.returnAddressZip()).toEqual(Objects.casepage.data.zip);
        expect(complaintPage.returnaddressCountryValue()).toEqual(Objects.casepage.data.country);
        expect(complaintPage.returnAddressLastModified()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new complaint and add/edit address', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        complaintPage.editAddress(Objects.casepage.data.addressTypeBusiness, Objects.casepage.data.editStreet, Objects.casepage.data.editCity, Objects.casepage.data.editState, Objects.casepage.data.editZip, Objects.casepage.data.editCountry);
        expect(complaintPage.returnAddressType()).toEqual(Objects.casepage.data.addressTypeBusiness);
        expect(complaintPage.returnAddressStreet()).toEqual(Objects.casepage.data.editStreet);
        expect(complaintPage.returnAddressCity()).toEqual(Objects.casepage.data.editCity);
        expect(complaintPage.returnAddressState()).toEqual(Objects.casepage.data.editState);
        expect(complaintPage.returnAddressZip()).toEqual(Objects.casepage.data.editZip);
        expect(complaintPage.returnaddressCountryValue()).toEqual(Objects.casepage.data.editCountry);
        expect(complaintPage.returnAddressLastModified()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnAddressModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);
    });


    it('should create new complaint and add/delete address', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAddress(Objects.casepage.data.addressTypeHome, Objects.casepage.data.street, Objects.casepage.data.city, Objects.casepage.data.state, Objects.casepage.data.zip, Objects.casepage.data.country);
        complaintPage.deleteAddress();
    });


    it('should create new complaint and add alias', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        expect(complaintPage.returnAliasesType()).toEqual(Objects.casepage.data.aliaseFKA);
        expect(complaintPage.returnAliasesValue()).toEqual(Objects.casepage.data.aliasValue);
        expect(complaintPage.returnAliasesLastModified()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);

    });


    it('should create new complaint and add/edit alias', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickPeopleLinkBtn();
        complaintPage.addAlias(Objects.casepage.data.aliaseFKA, Objects.casepage.data.aliasValue);
        complaintPage.editAlias(Objects.casepage.data.aliasMarried, Objects.casepage.data.editAlias);
        expect(complaintPage.returnAliasesType()).toEqual(Objects.casepage.data.aliasMarried);
        expect(complaintPage.returnAliasesValue()).toEqual(Objects.casepage.data.editAlias);
        expect(complaintPage.returnAliasesLastModified()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnAliasesModifiedBy()).toEqual(Objects.casepage.data.assigneeSamuel);

    });

    it('should create new complaint and click subscribe button and verify if unubscribe btn is displayed', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickSubscribeBtn();
        expect(complaintPage.returnUnsubscribeBtnText()).toEqual(Objects.casepage.data.unsubscribeBtn);

    });

    it('should create new complaint and click unubscribe button, verify if is changed to subscribe', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.clickSubscribeBtn();
        complaintPage.clickUnubscribeBtn();
        expect(complaintPage.returnSubscribeBtnText()).toEqual(Objects.casepage.data.subscribeBtn);

    });

    it('Edit due date', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.editDueDate();
        expect(complaintPage.returnDueDate()).toEqual(utils.returnToday("/"), "Due date is not updated");
    });


    it('should create new complaint and add location', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        expect(complaintPage.returnLocationAddress()).toEqual("street");
        expect(complaintPage.returnLocationType()).toEqual("Home");
        expect(complaintPage.returnLocationCity()).toEqual("city");
        expect(complaintPage.returnLocationState()).toEqual("state");
        expect(complaintPage.returnLocationZip()).toEqual("zip");

    });

    it('should create new complaint add location and verify if add location button is still displayed', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        complaintPage.verifyIfAddLocationsBtnIsDisplayed();
    });

    it('should create new complaint add location and delete it', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        complaintPage.deleteLocation();

    });

    it('should create new complaint add/edit location', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.addLocation("Home", "street", "city", "state", "zip");
        complaintPage.editLocation("Business", "street1", "city1", "state1", "zip1");
        expect(complaintPage.returnLocationAddress()).toEqual("street1");
        expect(complaintPage.returnLocationType()).toEqual("Business");
        expect(complaintPage.returnLocationCity()).toEqual("city1");
        expect(complaintPage.returnLocationState()).toEqual("state1");
        expect(complaintPage.returnLocationZip()).toEqual("zip1");
    });

    it('Verify the assighnee by default', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.participantTable();
        expect(complaintPage.returnParticipantTypeFirstRow()).toEqual("*");
        expect(complaintPage.returnParticipantNameFirstRow()).toEqual("*");
        expect(complaintPage.returnParticipantTypeSecondRow()).toEqual("assignee");
        expect(complaintPage.returnParticipantNameSecondRow()).toEqual("Samuel Supervisor");
        expect(complaintPage.returnParticipantTypeThirdRow()).toEqual("owning group");
        expect(complaintPage.returnParticipantNameThirdRow()).toEqual("ACM_INVESTIGATOR_DEV");
        expect(complaintPage.returnParticipantTypeForthRow()).toEqual("reader");
        expect(complaintPage.returnParticipantNameForthRow()).toEqual("Samuel Supervisor");
    });


    it('should Add tag', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickTagsLinkBtn();
        complaintPage.addTag("teg");
        expect(complaintPage.returnTagName()).toEqual("teg");
        expect(complaintPage.returntagCratedDate()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returntagCreatedBy()).toEqual(Objects.casepage.data.assigneeSamuel);
    });

    it('should create new complaint and add/delete tag', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.clickTagsLinkBtn();
        complaintPage.addTag("compl");
        complaintPage.deleteTag();

    });

    it('should Sdd timesheet and verify it in the complaint timesheet table', function() {

        complaintPage.clickModuleComplaints();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            timeTrackingPage.navigateToTimeTrackingPage();
            complaintPage.switchToIframes();
            timeTrackingPage.submitTimesheetTable("Complaint", text, "8");
            complaintPage.selectApprover(Objects.casepage.data.approverSamuel);
            complaintPage.switchToIframes();
            timeTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.TimeTable();
            expect(complaintPage.returnTimesheetFormName()).toContain(Objects.casepage.data.timeSheet);
            expect(complaintPage.returnTimesheetUser()).toEqual(Objects.casepage.data.assigneeSamuel);
            expect(complaintPage.returnTimesheetModifiedDate()).toEqual(utils.returnToday("/"));
            expect(complaintPage.returnTimesheetStatus()).toEqual(Objects.casepage.data.statusDraft);
            expect(complaintPage.returnTimesheetHours()).toEqual(Objects.casepage.data.totalHours);

        });
    });

    it('should Add costsheet and verify in complaints costsheet table', function() {

        complaintPage.clickModuleComplaints();
        element(by.xpath(Objects.casepage.locators.caseID)).getText().then(function(text) {
            console.log(text);
            complaintPage.clickNewButton();
            costTrackingPage.navigateToExpensesPage();
            complaintPage.switchToIframes();
            costTrackingPage.submitExpenses("Complaint", text, Objects.costsheetPage.data.Taxi, Objects.costsheetPage.data.taxi, Objects.costsheetPage.data.Ammount);
            costTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.CostTable();
            expect(complaintPage.returncostSheetFormName()).toContain("Costsheet");
            expect(complaintPage.returncostSheetUser()).toEqual(Objects.casepage.data.assigneeSamuel);
            expect(complaintPage.returncostSheetModifiedDate()).toEqual(utils.returnToday("/"));
            expect(complaintPage.returncostSheetTotalCost()).toEqual(Objects.costsheetPage.data.verifyAmmount);
            expect(complaintPage.returncostSheetStatus()).toEqual(Objects.casepage.data.statusDraft);
        });
    });

    it('should Verify the event in the history table', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.historyTable();
        expect(complaintPage.returnHistoryEventName()).toEqual("Complaint Created");
        expect(complaintPage.returnHistoryDate()).toContain(utils.returnToday("/"));
        expect(complaintPage.returnHistoryUser()).toEqual(Objects.casepage.data.assigneeSamuel);
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
        expect(complaintPage.returnReferenceNumber()).toEqual(caseid);
        expect(complaintPage.returnReferenceTitle()).toEqual("ComplaintTitle");
        expect(complaintPage.returnReferenceModified()).toEqual(utils.returnToday("/"));
        expect(complaintPage.returnReferenceType()).toEqual("COMPLAINT");
        expect(complaintPage.returnReferenceStatus()).toEqual(Objects.casepage.data.referenceStatusDraft);
    });

    it('should  click new complaint button to create new complaint', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickNewComplaintBtn();
        complaintPage.switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.titleComplaint);
        complaintPage.clickSubmitBtn();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickFirstTopElementInList();
        expect(complaintPage.returnComplaintsTitle()).toEqual(Objects.complaintPage.data.titleComplaint);
    });

    it('should create new complaint and add task from tasks table verify the task column number', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
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

    it('should verify that searching of Case id during close complaint is retrieving the data in the fields', function () {
        casePage.navigateToPage("Case Files").waitForCaseID();
        var caseid = casePage.getCaseId();
        var caseTitle = casePage.returnCaseTitle();
        var caseCreateDate = casePage.returnCreatedDate();
        var casePriority = casePage.returnPriority();
        complaintPage.clickModuleComplaints();
        complaintPage.clickCloseComplaint().switchToIframes().selectComplaintDisposition("Add to Existing Case").insertCaseNumber(caseid).clickSearchButton();
        expect(complaintPage.returnCaseTitle()).toEqual(caseTitle);
        expect(complaintPage.returnCaseCreatedDate()).toEqual(caseCreateDate);
        expect(complaintPage.returnCasePriority()).toEqual(casePriority);
        complaintPage.switchToDefaultContent();
    })

});
