/**
 * Audit module
 *
 * @author jwu
 */
var Audit = Audit || {
    create: function() {
        if (Audit.Model.create)      {Audit.Model.create();}
        if (Audit.Service.create)    {Audit.Service.create();}
        if (Audit.View.create)       {Audit.View.create();}
        if (Audit.Controller.create) {Audit.Controller.create();}
    }
    ,onInitialized: function() {
        if (Audit.Model.onInitialized)      {Audit.Model.onInitialized();}
        if (Audit.Service.onInitialized)    {Audit.Service.onInitialized();}
        if (Audit.View.onInitialized)       {Audit.View.onInitialized();}
        if (Audit.Controller.onInitialized) {Audit.Controller.onInitialized();}
    }
};


