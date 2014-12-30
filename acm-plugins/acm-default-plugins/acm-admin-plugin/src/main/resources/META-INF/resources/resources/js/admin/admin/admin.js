/**
 * Created by manoj.dhungana on 12/4/2014.
 */

var Admin = Admin || {
    create: function() {
        if (Admin.Model.create)      {Admin.Model.create();}
        if (Admin.Service.create)    {Admin.Service.create();}
        if (Admin.View.create)       {Admin.View.create();}
        if (Admin.Controller.create) {Admin.Controller.create();}
        //Admin.create_old();
    }
    ,onInitialized: function() {
        if (Admin.Model.onInitialized)      {Admin.Model.onInitialized();}
        if (Admin.Service.onInitialized)    {Admin.Service.onInitialized();}
        if (Admin.View.onInitialized)       {Admin.View.onInitialized();}
        if (Admin.Controller.onInitialized) {Admin.Controller.onInitialized();}
    }
};

