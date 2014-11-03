/**
 * Profile.Model
 *
 * @author jwu
 */
Profile.Model = {
    create : function() {
        if (Profile.Model.Info.create) {Profile.Model.Info.create();}
    }
    ,initialize: function() {
        if (Profile.Model.Info.initialize) {Profile.Model.Info.initialize();}
    }

    ,Info: {
        create: function() {
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_LOCATION_CHANGED    ,this.onLocationChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_IM_ACCOUNT_CHANGED  ,this.onImAccountChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_IM_SYSTEM_CHANGED   ,this.onImSystemChanged);
        }
        ,initialize: function() {
            Profile.Service.Info.retrieveProfileInfo(App.getUserName());
        }

        ,_profileInfo: {}
        ,getProfileInfo: function() {
            return this._profileInfo;
        }
        ,setProfileInfo: function(profileInfo) {
            this._profileInfo = profileInfo;
        }

        ,isReadOnly: function() {
            return false;
        }


        ,onLocationChanged: function(location) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.location = location;
            Profile.Service.Info.saveProfileInfo(profileInfo);
        }
        ,onImAccountChanged: function(imAccount) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.imAccount = imAccount;
            Profile.Service.Info.saveProfileInfo(profileInfo);
        }
        ,onImSystemChanged: function(imSystem) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.imSystem = imSystem;
            Profile.Service.Info.saveProfileInfo(profileInfo);
        }

    }

};

