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


//        Login.Object.initialize();
//        Login.Event.initialize();
//        Login.Page.initialize();
//        Login.Rule.initialize();
//        Login.Service.initialize();
//        Login.Callback.initialize();
//
//        Acm.deferred(Login.Event.onPostInit);
    }

//    ,Object: {}
//    ,Event:{}
//    ,Page: {}
//    ,Rule: {}
//    ,Service: {}
//    ,Callback: {}
};

