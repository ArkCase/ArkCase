'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', 'ConfigService',
    function($scope, ConfigService) {

        $scope.$emit('main-component-started');

        ConfigService.getComponentConfig("cost-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        ConfigService.getModuleConfig("cost-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });
    }
]);