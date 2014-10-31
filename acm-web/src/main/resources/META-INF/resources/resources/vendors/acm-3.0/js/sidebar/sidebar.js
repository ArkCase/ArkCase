/**
 * Sidebar is namespace component for Sidebar plugin
 *
 * @author jwu
 */
var Sidebar = Sidebar || {
    create: function() {
        Sidebar.Object.create();
        Sidebar.Event.create();
        Sidebar.Page.create();
        Sidebar.Rule.create();
        Sidebar.Service.create();
        Sidebar.Callback.create();

        Acm.deferred(Sidebar.Event.onPostInit);
    }

};

