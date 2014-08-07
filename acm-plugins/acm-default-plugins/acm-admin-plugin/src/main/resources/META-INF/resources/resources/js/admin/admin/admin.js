/**
 * Admin is namespace component for Admin plugin
 *
 * @author jwu
 */
var Admin = Admin || {
    initialize: function() {
        Admin.Object.initialize();
        Admin.Event.initialize();
        Admin.Page.initialize();
        Admin.Rule.initialize();
        Admin.Service.initialize();
        Admin.Callback.initialize();

        Acm.deferred(Admin.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

