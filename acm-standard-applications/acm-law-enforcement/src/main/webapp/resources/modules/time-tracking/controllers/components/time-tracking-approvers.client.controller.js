'use strict';

angular.module('time-tracking').controller('TimeTracking.ApproversController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'LookupService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, TimeTrackingInfoService, HelperObjectBrowserService, LookupService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "time-tracking"
            , componentId: "approvers"
            , retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo
            , validateObjectInfo:TimeTrackingInfoService.validateTimesheet
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions = $scope.gridOptions || {};

            LookupService.getApprovers(objectInfo).then(
                function (approvers) {
                    $scope.gridOptions.data = approvers;
                }
            );


        };
    }
]);