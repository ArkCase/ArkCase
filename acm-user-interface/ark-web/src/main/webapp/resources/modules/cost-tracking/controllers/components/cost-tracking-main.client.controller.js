'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', 'ConfigService',
    function($scope, ConfigService) {
        $scope.$emit('req-component-config', 'main');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        });

        ConfigService.getModuleConfig("cost-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });
    }
]);