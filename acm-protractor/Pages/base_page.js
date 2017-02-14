var EC = protractor.ExpectedConditions;
var Objects = require('../json/Objects.json');
var util = require('../util/utils.js');
var logger = require('../log');
var SelectWrapper = require('../util/select-wrapper.js');
var newBtn = element(by.xpath(Objects.basepage.locators.newButton));
var root = element(by.xpath(Objects.basepage.locators.root));
var newCorrespondence = element(by.xpath(Objects.basepage.locators.newCorrespondence));
var docTitle = element(by.xpath(Objects.basepage.locators.docTitle));
var docExtension = element(by.xpath(Objects.basepage.locators.docExtension));
var docType = element(by.xpath(Objects.basepage.locators.docType));
var docCreated = element(by.xpath(Objects.basepage.locators.docCreated));
var docModified = element(by.xpath(Objects.basepage.locators.docModified));
var docAuthor = element(by.xpath(Objects.basepage.locators.docAuthor));
var docVersion = element(by.xpath(Objects.basepage.locators.docVersion));
var docStatus = element(by.xpath(Objects.basepage.locators.docStatus));
var deleteDoc = element(by.xpath(Objects.basepage.locators.deleteDoc));
var downloadDoc = element(by.xpath(Objects.basepage.locators.downloadDoc));
var docRow = element(by.xpath(Objects.basepage.locators.docRow));
var fancyTreeExpandTop = element(by.xpath(Objects.casepage.locators.fancyTreeExpandTop));
var notesLink = element(by.xpath(Objects.casepage.locators.notesLink));
var addNoteBtn = element(by.xpath(Objects.casepage.locators.addNoteBtn));
var noteTextArea = element(by.model(Objects.casepage.locators.noteTextArea));
var saveNoteBtn = element(by.xpath(Objects.casepage.locators.saveNoteBtn));
var addedNoteName = element.all(by.repeater(Objects.casepage.locators.addedNoteName)).get(0);
var deleteNoteBtn = element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var editNoteBtn = element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var addNewTaskBtn = element(by.xpath(Objects.casepage.locators.addNewTaskBtn));
var tasksLinkBtn = element(by.xpath(Objects.casepage.locators.tasksLink));
var taskTitle = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(8)
var createdTaskTitle = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(8)
var taskAssighnee = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(9);
var taskCreated = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(10);
var taskPriority = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(11);
var taskDueDate = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(12);
var taskStatus = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(13);
var tagsLinkBtn = element(by.xpath(Objects.casepage.locators.tagsLinkBtn));
var addNewTagBtn = element(by.css(Objects.casepage.locators.addNewTagBtn));
var addTagInput = element(by.model(Objects.casepage.locators.addTagInput));
var tagPopUpTitle = element(by.xpath(Objects.casepage.locators.tagPopUpTitle));
var addTagBtn = element(by.css(Objects.casepage.locators.addTagBtn));
var createdTagName = element.all(by.repeater(Objects.casepage.locators.tagTableColumns)).get(0);
var tagCratedDate = element.all(by.repeater(Objects.casepage.locators.tagTableColumns)).get(1);
var tagCreatedBy = element.all(by.repeater(Objects.casepage.locators.tagTableColumns)).get(2);
var deleteTagBtn = element(by.css(Objects.casepage.locators.deleteTagBtn));
var selectApprover = element(by.name(Objects.casepage.locators.selectApprover));
var searchForUser = element(by.xpath(Objects.casepage.locators.searchForUser));
var goBtn = element(by.xpath(Objects.casepage.locators.goBtn));
var addBtn = element(by.xpath(Objects.casepage.locators.addBtn));
//var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUser));
var poeopleLinkBtn = element(by.xpath(Objects.casepage.locators.peopleLinkBtn));
var peopleTypeColumn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1);
var peopleFirstNameColumn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(2);
var peopleLastNameColumn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(3);
var peopleTable = element(by.xpath(Objects.casepage.locators.peopleTable));
var addPeopleBtn = element(by.xpath(Objects.casepage.locators.addPeopleBtn));
var personsTypeDropDown = new SelectWrapper(by.model(Objects.casepage.locators.personsTypeDropDown));
var personFirstNameInput = element(by.model(Objects.casepage.locators.personFirstNameInput));
var personLastNameInput = element(by.model(Objects.casepage.locators.personLastNameInput));
var savePersonBtn = element(by.xpath(Objects.casepage.locators.savePersonBtn));
var peopleTypeColumnSecondRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var peopleFirstNameColumnSecondRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var peopleLastNameColumnSecondRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var deletePersonBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var editPersonBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var addContactMethodBtn = element(by.css(Objects.casepage.locators.addContactMethodBtn));
var contactMethodsLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var contactMethodTypes = element(by.model(Objects.casepage.locators.contactMethodTypes));
var contactValue = element(by.model(Objects.casepage.locators.contactValue));
var saveContactBtn = element(by.css(Objects.casepage.locators.saveContactBtn));
var contactMethodTypeFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var contactMethodValueFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var contactMethodLastModifiedFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var contactMethodModifiedByFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var contactMethodDeleteBtn = element(by.css(Objects.casepage.locators.deleteContactMethodBtn));
var contactMethodEditBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(9).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var emptyContactMethodTable = element(by.css(Objects.casepage.locators.emptyContactMethodTable));
var editContactMethodBtn = element(by.css(Objects.casepage.locators.editContactMethodBtn));
var organizationsLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var addOrganizationBtn = element(by.css(Objects.casepage.locators.addOgranizationBtn));
var organizationTypeDropdown = new SelectWrapper(by.model(Objects.casepage.locators.organizationTypeDropDown));
var organizationValueInput = element(by.model(Objects.casepage.locators.organizationValueInput));
var saveOrganizationBtn = element(by.css(Objects.casepage.locators.saveOrganizationBtn));
var organizationTypeFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var organizationValueFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var organizationLastModifiedFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var organizationModifiedByFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var organizationDeleteBtn = element(by.css(Objects.casepage.locators.organizationDeleteBtn));
var organizationEditBtn = element(by.css(Objects.casepage.locators.organizationEditBtn));
var addressLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(2);
var addAddressBtn = element(by.css(Objects.casepage.locators.addAddressBtn));
var addressTypeDropDow = new SelectWrapper(by.model(Objects.casepage.locators.addressTypeDropDow));
var streetAddress = element(by.model(Objects.casepage.locators.streetAddress));
var addressCity = element(by.model(Objects.casepage.locators.addressCity));
var addressState = element(by.model(Objects.casepage.locators.addressState));
var addressZip = element(by.model(Objects.casepage.locators.addressZip));
var addressCountry = element(by.model(Objects.casepage.locators.addressCountry));
var saveAddressBtn = element(by.css(Objects.casepage.locators.saveAddressBtn));
var addressTypeValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var addressStreetValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var addressCityValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var addressStateValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var addressZipValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(9);
var addressCountryValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(10);
var addresLastModifiedByValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(11);
var addressModifiedByValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(12);
var deleteAddressBtn = element(by.css(Objects.casepage.locators.deleteAddressBtn));
var editAddressBtn = element(by.css(Objects.casepage.locators.editAddressBtn));
var aliassesLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(3);
var addAliasesBtn = element(by.css(Objects.casepage.locators.addAliasesBtn));
var aliasesDropdown = new SelectWrapper(by.model(Objects.casepage.locators.aliasesDropdown));
var aliasesInputValue = element(by.model(Objects.casepage.locators.aliasesValue));
var saveAliasBtn = element(by.css(Objects.casepage.locators.saveAliasesBtn));
var aliasesTypeValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var aliasesValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var aliasesLastModified = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var aliasesModifiedBy = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var deleteAliasBtn = element(by.css(Objects.casepage.locators.deleteAliasBtn));
var editAliasBtn = element(by.css(Objects.casepage.locators.editAliasBtn));
var referenceLink = element(by.xpath(Objects.casepage.locators.referenceLink));
var addReferenceBtn = element(by.css(Objects.casepage.locators.addReferenceBtn));
var searchForReferenceInput = element(by.model(Objects.casepage.locators.searchForReferenceInput));
var searchReferenceBtn = element(by.css(Objects.casepage.locators.searchReferenceBtn));
var searchedReferenceResult = element(by.repeater(Objects.casepage.locators.searchedReferenceResult));
var addSearchedReferenceBtn = element(by.css(Objects.casepage.locators.saveAliasesBtn));
var addedReferenceRow = element(by.repeater(Objects.casepage.locators.addedReferenceRow));
var referenceNumber = element.all(by.repeater(Objects.casepage.locators.referenceRow)).get(0);
var referenceTitle = element.all(by.repeater(Objects.casepage.locators.referenceRow)).get(1);
var referenceModified = element.all(by.repeater(Objects.casepage.locators.referenceRow)).get(2);
var referenceType = element.all(by.repeater(Objects.casepage.locators.referenceRow)).get(3);
var referenceStatus = element.all(by.repeater(Objects.casepage.locators.referenceRow)).get(4);
var cancelReferenceBtn = element(by.css(Objects.casepage.locators.cancelBtn));
var subscribeBtn = element(by.buttonText(Objects.casepage.locators.subscribeButton));
var unsubscribeBtn = element(by.buttonText(Objects.casepage.locators.unsubscribeButton));
var dueDateLink = element(by.xpath(Objects.casepage.locators.dueDate));
var dueDateInput = element(by.css(Objects.casepage.locators.dueDateInput));
var dueDateTodayBtn = element(by.buttonText(Objects.casepage.locators.dueDateTodayBtn));
var confirmDueDateBtn = element(by.xpath(Objects.casepage.locators.confirmDueDateBtn));
var hitstoryLink = element(by.xpath(Objects.casepage.locators.hitstoryLink));
var historyTable = element(by.xpath(Objects.casepage.locators.historyTable));
var historyEventName = element.all(by.repeater(Objects.casepage.locators.historyTableRow)).get(0);
var historyDate = element.all(by.repeater(Objects.casepage.locators.historyTableRow)).get(1);
var historyUser = element.all(by.repeater(Objects.casepage.locators.historyTableRow)).get(2);
var timesheetLinkBtn = element(by.xpath(Objects.casepage.locators.timesheetLinkBtn));
var timehseetFormName = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(0);
var timesheetUser = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(1);
var timesheetTotalHours = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(2);
var timesheetModifiedDate = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(3);
var timesheetStatus = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(4);
var costSheetLinkBtn = element(by.xpath(Objects.casepage.locators.costSheetLinkBtn));
var costSheetFormName = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(0);
var costSheetUser = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(1);
var costSheetTotalCost = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(2);
var costSheetModifiedDate = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(3);
var costSheetStatus = element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).get(4);
var caseFileModule = element(by.css(Objects.timetrackingPage.locators.caseFileModule));
var complaintsModule = element(by.css(Objects.timetrackingPage.locators.complaintsModule));
var refreshList = element(by.css(Objects.casepage.locators.refreshCasesList));
var firstElementInList = element(by.xpath(Objects.casepage.locators.firstCaseInCasesList));
var refreshBtn = element(by.xpath(Objects.casepage.locators.refreshBtn));
var secondElementInList = element(by.xpath(Objects.casepage.locators.secondCaseInCasesList));
var newDocument = element(by.xpath(Objects.basepage.locators.newDocument));
var detailsTextArea = element(by.xpath(Objects.taskspage.locators.detailsTextArea));
var detailsSaveBtn = element(by.xpath(Objects.taskspage.locators.detailsSaveBtn));
var refreshBtn = element(by.xpath(Objects.taskspage.locators.refreshBtn));
var detailsLinkBtn = element(by.xpath(Objects.taskspage.locators.detailsLinkBtn));
var insertLinkTitle = element(by.xpath(Objects.taskspage.locators.insertLinkTitle));
var insertLinkText = element(by.xpath(Objects.taskspage.locators.insertLinkText));
var insertLinkUrl = element(by.xpath(Objects.taskspage.locators.insertLinkUrl));
var insertLinkBtn = element(by.buttonText(Objects.taskspage.locators.insertLinkBtn));
var detailsPicture = element(by.xpath(Objects.basepage.locators.detailsPicture));
var browseButton = element(by.name(Objects.basepage.locators.browseButton));
var detailsUploadedImage = element(by.xpath(Objects.basepage.locators.detailsUploadedImage));
var checkOut = element(by.xpath(Objects.basepage.locators.checkout));
var checkIn = element(by.xpath(Objects.basepage.locators.checkin));
var cancelEditing = element(by.xpath(Objects.basepage.locators.cancelEditing));
var checkoutDisabled = element(by.xpath(Objects.basepage.locators.checkoutDisabled));
var checkinDisabled = element(by.xpath(Objects.basepage.locators.checkinDisabled));
var cancelEditingDisabled = element(by.xpath(Objects.basepage.locators.cancelEditingDisabled));
var lockIcon = element(by.xpath(Objects.basepage.locators.lockIcon));
var tasksLink = element(by.xpath(Objects.basepage.locators.tasksLink));
var reportTitle = element(by.name(Objects.basepage.locators.reportTitle));
var submitButton = element(by.xpath(Objects.complaintPage.locators.submitButton));
var participantTab = element(by.css(Objects.casepage.locators.participantsTab));
var selectParticipantType = element(by.xpath(Objects.casepage.locators.selectParticipantType));
var selectparticipant = element(by.name(Objects.casepage.locators.selectParticipant));
var searchForUserInput = element(by.xpath(Objects.casepage.locators.searchForUserInput));
var searchForUserBtn = element(by.buttonText(Objects.casepage.locators.searchUserBtn));
var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUserName));
var okBtn = element(by.buttonText(Objects.casepage.locators.OkBtn));
var participantTypeFirstRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(0);
var participantNameFirstRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(1);
var participantTypeSecondRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(2);
var participantNameSecondRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(3);
var participantTypeThirdRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(4);
var participantNameThirdRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(5);
var participantTypeForthRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(6);
var participantNameForthRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(7);
var participantTypeFifthRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(8);
var participantNameFifthRow = element.all(by.xpath(Objects.casepage.locators.participantTableRow)).get(9);
var participantsLinkBtn = element(by.xpath(Objects.casepage.locators.participantLinkBtn));
var addParticipantBtn = element(by.css(Objects.casepage.locators.addParticipantBtn));
var priorityType = element.all(by.xpath(Objects.casepage.locators.priorityType)).get(0);
var editAssigneeBtn = element.all(by.css(Objects.casepage.locators.participantEditBtn)).get(1);
var modalParticipantType = new SelectWrapper(by.xpath(Objects.casepage.locators.modalParticipantType));
var modalParticipantName = element(by.model(Objects.casepage.locators.modalParticipantName));
var saveParticipantBtn = element(by.buttonText(Objects.casepage.locators.saveParticipantBtn));
var assigneeDeleteBtn = element.all(by.css(Objects.casepage.locators.participantDeleteBtn)).get(1);
var specialTypeDeleteBtn = element.all(by.css(Objects.casepage.locators.participantDeleteBtn)).get(0);
var owningGroupDeleteBtn = element.all(by.css(Objects.casepage.locators.participantDeleteBtn)).get(2);
var readerDeleteBtn = element.all(by.css(Objects.casepage.locators.participantDeleteBtn)).get(3);
var docTreeExpand = element(by.xpath(Objects.basepage.locators.docTreeExpand));
var fileTitle = element(by.xpath(Objects.basepage.locators.fileTitle));
var docViewNotesLink = element(by.linkText(Objects.basepage.locators.docViewNotesLink));
var docViewAddNoteButton = element(by.xpath(Objects.basepage.locators.docViewAddNoteButton));
var noteText = element(by.model(Objects.taskspage.locators.notesTextArea));
var saveButton = element(by.buttonText(Objects.basepage.locators.saveButton));
var noteColumnValue = element(by.css(Objects.basepage.locators.noteColumnValue));
var priorityLink = element(by.xpath(Objects.casepage.locators.priority));
var priorityDropDownEdit = new SelectWrapper(by.xpath(Objects.casepage.locators.priorityDropDown));
var priorityBtn = element(by.xpath(Objects.casepage.locators.priorityBtn));
var assigneeLink = element(by.css(Objects.casepage.locators.assignee));
var assigneeDropDown = new SelectWrapper(by.xpath(Objects.casepage.locators.assigneeDropDown));
var assigneeBtn = element(by.xpath(Objects.casepage.locators.assigneeBtn));
var submitBtn = element(by.xpath(Objects.casepage.locators.submitBtn));
var complaintParticipantsTab = element(By.xpath(Objects.complaintPage.locators.participantsTab));
var preferenceLink = element(by.linkText(Objects.preferencesPage.locators.preferenceLink));
var overviewLink = element(by.xpath(Objects.casepage.locators.overviewLink));
var tasksModule = element(by.css(Objects.taskspage.locators.TasksModule));
var timeTrackingModule = element(by.css(Objects.timetrackingPage.locators.timeTrackingModule));
var costTrackingModule = element(by.css(Objects.costsheetPage.locators.costTrackingModule));
var initiatorDeleteBtn = element(by.xpath(Objects.casepage.locators.initiatorDeleteBtn));
var notificationMessage = element(by.css(Objects.basepage.locators.notificationMessage));
var docVersionDropDownList = new SelectWrapper(by.css(Objects.basepage.locators.docVersionDropDownList));
var emailRecepient = element(by.xpath(Objects.basepage.locators.emailRecepient));
var sendEmailButton = element(by.buttonText(Objects.basepage.locators.sendEmailButton));
var claimButton = element(by.css(Objects.casepage.locators.claimButton));
var unclaimButton = element(by.css(Objects.casepage.locators.unclaimButton));
var sugestedTag = element(by.css(Objects.basepage.locators.sugestedTag));
var tagPriority = element(by.xpath(Objects.casepage.locators.tagPriority));
var participantTypeConflictMessage = element(by.css(Objects.casepage.locators.participantTypeConflictMessage));
var addParticipantTypeSecondRowbtn = element(by.xpath(Objects.casepage.locators.addParticipantTypeSecondRowbtn));
var selectParticipantTypeSecondRow = element(by.xpath(Objects.casepage.locators.selectParticipantTypeSecondRow));
var selectParticipantSecondRow = element.all(by.name(Objects.casepage.locators.selectParticipant)).get(1);
var timeCanvasData = element(by.css(Objects.basepage.locators.timeCanvasData));
var owningGroupDropDown = new SelectWrapper(by.xpath(Objects.basepage.locators.owningGroupDropDown));
var owningGroupConfirmBtn = element(by.xpath(Objects.basepage.locators.owningGroupConfirmBtn));
var owningGroup = element(by.xpath(Objects.casepage.locators.owningGroup));
var assigneeNameModelInput = element(by.model(Objects.basepage.locators.assigneeNameModelInput));
var treeSortersBtn = element(by.css(Objects.basepage.locators.treeSortersBtn));
var sortByIdDesc = element(by.xpath(Objects.basepage.locators.sortByIdDesc));
var objectStatus = element(by.xpath(Objects.basepage.locators.objectStatus));
var doc = element(by.id(Objects.casepage.locators.doc));

