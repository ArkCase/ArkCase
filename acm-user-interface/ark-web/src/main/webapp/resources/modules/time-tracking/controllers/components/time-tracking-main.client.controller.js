'use strict';

angular.module('time-tracking').controller('TimeTracking.MainController', ['$scope', 'ConfigService',
    function($scope, ConfigService) {
        $scope.$emit('req-component-config', 'main');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        });

        ConfigService.getModuleConfig("time-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });
    }
]);