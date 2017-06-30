'use strict';

angular.module('time-tracking').controller('TimeTracking.MainController', ['$scope', 'ConfigService', 'Helper.DashboardService'
    , function ($scope, ConfigService, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "time-tracking"
            , dashboardName: "TIME"
        });

        ConfigService.getComponentConfig("time-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            $scope.allowedWidgets = ['details'];
            return componentConfig;
        });

    }
]);