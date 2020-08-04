'use strict';

angular.module('consultations').controller('Consultations.UserSearchController', [ '$scope', '$modalInstance', '$config', '$filter', '$extraFilter', function($scope, $modalInstance, $config, $filter, $extraFilter) {
    $scope.filter = $filter;
    $scope.extraFilter = $extraFilter;
    $scope.modalInstance = $modalInstance;
    $scope.config = $config;
    $scope.secondGrid = 'true';
} ]);
