'use strict';

angular.module('preference').controller('Preference.ModuleInfoController', ['$scope', '$state', '$stateParams'
    , '$q', 'ConfigService', 'dashboard', 'Preference.PreferenceService', 'Dashboard.DashboardService',
    function ($scope, $state, $stateParams, $q, ConfigService, dashboard, PreferenceService, DashboardService) {
        $scope.module = null;
        $scope.$on('module-selected', moduleSelected);

        function moduleSelected(e, newModule) {
            var promiseModule = ConfigService.getModule({moduleId: newModule.id});
            var promiseDashboardConfig = DashboardService.getConfig({module: newModule.id});

            /*  $q.all([promiseModule, promiseDashboardConfig]).then(function(data) {
             var module = data[0];
             var config = data[1];
             $scope.$broadcast('show-widgets', dashboard.widgets, module.id, config);
             }); */
            ConfigService.getModule({moduleId: newModule.id}, function (module) {
                DashboardService.getConfig({module: module.id}, function(config) {
                    $scope.$broadcast('show-widgets', dashboard.widgets, module.id, config);
                })
            });
        }
    }
]);
