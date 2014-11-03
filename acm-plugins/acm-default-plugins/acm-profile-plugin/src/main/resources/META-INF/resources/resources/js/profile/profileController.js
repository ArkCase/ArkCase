/**
 * Profile.Controller
 *
 * @author jwu
 */
Profile.Controller = {
    create : function() {
    }
    ,initialize: function() {
    }

    ,ME_PROFILE_INFO_RETRIEVED		  : "profile-info-retrieved"             //param: profileInfo
    ,ME_PROFILE_INFO_SAVED		      : "profile-info-saved"                 //param: profileInfo

    ,VE_LOCATION_CHANGED              : "profile-location-changed"           //param: location
    ,VE_IM_ACCOUNT_CHANGED            : "profile-im-account-changed"         //param: imAccount
    ,VE_IM_SYSTEM_CHANGED             : "profile-im-system-changed"          //param: imSystem

    ,modelRetrievedProfile: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.ME_PROFILE_INFO_RETRIEVED, profileInfo);
    }
    ,modelSavedProfileInfo: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.ME_PROFILE_INFO_SAVED, profileInfo);
    }
    ,viewChangedLocation: function(location) {
        Acm.Dispatcher.fireEvent(this.VE_LOCATION_CHANGED, location);
    }
    ,viewChangedImAccount: function(imAccount) {
        Acm.Dispatcher.fireEvent(this.VE_IM_ACCOUNT_CHANGED, imAccount);
    }
    ,viewChangedImSystem: function(imSystem) {
        Acm.Dispatcher.fireEvent(this.VE_IM_SYSTEM_CHANGED, imSystem);
    }


};


