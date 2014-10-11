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
        //Topbar.Service.initialize();
        Topbar.Callback.initialize();

        if (Topbar.Model.create)      {Topbar.Model.create();}
        if (Topbar.View.create)       {Topbar.View.create();}
        if (Topbar.Controller.create) {Topbar.Controller.create();}

        //if (Topbar.Model.initialize)      {Acm.deferred(Topbar.Model.initialize);}
        //if (Topbar.View.initialize)       {Acm.deferred(Topbar.View.initialize);}
        if (Topbar.Controller.initialize) {Acm.deferred(Topbar.Controller.initialize);}
    }

};

