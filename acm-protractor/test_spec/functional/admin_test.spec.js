var logger = require('../../log');
var utils = require('../../util/utils.js');
var adminPage = require('../../Pages/admin_page.js');
var Objects = require('../../json/Objects.json');
var loginPage = require('../../Pages/login_page.js');
var Users = require('../../json/Users.json');
var flag = false;
var using = require(process.env['USERPROFILE'] + '/node_modules/jasmine-data-provider');

function testAsync(done) {
    // Wait two seconds, then set the flag to true
    setTimeout(function() {
        flag = true;

        // Invoke the special done callback
        done();
    }, 20000);
}


describe('Validate that group in which is logged in user is in authorized group', function() {

    beforeEach(function (done) {

        loginPage.Login(Objects.loginpage.data.adminuser.username, Objects.loginpage.data.adminuser.password);
        testAsync(done);

    });

    afterEach(function () {

        loginPage.Logout();

    });

    using([{reportName: "Case Summary Report"}, {reportName: "ComplaintDispositionCount"}, {reportName: "Complaint Report"}], function(data) {

        it('should validate that logged in user group is in authorized groups in ' +data.reportName+ 'configuration', function () {
            adminPage.navigateToPage("Admin").clickSubLink("Reports Configuration").ChooseListBoxValue(data.reportName);
            var groups = utils.readGroupsFromJson("samuel-acm");
            for (var i in groups) {
                expect(adminPage.returnAuthorized()).toContain(groups[i]);
            }
        });
    });

    it('should validate that LDAP configuration for Armedia is set', function () {
        adminPage.navigateToPage("Admin").clickSubLink("LDAP Configuration");
        expect(adminPage.returnArmediaDirectoryName()).toEqual(Objects.adminPage.data.ArmediaDirectoryName);
        expect(adminPage.returnArmediaLDAPUrl()).toEqual(Objects.adminPage.data.ArmediaLDAPUrl);
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

    it('should validate that report of investigation form is configured correctly', function () {
        adminPage.navigateToPage("Admin").clickSubLink("Form Configuration");
        expect(adminPage.returnCaseFormName()).toEqual(Objects.adminPage.data.formName);
        expect(adminPage.returnCaseApplicationName()).toEqual(Objects.adminPage.data.applicationName);
        expect(adminPage.returnCaseDescription()).toEqual(Objects.adminPage.data.description);
        expect(adminPage.returnCaseTargetFile()).toEqual(Objects.adminPage.data.caseTarget);
        expect(adminPage.returnComplaintFormName()).toEqual(Objects.adminPage.data.formName);
        expect(adminPage.returnComplaintApplicationName()).toEqual(Objects.adminPage.data.applicationName);
        expect(adminPage.returnComplaintDescription()).toEqual(Objects.adminPage.data.description);
        expect(adminPage.returnComplaintTargetFile()).toEqual(Objects.adminPage.data.complaintTarget);
    });

    it('should validate workflow configuration for complaint rewiew and approval process and ACM doc approval', function () {
        adminPage.navigateToPage("Admin").clickSubLink("Workflow Configuration");
        expect(adminPage.returnComplaintBussinessProcessName()).toEqual(Objects.adminPage.data.complaintBussinesProcessName);
        expect(adminPage.returnComplaintBPDescription()).toEqual(Objects.adminPage.data.complaintBPDescription);
        expect(adminPage.returnComplaintBPModified()).toEqual(Objects.adminPage.data.complaintBPModified);
        expect(adminPage.returnComplaintBPAuthor()).toEqual(Objects.adminPage.data.complaintBPAuthor);
        expect(adminPage.returnDocApprovalBussinesProcessName()).toEqual(Objects.adminPage.data.docApprovalBussinesProcessName);
        expect(adminPage.returnDocApprovalBPDescription()).toEqual(Objects.adminPage.data.docApprovalBPDescriptionc);
        expect(adminPage.returnDocApprovalBPModified()).toEqual(Objects.adminPage.data.docApprovalBPModified);
        expect(adminPage.returnDocApprovalBPAuthor()).toEqual(Objects.adminPage.data.docApprovalBPAuthor);
    });


});









