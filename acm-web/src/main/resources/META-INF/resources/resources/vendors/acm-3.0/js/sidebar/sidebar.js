/**
 * Sidebar is namespace component for Sidebar plugin
 *
 * @author jwu
 */
var Sidebar = Sidebar || {
    initialize: function() {
        Sidebar.Object.initialize();
        Sidebar.Event.initialize();
        Sidebar.Page.initialize();
        Sidebar.Rule.initialize();
        Sidebar.Service.initialize();
        Sidebar.Callback.initialize();

        Acm.deferred(Sidebar.Event.onPostInit);
    }

//    ,Object: {}
//    ,Event:{}
//    ,Page: {}
//    ,Rule: {}
//    ,Service: {}
//    ,Callback: {}
};

