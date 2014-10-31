/**
 * Profile.Controller
 *
 * @author jwu
 */
Profile.Controller = {
    create : function() {
        if (Profile.Controller.Info.create) {Profile.Controller.Info.create();}
    }
    ,initialize: function() {
        if (Profile.Controller.Info.initialize) {Profile.Controller.Info.initialize();}
    }

    ,Info: {
        create : function() {
        }
        ,initialize: function() {
        }
        ,onViewChangedLocation: function(value) {
            Profile.Model.Info.ctrlUpdateLocation(value);
        }
        ,onModelChangedProfileInfo: function(info) {
            Profile.View.Info.ctrlProfileInfoChanged(info);
        }
        ,onModelChangedProfileInfoSaved: function() {
            Profile.View.Info.ctrlProfileInfoSaved();
        }
    }

};


