'use strict';

angular.module('tasks').controller('Tasks.ObjectSearchController', ['$scope', '$modalInstance', '$config'
    , '$filter', 'ConfigService', 'UtilService'
    , function ($scope, $modalInstance, $config, $filter, ConfigService, Util) {
        $scope.filter = $filter;
        $scope.modalInstance = $modalInstance;
        $scope.config = $config;
        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            var customization = Util.goodMapValue(moduleConfig, "customization", {});
            if (customization) {
                $scope.customization = customization;
            }
        });
    }
]);
