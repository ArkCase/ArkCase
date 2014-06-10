/**
 * ComplaintDetail is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var ComplaintDetail = ComplaintDetail || {
    initialize: function() {
        ComplaintDetail.Object.initialize();
        ComplaintDetail.Event.initialize();
        ComplaintDetail.Page.initialize();
        ComplaintDetail.Rule.initialize();
        ComplaintDetail.Service.initialize();
        ComplaintDetail.Callback.initialize();

        Acm.deferred(ComplaintDetail.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}


};

