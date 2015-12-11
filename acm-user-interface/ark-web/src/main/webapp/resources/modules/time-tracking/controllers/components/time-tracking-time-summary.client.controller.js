'use strict';

angular.module('time-tracking').controller('TimeTracking.TimeSummaryController', ['$scope', 'UtilService', 'Helper.UiGridService',
    function ($scope, Util, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.$emit('req-component-config', 'time-summary');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('time-summary' == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
            }
        });

        $scope.$on('timesheet-updated', function (e, data) {
            $scope.timesheetInfo = data;

            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.timesheetInfo.times;
        });

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "type");
            var targetId = Util.goodMapValue(rowEntity, "objectId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);