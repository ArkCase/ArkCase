/**
 * Sidebar.Service
 *
 * manages service calls to application server
 *
 * @author jwu
 */
Sidebar.Service = {
    create : function() {
        if (this.Profile.create) {this.Profile.create();}
    }
    ,initialize: function() {
        if (Sidebar.Service.Profile.initialize) {Sidebar.Service.Profile.initialize();}
    }

    ,Profile: {
        create: function() {
        }
        ,initialize: function() {
        }

        ,API_RETRIEVE_PROFILE_INFO_          : "/api/latest/plugin/profile/get/"
        ,API_DOWNLOAD_PICTURE_BEGIN_         : "/api/latest/plugin/ecm/download/byId/"
        ,API_DOWNLOAD_PICTURE_END            : "?inline=true"

        ,getPictureUrl: function(ecmFileId) {
            if (0 >= ecmFileId) {
                return "";
            }
            return App.getContextPath() + this.API_DOWNLOAD_PICTURE_BEGIN_ + ecmFileId + this.API_DOWNLOAD_PICTURE_END;
        }
        ,_validateProfile: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.userId) || Acm.isEmpty(data.email)) {
                return false;
            }
            if (!Acm.isArray(data.groups)) {
                return false;
            }
            return true;
        }
        ,retrieveProfileInfo : function(userId) {
            var url = App.getContextPath() + this.API_RETRIEVE_PROFILE_INFO_ + userId;
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        Sidebar.Controller.modelRetrievedProfile(response);

                    } else {
                        if (Sidebar.Service.Profile._validateProfile(response)) {
                            var profileInfo = response;
                            Sidebar.Model.Profile.setProfileInfo(profileInfo);
                            Sidebar.Controller.modelRetrievedProfile(profileInfo);
                        }
                    }
                }
                ,url
            )
        }
    }
};

