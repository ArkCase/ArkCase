'use strict';

angular.module('common').controller('Common.UserGroupPickerController', [ '$scope', '$modalInstance', '$config', '$filter', '$extraFilter', '$params', function($scope, $modalInstance, $config, $filter, $extraFilter, $params) {
    $scope.filter = $filter;
    $scope.extraFilter = $extraFilter;
    $scope.modalInstance = $modalInstance;
    $scope.config = $config;
    $scope.secondGrid = 'true';
    $scope.secondSelectionOptional = 'true';
    $scope.params = $params;
} ]);
