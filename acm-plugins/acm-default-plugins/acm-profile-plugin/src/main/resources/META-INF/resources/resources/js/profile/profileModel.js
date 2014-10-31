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
        }
        ,initialize: function() {
            Profile.Service.Info.retrieveProfileInfo();
        }

        ,_profileInfo: {}
        ,getProfileInfo: function() {
            return this._profileInfo;
        }
        ,setProfileInfo: function(info) {
            this._profileInfo = info;
        }

        ,isReadOnly: function() {
            return false;
        }

        ,ctrlUpdateLocation: function(value) {
            var info = {};
            //save location
            Profile.Service.Info.updateProfileInfo(info);
        }
    }

};

