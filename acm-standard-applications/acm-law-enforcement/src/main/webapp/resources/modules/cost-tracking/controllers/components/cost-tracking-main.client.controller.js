'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', 'Dashboard.DashboardService', 'ConfigService',
    function ($scope, DashboardService, ConfigService) {

        ConfigService.getComponentConfig("cost-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            $scope.allowedWidgets = ['details'];
            return componentConfig;
        });

        ConfigService.getModuleConfig("cost-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            costModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "COST"}, function (data) {
            $scope.dashboard.costModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("COST", $scope.dashboard.costModel);
            $scope.dashboard.costModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);