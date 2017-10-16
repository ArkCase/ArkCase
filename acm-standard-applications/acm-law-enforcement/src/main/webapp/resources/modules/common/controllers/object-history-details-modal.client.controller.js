'use strict';

angular.module('common').controller('Common.ObjectHistoryDetailsController', ['$scope', '$translate'
    , '$state', '$modal', 'ConfigService', 'params', '$modalInstance', 'Helper.UiGridService'
    , function ($scope, $translate, $state
        , $modal, ConfigService, params, $modalInstance, HelperUiGridService) {

        var gridData = params.details;

        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            $scope.config = moduleConfig;
            onConfigRetrieved(moduleConfig.objectHistoryGrid);
            return moduleConfig;
        });


        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        function onConfigRetrieved(config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            $scope.gridOptions.data = gridData;
        }

        $scope.close = function () {
            $modalInstance.dismiss('close');
        };

    }
]);
