/**
 * Sidebar.View
 *
 * @author jwu
 */
Sidebar.View = {
    create : function() {
        if (Sidebar.View.Profile.create)             {Sidebar.View.Profile.create();}
    }
    ,onInitialized: function() {
        if (Sidebar.View.Profile.onInitialized)      {Sidebar.View.Profile.onInitialized();}
    }


    ,Profile: {
        create: function() {
            this.$imgPicture    = $("#sidebarPic");
            this.$spanTitle     = $("#sidebarTitle");

            Acm.Dispatcher.addEventListener(Sidebar.Controller.MODEL_RETRIEVED_PROFILE_INFO  ,this.onModelRetrievedProfileInfo);
            if ("undefined" != typeof Profile) {
                Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_TITLE         ,this.onModelSavedTitleByProfile);
                Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_SAVED_ECM_FILE_ID   ,this.onSavedEcmFileIdByProfile);
            }

        }
        ,onInitialized: function() {
        }

        ,onModelRetrievedProfileInfo: function(profileInfo) {
            if (profileInfo.hasError) {
                alert("View: onProfileInfoRetrieved, hasError, errorMsg:" + profileInfo.errorMsg);
            } else {
                Sidebar.View.Profile.populateProfileInfo(profileInfo);
            }
        }
        ,onModelSavedTitleByProfile: function(title) {
            if (title.hasError) {
                Sidebar.View.Profile.setTextSpanTitle("(Error)");
            } else {
                Sidebar.View.Profile.setTextSpanTitle(title);
            }
        }
        ,onSavedEcmFileIdByProfile: function(ecmFileId) {
            if (ecmFileId.hasError) {
                Sidebar.View.Profile.displayPicture(0);
            } else {
                Sidebar.View.Profile.displayPicture(ecmFileId);
            }
        }

        ,populateProfileInfo: function(profileInfo) {
            this.displayPicture(Acm.goodValue(profileInfo.ecmFileId, -1));
            this.setTextSpanTitle(Acm.goodValue(profileInfo.title));
        }
        ,displayPicture: function(ecmFileId) {
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
        ,setTextSpanTitle: function(txt) {
            Acm.Object.setText(this.$spanTitle, txt);
        }

    }

};

