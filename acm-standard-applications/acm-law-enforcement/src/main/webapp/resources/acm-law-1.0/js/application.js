/**
 * Application
 *
 * Application specific functions
 *
 * @author jwu
 */
var Application = Application || {
    run : function() {
        var acmModules = [];

        if ("undefined" != typeof Acm) {
            acmModules.push(Acm);
        }
        if ("undefined" != typeof AcmEx) {
            acmModules.push(AcmEx);
        }
        if ("undefined" != typeof App) {
            acmModules.push(App);
        }
        if ("undefined" != typeof Topbar) {
            acmModules.push(Topbar);
        }
        if ("undefined" != typeof Sidebar) {
            acmModules.push(Sidebar);
        }
        if ("undefined" != typeof Login) {
            acmModules.push(Login);
        }
        if ("undefined" != typeof Dashboard) {
            acmModules.push(Dashboard);
        }
        if ("undefined" != typeof Complaint) {
            acmModules.push(Complaint);
        }
        if ( "undefined" != typeof CaseFile ) {
            acmModules.push(CaseFile);
        }
        if ("undefined" != typeof ComplaintWizard) {
            acmModules.push(ComplaintWizard);
        }
        if ("undefined" != typeof ComplaintList) {
            acmModules.push(ComplaintList);
        }
        if ("undefined" != typeof Task) {
            acmModules.push(Task);
        }
        if ("undefined" != typeof TaskWizard) {
            acmModules.push(TaskWizard);
        }
        if ("undefined" != typeof TaskList) {
            acmModules.push(TaskList);
        }
        if ("undefined" != typeof Search) {
            acmModules.push(Search);
        }
        if ("undefined" != typeof Admin) {
            acmModules.push(Admin);
        }
        if ("undefined" != typeof Audit) {
            acmModules.push(Audit);
        }
        if ("undefined" != typeof AdminAccess) {
            acmModules.push(AdminAccess);
        }
        if ("undefined" != typeof Report) {
            acmModules.push(Report);
        }
        if ("undefined" != typeof Profile) {
            acmModules.push(Profile);
        }

        for (var i = 0; i < acmModules.length; i++) {
            var module = acmModules[i];
            if ("undefined" != typeof module) {
                if (module.create) {
                    module.create();
                }
            }
        }
        for (var i = 0; i < acmModules.length; i++) {
            var module = acmModules[i];
            if ("undefined" != typeof module) {
                if (module.onInitialized){
                    Acm.deferred(module.onInitialized);
                }
            }
        }
    }

    ,initSessionData: function() {
        sessionStorage.setItem("AcmProfile", null);

        sessionStorage.setItem("AcmApprovers", null);
        sessionStorage.setItem("AcmComplaintTypes", null);
        sessionStorage.setItem("AcmPriorities", null);

        sessionStorage.setItem("AcmCaseAssignees", null);
        sessionStorage.setItem("AcmCaseTypes", null);
        sessionStorage.setItem("AcmCasePriorities", null);

        sessionStorage.setItem("AcmQuickSearchTerm", null);
        sessionStorage.setItem("AcmAsnList", null);
        sessionStorage.setItem("AcmAsnData", null);
        sessionStorage.setItem("AcmCaseFileTreeInfo", null);
        sessionStorage.setItem("AcmComplaintTreeInfo", null);
        sessionStorage.setItem("AcmAdminTreeInfo", null);

        sessionStorage.setItem("AcmTaskAssignees", null);

    }
}