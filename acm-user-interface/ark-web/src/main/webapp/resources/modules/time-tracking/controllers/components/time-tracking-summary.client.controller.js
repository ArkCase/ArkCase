'use strict';

angular.module('time-tracking').controller('TimeTracking.SummaryController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService'
    , function ($scope, $stateParams, Util, ConfigService, HelperUiGridService, TimeTrackingInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("time-tracking", "summary").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            return config;
        });

        //$scope.$on('timesheet-updated', function (e, data) {
        //    $scope.timesheetInfo = data;
        //
        //    $scope.gridOptions = $scope.gridOptions || {};
        //    $scope.gridOptions.data = $scope.timesheetInfo.times;
        //});
        TimeTrackingInfoService.getTimeTrackingInfo($stateParams.id).then(function (timesheetInfo) {
            $scope.timesheetInfo = timesheetInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.timesheetInfo.times;
            return timesheetInfo;
        });

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "type");
            var targetId = Util.goodMapValue(rowEntity, "objectId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);