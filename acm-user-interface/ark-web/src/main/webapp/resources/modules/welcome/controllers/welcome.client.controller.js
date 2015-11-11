'use strict';

angular.module('welcome').controller('WelcomeController', ['$scope', '$q', '$state', 'StoreService', 'HelperService', 'CallLookupService', 'CallConfigService', 'CallCasesService', 'CallComplaintsService', 'CallTasksService',
    function ($scope, $q, $state, Store, Helper, CallLookupService, CallConfigService, CallCasesService, CallComplaintsService, CallTasksService) {

        _.each(CallLookupService.SessionCacheNames, function (name) {
            var cache = new Store.SessionData(name);
            cache.set(null);
        });
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