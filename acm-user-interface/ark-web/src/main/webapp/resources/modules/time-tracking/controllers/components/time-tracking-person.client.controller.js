'use strict';

angular.module('time-tracking').controller('TimeTracking.PersonController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, TimeTrackingInfoService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("time-tracking", "person").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            return config;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(function (timesheetInfo) {
                $scope.timesheetInfo = timesheetInfo;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = [$scope.timesheetInfo.user];
                return timesheetInfo;
            });
        }

    }
]);