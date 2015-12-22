'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', '$stateParams', 'UtilService', 'ConfigService'
    , function ($scope, $stateParams, Util, ConfigService) {

        $scope.$emit('main-component-started');

        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            return moduleConfig;
        });

    }
]);