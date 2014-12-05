/**
 * Profile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Profile.Service = {
    create : function() {
        if (this.Picture.create) {this.Picture.create();}
        if (this.Info.create)    {this.Info.create();}
    }
    ,onInitialized: function() {
        if (Profile.Service.Picture.onInitialized) {Profile.Service.Picture.onInitialized();}
        if (Profile.Service.Info.onInitialized)    {Profile.Service.Info.onInitialized();}
    }

    ,Picture: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_UPLOAD_IMAGE: "/api/latest/plugin/profile/img"

        ,_validateUploadInfo: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.files)) {
                return false;
            }
            if (!Acm.isArray(data.files)) {
                return false;
            }
            if (0 >= data.files.length) {
                return false;
            }
            return true;
        }
        ,uploadImage: function(formData) {
            var url = App.getContextPath() + this.API_UPLOAD_IMAGE;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        Profile.Controller.modelUploadedPicture(response);
                    } else {
                        if (Profile.Service.Picture._validateUploadInfo(response)) {
                            var uploadInfo = response;
                            Profile.Model.Picture.setUploadInfo(uploadInfo);
                            Profile.Controller.modelUploadedPicture(uploadInfo);
                        }
                    }
                }
            });
        }
    }

    ,Info: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_PROFILE_INFO_          : "/api/latest/plugin/profile/get/"
        ,API_SAVE_PROFILE_INFO               : "/api/latest/plugin/profile/userOrgInfo/set"
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
        ,saveProfileInfo : function(data, handler) {
            var profileInfo = data;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            Profile.Controller.modelSavedProfileInfo(response);
                        }

                    } else {
                        if (Profile.Service.Info._validateProfile(response)) {
                            var profileInfo = response;
                            Profile.Model.Info.setProfileInfo(profileInfo);
                            if (handler) {
                                handler(profileInfo);
                            } else {
                                Profile.Controller.modelSavedProfileInfo(profileInfo);
                            }
                        }
                    }
                }
                ,App.getContextPath() + this.API_SAVE_PROFILE_INFO
                ,JSON.stringify(profileInfo)
            )
        }
        ,_dataWrapper: function(data, value) {
            if (data.hasError) {
                return data;
            } else {
                return value;
            }
        }
        ,saveTitle: function(title) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.title = title;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedTitle(Profile.Service.Info._dataWrapper(data, data.title));
                    }
                );
            }
        }
        ,saveLocation: function(location) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.location = location;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedLocation(Profile.Service.Info._dataWrapper(data, data.location));
                    }
                );
            }
        }
        ,saveImAccount: function(imAccount) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.imAccount = imAccount;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedLocation(Profile.Service.Info._dataWrapper(data, data.imAccount));
                    }
                );
            }
        }
        ,saveImSystem: function(imSystem) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.imSystem = imSystem;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedLocation(Profile.Service.Info._dataWrapper(data, data.imSystem));
                    }
                );
            }
        }
        ,saveOfficePhone: function(officePhoneNumber) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.officePhoneNumber = officePhoneNumber;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedOfficePhone(Profile.Service.Info._dataWrapper(data, data.officePhoneNumber));
                    }
                );
            }
        }
        ,saveMobilePhone: function(mobilePhoneNumber) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.mobilePhoneNumber = mobilePhoneNumber;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedMobilePhone(Profile.Service.Info._dataWrapper(data, data.mobilePhoneNumber));
                    }
                );
            }
        }
        ,saveCompany: function(companyName) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.companyName = companyName;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedCompany(Profile.Service.Info._dataWrapper(data, data.companyName));
                    }
                );
            }
        }
        ,saveStreet: function(firstAddress) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.firstAddress = firstAddress;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedStreet(Profile.Service.Info._dataWrapper(data, data.firstAddress));
                    }
                );
            }
        }
        ,saveAddress2: function(secondAddress) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.secondAddress = secondAddress;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedAddress2(Profile.Service.Info._dataWrapper(data, data.secondAddress));
                    }
                );
            }
        }
        ,saveCity: function(city) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.city = city;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedCity(Profile.Service.Info._dataWrapper(data, data.city));
                    }
                );
            }
        }
        ,saveState: function(state) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.state = state;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedState(Profile.Service.Info._dataWrapper(data, data.state));
                    }
                );
            }
        }
        ,saveZip: function(zip) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.zip = zip;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedZip(Profile.Service.Info._dataWrapper(data, data.zip));
                    }
                );
            }
        }
        ,saveMainPhone: function(mainOfficePhone) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.mainOfficePhone = mainOfficePhone;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedMainPhone(Profile.Service.Info._dataWrapper(data, data.mainOfficePhone));
                    }
                );
            }
        }
        ,saveFax: function(fax) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.fax = fax;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedFax(Profile.Service.Info._dataWrapper(data, data.fax));
                    }
                );
            }
        }
        ,saveWebsite: function(website) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.website = website;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedWebsite(Profile.Service.Info._dataWrapper(data, data.website));
                    }
                );
            }
        }
        ,saveEcmFileId: function(ecmFileId) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (Profile.Service.Info._validateProfile(profileInfo)) {
                profileInfo.ecmFileId = ecmFileId;
                this.saveProfileInfo(profileInfo
                    ,function(data) {
                        Profile.Controller.modelSavedEcmFileId(Profile.Service.Info._dataWrapper(data, data.ecmFileId));
                    }
                );
            }
        }

    }


};

