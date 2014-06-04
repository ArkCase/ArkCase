/**
 * TaskDetail is namespace component for Complaint Wizard
 *
 * @author jwu
 */
var TaskDetail = TaskDetail || {
    initialize: function() {
        TaskDetail.Object.initialize();
        TaskDetail.Event.initialize();
        TaskDetail.Page.initialize();
        TaskDetail.Rule.initialize();
        TaskDetail.Service.initialize();
        TaskDetail.Callback.initialize();

        Acm.deferred(TaskDetail.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

