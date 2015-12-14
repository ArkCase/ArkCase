'use strict';

angular.module('time-tracking').controller('TimeTracking.SummaryController', ['$scope', 'UtilService', 'Helper.UiGridService',
    function ($scope, Util, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.$emit('req-component-config', 'summary');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('summary' == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);
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