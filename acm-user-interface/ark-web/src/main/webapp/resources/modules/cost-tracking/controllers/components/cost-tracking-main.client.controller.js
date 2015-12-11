'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', 'ConfigService',
    function($scope, ConfigService) {
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'main');
        $scope.components = null;
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        }

        ConfigService.getModule({moduleId: 'cost-tracking'}, function(moduleConfig){
            $scope.components = moduleConfig.components;
        });
    }
]);