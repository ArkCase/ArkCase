/**
 * Dashboard is namespace component for Dashboard plugin
 *
 * @author jwu
 */
var Dashboard = Dashboard || {
    initialize: function() {
        Dashboard.Object.initialize();
        Dashboard.Event.initialize();
        Dashboard.Page.initialize();
        Dashboard.Rule.initialize();
        Dashboard.Service.initialize();
        Dashboard.Callback.initialize();

        Acm.deferred(Dashboard.Event.onPostInit);
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

