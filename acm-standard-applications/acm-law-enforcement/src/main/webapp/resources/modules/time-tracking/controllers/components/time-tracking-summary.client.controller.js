'use strict';

angular.module('time-tracking').controller('TimeTracking.SummaryController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, TimeTrackingInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , componentId: "summary"
            , retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo
            , validateObjectInfo: TimeTrackingInfoService.validateTimesheet
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            var customization = Util.goodMapValue(moduleConfig, "customization", {});
            if (customization) {
                $scope.customization = customization;
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.objectInfo.times;
        };

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "type");
            var targetId = Util.goodMapValue(rowEntity, "objectId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);