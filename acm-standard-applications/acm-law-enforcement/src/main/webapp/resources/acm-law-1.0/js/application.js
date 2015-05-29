/**
 * Application
 *
 * Application specific functions
 *
 * @author jwu
 */
var Application = Application || {
    run : function(context) {

        var acmModules = this.acmModules = [];

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
        if ("undefined" != typeof CaseFile) {
            acmModules.push(CaseFile);
        }
        if ("undefined" != typeof Task) {
            acmModules.push(Task);
        }
        if ("undefined" != typeof TaskOld) {
            acmModules.push(TaskOld);
        }
        if ("undefined" != typeof TaskWizard) {
            acmModules.push(TaskWizard);
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
        if ("undefined" != typeof Report) {
            acmModules.push(Report);
        }
        if ("undefined" != typeof Profile) {
            acmModules.push(Profile);
        }
        if ("undefined" != typeof Subscription) {
            acmModules.push(Subscription);
        }
        if ("undefined" != typeof AcmNotification) {
            acmModules.push(AcmNotification);
        }
        if ("undefined" != typeof Costsheet) {
            acmModules.push(Costsheet);
        }
        if ("undefined" != typeof Timesheet) {
            acmModules.push(Timesheet);
        }
        if ("undefined" != typeof Tag) {
            acmModules.push(Tag);
        }
        if ("undefined" != typeof AcmDocument) {
            acmModules.push(AcmDocument);
        }
        if ("undefined" != typeof CaseFileSplit) {
            acmModules.push(CaseFileSplit);
        }
        if ("undefined" != typeof IssueCollector) {
            acmModules.push(IssueCollector);
        }


        Application.prepareModules(context);


        if (Acm.isEmpty(context.loginPage)) {
            App.Model.Login.setLoginStatus(true);
            Application.configModules();
        }

        var promiseConfig = App.checkConfig(context);
        var promiseI18n = App.initI18n(context);

        $.when(promiseConfig, promiseI18n).done(function() {
            Application.createModules(context);
            Application.initModules(context);
        });
    }


    ,prepareModules : function(context) {
        var acmModules = Application.acmModules;
        for (var i = 0; i < acmModules.length; i++) {
            var module = acmModules[i];
            if ("undefined" != typeof module) {
                if (module.prepare) {
                    module.prepare(context);
                }
            }
        }
    }

    ,configModules : function(context) {
        var acmModules = Application.acmModules;
        for (var i = 0; i < acmModules.length; i++) {
            var module = acmModules[i];
            if ("undefined" != typeof module) {
                if (module.config) {
                    module.config(context);
                }
            }
        }
    }

    ,createModules : function(context) {
        var acmModules = Application.acmModules;
        for (var i = 0; i < acmModules.length; i++) {
            var module = acmModules[i];
            if ("undefined" != typeof module) {
                if (module.create) {
                    module.create(context);
                }
            }
        }
    }

    ,initModules : function(context) {
        var acmModules = Application.acmModules;
        for (var i = 0; i < acmModules.length; i++) {
            var module = acmModules[i];
            if ("undefined" != typeof module) {
                if (module.onInitialized) {
                    //Acm.deferred(module.onInitialized);
                    Acm.deferredTimer(module.onInitialized).done(function(fn) {
                        fn(context);
                    });
                }
            }
        }
    }


    ,SESSION_DATA_COMPLAINT_ASSIGNEES   : "AcmComplaintApprovers"
    ,SESSION_DATA_COMPLAINT_TYPES       : "AcmComplaintTypes"
    ,SESSION_DATA_COMPLAINT_PRIORITIES  : "AcmComplaintPriorities"
	,SESSION_DATA_COMPLAINT_GROUPS		: "AcmComplaintGroups"
    ,SESSION_DATA_COMPLAINT_USERS		: "AcmComplaintUsers"
    ,SESSION_DATA_CASE_FILE_ASSIGNEES   : "AcmCaseAssignees"
    ,SESSION_DATA_CASE_FILE_TYPES       : "AcmCaseTypes"
    ,SESSION_DATA_CASE_FILE_PRIORITIES  : "AcmCasePriorities"
    ,SESSION_DATA_CASE_FILE_GROUPS		: "AcmCaseGroups"
    ,SESSION_DATA_CASE_FILE_USERS		: "AcmCaseUsers"
    ,SESSION_DATA_COMPLAINT_TREEINFO    : "AcmComplaintTreeInfo"
    ,SESSION_DATA_ADMIN_TREEINFO        : "AcmAdminTreeInfo"
    ,SESSION_DATA_TASK_ASSIGNEES        : "AcmTaskAssignees"
    ,SESSION_DATA_TASK_PRIORITIES       : "AcmTaskPriorities"


    ,resetStorageData: function() {
        App.Model.Storage.reset();

        sessionStorage.setItem(this.SESSION_DATA_COMPLAINT_ASSIGNEES, null);
        sessionStorage.setItem(this.SESSION_DATA_COMPLAINT_TYPES, null);
        sessionStorage.setItem(this.SESSION_DATA_COMPLAINT_PRIORITIES, null);
        sessionStorage.setItem(this.SESSION_DATA_COMPLAINT_GROUPS, null);
        sessionStorage.setItem(this.SESSION_DATA_COMPLAINT_USERS, null);

        sessionStorage.setItem(this.SESSION_DATA_CASE_FILE_ASSIGNEES, null);
        sessionStorage.setItem(this.SESSION_DATA_CASE_FILE_TYPES, null);
        sessionStorage.setItem(this.SESSION_DATA_CASE_FILE_PRIORITIES, null);
        sessionStorage.setItem(this.SESSION_DATA_CASE_FILE_GROUPS, null);
        sessionStorage.setItem(this.SESSION_DATA_CASE_FILE_USERS, null);

//        sessionStorage.setItem("AcmCaseFileTreeInfo", null);
//        sessionStorage.setItem("AcmComplaintTreeInfo", null);
//        sessionStorage.setItem("AcmAdminTreeInfo", null);

        sessionStorage.setItem("AcmTaskAssignees", null);
        sessionStorage.setItem(this.SESSION_DATA_TASK_PRIORITIES, null);

    }

//    ,CONFIG_NAME_APP : "app"
//    ,CONFIG_NAME_THIS_APP : "thisApp"
//    ,CONFIG_NAME_ADMIN : "admin"
//    ,CONFIG_NAME_ALFARESCO_RMA : "alfrescoRma"
//    ,CONFIG_NAME_APPLICATION_ROLE_TO_USER_GROUP : "applicationRoleToUserGroup"
//    ,CONFIG_NAME_AUDIT : "audit"
//    ,CONFIG_NAME_CASE_FILE : "caseFile"
//    ,CONFIG_NAME_COMPLAINT : "complaint"
//    ,CONFIG_NAME_CMIS : "cmis"
//    ,CONFIG_NAME_CORRESPONDENCE : "correspondence"
//    ,CONFIG_NAME_COST : "cost"
//    ,CONFIG_NAME_DACSERVICE : "dacService"
//    ,CONFIG_NAME_DASHBOARD : "dashboard"
//    ,CONFIG_NAME_DATASOURCE : "datasource"
//    ,CONFIG_NAME_ECM_FILE_SERVICE : "ecmFileService"
//    ,CONFIG_NAME_EVENT_TYPE : "eventType"
//    ,CONFIG_NAME_ACM_FORMS : "acm-forms"
//    ,CONFIG_NAME_MS_OUTLOOK_INTEGRATION : "msOutlookIntegration"
//    ,CONFIG_NAME_NOTIFICATION : "notification"
//    ,CONFIG_NAME_PARTICIPANT : "participant"
//    ,CONFIG_NAME_PROFILE : "profile"
//    ,CONFIG_NAME_ACM_REPORTS : "acm-reports"
//    ,CONFIG_NAME_REPORT_TO_GROUPS_MAP : "reportToGroupsMap"
//    ,CONFIG_NAME_ACM_ROLES : "acm-roles"
//    ,CONFIG_NAME_SEARCH : "search"
//    ,CONFIG_NAME_SOLR : "solr"
//    ,CONFIG_NAME_SUBSCRIPTION : "subscription"
//    ,CONFIG_NAME_TAG : "tag"
//    ,CONFIG_NAME_TASK : "task"
//    ,CONFIG_NAME_TIME : "time"

}