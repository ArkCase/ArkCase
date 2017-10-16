'use strict';

angular.module('tasks').controller('Tasks.UserSearchController', ['$scope', '$modalInstance', '$config', '$filter', '$extraFilter', 
    function ($scope, $modalInstance, $config, $filter, $extraFilter) {
        $scope.filter = $filter;
        $scope.extraFilter = $extraFilter;
        $scope.modalInstance = $modalInstance;
        $scope.config = $config;
    }
]);
