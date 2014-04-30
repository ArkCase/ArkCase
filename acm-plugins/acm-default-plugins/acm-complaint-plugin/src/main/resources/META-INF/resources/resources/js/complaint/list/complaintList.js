/**
 * ComplaintList is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var ComplaintList = ComplaintList || {
    initialize: function() {
        ComplaintList.Object.initialize();
        ComplaintList.Event.initialize();
        ComplaintList.Page.initialize();
        ComplaintList.Rule.initialize();
        ComplaintList.Service.initialize();
        ComplaintList.Callback.initialize();

        ComplaintList.Event.onPostInit();
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

