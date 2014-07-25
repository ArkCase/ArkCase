/**
 * Login is namespace component for Login plugin
 *
 * @author jwu
 */
var Login = Login || {
    initialize: function() {
        sessionStorage.setItem("AcmApprovers", null);
        sessionStorage.setItem("AcmComplaintTypes", null);
        sessionStorage.setItem("AcmPriorities", null);
        sessionStorage.setItem("AcmQuickSearchTerm", null);
    }

};

