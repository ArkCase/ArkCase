'use strict';

angular.module('cases').controller('Cases.ExemptionCodesDescriptionModalController', ['$scope', '$modalInstance', 'ConfigService', 'Object.LookupService',
    function($scope, $modalInstance, ConfigService, ObjectLookupService){

        ConfigService.getModuleConfig('cases').then(function(moduleConfig){
            var codesDescriptionConfig = _.find(moduleConfig.components, {
                id: 'exemptionCodesDescription'
            });


            $scope.config = codesDescriptionConfig;

            $scope.gridOptions = {
                enableColumnResizing: true,
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                paginationPageSizes: $scope.config.paginationPageSizes,
                paginationPageSize: $scope.config.paginationPageSize,
                multiSelect: false,
                noUnselect: false,
                totalItems: 0,
                data: []
            };

            $scope.gridOptions.columnDefs = $scope.config.columnDefs;
            reloadGrid();
        });


        function reloadGrid() {
            ObjectLookupService.getExemptionStatutes().then(function (exemptionStatute) {
                $scope.exemptionStatutes = exemptionStatute;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.exemptionStatutes;
            });
        }

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

    }]);