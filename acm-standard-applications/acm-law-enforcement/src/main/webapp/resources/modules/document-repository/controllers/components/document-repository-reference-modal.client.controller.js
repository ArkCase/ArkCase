'use strict';

angular.module('document-repository').controller('DocumentRepository.ReferenceModalController', ['$scope'
    , '$modalInstance', '$config', '$filter',
    function ($scope, $modalInstance, $config, $filter) {
        $scope.filter = $filter;
        $scope.modalInstance = $modalInstance;
        $scope.config = $config;
    }
]);
