/**
 * Login is namespace component for Login plugin
 *
 * @author jwu
 */
var Login = Login || {
    create: function() {
        sessionStorage.setItem("AcmApprovers", null);
        sessionStorage.setItem("AcmComplaintTypes", null);
        sessionStorage.setItem("AcmPriorities", null);
        sessionStorage.setItem("AcmQuickSearchTerm", null);
        sessionStorage.setItem("AcmAsnList", null);
        sessionStorage.setItem("AcmCaseFileTreeInfo", null);
        sessionStorage.setItem("AcmComplaintTreeInfo", null);
        sessionStorage.setItem("AcmAdminTreeInfo", null);

    }


};

