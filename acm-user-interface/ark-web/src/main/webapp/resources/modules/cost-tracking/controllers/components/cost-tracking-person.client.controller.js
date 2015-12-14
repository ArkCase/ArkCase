'use strict';

angular.module('cost-tracking').controller('CostTracking.PersonController', ['$scope'
    , 'ConfigService', 'Helper.UiGridService'
    , function ($scope, ConfigService, HelperUiGridService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        ConfigService.getComponentConfig("cost-tracking", "person").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            return config;
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = [$scope.costsheetInfo.user];
        });



    }
]);