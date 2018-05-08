'use strict';

angular.module('preference').controller('Preference.ModuleInfoController',
        [ '$scope', '$state', '$stateParams', '$q', 'ConfigService', 'dashboard', 'Preference.PreferenceService', 'Dashboard.DashboardService', function($scope, $state, $stateParams, $q, ConfigService, dashboard, PreferenceService, DashboardService) {
            $scope.module = null;
            $scope.$on('module-selected', moduleSelected);

            function moduleSelected(e, newModuleId, dashboardConfig, preferenceConfig) {
                var objectWidgets = dashboardConfig.objectWidgets;
                var modules = dashboardConfig.modules;
                var selectedModule = _.find(modules, function(module) {
                    return module.configName == newModuleId;
                });

                DashboardService.getConfig({
                    moduleName: selectedModule.name
                }, function(moduleDashboardConfig) {
                    $scope.$broadcast('show-widgets', dashboard.widgets, moduleDashboardConfig, objectWidgets, preferenceConfig);
                })

            }
        } ]);
