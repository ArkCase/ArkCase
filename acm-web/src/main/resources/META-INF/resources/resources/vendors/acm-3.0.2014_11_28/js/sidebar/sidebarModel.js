/**
 * Sidebar.Model
 *
 * @author jwu
 */
Sidebar.Model = {
    create : function() {
        if (Sidebar.Model.Profile.create)        {Sidebar.Model.Profile.create();}
    }
    ,onInitialized: function() {
        if (Sidebar.Model.Profile.onInitialized)    {Sidebar.Model.Profile.onInitialized();}
    }

    ,Profile: {
        create: function() {
            this._profileInfo    = new Acm.Model.SessionData("AcmProfile");
        }
        ,onInitialized: function() {
            var profileInfo = Sidebar.Model.Profile.getProfileInfo();
            if (profileInfo) {
                Sidebar.Controller.modelRetrievedProfile(profileInfo);
            } else {
                Sidebar.Service.Profile.retrieveProfileInfo(App.getUserName());
            }
        }

        ,getProfileInfo: function() {
            return this._profileInfo.get();
        }
        ,setProfileInfo: function(profileInfo) {
            this._profileInfo.set(profileInfo);
        }
    }

};

