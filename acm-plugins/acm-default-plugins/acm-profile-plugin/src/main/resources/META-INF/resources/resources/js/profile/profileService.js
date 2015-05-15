/**
 * Profile.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Profile.Service = {
    create : function() {
        if (Profile.Service.Picture.create)         {Profile.Service.Picture.create();}
        if (Profile.Service.Info.create)            {Profile.Service.Info.create();}
        if (Profile.Service.Subscription.create)    {Profile.Service.Subscription.create();}
        if (Profile.Service.OutlookPassword.create)    {Profile.Service.OutlookPassword.create();}
    }
    ,onInitialized: function() {
        if (Profile.Service.Picture.onInitialized)         {Profile.Service.Picture.onInitialized();}
        if (Profile.Service.Info.onInitialized)            {Profile.Service.Info.onInitialized();}
        if (Profile.Service.Subscription.onInitialized)    {Profile.Service.Subscription.onInitialized();}
        if (Profile.Service.OutlookPassword.onInitialized)    {Profile.Service.OutlookPassword.onInitialized();}

    }

    ,Picture: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_UPLOAD_IMAGE: "/api/latest/service/ecm/upload"

        ,_validateUploadInfo: function(data) {
            // upload response is an array of EcmFile JSON
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data)) {
                return false;
            }

            if (0 >= data.length) {
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
    ,Subscription: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,API_RETRIEVE_SUBSCRIPTION: "/api/v1/service/subscription/"

        ,API_DELETE_SUBSCRIPTION: "/api/v1/service/subscription/"

        ,retrieveSubscriptionDeferred : function(postData, jtParams, sortMap, callbackSuccess, callbackError) {
            return AcmEx.Service.JTable.deferredPagingListAction(postData, jtParams, sortMap
                ,function() {
                    var url;
                    url =  App.getContextPath() + Profile.Service.Subscription.API_RETRIEVE_SUBSCRIPTION + App.getUserName();
                    return url;
                }
                ,function(data) {
                    var jtData = AcmEx.Object.JTable.getEmptyRecords();
                    if(Acm.isArray(data)) {
                        var response = data;
                        var subscriptions = [];
                        for (var i = 0; i < response.length; i++) {
                            var subscription = {};
                            if(Profile.Model.Subscription.validateSubscription(response[i])){
                                subscription.id = Acm.goodValue(response[i].subscriptionId);
                                subscription.parentId = Acm.goodValue(response[i].objectId);
                                subscription.parentType = Acm.goodValue(response[i].subscriptionObjectType);
                                subscription.parentTitle = Acm.goodValue(response[i].objectTitle);
                                subscription.parentName = Acm.goodValue(response[i].objectName);
                                subscription.created = Acm.getDateFromDatetime(response[i].created);
                                subscriptions.push(subscription);
                            }
                        }
                        Profile.Model.Subscription.cacheSubscription.put(App.getUserName(), subscriptions);
                        jtData = callbackSuccess(subscriptions);
                    }
                    else
                    {
                        if (Acm.isNotEmpty(data.error)) {
                            Profile.Controller.modelRetrievedSubscriptions(data);
                        }
                    }
                    return jtData;
                }
            );
        }

        ,deleteSubscription : function(parentId,parentType,userId) {
            var url = App.getContextPath() + this.API_DELETE_SUBSCRIPTION + userId + "/" + parentType + "/" +  parentId;

            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        Profile.Controller.modelDeletedSubscription(response);
                    } else {
                        if (Profile.Model.Subscription.validateDeletedSubscription(response)) {
                            var subscriptions = Profile.Model.Subscription.cacheSubscription.get(userId);
                            if (Acm.isArray(subscriptions) && Acm.isNotEmpty(subscriptions)) {
                                for (var i = 0; i < subscriptions.length; i++) {
                                    if (response.deletedSubscriptionId == subscriptions[i].parentId) {
                                        subscriptions.splice(i, 1);
                                        Profile.Controller.modelDeletedSubscription(Acm.Service.responseWrapper(response, response.deletedSubscriptionId));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                ,url
            )
        }
    }

    ,OutlookPassword: {
        create: function () {
        }
        , onInitialized: function () {
        }

        ,API_SAVE_OUTLOOK_PASSWORD: "/api/v1/plugin/profile/outlook"

        ,saveOutlookPassword : function(outlookPasswordToSave) {
            var url = App.getContextPath() + this.API_SAVE_OUTLOOK_PASSWORD;
            Acm.Service.asyncPost(
                function (response) {
                    if (response.hasError) {
                        Profile.Controller.modelSavedOutlookPassword(response);
                    } else {
                        if (Profile.Model.OutlookPassword.validateOutlookPassword(response)) {
                            var savedOutlookPassword = response;
                            Profile.Controller.modelSavedOutlookPassword(savedOutlookPassword);
                        }
                    } //end else
                }
                , url
                ,JSON.stringify(outlookPasswordToSave)
            )
        }
    }
};

