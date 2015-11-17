'use strict';

angular.module('time-tracking').controller('CostTracking.ActionsController', ['$scope', '$state',
    function ($scope, $state) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.costsheetInfo = null;

        $scope.$on('timesheet-retrieved', function(e, data) {
            $scope.costsheetInfo = data;
        });

        $scope.loadNewTimesheetFrevvoForm = function () {
        };

        $scope.loadExistingTimesheetFrevvoForm = function () {
        };

    }
]);