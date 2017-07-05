'use strict';

angular.module('organizations').controller('Organizations.MainController', ['$scope', 'ConfigService', 'Dashboard.DashboardService'
    , function ($scope, ConfigService, DashboardService) {
        var promiseConfig = ConfigService.getModuleConfig("organizations").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            organizationModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "ORGANIZATION"}, function (data) {
            $scope.dashboard.organizationModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("ORGANIZATION", $scope.dashboard.organizationModel);
            $scope.dashboard.organizationModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);