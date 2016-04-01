'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$scope', '$window', '$q', '$state'
    , 'Acm.StoreService', 'Authentication', 'ConfigService', 'UtilService', 'LookupService', 'Object.LookupService'
    , 'Case.LookupService', 'Complaint.LookupService', 'Acm.LoginStatService', 'Acm.AppService'
    , function ($scope, $window, $q, $state
        , Store, Authentication, ConfigService, Util, LookupService, ObjectLookupService
        , CaseLookupService, ComplaintLookupService, AcmLoginStatService, AcmAppService
    ) {
        var sessionCacheNamesList = [
            Authentication.SessionCacheNames
            , ConfigService.SessionCacheNames
            , LookupService.SessionCacheNames
            , ObjectLookupService.SessionCacheNames
            , CaseLookupService.SessionCacheNames
            , ComplaintLookupService.SessionCacheNames

        ];
        for (var i = 0; i < sessionCacheNamesList.length; i++) {
            _.each(sessionCacheNamesList[i], function (name) {
                var cache = new Store.SessionData(name);
                cache.set(null);
            });
        }

        // Retrieves the app properties from app-config.xml file
        var appConfig = LookupService.getConfig('app').then(function (data) {
            // redirect to logout page
            $window.location.href = AcmAppService.getAppUrl(Util.goodMapValue(data, "logoutUrl", "/logout"));
        });
        AcmLoginStatService.setLogin(false);        
    }
]);