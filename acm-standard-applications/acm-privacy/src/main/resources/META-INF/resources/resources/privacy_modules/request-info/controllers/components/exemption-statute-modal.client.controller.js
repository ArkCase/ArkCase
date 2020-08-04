'use strict';

angular.module('request-info').controller('RequestInfo.ExemptionStatuteModalController', ['$scope', '$modal', '$modalInstance', 'Object.LookupService', 'params', 'ConfigService', function ($scope, $modal, $modalInstance, ObjectLookupService, params, ConfigService) {

    if (params.item.exemptionStatute) {
        $scope.statute = params.item.exemptionStatute
    }

    $scope.exemptionStatutes = params.exemptionStatutes;

    ConfigService.getModuleConfig('request-info').then(function (moduleConfig) {
        var codesDescriptionConfig = _.find(moduleConfig.components, {
            id: 'exemptionStatutesLookup'
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
        var params = {};
        params.config = $scope.config;
        params.gridOptions = $scope.gridOptions;
        $scope.gridOptions.data = $scope.exemptionStatutes;
        $modal.open({
            size: 'lg',
            templateUrl: 'modules/request-info/views/components/exemption-statutes-lookup-modal.client.view.html',
            controller: 'RequestInfo.ExemptionStatutesLookupModalController',
            backdrop: 'static',
            resolve: {
                params: function () {
                    return params;
                }
            }
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
