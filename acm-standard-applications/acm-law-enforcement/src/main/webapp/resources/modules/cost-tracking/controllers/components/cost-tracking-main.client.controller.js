'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', 'ConfigService', 'Helper.DashboardService'
    , function ($scope, ConfigService, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "cost-tracking"
            , dashboardName: "COST"
        });

        ConfigService.getComponentConfig("cost-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            $scope.allowedWidgets = ['details'];
            return componentConfig;
        });

    }
]);