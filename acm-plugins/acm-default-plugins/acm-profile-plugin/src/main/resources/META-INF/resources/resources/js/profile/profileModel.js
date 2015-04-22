/**
 * Profile.Model
 *
 * @author jwu
 */
Profile.Model = {
    create : function() {
        if (Profile.Model.Picture.create)           {Profile.Model.Picture.create();}
        if (Profile.Model.Info.create)              {Profile.Model.Info.create();}
        if (Profile.Model.Subscription.create)      {Profile.Model.Subscription.create();}
        if (Profile.Model.OutlookPassword.create)     {Profile.Model.OutlookPassword.create();}
    }
    ,onInitialized: function() {
        if (Profile.Model.Picture.onInitialized)            {Profile.Model.Picture.onInitialized();}
        if (Profile.Model.Info.onInitialized)               {Profile.Model.Info.onInitialized();}
        if (Profile.Model.Subscription.onInitialized)       {Profile.Model.Subscription.onInitialized();}
        if (Profile.Model.OutlookPassword.onInitialized)     {Profile.Model.OutlookPassword.onInitialized();}
    }

    ,DOC_TYPE_USER_PROFILE: "USER_ORG"

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
                // uploadInfo is an array of EcmFile JSON
                if (Acm.isArray(uploadInfo)) {
                    if (0 < uploadInfo.length) {
                        ecmFileId = Acm.goodValue(uploadInfo[0].fileId);
                    }
                }
            }
            return ecmFileId;
        }

    }
    ,Info: {
        create: function() {
            this._profileInfo    = new Acm.Model.SessionData("AcmProfile");

            Acm.Dispatcher.addEventListener(Profile.Controller.MODEL_UPLOADED_PICTURE        ,this.onModelUploadedPicture);

            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_TITLE            ,this.onViewChangedTitle);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_LOCATION         ,this.onViewChangedLocation);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_IM_ACCOUNT       ,this.onViewChangedImAccount);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_IM_SYSTEM        ,this.onViewChangedImSystem);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_OFFICE_PHONE     ,this.onViewChangedOfficePhone);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_MOBILE_PHONE     ,this.onViewChangedMobilePhone);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_COMPANY          ,this.onViewChangedCompany);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_STREET           ,this.onViewChangedStreet);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_ADDRESS2         ,this.onViewChangedAddress2);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_CITY             ,this.onViewChangedCity);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_STATE            ,this.onViewChangedState);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_ZIP              ,this.onViewChangedZip);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_MAIN_PHONE       ,this.onViewChangedMainPhone);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_FAX              ,this.onViewChangedFax);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_WEBSITE          ,this.onViewChangedWebsite);
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


        ,onModelUploadedPicture: function(uploadInfo) {
            if (!uploadInfo.hasError) {
                var ecmFileId = Profile.Model.Picture.getEcmFileId(uploadInfo);
                Profile.Service.Info.saveEcmFileId(ecmFileId);
            }
        }
        ,onViewChangedTitle: function(title) {
            Profile.Service.Info.saveTitle(title);
        }
        ,onViewChangedLocation: function(location) {
            Profile.Service.Info.saveLocation(location);
        }
        ,onViewChangedImAccount: function(imAccount) {
            Profile.Service.Info.saveImAccount(imAccount);
        }
        ,onViewChangedImSystem: function(imSystem) {
            Profile.Service.Info.saveImSystem(imSystem);
        }
        ,onViewChangedOfficePhone: function(officePhoneNumber) {
            Profile.Service.Info.saveOfficePhone(officePhoneNumber);
        }
        ,onViewChangedMobilePhone: function(mobilePhoneNumber) {
            Profile.Service.Info.saveMobilePhone(mobilePhoneNumber);
        }
        ,onViewChangedCompany: function(companyName) {
            Profile.Service.Info.saveCompany(companyName);
        }
        ,onViewChangedStreet: function(firstAddress) {
            Profile.Service.Info.saveStreet(firstAddress);
        }
        ,onViewChangedAddress2: function(secondAddress) {
            Profile.Service.Info.saveAddress2(secondAddress);
        }
        ,onViewChangedCity: function(city) {
            Profile.Service.Info.saveCity(city);
        }
        ,onViewChangedState: function(state) {
            Profile.Service.Info.saveState(state);
        }
        ,onViewChangedZip: function(zip) {
            Profile.Service.Info.saveZip(zip);
        }
        ,onViewChangedMainPhone: function(mainOfficePhone) {
            Profile.Service.Info.saveMainPhone(mainOfficePhone);
        }
        ,onViewChangedFax: function(fax) {
            Profile.Service.Info.saveFax(fax);
        }
        ,onViewChangedWebsite: function(website) {
            Profile.Service.Info.saveWebsite(website);
        }

    }

    ,Subscription: {
        create: function () {
            this.cacheSubscription = new Acm.Model.CacheFifo(3);
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_DELETED_SUBSCRIPTION , this.onViewDeletedSubscription);

        }
        , onInitialized: function () {
        }
        ,validateDeletedSubscription: function(data){
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.deletedSubscriptionId)) {
                return false;
            }
            return true;
        }
        ,validateSubscription: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.userId)) {
                return false;
            }
            if (Acm.isEmpty(data.subscriptionObjectType)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
            if (Acm.isEmpty(data.subscriptionId)) {
                return false;
            }
            return true;
        }
        ,onViewDeletedSubscription: function(parentId,parentType,userId){
            Profile.Service.Subscription.deleteSubscription(parentId,parentType,userId);
        }
    }

    ,OutlookPassword: {
        create: function () {
            Acm.Dispatcher.addEventListener(Profile.Controller.VIEW_CHANGED_OUTLOOK_PASSWORD, this.onViewChangedOutlookPassword);
        }
        , onInitialized: function () {
        }

        ,onViewChangedOutlookPassword:function(outlookPasswordToSave){
            Profile.Service.OutlookPassword.saveOutlookPassword(outlookPasswordToSave);
        }

        ,validateOutlookPassword: function(data){
            if(Acm.isEmpty(data)){
                return false;
            }
            if(Acm.isEmpty(data.outlookPassword)){
                return false;
            }
            return true;
        }
    }
};

