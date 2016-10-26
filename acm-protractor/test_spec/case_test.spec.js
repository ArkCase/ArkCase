var logger = require('../log');
var casePage = require('../Pages/case_page.js');
var authentication = require('../authentication.js');
var Objects = require('../json/Objects.json');
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


/*
  it('should create new case and verify case title', function() {

       casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle);
        casePage.initiatorInformation(Objects.casepage.data.firstName,Objects.casepage.data.lastName);
        casePage.switchToDefaultContent();
        casePage.waitForCaseTitle();
        expect(casePage.returnCaseTitle()).toEqual(Objects.casepage.data.caseTitle);
    });


    it('should create new case and verify case type', function() {


        casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle);
        casePage.initiatorInformation(Objects.casepage.data.firstName,Objects.casepage.data.lastName);
        casePage.switchToDefaultContent();
        casePage.waitForCaseType();
        expect(casePage.returnCaseType()).toEqual(Objects.casepage.data.casesType);
    });
*/

    it('should create new case and change case status to closed, verify the automated task in tasks table', function() {

       casePage.navigateToNewCasePage().switchToIframes().submitGeneralInformation(Objects.casepage.data.caseTitle);
        casePage.initiatorInformation(Objects.casepage.data.firstName,Objects.casepage.data.lastName);
        casePage.switchToDefaultContent();
        casePage.waitForChangeCaseButton();
        casePage.clickChangeCaseBtn();
        casePage.switchToIframes().selectCaseStatusClosed();
        casePage.selectApprover(Objects.casepage.data.approverSamuel).chnageCaseSubmit();
        casePage.clickTasksLinkBtn().waitForTasksTable();
        expect(casePage.returnAutomatedTask()).toContain(Objects.casepage.data.automatedTaskTitle);
        casePage.clickTaskTitle();

    });

});
