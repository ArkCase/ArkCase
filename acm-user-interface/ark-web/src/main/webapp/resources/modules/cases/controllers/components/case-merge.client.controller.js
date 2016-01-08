'use strict';

angular.module('cases').controller('Cases.MergeController', ['$scope', '$modalInstance', '$clientInfoScope', '$filter',
    function ($scope, $modalInstance, $clientInfoScope, $filter) {

        $scope.modalInstance = $modalInstance;
        $scope.config = $clientInfoScope;
        $scope.filter = $filter;
    }
]);