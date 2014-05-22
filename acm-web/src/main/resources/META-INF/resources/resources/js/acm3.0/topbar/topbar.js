/**
 * Topbar is namespace component for Topbar plugin
 *
 * @author jwu
 */
var Topbar = Topbar || {
    initialize: function() {
        Topbar.Object.initialize();
        Topbar.Event.initialize();
        Topbar.Page.initialize();
        Topbar.Rule.initialize();
        Topbar.Service.initialize();
        Topbar.Callback.initialize();

        Topbar.Event.onPostInit();
    }

    ,Object: {}
    ,Event:{}
    ,Page: {}
    ,Rule: {}
    ,Service: {}
    ,Callback: {}
};

