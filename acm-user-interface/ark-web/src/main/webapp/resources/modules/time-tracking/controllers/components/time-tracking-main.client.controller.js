'use strict';

angular.module('time-tracking').controller('TimeTracking.MainController', ['$scope', 'ConfigService',
    function($scope, ConfigService) {

        $scope.$emit('main-component-started');

        ConfigService.getComponentConfig("time-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        ConfigService.getModuleConfig("time-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });
    }
]);