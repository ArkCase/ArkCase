'use strict';

angular.module('cost-tracking').controller('CostTracking.TimeSummaryController', ['$scope', 'UtilService', 'HelperService',
    function ($scope, Util, Helper) {
        $scope.$emit('req-component-config', 'time-summary');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('time-summary' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
            var parentNumber = {parentNumber: $scope.costsheetInfo.parentNumber};
            var parentType = {parentType: $scope.costsheetInfo.parentType};
            var parentId = {parentId: $scope.costsheetInfo.parentId};
            $scope.costsheetInfo.costs = $scope.costsheetInfo.costs.map(function (obj){
               return angular.extend(obj, parentNumber, parentType, parentId);
            });
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.costsheetInfo.costs;
        });

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "parentType");
            var targetId = Util.goodMapValue(rowEntity, "parentId");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);