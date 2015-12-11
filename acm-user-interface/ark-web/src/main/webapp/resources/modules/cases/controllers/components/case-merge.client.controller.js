'use strict';

angular.module('cases').controller('Cases.MergeController', ['$scope', '$modalInstance', '$clientInfoScope','$filter',
    function ($scope, $modalInstance, $clientInfoScope, $filter) {
        $scope.filter = $filter;

        $clientInfoScope.$on('component-config', applyConfig);
        $clientInfoScope.$emit('req-component-config', 'merge');

        $scope.config = null;
        $scope.modalInstance = $modalInstance;
        function applyConfig(e, componentId, config) {
            if (componentId == 'cases.comp.merge') {
                $scope.config = config;
            }
        }
    }
]);