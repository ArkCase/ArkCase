'use strict';

angular.module('cost-tracking').controller('CostTracking.PersonController', ['$scope', 'HelperService',
    function ($scope, Helper) {
        $scope.$emit('req-component-config', 'person');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('person' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
            }
        });

        $scope.$on('costsheet-retrieved', function(e, data) {
            $scope.costsheetInfo = data;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.costsheetInfo.user];
        });



    }
]);