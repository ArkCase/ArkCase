'use strict';

angular.module('time-tracking').controller('TimeTracking.SummaryController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, TimeTrackingInfoService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("time-tracking", "summary").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            return config;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(function (timesheetInfo) {
                updateData(timesheetInfo);
                return timesheetInfo;
            });
        }

        $scope.$on('object-refreshed', function (e, timesheetInfo) {
            updateData(timesheetInfo);
        });

        var updateData = function (timesheetInfo) {
            $scope.timesheetInfo = timesheetInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.timesheetInfo.times;
        };

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "type");
            var targetId = Util.goodMapValue(rowEntity, "objectId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);