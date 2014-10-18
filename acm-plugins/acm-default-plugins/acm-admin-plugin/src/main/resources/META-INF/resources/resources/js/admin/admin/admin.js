/**
 * Admin is namespace component for Admin plugin
 *
 * @author jwu
 */
var Admin = Admin || {
    create: function() {
        Admin.Object.create();
        Admin.Event.create();
        Admin.Page.create();
        Admin.Rule.create();
        Admin.Service.create();
        Admin.Callback.create();

        Acm.deferred(Admin.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

