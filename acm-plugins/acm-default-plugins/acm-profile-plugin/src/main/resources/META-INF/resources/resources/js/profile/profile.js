/**
 * Profile is namespace component for Dashboard Profile
 *
 * @author jwu
 */
var Profile = Profile || {
    create: function() {
        if (Profile.Model.create)      {Profile.Model.create();}
        if (Profile.View.create)       {Profile.View.create();}
        if (Profile.Controller.create) {Profile.Controller.create();}
    }
    ,initialize: function() {
        if (Profile.Model.initialize)      {Profile.Model.initialize();}
        if (Profile.View.initialize)       {Profile.View.initialize();}
        if (Profile.Controller.initialize) {Profile.Controller.initialize();}
    }
};

