'use strict';

angular.module('time-tracking').controller('TimeTracking.PersonController', ['$scope', '$stateParams'
    , 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService'
    , function ($scope, $stateParams, ConfigService, HelperUiGridService, TimeTrackingInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("time-tracking", "person").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            return config;
        });

        //$scope.$on('timesheet-updated', function (e, data) {
        //    $scope.timesheetInfo = data;
        //
        //    $scope.gridOptions = $scope.gridOptions || {};
        //    $scope.gridOptions.data = [$scope.timesheetInfo.user];
        //});
        TimeTrackingInfoService.getTimeTrackingInfo($stateParams.id).then(function (timesheetInfo) {
            $scope.timesheetInfo = timesheetInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.timesheetInfo.user];
            return timesheetInfo;
        });

    }
]);