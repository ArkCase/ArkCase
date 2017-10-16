'use strict';

angular.module('complaints').controller('Complaints.UserSearchController', ['$scope', '$modalInstance', '$config', '$filter', '$extraFilter',
    function ($scope, $modalInstance, $config, $filter, $extraFilter) {
        $scope.filter = $filter;
        $scope.extraFilter = $extraFilter;
        $scope.modalInstance = $modalInstance;
        $scope.config = $config;
    }
]);
