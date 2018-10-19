'use strict';

angular.module('request-info').controller('RequestInfo.ReferenceModalController', [ '$scope', '$modalInstance', '$config', '$filter', function($scope, $modalInstance, $config, $filter) {
    $scope.filter = $filter;
    $scope.modalInstance = $modalInstance;
    $scope.config = $config;

    $scope.onClickOk = function() {
        $modalInstance.close($scope.returnReason);
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);
