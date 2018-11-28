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
                multiSelect: false,
                noUnselect: false,
                columnDefs: $scope.config.columnDefs,
                totalItems: 0,
                data: [],
                rowHeight: 60
            };
        });


        ObjectLookupService.getAnnotationTags().then(function(annotationTags){
            $scope.annotationTags = annotationTags;
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = $scope.annotationTags;
        });

        $scope.onClickCancel = function() {
            $modalInstance.dismiss('Cancel');
        };

    }]);