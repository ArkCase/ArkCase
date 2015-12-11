'use strict';

angular.module('time-tracking').controller('TimeTracking.PersonController', ['$scope', 'Helper.UiGridService',
    function ($scope, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.$emit('req-component-config', 'person');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('person' == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
            }
        });

        $scope.$on('timesheet-updated', function (e, data) {
            $scope.timesheetInfo = data;

            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.timesheetInfo.user];
        });



    }
]);