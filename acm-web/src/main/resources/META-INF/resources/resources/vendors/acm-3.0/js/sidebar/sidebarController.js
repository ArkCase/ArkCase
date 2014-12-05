/**
 * Sidebar.Controller
 *
 * @author jwu
 */
Sidebar.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_PROFILE_INFO        : "sidebar-retrieved-profile"             //param: profileInfo

    ,modelRetrievedProfile: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PROFILE_INFO, profileInfo);
    }

};


