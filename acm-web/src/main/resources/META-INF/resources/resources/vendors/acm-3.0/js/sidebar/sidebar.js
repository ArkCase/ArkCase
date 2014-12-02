/**
 * Sidebar is namespace component for Sidebar plugin
 *
 * @author jwu
 */
var Sidebar = Sidebar || {
    create: function() {
        if (Sidebar.Model.create)      {Sidebar.Model.create();}
        if (Sidebar.View.create)       {Sidebar.View.create();}
        if (Sidebar.Controller.create) {Sidebar.Controller.create();}

        Sidebar.create_old();
    }
    ,onInitialized: function() {
        if (Sidebar.Model.onInitialized)      {Sidebar.Model.onInitialized();}
        if (Sidebar.View.onInitialized)       {Sidebar.View.onInitialized();}
        if (Sidebar.Controller.onInitialized) {Sidebar.Controller.onInitialized();}
    }

    ,create_old: function() {
        Sidebar.Object.create();
        Sidebar.Event.create();
        //Sidebar.Page.create();
        //Sidebar.Rule.create();
        //Sidebar.Service.create();
        //Sidebar.Callback.create();

        Acm.deferred(Sidebar.Event.onPostInit);
    }

};

