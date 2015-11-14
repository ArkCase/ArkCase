'use strict';

angular.module('time-tracking').controller('TimeTracking.PersonController', ['$scope', 'HelperService',
    function ($scope, Helper) {
        $scope.$emit('req-component-config', 'person');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('person' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('timesheet-retrieved', function(e, data) {
            $scope.timesheetInfo = data;

            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.timesheetInfo.user];
        });



    }
]);