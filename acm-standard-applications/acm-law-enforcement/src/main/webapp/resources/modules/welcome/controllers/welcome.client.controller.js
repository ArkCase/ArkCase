'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$q', '$state'
    , 'Acm.StoreService', 'Authentication', 'Acm.AppService', 'ConfigService'
    , 'LookupService', 'Object.LookupService', 'Case.LookupService', 'Complaint.LookupService'
    , 'Acm.LoginStatService'
    , function ($scope, $q, $state
        , Store, Authentication, AcmAppService , ConfigService
        , LookupService, ObjectLookupService, CaseLookupService, ComplaintLookupService
        , AcmLoginStatService
    ) {

        var sessionCacheNamesList = [
            Authentication.SessionCacheNames
            , AcmAppService.SessionCacheNames
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

        AcmLoginStatService.setLogin(true);

        $state.go("dashboard");
    }
]);