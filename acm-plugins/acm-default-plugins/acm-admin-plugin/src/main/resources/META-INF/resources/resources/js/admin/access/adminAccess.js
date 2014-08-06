/**
 * AdminAccess is namespace component for AdminAccess plugin
 *
 * @author jwu
 */
var AdminAccess = AdminAccess || {
    initialize: function() {
        AdminAccess.Object.initialize();
        AdminAccess.Event.initialize();
        AdminAccess.Page.initialize();
        AdminAccess.Rule.initialize();
        AdminAccess.Service.initialize();
        AdminAccess.Callback.initialize();

        Acm.deferred(AdminAccess.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

