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
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.userId) || Acm.isEmpty(data.email)) {
                return false;
            }
            if (Acm.isArray(data.groups)) {
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
        ,saveProfileInfo : function(data, callbackHandler) {
            var profileInfo = data;
            Acm.Service.asyncPost(
                function(response) {
                    if (response.hasError) {
                        if (callbackHandler) {
                            callbackHandler(response);
                        } else {
                            Profile.Controller.modelSavedProfileInfo(response);
                        }

                    } else {
                        if (Profile.Service.Info._validateProfile(response)) {
                            var profileInfo = response;
                            Profile.Model.Info.setProfileInfo(profileInfo);
                            if (callbackHandler) {
                                callbackHandler(profileInfo);
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
        ,saveLocation: function(location) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.location = location;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedLocation(Profile.Service.Info._dataWrapper(data, data.location));
                }
            );
        }
        ,saveImAccount: function(imAccount) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.imAccount = imAccount;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedLocation(Profile.Service.Info._dataWrapper(data, data.imAccount));
                }
            );
        }
        ,saveImSystem: function(imSystem) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.imSystem = imSystem;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedLocation(Profile.Service.Info._dataWrapper(data, data.imSystem));
                }
            );
        }
        ,saveOfficePhone: function(officePhoneNumber) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.officePhoneNumber = officePhoneNumber;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedOfficePhone(Profile.Service.Info._dataWrapper(data, data.officePhoneNumber));
                }
            );
        }
        ,saveMobilePhone: function(mobilePhoneNumber) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.mobilePhoneNumber = mobilePhoneNumber;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedMobilePhone(Profile.Service.Info._dataWrapper(data, data.mobilePhoneNumber));
                }
            );
        }
        ,saveCompany: function(companyName) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.companyName = companyName;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedCompany(Profile.Service.Info._dataWrapper(data, data.companyName));
                }
            );
        }
        ,saveStreet: function(firstAddress) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.firstAddress = firstAddress;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedStreet(Profile.Service.Info._dataWrapper(data, data.firstAddress));
                }
            );
        }
        ,saveAddress2: function(secondAddress) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.secondAddress = secondAddress;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedAddress2(Profile.Service.Info._dataWrapper(data, data.secondAddress));
                }
            );
        }
        ,saveCity: function(city) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.city = city;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedCity(Profile.Service.Info._dataWrapper(data, data.city));
                }
            );
        }
        ,saveState: function(state) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.state = state;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedState(Profile.Service.Info._dataWrapper(data, data.state));
                }
            );
        }
        ,saveZip: function(zip) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.zip = zip;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedZip(Profile.Service.Info._dataWrapper(data, data.zip));
                }
            );
        }
        ,saveMainPhone: function(mainOfficePhone) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.mainOfficePhone = mainOfficePhone;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedMainPhone(Profile.Service.Info._dataWrapper(data, data.mainOfficePhone));
                }
            );
        }
        ,saveFax: function(fax) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.fax = fax;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedFax(Profile.Service.Info._dataWrapper(data, data.fax));
                }
            );
        }
        ,saveWebsite: function(website) {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            profileInfo.website = website;
            this.saveProfileInfo(profileInfo
                ,function(data) {
                    Profile.Controller.modelSavedWebsite(Profile.Service.Info._dataWrapper(data, data.website));
                }
            );
        }
    }


};

