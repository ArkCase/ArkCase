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

        $scope.$on('timesheet-updated', function (e, data) {
            $scope.timesheetInfo = data;
            $scope.timesheetInfo.starDate = moment($scope.timesheetInfo.startDate).format($scope.config.frevvoDateFormat);
        });

        $scope.loadNewTimesheetFrevvoForm = function () {
            $state.go('newTimesheet');
        };

        $scope.loadExistingTimesheetFrevvoForm = function () {
            $state.go('editTimesheet', { period : $scope.timesheetInfo.starDate});
        };

    }
]);