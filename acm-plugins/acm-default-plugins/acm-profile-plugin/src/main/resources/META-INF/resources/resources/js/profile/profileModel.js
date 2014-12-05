/**
 * Profile.Model
 *
 * @author jwu
 */
Profile.Model = {
    create : function() {
        if (Profile.Model.Picture.create) {Profile.Model.Picture.create();}
        if (Profile.Model.Info.create)    {Profile.Model.Info.create();}
    }
    ,onInitialized: function() {
        if (Profile.Model.Picture.onInitialized) {Profile.Model.Picture.onInitialized();}
        if (Profile.Model.Info.onInitialized)    {Profile.Model.Info.onInitialized();}
    }

    ,Picture: {
        create: function() {
        }
        ,onInitialized: function() {
        }

        ,_uploadInfo: null
        ,setUploadInfo: function(uploadInfo) {
            this._uploadInfo = uploadInfo;
        }
        ,getEcmFileId: function(uploadInfo) {
            var ecmFileId = -1;
            if (uploadInfo) {
                if (Acm.isArray(uploadInfo.files)) {
                    if (0 < uploadInfo.files.length) {
                        var url = Acm.goodValue(uploadInfo.files[0].url); //url in format of "acm/file/123"
                        var idx = url.lastIndexOf("/");
                        if (0 <= idx) {
                            ecmFileId = parseInt(url.substring(idx+1)) || (-1);
                        }
                    }
                }
            }
            return ecmFileId;
        }

    }
    ,Info: {
        create: function() {
            this._profileInfo    = new Acm.Model.SessionData("AcmProfile");

            Acm.Dispatcher.addEventListener(Profile.Controller.ME_PICTURE_UPLOADED        ,this.onPictureUploaded);

            Acm.Dispatcher.addEventListener(Profile.Controller.VE_LOCATION_CHANGED        ,this.onLocationChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_IM_ACCOUNT_CHANGED      ,this.onImAccountChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_IM_SYSTEM_CHANGED       ,this.onImSystemChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_OFFICE_PHONE_CHANGED    ,this.onOfficePhoneChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_MOBILE_PHONE_CHANGED    ,this.onMobilePhoneChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_COMPANY_CHANGED         ,this.onCompanyChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_STREET_CHANGED          ,this.onStreetChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_ADDRESS2_CHANGED        ,this.onAddress2Changed);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_CITY_CHANGED            ,this.onCityChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_STATE_CHANGED           ,this.onStateChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_ZIP_CHANGED             ,this.onZipChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_MAIN_PHONE_CHANGED      ,this.onMainPhoneChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_FAX_CHANGED             ,this.onFaxChanged);
            Acm.Dispatcher.addEventListener(Profile.Controller.VE_WEBSITE_CHANGED         ,this.onWebsiteChanged);
        }
        ,onInitialized: function() {
            var profileInfo = Profile.Model.Info.getProfileInfo();
            if (profileInfo) {
                Profile.Controller.modelRetrievedProfile(profileInfo);
            } else {
                Profile.Service.Info.retrieveProfileInfo(App.getUserName());
            }
        }

        //,_profileInfo: null
        ,getProfileInfo: function() {
            return this._profileInfo.get();
        }
        ,setProfileInfo: function(profileInfo) {
            this._profileInfo.set(profileInfo);
        }

        ,isReadOnly: function() {
            return false;
        }


        ,onPictureUploaded: function(uploadInfo) {
            if (!uploadInfo.hasError) {
                var ecmFileId = Profile.Model.Picture.getEcmFileId(uploadInfo);
                Profile.Service.Info.saveEcmFileId(ecmFileId);
            }
        }
        ,onLocationChanged: function(location) {
            Profile.Service.Info.saveLocation(location);
        }
        ,onImAccountChanged: function(imAccount) {
            Profile.Service.Info.saveImAccount(imAccount);
        }
        ,onImSystemChanged: function(imSystem) {
            Profile.Service.Info.saveImSystem(imSystem);
        }
        ,onOfficePhoneChanged: function(officePhoneNumber) {
            Profile.Service.Info.saveOfficePhone(officePhoneNumber);
        }
        ,onMobilePhoneChanged: function(mobilePhoneNumber) {
            Profile.Service.Info.saveMobilePhone(mobilePhoneNumber);
        }
        ,onCompanyChanged: function(companyName) {
            Profile.Service.Info.saveCompany(companyName);
        }
        ,onStreetChanged: function(firstAddress) {
            Profile.Service.Info.saveStreet(firstAddress);
        }
        ,onAddress2Changed: function(secondAddress) {
            Profile.Service.Info.saveAddress2(secondAddress);
        }
        ,onCityChanged: function(city) {
            Profile.Service.Info.saveCity(city);
        }
        ,onStateChanged: function(state) {
            Profile.Service.Info.saveState(state);
        }
        ,onZipChanged: function(zip) {
            Profile.Service.Info.saveZip(zip);
        }
        ,onMainPhoneChanged: function(mainOfficePhone) {
            Profile.Service.Info.saveMainPhone(mainOfficePhone);
        }
        ,onFaxChanged: function(fax) {
            Profile.Service.Info.saveFax(fax);
        }
        ,onWebsiteChanged: function(website) {
            Profile.Service.Info.saveWebsite(website);
        }

    }

};

