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
    ,initialize: function() {
        if (Topbar.Model.initialize)      {Topbar.Model.initialize();}
        if (Topbar.View.initialize)       {Topbar.View.initialize();}
        if (Topbar.Controller.initialize) {Topbar.Controller.initialize();}
    }

};

