/**
 * Application
 *
 * Application specific functions
 *
 * @author jwu
 */
var Application = Application || {
    run : function() {

        // Init I18n functinality
        var lng= "en";
        var namespaces = ['common'];

        // Get namespace from detailData
        var namespace = Acm.Object.MicroData.get("resourceNamespace");
        if (namespace) {
            namespaces.push(namespace);
        }

        i18n.init({
                useLocalStorage: false,
                localStorageExpirationTime: 86400000, // 1 week
                load: 'current', // Prevent loading of 'en' locale
                fallbackLng: false,
                lng: lng,
                ns:{
                    namespaces: namespaces
                },
                lowerCaseLng: true,
                resGetPath: App.getContextPath() + '/api/latest/plugin/admin/labelconfiguration/resource?lang=__lng__&ns=__ns__'
            }, function() {
            // Apply resources to DOM
            $('*[data-i18n]').i18n();

            // Send "i18n ready" global event
            $(document).trigger('i18n-ready');


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
                    if (module.onInitialized) {
                        Acm.deferred(module.onInitialized);
                    }
                }
            }
        });
    }

    ,SESSION_DATA_PROFILE               : "AcmProfile"
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
    ,SESSION_DATA_QUICK_SEARCH_TERM     : "AcmQuickSearchTerm"
    ,SESSION_DATA_ASN_LIST              : "AcmAsnList"
    ,SESSION_DATA_ASN_DATA              : "AcmAsnData"
    ,SESSION_DATA_COMPLAINT_TREEINFO    : "AcmComplaintTreeInfo"
    ,SESSION_DATA_ADMIN_TREEINFO        : "AcmAdminTreeInfo"
    ,SESSION_DATA_TASK_ASSIGNEES        : "AcmTaskAssignees"
    ,SESSION_DATA_TASK_PRIORITIES       : "AcmTaskPriorities"

    ,initSessionData: function() {
        sessionStorage.setItem("AcmProfile", null);

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

        sessionStorage.setItem("AcmQuickSearchTerm", null);
        sessionStorage.setItem("AcmAsnList", null);
        sessionStorage.setItem("AcmAsnData", null);
        sessionStorage.setItem("AcmCaseFileTreeInfo", null);
        sessionStorage.setItem("AcmComplaintTreeInfo", null);
        sessionStorage.setItem("AcmAdminTreeInfo", null);

        sessionStorage.setItem("AcmTaskAssignees", null);
        sessionStorage.setItem(this.SESSION_DATA_TASK_PRIORITIES, null);

    }
}