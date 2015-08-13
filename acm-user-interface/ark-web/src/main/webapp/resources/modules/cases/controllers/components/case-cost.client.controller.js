'use strict';

angular.module('cases').controller('CaseCostController', ['$scope',
    function($scope) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'cost');
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'cost') {
                $scope.config = config;
            }
        }
    }
]);