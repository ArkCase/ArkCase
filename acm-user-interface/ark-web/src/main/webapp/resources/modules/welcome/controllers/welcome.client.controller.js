'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$q', '$state', 'HelperService', 'Authentication'
    , 'ConfigService', 'LookupService', 'Object.LookupService', 'Case.LookupService', 'Complaint.LookupService'
    , function ($scope, $q, $state, Helper, Authentication, ConfigService
        , LookupService, ObjectLookupService, CaseLookupService, ComplaintLookupService) {

        var sessionCacheNamesList = [
            Authentication.SessionCacheNames
            , ConfigService.SessionCacheNames
            , Helper.SessionCacheNames
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


        //var promiseGetModuleConfig = ConfigService.getModuleConfig("welcome").then(function (config) {
        //    $scope.config = config;
        //    return config;
        //});
        //$scope.$on('req-component-config', function (e, componentId) {
        //    promiseGetModuleConfig.then(function (config) {
        //        var componentConfig = _.find(config.components, {id: componentId});
        //        $scope.$broadcast('component-config', componentId, componentConfig);
        //    });
        //});
        //$q.all([promiseGetModuleConfig]).then(function(data) {
        //    $state.go("dashboard");
        //});

        $state.go("dashboard");
    }
]);