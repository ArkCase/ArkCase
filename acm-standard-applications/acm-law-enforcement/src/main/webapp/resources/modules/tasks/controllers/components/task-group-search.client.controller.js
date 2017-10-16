'use strict';

angular.module('tasks').controller('Tasks.GroupSearchController', ['$scope', '$modalInstance', '$config', '$filter', '$searchValue',
    function ($scope, $modalInstance, $config, $filter, $searchValue) {
        $scope.filter = $filter;
        $scope.searchValue = $searchValue;
        $scope.findOwningGroups = 'true';
        $scope.modalInstance = $modalInstance;
        $scope.config = $config;
    }
]);
