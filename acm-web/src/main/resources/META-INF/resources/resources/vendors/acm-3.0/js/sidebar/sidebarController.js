/**
 * Sidebar.Controller
 *
 * @author jwu
 */
Sidebar.Controller = {
    create : function() {
    }
    ,initialize: function() {
    }

    ,ME_PROFILE_INFO_RETRIEVED        : "sidebar-profile-retrieved"             //param: profileInfo

    ,modelRetrievedProfile: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.ME_PROFILE_INFO_RETRIEVED, profileInfo);
    }

};


