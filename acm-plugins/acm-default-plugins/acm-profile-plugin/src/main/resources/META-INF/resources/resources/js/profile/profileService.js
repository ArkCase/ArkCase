/**
 * Profile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Profile.Service = {
    create : function() {
        if (this.Info.create) {Profile.Service.Info.create();}
    }
    ,initialize: function() {
        if (this.Info.initialize) {Profile.Service.Info.initialize();}
    }

    ,Info: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_PROFILE_INFO_          : "/api/latest/plugin/profile/get/"
        ,API_SAVE_PROFILE_INFO               : "/api/latest/plugin/profile/userOrgInfo/set"


        ,_validateProfile: function(data) {
            if (Acm.isEmpty(data.userId) || Acm.isEmpty(data.email)) {
                return false;
            }
            return true;
        }
        ,retrieveProfileInfo : function(userId) {
            var url = App.getContextPath() + this.API_RETRIEVE_PROFILE_INFO_ + userId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Profile.Controller.modelRetrievedProfile(response);

                    } else {
                        if (Profile.Service.Info._validateProfile(response)) {
                            var profileInfo = response;
                            Profile.Model.Info.setProfileInfo(profileInfo);
                            Profile.Controller.modelRetrievedProfile(profileInfo);
                        }
                    }
                }
                ,url
            )
        }

        ,saveProfileInfo : function(data) {
            var profileInfo = data;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        Profile.Controller.modelSavedProfileInfo(response);

                    } else {
                        if (Profile.Service.Info._validateProfile(response)) {
                            var profileInfo = response;
                            Profile.Model.Info.setProfileInfo(profileInfo);
                            Profile.Controller.modelSavedProfileInfo(profileInfo);
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_PROFILE_INFO
                ,JSON.stringify(profileInfo)
            )
        }
    }


};

