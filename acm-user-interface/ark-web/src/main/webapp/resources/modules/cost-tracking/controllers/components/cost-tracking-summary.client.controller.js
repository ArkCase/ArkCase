'use strict';

angular.module('cost-tracking').controller('CostTracking.SummaryController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, HelperUiGridService, CostTrackingInfoService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("cost-tracking", "summary").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(function (costsheetInfo) {
                $scope.costsheetInfo = costsheetInfo;
                var parentNumber = {parentNumber: $scope.costsheetInfo.parentNumber};
                var parentType = {parentType: $scope.costsheetInfo.parentType};
                var parentId = {parentId: $scope.costsheetInfo.parentId};

                var costs = angular.copy($scope.costsheetInfo.costs);
                costs = costs.map(function (obj) {
                    return angular.extend(obj, parentNumber, parentType, parentId);
                });
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = costs;
                return costsheetInfo;
            });
        }

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "parentType");
            var targetId = Util.goodMapValue(rowEntity, "parentId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);