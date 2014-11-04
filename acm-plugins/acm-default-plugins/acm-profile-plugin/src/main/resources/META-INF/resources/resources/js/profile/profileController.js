/**
 * Profile.Controller
 *
 * @author jwu
 */
Profile.Controller = {
    create : function() {
    }
    ,initialize: function() {
    }

    ,ME_PROFILE_INFO_RETRIEVED        : "profile-info-retrieved"             //param: profileInfo
    ,ME_PROFILE_INFO_SAVED            : "profile-info-saved"                 //param: profileInfo
    ,ME_LOCATION_SAVED                : "profile-location-saved"             //param: location
    ,ME_IM_ACCOUNT_SAVED              : "profile-im-account-saved"           //param: imAccount
    ,ME_IM_SYSTEM_SAVED               : "profile-im-system-saved"            //param: imSystem
    ,ME_OFFICE_PHONE_SAVED            : "profile-office-phone-saved"         //param: officePhoneNumber
    ,ME_MOBILE_PHONE_SAVED            : "profile-mobile-phone-saved"         //param: mobilePhoneNumber
    ,ME_COMPANY_SAVED                 : "profile-company-saved"              //param: companyName
    ,ME_STREET_SAVED                  : "profile-street-saved"               //param: firstAddress
    ,ME_ADDRESS2_SAVED                : "profile-address2-saved"             //param: secondAddress
    ,ME_CITY_SAVED                    : "profile-city-saved"                 //param: city
    ,ME_STATE_SAVED                   : "profile-state-saved"                //param: state
    ,ME_ZIP_SAVED                     : "profile-zip-saved"                  //param: zip
    ,ME_MAIN_PHONE_SAVED              : "profile-main-phone-saved"           //param: mainOfficePhone
    ,ME_FAX_SAVED                     : "profile-fax-saved"                  //param: fax
    ,ME_WEBSITE_SAVED                 : "profile-website-saved"              //param: website

    ,VE_LOCATION_CHANGED              : "profile-location-changed"           //param: location
    ,VE_IM_ACCOUNT_CHANGED            : "profile-im-account-changed"         //param: imAccount
    ,VE_IM_SYSTEM_CHANGED             : "profile-im-system-changed"          //param: imSystem
    ,VE_OFFICE_PHONE_CHANGED          : "profile-office-phone-changed"       //param: officePhoneNumber
    ,VE_MOBILE_PHONE_CHANGED          : "profile-mobile-phone-changed"       //param: mobilePhoneNumber
    ,VE_COMPANY_CHANGED               : "profile-company-changed"            //param: companyName
    ,VE_STREET_CHANGED                : "profile-street-changed"             //param: firstAddress
    ,VE_ADDRESS2_CHANGED              : "profile-address2-changed"           //param: secondAddress
    ,VE_CITY_CHANGED                  : "profile-city-changed"               //param: city
    ,VE_STATE_CHANGED                 : "profile-state-changed"              //param: state
    ,VE_ZIP_CHANGED                   : "profile-zip-changed"                //param: zip
    ,VE_MAIN_PHONE_CHANGED            : "profile-main-phone-changed"         //param: mainOfficePhone
    ,VE_FAX_CHANGED                   : "profile-fax-changed"                //param: fax
    ,VE_WEBSITE_CHANGED               : "profile-website-changed"            //param: website

    ,modelRetrievedProfile: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.ME_PROFILE_INFO_RETRIEVED, profileInfo);
    }
    ,modelSavedProfileInfo: function(profileInfo) {
        Acm.Dispatcher.fireEvent(this.ME_PROFILE_INFO_SAVED, profileInfo);
    }
    ,modelSavedLocation: function(location) {
        Acm.Dispatcher.fireEvent(this.ME_LOCATION_SAVED, location);
    }
    ,modelSavedImAccount: function(imAccount) {
        Acm.Dispatcher.fireEvent(this.ME_IM_ACCOUNT_SAVED, imAccount);
    }
    ,modelSavedImSystem: function(imSystem) {
        Acm.Dispatcher.fireEvent(this.ME_IM_SYSTEM_SAVED, imSystem);
    }
    ,modelSavedOfficePhone: function(officePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.ME_OFFICE_PHONE_SAVED, officePhoneNumber);
    }
    ,modelSavedMobilePhone: function(mobilePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.ME_MOBILE_PHONE_SAVED, mobilePhoneNumber);
    }
    ,modelSavedCompany: function(companyName) {
        Acm.Dispatcher.fireEvent(this.ME_COMPANY_SAVED, companyName);
    }
    ,modelSavedStreet: function(firstAddress) {
        Acm.Dispatcher.fireEvent(this.ME_STREET_SAVED, firstAddress);
    }
    ,modelSavedAddress2: function(secondAddress) {
        Acm.Dispatcher.fireEvent(this.ME_ADDRESS2_SAVED, secondAddress);
    }
    ,modelSavedCity: function(city) {
        Acm.Dispatcher.fireEvent(this.ME_CITY_SAVED, city);
    }
    ,modelSavedState: function(state) {
        Acm.Dispatcher.fireEvent(this.ME_STATE_SAVED, state);
    }
    ,modelSavedZip: function(zip) {
        Acm.Dispatcher.fireEvent(this.ME_ZIP_SAVED, zip);
    }
    ,modelSavedMainPhone: function(mainOfficePhone) {
        Acm.Dispatcher.fireEvent(this.ME_MAIN_PHONE_SAVED, mainOfficePhone);
    }
    ,modelSavedFax: function(fax) {
        Acm.Dispatcher.fireEvent(this.ME_FAX_SAVED, fax);
    }
    ,modelSavedWebsite: function(website) {
        Acm.Dispatcher.fireEvent(this.ME_WEBSITE_SAVED, website);
    }
    ,viewChangedLocation: function(location) {
        Acm.Dispatcher.fireEvent(this.VE_LOCATION_CHANGED, location);
    }
    ,viewChangedImAccount: function(imAccount) {
        Acm.Dispatcher.fireEvent(this.VE_IM_ACCOUNT_CHANGED, imAccount);
    }
    ,viewChangedImSystem: function(imSystem) {
        Acm.Dispatcher.fireEvent(this.VE_IM_SYSTEM_CHANGED, imSystem);
    }
    ,viewChangedOfficePhone: function(officePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.VE_OFFICE_PHONE_CHANGED, officePhoneNumber);
    }
    ,viewChangedMobilePhone: function(mobilePhoneNumber) {
        Acm.Dispatcher.fireEvent(this.VE_MOBILE_PHONE_CHANGED, mobilePhoneNumber);
    }
    ,viewChangedCompany: function(companyName) {
        Acm.Dispatcher.fireEvent(this.VE_COMPANY_CHANGED, companyName);
    }
    ,viewChangedStreet: function(firstAddress) {
        Acm.Dispatcher.fireEvent(this.VE_STREET_CHANGED, firstAddress);
    }
    ,viewChangedAddress2: function(secondAddress) {
        Acm.Dispatcher.fireEvent(this.VE_ADDRESS2_CHANGED, secondAddress);
    }
    ,viewChangedCity: function(city) {
        Acm.Dispatcher.fireEvent(this.VE_CITY_CHANGED, city);
    }
    ,viewChangedState: function(state) {
        Acm.Dispatcher.fireEvent(this.VE_STATE_CHANGED, state);
    }
    ,viewChangedZip: function(zip) {
        Acm.Dispatcher.fireEvent(this.VE_ZIP_CHANGED, zip);
    }
    ,viewChangedMainPhone: function(mainOfficePhone) {
        Acm.Dispatcher.fireEvent(this.VE_MAIN_PHONE_CHANGED, mainOfficePhone);
    }
    ,viewChangedFax: function(fax) {
        Acm.Dispatcher.fireEvent(this.VE_FAX_CHANGED, fax);
    }
    ,viewChangedWebsite: function(website) {
        Acm.Dispatcher.fireEvent(this.VE_WEBSITE_CHANGED, website);
    }


};


