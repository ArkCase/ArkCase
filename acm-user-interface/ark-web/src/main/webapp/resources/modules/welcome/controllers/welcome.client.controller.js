'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$q', '$state', 'StoreService', 'HelperService', 'CallAuthentication', 'CallLookupService', 'CallConfigService', 'CallObjectsService', 'CallCasesService', 'CallComplaintsService', 'CallTasksService',
    function ($scope, $q, $state, Store, Helper, CallAuthentication, CallLookupService, CallConfigService, CallObjectsService, CallCasesService, CallComplaintsService, CallTasksService) {

        var sessionCacheNamesList = [
            CallAuthentication.SessionCacheNames
            , CallConfigService.SessionCacheNames
            , Helper.SessionCacheNames
            , CallLookupService.SessionCacheNames
            , CallObjectsService.SessionCacheNames
            , CallCasesService.SessionCacheNames
            , CallComplaintsService.SessionCacheNames
            , CallTasksService.SessionCacheNames

        ];
        for (var i = 0; i < sessionCacheNamesList.length; i++) {
            _.each(sessionCacheNamesList[i], function (name) {
                var cache = new Store.SessionData(name);
                cache.set(null);
            });
        }


        //var promiseGetModuleConfig = CallConfigService.getModuleConfig("welcome").then(function (config) {
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