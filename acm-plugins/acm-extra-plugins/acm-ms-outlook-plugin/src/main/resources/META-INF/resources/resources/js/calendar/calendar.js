/**
 * Calendar module
 *
 * @author md
 */
var Calendar = Calendar || {
    create: function(args) {
        if (Acm.isEmpty(args)) {
            args = {};
        }
        if (Calendar.Controller.create) {Calendar.Controller.create(args);}
        if (Calendar.Model.create)      {Calendar.Model.create(args);}
        if (Calendar.View.create)       {Calendar.View.create(args);}
    }
    ,onInitialized: function(args) {
        if (Calendar.Controller.onInitialized) {Calendar.Controller.onInitialized(args);}
        if (Calendar.Model.onInitialized)      {Calendar.Model.onInitialized(args);}
        if (Calendar.View.onInitialized)       {Calendar.View.onInitialized(args);}
    }
};


