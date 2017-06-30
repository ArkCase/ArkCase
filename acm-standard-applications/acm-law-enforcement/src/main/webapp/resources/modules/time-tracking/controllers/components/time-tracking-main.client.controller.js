'use strict';

angular.module('time-tracking').controller('TimeTracking.MainController', ['$scope', 'Dashboard.DashboardService', 'ConfigService'
    , function ($scope, DashboardService, ConfigService) {

        ConfigService.getComponentConfig("time-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            $scope.allowedWidgets = ['details'];
            return componentConfig;
        });

        ConfigService.getModuleConfig("time-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            timeModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "TIME"}, function (data) {
            $scope.dashboard.timeModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("TIME", $scope.dashboard.timeModel);
            $scope.dashboard.timeModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);