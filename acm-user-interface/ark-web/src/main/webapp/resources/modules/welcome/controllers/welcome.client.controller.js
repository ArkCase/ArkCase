'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$state', 'StoreService', 'HelperService', 'CallConfigService', 'CallCasesService', 'CallComplaintsService', 'CallTasksService',
    function ($scope, $state, Store, Helper, CallConfigService, CallCasesService, CallComplaintsService, CallTasksService) {
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


        _.each(CallConfigService.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });
        _.each(Helper.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });
        _.each(CallCasesService.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });
        _.each(CallComplaintsService.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });
        _.each(CallTasksService.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });


        $state.go("dashboard");
    }
]);