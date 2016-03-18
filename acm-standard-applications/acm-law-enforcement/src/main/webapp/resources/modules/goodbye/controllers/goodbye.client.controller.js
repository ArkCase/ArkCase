'use strict';

angular.module('goodbye').controller('GoodbyeController', ['$scope', '$window', '$q', '$state', 'StoreService', 'Authentication'
    , 'ConfigService', 'LookupService', 'Object.LookupService', 'Case.LookupService', 'Complaint.LookupService', '$browser'
    , function ($scope, $window, $q, $state, Store, Authentication
        , ConfigService, LookupService, ObjectLookupService, CaseLookupService, ComplaintLookupService, $browser) {

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
            // redirect to login page
            $window.location.href = data['logoutUrl'];
        });        
    }
]);