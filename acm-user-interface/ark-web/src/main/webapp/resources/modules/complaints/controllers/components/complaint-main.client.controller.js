'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', 'ConfigService',
    function ($scope, ConfigService) {
        $scope.$emit('req-component-config', 'main');
        $scope.$on('component-config', function applyConfig(e, componentId, config) {
            if (componentId == 'main') {
                $scope.config = config;
            }
        });


        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });
        //ConfigService.getModule({moduleId: 'complaints'}, function (moduleConfig) {
        //    $scope.components = moduleConfig.components;
        //});
    }
]);