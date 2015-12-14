'use strict';

angular.module('cost-tracking').controller('CostTracking.SummaryController', ['$scope'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService'
    , function ($scope, Util, ConfigService, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("cost-tracking", "summary").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
            var parentNumber = {parentNumber: $scope.costsheetInfo.parentNumber};
            var parentType = {parentType: $scope.costsheetInfo.parentType};
            var parentId = {parentId: $scope.costsheetInfo.parentId};

            var costs = angular.copy($scope.costsheetInfo.costs);
            costs = costs.map(function (obj) {
                return angular.extend(obj, parentNumber, parentType, parentId);
            });
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = costs;
        });

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "parentType");
            var targetId = Util.goodMapValue(rowEntity, "parentId");
            gridHelper.showObject(targetType, targetId);
        };

    }
]);