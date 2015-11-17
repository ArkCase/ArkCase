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

        $scope.$on('costsheet-retrieved', function(e, data) {
            $scope.costsheetInfo = data;
            var parentNumber = {parentNumber: $scope.costsheetInfo.parentNumber};
            var parentType = {parentType: $scope.costsheetInfo.parentType};
            angular.extend($scope.costsheetInfo.costs[0], parentNumber);
            angular.extend($scope.costsheetInfo.costs[0], parentType);
            console.log($scope.costsheetInfo.costs);
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.costsheetInfo.costs;
        });

        $scope.onClickObjectType = function (event, rowEntity) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "parentType");
            var targetId = Util.goodMapValue(rowEntity, "parentNumber");
            Helper.Grid.showObject($scope, targetType, targetId);
        };

    }
]);