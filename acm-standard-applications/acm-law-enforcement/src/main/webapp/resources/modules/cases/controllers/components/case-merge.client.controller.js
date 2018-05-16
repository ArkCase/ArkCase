'use strict';

angular.module('cases').controller('Cases.MergeController', [ '$scope', '$modalInstance', 'config', 'filter', function($scope, $modalInstance, config, filter) {

    $scope.modalInstance = $modalInstance;
    $scope.config = config;
    $scope.filter = filter;
} ]);