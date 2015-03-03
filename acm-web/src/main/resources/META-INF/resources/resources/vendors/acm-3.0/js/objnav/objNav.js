/**
 * ObjNav is namespace component for Object Navigator, a base for navigation of complaint, case, task, etc.
 *
 * @author jwu
 */
var ObjNav = ObjNav || {
    create: function(args) {
        if (Acm.isEmpty(args)) {
            args = {};
        }

        if (ObjNav.Controller.create) {ObjNav.Controller.create(args);}
        if (ObjNav.Model.create)      {ObjNav.Model.create(args);}
        if (ObjNav.View.create)       {ObjNav.View.create(args);}
    }
    ,onInitialized: function() {
        if (ObjNav.Controller.onInitialized) {ObjNav.Controller.onInitialized();}
        if (ObjNav.Model.onInitialized)      {ObjNav.Model.onInitialized();}
        if (ObjNav.View.onInitialized)       {ObjNav.View.onInitialized();}
    }

};

