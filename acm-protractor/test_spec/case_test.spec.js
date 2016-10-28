var logger = require('../log');
var casePage = require('../Pages/case_page.js');
var authentication = require('../authentication.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var utils = require('../util/utils.js');
var flag = false;


function testAsync(done) {

    setTimeout(function() {
        flag = true;
        done();
    }, 20000);
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

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForCaseTitle();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle);
    });


    it('should create new case and verify case type', function() {


        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForCaseType();
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType);
    });


    it('should create new case and change case status to closed, verify the automated task in tasks table and approve', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatusClosed();
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();
        taskPage.clickApproveBtn();
        expect(taskPage.returnTaskState()).toEqual(Objects.taskspage.data.taskStateClosed, 'The task state should be CLOSED');
    });

    it('should create new case and verify the priority filed', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForPriority();
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium);

    });


    it('should create new case and verify the created date', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForCreatedDate();
        expect(casePage.returnCreatedDate()).toEqual(utils.returnToday("/"));

    });


    it('shoul create new case and edit the priority to High', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForPriority();
        casePage.editPriority('High');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityHigh);

    });

    it('shoul create new case and edit the priority to Medium', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForPriority();
        casePage.editPriority('Medium');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityMedium);

    });

    it('shoul create new case and edit the priority to Medium', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Arson");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForPriority();
        casePage.editPriority('Expedite');
        expect(casePage.returnPriority()).toEqual(Objects.casepage.data.priorityExpedite);
    });


    it('shoul create new case and edit the assignee from ann to samuel', function() {

        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle, "Agricultural");
        casePage.initiatorInformation(Objects.casepage.data.firstName, Objects.casepage.data.lastName);
        casePage.waitForAssignee();
        casePage.editAssignee("samuel-acm").waitForAssignee();
        expect(casePage.returnAssignee()).toEqual(Objects.casepage.data.assigneeSamuel);
    });



});
