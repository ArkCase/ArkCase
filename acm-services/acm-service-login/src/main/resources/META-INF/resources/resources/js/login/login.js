/**
 * Login is namespace component for Login plugin
 *
 * @author jwu
 */
var Login = Login || {
    create: function() {
        //
        //to do, move to individual plugin ro acm-law app
        //
        sessionStorage.setItem("AcmProfile", null);

        sessionStorage.setItem("AcmApprovers", null);
        sessionStorage.setItem("AcmComplaintTypes", null);
        sessionStorage.setItem("AcmPriorities", null);

        sessionStorage.setItem("AcmCaseAssignees", null);
        sessionStorage.setItem("AcmCaseTypes", null);
        sessionStorage.setItem("AcmCasePriorities", null);

        sessionStorage.setItem("AcmQuickSearchTerm", null);
        sessionStorage.setItem("AcmAsnList", null);
        sessionStorage.setItem("AcmCaseFileTreeInfo", null);
        sessionStorage.setItem("AcmComplaintTreeInfo", null);
        sessionStorage.setItem("AcmAdminTreeInfo", null);

    }



};

