'use strict';

angular.module('cases').controller('Cases.ExemptionCodesDescriptionModalController', ['$scope', '$modalInstance', 'ConfigService', 'Object.LookupService',
    function($scope, $modalInstance, ConfigService, ObjectLookupService){
    
        $scope.gridOptions = {
            enableColumnResizing: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            noUnselect: false,
            totalItems: 0,
            data: []
        };

        ConfigService.getModuleConfig('cases').then(function (moduleConfig) {
            var codesDescriptionConfig = _.find(moduleConfig.components, {
                id: 'exemptionCodesDescription'
            });

            $scope.config = codesDescriptionConfig;
            $scope.gridOptions.columnDefs = $scope.config.columnDefs;
            $scope.gridOptions.paginationPageSizes = $scope.config.paginationPageSizes;
            $scope.gridOptions.paginationPageSize = $scope.config.paginationPageSize;
            reloadGrid();
        });


        function reloadGrid() {
            ObjectLookupService.getAnnotationTags().then(function (annotationTags) {
                $scope.annotationTags = annotationTags;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = $scope.annotationTags;
            });
        }

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

    }]);