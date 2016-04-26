'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$scope', '$window', '$q', '$state'
    , 'Acm.StoreService', 'UtilService', 'Acm.LoginService', 'LookupService', 'Acm.AppService'
    //, 'Authentication', 'ConfigService', 'Object.LookupService'
    //, 'Case.LookupService', 'Complaint.LookupService'
    , function ($scope, $window, $q, $state
        , Store, Util, AcmLoginService, LookupService, AcmAppService
        //, Authentication, ConfigService, ObjectLookupService
        //, CaseLookupService, ComplaintLookupService
    ) {
        //var sessionCacheNamesList = [
        //    Authentication.SessionCacheNames
        //    , ConfigService.SessionCacheNames
        //    , LookupService.SessionCacheNames
        //    , ObjectLookupService.SessionCacheNames
        //    , CaseLookupService.SessionCacheNames
        //    , ComplaintLookupService.SessionCacheNames
        //
        //];
        //for (var i = 0; i < sessionCacheNamesList.length; i++) {
        //    _.each(sessionCacheNamesList[i], function (name) {
        //        var cache = new Store.SessionData(name);
        //        cache.set(null);
        //    });
        //}

        AcmLoginService.resetCaches();
        AcmLoginService.setLogin(false);

        // Retrieves the app properties from app-config.xml file
        var appConfig = LookupService.getConfig('app').then(function (data) {
            // clear redirectURL and redirectState
            localStorage.removeItem('redirectURL');
            sessionStorage.removeItem('redirectState');
            // redirect to logout page
            $window.location.href = AcmAppService.getAppUrl(Util.goodMapValue(data, "logoutUrl", "/logout"));
        });

    }
]);