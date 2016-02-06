'use strict';

angular.module('cost-tracking').controller('CostTracking.PersonController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, CostTrackingInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , componentId: "person"
            , retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo
            , validateObjectInfo: CostTrackingInfoService.validateCostsheet
            , onObjectInfoRetrieved: function (costsheetInfo) {
                onObjectInfoRetrieved(costsheetInfo);
            }
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
        };

        var onObjectInfoRetrieved = function (costsheetInfo) {
            $scope.costsheetInfo = costsheetInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.costsheetInfo.user];
        };

    }
]);