'use strict';

angular.module('cost-tracking').controller('CostTracking.PersonController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, CostTrackingInfoService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("cost-tracking", "person").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(function (costsheetInfo) {
                $scope.costsheetInfo = costsheetInfo;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = [$scope.costsheetInfo.user];
                return costsheetInfo;
            });
        }



    }
]);