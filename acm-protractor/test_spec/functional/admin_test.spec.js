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
                expect(adminPage.returnAuthorized()).toContain(groups[i], "Samuel is not authorized to view " + data.reportName);
            }
        });
    });

    it('should validate that LDAP configuration for Armedia is set', function () {
        adminPage.navigateToPage("Admin").clickSubLink("LDAP Configuration");
        expect(adminPage.returnArmediaDirectoryName()).toEqual(Objects.adminPage.data.ArmediaDirectoryName, "LDAP Directory name is not correct");
        expect(adminPage.returnArmediaLDAPUrl()).toEqual(Objects.adminPage.data.ArmediaLDAPUrl, "LDAP Directory url is not correct");
    });

    //have to be checked what is Role what is group and how they are connected in order to test this

    using([{widgetName: "News Widget"}, {widgetName: "My Tasks Widget"}, {widgetName: "My Cases Widget"}, {widgetName: "My Complaints Widget"}, {widgetName: "New Complaints"}, {widgetName: "Weather Widget"}, {widgetName: "Team Workload"}, {widgetName: "Cases By Status"} ], function(data){
        it('should validate that samuel supervisor is enabled to add ' + data.widgetName, function () {
            adminPage.navigateToPage("Admin").clickSubLink("Dashboard Configuration").ChooseListBoxValue(data.widgetName);
            var groups = utils.readGroupsFromJson("samuel-acm");
            for (var i in groups)
            {
                expect(adminPage.returnAuthorized()).toContain(groups[i]);
            }
        });
    });

    it('should validate that report of investigation form is configured correctly', function () {
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

    it('should validate workflow configuration for complaint rewiew and approval process and ACM doc approval', function () {
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









