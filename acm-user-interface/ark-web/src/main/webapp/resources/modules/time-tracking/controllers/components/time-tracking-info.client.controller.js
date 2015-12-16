'use strict';

angular.module('time-tracking').controller('TimeTracking.InfoController', ['$scope', 'ConfigService'
    , function ($scope, ConfigService) {

        ConfigService.getComponentConfig("time-tracking", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        //$scope.timesheetSolr = null;
        //$scope.timesheetInfo = null;
        $scope.$on('timesheet-selected', function onSelectedCase(e, selectedTimesheet) {
            $scope.timesheetSolr = selectedTimesheet;
        });

        $scope.$on('timesheet-updated', function (e, data) {
                $scope.timesheetInfo = data;

        });
    }
]);