'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', 'ConfigService', 'Dashboard.DashboardService'
    , function ($scope, ConfigService, DashboardService) {
        var promiseConfig = ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });

        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            caseModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "CASE"}, function (data) {
            $scope.dashboard.caseModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("CASE", $scope.dashboard.caseModel);
            $scope.dashboard.caseModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });

    }
]);