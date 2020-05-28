'use strict';

angular.module('request-info').controller('RequestInfo.ExemptionStatuteModalController', ['$scope', '$modal', '$modalInstance', 'Object.LookupService', 'params', 'ConfigService', function ($scope, $modal, $modalInstance, ObjectLookupService, params, ConfigService) {

    if (params.item.exemptionStatute) {
        $scope.statute = params.item.exemptionStatute
    }

    $scope.exemptionStatutes = params.exemptionStatutes;

    ConfigService.getModuleConfig('request-info').then(function (moduleConfig) {
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
        $scope.gridOptions = $scope.gridOptions || {};
        $scope.gridOptions.data = $scope.exemptionStatutes;

    });


    $scope.showExemptionStatutes = function () {
        $modal.open({
            size: 'lg',
            templateUrl: 'modules/cases/views/components/case-exemption-codes-description-modal.client.view.html',
            controller: 'RequestInfo.ExemptionCodesDescriptionModalController',
            backdrop: 'static'
        })
    };

    $scope.save = function() {
        $modalInstance.close({
            exemptionStatute: $scope.statute
        });

    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);
