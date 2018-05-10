'use strict';

angular.module('admin').controller('Admin.UsersPicker', [ '$scope', '$modalInstance', '$config', '$filter', function($scope, $modalInstance, $config, $filter) {
    $scope.filter = $filter;
    $scope.modalInstance = $modalInstance;
    $scope.config = $config;
} ]);