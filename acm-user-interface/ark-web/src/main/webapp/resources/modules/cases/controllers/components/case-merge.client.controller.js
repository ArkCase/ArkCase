'use strict';

angular.module('cases').controller('Cases.MergeController', ['$scope', '$modalInstance', '$clientInfoScope','$filter', 'ConfigService',
    function ($scope, $modalInstance, $clientInfoScope, $filter, ConfigService) {
        $scope.filter = $filter;

//        $clientInfoScope.$on('component-config', applyConfig);
//        $clientInfoScope.$emit('req-component-config', 'merge');
//
//        $scope.config = null;
//        function applyConfig(e, componentId, config) {
//            if (componentId == 'merge') {
//                $scope.config = config;
//            }
//        }
        
        
        $scope.modalInstance = $modalInstance;
        ConfigService.getComponentConfig("cases", "merge").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });
    }
]);