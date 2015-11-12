'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController', ['$scope', '$state',
    function ($scope, $state) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.timesheetInfo = null;

        $scope.$on('timesheet-retrieved', function(e, data) {
            $scope.timesheetInfo = data;
        });

        $scope.loadNewTimesheetFrevvoForm = function () {
            $state.go('newTimesheet');
        };

        $scope.loadExistingTimesheetFrevvoForm = function () {
            //$scope.$broadcast('send-data-for-frevvo', $scope.timesheetInfo);
           // $state.go('editTimesheet');
        };

    }
]);