'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$q', '$state'
    , 'Acm.StoreService', 'Authentication', 'Acm.AppService', 'ConfigService'
    , 'LookupService', 'Object.LookupService', 'Case.LookupService', 'Complaint.LookupService'
    , 'Acm.LoginStatService', '$window'
    , function ($scope, $q, $state
        , Store, Authentication, AcmAppService , ConfigService
        , LookupService, ObjectLookupService, CaseLookupService, ComplaintLookupService
        , AcmLoginStatService, $window
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

        // TODO: check if this is needed. It is used in 'user-info.client.controller.js'. Doesn't seem like it is necessary...
        // In the 'goodbye.client.controller.js' the AcmLoginStatService.setLogin(false) will do nothing because the page will already be redirected to logout page.
        AcmLoginStatService.setLogin(true);

        if (sessionStorage.redirectState) {
            // redirect to the last remembered state  
            var redirectState = angular.fromJson(sessionStorage.redirectState);
            $state.go(redirectState.name, redirectState.params);
        } else if (localStorage.redirectURL){
            // redirect to hash passed in the URL of the login page
            $window.location.href = AcmAppService.getAppUrl('home.html' + localStorage.redirectURL);
        } else {
            $state.go("dashboard");
        }
    }
]);