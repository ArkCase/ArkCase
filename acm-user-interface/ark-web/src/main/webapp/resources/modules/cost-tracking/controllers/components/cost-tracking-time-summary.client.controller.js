'use strict';

angular.module('cost-tracking').controller('CostTracking.TimeSummaryController', ['$scope', 'UtilService', 'Helper.UiGridService',
    function ($scope, Util, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        $scope.$emit('req-component-config', 'time-summary');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('time-summary' == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
            }
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
            var parentNumber = {parentNumber: $scope.costsheetInfo.parentNumber};
            var parentType = {parentType: $scope.costsheetInfo.parentType};
            var parentId = {parentId: $scope.costsheetInfo.parentId};

            var costs = angular.copy($scope.costsheetInfo.costs);
            costs = costs.map(function (obj){
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