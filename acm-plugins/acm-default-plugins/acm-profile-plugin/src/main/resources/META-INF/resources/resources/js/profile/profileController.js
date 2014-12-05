/**
 * Profile.Controller
 *
 * @author jwu
 */
Profile.Controller = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,MODEL_RETRIEVED_PROFILE_INFO        : "profile-retrieved-info"             //param: profileInfo
    ,MODEL_SAVED_PROFILE_INFO            : "profile-saved-info-saved"                 //param: profileInfo
    ,MODEL_SAVED_TITLE                   : "profile-saved-title-saved"                //param: title
    ,MODEL_SAVED_LOCATION                : "profile-saved-location-saved"             //param: location
    ,MODEL_SAVED_IM_ACCOUNT              : "profile-saved-im-account-saved"           //param: imAccount
    ,MODEL_SAVED_IM_SYSTEM               : "profile-saved-im-system-saved"            //param: imSystem
    ,MODEL_SAVED_OFFICE_PHONE            : "profile-saved-office-phone-saved"         //param: officePhoneNumber
    ,MODEL_SAVED_MOBILE_PHONE            : "profile-saved-mobile-phone-saved"         //param: mobilePhoneNumber
    ,MODEL_SAVED_COMPANY                 : "profile-saved-company-saved"              //param: companyName
    ,MODEL_SAVED_STREET                  : "profile-saved-street-saved"               //param: firstAddress
    ,MODEL_SAVED_ADDRESS2                : "profile-saved-address2-saved"             //param: secondAddress
    ,MODEL_SAVED_CITY                    : "profile-saved-city-saved"                 //param: city
    ,MODEL_SAVED_STATE                   : "profile-saved-state-saved"                //param: state
    ,MODEL_SAVED_ZIP                     : "profile-saved-zip-saved"                  //param: zip
    ,MODEL_SAVED_MAIN_PHONE              : "profile-saved-main-phone-saved"           //param: mainOfficePhone
    ,MODEL_SAVED_FAX                     : "profile-saved-fax-saved"                  //param: fax
    ,MODEL_SAVED_WEBSITE                 : "profile-saved-website-saved"              //param: website
    ,MODEL_SAVED_ECM_FILE_ID             : "profile-saved-ecm-file-id-saved"          //param: ecmFileId

    ,MODEL_UPLOADED_PICTURE              : "profile-uploaded-picture"                 //param: uploadInfo

    ,VIEW_CHANGED_TITLE                  : "profile-changed-title-changed"              //param: title
    ,VIEW_CHANGED_LOCATION               : "profile-changed-location-changed"           //param: location
    ,VIEW_CHANGED_IM_ACCOUNT             : "profile-changed-im-account-changed"         //param: imAccount
    ,VIEW_CHANGED_IM_SYSTEM              : "profile-changed-im-system-changed"          //param: imSystem
    ,VIEW_CHANGED_OFFICE_PHONE           : "profile-changed-office-phone-changed"       //param: officePhoneNumber
    ,VIEW_CHANGED_MOBILE_PHONE           : "profile-changed-mobile-phone-changed"       //param: mobilePhoneNumber
    ,VIEW_CHANGED_COMPANY                : "profile-changed-company-changed"            //param: companyName
    ,VIEW_CHANGED_STREET                 : "profile-changed-street-changed"             //param: firstAddress
    ,VIEW_CHANGED_ADDRESS2               : "profile-changed-address2-changed"           //param: secondAddress
    ,VIEW_CHANGED_CITY                   : "profile-changed-city-changed"               //param: city
    ,VIEW_CHANGED_STATE                  : "profile-changed-state-changed"              //param: state
    ,VIEW_CHANGED_ZIP                    : "profile-changed-zip-changed"                //param: zip
    ,VIEW_CHANGED_MAIN_PHONE             : "profile-changed-main-phone-changed"         //param: mainOfficePhone
    ,VIEW_CHANGED_FAX                    : "profile-changed-fax-changed"                //param: fax
    ,VIEW_CHANGED_WEBSITE                : "profile-changed-website-changed"            //param: website

    ,modelRetrievedProfile: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.MODEL_RETRIEVED_PROFILE_INFO, profileInfo);
        //Sidebar.Controller.modelRetrievedProfile(profileInfo);
        //var z = 1;
        //Acm.Dispatcher.fireEvent(Sidebar.Controller.ME_PROFILE_INFO_RETRIEVED, profileInfo);
    }
    ,modelSavedProfileInfo: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_PROFILE_INFO, profileInfo);
    }
    ,modelSavedTitle: function(title) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_TITLE, title);
    }
    ,modelSavedLocation: function(location) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_LOCATION, location);
    }
    ,modelSavedImAccount: function(imAccount) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_IM_ACCOUNT, imAccount);
    }
    ,modelSavedImSystem: function(imSystem) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_IM_SYSTEM, imSystem);
    }
    ,modelSavedOfficePhone: function(officePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_OFFICE_PHONE, officePhoneNumber);
    }
    ,modelSavedMobilePhone: function(mobilePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_MOBILE_PHONE, mobilePhoneNumber);
    }
    ,modelSavedCompany: function(companyName) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_COMPANY, companyName);
    }
    ,modelSavedStreet: function(firstAddress) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_STREET, firstAddress);
    }
    ,modelSavedAddress2: function(secondAddress) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ADDRESS2, secondAddress);
    }
    ,modelSavedCity: function(city) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_CITY, city);
    }
    ,modelSavedState: function(state) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_STATE, state);
    }
    ,modelSavedZip: function(zip) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ZIP, zip);
    }
    ,modelSavedMainPhone: function(mainOfficePhone) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_MAIN_PHONE, mainOfficePhone);
    }
    ,modelSavedFax: function(fax) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_FAX, fax);
    }
    ,modelSavedWebsite: function(website) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_WEBSITE, website);
    }
    ,modelSavedEcmFileId: function(ecmFileId) {
        Acm.Dispatcher.fireEvent(this.MODEL_SAVED_ECM_FILE_ID, ecmFileId);
    }
    ,modelUploadedPicture: function(uploadInfo) {
        Acm.Dispatcher.fireEvent(this.MODEL_UPLOADED_PICTURE, uploadInfo);
    }

    ,viewChangedTitle: function(title) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_TITLE, title);
    }
    ,viewChangedLocation: function(location) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_LOCATION, location);
    }
    ,viewChangedImAccount: function(imAccount) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_IM_ACCOUNT, imAccount);
    }
    ,viewChangedImSystem: function(imSystem) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_IM_SYSTEM, imSystem);
    }
    ,viewChangedOfficePhone: function(officePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_OFFICE_PHONE, officePhoneNumber);
    }
    ,viewChangedMobilePhone: function(mobilePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_MOBILE_PHONE, mobilePhoneNumber);
    }
    ,viewChangedCompany: function(companyName) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_COMPANY, companyName);
    }
    ,viewChangedStreet: function(firstAddress) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_STREET, firstAddress);
    }
    ,viewChangedAddress2: function(secondAddress) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ADDRESS2, secondAddress);
    }
    ,viewChangedCity: function(city) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_CITY, city);
    }
    ,viewChangedState: function(state) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_STATE, state);
    }
    ,viewChangedZip: function(zip) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_ZIP, zip);
    }
    ,viewChangedMainPhone: function(mainOfficePhone) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_MAIN_PHONE, mainOfficePhone);
    }
    ,viewChangedFax: function(fax) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_FAX, fax);
    }
    ,viewChangedWebsite: function(website) {
        Acm.Dispatcher.fireEvent(this.VIEW_CHANGED_WEBSITE, website);
    }


};


