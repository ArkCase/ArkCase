var logger = require('../../log');
var utils = require('../../util/utils.js');
var complaintPage = require('../../Pages/complaint_page.js');
var casePage = require('../../Pages/case_page.js');
var userPage = require('../../Pages/user_profile_page.js');
var taskPage = require('../../Pages/task_page.js');
var Objects = require('../../json/Objects.json');
var loginPage = require('../../Pages/login_page.js');
var timeTrackingPage = require('../../Pages/time_tracking_page.js');
var costTrackingPage = require('../../Pages/cost_tracking_page.js');
var preferencesPage = require('../../Pages/preference_page.js');
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');
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

    it('should create new complaint ', function() {

        complaintPage.clickNewButton().clickComplaintButton().switchToIframes().submitInitiatorInformation(Objects.complaintPage.data.firstName, Objects.complaintPage.data.lastName).reenterFirstName(Objects.complaintPage.data.firstName).clickTab("Incident").insertIncidentInformation("Arson", Objects.complaintPage.data.title).clickSubmitButton();
        complaintPage.switchToDefaultContent();
        complaintPage.waitForComplaintsPage();
        expect(complaintPage.returnComplaintsTitle()).toEqual(Objects.complaintPage.data.title, "Title is not correct on new created complaint");

    });

    it('Add/delete note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
        complaintPage.clickNotesLink();
        complaintPage.addNote(Objects.casepage.data.note);
        complaintPage.deleteNote();
    });

    it('Add new note and edit added note', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.waitForComplaintsPage();
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
        complaintPage.switchToDefaultContent();
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn1, "Report of Investigation");
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn2, ".pdf");
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn3, "Report of Investigation");
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn6, Objects.taskspage.data.assigneeSamuel);
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn7, "1.0");
        complaintPage.validateDocGridValue("Report of Investigation", Objects.basepage.data.docGridColumn8, "ACTIVE");
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
            timeTrackingPage.submitTimesheetTable("1");
            timeTrackingPage.clickSaveBtn();
            complaintPage.clickModuleComplaints();
            complaintPage.verifyTimeWidgetData("7");

        });
    });


    //Add a document to document management

    it('Verify adding correspondence document', function() {

        complaintPage.clickModuleComplaints();
        complaintPage.clickExpandFancyTreeTopElementAndSubLink("Documents");
        complaintPage.rightClickRootFolder().addCorrespondence("complaint", "Clearance Granted");
        complaintPage.verifyTheNotificationMessage("Case File ", "The notification message after adding document is not correct");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn1, "Clearance Granted");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn2, ".docx");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn3, "Clearance Granted");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn4, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn5, utils.returnToday("/"));
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn6, Objects.taskspage.data.assigneeSamuel);
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn7, "1.0");
        complaintPage.validateDocGridValue("Clearance Granted", Objects.basepage.data.docGridColumn8, "ACTIVE");

    });

});
