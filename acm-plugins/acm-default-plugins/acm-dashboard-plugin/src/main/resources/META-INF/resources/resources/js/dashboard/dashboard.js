/**
 * Dashboard is namespace component for Dashboard plugin
 *
 * @author jwu
 */
var Dashboard = Dashboard || {
    create: function() {
        Dashboard.Object.create();
        Dashboard.Event.create();
        Dashboard.Page.create();
        Dashboard.Rule.create();
        Dashboard.Service.create();
        Dashboard.Callback.create();

        Acm.deferred(Dashboard.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

