/**
 * Sidebar.View
 *
 * @author jwu
 */
Sidebar.View = {
    create : function() {
        if (Sidebar.View.Profile.create)             {Sidebar.View.Profile.create();}
    }
    ,initialize: function() {
        if (Sidebar.View.Profile.initialize)         {Sidebar.View.Profile.initialize();}
    }


    ,Profile: {
        create: function() {
            this.$imgPicture     = $("#sidebarPic");

            Acm.Dispatcher.addEventListener(Sidebar.Controller.ME_PROFILE_INFO_RETRIEVED  ,this.onProfileInfoRetrieved);

        }
        ,initialize: function() {
        }

        ,onProfileInfoRetrieved: function(profileInfo) {
            if (profileInfo.hasError) {
                alert("View: onProfileInfoRetrieved, hasError, errorMsg:" + profileInfo.errorMsg);
            } else {
                Sidebar.View.Profile.populateProfileInfo(profileInfo);
            }
        }

        ,populateProfileInfo: function(profileInfo) {
            this._displayPicture(Acm.goodValue(profileInfo.ecmFileId, -1));
        }
        ,_displayPicture: function(ecmFileId) {
            var pictureUrl = (0 < ecmFileId)? Sidebar.Service.Profile.getPictureUrl(ecmFileId)
                : this.getDefaultImgPicture();
            this.setSrcImgPicture(pictureUrl);
        }

        ,setSrcImgPicture: function(src) {
            this.$imgPicture.attr("src", src);
        }
        ,getDefaultImgPicture: function() {
            return this.$imgPicture.attr("default");
        }

    }

};

