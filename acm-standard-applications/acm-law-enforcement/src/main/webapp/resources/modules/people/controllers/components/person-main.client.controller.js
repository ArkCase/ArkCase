'use strict';

angular.module('people').controller('People.MainController', ['$scope', 'ConfigService', 'Dashboard.DashboardService'
    , function ($scope, ConfigService, DashboardService) {

        var promiseConfig = ConfigService.getModuleConfig("people").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });


        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            personModel: {
                titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "PERSON"}, function (data) {
            $scope.dashboard.personModel = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("PERSON", $scope.dashboard.personModel);
            $scope.dashboard.personModel.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);