var BasePage = function() {

    this.navigateToURL = function(url) {

        browser.get(url);
    };

    this.getPageTitle = function() {

        return browser.getTitle();
    };

    this.clickNewButton = function() {
        browser.waitForAngular().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.newButton))), 30000, "New button is not present in the DOM").then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.newButton))), 30000, "New button is not visible").then(function() {
                    browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.basepage.locators.newButton))), 30000, "New button is not clickable").then(function() {
                        browser.executeScript('arguments[0].click()', newBtn);
                        //newBtn.click();
                    });
                });
            });
        });
        return this;
    };

    this.clickNewCorrespondence = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.newCorrespondence))), 30000, "New Correspondence option is not visible").then(function() {
            newCorrespondence.click();
            return this;
        });
    };
    this.selectCorrespondence = function(type, correspondence) {
        var xPathStr;
        if (type == "case") {
            xPathStr = ".//li[@data-command='template/";
        } else {
            xPathStr = ".//li[@data-command='template/Complaint";
        }
        var completexPath;
        switch (correspondence) {
            case "General Release":
                completexPath = xPathStr + "GeneralRelease.docx']";
                break;
            case "Medical Release":
                completexPath = xPathStr + "MedicalRelease.docx']";
                break;
            case "Clearance Granted":
                completexPath = xPathStr + "ClearanceGranted.docx']";
                break;
            case "Clearance Denied":
                completexPath = xPathStr + "ClearanceDenied.docx']";
                break;
            case "Notice of Investigation":
                completexPath = xPathStr + "NoticeofInvestigation.docx']";
                break;
            case "Witness Interview Request":
                completexPath = xPathStr + "InterviewRequest.docx']";
                break;
            case "Correspondence - Invitation":
                completexPath = xPathStr + "Correspondence - Invitation.docx']";
                break;
            default:
                completexPath = xPathStr + "ClearanceGranted.docx']";
                break;
        }
        browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, completexPath + "item is not visible").then(function() {
            var el = element(by.xpath(completexPath));
            el.click();
        });
        return this;
    };
    this.addCorrespondence = function(correspondence) {
        this.clickNewCorrespondence();
        this.selectCorrespondence(correspondence);
    };

    this.clickDocAction = function(action) {
        browser.waitForAngular().then(function() {
            var xPathStr = ".//li[@data-command='";
            var completexPath;
            switch (action) {
                case "Open":
                    completexPath = xPathStr + "open']";
                    break;
                case "Edit":
                    completexPath = xPathStr + "editWithWebDAV']";
                    break;
                case "Email":
                    completexPath = xPathStr + "email']";
                    break;
                case "Checkout":
                    completexPath = xPathStr + "checkout']";
                    break;
                case "Checkin":
                    completexPath = xPathStr + "checkin']";
                    break;
                case "Cancel Editing":
                    completexPath = xPathStr + "cancelEditing']";
                    break;
                case "Cut":
                    completexPath = xPathStr + "cut']";
                    break;
                case "Copy":
                    completexPath = xPathStr + "copy']";
                    break;
                case "Paste":
                    completexPath = xPathStr + "paste']";
                    break;
                case "Rename":
                    completexPath = xPathStr + "rename']";
                    break;
                case "Delete":
                    completexPath = xPathStr + "remove']";
                    break;
                case "Download":
                    completexPath = xPathStr + "download']";
                    break;
                case "Replace":
                    completexPath = xPathStr + "replace']";
                    break;
                case "Declare As Record":
                    completexPath = xPathStr + "declare']";
                    break;
                default:
                    completexPath = xPathStr + "open']";
                    break;
            }
            var el = element(by.xpath(completexPath));
            browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, "Doc action is not visible").then(function() {
                el.click();
            });

        });

        return this;
    };

    this.validateDocGridData = function(added, doctitle, docextension, doctype, createddate, modifieddate, author, version, status) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.docAuthor))), 30000, "Doc Author value is not present in the DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.docAuthor))), 30000, "Doc Author value is not visible").then(function() {
                expect(docRow.isPresent()).toBe(added, "Document is not sucessfully added");
                expect(docTitle.getText()).toEqual(doctitle, "Document title is not correct in grid");
                expect(docExtension.getText()).toEqual(docextension, "Document extension is not correct in grid");
                expect(docType.getText()).toEqual(doctype, "Document type is not correct in grid");
                expect(docCreated.getText()).toEqual(createddate, "Document created date is not correct in grid");
                expect(docModified.getText()).toEqual(modifieddate, "Document modified date is not correct in grid");
                expect(docAuthor.getText()).toEqual(author, "Document author is not correct in grid");
                expect(docVersion.getText()).toEqual(version, "Document version is not correct in grid");
                expect(docStatus.getText()).toEqual(status, "Document status is not correct in grid");
            });
        })

    }
    this.switchToIframes = function() {
        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.className("new-iframe ng-scope"))), 30000, "Iframe is not visible");
        browser.switchTo().frame(browser.driver.findElement(by.className("new-iframe ng-scope"))).then(function() {
            browser.switchTo().frame(browser.driver.findElement(By.className("frevvo-form")));
        });
        return this;
    };
    this.switchToDefaultContent = function() {

        browser.driver.switchTo().defaultContent();
        return this;

    };
    this.rightClickRootFolder = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.taskspage.locators.root))), 30000, "Root folder is not visible").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.root))), 30000, "Root folder is not visible").then(function () {
                root.click();
                browser.actions().click(protractor.Button.RIGHT).perform();
            });
        });
        return this;
    }


    this.clickFirstTopElementInList = function() {
        browser.sleep(10000);
        refreshList.click().then(function() {
            browser.sleep(10000);
            firstElementInList.click();
        });
        return this;
    }

    this.clickSecondElementInList = function() {

        browser.sleep(10000);
        refreshList.click().then(function() {
            browser.sleep(10000);
            secondElementInList.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000, "Case title is not displayed");
            });
        });
        return this;

    }

    this.clickRefreshBtn = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.refreshBtn))), 30000, "Refresh button is not displayed").then(function() {
            refreshBtn.click();
            return this;
        });
    }

    this.clickExpandFancyTreeTopElementAndSubLink = function(link) {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.fancyTreeExpandTop))), 30000, "Fancy tree top element expand is not visible").then(function() {
            browser.sleep(5000);
            fancyTreeExpandTop.click().then(function() {
                var xPathStr = "//span[contains(text(),'";
                var completexPath;
                completexPath = xPathStr + link + "')]";
                browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, "Sublink " + link + " of top element is not visible").then(function() {
                    browser.wait(EC.elementToBeClickable(element(by.xpath(completexPath))), 30000, "Sublink " + link + " of top element is not visible").then(function() {
                        var el = element(by.xpath(completexPath));
                        el.click();
                    });
                });
            });
        });
        return this;

    };

    this.clickNotesLink = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.notesLink))), 30000, "Notes link button is not displayed");
        notesLink.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNoteBtn))), 30000, "Add Note button is not visible");
        });
        return this;
    };
    this.addNote = function(note) {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNoteBtn))), 30000, "Add Note button is not visible").then(function() {
            addNoteBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.noteTextArea))), 30000, "Note text area field is not visible").then(function() {
                    noteTextArea.click().then(function() {
                        noteTextArea.sendKeys(note).then(function() {
                            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 30000, "Save note button is not visible").then(function() {
                                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 30000, "Save note button is not clickable").then(function() {
                                    saveNoteBtn.click().then(function() {
                                        browser.wait(EC.presenceOf(element.all(by.repeater(Objects.casepage.locators.addedNoteName)).get(0)), 30000, "Added note name element is not present in DOM");
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    };
    this.editNote = function(note) {
        browser.wait(EC.invisibilityOf(element(by.xpath(Objects.basepage.locators.fadeElementEditNote))), 30000).then(function() {
            browser.wait(EC.presenceOf(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 30000, "Edit note button is not present in DOM").then(function() {
                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 30000, "Edit note button is not visible").then(function() {
                    browser.wait(EC.elementToBeClickable(element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0)), 30000, "Edit note button is not clickable").then(function() {
                        editNoteBtn.click().then(function() {
                            noteTextArea.clear().then(function() {
                                noteTextArea.sendKeys(note).then(function() {
                                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.saveNoteBtn))), 30000, "Save note button is not visible").then(function() {
                                        saveNoteBtn.click().then(function() {
                                            browser.wait(EC.textToBePresentInElement((addedNoteName), Objects.casepage.data.editnote), 30000, "Edited note is not saved");
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    };
    this.returnNoteName = function() {
        return addedNoteName.getText();

    };
    this.deleteNote = function() {
        browser.wait(EC.invisibilityOf(element(by.xpath(Objects.basepage.locators.fadeElementDeleteNote))), 30000, "Animation element is visible").then(function() {
            browser.wait(EC.presenceOf(element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1)), 30000, "Delete note button is not present in DOM").then(function() {
                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1)), 30000, "Delete note button is not visible").then(function() {
                    browser.wait(EC.elementToBeClickable(element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1)), 30000, "Delete note button is not clickable").then(function() {
                        deleteNoteBtn.click();
                    });
                });

            });
        });
        return this;
    };

    this.clickTagsLinkBtn = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tagsLinkBtn))), 30000, "Tags link button is not displayed").then(function() {
            tagsLinkBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addNewTagBtn))), 20000, "Add new tag button is not visible");
            });
        });
        return this;
    }

    this.addTag = function(tagName) {

        addNewTagBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.addTagInput))), 20000, " Add tag modal is not displayed").then(function() {
                addTagInput.sendKeys(tagName).then(function() {
                    tagPopUpTitle.click().then(function() {
                        addTagBtn.click().then(function() {
                            browser.sleep(5000);
                            element.all(by.repeater(Objects.casepage.locators.tagTableColumns)).then(function(items) {
                                expect(items.length).toBeGreaterThan(3, "The tag is not added");
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.returnTagName = function() {
        return createdTagName.getText();
    }

    this.returntagCratedDate = function() {
        return tagCratedDate.getText();
    }

    this.returntagCreatedBy = function() {
        return tagCreatedBy.getText();
    }

    this.addSugestedTag = function(tagName) {

        addNewTagBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.addTagInput))), 20000, " Add tag modal is not displayed").then(function() {
                addTagInput.sendKeys(tagName).then(function() {
                    browser.wait(EC.visibilityOf(element(by.css(Objects.basepage.locators.sugestedTag))), 10000, "Sugested tag is not displayed").then(function() {
                        sugestedTag.click().then(function() {
                            tagPopUpTitle.click().then(function() {
                                addTagBtn.click().then(function() {
                                    tagPriority.click().then(function() {
                                        element.all(by.repeater(Objects.casepage.locators.tagTableColumns)).then(function(items) {
                                            expect(items.length).toBeGreaterThan(3, "The suggested  tag is not added");
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.deleteTag = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.deleteTagBtn))), 10000, "Delete tag button is not visible").then(function() {
            deleteTagBtn.click().then(function() {
                element.all(by.repeater(Objects.casepage.locators.tagTableColumns)).then(function(items) {
                    expect(items.length).toBe(0, "The tag is not deleted");
                });
            });
        });
        return this;
    }

    this.selectApprover = function(approverSamuel) {
        browser.wait(EC.presenceOf(element(by.name(Objects.casepage.locators.selectApprover))), 30000, "Select approver element is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.selectApprover))), 30000, "Select approver element is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.casepage.locators.selectApprover))), 30000, "Select approver element is not clickable").then(function() {
                    selectApprover.click().then(function() {
                        browser.driver.switchTo().defaultContent();
                        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUser))), 10000, "Search field is not visible");
                        searchForUserInput.click();
                        searchForUserInput.sendKeys(approverSamuel);
                        searchForUserBtn.click().then(function() {
                            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 3000, "Search user button is not visible");
                            searchedUser.click().then(function() {
                                browser.wait(EC.visibilityOf(element(by.buttonText(Objects.casepage.locators.OkBtn))), 3000, "There is no found user");
                                okBtn.click();
                            });
                        });
                    });
                })
            });
        });
        this.switchToIframes();
        return this;
    }

    this.clickSubscribeBtn = function() {
        browser.wait(EC.visibilityOf(element(by.buttonText(Objects.casepage.locators.subscribeButton))), 20000, "Subscribe btn is not displayed").then(function() {
            subscribeBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.buttonText(Objects.casepage.locators.unsubscribeButton))), 10000, "After subscibe btn is clicked, unsubscribe is not displayed");
            });
        });
        return this;
    }
    this.clickUnubscribeBtn = function() {
        browser.wait(EC.visibilityOf(element(by.buttonText(Objects.casepage.locators.unsubscribeButton))), 10000, "Unsubscribe button is not visible").then(function() {
            unsubscribeBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.buttonText(Objects.casepage.locators.subscribeButton))), 10000, "After unsubscribe btn is cliked, subscribe btn is not displayed");
            });
        });
        return this;
    }

    this.returnUnsubscribeBtnText = function() {
        return unsubscribeBtn.getText();
    }
    this.returnSubscribeBtnText = function() {
        return subscribeBtn.getText();
    }
    this.editDueDate = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.dueDate))), 30000, "Due date field is not displayed").then(function() {
            dueDateLink.click().then(function() {
                dueDateInput.click().then(function() {
                    dueDateTodayBtn.click().then(function() {
                        confirmDueDateBtn.click().then(function() {
                            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.dueDate))), 10000, "Due date is not visible after update");
                        });
                    });
                });
            });
        });
        return this;
    }
    this.returnDueDate = function() {
        return dueDateLink.getText();
    }

    this.historyTable = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.hitstoryLink))), 30000, "History link is not displayed").then(function() {
            hitstoryLink.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.historyTable))), 30000, "History table is not displayed").then(function() {
                    browser.wait(EC.visibilityOf(element(by.repeater(Objects.casepage.locators.historyTableRow))), 30000, "History data is not displayed");
                });
            });
        });

        return this;
    }

    this.clickPeopleLinkBtn = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.peopleLinkBtn))), 30000, "People link button is not visible").then(function() {
            browser.sleep(5000);
            poeopleLinkBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.peopleTable))), 15000, "People table is not visible").then(function() {
                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1)), 15000, "People table column is not visible");
                });
            });
        });
        return this;
    }
    this.returnPeopleType = function() {
        return peopleTypeColumn.getText();
    }

    this.returnPeopleFirstName = function() {
        return peopleFirstNameColumn.getText();
    }
    this.returnPeopleLastName = function() {
        return peopleLastNameColumn.getText();
    }

    this.addPerson = function(personType, firstName, lastName) {

        addPeopleBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.personsTypeDropDown))), 15000, "Add people pop up is not shown").then(function() {
                personsTypeDropDown.selectByText(personType).then(function() {
                    personFirstNameInput.click().then(function() {
                        personFirstNameInput.sendKeys(firstName).then(function() {
                            personLastNameInput.click().then(function() {
                                personLastNameInput.sendKeys(lastName).then(function() {
                                    savePersonBtn.click().then(function() {
                                        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 15000, "Person is not added");
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }


    this.returnPeopleTypeSecondRow = function() {
        return peopleTypeColumnSecondRow.getText();
    }
    this.returnPeopleFirstNameColumnSecondRow = function() {
        return peopleFirstNameColumnSecondRow.getText();
    }

    this.returnPeopleLastNameColumnSecondRow = function() {
        return peopleLastNameColumnSecondRow.getText();
    }

    this.deletePerson = function() {
        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(1))).then(function() {
            deletePersonBtn.click().then(function() {
                element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                    expect(items.length).toBe(0, "The person is not deleted");
                });

            });
        });
        return this;
    }
    this.editPerson = function(personType, firstName, lastName) {
        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(0))).then(function() {
            editPersonBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.personsTypeDropDown))), 15000, "Add people pop up is not shown").then(function() {
                    personsTypeDropDown.selectByText(personType).then(function() {
                        personFirstNameInput.clear().then(function() {
                            personFirstNameInput.sendKeys(firstName).then(function() {
                                personLastNameInput.clear().then(function() {
                                    personLastNameInput.sendKeys(lastName).then(function() {
                                        savePersonBtn.click().then(function() {
                                            browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1)), 15000, "After save people table colimn is not visible");
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        return this;
    }

    this.editInitiator = function(firstName, LastName) {

        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(0))).then(function() {
            editPersonBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.personFirstNameInput))), 10000, "Person input is not displayed").then(function() {
                    personFirstNameInput.clear().then(function() {
                        personFirstNameInput.sendKeys(firstName).then(function() {
                            personLastNameInput.clear().then(function() {
                                personLastNameInput.sendKeys(LastName).then(function() {
                                    savePersonBtn.click().then(function() {
                                        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1)), 15000, "After edit initiator, people tabke column is not visible");
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.verifyIfInitiatorCanBeDeleted = function() {

        browser.wait(EC.visibilityOf(element(by.repeater(Objects.casepage.locators.peopleTableColumns))), 10000, "People table columns are not displayed").then(function() {
            element.all(by.xpath(Objects.casepage.locators.initiatorDeleteBtn)).then(function(items) {
                expect(items.length).toBe(0, "The delete button for the initiator should not be displayed");
            });
        });
        return this;
    }

    this.addContactMethod = function(contactMethodType, contactvalue) {

        contactMethodsLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addContactMethodBtn))), 10000, "Add contact method button is not visible").then(function() {
                addContactMethodBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.contactMethodTypes))), 10000, "Contact method type element is not visible").then(function() {
                        contactMethodTypes.$('[value="string:' + contactMethodType + '"]').click().then(function() {
                            contactValue.sendKeys(contactvalue).then(function() {
                                saveContactBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5)), 10000, "Affter adding contact, people table column is not visible").then(function() {
                                        browser.sleep(8000);
                                        contactMethodsLinkBtn.click();
                                    });
                                });
                            });
                        });

                    });
                });

            });
        });
        return this;

    }

    this.returnContatMethodType = function() {
        return contactMethodTypeFirstRow.getText();

    }

    this.returncontactMethodValueFirstRow = function() {

        return contactMethodValueFirstRow.getText();
    }

    this.returncontactMethodLastModifiedFirstRow = function() {
        return contactMethodLastModifiedFirstRow.getText();
    }

    this.returncontactMethodModifiedByFirstRow = function() {
        return contactMethodModifiedByFirstRow.getText();
    }

    this.deleteContactMethod = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.deleteContactMethodBtn))), 10000, "Delete contact method button is not visible").then(function() {
            contactMethodDeleteBtn.click().then(function() {
                browser.sleep(3000);
                element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                    expect(items.length).toBe(5, "The contact method is not deleted");
                });
            });
        });
        return this;
    }

    this.editContactMethod = function(contactMethodType, contactvalue) {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.editContactMethodBtn))), 10000, "Edit contact method button is not visible").then(function() {
            editContactMethodBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.contactMethodTypes))), 10000, "Contact method types field is not visible").then(function() {
                    contactMethodTypes.$('[value="string:' + contactMethodType + '"]').click().then(function() {
                        contactValue.clear().then(function() {
                            contactValue.sendKeys(contactvalue).then(function() {
                                saveContactBtn.click().then(function() {
                                    browser.sleep(8000);
                                    contactMethodsLinkBtn.click();
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.addOrganization = function(organizationType, organizationvalue) {

        organizationsLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addOgranizationBtn))), 10000, "Add organization button is not visible").then(function() {
                addOrganizationBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.organizationTypeDropDown))), 10000, "Organization type drop down is not visible").then(function() {
                        organizationTypeDropdown.selectByText(organizationType).then(function() {
                            organizationValueInput.sendKeys(organizationvalue).then(function() {
                                saveOrganizationBtn.click().then(function() {
                                    browser.sleep(8000);
                                    organizationsLinkBtn.click();
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.returnorganizationTypeFirstRow = function() {
        return organizationTypeFirstRow.getText();
    }

    this.returnorganizationValueFirstRow = function() {
        return organizationValueFirstRow.getText();
    }

    this.returnorganizationLastModifiedFirstRow = function() {
        return organizationLastModifiedFirstRow.getText();
    }

    this.returnorganizationModifiedByFirstRow = function() {
        return organizationModifiedByFirstRow.getText();
    }

    this.deleteOrganization = function() {
        organizationDeleteBtn.click().then(function() {
            browser.sleep(3000);
            element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                expect(items.length).toBe(5, "The organization is not deleted");
            });
        });
        return this;
    }

    this.editOrganization = function(organizationType, organizationvalue) {

        organizationEditBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.organizationTypeDropDown))), 10000, "Organization type dropdown is not visible").then(function() {
                organizationTypeDropdown.selectByText(organizationType).then(function() {
                    organizationValueInput.clear().then(function() {
                        organizationValueInput.sendKeys(organizationvalue).then(function() {
                            saveOrganizationBtn.click().then(function() {
                                browser.sleep(8000);
                                organizationsLinkBtn.click();
                                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 10000, "Column in grid is not visible after editing organization");
                            });
                        });
                    });
                });
            });

        });

        return this;
    }

    this.addAddress = function(addressType, street, city, state, zip, country) {

        addressLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addAddressBtn))), 10000, "Add address link button is not visible");
        }).then(function() {
            addAddressBtn.click();
        }).then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.addressTypeDropDow))), 10000, "Address type field is not visible").then(function() {
                addressTypeDropDow.selectByText(addressType);
            }).then(function() {
                streetAddress.sendKeys(street);
            }).then(function() {
                addressCity.sendKeys(city);
            }).then(function() {
                addressState.sendKeys(state);
            }).then(function() {
                addressZip.sendKeys(zip);
            }).then(function() {
                addressCountry.sendKeys(country);
            }).then(function() {
                saveAddressBtn.click();
            }).then(function() {
                browser.sleep(8000);
                addressLinkBtn.click();
            });
        });

        return this;
    }

    this.returnAddressType = function() {
        return addressTypeValue.getText();
    }
    this.returnAddressStreet = function() {
        return addressStreetValue.getText();
    }
    this.returnAddressCity = function() {
        return addressCityValue.getText();
    }
    this.returnAddressState = function() {
        return addressStateValue.getText();
    }
    this.returnAddressZip = function() {
        return addressZipValue.getText();
    }

    this.returnaddressCountryValue = function() {
        return addressCountryValue.getText();
    }
    this.returnAddressModifiedBy = function() {
        return addressModifiedByValue.getText();
    }
    this.returnAddressLastModified = function() {
        return addresLastModifiedByValue.getText();
    }

    this.returnAddressModifiedBy = function() {
        return addressModifiedByValue.getText();
    }

    this.deleteAddress = function() {
        deleteAddressBtn.click().then(function() {
            browser.sleep(5000);
            addressLinkBtn.click();
            element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                expect(items.length).toBe(5, "The address is not deleted");
            });
        });
        return this;
    }

    this.editAddress = function(addressType, street, city, state, zip, country) {

        editAddressBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.addressTypeDropDow))), 10000, "Address type dropdown is not visible");
        }).then(function() {
            addressTypeDropDow.selectByText(addressType);
        }).then(function() {
            streetAddress.clear();
        }).then(function() {
            streetAddress.sendKeys(street);
        }).then(function() {
            addressCity.clear();
        }).then(function() {
            addressCity.sendKeys(city);
        }).then(function() {
            addressState.clear();
        }).then(function() {
            addressState.sendKeys(state);
        }).then(function() {
            addressZip.clear();
        }).then(function() {
            addressZip.sendKeys(zip);
        }).then(function() {
            addressCountry.clear();
        }).then(function() {
            addressCountry.sendKeys(country);
        }).then(function() {
            saveAddressBtn.click();
            browser.sleep(8000);
            addressLinkBtn.click();
        });

        return this;
    }

    this.returnAliasesType = function() {
        return aliasesTypeValue.getText();
    }
    this.returnAliasesValue = function() {
        return aliasesValue.getText();
    }
    this.returnAliasesLastModified = function() {
        return aliasesLastModified.getText();
    }
    this.returnAliasesModifiedBy = function() {
        return aliasesModifiedBy.getText();
    }

    this.addAlias = function(aliasType, aliasValue) {

        aliassesLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addAliasesBtn))), 10000, "Add aliasses button is not visible").then(function() {
                addAliasesBtn.click();
            }).then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.aliasesDropdown))), 10000, "Aliasses drop down list is not visible").then(function() {
                    aliasesDropdown.selectByText(aliasType);
                }).then(function() {
                    aliasesInputValue.sendKeys(aliasValue);
                }).then(function() {
                    saveAliasBtn.click();
                }).then(function() {
                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 10000, "After saving aliasses table column is not visible").then(function() {
                        browser.sleep(8000);
                        aliassesLinkBtn.click();

                    });
                });
            });
        });
        return this;

    }

    this.deleteAlias = function() {
        deleteAliasBtn.click().then(function() {
            browser.sleep(8000);
            aliassesLinkBtn.click();
            element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                expect(items.length).toBe(5, "The alias is not deleted");
            });
        });
        return this;
    }

    this.editAlias = function(aliasType, aliasValue) {
        editAliasBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.aliasesDropdown))), 10000, "Aliasses drop down is not visible").then(function() {
                aliasesDropdown.selectByText(aliasType).then(function() {
                    aliasesInputValue.clear().then(function() {
                        aliasesInputValue.sendKeys(aliasValue).then(function() {
                            saveAliasBtn.click().then(function() {
                                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 10000, "After saving aliasses table column is not visible");
                                browser.sleep(8000);
                                aliassesLinkBtn.click();
                            });

                        });
                    });
                });
            });
        });

        return this;
    }
    this.addReference = function(caseReference) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.referenceLink))), 30000, "Reference link is not displayed").then(function() {
            referenceLink.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addReferenceBtn))), 20000, "Add reference button is not displayed").then(function() {
                    addReferenceBtn.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.searchForReferenceInput))), 30000, "Reference input is not displayed").then(function() {
                            searchForReferenceInput.sendKeys(caseReference).then(function() {
                                searchReferenceBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element(by.repeater(Objects.casepage.locators.searchedReferenceResult))), 10000, "Searched reference is not displayed").then(function() {
                                        searchedReferenceResult.click().then(function() {
                                            addSearchedReferenceBtn.click().then(function() {
                                                browser.wait(EC.visibilityOf(element(by.repeater(Objects.casepage.locators.addedReferenceRow))), 30000, "Added reference is not displayed");
                                            });
                                        });
                                    });
                                });

                            });
                        });
                    });
                });
            });

        });
        return this;
    }

    this.clikReferenceLink = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.referenceLink))), 30000, "Reference link is not displayed").then(function() {
            referenceLink.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.repeater(Objects.casepage.locators.addedReferenceRow))), 30000, "Reference is not displayed");
            });
        });
    }


    this.returnReferenceNumber = function() {
        return referenceNumber.getText();
    }

    this.returnReferenceTitle = function() {
        return referenceTitle.getText();
    }
    this.returnReferenceModified = function() {
        return referenceModified.getText();
    }

    this.returnReferenceType = function() {
        return referenceType.getText();
    }
    this.returnReferenceStatus = function() {
        return referenceStatus.getText();
    }

    this.addReferenceAsItself = function(caseReference) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.referenceLink))), 30000, "Reference link is not displayed").then(function() {
            referenceLink.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addReferenceBtn))), 20000, "Add reference button is not displayed").then(function() {
                    addReferenceBtn.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.searchForReferenceInput))), 30000, "Reference input is not displayed").then(function() {
                            searchForReferenceInput.sendKeys(caseReference).then(function() {
                                searchReferenceBtn.click().then(function() {
                                    browser.sleep(5000);
                                    element.all(by.repeater(Objects.casepage.locators.searchedReferenceResult)).then(function(items) {
                                        expect(items.length).toBe(0, "Same case should not be eable to be added  as reference to itself");
                                        cancelReferenceBtn.click();
                                    });
                                });

                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.returnAutomatedTask = function() {

        return taskTitle.getText();

    }

    this.clickTaskTitle = function() {
        taskTitle.click();
    };

    this.TimeTable = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.timesheetLinkBtn))), 30000, "Time link btn is not displayed").then(function() {
            browser.sleep(10000);
            timesheetLinkBtn.click().then(function() {
                browser.sleep(5000);
                element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).then(function(items) {
                    expect(items.length).toBe(6, "Time sheet is not displayed in the time table");
                });
            });
        });
        return this;
    }

    this.returnHistoryEventName = function() {
        return historyEventName.getText();
    }
    this.returnHistoryDate = function() {
        return historyDate.getText();
    }
    this.returnHistoryUser = function() {
        return historyUser.getText();
    }
    this.returnTimesheetFormName = function() {
        return timehseetFormName.getText();
    }
    this.returnTimesheetUser = function() {
        return timesheetUser.getText();
    }
    this.returnTimesheetModifiedDate = function() {
        return timesheetModifiedDate.getText();
    }
    this.returnTimesheetStatus = function() {
        return timesheetStatus.getText();
    }
    this.returnTimesheetHours = function() {
        return timesheetTotalHours.getText();
    }
    this.returncostSheetFormName = function() {
        return costSheetFormName.getText();
    }
    this.returncostSheetUser = function() {
        return costSheetUser.getText();
    }
    this.returncostSheetTotalCost = function() {
        return costSheetTotalCost.getText();
    }
    this.returncostSheetModifiedDate = function() {
        return costSheetModifiedDate.getText();
    }
    this.returncostSheetStatus = function() {
        return costSheetStatus.getText();
    }

    this.CostTable = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.costSheetLinkBtn))), 30000, "Cost link btn is not displayed").then(function() {
            browser.sleep(10000);
            costSheetLinkBtn.click().then(function() {
                browser.sleep(5000);
                browser.wait(EC.visibilityOf(element(by.repeater(Objects.casepage.locators.timesheetTableRow))), 30000, "Costsheet table row is not visible").then(function() {
                    element.all(by.repeater(Objects.casepage.locators.timesheetTableRow)).then(function(items) {
                        expect(items.length).not.toBe(0, "Cost sheet is not displayed in the cost table");
                    });
                });
            });
        });
    }
    this.clickModuleCasesFiles = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.timetrackingPage.locators.caseFileModule))), 30000, "Case module is not visible").then(function() {
            browser.executeScript('arguments[0].click()', caseFileModule).then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000, "Case title is not visible");
            })

        })
        return this;
    }
    this.clickModuleComplaints = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.timetrackingPage.locators.complaintsModule))), 30000, "Complaints module is not visible").then(function() {
            browser.executeScript('arguments[0].click()', complaintsModule).then(function() {
                browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.complaintTitleLink))), 30000, "Complaint title is not present in DOM").then(function() {
                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.complaintTitleLink))), 30000, "Complaint title is not visible");
                })

            })
        });
    }
    this.clickModuleTasks = function() {
        browser.executeScript('arguments[0].click()', tasksModule);
    }
    this.clickModuleTimeTracking = function() {

        browser.executeScript('arguments[0].click()', timeTrackingModule);

    }
    this.clickModuleCostTracking = function() {

        browser.executeScript('arguments[0].click()', costTrackingModule);
    }

    this.clickAddTaskButton = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNewTaskBtn))), 30000, "Add new task button is not visible").then(function() {
            addNewTaskBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.id(Objects.taskpage.locators.subject))), 30000, "After clicking on add task button, subject is not visible");
            });
        });

        return this;
    };
    this.clickTasksLinkBtn = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tasksLink))), 20000, "Tasks link button is not visible").then(function() {

            tasksLinkBtn.click();
        });

        return this;

    };
    this.waitForTasksTable = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.tasksTable))), 30000, "Tasks table is not visible").then(function() {
            refreshBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.taskTitle))), 30000, "After 30 second task is not shown in the task table");
            });
        });
        return this;

    };
    this.returnTaskTitle = function() {
        return taskTitle.getText();
    };

    this.returnTaskTableTitle = function() {
        return createdTaskTitle.getText();
    }
    this.returnTaskTableAssignee = function() {
        return taskAssighnee.getText();
    };
    this.returnTaskTableCreatedDate = function() {
        return taskCreated.getText();
    };
    this.returnTaskTablePriority = function() {
        return taskPriority.getText();
    };

    this.returnTaskTableDueDate = function() {
        return taskDueDate.getText();
    };

    this.returnTaskTableStatus = function() {
        return taskStatus.getText();
    };

    this.clickNewDocument = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.newDocument))), 30000, "New Document element is not present in the DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.newDocument))), 30000, "New document element is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.basepage.locators.newDocument))), 30000, "New docuemnt element is not clickable").then(function() {
                    newDocument.click();
                });
            });
        });
        return this;
    };

    this.selectDocument = function(docType) {
        xPathStr = ".//li[@data-command='file/";
        xPathStrForm = " .//li[@data-command='form/"
        var completexPath;
        switch (docType) {
            case "Medical Release":
                completexPath = xPathStr + "mr']";
                break;
            case "General Release":
                completexPath = xPathStr + "gr']";
                break;
            case "eDelivery":
                completexPath = xPathStr + "ev']";
                break;
            case "SF86 Signature":
                completexPath = xPathStr + "sig']";
                break;
            case "Notice of Investigation":
                completexPath = xPathStr + "noi']";
                break;
            case "Witness Interview Request":
                completexPath = xPathStr + "wir']";
                break;
            case "Report of Investigation":
                completexPath = xPathStrForm + "roi']";
                break;
            case "Other":
                completexPath = xPathStr + "Other']";
                break;
            default:
                completexPath = xPathStr + "Other']";
                break;
        }
        var el = element(by.xpath(completexPath));
        el.click().then(function () {
            util.uploadDocx();
        })
        return this;

    };
    this.addDocument = function(doctype) {
        this.clickNewDocument();
        this.selectDocument(doctype);
        return this;
    };

    this.returnDetailsUploadedImage = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.detailsUploadedImage))), 10000, "The image is not uploaded");
    };
    this.clickCheckin = function() {
        checkIn.click();
        return this;
    };
    this.clickCheckOut = function() {
        checkOut.click();
        return this;
    };
    this.clickCancelEditing = function() {
        cancelEditing.click();
        return this;
    };

    this.insertDetailsTextAreaText = function(details) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.taskspage.locators.detailsTextArea))), 30000, "Details text area is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.detailsTextArea))), 30000, "Details text area is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.taskspage.locators.detailsTextArea))), 30000, "Details text area is not clickable").then(function() {
                    detailsTextArea.clear();
                    detailsTextArea.sendKeys(details);
                    return this;
                });
            });
        });
    };
    this.clickSaveDetailsButton = function() {
        detailsSaveBtn.click();
        return this;
    };
    this.clickRefreshButton = function() {
        refreshBtn.click();
        return this;
    };
    this.validateDetailsTextArea = function(text, error) {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.detailsTextArea))), 30000, "Details text area is not visible").then(function() {
            expect(detailsTextArea.getText()).toContain(text, error);
        });
    };
    this.returnDetailsTextArea = function () {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.detailsTextArea))), 30000, "Details text area is not visible").then(function() {
            return detailsTextArea.getText();
        });
    }
    this.clickInsertLinkInDetails = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.detailsLinkBtn))), 30000, "Details link button is not visible").then(function() {
            detailsLinkBtn.click();
            return this;
        });
    };
    this.returnInsertLinkTitle = function() {
        return insertLinkTitle.getText();
    };
    this.insertDetailsTextAreaLink = function(text, url) {
        insertLinkText.click();
        insertLinkText.sendKeys(text);
        insertLinkUrl.clear();
        insertLinkUrl.sendKeys(url);
        insertLinkBtn.click();
    };
    this.clickDetailsAddPicture = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.detailsPicture))), 30000, "Picture icon in details is not visible").then(function() {
            detailsPicture.click();
            return this;
        });
    };
    this.uploadPicture = function() {
        browseButton.click().then(function() {
            util.uploadPng();
        });
        return this;
    };

    this.navigateToPage = function(link) {
        xPathStr = ".//a[@title='";
        var completexPath = xPathStr + link + "']";
        var el = element(by.xpath(completexPath));
        browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, "Link " + link + " is not visible").then(function() {
            browser.executeScript('arguments[0].click()', el);
        });
        return this;
    }

    this.rightClickDocument = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.docTitle))), 30000, "Document title is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.docTitle))), 30000, "Document title is not visible").then(function() {
                docTitle.click().then(function() {
                    browser.actions().click(protractor.Button.RIGHT).perform();
                })
            });
        });
        return this;
    };
    this.rightClickFileTitle = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.fileTitle))), 30000, "File title is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.fileTitle))), 30000, "File title is not visible").then(function() {
                fileTitle.click();
                browser.actions().click(protractor.Button.RIGHT).perform();

            });
        });
        return this;

    };
    this.validateChekoutEnabled = function() {
        return !checkoutDisabled.isPresent();
    };
    this.validateCheckinEnabled = function() {
        return !checkinDisabled.isPresent();
    };
    this.validateCancelEditingEnabled = function() {
        return !cancelEditingDisabled.isPresent();
    };
    this.lockIconIsPresent = function() {
        return lockIcon.isPresent();
    };
    this.clickTasksLink = function() {
        tasksLink.click();
        return this;
    };
    this.insertReportTitle = function(reporttitle) {
        browser.wait(EC.presenceOf(element(by.name(Objects.basepage.locators.reportTitle))), 30000, "Report title field is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.name(Objects.basepage.locators.reportTitle))), 30000, "Report title field is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.basepage.locators.reportTitle))), 30000, "Report title field is not clickable").then(function() {
                    reportTitle.sendKeys(reporttitle);
                });
            });
        });
    }

    this.clickParticipantTab = function() {
        participantTab.click();
        return this;
    }

    this.selectParticipant = function(type, participant) {

        var participantType = element(by.linkText(type));
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.selectParticipantType))), 10000, "Select participant drop down is not visible").then(function() {
            selectParticipantType.click().then(function() {
                participantType.click().then(function() {
                    selectparticipant.click().then(function() {
                        browser.driver.switchTo().defaultContent();
                        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 10000, "Search for user input is not displayed").then(function() {
                            searchForUserInput.sendKeys(participant).then(function() {
                                searchForUserBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 30000, "Searched user is not displayed").then(function() {
                                        searchedUser.click().then(function() {
                                            okBtn.click();
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        return this;
    };

    this.clickSubmitButton = function() {
        submitButton.click();
        return this;
    };
    this.submitReportOfInvestigation = function(reporttitle, approver) {
        this.insertReportTitle(reporttitle);
        this.selectApprover(approver);
        this.clickSubmitButton();
    }

    this.returnParticipantTypeFirstRow = function() {
        return participantTypeFirstRow.getText();
    }
    this.returnParticipantNameFirstRow = function() {
        return participantNameFirstRow.getText();
    }
    this.returnParticipantTypeSecondRow = function() {
        return participantTypeSecondRow.getText();
    }
    this.returnParticipantNameSecondRow = function() {
        return participantNameSecondRow.getText();
    }
    this.returnParticipantTypeThirdRow = function() {
        return participantTypeThirdRow.getText();
    }
    this.returnParticipantNameThirdRow = function() {
        return participantNameThirdRow.getText();
    }
    this.returnParticipantTypeForthRow = function() {
        return participantTypeForthRow.getText();
    }
    this.returnParticipantNameForthRow = function() {
        return participantNameForthRow.getText();
    }
    this.returnParticipantTypeFifthRow = function() {
        return participantTypeFifthRow.getText();
    }
    this.returnParticipantNameFifthRow = function() {
        return participantNameFifthRow.getText();
    }

    this.participantTable = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.participantLinkBtn))), 30000, "Partipants link button is not displayed").then(function() {
            participantsLinkBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.participantTable))), 30000, "Partipants Tabe is not displayed")
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.participantTableRow))), 30000, "Participant table row is not visible").then(function() {
                    priorityType.click();
                    browser.sleep(3000);
                });
            });
        });
        return this;
    };
    this.moveToTab = function() {
        browser.getAllWindowHandles().then(function(handles) {
            newWindowHandle = handles[1]; // this is your new window
            browser.switchTo().window(newWindowHandle);
        });
        return this;
    };
    this.clickDocTreeExpand = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.basepage.locators.docTreeExpand))), 30000, "Expand doc tree element is not visible").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.docTreeExpand))), 30000, "Expand doc tree element is not visible").then(function() {
                docTreeExpand.click();
            });
        });
        return this;
    };
    this.clickDocViewNotesLink = function() {
        browser.wait(EC.visibilityOf(element(by.linkText(Objects.basepage.locators.docViewNotesLink))), 30000, "Document view notes link is not visible").then(function() {
            docViewNotesLink.click();
        });
        return this;
    };
    this.clickDocViewAddNote = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.docViewAddNoteButton))), 30000, "Document view add note button is not visible").then(function() {
            docViewAddNoteButton.click();
        });
        return this;
    };
    this.insertDocViewNote = function(note) {
        browser.wait(EC.visibilityOf(element(by.model(Objects.taskspage.locators.notesTextArea))), 30000, "Notes text area is not visible").then(function() {
            noteText.sendKeys(note);
        });
        return this;
    };
    this.clickSaveButton = function() {
        browser.wait(EC.visibilityOf(element(by.buttonText(Objects.basepage.locators.saveButton))), 30000, "Save button is not visible").then(function() {
            saveButton.click();
        });
        return this;
    };
    this.submitNote = function(note) {
        this.clickDocViewAddNote();
        this.insertDocViewNote(note);
        this.clickSaveButton();
        return this;
    };
    this.returnSavedNoteInGrid = function() {
        browser.wait(EC.presenceOf(element(by.css(Objects.basepage.locators.noteColumnValue))), 30000, "After save note column value is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.basepage.locators.noteColumnValue))), 30000, "After save note column value is not visible").then(function() {
                return noteColumnValue.getText();
            });
        });
    }

    this.clickEditAssigneeBtn = function() {

        browser.wait(EC.textToBePresentInElement((participantTypeSecondRow), "assignee"), 10000, "Text assignee is not present in second row participant element").then(function() {
            editAssigneeBtn.click();
        });
    }

    this.editAssigneeInParticipantTable = function(participant) {

        browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.modalParticipantName))), 30000, "Modal participant name is not visible").then(function() {
            modalParticipantName.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 10000, "Search for user input is not displayed").then(function() {
                    searchForUserInput.sendKeys(participant).then(function() {
                        searchForUserBtn.click().then(function() {
                            browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 30000, "Searched user is not displayed").then(function() {
                                searchedUser.click().then(function() {
                                    browser.sleep(3000);
                                    okBtn.click().then(function() {
                                        browser.sleep(3000);
                                        saveParticipantBtn.click().then(function() {
                                            browser.wait(EC.textToBePresentInElement((participantNameSecondRow), participant), 10000, "Text participant is not present in second row participant element");
                                        });
                                    });
                                });

                            });
                        });

                    });

                });
            });
        });
        return this;
    }

    this.addParticipantFromParticipantTable = function(type, participant) {

        addParticipantBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.modalParticipantType))), 30000, "Modal participant type is not visible").then(function() {
                modalParticipantType.selectByText(type).then(function() {
                    modalParticipantName.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 10000, "Search for user input is not displayed").then(function() {
                            searchForUserInput.sendKeys(participant).then(function() {
                                searchForUserBtn.click().then(function() {
                                    browser.sleep(3000);
                                    browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 30000, "Searched user is not displayed").then(function() {
                                        searchedUser.click().then(function() {
                                            browser.sleep(3000);
                                            okBtn.click().then(function() {
                                                browser.sleep(3000);
                                                saveParticipantBtn.click();
                                                browser.sleep(5000);
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        return this;
    }

    this.clickDeleteAsigneeBtn = function() {

        assigneeDeleteBtn.click();
    }
    this.verifyIfAssigneeCanBeDeleted = function() {
        browser.wait(EC.textToBePresentInElement((participantTypeSecondRow), "assignee"), 10000, "Assignee should not be enable to be deleted");
    }

    this.clickSpecialTypeDeleteBtn = function() {
        specialTypeDeleteBtn.click();
    }
    this.verifyIfSpecialTypeCaneBeDeleted = function() {
        browser.wait(EC.textToBePresentInElement((participantNameFirstRow), "*"), 10000, "Special Type should not be enable to be deleted");
    }

    this.clickOwningGroupDeleteBtn = function() {
        owningGroupDeleteBtn.click();
    };

    this.verifyIfOwningGroupCanBeDeleted = function() {
        browser.wait(EC.textToBePresentInElement((participantTypeThirdRow), "owning group"), 10000, "Owning Group should not be enable to be deleted");
    }

    this.clickReaderDeleteBtn = function() {
        readerDeleteBtn.click();
    }
    this.verifyIfReaderCanBeDeleted = function() {
        browser.wait(EC.textToBePresentInElement((participantTypeForthRow), "reader"), 10000, "reader should not be enable to be deleted");
    }
    this.waitForPriority = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.priority))), 20000, "priority is not visible");
    };
    this.editPriority = function(priority) {

        priorityLink.click().then(function() {
            priorityDropDownEdit.selectByText(priority).then(function() {
                priorityBtn.click();
            });
        });
        return this;
    };
    this.returnPriority = function() {
        return priorityLink.getText();
    };
    this.editAssignee = function(assignee) {
        assigneeLink.click().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.assigneeDropDown))), 5000, "Assignee drop down is not present in DOM").then(function() {
                assigneeDropDown.selectByText(assignee).then(function() {
                    assigneeBtn.click();
                });
            });
        });
        return this;
    }

    this.waitForAssignee = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.assignee))), 30000, "Assignee is not visible");
    };

    this.returnAssignee = function() {

        return assigneeLink.getText();
    };

    this.clickSubmitBtn = function() {
        browser.sleep(3000);
        browser.executeScript('arguments[0].click()', submitBtn);
        return this;
    }

    this.clickPreferenceLink = function() {
        browser.wait(EC.visibilityOf(element(by.linkText(Objects.preferencesPage.locators.preferenceLink))), 10000, "Preference link is not displayed").then(function() {
            preferenceLink.click();
        });
    }

    this.navigateToPreferencePage = function() {
        this.clickFullNameLink();
        this.clickPreferenceLink();
    }

    this.waitForOverView = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.overviewLink))), 20000, "Owerview Link is nor displayed").then(function() {
            browser.sleep(5000);
        });
    }

    this.verifyIfWidgetIsDisplayed = function(widget, title) {

        element.all(by.css('[adf-widget-type=' + widget + ']')).then(function(items) {
            expect(items.length).toBe(1, "The" + " " + title + " " + "widget is enabled and not displayed");
            expect(element(by.css('[adf-widget-type=' + widget + ']')).element(by.css('.ng-scope')).getText()).toEqual(title);
        });
    }

    this.verifyIfWidgetIsNotDisplayed = function(widget, title) {

        element.all(by.css('[adf-widget-type=' + widget + ']')).then(function(items) {
            expect(items.length).toBe(0, "The" + " " + title + " " + "widget is disabled it should not be displayed");
        });
    }

    this.verifyTasksTableColumnsNumber = function() {

        element.all(by.repeater(Objects.casepage.locators.taskTableRows)).then(function(items) {
            expect(items.length).toBe(7, "The column number in the task table is changed");
        });
    }

    this.verifyTheNotificationMessage = function(objectEvent) {
        browser.driver.switchTo().defaultContent();
        browser.wait(EC.visibilityOf(element(by.css(Objects.basepage.locators.notificationMessage))), 30000, "Notification Message is not displayed").then(function() {
            expect(notificationMessage.getText()).toContain(objectEvent);
        });
    }

    this.clickClaimButton = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.claimButton))), 30000, "Claim button is not displayed").then(function() {
            claimButton.click();
        }).then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.unclaimButton))), 10000, "After is clicked the claim button is still displayed").then(function() {
                browser.wait(EC.textToBePresentInElement((assigneeLink), Objects.casepage.data.assigneeSamuel), 10000, "Assignee is not containing current user after click on claim button");
            });
        });
    }
    this.doubleClickRootFolder = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.taskspage.locators.root))), 30000, "Root document folder is not visible").then(function() {
            root.click();
            browser.actions().doubleClick(root).perform();
        });
        return this;
    }
    this.uploadFile = function() {
        util.uploadDocx();
        return this;
    }
    this.replaceVersion = function(value) {
        docVersionDropDownList.selectByValue(value);
    };

    this.selectRecipient = function(email) {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 30000, "Search user element is not visible").then(function() {
            searchForUserInput.click();
            searchForUserInput.sendKeys(Objects.casepage.data.approverSamuel);
            searchForUserBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.addedReferenceRow)).get(0)), 30000, "There is no found user visible").then(function() {
                    searchedUser.click()
                });
            });
        });
        return this;
    };
    this.clickSendEmailButton = function() {
        browser.wait(EC.visibilityOf(element(by.buttonText(Objects.basepage.locators.sendEmailButton))), 30000, "Send Email button is not visible").then(function() {
            browser.executeScript('arguments[0].click()', sendEmailButton);
        });
        return this;
    };
    this.sendEmail = function(email) {
        this.selectRecipient(email);
        this.clickSendEmailButton();
        return this;
    }

    this.clickUnclaimButton = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.unclaimButton))), 30000, "Unclaim button is not displayed").then(function() {
            unclaimButton.click();
        }).then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.claimButton))), 10000, "After is clicked the unclaim button is still displayed").then(function() {
                browser.wait(EC.textToBePresentInElement((assigneeLink), ""), 10000, "After click on unclaim current user should be removed from assignee");
            });
        });
        return this;
    };

    this.clickLastElementInTreeData = function() {
        browser.wait(EC.visibilityOf($('.fancytree-lastsib')), 20000, "Elements in the data tree are not displayed");
        var lastElement = $('.fancytree-lastsib');
        lastElement.click();
    }

    this.returnParticipantTypeAlert = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.participantTypeConflictMessage))), 10000, "Participant conflict message is not displayed");
        return participantTypeConflictMessage.getText();
    }


    this.clickAddParticipantTypeSecondRowbtn = function() {

        browser.executeScript('arguments[0].click()', addParticipantTypeSecondRowbtn);
    }

    this.selectParticipantSecondRow = function(type, participant) {

        var participantType = element(by.linkText(type));
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.selectParticipantTypeSecondRow))), 10000, "Participant type second row is not visible").then(function() {
            selectParticipantTypeSecondRow.click().then(function() {
                participantType.click().then(function() {
                    selectParticipantSecondRow.click().then(function() {
                        browser.driver.switchTo().defaultContent();
                        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 10000, "Search for user input is not displayed").then(function() {
                            searchForUserInput.sendKeys(participant).then(function() {
                                searchForUserBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 30000, "Searched user is not displayed").then(function() {
                                        searchedUser.click().then(function() {
                                            okBtn.click();
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        return this;
    };

    this.clickSublink = function(link) {
        var xPathStr = "//span[contains(text(),'";
        var completexPath;
        completexPath = xPathStr + link + "')]";
        browser.wait(EC.presenceOf(element(by.xpath(completexPath))), 30000, "Sublink " + link + " is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, "Sublink " + link + " is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(completexPath))), 30000, "Sublink " + link + "is not clickable").then(function() {
                    var el = element(by.xpath(completexPath));
                    browser.executeScript('arguments[0].click()', el);
                });
            });
        });
    }

    this.verifyTimeWidgetData = function(time) {

        browser.wait(EC.visibilityOf(element(by.css(Objects.basepage.locators.timeCanvasData))), 30000, "Time widget data is not displayed");
        timeCanvasData.evaluate('time.data').then(function(data) {
            var date = "" + data + "";
            expect(date).toEqual(time, "The time in the time widget is not updated");
        });
    }

    this.verifyFirstElementNameNoAccess = function() {

        browser.wait(EC.textToBePresentInElement((firstElementInList), "No Access"), 10000, "Name of the object in the list should be No Access");
    }

    this.returnOwningGroup = function() {
        return owningGroup.getText();
    };

    this.editOwningGroup = function(owninggroup) {

        owningGroup.click().then(function() {
            owningGroupDropDown.selectByText(owninggroup).then(function() {
                owningGroupConfirmBtn.click();
            });
        });
        return this;
    };

    this.waitForComplaintID = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.caseID))), 60000, "Case ID is not present").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.caseID))), 60000, "Case ID is not displayed");
        });
        return this;
    };


    this.clickTreeSortersBtn = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.basepage.locators.treeSortersBtn))), 30000, "Tree sorter button is not visible").then(function() {
            treeSortersBtn.click();
        });
        return this;
    }
    this.returnSortByIdDesc = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.basepage.locators.sortByIdDesc))), 30000, "Sort element by id desc is not visible");
        return sortByIdDesc.getText();
    }

    this.searchForObject = function(object) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 10000, "Search for object input is not displayed").then(function() {
            searchForUserInput.sendKeys(object).then(function() {
                searchForUserBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 30000, "Searched object is not displayed").then(function() {
                        searchedUser.click().then(function() {
                            okBtn.click();
                        });
                    });
                });
            });
        });
        return this;
    }

    this.clickOnTask = function(type) {
        var xPathStart = "//a[contains(text(),'";
        var completexPath;
        switch (type) {
            case "Automatic Task on Change Case Status":
                completexPath = xPathStart + "Review Request to Change Case Status')]";
                break;
            case "Automatic Task on Creation":
                completexPath = xPathStart + "Review')]";
                break;
            case "Ad hoc task":
                completexPath = xPathStart + "Ad hoc task')]";
                break;
            case "Automatic Task on Close Complaint":
                completexPath = xPathStart + "Review Request to Close Complaint')]";
                break;
            default:
                completexPath = xPathStart + "Ad hoc task'])";
                break;
        }
        var el = element(by.xpath(completexPath));
        el.click();
        return this;

    }

    this.validateTaskTableValue = function (type, column, expectedValue) {
        var xPathStart = "//a[contains(text(),'";
        var completexPath;
        var xPathEnd;
        switch (column) {
            case "Title":
                xPathEnd = "/div[2]/a";
                break;
            case "Assignee":
                xPathEnd = "/div[3]/div";
                break;
            case "Created":
                xPathEnd = "/div[4]/div";
                break;
            case "Priority":
               xPathEnd = "/div[5]/div";
                break;
            case "Due":
                xPathEnd = "/div[6]/div";
                break;
            case "Status":
                xPathEnd = "/div[7]/div";
                break;
            default:
                xPathEnd = "/div[2]/a";
                break;
        }
        switch (type) {
            case "Automatic Task on Change Case Status":
                completexPath = xPathStart + "Review Request to Change Case Status')]/../.." + xPathEnd;
                break;
            case "Automatic Task on Creation":
                completexPath = xPathStart + "Review')]/../.." + xPathEnd;
                break;
            case "Ad hoc task":
                completexPath = xPathStart + "Ad hoc task')]/../.." + xPathEnd;
                break;
            case "Automatic Task on Close Complaint":
                completexPath = xPathStart + "Review Request to Close Complaint')]/../.." + xPathEnd;
                break;
            default:
                completexPath = xPathStart + "Ad hoc task']/../.." + xPathEnd;
                break;
        }
        var el = element(by.xpath(completexPath));
        browser.wait(EC.visibilityOf(element(by.xpath(completexPath))), 30000, "task is not visible in the grid").then(function () {
            if (column == "Title") {
                expect(el.getText()).toContain(expectedValue, "Task table " + column + " value is not correct in the grid");
            }
            else
            {
                expect(el.getText()).toEqual(expectedValue, "Task table " + column + " value is not correct in the grid");
            }
        });
    }

    this.returnObjectStatus = function() {
        return objectStatus.getText();
    }

    this.returnDocViewOpened = function (added) {
        browser.wait(EC.presenceOf(element(by.id(Objects.casepage.locators.doc))), 30000, "Document is not present in DOM").then(function () {
            browser.wait(EC.visibilityOf(element(by.id(Objects.basepage.locators.doc))), 30000, "Doc Author value is not visible").then(function () {
                expect(doc.isPresent()).toBe(added, "In document view, doc element is not displayed");
            });
        });
    }

    this.switchToDocIframes = function() {

        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.model(Objects.taskspage.locators.notesTextArea))), 30000, "Notes text area is not visible").then(function() {
            browser.wait(EC.presenceOf(element(by.className("snowbound-iframe"))), 30000, "Document i-frame is not present in DOM").then(function () {
                browser.wait(EC.visibilityOf(element(by.className("snowbound-iframe"))), 30000, "Document i-frame is not visible").then(function () {
                    browser.switchTo().frame(browser.driver.findElement(by.className("snowbound-iframe")));
                })
            })
        })
        return this;
    };


}

module.exports = new BasePage();
