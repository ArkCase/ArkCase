'use strict';

angular.module('consultations').controller('Consultations.ReferenceModalController', [ '$scope', '$modalInstance', '$config', '$filter', function($scope, $modalInstance, $config, $filter) {
    $scope.filter = $filter;
    $scope.modalInstance = $modalInstance;
    $scope.config = $config;
} ]);
