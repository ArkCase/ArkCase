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
            var modules = [
                {name: "CASE", configName: "cases"}
                , {name: "COMPLAINT", configName: "complaints"}
                , {name: "COST", configName: "cost-tracking"}
                , {name: "TIME", configName: "time-tracking"}
                , {name: "TASK", configName: "tasks"}
            ];
            var selectedModule = _.find(modules, function (module) {
                return module.configName == newModule.id;
            });
            ConfigService.getModule({moduleId: newModule.id}, function (module) {
                DashboardService.getConfig({moduleName: selectedModule.name}, function(config) {
                    $scope.$broadcast('show-widgets', dashboard.widgets, module.id, config);
                })
            });
        }
    }
]);
