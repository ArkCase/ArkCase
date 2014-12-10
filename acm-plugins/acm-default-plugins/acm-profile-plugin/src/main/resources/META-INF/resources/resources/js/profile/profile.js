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
    ,onInitialized: function() {
        if (Profile.Model.onInitialized)      {Profile.Model.onInitialized();}
        if (Profile.View.onInitialized)       {Profile.View.onInitialized();}
        if (Profile.Controller.onInitialized) {Profile.Controller.onInitialized();}
    }
};

