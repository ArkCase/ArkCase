/**
 * Topbar is namespace component for Topbar plugin
 *
 * @author jwu
 */
var Topbar = Topbar || {
    create: function() {
        if (Topbar.Model.create)      {Topbar.Model.create();}
        if (Topbar.View.create)       {Topbar.View.create();}
        if (Topbar.Controller.create) {Topbar.Controller.create();}
    }
    ,onInitialized: function() {
        if (Topbar.Model.onInitialized)      {Topbar.Model.onInitialized();}
        if (Topbar.View.onInitialized)       {Topbar.View.onInitialized();}
        if (Topbar.Controller.onInitialized) {Topbar.Controller.onInitialized();}
    }

};

