'use strict';

angular.module('tasks').controller('Tasks.ReferenceModalController', ['$scope', '$modalInstance', '$config', '$filter',
    function ($scope, $modalInstance, $config, $filter) {
        $scope.filter = $filter;
        $scope.modalInstance = $modalInstance;
        $scope.config = $config;
    }
]);
