'use strict';

angular.module('cost-tracking').controller('CostTracking.PersonController', ['$scope', '$stateParams'
    , 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService'
    , function ($scope, $stateParams, ConfigService, HelperUiGridService, CostTrackingInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("cost-tracking", "person").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        //$scope.$on('costsheet-updated', function (e, data) {
        //    $scope.costsheetInfo = data;
        //    $scope.gridOptions = $scope.gridOptions || {};
        //    $scope.gridOptions.data = [$scope.costsheetInfo.user];
        //});
        CostTrackingInfoService.getCostTrackingInfo($stateParams.id).then(function (costsheetInfo) {
            $scope.costsheetInfo = costsheetInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.costsheetInfo.user];
            return costsheetInfo;
        });



    }
]);