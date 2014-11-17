/**
 * Sidebar.Model
 *
 * @author jwu
 */
Sidebar.Model = {
    create : function() {
        if (Sidebar.Model.Profile.create)        {Sidebar.Model.Profile.create();}
    }
    ,initialize: function() {
        if (Sidebar.Model.Profile.initialize)    {Sidebar.Model.Profile.initialize();}
    }

    ,Profile: {
        create: function() {
            this._profileInfo    = new Acm.Model.SessionData("AcmProfile");
        }
        ,initialize: function() {
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

