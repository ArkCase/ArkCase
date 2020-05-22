'use strict';

angular.module('request-info').controller('RequestInfo.ExemptionStatutesLookupModalController', ['$scope', 'params', '$modalInstance',
    function ($scope, params, $modalInstance) {

        $scope.config = params.config;
        $scope.gridOptions = params.gridOptions;
        $scope.gridOptions.data = params.gridOptions.data;

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

    }]);