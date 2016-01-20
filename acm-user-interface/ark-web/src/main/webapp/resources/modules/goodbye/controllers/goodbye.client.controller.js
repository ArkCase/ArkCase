'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$scope', '$window', '$q', '$state', 'StoreService', 'Authentication'
    , 'ConfigService', 'LookupService', 'Object.LookupService', 'Case.LookupService', 'Complaint.LookupService'
    , function ($scope, $window, $q, $state, Store, Authentication
        , ConfigService, LookupService, ObjectLookupService, CaseLookupService, ComplaintLookupService) {

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


        $window.location.href = "/arkcase/logout";
    }
]);