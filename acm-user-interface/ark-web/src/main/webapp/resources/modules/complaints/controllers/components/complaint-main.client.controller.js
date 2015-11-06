'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', 'ConfigService',
    function ($scope, ConfigService) {
        var z = 1;
        return;
        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'main');
        $scope.components = null;
        $scope.config = null;

        function applyConfig(e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        }

        ConfigService.getModule({moduleId: 'complaints'}, function (moduleConfig) {
            $scope.components = moduleConfig.components;
        });
    }
]);