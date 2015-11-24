'use strict';

angular.module('time-tracking').controller('TimeTracking.InfoController', ['$scope',
    function ($scope) {
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });

        $scope.timesheetSolr = null;
        $scope.timesheetInfo = null;
        $scope.$on('timesheet-selected', function onSelectedCase(e, selectedTimesheet) {
            $scope.timesheetSolr = selectedTimesheet;
        });

        $scope.$on('timesheet-updated', function (e, data) {
                $scope.timesheetInfo = data;

        });
    }
]);