'use strict';

angular.module('request-info').controller('RequestInfo.UserSearchController', [ '$scope', '$modalInstance', '$config', '$filter', '$extraFilter', function($scope, $modalInstance, $config, $filter, $extraFilter) {
    $scope.filter = $filter;
    $scope.extraFilter = $extraFilter;
    $scope.modalInstance = $modalInstance;
    $scope.config = $config;
} ]);
